package edu.sfsu.cs.csc867.msales.voctopus.request.validation;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest;
import edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequestAbstractFactory;
import edu.sfsu.cs.csc867.msales.voctopus.request.AbstractHttpRequest.RequestMethodType;
import edu.sfsu.cs.csc867.msales.voctopus.request.AbstractHttpRequest.RequestVersion;
import edu.sfsu.cs.csc867.msales.voctopus.request.validation.HttpRequestInterpreterException.ErrorToken;

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
     * @author marcello Feb 20, 2008 2:19:52 PM
     */
    public static enum RequestType {
        STATIC_CONTENT, SCRIPT_EXECUTION, WEB_SERVICE, INVALID;
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
     * The version used on the request.
     */
    private RequestVersion requestVersion;

    /**
     * The query string from the request. It's part of the URI, just following the question mark <BR>
     * <li>www.google.com/Resource?var1=value1&var2=value2 <BR>
     * The query string is ?var1=value1&var2=value2
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
     * Additional data sent to the POST and PUT request methods.
     */
    private String additionalEncodedData;

    /**
     * Additional data that was decoded from the encoded version
     */
    private String additionalDecodedData;

    /**
     * If the request is malformed, then the 400 error must be submitted.
     */
    private boolean malformedRequest;

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
                throw new HttpRequestInterpreterException("Http Request is malformed!", ErrorToken.METHOD_TYPE
                        .setToken(firstLineTokens[0]));
            }
        }
    }

    /**
     * @return the list of all the lines from the request, which is comprised by the first line of the request and all
     *         the request headers.
     */
    public String[] getRequestLines() {
        return requestLinesContext;
    }
    
    public void setRequestLines(String[] newLines) {
        this.requestLinesContext = newLines;
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
    public HttpRequest getParsedRequest(HttpRequestMethodExpression firstLineExpr,
            HttpRequestHeaderFieldVarExpression[] vars, InetAddress clientAddress) {
        return HttpRequestAbstractFactory.getInstance().createHttpRequest(firstLineExpr, vars, clientAddress);
    }

    /**
     * Sets the Method type of the request.
     * 
     * @param methodType
     */
    public void setMethodType(RequestMethodType methodType) {
        this.requestMethod = methodType;
    }

    /**
     * Sets the request Type
     * 
     * @param requestType
     */
    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    /**
     * Sets the query string of the request.
     * 
     * @param queryString is the query string from the request.
     */
    public void setQueryString(String queryString) {
        this.queryString = queryString;

    }

    /**
     * Sets the URI of the request.
     * 
     * @param requestUri is the request URI sent by the request.
     */
    public void setURI(String requestUri) {
        try {
            this.uri = new URI(requestUri);
        } catch (URISyntaxException e) {
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

    /**
     * @return The list of the request header vars. The last var might be from the post request
     */
    public Map<String, String> getRequestHeaderVars() {
        Map<String, String> headerVars = new HashMap<String, String>(this.requestLinesContext.length);
        String[] valeuVar = new String[2];
        for (int i = 1; i < requestLinesContext.length; i++) {
            if (this.requestLinesContext[i].indexOf(": ") < 0) {
                continue;
            }
            valeuVar = this.requestLinesContext[i].split(": ");
            headerVars.put(valeuVar[0], valeuVar[1]);
        }
        return headerVars;
    }

    public void setRequestVersion(RequestVersion version) {
        this.requestVersion = version;
    }

    /**
     * @return the additional data from a POST or PUT request methods.
     */
    public String getAdditionalEncodedData() {
        return this.additionalEncodedData;
    }

    /**
     * Sets a new additional encoded data from a POST or PUT request methods.
     * 
     * @param additionalData
     */
    public void setAdditionalEncodedData(String additionalData) {
        this.additionalEncodedData = additionalData;
    }

    /**
     * @param additionalDecodedData sets the additional decoded data.
     */
    public void setAdditionalDecodedData(String additionalDecodedData) {
        this.additionalDecodedData = additionalDecodedData;
    }

    public String getAdditionalDecodedData() {
        return additionalDecodedData;
    }

    /**
     * Signals the request included malformed tokens
     */
    public void signalMalformedRequest() {
        this.malformedRequest = true;
    }

    public boolean isRequestFormatMalformed() {
        return this.malformedRequest;
    }
}