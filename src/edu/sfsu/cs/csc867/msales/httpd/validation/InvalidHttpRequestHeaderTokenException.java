package edu.sfsu.cs.csc867.msales.httpd.validation;

/**
 * This exception signalizes that a given variable on the header is invalid.
 * @author marcello
 * Feb 8, 2008 7:26:15 PM
 */
public class InvalidHttpRequestHeaderTokenException extends HttpRequestInterpreterException {

    /**
     * serial version
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates a new exception for the request header tokens.
     * @param token is the malformed token.
     */
    public InvalidHttpRequestHeaderTokenException(String token) {
        super("This HTTP request header variable is invalid", token);
    }
}
