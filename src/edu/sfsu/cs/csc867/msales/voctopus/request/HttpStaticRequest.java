package edu.sfsu.cs.csc867.msales.voctopus.request;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.util.Map;

/**
 * @author marcello Feb 16, 2008 11:02:44 AM
 */
public class HttpStaticRequest extends AbstractHttpRequest {

    /**
     * Creates a new HttpContent handler
     * 
     * @param methodType
     * @param uri
     * @param version
     * @param headerVars
     */
    public HttpStaticRequest(InetAddress clientAddress, String methodType, URI uri, String version,
            Map<String, String> headerVars) {
        super(clientAddress, methodType, uri, version, headerVars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest#getRequestedResource()
     */
    public File getRequestedResource() {
        return this.requestHandler.getRequestedFile();
    }

}
