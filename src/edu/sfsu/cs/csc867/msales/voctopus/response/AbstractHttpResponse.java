package edu.sfsu.cs.csc867.msales.voctopus.response;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.sfsu.cs.csc867.msales.voctopus.VOctopusConfigurationManager;
import edu.sfsu.cs.csc867.msales.voctopus.RequestResponseMediator.ReasonPhrase;
import edu.sfsu.cs.csc867.msales.voctopus.VOctopusConfigurationManager.LogFormats;
import edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest;
import edu.sfsu.cs.csc867.msales.voctopus.request.HttpScriptRequest;

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
    
    private HttpRequest request;
    
    private String[] responseBody;
    
    private List<String> responseHeader;
    
    public AbstractHttpResponse(HttpRequest originatingRequest) {
        this.request = originatingRequest;
        this.responseHeader = new ArrayList<String>();
        this.setDefaultHeaderValues();
        if (this.request instanceof HttpScriptRequest) {
            String[] responseBody = this.getResponseBody();
            String contentType = responseBody[0];
            if (!contentType.contains("Content-Type: ")) {
                contentType = "Content-Type: text/plain";
            } else {
                //this time we remove the header from the response.
                //also remove the blank line
                this.responseBody = new String[responseBody.length - 2];
                if (contentType.contains("text/")) {
                    for (int i = 2; i < responseBody.length; i++) {
                        this.responseBody[i-2] = responseBody[i];
                    }
                }
            }
            this.responseHeader.add(contentType);
        }
    }
    
    /**
     * @return the request associated with this response.
     */
    public HttpRequest getRequest() {
        return this.request;
    }
    
    /**
     * Adds a new header line to the response header
     * @param headerLine is the new response variable and value
     */
    public void addResponseHeaderVar(String headerLine) {
        this.responseHeader.add(headerLine);
    }
    
    /* (non-Javadoc)
     * @see edu.sfsu.cs.csc867.msales.voctopus.response.HttpResponse#getResponseBody()
     */
    public String[] getResponseBody() {
        if (this.responseBody == null) {
            responseBody = this.request.getResourceLines();
        }
        return this.responseBody;
    }
    
    /* (non-Javadoc)
     * @see edu.sfsu.cs.csc867.msales.voctopus.response.HttpResponse#getStatusCode()
     */
    public ReasonPhrase getStatusCode() {
        return this.request.getStatus();
    }
    
    /** 
     * Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
     */
    public String[] getResponseHeader() {
        return this.responseHeader.toArray(new String[this.responseHeader.size()]);
    }
    
    /* (non-Javadoc)
     * @see edu.sfsu.cs.csc867.msales.voctopus.response.HttpResponse#sendResponse(java.io.OutputStream)
     */
    public void sendResponse(OutputStream clientOutput) throws IOException {

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientOutput));
        PrintWriter writer = new PrintWriter(out, true);

        this.sendDefaultHeaders(writer);
        this.sendHeader(writer);
        
        writer.print("\r\n");
        writer.flush();
        
        this.sendBody(clientOutput, writer);
        
            //The buffer needs to be closed.
        if (out != null) {
          //if the connection sends the acknowledgment to close it, then close it
          //usually with a file
            out.flush();
            if (!this.request.keepAlive()) {
                out.close();
            }
        }
    }
    
    /**
     * Sends the default header values that are general to all the 
     * @param writer is the default writer from the output.
     */
    private void sendDefaultHeaders(PrintWriter writer) {
        for (String headerLine : this.responseHeader) {
            writer.println(headerLine);
        }
    }

    /**
     *Sets the default values for the header list 
     */
    private void setDefaultHeaderValues() {
        StringBuilder header = new StringBuilder();
        header.append(this.request.getRequestVersion());
        header.append(" ");
        header.append(this.request.getStatus());
        this.responseHeader.add(header.toString());
        header.delete(0, header.length());
        
        header.append("Date: ");
        header.append(LogFormats.HEADER_RESPONSE.format(new Date()));
        this.responseHeader.add(header.toString());
        header.delete(0, header.length());
        
        header.append("Last-Modified: ");
        header.append(LogFormats.HEADER_RESPONSE.format(this.getLastModified()));
        this.responseHeader.add(header.toString());
        header.delete(0, header.length());

        //header.append("Expires: ");
        //header.append(new SimpleDateFormat(RESPONSE_DATE_FORMAT).format(new Date(2038,1,1)));
        //this.responseHeader.add(header.toString());
        //header.delete(0, header.length());

        header.append("Server: ");
        header.append(VOctopusConfigurationManager.getInstance().getServerVersion());
        this.responseHeader.add(header.toString());
        header.delete(0, header.length());

        
        header.append("Content-Length: ");
        header.append(this.getRequestSize());
        this.responseHeader.add(header.toString());
        header.delete(0, header.length());
    }
    
    /**
     * @return the size of the request
     */
    public long getRequestSize() {
        if (this.request.getStatus().equals(ReasonPhrase.STATUS_200) && !this.request.getRequestedResource().isFile()) {
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
    
    /**
     * @return The modified status for the response.
     */
    public Date getLastModified() {
        if (this.request.getStatus().equals(ReasonPhrase.STATUS_200) 
                && !this.request.getRequestedResource().isFile()) {
            return new Date();
        } else {
            return new Date(this.request.getRequestedResource().lastModified());
        }
    }

}