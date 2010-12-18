package com.googlecode.voctopus.config;

/**
 * Exception related to the decoder
 * 
 * @author marcello Mar 12, 2008 2:31:36 AM
 */
public class Base64FormatException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create that kind of exception
     * 
     * @param msg The associated error message
     */

    public Base64FormatException(String msg) {
        super(msg);
    }

}
