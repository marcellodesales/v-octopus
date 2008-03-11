package edu.sfsu.cs.csc867.msales.httpd.validation;

import java.util.Set;

/**
 * This interpreter validates the HTTP header variables that come with a request. It must
 * validate all the variables that are available on HTTP 1.1 version, as well as support 
 * backwards compatibility with HTTP/1.0, since some browsers still sends some of them.
 * @see http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5.3
 * @author marcello
 * Feb 8, 2008 7:08:45 PM
 */
public final class HttpRequestHeaderFieldsInterpreter {

    /**
     * These are the tokens that represents the header variables.
     * @author marcello
     * Feb 8, 2008 7:10:24 PM
     */
    private enum RequestHeaderVarTokens {

        ACCEPT_TOKEN("Accept"),
        ACCEPT_CHARSET_TOKEN("Accept-Charset"),
        ACCEPT_ENCODING_TOKEN("Accept-Encoding"),
        ACCEPT_LANGUAGE_TOKEN("Accept-Language"),
        AUTHORIZATION_TOKEN("Authorization"),
        EXPECT_TOKEN("Expect"),
        FROM_TOKEN("From"),
        HOST_TOKEN("Host"),
        IF_MATCH_TOKEN("If-Match"),
        IF_MODIFIED_SINCE_TOKEN("If-Modified-Since"),
        IF_NONE_MATCH_TOKEN("If-None-Match"),
        IF_RANGE_TOKEN("If-Range"),
        IF_UNMODIFIED_SINCE_TOKEN("If-Unmodified-Since"),
        MAX_FORWARDS_TOKEN("Max-Forwards"),
        PROXY_AUTHORIZATION_TOKEN("Proxy-Authorization"),
        RANGE_TOKEN("Range"),
        REFERER_TOKEN("Referer"),
        TE_TOKEN("TE"),
        USER_AGENT_TOKEN("User-Agent"),
        /**
         * This token can have values from HTTP/1.0 to maintain backwards compatibility.
         */
        CONNECTION_TOKEN("Connection"),
        /**
         * This token refers to an HTTP/1.0 version var to make servers support persistent connections
         * @see http://www.oreilly.com/openbook/webclient/appa.html
         */
        KEEP_ALIVE_TOKEN("Keep-Alive"), 
        /**
         * Cache-control was sent by repeated requests using the Eclipse Internet Browser
         * @SEE http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9
         */
        CACHE_CONTROL("Cache-Control");

        private String tokenString;
        
        private RequestHeaderVarTokens(String tokenString) {
            this.tokenString = tokenString;
        }
        
        @Override
        public String toString() {
            return this.tokenString;
        }
    }
    
    private Set<String> varList;
    
    /**
     * Creates a new Interpreter with the set of vars
     * @param varsList is the set of the vars from the HTTP request.
     */
    private HttpRequestHeaderFieldsInterpreter(Set<String> varsList) {
        this.varList = varsList;
    }
    
    /**
     * Goes through the header vars and verify if each of them are valid conforming to the
     * specification described at the RFC.
     * @see http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5.3
     * @throws HttpRequestInterpreterException if at least one of the vars sent are not valid
     * 
     */
    public void interpret() throws HttpRequestInterpreterException {
        
        for (String var : this.varList) {
            boolean foundIt = false;
            for (RequestHeaderVarTokens token : RequestHeaderVarTokens.values()) {
                if (token.toString().equals(var)) {
                    foundIt = true;
                    break;
                } else {
                    continue;
                }
            }
            if (!foundIt) {
                throw new InvalidHttpRequestHeaderTokenException(var);
            }
        }
    }
    
    /**
     * Creates a new HttpRequestHeader interpreter in order to verify if the header
     * vars sent by the client are valid.
     * @param varsList the set with all the header vars sent by the client
     * @return a new Instance of this interpreter.
     */
    public static HttpRequestHeaderFieldsInterpreter createNewInterpreter(Set<String> varsList) {
        return new HttpRequestHeaderFieldsInterpreter(varsList);
    }
}