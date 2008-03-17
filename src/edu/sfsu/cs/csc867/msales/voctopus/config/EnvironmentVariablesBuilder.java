package edu.sfsu.cs.csc867.msales.voctopus.config;

import java.net.InetAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import edu.sfsu.cs.csc867.msales.voctopus.request.AbstractHttpRequest;
import edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest;

/**
 * Environment variables for CGI scripts and Server-Side includes
 * 
 * @author marcello Mar 13, 2008 2:43:02 PM
 */
public final class EnvironmentVariablesBuilder {

    /**
     * General vars
     */
    private static Map<String, String> generalVars = new HashMap<String, String>();
    /**
     * Request-related vars
     */
    private Map<String, String> requestVars = new HashMap<String, String>();
    /**
     * Header vars
     */
    private Map<String, String> headerVars = new HashMap<String, String>();
    /**
     * The originating request to these variables
     */
    private HttpRequest request;
    
    /**
     * Builds this enumeration, having the general vars enabled.
     * @param request 
     */
    public EnvironmentVariablesBuilder(HttpRequest request) {
        this.request = request;
    }

    /**
     * @param mediator is the main mediator of the request/response.
     * @return all the environment variables
     */
    public Map<String, String> getAllEn() {
        if (generalVars.size() == 0) {
            buildGeneralVars();
        }
        this.buildRequestVars();
        this.buildRequestHeaderVars();
        Map<String, String> all = new HashMap<String, String>();
        all.putAll(generalVars);
        all.putAll(requestVars);
        all.putAll(headerVars);
        return all;
    }

    /**
     * Build the request vars from the request.
     * 
     * @param mediator is the mediator for the request
     */
    private void buildRequestHeaderVars() {
        String[] varValue;
        for (String requestHeaderVar : this.request.getRequestHeaders()) {
            varValue = requestHeaderVar.split(": ");
            headerVars.put("HTTP_" + varValue[0].toUpperCase().replace("-", "_"), varValue[1]);
        }
    }

    /**
     * Builds the general environment variables
     */
    private void buildGeneralVars() {
        // Non-request-specific environment variables are set for all requests:
        // , the name and version of the information server software answering the request
        generalVars.put("SERVER_SOFTWARE", VOctopusConfigurationManager.getSoftwareName());
        // server’s hostname, DNS alias, or IP address e.g. SERVER_NAME = nunki.usc.edu
        generalVars.put("SERVER_NAME", VOctopusConfigurationManager.WebServerProperties.HTTPD_CONF
                .getPropertyValue("ServerName"));
        // the revision of the CGI specification with which this server complies
        generalVars.put("GATEWAY_INTERFACE", VOctopusConfigurationManager.getCGIHandlersVersion());
        // TODO: GET THE VERSION OF THE PYTHON, PERL INSTALLED
    }

    /**
     * Builds the request parameters
     * 
     * @param mainRequest the main http request from the client.
     */
    private void buildRequestVars() {
        // These variables are set depending on each request
        AbstractHttpRequest request = (AbstractHttpRequest)this.request;
        URI uri = request.getUri();
        // the name and revision of the information protocol with which this request came in
        requestVars.put("SERVER_PROTOCOL", request.getRequestVersion().toString());
        // , the port number to which the request was sent
        requestVars.put("SERVER_PORT", VOctopusConfigurationManager.WebServerProperties.HTTPD_CONF
                .getPropertyValue("Listen"));
        // the method with which the request was made; e.g., (GET, POST)‏
        requestVars.put("REQUEST_METHOD", request.getMethodType().toString());
        // the extra path information as //given by the client; e.g., given
        // http://nunki.usc.edu:8080/cgi-bin/test-cgi/extra/path then PATH_INFO = /extra/path
        requestVars.put("PATH_INFO", uri.getPath());
        // the PATH_INFO path translated into an absolute document path on the local system
        // PATH_TRANSLATED = /auto/home-scf-03/csci351/WebServer/apache_1.2.5/htdocs/extra/path
        requestVars.put("PATH_TRANSLATED", VOctopusConfigurationManager.getInstance().getDocumentRootPath()
                + uri.getPath());
        // the path and name of the script being accessed as referenced in the URL SCRIPT_NAME = /cgi-bin/test-cgi
        requestVars.put("SCRIPT_NAME", uri.getPath().startsWith("/cgi-bin/") ? request.getUri().getPath() : "");
        // the information that follows the ? in the URL that referenced this script
        requestVars.put("QUERY_STRING", (uri.getQuery() != null) ? "?" + uri.getQuery() : "");
        // Internet domain name of the host making the request
        InetAddress clientAddress = request.getInetAddress();
        requestVars.put("REMOTE_HOST", clientAddress.getHostName());
        // the IP address of the remote host making the request
        requestVars.put("REMOTE_ADDR", clientAddress.getHostAddress());

        // System.out.println ("Host class : " + clientAddress.getAddress ());
        // int highByte = 0xff & clientAddress.getAddress()[0];
        // char ipClass = (highByte < 128) ? 'A' : (highByte < 192) ? 'B' :
        // (highByte < 224) ? 'C' : (highByte < 240) ? 'D' : 'E';

        // the authentication method required to authenticate a user who wants access
        requestVars.put("AUTH_TYPE", "");
        // user name that server and script have authenticated
        requestVars.put("REMOTE_USER", "");
        // the remote user name retrieved by the server using inetd identification (RFC 1413)‏
        requestVars.put("REMOTE_IDENT", "");
        
        String contentType = "", contentLength = "";
        for(String header : request.getRequestHeaders()) {
            if (header.startsWith("Content-Type: ")) {
                contentType = header.split(": ")[1];
            } else
            if (header.startsWith("Content-Length: ")) {
                contentLength = header.split(": ")[1];
            }
            if (!contentLength.equals("") && !contentType.equals("")) {
                break;
            }
        }
        
        // for queries that have attached information, such as POST method, this is the MIME content type of the data
        requestVars.put("CONTENT_TYPE", contentType);
        // the length of the content as given by the client
        requestVars.put("CONTENT_LENGTH", contentLength);
    }

    /**
     * @param request is the originating request.
     * @return Builds a new {@link EnvironmentVariablesBuilder} for the given request
     */
    public static EnvironmentVariablesBuilder createNew(HttpRequest request) {
        return new EnvironmentVariablesBuilder(request);
    }
}