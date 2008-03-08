package edu.sfsu.cs.csc867.msales.voctopus.request.handler;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class EmptyBodyRequestHandler extends AbstractRequestHandler {

    /**
     * When there's nothing to be handled. Implementation for 304 requests when cached.
     * @param uri is the uri for the resource
     * @param requestedFile is the requested file. Since it's the cached version, it will be only
     * used to check version and date of the file system change.
     */
    public EmptyBodyRequestHandler(URI uri, File requestedFile) {
        super(uri, requestedFile, RequestType.CACHED, null);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.handler.HttpRequestHandler#getResourceLines()
     */
    public String[] getResourceLines() throws IOException {
        return null;
    }
}
