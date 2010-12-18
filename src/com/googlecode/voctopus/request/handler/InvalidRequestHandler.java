package com.googlecode.voctopus.request.handler;

import com.googlecode.voctopus.RequestResponseMediator.ReasonPhase;

/**
 * Invalid request handler
 * @author marcello
 * Mar 14, 2008 11:59:48 PM
 */
public class InvalidRequestHandler extends EmptyBodyRequestHandler {

    public InvalidRequestHandler() {
        super(null, null, RequestType.INVALID, ReasonPhase.STATUS_400);
    }

    public String[] getParticularResponseHeaders() {
        // TODO Auto-generated method stub
        return null;
    }
}
