package edu.sfsu.cs.csc867.msales.voctopus.response;

import java.io.OutputStream;

/**
 * @author marcello
 * Feb 16, 2008 10:31:02 AM
 */
public interface HttpResponse {

    public String[] getResponseBody();
    public String getResponseHeader();
    public int getStatusCode();
    public void sendResponse(OutputStream outputStream);
}
