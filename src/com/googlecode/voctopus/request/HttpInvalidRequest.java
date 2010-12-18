package com.googlecode.voctopus.request;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.util.Map;

/**
 * @author marcello
 * Mar 13, 2008 3:29:19 AM
 */
public class HttpInvalidRequest extends AbstractHttpRequest {

    /**
     * A regular invalid request
     * @param clientConnection
     * @param methodType
     * @param uri
     * @param version
     * @param headerVars
     * @param additionalHeaderData
     */
    public HttpInvalidRequest(InetAddress clientConnection, String methodType, URI uri, String version,
            Map<String, String> headerVars, String additionalHeaderData) {
        super(clientConnection, methodType, uri, version, headerVars, additionalHeaderData);
    }
    
    public HttpInvalidRequest(InetAddress clientConnection) {
        this(clientConnection, "invalid", null, "invalid", null, "");
    }
    
    public HttpInvalidRequest(InetAddress clientConnection, RequestVersion version) {
        this(clientConnection, "invalid", null, version.toString(), null, "");
    }

    public File getRequestedResource() {
        // TODO Auto-generated method stub
        return null;
    }

}
