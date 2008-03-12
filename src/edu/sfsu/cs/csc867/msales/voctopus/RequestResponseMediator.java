package edu.sfsu.cs.csc867.msales.voctopus;

import java.io.IOException;

import edu.sfsu.cs.csc867.msales.httpd.validation.HttpRequestInterpreterException;
import edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest;
import edu.sfsu.cs.csc867.msales.voctopus.request.validation.HttpRequestInterpreter;
import edu.sfsu.cs.csc867.msales.voctopus.response.HttpResponse;
import edu.sfsu.cs.csc867.msales.voctopus.response.HttpResponseAbstractFactory;

/**
 * The Request-Response Mediator is responsible for mediating the transactions between the Request and Response
 * instances.
 * 
 * @author marcello Feb 16, 2008 10:26:58 AM
 */
public final class RequestResponseMediator {

    /**
     * Holds the connection from the client on the socket level.
     */
    private HttpClientConnection clientConnection;

    /**
     * It's the interface from the client's request.
     */
    private HttpRequest request;

    /**
     * It's the response based on the request's result.
     */
    private HttpResponse response;

    /**
     * This represents the phases of the request/response. More information about it at
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html Compact infor about each of them
     * http://256.com/gray/docs/http_codes.html The first digit of the Status-Code defines the class of response. The
     * last two digits do not have any categorization role. There are 5 values for the first digit: <BR>
     * <li>1xx: Informational - Request received, continuing process
     * <li>2xx: Success - The action was successfully received, understood, and accepted
     * <li>3xx: Redirection - Further action must be taken in order to complete the request
     * <li>4xx: Client Error - The request contains bad syntax or cannot be fulfilled
     * <li>5xx: Server Error - The server failed to fulfill an apparently valid request
     * 
     * @author marcello Feb 26, 2008 12:05:05 PM
     */
    public static enum ReasonPhrase {
        STATUS_100("Continue"), STATUS_101("Switching Protocols"),
        /**
         * The request has been fulfilled and an entity corresponding to the requested resource is being sent in the
         * response. If the HEAD method was used, the response should only contain the Entity-Header information and no
         * Entity-Body.
         */
        STATUS_200("OK"), STATUS_201("Created"), STATUS_202("Accepted"), STATUS_203("Non-Authoritative Information"),
        /**
         * The server has fulfilled the request but there is no new information to send back. If the client is a user
         * agent, it should not change its document view. This response is primarily intended to allow input for scripts
         * or other actions to take place without causing a change to the user agent's current document view.
         */
        STATUS_204("No Content"), STATUS_205("Reset Content"), STATUS_206("Partial Content"), STATUS_300(
                "Multiple Choices"), STATUS_301("Moved Permanently"), STATUS_302("Found"), STATUS_303("See Other"),
        /**
         * If the client has performed a conditional GET request and access is allowed, but the document has not been
         * modified since the date and time specified in the If-Modified-Since field, the server shall respond with this
         * status code and not send an Entity-Body to the client. Header fields contained in the response should only
         * include information which is relevant to cache managers and which may have changed independently of the
         * entity's Last-Modified date. Examples of relevant header fields include: Date, Server, and Expires.
         */
        STATUS_304("Not Modified"), STATUS_305("Use Proxy"), STATUS_307("Temporary Redirect"), STATUS_400("Bad Request"),
        /**
         * The request requires user authentication. The response must include a WWW-Authenticate header field
         * containing a challenge applicable to the requested resource. The client may repeat the request with a
         * suitable Authorization header field.
         */
        STATUS_401("Unauthorized"), STATUS_402("Payment Required"),
        /**
         * The request is forbidden because of some reason that remains unknown to the client. Authorization will not
         * help and the request should not be repeated. This status code can be used if the server does not want to make
         * public why the request cannot be fulfilled. <BR>
         * <BR>
         * In the context of this webserver, this error will be issued if the user who initiated the server doesn't have
         * access permissions to it.
         */
        STATUS_403("Forbidden"),
        /**
         * The server has not found anything matching the Request-URI. No indication is given of whether the condition
         * is temporary or permanent. If the server does not wish to make this information available to the client, the
         * status code "403 Forbidden" can be used instead. The "410 Gone" status code should be used if the server
         * knows (through some internally configurable method) that an old resource is permanently unavailable and has
         * no forwarding address.
         */
        STATUS_404("Not Found"),
        /**
         * The method specified in the Request-Line is not allowed for the resource identified by the Request-URI. The
         * response must include an Allow header containing a list of valid method's for the requested resource.
         */
        STATUS_405("Method Not Allowed"), STATUS_406("Not Acceptable"), STATUS_407("Proxy Authentication Required"), STATUS_408(
                "Request Time-out"), STATUS_409("Conflict"), STATUS_410("Gone"), STATUS_411("Length Required"), STATUS_412(
                "Precondition Failed"), STATUS_413("Request Entity Too Large"), STATUS_414("Request-URI Too Large"), STATUS_415(
                "Unsupported Media Type"),
        /**
         * The server encountered an unexpected condition which prevented it from fulfilling the request.
         */
        STATUS_500("Internal Server Error"), STATUS_501("Not Implemented"), STATUS_502("Bad Gateway"), STATUS_503(
                "Service Unavailable"), STATUS_504("Gateway Time-out"),
        /**
         * 
         */
        STATUS_505("HTTP Version not supported");

        /**
         * The title of each error message
         */
        private String humanValue;

        private ReasonPhrase(String humanValue) {
            this.humanValue = humanValue;
        }

        @Override
        public String toString() {
            return this.getCode() + " " + this.humanValue;
        }

        /**
         * @param code the code to be generated.
         * @return a new instance of ReasonPhase based on the code
         */
        public static ReasonPhrase getReasonPhrase(int code) {
            for (ReasonPhrase key : ReasonPhrase.values()) {
                if (key.toString().contains(String.valueOf(code))) {
                    return key;
                }
            }
            return STATUS_406;
        }

        public int getCode() {
            return Integer.valueOf(this.name().substring(this.name().indexOf("_") + 1));
        }
    }

    /**
     * Creates a new mediator based on the connection from the client.
     * 
     * @param clientConnection is the connection from the client
     * @throws HttpRequestInterpreterException if the request
     */
    public RequestResponseMediator(HttpClientConnection clientConnection) throws HttpRequestInterpreterException {
        this.clientConnection = clientConnection;
        try {
            this.request = new HttpRequestInterpreter(this.clientConnection.getConnectionLines()).interpret();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        System.out.println("Processing request for " + this.request.getUri());
        this.response = HttpResponseAbstractFactory.getInstance().createNewHttpResponse(this.request);
    }

    /**
     * @return The request from the client.
     */
    public HttpRequest getRequest() {
        return request;
    }

    /**
     * @return The response based on the request.
     */
    public HttpResponse getResponse() {
        return response;
    }

    /**
     * Signals the response to be sent to the client.
     */
    public void sendResponse() {
        try {
            this.response.sendResponse(this.clientConnection.getOutputStream());
        } catch (IOException e) {
            // TODO LOG THIS INFORMATION.
            e.printStackTrace();
        }
    }
}
