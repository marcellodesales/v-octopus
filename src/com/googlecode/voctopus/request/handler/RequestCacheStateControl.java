package com.googlecode.voctopus.request.handler;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.googlecode.voctopus.RequestResponseMediator.ReasonPhase;
import com.googlecode.voctopus.config.VOctopusConfigurationManager.LogFormats;


public enum RequestCacheStateControl {
    
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
    IF_NONE_MATCH("If-None-Match"),
    
    /**
     * Explains the server that the request has some kind of properties.
     */
    CACHE_CONTROL("Cache-Control");
    
    
    /**
     * The header variable value that can come on a request header
     */
    private String headerValue;
    
    private RequestCacheStateControl(String headerValue) {
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
    public static ReasonPhase getRequestReasonPhase(File requestedFile, String[] requestHeaders) {
        String[] keyValue = null;
        ReasonPhase chosen = null;
        pars:
        for (String pair : requestHeaders) {
            keyValue = pair.split(": ");
            
            //Verify cache
            for(RequestCacheStateControl control : RequestCacheStateControl.values()) {
                
                if (keyValue[0].equalsIgnoreCase(control.toString())) {
                    switch (control) {
                        
                        case PRAGMA:
                            if (requestedFile.exists()) {
                                if (keyValue[1].equals("no-cache")) {
                                    chosen = ReasonPhase.STATUS_200;
                                } else {
                                    chosen = ReasonPhase.STATUS_304;
                                }
                                break pars;
                            } else {
                                chosen = ReasonPhase.STATUS_404;
                                break pars;
                            }
                        case IF_MODIFIED_SINCE:
                            if (requestedFile.isDirectory() ||  
                                    new Date(requestedFile.lastModified()).after(getDate(keyValue[1]))) {
                                chosen = ReasonPhase.STATUS_200;
                                break pars;
                            } else {
                                chosen = ReasonPhase.STATUS_304;
                                break pars;
                            }
                        case IF_NONE_MATCH:
                            continue pars;
                    }
                    break pars;
                }
            }
        }
        return chosen != null ? chosen : (requestedFile.exists() ? ReasonPhase.STATUS_200 : ReasonPhase.STATUS_404);
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