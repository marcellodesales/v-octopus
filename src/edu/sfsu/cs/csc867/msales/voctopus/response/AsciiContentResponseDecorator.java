package edu.sfsu.cs.csc867.msales.voctopus.response;

import java.io.OutputStream;
import java.io.PrintWriter;

import edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest;

/**
 * @author marcello
 * Feb 16, 2008 11:01:47 AM
 */
public class AsciiContentResponseDecorator extends AbstractHttpResponse {

    public AsciiContentResponseDecorator(HttpRequest originatingRequest) {
        super(originatingRequest);
        
    }

    /* (non-Javadoc)
     * @see edu.sfsu.cs.csc867.msales.voctopus.response.HttpResponse#sendBody(java.io.OutputStream, java.io.PrintWriter)
     */
    public void sendBody(OutputStream outputStream, PrintWriter writer) {
        for(String line : this.getResponseBody()) {
            writer.println(line);
        } 
    }

    /* (non-Javadoc)
     * @see edu.sfsu.cs.csc867.msales.voctopus.response.HttpResponse#sendHeader(java.io.PrintWriter)
     */
    public void sendHeader(PrintWriter writer) {
        writer.println("Content-Type: " + this.getRequest().getContentType());
        for (String headerVar : this.getResponseHeader()) {
            writer.println(headerVar);
        }
    }
}
