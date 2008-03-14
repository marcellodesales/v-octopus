package edu.sfsu.cs.csc867.msales.voctopus.request;

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

    public HttpWebServiceRequest(InetAddress clientAddress, String methodType, URI uri, String version,
            Map<String, String> headerVars) {
        super(clientAddress, methodType, uri, version, headerVars);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest#getRequestedResource()
     */
    public File getRequestedResource() {
        return null;
    }

}
