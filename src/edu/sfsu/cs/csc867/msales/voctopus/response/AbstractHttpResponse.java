package edu.sfsu.cs.csc867.msales.voctopus.response;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.sfsu.cs.csc867.msales.voctopus.VOctopusConfigurationManager;
import edu.sfsu.cs.csc867.msales.voctopus.RequestResponseMediator.ReasonPhrase;
import edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest;

/**
 * It has to conform to the following grammar
 *        Response   = Status-Line               ; Section 6.1
                       *(( general-header        ; Section 4.5
                        | response-header        ; Section 6.2
                        | entity-header ) CRLF)  ; Section 7.1
                       CRLF
                       [ message-body ]          ; Section 7.2
 * @author marcello
 * Feb 18, 2008 7:21:12 PM
 */
public abstract class AbstractHttpResponse implements HttpResponse {

    private static final String RESPONSE_DATE_FORMAT = "EEE, MMM d yyyy HH:mm:ss z";

    private static final String SERVER_NAME_VERSION = VOctopusConfigurationManager.getInstance().getServerVersion();
    
    private HttpRequest request;
    
    private String[] responseBody;
    
    public AbstractHttpResponse(HttpRequest originatingRequest) {
        this.request = originatingRequest;
    }
    
    public String[] getResponseBody() {
        if (this.responseBody == null) {
            responseBody = this.request.getResourceLines();
        }
        return this.responseBody;
    }
    
    public ReasonPhrase getStatusCode() {
        return this.request.getStatus();
    }
    
    /** 
     * Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
     */
    public String getResponseHeader() {
        StringBuilder header = new StringBuilder();
        header.append(this.request.getRequestVersion());
        header.append(" ");
        header.append(this.request.getStatus());
        return header.toString();
    }
    
    public void sendResponse(OutputStream clientOutput){

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientOutput));
        PrintWriter writer = new PrintWriter(out, true);
        writeResponseToClient(writer);
        //The buffer needs to be closed.
        if (out != null) {
            try {
              //if the connection sends the acknowledgment to close it, then close it
              //usually with a file
                if (!this.request.keepAlive()) {
                    out.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
    
    private void writeResponseToClient(PrintWriter writer) {
        writeHeader(writer);
        writer.println("");
        writeBody(writer);
    }
    
    private long getRequestSize() {
        long size = this.request.getRequestedResource().length();
        if (this.request.getStatus().equals(ReasonPhrase.STATUS_200) 
                && size == 0) {
            //TODO: Decide if this was a request to a directory, Script or something else and create more holders
            long nsize = 0;
            for (String lines : this.getResponseBody()) {
                nsize += lines.length();
            }
            return nsize;
        } else {
            return this.request.getRequestedResource().length();
        }
    }
    
    private Date getLastModified() {
        if (this.request.getStatus().equals(ReasonPhrase.STATUS_200) 
                && this.request.getRequestedResource().length() == 0) {
            return new Date();
        } else {
            return new Date(this.request.getRequestedResource().lastModified());
        }
    }
        
    private void writeHeader(PrintWriter writer) {
        writer.println(this.getResponseHeader());
        
        String[] more = new String[] {
                "Date: " + new SimpleDateFormat(RESPONSE_DATE_FORMAT).format(new Date()),
                "Server: " + SERVER_NAME_VERSION,
                "Content-Type: " + this.request.getContentType(),
                "Content-Length: " + this.getRequestSize(), 
                "Last-Modified: " + new SimpleDateFormat(RESPONSE_DATE_FORMAT).format(this.getLastModified())
        };
        for (String headerVar : more) {
            writer.println(headerVar);
        }
    }
    
    /**
     * Writes the body to the output stream of the connection
     * @param writer
     */
    private void writeBody(PrintWriter writer) {
        for(String line : this.request.getResourceLines()) {
            writer.println(line);
        }
    }
}