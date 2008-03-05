package edu.sfsu.cs.csc867.msales.voctopus.request;

import java.net.URI;
import java.util.Map;

import edu.sfsu.cs.csc867.msales.voctopus.request.handler.HttpRequestHandlerAbstractFactory;

/**
 * @author marcello
 * Feb 16, 2008 11:02:44 AM
 */
public class HttpStaticRequest extends AbstractHttpRequest {

    /**
     * Creates a new HttpContent handler
     * @param methodType
     * @param uri
     * @param version
     * @param headerVars
     */
    public HttpStaticRequest(String methodType, URI uri, String version, Map<String, String> headerVars) {
        super(methodType, uri, version, headerVars, 
         HttpRequestHandlerAbstractFactory.getInstance().createRequestHandler(uri, headerVars));
    }
}
