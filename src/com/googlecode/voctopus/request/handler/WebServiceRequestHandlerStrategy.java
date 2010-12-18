package com.googlecode.voctopus.request.handler;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import com.googlecode.voctopus.RequestResponseMediator.ReasonPhase;


/**
 * @author marcello Feb 24, 2008 8:36:39 AM
 */
public class WebServiceRequestHandlerStrategy extends AbstractRequestHandler {

    public WebServiceRequestHandlerStrategy(URI uri, File requestedFile, String handlerFound, ReasonPhase reasonPhrase) {
        super(uri, requestedFile, RequestType.ASCII, handlerFound);
        // TODO Auto-generated constructor stub
    }

    public String[] getResourceLines() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getParticularResponseHeaders() {
        // TODO Auto-generated method stub
        return null;
    }

}
