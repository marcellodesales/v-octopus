package edu.sfsu.cs.csc867.msales.voctopus.request.validation;


public class HttpRequestHeaderFieldVarExpression extends HttpRequestNonTerminalExpression {

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
        CONTENT_TYPE("Content-Type"),
        CONTENT_LENGTH("Content-Length"),
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
    
    public HttpRequestHeaderFieldVarExpression(HttpRequestInterpreterContext context,
            AbstractHttpRequestExpression next, String token) {
        super(context, next, token);
    }

    @Override
    protected void validate() throws HttpRequestInterpreterException {
        
    }
}
