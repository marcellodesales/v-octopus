package edu.sfsu.cs.csc867.msales.httpd.validation;

/**
 * This Interpreter implements the Go4 Design-Pattern Interpreter. It analyzes the tokens 
 * from a given String based on the properties of its tokens. The verify method is called
 * interpret and must be executed to assure that the entry is valid.
 * 
 * @author marcello
 * Feb 8, 2008 6:56:39 PM
 */
public final class HttpRequestFirstLineInterpreter {

    /**
     * Ths default version of the HTTP protocol that is accepted
     */
    private static final String DEFAILT_PROTOCOL_VERSION = "HTTP/1.1";

    /**
     * The first line of the request with the protocol URI and protocol version
     */
    private String firstLine;

    /**
     * This is the request method tokens constants accepted by the server
     * @author marcello
     * Feb 8, 2008 6:57:32 PM
     */
    public enum REQUEST_METHOD_TOKENS {
        OPTIONS, GET, HEAD, POST, DELETE
    }

    /**
     * This constructs the interpreter with the first line to be analyzed.
     * @param firstLine is the first line of a given HTTP request
     * @throws HttpRequestInterpreterException in case one of the tokens from the request is invalid.
     */
    private HttpRequestFirstLineInterpreter(String firstLine) throws HttpRequestInterpreterException {
        this.firstLine = firstLine;
    }
    
    /**
     * Factory method to create a new Interpreter. Subsequent call {@link HttpRequestFirstLineInterpreter#interpret()}
     * is needed to perform the analyzes.
     * @param firstLine is the first line of an HTTP request
     * @return a new Interpreter
     * @throws HttpRequestInterpreterException is thrown in case one of the tokens from the request is invalid.
     */
    public static HttpRequestFirstLineInterpreter createNewInterpreter(String firstLine) throws HttpRequestInterpreterException {
        return new HttpRequestFirstLineInterpreter(firstLine);
    }

    /**
     * Interprets the first line of the HTTP Request from the client.
     * 
     * @throws HttpRequestInterpreterException if problem is encountered, an specialized exception is
     *             thrown.
     */
    public void interpret() throws HttpRequestInterpreterException {
        String[] tokens = this.firstLine.trim().split(" ");
        System.out.println("First line from the request: " + this.firstLine);
        this.isRequestMethodValid(tokens[0]);
        this.isRequestURIValid(tokens[1]);
        this.isRequestVersionValid(tokens[2]);
    }

    /**
     * Verifies if the version of the HTTP request is valid.
     * @param requestVersion is the version of the HTTP request
     * @throws InvalidHttpRequestVersionTokenException in case it is a version that differs from the constant.
     */
    private void isRequestVersionValid(String requestVersion)
            throws InvalidHttpRequestVersionTokenException {
        if (requestVersion == null || requestVersion.equals("")
                || !requestVersion.equals(DEFAILT_PROTOCOL_VERSION)) {
            throw new InvalidHttpRequestVersionTokenException(requestVersion);
        }
    }

    /**
     * Verifies if the request URI string is valid. It follows the specification from 
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5.1.2
     * @param requestURI is the request URL from the request.
     * @throws InvalidHttpRequestURITokenException in case it does not conform with the specification.
     */
    private void isRequestURIValid(String requestURI)
            throws InvalidHttpRequestURITokenException {
        if (requestURI == null || requestURI.trim().equals("")
                || !requestURI.startsWith("/")) {
            throw new InvalidHttpRequestURITokenException(requestURI);
        }
    }

    /**
     * Verifies if the request method is from one of the accepted subset from this server. The complete list of request
     * methods can be found at http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5.1.1
     * @param methodType is the request method as described at 
     * @throws InvalidHttpRequestMethodTokenException
     */
    private void isRequestMethodValid(String methodType)
            throws InvalidHttpRequestMethodTokenException {
        if (REQUEST_METHOD_TOKENS.valueOf(methodType.toUpperCase()) == null) {
            throw new InvalidHttpRequestMethodTokenException(methodType);
        }
    }
}