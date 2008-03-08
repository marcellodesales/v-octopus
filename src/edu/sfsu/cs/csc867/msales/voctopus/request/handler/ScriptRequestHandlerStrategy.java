package edu.sfsu.cs.csc867.msales.voctopus.request.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.sfsu.cs.csc867.msales.voctopus.RequestResponseMediator.ReasonPhrase;
import edu.sfsu.cs.csc867.msales.voctopus.config.VOctopusConfigurationManager;

public class ScriptRequestHandlerStrategy extends AbstractRequestHandler {
    
    /**
     * The request parameters from the request to be used on the script.
     */
    private Map<String, String> requestParameters;
    /**
     * The lines generated from the script. If the file is binary, get the stream.//TODO STILL TO IMPLEMENT SCRIPT BYNARY
     */
    private String[] scriptsLines;

    public ScriptRequestHandlerStrategy(Map<String, String> requestParameters, URI uri, File requestedFile, 
            String handlerFound, ReasonPhrase status) {
        super(uri, requestedFile, RequestType.ASCII, handlerFound);
        this.status = status;//consider that the script ok...
        this.requestParameters = requestParameters;
        try {
            executeScriptToGetResponseLines();
        } catch (IOException e) {
            // TODO decide on what to do while creating a script...
            e.printStackTrace();
        }
    }
    
    private void executeScriptToGetResponseLines() throws IOException {
        System.out.println("handling the requested resource " + this.getRequestedResource().getPath());
        
        FileChannel channel = new FileInputStream(this.getRequestedFile()).getChannel();
        MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, this.getRequestedFile().length());
        
        Charset charset = Charset.forName("ISO-8859-1"); 
        CharsetDecoder decoder = charset.newDecoder();
        List<String> lines = new ArrayList<String>();

        StringBuilder builder = new StringBuilder();

        if (this.wasA404Rquest()) {
            System.out.println("Request Not Found: handler chose " + this.getRequestedFile().getPath());
            channel = new FileInputStream(this.getRequestedFile()).getChannel();
            buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, this.getRequestedFile().length());
            CharBuffer charBuffer = decoder.decode(buffer);
            //FILE NOT FOUND EXECUTION
            for(int i = 0, n=charBuffer.length () ; i < n; i++ ) {
                
                char charValue = charBuffer.get(); 
                if (charValue != '\n') {
                    builder.append(charValue);
                } else {
                    lines.add(builder.toString().replace("$REQUESTED_RESOURCE", 
                            this.getRequestedResource().getPath()));
                    builder.delete(0, builder.capacity());
                }
            }
            this.scriptsLines = lines.toArray(new String[lines.size()]);

        } else
        if (this.getRequestedResource().getPath().contains("/cgi-bin/")) {
            
            String[] args = null;
            if (this.requestParameters != null) {
                args = new String[this.requestParameters.size()];
                int i = -1;
                for(String arg : this.requestParameters.keySet()) {
                    args[++i] = arg + "=" + this.requestParameters.get(arg); 
                }
            }
            
            try {
                this.scriptsLines = this.getCgiExecutionResponse(args);
                if (this.scriptsLines[0].contains("Content-type: ")) {
                    this.contentType = this.scriptsLines[0];
                } else {
                    this.contentType = "text/html";
                    this.requestType = RequestType.ASCII;
                }
//                this.requestType = RequestType.BINARY; //TODO: THE REQUEST MUST BE DONE...
                
                this.status = ReasonPhrase.STATUS_200;
            
            } catch (CgiExecutionException e) {
                
                this.status = ReasonPhrase.STATUS_500;
                this.contentType = "text/html";
                this.requestType = RequestType.ASCII;
                
                File error500 = VOctopusConfigurationManager.get500ErrorFile();
                
                System.out.println("Request generated a 500: handler chose " + error500.getPath());
                channel = new FileInputStream(error500).getChannel();
                
                buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, error500.length());
                CharBuffer charBuffer = decoder.decode(buffer);
                //FILE NOT FOUND EXECUTION
                for(int i = 0, n=charBuffer.length () ; i < n; i++ ) {
                    
                    char charValue = charBuffer.get(); 
                    if (charValue != '\n') {
                        builder.append(charValue);
                    } else {
                        String complete = builder.toString();
                        String changed = complete.replace("$REQUESTED_RESOURCE", this.getRequestedResource().getPath()).
                                            replace("$REASON", e.getMessage());
                        lines.add(changed);
                        builder.delete(0, builder.capacity());
                    }
                }
                this.scriptsLines = lines.toArray(new String[lines.size()]);
            }
        }
    }

    /**
     * Builds a ProcessBuilder with the based on the arguments
     * @param arguments is the O.S. arguments (program plus arguments) 
     * @return a new ProcessBuilder with the correct information with updated environment variables
     */
    private ProcessBuilder buildProcess(String ... arguments) {
        ProcessBuilder pb = new ProcessBuilder(arguments);
//        if (this.requestParameters != null) {
//            pb.environment().putAll(this.requestParameters);
//        }
        //TODO: Set the correct values of the environment based on the server. Check the correct ones...
        return pb;
    }

    /**
     * @param cgiArguments the arguments sent on the URI, that is, the query string
     * @return the list of the lines from the process response.
     * @throws IOException if any problem reading from the process occurs.
     */
    private String[] getCgiExecutionResponse(String[] cgiArguments) throws CgiExecutionException, IOException {
        
        List<String> processArgs = new ArrayList<String>();
        String path = this.getRequestedResource().getPath();
        String fileExtention = path.substring(path.lastIndexOf("."));
        
        processArgs.add(VOctopusConfigurationManager.getExecutor(fileExtention));
        processArgs.add(this.getRequestedFile().getAbsolutePath());

        if (cgiArguments != null) {
            for(String arg : cgiArguments) {
                processArgs.add(arg);
            }
        }
        Process process = this.buildProcess((String[])processArgs.toArray(new String[processArgs.size()])).start();
        
        List<String> lines = new ArrayList<String>();
        InputStreamReader reader;
        
        int scriptResult = -1;
        try {
            scriptResult = process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        this.setStatus(ReasonPhrase.STATUS_200);
        if (scriptResult == 1) {
            reader = new InputStreamReader(process.getErrorStream());
            //this.setStatus(ReasonPhrase.STATUS_501);
            //Treat is as if it was success, and get the response from the execution.
            if (Boolean.valueOf(VOctopusConfigurationManager.WebServerProperties.
                    HTTPD_CONF.getPropertyValue("FAIL_SCRIPT_ON_ERROR"))) {
                this.setStatus(ReasonPhrase.STATUS_500);
                
                BufferedReader buffer = new BufferedReader(reader);
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = buffer.readLine()) != null) {
                    builder.append(line + "\n\r");
                }
                throw new CgiExecutionException(builder.toString());
            }
        } else {
            reader = new InputStreamReader(process.getInputStream());
        }
        if (this.getStatus().equals(ReasonPhrase.STATUS_200)) {
            BufferedReader buffer = new BufferedReader(reader);
            String line;
            while ((line = buffer.readLine()) != null) {
                lines.add(line);
            }
            
            return lines.toArray(new String[lines.size()]);
        } else {
            //never returned... just to make it compile...
            return null;
        }
    }
    
    public String[] getResourceLines() throws IOException {
        return this.scriptsLines;
    }
    
    @Override
    public boolean isRequestedResourceBinary() {
        return !this.contentType.startsWith("text/");
    }

    /**
     * 
     * 
     * @return Verifies if the request status has a value of {@link ReasonPhrase#STATUS_404}
     */
    private boolean wasA404Rquest() {
        return this.status.equals(ReasonPhrase.STATUS_404);
    }
}
