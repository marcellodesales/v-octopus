package edu.sfsu.cs.csc867.msales.voctopus.response;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import edu.sfsu.cs.csc867.msales.voctopus.RequestResponseMediator.ReasonPhrase;

/**
 * @author marcello
 * Feb 16, 2008 10:31:02 AM
 */
public interface HttpResponse {

    public String[] getResponseBody();
    public String[] getResponseHeader();
    public ReasonPhrase getStatusCode();
    public void sendResponse(OutputStream outputStream) throws IOException;
    
    public void sendBody(OutputStream outputStream, PrintWriter writer) throws IOException;
    public void sendHeader(PrintWriter writer) throws IOException;
}
