package edu.sfsu.cs.csc867.msales.httpd.validation;

/**
 * This exception occurs when the URI token is invalid. More information about this token can
 * be found on the RFC at w3.
 * @see http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5.1.2
 * @author marcello
 * Feb 8, 2008 7:29:28 PM
 */
public class InvalidHttpRequestURITokenException extends HttpRequestInterpreterException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs this exception with an embedded message, followed by the malformed token found.
	 * @param token is the malformed token found
	 */
	public InvalidHttpRequestURITokenException(String token) {
		super("The URI token is invalid", token);
	}
}
