package edu.sfsu.cs.csc867.msales.voctopus.request.validation;

/**
 * This exception is thrown by any of the Interpreters. It contains the malformed token 
 * that was found during the lexical analyzes.
 * @author marcello
 * Feb 8, 2008 7:23:41 PM
 */
public class HttpRequestInterpreterException extends Exception {

    /**
     * Java5 required version id
     */
    private static final long serialVersionUID = 1L;

    /**
     * Token that caused the exception
     */
    private String token;

    /**
     * Exception cause when there's an exception during the validation of the HttpRequest protocol
     * @param message is the message configured by the children exception
     * @param token is the malformed token found on the request.
     */
    public HttpRequestInterpreterException(String message, String token) {
        super(message + " ('" + token + "')");
        this.token = token;
    }
    
    /**
     * @return the malformed tokend
     */
    public String getToken() {
        return this.token;
    }
}