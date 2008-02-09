package edu.sfsu.cs.csc867.msales.httpd.validation;

/**
 * This exceptions signalizes the method token is not supported by this server.
 * @author marcello
 * Feb 8, 2008 7:27:23 PM
 */
public class InvalidHttpRequestMethodTokenException extends HttpRequestInterpreterException {

	/**
	 * serial number
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an instance of this method using the malformed token found, with an embedded message.
	 * @param token is the malformed token found.
	 */
	public InvalidHttpRequestMethodTokenException(String token) {
		super("The HttpRequest method specified is invalid.", token);
	}
}
