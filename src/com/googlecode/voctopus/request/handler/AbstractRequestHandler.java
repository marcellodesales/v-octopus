package com.googlecode.voctopus.request.handler;

import java.io.File;
import java.net.URI;

import com.googlecode.voctopus.RequestResponseMediator.ReasonPhase;


public abstract class AbstractRequestHandler implements HttpRequestHandler {

    /**
     * The physical requested file. It might exist or not and the handler is responsible for determining that.
     */
    private File physicalFile;

    /**
     * The content type identified from the mime-types.
     */
    protected String contentType;

    /**
     * Defines if the requested resource if binary or a text file.
     * 
     * @author marcello Feb 24, 2008 2:28:24 PM
     */
    public enum RequestType {
        ASCII, BINARY, CACHED, PROTECTED, PROHIBITED, INVALID;
    }

    /**
     * The type of the request, depending on the mime types
     */
    protected RequestType requestType;

    /**
     * The status as the result of the request processing
     */
    protected ReasonPhase status;

    /**
     * Original requested URI by the client.
     */
    private URI uri;

    /**
     * Constructs a new request handler.
     * 
     * @param uri the uri requested by the client connection
     * @param requestedFile the file on the time of the request. It can be the 404 file.
     * @param requestType the request type that was bound to the handler
     * @param contentType is the handler found on the configuration file.
     */
    public AbstractRequestHandler(URI uri, File requestedFile, RequestType requestType, String contentType) {
        this.physicalFile = requestedFile;
        this.requestType = requestType;
        this.contentType = contentType;
        this.uri = uri;
        if (requestedFile == null) {
            this.status = ReasonPhase.STATUS_204;
        }
        // System.out.println("Selected handler: " + this);
        // System.out.println("File to be handled: " + requestedFile);
        // System.out.println("REquest type: " + requestType);
        // System.out.println("Handler found: " + this.handlerFound);
    }

    /**
     * @return if the requested resource exists.
     */
    public boolean requestedResourceExists() {
        // TODO: It must handle CGI scripts, Web Services, Etc.
        if (this instanceof DirectoryContentRequestHandlerStrategy || this instanceof ScriptRequestHandlerStrategy
                || this instanceof UnknownContentRequestHandlerStrategy
                || this instanceof WebServiceRequestHandlerStrategy) {
            return true;
        } else if (this instanceof AsciiContentRequestHandlerStrategy
                || this instanceof BinaryContentRequestHandlerStrategy) {
            return this.physicalFile.exists();
        } else
            return false;

    }

    public URI getRequestedResource() {
        return this.uri;
    }

    /**
     * @return The type of this request.
     */
    public RequestType getRequestType() {
        return this.requestType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.googlecode.voctopus.request.handler.HttpRequestHandler#getRequestedResource()
     */
    public File getRequestedFile() {
        return this.physicalFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.googlecode.voctopus.request.handler.HttpRequestHandler#getStatus()
     */
    public ReasonPhase getStatus() {
        return this.status;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.googlecode.voctopus.request.handler.HttpRequestHandler#getContentType()
     */
    public String getContentType() {
        return this.contentType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.googlecode.voctopus.request.handler.HttpRequestHandler#isRequestedResourceBinary()
     */
    public boolean isRequestedResourceBinary() {
        return this.requestType.equals(RequestType.BINARY);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.googlecode.voctopus.request.handler.HttpRequestHandler#setStatus(com.googlecode.voctopus.RequestResponseMediator.ReasonPhrase)
     */
    public void setStatus(ReasonPhase status) {
        this.status = status;
    }
}
