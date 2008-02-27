package edu.sfsu.cs.csc867.msales.voctopus.request.handler;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import edu.sfsu.cs.csc867.msales.voctopus.RequestResponseMediator.ReasonPhrase;

/**
 * @author marcello
 * Feb 24, 2008 7:55:58 AM
 */
public interface HttpRequestHandler {

    /**
     * @return The physical requested resource. 
     */
    public File getRequestedFile();
    
    public URI getRequestedResource();
    
    /**
     * @return The status code about the file. Just maps the 200, 404. Other status must be handled on other
     * Interfaces for the request.
     */
    public ReasonPhrase getStatus();

    /**
     * @return The contetn type matched with the configuration file.
     */
    public String getContentType();

    /**
     * @return the resource lines from the request
     * @throws IOException if any problem occurs with the request
     */
    public String[] getResourceLines() throws IOException;
    
    /**
     * @return if the request produced a binary file to the output
     */
    public boolean isRequestedResourceBinary();
    
    /**
     * @return if the requested resource was found on the file system.
     * <li>If the request was for a filesystem, returns if the file exist;
     * <li>If the request is for a given cgi script, returns if the cgi exists
     * <li>If the request was for a web servcer resource, returns if the resource exists
     */
    public boolean requestedResourceExists();
    
    /**
     * Sets the status of the request
     * @param reason
     */
    public void setStatus(ReasonPhrase reason);
}
