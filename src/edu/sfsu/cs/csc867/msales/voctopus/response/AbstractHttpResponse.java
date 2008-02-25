package edu.sfsu.cs.csc867.msales.voctopus.response;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.sfsu.cs.csc867.msales.httpd.HttpErrorException;
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

    private static final String RESPONSE_DATE_FORMAT = "EEE,.MMM.d yyyy HH:mm:ss z";

    private static final String SERVER_NAME_VERSION = "VOctopus/0.1";
    
    private enum ReasonPhrase {
        STATUS_100("Continue"),
        STATUS_101("Switching Protocols"),
        STATUS_200("OK"),
        STATUS_201("Created"),
        STATUS_202("Accepted"),
        STATUS_203("Non-Authoritative Information"),
        STATUS_204("No Content"),
        STATUS_205("Reset Content"),
        STATUS_206("Partial Content"),
        STATUS_300("Multiple Choices"),
        STATUS_301("Moved Permanently"),
        STATUS_302("Found"),
        STATUS_303("See Other"),
        STATUS_304("Not Modified"),
        STATUS_305("Use Proxy"),
        STATUS_307("Temporary Redirect"),
        STATUS_400("Bad Request"),
        STATUS_401("Unauthorized"),
        STATUS_402("Payment Required"),
        STATUS_403("Forbidden"),
        STATUS_404("Not Found"),
        STATUS_405("Method Not Allowed"),
        STATUS_406("Not Acceptable");

        private String humanValue;
        private ReasonPhrase(String humanValue) {
            this.humanValue = humanValue;
        }
        
        @Override
        public String toString() {
            return this.humanValue;
        }
        
        public static ReasonPhrase getReasonPhrase(int code) {
            for (ReasonPhrase key : ReasonPhrase.values()) {
                if (key.toString().contains(String.valueOf(code))) {
                    return key;
                }
            }
            return STATUS_406;
        }
    }
    
    private HttpRequest request;
    
    public AbstractHttpResponse(HttpRequest originatingRequest) {
        this.request = originatingRequest;
    }
    
    public String[] getResponseBody() {
        return this.request.getResourceLines();
    }
    
    public int getStatusCode() {
        return this.request.getStatus();
    }
    
    /** 
     * Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
     */
    public String getResponseHeader() {
        StringBuilder header = new StringBuilder();
        header.append(this.request.getRequestVersion());
        header.append(" ");
        header.append(this.getStatusCode());
        header.append(" ");
        header.append(ReasonPhrase.getReasonPhrase(this.request.getStatus()));
        return header.toString();
    }
    
    public void sendResponse(OutputStream clientOutput){
        
        try {

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientOutput));
            PrintWriter writer = new PrintWriter(out, true);
            writeResponseToClient(new PrintWriter(System.out));
            writeResponseToClient(writer);
            //The buffer needs to be closed.
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ioe) {
                    throw HttpErrorException.buildNewException(ioe);
                }
            }
        
        } catch (HttpErrorException e) {
            // TODO Auto-generated catch block
            //Must SEND AN ERROR CODE TO THE CLIENT.
            e.printStackTrace();
        }
    }
    
    private void writeResponseToClient(PrintWriter writer) {
        writeHeader(writer);
        writer.println("");
        writeBody(writer);
    }
    
    private String[] getResponseHeaderVars() {
        return new String[] {
                "Date: " + new SimpleDateFormat(RESPONSE_DATE_FORMAT).format(new Date()),
                "Server: " + SERVER_NAME_VERSION,
                "Content-Type: " + this.request.getContentType()
        };
    }
    
    private void writeHeader(PrintWriter writer) {
        writer.println(this.getResponseHeader());
        
        for (String headerVar : this.getResponseHeaderVars()) {
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