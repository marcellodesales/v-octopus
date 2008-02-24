package edu.sfsu.cs.csc867.msales.voctopus.request;

import java.net.URI;
import java.util.Map;


/**
 * @author marcello
 * Feb 16, 2008 11:02:29 AM
 */
public class HttpScriptRequest extends AbstractHttpRequest {

    public HttpScriptRequest(String methodType, URI uri, String version, Map<String, String> headerVars) {
        super(methodType, uri, version, headerVars, null);
        // TODO Auto-generated constructor stub
    }

    public String[] getResourceLines() {
        // TODO Auto-generated method stub
        return null;
    }

}
