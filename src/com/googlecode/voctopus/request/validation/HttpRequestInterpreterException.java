package com.googlecode.voctopus.request.validation;

/**
 * This exception is thrown by any of the Interpreters. It contains the malformed token that was found during the
 * lexical analyzes.
 * 
 * @author marcello Feb 8, 2008 7:23:41 PM
 */
public class HttpRequestInterpreterException extends Exception {

    /**
     * Java5 required version id
     */
    private static final long serialVersionUID = 1L;

    /**
     * Token that caused the exception
     */
    private ErrorToken token;

    /**
     * Used to verify the errors sent by the validation mechanism
     * @author marcello
     * Mar 16, 2008 3:22:08 AM
     */
    public static enum ErrorToken {
        /**
         * If the method type has a malformed method type
         */
        METHOD_TYPE,
        /**
         * If the URI has an invalid URI
         */
        URI_TYPE,
        /**
         * If the version type is not valid
         */
        VERSION_TYPE;

        /**
         * Mulformed token used on the connection.
         */
        private String token;

        /**
         * Sets the token value for this ErrorToken
         * 
         * @param usedToken
         */
        public ErrorToken setToken(String usedToken) {
            this.token = usedToken;
            return this;
        }
        
        /**
         * @return the token used 
         */
        public String getTokenUsed(){
            return this.token;
        }

        /**
         * @param tokenUsed
         * @return The method type of this request.
         */
        public static ErrorToken generateMethodToken(String tokenUsed) {
            return METHOD_TYPE.setToken(tokenUsed);
        }

        /**
         * @param tokenUsed
         * @return The generated token for the version
         */
        public static ErrorToken generateURIToken(String tokenUsed) {
            return URI_TYPE.setToken(tokenUsed);
        }

        /**
         * @param tokenUsed is the version token used on the request
         * @return The generated version error token with the specified token
         */
        public static ErrorToken generateVersionToken(String tokenUsed) {
            return VERSION_TYPE.setToken(tokenUsed);
        }
    }

    /**
     * Exception cause when there's an exception during the validation of the HttpRequest protocol
     * 
     * @param message is the message configured by the children exception
     * @param token is the malformed token found on the request.
     */
    public HttpRequestInterpreterException(String message, ErrorToken token) {
        super(message + " ('" + token.getTokenUsed() + "')");
        this.token = token;
    }

    /**
     * @return the malformed token
     */
    public ErrorToken getToken() {
        return this.token;
    }
}