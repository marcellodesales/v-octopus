package com.googlecode.voctopus.request.handler;

import java.io.File;
import java.net.URI;

import com.googlecode.voctopus.RequestResponseMediator.ReasonPhase;
import com.googlecode.voctopus.config.Base64Decoder;
import com.googlecode.voctopus.config.Base64FormatException;
import com.googlecode.voctopus.config.CryptPassword;
import com.googlecode.voctopus.config.DirectoryConfigHandler;
import com.googlecode.voctopus.config.DirectoryConfigHandler.AuthType;


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
        if (this.protectedDirHandler.getAuthType().equals(AuthType.BASIC)) {
            return new String[] {
                    "WWW-Authenticate: " + this.protectedDirHandler.getAuthType().toString() + " realm=\""
                            + this.getProtectedDirectoryHandler().getAuthName() + "\"", "Connection: close",
                    "Transfer-Encoding: chunked" };
        } else {
            return new String[] {
                    "WWW-Authenticate: " + this.protectedDirHandler.getAuthType().toString() + " realm=\""
                            + "voctopus" + "\"", "Connection: close",
                    "Transfer-Encoding: chunked" };

        }
    }

    /**
     * @return if the authorization that came from the request is valid.
     */
    public boolean isAuthorizationValid() {
        if (this.authorization == null) {
            return false;
        }
        String decodedAuthorization = "";
        try {
            if (this.protectedDirHandler.getAuthType().equals(AuthType.BASIC)) {
                decodedAuthorization  = new Base64Decoder(this.authorization).processString();
            
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
            
            } else {
                //username="marcello", realm="Selected Users MD5-secured Directories", nonce="", uri="/", 
                //response="6215ff0f8b0195bfdff0fff68160e29f"

                String[] httpUsrPwd = this.authorization.replace("\"", "").split(", ");
                
                String[] passwdUsrPwd = new String[3];
                for (String allowedUser : this.protectedDirHandler.getHtpasswdUsersPasswords()) {
                    passwdUsrPwd = allowedUser.split(":");
                    if (httpUsrPwd[0].split("=")[0].equals(passwdUsrPwd[0]) //username:realm:md5passw
                            && httpUsrPwd[1].split("=")[1].equals(httpUsrPwd[1])) {
                        break;
                    }
                }
                return CryptPassword.isMD5DigestValid(passwdUsrPwd[0], passwdUsrPwd[1], passwdUsrPwd[2]);
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