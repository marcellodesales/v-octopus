package com.googlecode.voctopus.request.handler;

/**
 * To control the flow of information from the execution of CGI. Usually is just to handle the
 * 500 internal server error.
 * @author marcello
 * Mar 5, 2008 2:48:54 AM
 */
public class CgiExecutionException extends Exception {

    /**
     * The version of this class
     */
    private static final long serialVersionUID = 1L;

    /**
     * Builds a new CgiExecutionException with the 
     * @param cause is the cause returned by the script execution.
     */
    public CgiExecutionException(String cause) {
        super(cause);
    }
}
