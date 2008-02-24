package edu.sfsu.cs.csc867.msales.voctopus.request.handler;

import java.io.File;
import java.io.IOException;

/**
 * @author marcello
 * Feb 24, 2008 7:55:58 AM
 */
public interface HttpRequestHandler {

    /**
     * @return The physical requested resource. 
     */
    public File getRequestedResource();
    
    /**
     * @return The status code about the file. Just maps the 200, 404. Other status must be handled on other
     * Interfaces for the request.
     */
    public int getStatus();

    public String getContentType();

    public String[] getResourceLines() throws IOException;
    
    public boolean isRequestedResourceBinary();
}
