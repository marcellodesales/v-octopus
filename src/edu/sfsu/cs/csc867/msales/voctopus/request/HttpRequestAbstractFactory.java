package edu.sfsu.cs.csc867.msales.voctopus.request;

import edu.sfsu.cs.csc867.msales.voctopus.request.validation.HttpRequestHeaderFieldVarExpression;
import edu.sfsu.cs.csc867.msales.voctopus.request.validation.HttpRequestInterpreterContext;
import edu.sfsu.cs.csc867.msales.voctopus.request.validation.HttpRequestMethodExpression;

/**
 * Creates a new Request based on the Method expression and the version
 * 
 * @author marcello Feb 20, 2008 10:34:49 AM
 */
public class HttpRequestAbstractFactory {

    /**
     * TheradLocal instance for this singleton.
     */
    private static ThreadLocal<HttpRequestAbstractFactory> singleton = new ThreadLocal<HttpRequestAbstractFactory>() {
        @Override
        protected HttpRequestAbstractFactory initialValue() {
            return new HttpRequestAbstractFactory();
        }
    };

    /**
     * Private constructor for the singleton.
     */
    private HttpRequestAbstractFactory() {
    }

    /**
     * @return the unique instance of this class.
     */
    public static HttpRequestAbstractFactory getInstance() {
        return singleton.get();
    }

    /**
     * @param firstLineExpr
     * @param vars
     * @return a new instance of an HttpRequest.
     */
    public HttpRequest createHttpRequest(HttpRequestMethodExpression firstLineExpr,
            HttpRequestHeaderFieldVarExpression[] vars) {

        HttpRequest req = null;
        HttpRequestInterpreterContext context = firstLineExpr.getContext();
        switch (firstLineExpr.getContext().getRequestType()) {
        case STATIC_CONTENT:
            req = new HttpContentRequest(context.getRequestMethod().toString(), context.getUri(), 
                    context.getRequestVersion().toString(), context.getRequestHeaderVars());
            break;
        case SCRIPT_EXECUTION:
            req = new HttpScriptRequest(context.getRequestMethod().toString(), context.getUri(), 
                    context.getRequestVersion().toString(), context.getRequestHeaderVars());
            break;
            
        case WEB_SERVICE:
            req = new HttpWebServiceRequest(context.getRequestMethod().toString(), context.getUri(), 
                    context.getRequestVersion().toString(), context.getRequestHeaderVars());
            break;
        }
        return req;
    }
}