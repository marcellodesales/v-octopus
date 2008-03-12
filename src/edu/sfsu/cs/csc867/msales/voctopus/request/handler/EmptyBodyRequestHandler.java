package edu.sfsu.cs.csc867.msales.voctopus.request.handler;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import edu.sfsu.cs.csc867.msales.voctopus.RequestResponseMediator.ReasonPhrase;

public abstract class EmptyBodyRequestHandler extends AbstractRequestHandler {

    /**
     * Constructs a new Request Type
     * 
     * @param uri
     * @param requestedFile
     * @param requestType
     * @param status
     */
    public EmptyBodyRequestHandler(URI uri, File requestedFile, RequestType requestType, ReasonPhrase status) {
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
}
