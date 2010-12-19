package com.googlecode.voctopus.response;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import com.googlecode.voctopus.request.HttpRequest;


/**
 * @author marcello Feb 16, 2008 11:01:47 AM
 */
public class AsciiContentResponseDecorator extends AbstractHttpResponse {

    private static final Logger logger = Logger.getLogger(AsciiContentResponseDecorator.class);

    public AsciiContentResponseDecorator(HttpRequest originatingRequest) {
        super(originatingRequest);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.googlecode.voctopus.response.HttpResponse#sendBody(java.io.OutputStream, java.io.PrintWriter)
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
                logger.debug("HTTP Particular Response Header: " + particularHeader);
            }
        }
    }
}
