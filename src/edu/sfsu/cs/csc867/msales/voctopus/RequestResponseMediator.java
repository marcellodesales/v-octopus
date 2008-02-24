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
     * Creates a new mediator based on the connection from the client.
     * @param clientConnection is the connection from the client
     * @throws HttpRequestInterpreterException if the request 
     */
    public RequestResponseMediator(HttpClientConnection clientConnection) throws HttpRequestInterpreterException {
        this.clientConnection = clientConnection;
        try {
            this.request = new HttpRequestInterpreter(this.clientConnection.getConnectionLines()).interpret();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
        this.response.sendResponse(this.clientConnection.getOutputStream());
    }

}
