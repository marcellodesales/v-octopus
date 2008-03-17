package edu.sfsu.cs.csc867.msales.voctopus.request;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;

import edu.sfsu.cs.csc867.msales.voctopus.RequestResponseMediator.ReasonPhase;
import edu.sfsu.cs.csc867.msales.voctopus.request.AbstractHttpRequest.RequestMethodType;
import edu.sfsu.cs.csc867.msales.voctopus.request.AbstractHttpRequest.RequestVersion;
import edu.sfsu.cs.csc867.msales.voctopus.request.handler.HttpRequestHandler;

/**
 * Handles the Http requests from the client.
 * 
 * @author marcello Feb 16, 2008 10:26:13 PM
 */
public interface HttpRequest {

    /**
     * @return The method type representation.
     */
    public RequestMethodType getMethodType();

    /**
     * @return The file translated from the resource indicated on the URI.
     */
    public File getRequestedResource();

    /**
     * @return The status code as the reason phase representation.
     */
    public ReasonPhase getStatus();

    /**
     * @return The original requested URI from the client.
     */
    public URI getUri();

    /**
     * @return The Http version representation.
     */
    public RequestVersion getRequestVersion();

    /**
     * @return The requested resource lines, which includes the header and the body from the request.
     */
    public String[] getResourceLines();

    /**
     * @return The content Type of the request. This is the MIME type string.
     */
    public String getContentType();

    /**
     * @return Verifies whether the requested resource is a binary file.
     */
    public boolean isResourceBinary();

    /**
     * @return Verifies if the request was a successful one.
     */
    public boolean isRequestSuccessful();

    /**
     * @return Specification of the request, if it asked the server to maintain it alive. This is used for the cache
     *         mechanism.
     */
    public boolean keepAlive();

    /**
     * @return The request headers received from the client.
     */
    public String[] getRequestHeaders();

    /**
     * @return The request handler selected to handle the request.
     */
    public HttpRequestHandler getRequestHandler();

    /**
     * @return The InetAddress from the socket connection with the client.
     * @see InetAddress
     */
    public InetAddress getInetAddress();
}
