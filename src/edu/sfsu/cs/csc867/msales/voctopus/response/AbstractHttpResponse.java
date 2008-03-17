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

import edu.sfsu.cs.csc867.msales.voctopus.RequestResponseMediator.ReasonPhase;
import edu.sfsu.cs.csc867.msales.voctopus.config.VOctopusConfigurationManager;
import edu.sfsu.cs.csc867.msales.voctopus.config.VOctopusConfigurationManager.LogFormats;
import edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest;
import edu.sfsu.cs.csc867.msales.voctopus.request.HttpScriptRequest;
import edu.sfsu.cs.csc867.msales.voctopus.request.AbstractHttpRequest.RequestMethodType;
import edu.sfsu.cs.csc867.msales.voctopus.request.AbstractHttpRequest.RequestVersion;
import edu.sfsu.cs.csc867.msales.voctopus.request.handler.AsciiContentRequestHandlerStrategy;
import edu.sfsu.cs.csc867.msales.voctopus.request.handler.BinaryContentRequestHandlerStrategy;
import edu.sfsu.cs.csc867.msales.voctopus.request.handler.DirectoryContentRequestHandlerStrategy;
import edu.sfsu.cs.csc867.msales.voctopus.request.handler.EmptyBodyRequestHandler;
import edu.sfsu.cs.csc867.msales.voctopus.request.handler.HttpRequestHandler;
import edu.sfsu.cs.csc867.msales.voctopus.request.handler.InvalidRequestHandler;
import edu.sfsu.cs.csc867.msales.voctopus.request.handler.ScriptRequestHandlerStrategy;
import edu.sfsu.cs.csc867.msales.voctopus.request.handler.WebServiceRequestHandlerStrategy;

/**
 * It has to conform to the following grammar Response = Status-Line ; Section 6.1 (( general-header ; Section 4.5 |
 * response-header ; Section 6.2 | entity-header ) CRLF) ; Section 7.1 CRLF [ message-body ] ; Section 7.2
 * 
 * @author marcello Feb 18, 2008 7:21:12 PM
 */
public abstract class AbstractHttpResponse implements HttpResponse {

    /**
     * The incoming request
     */
    private HttpRequest request;

    protected boolean scriptExecution;

    /**
     * The response body to be sent to the client. If the request was to a script, the content will be generated
     */
    private String[] responseBody;

    /**
     * this is the response header
     */
    private List<String> responseHeader;

    /**
     * The request size based on the calculated size of the response body
     */
    private Long requestSize;

    public AbstractHttpResponse(HttpRequest originatingRequest) {
        this.request = originatingRequest;
        this.responseHeader = new ArrayList<String>();
        if (!(this instanceof BinaryContentResponseDecorator)) {
            //this.responseBody = new String[this.request.getResourceLines().length];
            this.responseBody = this.request.getResourceLines();
        }
        
        ReasonPhase status = this.request.getStatus();
        if (!status.equals(ReasonPhase.STATUS_404) && !status.equals(ReasonPhase.STATUS_403)
                && !status.equals(ReasonPhase.STATUS_401) && this.request instanceof HttpScriptRequest) {
            if (!status.equals(ReasonPhase.STATUS_500)) {
                this.setDefaultHeaderValues();
                this.responseHeader.add("Content-type: " + this.request.getContentType());
            } else {
                // 500 content type
                // if it were 500, 404, 403, the body will be automatically be handled before by the handler.
                this.setDefaultHeaderValues();
                this.responseHeader.add("Content-type: text/html");
            }

        } else { // anything else
            this.setDefaultHeaderValues();
            if (this.requestMustIncludeBody(status)) {
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
        if (this.responseBody == null || this.responseBody[0] == null) {
            this.responseBody = this.request.getResourceLines();
        }
        return this.responseBody;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.sfsu.cs.csc867.msales.voctopus.response.HttpResponse#getStatusCode()
     */
    public ReasonPhase getStatusCode() {
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
    }

    /**
     * @param status is the status from the request.
     * @return if the result must include a body.
     */
    private boolean requestMustIncludeBody(ReasonPhase status) {
        switch (request.getStatus()) {
        case STATUS_201:
        case STATUS_202:
        case STATUS_203:
        case STATUS_204:
        case STATUS_304:
        case STATUS_400:
        case STATUS_501:
            return false;
        case STATUS_401:
            // Extreme case where the user did not give the correct username and password.
            return (this.request.getResourceLines() != null) && this.request.getResourceLines().length > 0;
        default:
            return !request.getMethodType().equals(RequestMethodType.HEAD)
                    && !request.getMethodType().equals(RequestMethodType.PUT);
        }
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
        RequestVersion version = this.request.getRequestVersion();
        ReasonPhase status = this.request.getStatus();
        RequestMethodType method = this.request.getMethodType();
        
//        if (method.equals(RequestMethodType.NOT_SUPPORTED)) {
//            status = ReasonPhase.STATUS_501;
//        }
//      
        if (!method.isImplemented() && version.equals(RequestVersion.INVALID)) {
            version = RequestVersion.HTTP_1_1;
        }
        
        if (!method.isImplemented() && this.request.getUri() != null) {
            status = ReasonPhase.STATUS_501;
        }
        
        if (method.isImplemented() && version.equals(RequestVersion.INVALID)) {
            version = RequestVersion.HTTP_1_1;
            status = ReasonPhase.STATUS_505;
            this.responseHeader.add(header.toString());
        } 

        header.append(version);
        header.append(" ");
        header.append(status);
        this.responseHeader.add(header.toString()); 

        if (!status.equals(ReasonPhase.STATUS_304) && !status.equals(ReasonPhase.STATUS_400)) {
            header.delete(0, header.length());
            header.append("Date: ");
            Calendar cal = GregorianCalendar.getInstance();
            Date today = cal.getTime();
            header.append(LogFormats.HEADER_DATE_TIME.format(today));
            this.responseHeader.add(header.toString());
            header.delete(0, header.length());

            if (!status.equals(ReasonPhase.STATUS_201) && !status.equals(ReasonPhase.STATUS_505) 
                    && !status.equals(ReasonPhase.STATUS_501)) {
                header.append("Last-Modified: ");
                header.append(LogFormats.HEADER_DATE_TIME.format(this.getLastModified(status)));
                this.responseHeader.add(header.toString());
                header.delete(0, header.length());
            } else
            if (status.equals(ReasonPhase.STATUS_201)) {
                header.append("Location: ");
                header.append(request.getUri());
                this.responseHeader.add(header.toString());
                header.delete(0, header.length());
            }

            HttpRequestHandler handler = this.request.getRequestHandler();
            if (!(handler instanceof EmptyBodyRequestHandler)
                    && !(handler instanceof DirectoryContentRequestHandlerStrategy)
                    && !(handler instanceof ScriptRequestHandlerStrategy)) {
                cal.add(Calendar.YEAR, 1);
                Date expires = cal.getTime();
                header.append("Expires: ");
                header.append(LogFormats.HEADER_DATE_TIME.format(expires));
                this.responseHeader.add(header.toString());
                header.delete(0, header.length());
            } else 
            if (handler instanceof InvalidRequestHandler) {
                cal.add(Calendar.YEAR, 50);
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

            if (status.equals(ReasonPhase.STATUS_201) || this.requestMustIncludeBody(status)) {
                header.append("Content-Length: ");
                header.append(this.getRequestSize());
                this.responseHeader.add(header.toString());
                header.delete(0, header.length());
            }

            if (status.equals(ReasonPhase.STATUS_200) 
                    && VOctopusConfigurationManager.getInstance().isCacheControlEnabled()) {
                header.append("CACHE-CONTROL: ");
                
                if (this.request.getRequestHandler() instanceof BinaryContentRequestHandlerStrategy
                        || this.request.getRequestHandler() instanceof AsciiContentRequestHandlerStrategy) {
                    header.append("private");
                } else 
                    header.append("no-store");
                
                this.responseHeader.add(header.toString());
                header.delete(0, header.length());
            }
        }
    }

    /**
     * @return the size of the request
     */
    public long getRequestSize() {
        if (this.requestSize == null) {
            HttpRequestHandler handler = this.request.getRequestHandler();
            if ((handler instanceof ScriptRequestHandlerStrategy && !handler.isRequestedResourceBinary())
                    || handler instanceof DirectoryContentRequestHandlerStrategy
                    || handler instanceof WebServiceRequestHandlerStrategy) {
                // TODO: Decide if this was a request to a directory, Script or something else and create more holders
                long nsize = 0;
                for (String lines : this.getResponseBody()) {
                    if (!lines.toLowerCase().startsWith("content-type: ")) {
                        nsize += lines.length();
                    }
                }
                this.requestSize = new Long(nsize);

            } else {

                this.requestSize = this.request.getRequestedResource().length();
            }
        }
        return this.requestSize;
    }

    /**
     * @param status
     * @return The modified status for the response.
     */
    public Date getLastModified(ReasonPhase status) {
        if (this.request.getStatus().equals(ReasonPhase.STATUS_200)
                && (this.request.getRequestHandler() instanceof ScriptRequestHandlerStrategy || this.request
                        .getRequestHandler() instanceof WebServiceRequestHandlerStrategy)) {
            return new Date();
        } else {
            return new Date(this.request.getRequestedResource().lastModified());
        }
    }

}