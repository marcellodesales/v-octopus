package com.googlecode.voctopus.request;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.util.Map;

/**
 * Responsible for the API access.
 * 
 * @author marcello Feb 24, 2008 7:51:16 AM
 */
public class HttpWebServiceRequest extends AbstractHttpRequest {

    /**
     * Build a webservice request
     * @param clientAddress
     * @param methodType
     * @param uri
     * @param version
     * @param headerVars
     * @param additionalData
     */
    public HttpWebServiceRequest(InetAddress clientAddress, String methodType, URI uri, String version,
            Map<String, String> headerVars, String additionalData) {
        super(clientAddress, methodType, uri, version, headerVars, additionalData);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.googlecode.voctopus.request.HttpRequest#getRequestedResource()
     */
    public File getRequestedResource() {
        return null;
    }

}
