package edu.sfsu.cs.csc867.msales.voctopus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * The Http Client Connection is the representation of the connection with the client.
 * 
 * @author marcello Feb 19, 2008 6:55:30 PM
 */
public class HttpClientConnection {

    /**
     * This is the real socket connection with the client, which is managed here.
     */
    private Socket clientConnection;

    /**
     * Constructs a new Client connection based on the socket connection opened by the web server.
     * 
     * @param clientConnection is the socket connection with the client.
     */
    public HttpClientConnection(Socket clientConnection) {
        this.clientConnection = clientConnection;
    }

    /**
     * Parse the incoming request depending on the type of request you are receiving. This information is found from the
     * first line of the incoming request. You will also want to check and make sure the request you are receiving is a
     * valid request. If the request is not valid, throw an error using the http error codes.
     * 
     * @param inMsg BufferedReader which grabs the incoming message from the client socket
     * @throws IOException thrown from reading the buffered reader
     */
    public String[] getConnectionLines() throws IOException {

        return verifyConnection(this.clientConnection);
    }

    /**
     * @return the output stream from the connection.
     * @throws IOException if any problem occurs getting the output stream.
     */
    public OutputStream getOutputStream() throws IOException {
        return this.clientConnection.getOutputStream();
    }

    public Socket getClientConnection() {
        return clientConnection;
    }

    private String[] verifyConnection(Socket clientConnection2) throws IOException {
        List<String> requestLines = new ArrayList<String>();

        InputStream is = clientConnection2.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(is));

        // Read the request line
        StringTokenizer st = new StringTokenizer(in.readLine());
        if (!st.hasMoreTokens()) {
        }
        String method = st.nextToken();

        if (!st.hasMoreTokens()) {
        }
        String uri = decodePercent(st.nextToken());

        if (!st.hasMoreTokens()) {
        }
        String version = st.nextToken();

        // Decode parameters from the URI
        Properties parms = new Properties();
        int qmi = uri.indexOf('?');
        if (qmi >= 0) {
            decodeParms(uri.substring(qmi + 1), parms);
            uri = decodePercent(uri.substring(0, qmi));
        }
        String params = "";
        if (parms.size() > 0) {
            params = "?";
            for (Object key : parms.keySet()) {
                params = params + key + "=" + parms.getProperty(((String)key)) + "&";
            }
        }
        
        requestLines.add(method + " " + uri + params + " " + version);

        // If there's another token, it's protocol version,
        // followed by HTTP headers. Ignore version but parse headers.
        // NOTE: this now forces header names uppercase since they are
        // case insensitive and vary by client.
        Properties header = new Properties();
        // if ( st.hasMoreTokens())
        // {
        String line;
        while ((line = in.readLine()).trim().length() > 0) {
            requestLines.add(line);
            int p = line.indexOf(':');
            header.put(line.substring(0, p).trim().toLowerCase(), line.substring(p + 1).trim());
        }
        // }
        // requestLines.add(line);
        // If the method is POST, there may be parameters
        // in data section, too, read it:
        if (method.equalsIgnoreCase("POST")) {
            long size = 0x7FFFFFFFFFFFFFFFl;
            String contentLengthx = header.getProperty("content-length");
            if (contentLengthx != null) {
                try {
                    size = Integer.parseInt(contentLengthx);
                } catch (NumberFormatException ex) {
                }
            }
            String postLine = "";
            char buf[] = new char[512];
            int read = in.read(buf);
            while (read >= 0 && size > 0 && !postLine.endsWith("\r\n")) {
                size -= read;
                postLine += String.valueOf(buf, 0, read);
                if (size > 0) {
                    read = in.read(buf);
                }
            }
            postLine = postLine.trim();
            requestLines.add(postLine);
            decodeParms(postLine, parms);
        }
        return requestLines.toArray(new String[requestLines.size()]);
    }

    /**
     * Decodes the percent encoding scheme. <br/> For example: "an+example%20string" -> "an example string"
     */
    private static String decodePercent(String str) {
        try {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                switch (c) {
                case '+':
                    sb.append(' ');
                    break;
                case '%':
                    sb.append((char) Integer.parseInt(str.substring(i + 1, i + 3), 16));
                    i += 2;
                    break;
                default:
                    sb.append(c);
                    break;
                }
            }
            return new String(sb.toString().getBytes());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Decodes parameters in percent-encoded URI-format ( e.g. "name=Jack%20Daniels&pass=Single%20Malt" ) and adds them
     * to given Properties.
     */
    private static List<String> decodeParms(String parms, Properties p) {
        if (parms == null)
            return new ArrayList<String>();

        StringTokenizer st = new StringTokenizer(parms, "&");
        List<String> decoded = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String e = st.nextToken();
            int sep = e.indexOf('=');
            if (sep >= 0) {
                String var = decodePercent(e.substring(0, sep)).trim();
                String value = decodePercent(e.substring(sep + 1));
                p.put(var, value);
                decoded.add(var + ": " + value);
            }
        }
        return decoded;
    }
}
