package edu.sfsu.cs.csc867.msales.voctopus.request;

import java.io.File;
import java.net.URI;

import edu.sfsu.cs.csc867.msales.voctopus.RequestResponseMediator.ReasonPhase;
import edu.sfsu.cs.csc867.msales.voctopus.request.handler.HttpRequestHandler;

/**
 * @author marcello
 * Feb 16, 2008 10:26:13 PM
 */
public interface HttpRequest {

    public String getMethodType();
    public File getRequestedResource();
    public ReasonPhase getStatus();
    public URI getUri();
    public String getRequestVersion();
    public String[] getResourceLines();
    public String getContentType();
    public boolean isResourceBinary();
    public boolean isRequestSuccessful();
    public boolean keepAlive();
    public String[] getRequestHeaders();
    public HttpRequestHandler getRequestHandler();
}
