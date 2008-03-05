package edu.sfsu.cs.csc867.msales.voctopus.request.validation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import edu.sfsu.cs.csc867.msales.httpd.validation.HttpRequestInterpreterException;
import edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest;
import edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequestAbstractFactory;
import edu.sfsu.cs.csc867.msales.voctopus.request.AbstractHttpRequest.RequestMethodType;

/**
 * The context of the Interpretation of the HttpRequest is the list of lines sent by the client during an HTTP request
 * to the server. The context holds the original information to be processed and sends the resulting Interpretation back
 * to the consumer API. (our case using getParsedRequest().)
 * 
 * @author marcello Feb 16, 2008 10:51:13 AM
 */
public class HttpRequestInterpreterContext {

    /**
     * The first lines sent by the client connection. It is formed by the first line sent by the client and the request
     * header variables.
     */
    private String[] requestLinesContext;
    
    /**
     * 
     * @author marcello
     * Feb 20, 2008 2:19:52 PM
     */
    public static enum RequestType {
        STATIC_CONTENT, 
        SCRIPT_EXECUTION,
        WEB_SERVICE;
    }
    /**
     * The content type of the execution. It defines the type of Content Handler to be used.
     */
    public RequestType requestType;
        
    /**
     * The request method used on the request method.
     */
    private RequestMethodType requestMethod;

    /**
     * The versions of the HTTP protocol that are accepted
     */
    public static enum RequestVersion {
        HTTP_1_1("HTTP/1.1"),
        HTTP_1_0("HTTP/1.0");
        
        private String versionToken;
        private RequestVersion(String versionToken) {
            this.versionToken = versionToken;
        }
        
        @Override
        public String toString() {
            return this.versionToken;
        };
    }
    /**
     * The version used on the request.
     */
    private RequestVersion requestVersion;
    
    /**
     * The query string from the request. It's part of the URI, just following the question mark
     * <BR>
     * <li>www.google.com/Resource?var1=value1&var2=value2
     * <BR>The query string is ?var1=value1&var2=value2
     */
    private String queryString;

    /**
     * The request URI.
     */
    private URI uri;

    /**
     * Request parameters parsed from the query string 
     */
    private HashMap<String, String> requestParameters;
    
    /**
     * @return The content type that matches to this request. It will be used to construct the request handler.
     */
    public RequestType getRequestType() {
        return this.requestType;
    }

    /**
     * Creates a new interpreter context based on the request lines
     * 
     * @param context is the interpretation context. In our case, the HttpRequest lines sent by the client during an
     *            HTTP connection.
     * @throws HttpRequestInterpreterException if the format of request lines are not valid.
     */
    public HttpRequestInterpreterContext(String[] context) throws HttpRequestInterpreterException {
        this.requestLinesContext = context;
        this.validatePrerequirementsOfContext();
    }

    /**
     * Validates the request lines.
     * 
     * @throws HttpRequestInterpreterException in case one of the request tokens are missing
     */
    private void validatePrerequirementsOfContext() throws HttpRequestInterpreterException {
        String[] firstLineTokens = this.requestLinesContext[0].split(" ");
        for (String token : firstLineTokens) {
            if (token == null || token.equals(" ")) {
                throw new HttpRequestInterpreterException("Http Request is malformed!", firstLineTokens[0]);
            }
        }
        // TODO: VALIDATE THE REST OF THE REQUEST METHOD VARIABLES.
    }

    /**
     * @return the list of all the lines from the request, which is comprised by the first line of the request and all
     *         the request headers.
     */
    public String[] getRequestLines() {
        return requestLinesContext;
    }

    /**
     * Gets the line of the request on a given position
     * 
     * @param position is the position of the line.
     * @return the line on the given position from the list of the requested lines.
     */
    public String getRequestLine(int position) {
        return this.requestLinesContext[position];
    }

    /**
     * @param vars is the array of header variables with its respective value
     * @param method is the method expression, that also holds the rest of the first line expression
     * @return the resulting instance of the HttpRequest depending on the type of the request
     */
    public HttpRequest getParsedRequest(HttpRequestMethodExpression firstLineExpr, HttpRequestHeaderFieldVarExpression[] vars) {
        return HttpRequestAbstractFactory.getInstance().createHttpRequest(firstLineExpr, vars);
    }

    /**
     * Sets the Method type of the request. 
     * @param methodType
     */
    public void setMethodType(RequestMethodType methodType) {
        this.requestMethod = methodType;
    }

    /**
     * Sets the request Type
     * @param requestType
     */
    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
        
    }

    /**
     * Sets the query string of the request.
     * @param queryString is the query string from the request.
     */
    public void setQueryString(String queryString) {
        this.queryString = queryString;
        
    }

    /**
     * Sets the URI of the request.
     * @param requestUri is the request URI sent by the request.
     */
    public void setURI(String requestUri) {
        try {
            this.uri = new URI(requestUri);
        } catch (URISyntaxException e) {
            //TODO: This exception should not be thrown once the interpreter would catch the malformness
        }
    }

    public RequestMethodType getRequestMethod() {
        return requestMethod;
    }

    public RequestVersion getRequestVersion() {
        return requestVersion;
    }

    public String getQueryString() {
        return queryString;
    }

    public URI getUri() {
        return uri;
    }

    public HashMap<String, String> getRequestParameters() {
        return requestParameters;
    }
    
    public Map<String, String> getRequestHeaderVars() {
        Map<String, String> headerVars = new HashMap<String, String>(this.requestLinesContext.length-1);
        String[] valeuVar = new String[2];
        for (int i = 1; i < this.requestLinesContext.length; i++) {
            valeuVar = this.requestLinesContext[i].split(": ");
            headerVars.put(valeuVar[0], valeuVar[1]);
        }
        return headerVars;
     }

    public void setRequestVersion(RequestVersion version) {
        this.requestVersion = version;
    }
}