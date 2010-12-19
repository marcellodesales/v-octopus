package com.googlecode.voctopus.request;

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
            Map<String, String> headerVars, String additionalData) {
        super(clientAddress, methodType, uri, version, headerVars, additionalData);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.googlecode.voctopus.request.HttpRequest#getRequestedResource()
     */
    public File getRequestedResource() {
        return this.requestHandler.getRequestedFile();
    }

}
