package com.googlecode.voctopus.response;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.googlecode.voctopus.request.HttpRequest;


/**
 * @author marcello Feb 16, 2008 11:01:47 AM
 */
public class AsciiContentResponseDecorator extends AbstractHttpResponse {

    public AsciiContentResponseDecorator(HttpRequest originatingRequest) {
        super(originatingRequest);

    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.sfsu.cs.csc867.msales.voctopus.response.HttpResponse#sendBody(java.io.OutputStream, java.io.PrintWriter)
     */
    public void sendBody(OutputStream outputStream, PrintWriter writer) {
        for (String line : this.getResponseBody()) {
            if (!line.toLowerCase().startsWith("content-type: ")) {
                writer.println(line);
            }
        }
    }

    public void sendHeader(PrintWriter writer) throws IOException {
        if (this.getRequest().getRequestHandler().getParticularResponseHeaders() != null) {
            for (String particularHeader : this.getRequest().getRequestHandler().getParticularResponseHeaders()) {
                writer.println(particularHeader);
            }
        }
    }
}
