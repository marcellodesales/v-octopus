package edu.sfsu.cs.csc867.msales.voctopus.request;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.util.Map;

public class HttpInvalidRequest extends AbstractHttpRequest {

    public HttpInvalidRequest(InetAddress clientConnection, String methodType, URI uri, String version,
            Map<String, String> headerVars, String additionalHeaderData) {
        super(clientConnection, methodType, uri, version, headerVars, additionalHeaderData);
    }
    
    public HttpInvalidRequest(InetAddress clientConnection) {
        this(clientConnection, "invalid", null, "invalid", null, "");
    }

    public File getRequestedResource() {
        // TODO Auto-generated method stub
        return null;
    }

}
