package edu.sfsu.cs.csc867.msales.voctopus.request;

import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Map;

import edu.sfsu.cs.csc867.msales.voctopus.request.handler.HttpRequestHandlerAbstractFactory;

/**
 * @author marcello
 * Feb 16, 2008 11:02:44 AM
 */
public class HttpContentRequest extends AbstractHttpRequest {

    /**
     * Creates a new HttpContent handler
     * @param methodType
     * @param uri
     * @param version
     * @param headerVars
     * @throws FileNotFoundException in case the requested URI doesn't map to a file
     */
    public HttpContentRequest(String methodType, URI uri, String version, Map<String, String> headerVars) {
        super(methodType, uri, version, headerVars, 
         HttpRequestHandlerAbstractFactory.getInstance().createRequestHandler(uri, headerVars));
    }
}
