package com.googlecode.voctopus;

/**
 * This exception occurs when anything wrong happens on the Web Server, from an I/O error
 * or other problems related to the protocol.
 * @author marcello
 * Feb 8, 2008 7:34:01 PM
 */
public class HttpErrorException extends Exception {

        /**
         * This is a regular exception from the Http Protocol. It can be an I/O error, or any
         * other type of problem related to the protocol.
         * @param message is the message to be used.
         * @param throwable is the root cause of this exception to be raised.
         */
        private HttpErrorException(String message, Throwable throwable) {
                super(message, throwable);
        }

        public static HttpErrorException buildNewException(Throwable throwable) {
            return new HttpErrorException(throwable.getMessage(), throwable);
        }

}
