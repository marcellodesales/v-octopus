package edu.sfsu.cs.csc867.msales.voctopus.request;

import java.net.URI;
import java.util.Map;

import edu.sfsu.cs.csc867.msales.voctopus.request.handler.HttpRequestHandlerAbstractFactory;

/**
 * @author marcello
 * Feb 16, 2008 11:02:44 AM
 */
public class HttpContentRequest extends AbstractHttpRequest {

    public HttpContentRequest(String methodType, URI uri, String version, Map<String, String> headerVars) {
        super(methodType, uri, version, headerVars, HttpRequestHandlerAbstractFactory.getInstance().
                createRequestHandler(uri, headerVars));
    }
}
