package edu.sfsu.cs.csc867.msales.voctopus.request.handler;

import java.io.File;
import java.io.IOException;

/**
 * @author marcello
 * Feb 24, 2008 8:36:39 AM
 */
public class WebServiceRequestHandlerStrategy extends AbstractRequestHandler {

    public WebServiceRequestHandlerStrategy(File requestedFile, String handlerFound) {
        super(requestedFile, RequestType.ASCII, handlerFound);
        // TODO Auto-generated constructor stub
    }

    public String[] getResourceLines() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

}
