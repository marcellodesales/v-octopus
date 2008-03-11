package edu.sfsu.cs.csc867.msales.httpd;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

/**
 * Description: Used to process incoming requests to the server and generate the appropriate
 * response to that request. This is where most of the server processing is done, thus making it a
 * very important class to the implementation of your server.
 * </p>
 * 
 * @author Marcello de Sales
 * @version 1.0
 */
public class Response {

    private static final String RESPONSE_DATE_FORMAT = "EEE,.MMM.d yyyy HH:mm:ss z";

    private static final String SERVER_NAME_VERSION = "VOctopus/0.1";
    
    private Request request;

    /**
     * Default constructor for the response object. Variables are reset and/or initialized here.
     * These variables will be used throughout request processing and response generation.
     */
    private Response(Request originatingRequest) {
        this.request = originatingRequest;
    }

    /**
     * Builds a new Response object based on an instance of a request object
     * 
     * @param originatingRequest is the originating request.
     * @param clientSocket 
     * @return a new instance of the Response based on the request.
     * @throws HttpErrorException if no request instance is provided
     */
    public static Response buildNewResponse(Request originatingRequest) throws HttpErrorException {
        if (originatingRequest == null) {
            throw HttpErrorException.buildNewException(new InvalidParameterException(
                    "The request object MUST be defined"));
        }
        return new Response(originatingRequest);
    }

    /**
     * Used to process the request that came from the client to the server. There are many things
     * that need to be checked and verified during the processing of a request. You will need to
     * check for authentication, errors, cgi scripts, type of request, etc, and handle each
     * condition appropriately. HINT: it helps to use boolean flags throughout your code to check
     * for the various conditions that may or may not occur.
     * 
     * @param clientOutputStream output stream from the client
     * @throws HttpErrorException if an I/O error occurs with the output stream.
     */
    public void processRequest(OutputStream clientOutputStream) throws HttpErrorException {
        
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientOutputStream));
        PrintWriter writer = new PrintWriter(out, true);
        writeResponseToClient(writer);
    
        //The buffer needs to be closed.
        if (out != null) {
            try {
                out.close();
            } catch (IOException ioe) {
                throw HttpErrorException.buildNewException(ioe);
            }
        } 
        System.out.println("Connection closed by server...");
    }

    private void writeResponseToClient(PrintWriter writer) {
        writeHeader(writer);
        writer.println("");
        writeBody(writer);
    }

    private void writeBody(PrintWriter writer) {
        
        List<String> lines;
        try {

            for(String line : this.request.getTextFileLines()) {
                writer.println(line);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void writeHeader(PrintWriter writer) {
        writer.println("HTTP/1.1 200 OK");
        writer.println("Date: " + new SimpleDateFormat(RESPONSE_DATE_FORMAT).format(new Date()));
        writer.println("Server: " + SERVER_NAME_VERSION); 
        writer.println("Content-Type: text/html");
    }

    /**
     * Used to output a correctly formatted response to the client. This function will need to
     * process any output from a cgi script as well as generate the appropriate headers and body
     * required by an HTTP response.
     * 
     * @param output OutputStream object that will be used to send the response back to the socket.
     */
    public void writeOutput(OutputStream output) {

    }

    /**
     * Used to test for authentication. If the .htaccess file shows that authentication is needed
     * for access to the file or directory then set the appropriate headers and set the appropriate
     * status codes unless the user has included their authentication. If this is the case, check to
     * make sure their authentication is valid.
     * 
     * @param req Request object which is needed to check for authentication
     */
    public void checkAuthentication(Request req) {

    }

    /**
     * Used to set the reason for each HTTP status code as designated by the protocol.
     * 
     * @param code int value which corresponds to each status code
     */
    public void setStatus(int code) {

    }

    /**
     * Private function used to return the appropriate mime type for the file that is being
     * requested
     * 
     * @param MIMETable Hashtable of mime types from your mime.types file
     * @param extension String value which designates the extension of the file being requested.
     *            This will be used to determine the mime type
     * @return String value that contains the mime type of the file
     */
    private String getMIME(String extension) {
        return null;
    }

    /**
     * Private function used to determine whether the mime type requested is a valid mime type
     * 
     * @param MIMETable Hashtable value of the available mime types as designated by the mime.types
     *            file
     * @param extension String value which consists of the extension type requested. Used to
     *            determine the correct mime type
     * @return true if mime type if valid, false otherwise
     */
    private boolean checkMIME(Hashtable MIMETable, String extension) {
        return false;
    }

    /**
     * Processing a request from the client. Here, you will check for
     * mime type validity and handle a put request if it is requested. If the request is PUT, you
     * will need to use the body of the request to modify the existing file.
     * 
     * @param MIMETable Hashtable that contains the valid mime types as determined by the mime.types
     *            file
     * @param body String value that contains the body of the request.
     */
    private void processWithExistence(String body) {

    }

    /**
     * Handle output from a cgi script. You will need to check
     * the header passed back from the cgi script to determine the status code of the response. From
     * there, add your headers, attach the body and add any other server directives that need to be
     * included.
     * 
     * @param dataOut BufferedOutputStream object that will write to the client
     */
    private void processCGI(BufferedOutputStream dataOut) {

    }

    /**
     * Used to write the appropriate information to the log file.
     * 
     * @param logPath String value which contains the location of your log file
     * @param host String value that contains the address of the client who made the request
     */
    public void writeToLog(String logPath, String host) {

    }
}