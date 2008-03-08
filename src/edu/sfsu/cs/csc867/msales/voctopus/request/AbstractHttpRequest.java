package edu.sfsu.cs.csc867.msales.voctopus.request;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import edu.sfsu.cs.csc867.msales.voctopus.RequestResponseMediator.ReasonPhrase;
import edu.sfsu.cs.csc867.msales.voctopus.request.handler.HttpRequestHandler;
import edu.sfsu.cs.csc867.msales.voctopus.request.handler.HttpRequestHandlerAbstractFactory;
/**
 * The abstract Http request holds all the information from the request.
 * 
 * @author marcello Feb 20, 2008 2:56:38 PM
 */
public abstract class AbstractHttpRequest implements HttpRequest {

    /**
     * This is the request method tokens constants accepted by the server
     * @author marcello
     * Feb 8, 2008 6:57:32 PM
     */
    public static enum RequestMethodType {
        GET, HEAD, POST, PUT, NOT_SUPPORTED
    }
    
    /**
     * The method type of the request.
     */
    private RequestMethodType methodType;
    /**
     * This is the version used on the request.
     */
    private String version;
    /**
     * This is the resource requested.
     */
    private URI uri;
    /**
     * Request header vars from the request.
     */
    private Map<String, String> headerVars;
    /**
     * The parameters used on the query string.
     */
    private Map<String, String> requestParameters;

    /**
     * The handler is based on the type of handler.
     */
    protected HttpRequestHandler requestHandler;

    /**
     * Constructs the abstract request based on the method type, uri, version, header variables and the handler
     * @param methodType
     * @param uri
     * @param version
     * @param headerVars
     */
    public AbstractHttpRequest(String methodType, URI uri, String version, Map<String, String> headerVars) {
        this.methodType = RequestMethodType.valueOf(methodType.toUpperCase());
        this.uri = uri;
        this.version = version;
        this.headerVars = headerVars;
        
        if (this.uri.getQuery() != null && !this.uri.getQuery().equals("")) {
            String[] varsAndValues = this.uri.getQuery().split("&");
            this.requestParameters = new HashMap<String, String>(varsAndValues.length);
            String[] vV;
            for (String varValue : varsAndValues) {
                vV = varValue.split("=");
                requestParameters.put(vV[0], vV[1]);
            };
        }
        
        this.requestHandler = HttpRequestHandlerAbstractFactory.getInstance().createRequestHandler(this);
    }
    
    /* (non-Javadoc)
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest#isResourceBinary()
     */
    public boolean isResourceBinary() {
        return this.requestHandler.isRequestedResourceBinary();
    }

    /* (non-Javadoc)
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest#getStatus()
     */
    public ReasonPhrase getStatus() {
        return this.requestHandler.getStatus();
    }
    
    /* (non-Javadoc)
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest#getContentType()
     */
    public String getContentType() {
        return this.requestHandler.getContentType();
    }
    
    /* (non-Javadoc)
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest#getResourceLines()
     */
    public String[] getResourceLines() {
        try {
            return this.requestHandler.getResourceLines();
        } catch (IOException e) {
            e.printStackTrace();
            return new String[] {""};
        }
    }

    /**
     * @return The method type used on the request.
     */
    public String getMethodType() {
        return this.methodType.toString();
    }

    /* (non-Javadoc)
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest#getUri()
     */
    public URI getUri() {
        return uri;
    }
    
    /**
     * @return The version of the request.
     */
    public String getRequestVersion() {
        return version;
    }
    
    /**
     * @param headerVar is one of the variables that come on the header from a request
     * @return the value of a given header variable
     */
    public String getHeaderValue(String headerVar) {
        return this.headerVars.get(headerVar);
    }

    /**
     * @return The request parameters and values from the query string
     */
    public Map<String, String> getRequestParameters() {
        return requestParameters;
    }
    
    /* (non-Javadoc)
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest#isRequestSuccessful()
     */
    public boolean isRequestSuccessful() {
        return this.requestHandler.requestedResourceExists();
    }
    
    /* (non-Javadoc)
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest#keepAlive()
     */
    public boolean keepAlive() {
        return false;
    }
    
    /* (non-Javadoc)
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest#getRequestHeaders()
     */
    public String[] getRequestHeaders() {
        String[] headers = new String[this.headerVars.size()];
        int i = -1;
        for (String key : this.headerVars.keySet()) {
            headers[++i] = key + ": " + this.headerVars.get(key);
        }
        return headers;
    }

    public HttpRequestHandler getRequestHandler() {
        return requestHandler;
    }
}