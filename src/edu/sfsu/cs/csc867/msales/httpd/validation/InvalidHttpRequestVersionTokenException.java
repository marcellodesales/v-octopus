package edu.sfsu.cs.csc867.msales.httpd.validation;


/**
 * This exception is thrown when the version of the HTTP request differers from the one accepted
 * by this server.
 * @author marcello
 * Feb 8, 2008 7:31:37 PM
 */
public class InvalidHttpRequestVersionTokenException extends HttpRequestInterpreterException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new  instance of this exception with an embedded message, followed by the malformed
     * token.
     * @param token is the malformed token for the version
     */
    public InvalidHttpRequestVersionTokenException(String token) {
        super("The Http Request Version is incorrect.", token);
    }

}
