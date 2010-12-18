package com.googlecode.voctopus.request.handler;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import com.googlecode.voctopus.RequestResponseMediator.ReasonPhase;


public class EmptyBodyRequestHandler extends AbstractRequestHandler {

    /**
     * Constructs a new Request Type
     * 
     * @param uri
     * @param requestedFile
     * @param requestType
     * @param status
     */
    public EmptyBodyRequestHandler(URI uri, File requestedFile, RequestType requestType, ReasonPhase status) {
        super(uri, requestedFile, requestType, null);
        this.status = status;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.handler.HttpRequestHandler#getResourceLines()
     */
    public String[] getResourceLines() throws IOException {
        return null;
    }

    public String[] getParticularResponseHeaders() {
        // TODO Auto-generated method stub
        return null;
    }
}
