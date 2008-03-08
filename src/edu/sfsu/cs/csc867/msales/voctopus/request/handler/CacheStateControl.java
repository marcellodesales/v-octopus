package edu.sfsu.cs.csc867.msales.voctopus.request.handler;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.sfsu.cs.csc867.msales.voctopus.RequestResponseMediator.ReasonPhrase;
import edu.sfsu.cs.csc867.msales.voctopus.config.VOctopusConfigurationManager.LogFormats;
import edu.sfsu.cs.csc867.msales.voctopus.request.AbstractHttpRequest;

public enum CacheStateControl {
    
    /**
     * The client is unwilling to accept any cached responses from caches along the 
     * route and the origin server must be contacted for a fresh copy of the resource.
     */
    PRAGMA("Pragma"),
    /**
     * The server should return the requested resource only if the resource has been 
     * modified since the date-time provided by the client.
     */
    IF_MODIFIED_SINCE("If-Modified-Since"),
    /**
     * The server should return the requested resource if the ETAG of the resource is 
     * different than the value provided by the client. An  ETAG is a unique identifier 
     * representing a particular version of a file.
     */
    IF_NONE_MATCH("If-None-Match");
    
    /**
     * The header variable value that can come on a request header
     */
    private String headerValue;
    
    private CacheStateControl(String headerValue) {
        this.headerValue = headerValue;
    }
    
    @Override
    public String toString() {
        return this.headerValue;
    }
    
    /**
     * Returns the reason phase based on the request. 
     * @param request is the Http Request from the client
     * @return the Reason Phase code for the request. It may later change in case of 
     * execution of scripts, web services, etc. It will also be used for other method
     * requests such as HEAD, PUT, etc...
     */
    public static ReasonPhrase getRequestReasonPhase(File requestedFile, String[] requestHeaders) {
        String[] keyValue = null;
        ReasonPhrase chosen = null;
        pars:
        for (String pair : requestHeaders) {
            keyValue = pair.split(": ");
            
            //Verify cache
            for(CacheStateControl control : CacheStateControl.values()) {
                
                if (keyValue[0].equalsIgnoreCase(control.toString())) {
                    switch (control) {
                        case PRAGMA:
                            if (requestedFile.exists()) {
                                chosen = ReasonPhrase.STATUS_200;
                                break pars;
                            } else {
                                chosen = ReasonPhrase.STATUS_404;
                                break pars;
                            }
                        case IF_MODIFIED_SINCE:
                            if (new Date(requestedFile.lastModified()).after(getDate(keyValue[1]))) {
                                chosen = ReasonPhrase.STATUS_200;
                                break pars;
                            } else {
                                chosen = ReasonPhrase.STATUS_304;
                                break pars;
                            }
                        case IF_NONE_MATCH:
                            continue pars;
                    }
                    break pars;
                }
            }
        }
        return chosen != null ? chosen : (requestedFile.exists() ? ReasonPhrase.STATUS_200 : ReasonPhrase.STATUS_404);
    }
    
    private static Date getDate(String headerValue) {
        
        SimpleDateFormat df = new SimpleDateFormat(LogFormats.HEADER_DATE_TIME.toString());
        try {
            return df.parse(headerValue);
        } catch (ParseException e) {
            return null;
        }
    }
}