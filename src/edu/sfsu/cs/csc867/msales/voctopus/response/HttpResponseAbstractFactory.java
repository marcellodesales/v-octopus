package edu.sfsu.cs.csc867.msales.voctopus.response;

import edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest;

/**
 * @author marcello
 * Feb 16, 2008 10:40:02 AM
 */
public class HttpResponseAbstractFactory {

    /**
     * TheradLocal instance for this singleton.
     */
    private static ThreadLocal<HttpResponseAbstractFactory> singleton = new ThreadLocal<HttpResponseAbstractFactory>() {
        @Override
        protected HttpResponseAbstractFactory initialValue() {
            // TODO Auto-generated method stub
            return new HttpResponseAbstractFactory();
        }
    };

    /**
     * Private constructor for the singleton.
     */
    private HttpResponseAbstractFactory() {
    }

    /**
     * @return the unique instance of this class.
     */
    public static HttpResponseAbstractFactory getInstance() {
        return singleton.get();
    }
    
    /**
     * Creates a new HttpResponse instance based on the type of the HttpRequest
     * @param request is the request from the client. 
     * @return an instance of HttpResponse for content, script or web services request.
     */
    public HttpResponse createNewHttpResponse(HttpRequest request) {
        if (request.isResourceBinary()) {
            return new BinaryContentResponseDecorator(request);
        } else {
            return new AsciiContentResponseDecorator(request);
        }
    }
}