package com.googlecode.voctopus.response;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.googlecode.voctopus.RequestResponseMediator.ReasonPhase;


/**
 * Represents the response to the client.
 * @author marcello
 * Feb 16, 2008 10:31:02 AM
 */
public interface HttpResponse {

    /**
     * @return The lines for the response to be on the body section of the Http Response message.
     */
    public String[] getResponseBody();
    /**
     * @return The lines to be on the on the header section of the Http Response message
     */
    public String[] getResponseHeader();
    /**
     * @return The Reason phase code for the response. 
     */
    public ReasonPhase getStatusCode();
    /**
     * Sends the response back to the client through its outputstream.
     * @param outputStream is the client's output stream
     * @throws IOException if any problem related to the Socket pipe occurs.
     */
    public void sendResponse(OutputStream outputStream) throws IOException;
    /**
     * Sends the body to the client.
     * @param outputStream the client's output stream
     * @param writer is the writer, that can be anything from a terminal connector to a socket.
     * @throws IOException is any problem occurs with the connection
     */
    public void sendBody(OutputStream outputStream, PrintWriter writer) throws IOException;
    /**
     * Sends the header to the client.
     * @param writer is the writer that only writes characters.
     * @throws IOException in case any problem with the pipe of the output stream happens.
     */
    public void sendHeader(PrintWriter writer) throws IOException;
}
