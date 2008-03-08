package edu.sfsu.cs.csc867.msales.voctopus.response;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import edu.sfsu.cs.csc867.msales.voctopus.RequestResponseMediator.ReasonPhrase;
import edu.sfsu.cs.csc867.msales.voctopus.config.VOctopusConfigurationManager;
import edu.sfsu.cs.csc867.msales.voctopus.config.VOctopusConfigurationManager.LogFormats;
import edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest;
import edu.sfsu.cs.csc867.msales.voctopus.request.HttpScriptRequest;
import edu.sfsu.cs.csc867.msales.voctopus.request.handler.DirectoryContentRequestHandlerStrategy;
import edu.sfsu.cs.csc867.msales.voctopus.request.handler.HttpRequestHandler;
import edu.sfsu.cs.csc867.msales.voctopus.request.handler.ScriptRequestHandlerStrategy;
import edu.sfsu.cs.csc867.msales.voctopus.request.handler.WebServiceRequestHandlerStrategy;

/**
 * It has to conform to the following grammar Response = Status-Line ; Section 6.1 (( general-header ; Section 4.5 |
 * response-header ; Section 6.2 | entity-header ) CRLF) ; Section 7.1 CRLF [ message-body ] ; Section 7.2
 * 
 * @author marcello Feb 18, 2008 7:21:12 PM
 */
public abstract class AbstractHttpResponse implements HttpResponse {

    private HttpRequest request;

    private String[] responseBody;

    private List<String> responseHeader;

    public AbstractHttpResponse(HttpRequest originatingRequest) {
        this.request = originatingRequest;
        this.responseHeader = new ArrayList<String>();
        ReasonPhrase status = this.request.getStatus();
        if (!status.equals(ReasonPhrase.STATUS_404) && this.request instanceof HttpScriptRequest) {
            String[] responseBody = this.getResponseBody();
            if (!status.equals(ReasonPhrase.STATUS_500)) {
                String contentType = responseBody[0];
                if (!contentType.contains("Content-Type: ")) {
                    contentType = "Content-Type: text/plain";
                } else {
                    // this time we remove the header from the response.
                    // also remove the blank line
                    this.responseBody = new String[responseBody.length - 2];
                    if (contentType.contains("text/")) {
                        for (int i = 2; i < responseBody.length; i++) {
                            this.responseBody[i - 2] = responseBody[i];
                        }
                    }
                }
                this.setDefaultHeaderValues();
                this.responseHeader.add(contentType);
            } else {
                //500 content type
                // if it were 500 or 404, the body will be automatically be handled before by the handler.
                this.setDefaultHeaderValues();
                this.responseHeader.add("Content-Type: text/html");
            }
            
        } else { //anything else
            this.setDefaultHeaderValues();
            if (requestMustIncludeBody(status)) {
                this.responseHeader.add("Content-Type: " + this.getRequest().getContentType());
            }
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
     * 
     * @param headerLine is the new response variable and value
     */
    public void addResponseHeaderVar(String headerLine) {
        this.responseHeader.add(headerLine);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.sfsu.cs.csc867.msales.voctopus.response.HttpResponse#getResponseBody()
     */
    public String[] getResponseBody() {
        if (this.responseBody == null) {
            responseBody = this.request.getResourceLines();
        }
        return this.responseBody;
    }

    /*
     * (non-Javadoc)
     * 
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

    /*
     * (non-Javadoc)
     * 
     * @see edu.sfsu.cs.csc867.msales.voctopus.response.HttpResponse#sendResponse(java.io.OutputStream)
     */
    public void sendResponse(OutputStream clientOutput) throws IOException {

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientOutput));
        PrintWriter writer = new PrintWriter(out, true);

        this.sendDefaultHeaders(writer);
        this.sendHeader(writer);

        if (requestMustIncludeBody(this.request.getStatus())) {
            writer.print("\r\n");
            writer.flush();

            this.sendBody(clientOutput, writer);
        }

        out.flush();
        writer.close();
            // if the connection sends the acknowledgment to close it, then close it
            // usually with a file
        if (!this.request.keepAlive()) {
            out.close();
        }
    }

    /**
     * @param status is the status from the request.
     * @return if the result must include a body.
     */
    private boolean requestMustIncludeBody(ReasonPhrase status) {
        return !(status.equals(ReasonPhrase.STATUS_204) || (status.equals(ReasonPhrase.STATUS_304))); 
    }

    /**
     * Sends the default header values that are general to all the
     * 
     * @param writer is the default writer from the output.
     */
    private void sendDefaultHeaders(PrintWriter writer) {
        for (String headerLine : this.responseHeader) {
            writer.println(headerLine);
        }
    }

    /**
     * Sets the default values for the header list
     */
    private void setDefaultHeaderValues() {
        StringBuilder header = new StringBuilder();
        header.append(this.request.getRequestVersion());
        header.append(" ");
        ReasonPhrase status = this.request.getStatus();
        header.append(status);
        this.responseHeader.add(header.toString());
        
        if (!status.equals(ReasonPhrase.STATUS_304)) {
            header.delete(0, header.length());
            header.append("Date: ");
            Calendar cal = GregorianCalendar.getInstance();
            Date today = cal.getTime();
            header.append(LogFormats.HEADER_DATE_TIME.format(today));
            this.responseHeader.add(header.toString());
            header.delete(0, header.length());

            header.append("Last-Modified: ");
            header.append(LogFormats.HEADER_DATE_TIME.format(this.getLastModified(status)));
            this.responseHeader.add(header.toString());
            header.delete(0, header.length());

            if (!(this.request.getRequestHandler() instanceof DirectoryContentRequestHandlerStrategy 
                     || this.request.getRequestHandler() instanceof ScriptRequestHandlerStrategy)) {
                cal.add(Calendar.YEAR, 1);
                Date expires = cal.getTime();
                header.append("Expires: ");
                header.append(LogFormats.HEADER_DATE_TIME.format(expires));
                this.responseHeader.add(header.toString());
                header.delete(0, header.length());
            }
            
            header.append("Server: ");
            header.append(VOctopusConfigurationManager.getInstance().getServerVersion());
            this.responseHeader.add(header.toString());
            header.delete(0, header.length());

            header.append("Content-Length: ");
            header.append(this.getRequestSize());
            this.responseHeader.add(header.toString());
            header.delete(0, header.length());
        }
    }

    /**
     * @return the size of the request
     */
    public long getRequestSize() {
        HttpRequestHandler handler = this.request.getRequestHandler();
        if ((handler instanceof ScriptRequestHandlerStrategy && !handler.isRequestedResourceBinary())
                || handler instanceof DirectoryContentRequestHandlerStrategy 
                || handler instanceof WebServiceRequestHandlerStrategy) {
            // TODO: Decide if this was a request to a directory, Script or something else and create more holders
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
     * @param status 
     * @return The modified status for the response.
     */
    public Date getLastModified(ReasonPhrase status) {
        if (this.request.getRequestHandler() instanceof ScriptRequestHandlerStrategy
                || this.request.getRequestHandler() instanceof WebServiceRequestHandlerStrategy) {
            return new Date();
        } else {
            return new Date(this.request.getRequestedResource().lastModified());
        }
    }

}