package edu.sfsu.cs.csc867.msales.voctopus.request;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import edu.sfsu.cs.csc867.msales.voctopus.RequestResponseMediator.ReasonPhase;
import edu.sfsu.cs.csc867.msales.voctopus.request.handler.HttpRequestHandler;
import edu.sfsu.cs.csc867.msales.voctopus.request.handler.HttpRequestHandlerAbstractFactory;

/**
 * The abstract Http request holds all the information from the request.
 * 
 * @author marcello Feb 20, 2008 2:56:38 PM
 */
/**
 * @author marcello
 * Mar 13, 2008 3:17:04 PM
 */
public abstract class AbstractHttpRequest implements HttpRequest {

    /**
     * This is the request method tokens constants accepted by the server
     * 
     * @author marcello Feb 8, 2008 6:57:32 PM
     */
    public static enum RequestMethodType {
        GET, HEAD, POST, PUT, NOT_SUPPORTED, OPTIONS, DELETE, TRACE, CONNECT;
        
        /**
         * @return if the method requested is implemented
         */
        public boolean isImplemented() {
            switch (this) {
                case OPTIONS:
                case DELETE:
                case TRACE:
                case CONNECT:
                    return false;
                default: return true;
            }
        }
    }
    
    /**
     * The request method used on the request method.
     */
    private RequestMethodType requestMethod;

    /**
     * The versions of the HTTP protocol that are accepted
     */
    public static enum RequestVersion {
        HTTP_1_1("HTTP/1.1"), HTTP_1_0("HTTP/1.0"), INVALID("INVALID_VERSION");

        private String versionString;

        /**
         * Constructs a new version 
         * @param versionToken
         */
        private RequestVersion(String versionToken) {
            this.versionString = versionToken;
        }
        
        /**
         * @param version is the version token used by the client. (Http/1.1)
         * @return an instance of a valid RequestVersion
         */
        public static RequestVersion getVersion(String version){
            for(RequestVersion versionAv : values()) {
                if (versionAv.toString().equalsIgnoreCase(version)) {
                    return versionAv;
                }
            }
            return INVALID;
        }

        @Override
        public String toString() {
            return this.versionString;
        };
        
        /**
         * @return is the version is valid
         */
        public boolean isValid() {
            return !this.equals(RequestVersion.INVALID);
        }
    }
    
    /**
     * The requested version of this request.
     */
    private RequestVersion version;

    /**
     * The method type of the request.
     */
    private RequestMethodType methodType;
    
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
     * The Inet address from the client
     */
    protected InetAddress clientInetAddress;
    
    /**
     * Additional data sent from a POST, PUT request methods.
     */
    private String additionalHeaderData;

    /**
     * Constructs the abstract request based on the method type, uri, version, header variables and the handler
     * 
     * @param methodType
     * @param uri
     * @param version
     * @param headerVars
     */
    public AbstractHttpRequest(InetAddress clientConnection, String methodType, URI uri, String version,
            Map<String, String> headerVars, String additionalHeaderData) {
        try {
            this.methodType = RequestMethodType.valueOf(methodType.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.methodType = RequestMethodType.NOT_SUPPORTED;
        }
        
        this.uri = uri;
        this.version = RequestVersion.getVersion(version);
        this.headerVars = headerVars;
        this.clientInetAddress = clientConnection;
        this.additionalHeaderData = additionalHeaderData;
        
        if (this.uri != null && this.uri.getQuery() != null && !this.uri.getQuery().equals("")) {
            String[] varsAndValues = this.uri.getQuery().split("&");
            this.requestParameters = new HashMap<String, String>(varsAndValues.length);
            String[] vV;
            for (String varValue : varsAndValues) {
                vV = varValue.split("=");
                if (vV.length == 2) {
                    requestParameters.put(vV[0], vV[1]);
                }
            }
        }
        this.requestHandler = HttpRequestHandlerAbstractFactory.getInstance().createRequestHandler(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest#isResourceBinary()
     */
    public boolean isResourceBinary() {
        return this.requestHandler.isRequestedResourceBinary();
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest#getStatus()
     */
    public ReasonPhase getStatus() {
        // code snippet added because of the nature of the calls (if the response has not been created yet)
        if (this.requestHandler == null) {
            return ReasonPhase.STATUS_200;
        } else {
            return this.requestHandler.getStatus();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest#getContentType()
     */
    public String getContentType() {
        if (this.requestHandler == null) {
            return "text/plain";
        }
        return this.requestHandler.getContentType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest#getResourceLines()
     */
    public String[] getResourceLines() {
        try {
            return this.requestHandler.getResourceLines();
        } catch (IOException e) {
            e.printStackTrace();
            return new String[] { "" };
        }
    }

    /**
     * @return The method type used on the request.
     */
    public RequestMethodType getMethodType() {
        return this.methodType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest#getUri()
     */
    public URI getUri() {
        return uri;
    }

    /**
     * @return The version of the request.
     */
    public RequestVersion getRequestVersion() {
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

    /*
     * (non-Javadoc)
     * 
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest#isRequestSuccessful()
     */
    public boolean isRequestSuccessful() {
        return this.requestHandler.requestedResourceExists();
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest#keepAlive()
     */
    public boolean keepAlive() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
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

    /*
     * (non-Javadoc)
     * 
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest#getRequestHandler()
     */
    public HttpRequestHandler getRequestHandler() {
        return requestHandler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest#getInetAddress()
     */
    public InetAddress getInetAddress() {
        return this.clientInetAddress;
    }

    /**
     * @return Data sent to the request that was encoded
     */
    public String getAdditionalHeaderData() {
        return this.additionalHeaderData; 
    }
}