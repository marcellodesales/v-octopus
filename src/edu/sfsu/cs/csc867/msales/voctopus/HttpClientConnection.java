package edu.sfsu.cs.csc867.msales.voctopus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The Http Client Connection is the representation of the connection with the client.
 * @author marcello
 * Feb 19, 2008 6:55:30 PM
 */
public class HttpClientConnection {

    /**
     * This is the real socket connection with the client, which is managed here.
     */
    private Socket clientConnection;
    
    /**
     * Constructs a new Client connection based on the socket connection opened by the web server.
     * @param clientConnection is the socket connection with the client.
     */
    public HttpClientConnection(Socket clientConnection) {
        this.clientConnection = clientConnection;
    }
    
    /**
     * Parse the incoming request depending on the type of request you are receiving. This
     * information is found from the first line of the incoming request. You will also want to check
     * and make sure the request you are receiving is a valid request. If the request is not valid,
     * throw an error using the http error codes.
     * 
     * @param inMsg BufferedReader which grabs the incoming message from the client socket
     * @throws IOException thrown from reading the buffered reader
     */
    public String[] getConnectionLines() throws IOException {

        BufferedReader inMsg = new BufferedReader(new InputStreamReader(this.clientConnection.getInputStream()));
        Map<String, String> lines = new LinkedHashMap<String, String>();
        List<String> requestLines = new ArrayList<String>();
        String requestLine = null;
        while ((requestLine = inMsg.readLine()) != null) {
            if (requestLine.equals("") || requestLine.equals(" ")) {
                break;
            }
            requestLines.add(requestLine);
        }
        //inMsg.close();
        return requestLines.toArray(new String[lines.size()]);
    }
    
    /**
     * @return the output stream from the connection.
     * @throws IOException if any problem occurs getting the output stream.
     */
    public OutputStream getOutputStream() {
        try {
            return this.clientConnection.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
