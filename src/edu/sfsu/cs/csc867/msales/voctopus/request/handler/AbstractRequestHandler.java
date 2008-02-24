package edu.sfsu.cs.csc867.msales.voctopus.request.handler;

import java.io.File;


public abstract class AbstractRequestHandler implements HttpRequestHandler {

    /**
     * The physical requested file. It might exist or not and the handler is responsible for determining that.
     */
    private File physicalFile;
    
    /**
     * The content type identified from the mime-types.
     */
    private String contentType;
    
    
    /**
     * Defines if the requested resource if binary or a text file.
     * @author marcello
     * Feb 24, 2008 2:28:24 PM
     */
    public enum RequestType {
        ASCII, BINARY;
    }
    
    /**
     * The type of the request, depending on the mime types
     */
    private RequestType requestType;
    
    /**
     * Constructs a new request handler.
     * @param requestedFile
     * @param requestType
     */
    public AbstractRequestHandler(File requestedFile, RequestType requestType) {
        this.physicalFile = requestedFile;
        this.requestType = requestType;
    }
    
    /**
     * @return The type of this request.
     */
    public RequestType getRequestType() {
        return this.requestType;
    }
    
    /* (non-Javadoc)
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.handler.HttpRequestHandler#getRequestedResource()
     */
    public File getRequestedResource() {
        
        return this.physicalFile;
    }
    
    /* (non-Javadoc)
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.handler.HttpRequestHandler#getStatus()
     */
    public int getStatus() {
        return this.physicalFile.exists() ? 200 : 404;
    }
    
    /* (non-Javadoc)
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.handler.HttpRequestHandler#getContentType()
     */
    public String getContentType() {
        return this.contentType;
    }
    
    /* (non-Javadoc)
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.handler.HttpRequestHandler#isRequestedResourceBinary()
     */
    public boolean isRequestedResourceBinary() {
        return this.requestType.equals(RequestType.BINARY);
    }
}

