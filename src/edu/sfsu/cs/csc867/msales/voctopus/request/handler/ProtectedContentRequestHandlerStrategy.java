package edu.sfsu.cs.csc867.msales.voctopus.request.handler;

import java.io.File;
import java.net.URI;

import edu.sfsu.cs.csc867.msales.voctopus.RequestResponseMediator.ReasonPhase;
import edu.sfsu.cs.csc867.msales.voctopus.config.Base64Decoder;
import edu.sfsu.cs.csc867.msales.voctopus.config.Base64FormatException;
import edu.sfsu.cs.csc867.msales.voctopus.config.CryptPassword;
import edu.sfsu.cs.csc867.msales.voctopus.config.DirectoryConfigHandler;

public class ProtectedContentRequestHandlerStrategy extends EmptyBodyRequestHandler {

    /**
     * The directory protection
     */
    private DirectoryConfigHandler protectedDirHandler;

    /**
     * The authorization header from the request
     */
    private String authorization;

    /**
     * Constructs a protected request handler with the information of the directory that is protected
     * 
     * @param uri is the requested URI
     * @param requestedFile the requested file on the system (it could be a request to a CGI, WS, etc)
     * @param dirHandler is the protected section that governs the request. Handler must return the correct information
     *            on the header.
     * @param authorization is the authorization encoded string from the request headers
     */
    public ProtectedContentRequestHandlerStrategy(URI uri, File requestedFile, DirectoryConfigHandler dirHandler,
            String authorization) {
        super(uri, requestedFile, RequestType.PROTECTED, ReasonPhase.STATUS_401);
        this.protectedDirHandler = dirHandler;
        this.contentType = "Content-Type: text/html; charset=iso-8859-1";
        this.authorization = authorization;
    }

    /**
     * @return the protected directory handler
     */
    public DirectoryConfigHandler getProtectedDirectoryHandler() {
        return this.protectedDirHandler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.handler.HttpRequestHandler#getParticularResponseHeaders()
     */
    public String[] getParticularResponseHeaders() {
        return new String[] { "WWW-Authenticate: Basic realm=\"" + this.getProtectedDirectoryHandler().getAuthName() 
                +"\"", "Connection: close", "Transfer-Encoding: chunked" };
    }

    /**
     * @return if the authorization that came from the request is valid.
     */
    public boolean isAuthorizationValid() {
        if (this.authorization == null) {
            return false;
        }
        try {
            String decodedAuthorization = new Base64Decoder(this.authorization).processString();
            String[] httpUsrPwd = decodedAuthorization.split(":");

            boolean found = false;
            String saltedPasswd = "";
            for (String allowedUser : this.protectedDirHandler.getHtpasswdUsersPasswords()) {
                String[] passwdUsrPwd = allowedUser.split(":");
                if (passwdUsrPwd[0].equals(httpUsrPwd[0])) {
                    found = true;
                    saltedPasswd = passwdUsrPwd[1];
                    break;
                }
            }

            if (found) {
                return CryptPassword.crypt(saltedPasswd, httpUsrPwd[1]).equals(saltedPasswd);
            } else {
                return false;
            }
        } catch (Base64FormatException e) {
            return false;
        }
    }

    /**
     * @return if the request contained an authorization value
     */
    public boolean containsAuthorization() {
        return this.authorization != null && !this.authorization.equals("");
    }
}