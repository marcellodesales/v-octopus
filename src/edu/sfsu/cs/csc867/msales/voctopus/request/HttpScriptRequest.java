package edu.sfsu.cs.csc867.msales.voctopus.request;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.util.Map;

/**
 * @author marcello Feb 16, 2008 11:02:29 AM
 */
public class HttpScriptRequest extends AbstractHttpRequest {

    public HttpScriptRequest(InetAddress clientAddress, String methodType, URI uri, String version,
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