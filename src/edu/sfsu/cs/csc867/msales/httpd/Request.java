package edu.sfsu.cs.csc867.msales.httpd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.sfsu.cs.csc867.msales.httpd.config.HttpdConf;
import edu.sfsu.cs.csc867.msales.httpd.validation.HttpRequestFirstLineInterpreter;
import edu.sfsu.cs.csc867.msales.httpd.validation.HttpRequestHeaderFieldsInterpreter;

/**
 * This is the request the implementation of the HTTP request method. 
 * @author Marcello de Sales
 * @version 0.1 Feb 9, 2008 7:35:50 PM
 */
public class Request {

    /**
     * This is the first line from the HTTP request
     */
    private String requestFirstLine;
    /**
     * This map represents the request header variables.
     */
    private Map<String, String> requestHeaderFields;
    /**
     * This is the request method type.
     */
    private String methodType;
    /**
     * This is the URI from the request.
     */
    private URI URI;
    /**
     * This is the query string used on the URI
     */
    private String query;
    /**
     * This is the HTTP version that is used on the first line of the request method.
     */
    private String version;

    private InetAddress inetAddress;

    public String getRequestFirstLine() {
        return requestFirstLine;
    }
    
    public Map<String, String> getRequestHeaderFields() {
        return requestHeaderFields;
    }
    
    public String getMethodType() {
        return methodType;
    }

    public URI getURI() {
        return URI;
    }

    public String getQuery() {
        return query;
    }

    public String getVersion() {
        return version;
    }
    
    public String getBody() {
        String body = "";
        for(String headerKey : this.requestHeaderFields.keySet()) {
            body =  body + headerKey + ": " + this.requestHeaderFields.get(headerKey) 
                                + System.getProperty("line.separator"); 
        }
        return body;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    /**
     * Default constructor used to reset your variables and data structures for each new incoming
     * request.
     * 
     * @throws IOException
     * @throws
     */
    private Request(InputStream inputStream) throws HttpErrorException {
        try {
            this.parse(new BufferedReader(new InputStreamReader(inputStream)));
        } catch (Exception e) {
            throw HttpErrorException.buildNewException(e);
        }
    }

    /**
     * Parse the incoming request depending on the type of request you are receiving. This
     * information is found from the first line of the incoming request. You will also want to check
     * and make sure the request you are receiving is a valid request. If the request is not valid,
     * throw an error using the http error codes.
     * 
     * @param inMsg BufferedReader which grabs the incoming message from the client socket
     * @throws IOException thrown from reading the buffered reader
     */
    private void parse(BufferedReader inMsg) throws HttpErrorException {

        try {

            Map<String, String> headerVars = new LinkedHashMap<String, String>();
            String requestLine;
            while ((requestLine = inMsg.readLine()) != null) {
             
                if (requestLine.equals("") || requestLine.equals(" ")) {
                    break;
                }
                
                System.out.println("Request Line: " + requestLine);
                
                if (this.requestFirstLine == null) {
                    HttpRequestFirstLineInterpreter.createNewInterpreter(requestLine).interpret();
                    this.requestFirstLine = requestLine;
                    this.parseFirstLine(requestLine);
                    this.requestHeaderFields = new LinkedHashMap<String, String>();
                    continue;
                } else {
                        String[] headerVarValue = requestLine.split(": ");
                        headerVars.put(headerVarValue[0].trim(), headerVarValue[1].trim());
                }
                
            }

            HttpRequestHeaderFieldsInterpreter.createNewInterpreter(headerVars.keySet()).interpret();
            this.requestHeaderFields = headerVars;
            
        } catch (Exception e) {
            throw HttpErrorException.buildNewException(e);
        }
    }

    /**
     * Used to first check whether a requested file path has an alias set within the configuration
     * file and if so, replaces the alias of the file or directory with the real path. This way, the
     * server can find the right file in the tree. HINT: Remember that any one path can have
     * multiple aliases found within the httpd.of file. For example, the URI
     * http://www.blah.net/blah/help/hello.html could have an alias where blah is equivalent to
     * http://www.blah.net/blah_blah_blah and help could be an alias for
     * http://www.blah.net/blah_blah_blah/bleh/help. Another thing to note is that your URI could
     * also include script aliases which means you may be required to run a cgi script.
     * 
     * @param config HttpdConf Object which contains all the information on how the server is
     *            configured.
     */
    public void setHttpdConf(HttpdConf config) {

    }

    /**
     * Print function used for debugging purposes. Helpful to make sure you are parsing the incoming
     * request properly.
     */
    public void print() {
        // System.out.println("Client connected from " + .getInetAddress());
        System.out.println("The method was " + methodType);
        System.out.println("The Request URL was " + URI);
        System.out.println("The query string was " + query);
        System.out.println("The HTTP version is " + version);
        System.out.println("The following headers were included:");
        // for (int i = 0; i < tags.size(); i++) {
        // System.out.println(tags.get(i) + ": " + header.get(tags.get(i)));
        // }
        System.out.println("The message body was: \n" + this.getBody());
    }

    /**
     * private function used by request object to parse the information passed through from the
     * client to the server and save it for future use. The type of request can be found on this
     * first line of the request.
     * 
     * @param first String representation of the first line of the request from the client. Passed
     *            in as one long string which can easily be parsed.
     */
    private void parseFirstLine(String firstLine) {

        String[] tokens = firstLine.split(" ");
        
        this.methodType = tokens[0];
        try {
            this.URI = new URI(tokens[1]);
        } catch (URISyntaxException e) {
        }
        this.version = tokens[2];
        if (tokens[1].contains("?")) {
            this.query = tokens[1].split("\\?")[1];
        }
    }

    /**
     * private function used by the request object to parse the rest of the request message (e.g.
     * other headers and the body of the message) from the client so it can be used later when
     * actual processing of the request happens.
     * 
     * @param inFile BufferedReader object that comes through the socket. Needs to be processed
     *            properly before the data stored within it can be used.
     * @throws HttpErrorException in case anything wrong happens on the protocol level.
     */
    public static Request buildRequest(Socket clientConnection) throws HttpErrorException {
        Request request;
        try {
            request = new Request(clientConnection.getInputStream());
            request.setInetAddress(clientConnection.getInetAddress());
            System.out.println("Connection originated from " + clientConnection.getInetAddress());
        
        } catch (IOException e) {
            throw HttpErrorException.buildNewException(e);
        }
        return request;
    }
}