package edu.sfsu.cs.csc867.msales.voctopus.request.handler;

import java.io.File;
import java.io.IOException;
import java.net.URI;


/**
 * @author marcello
 * Feb 24, 2008 9:33:00 AM
 */
public class UnknownContentRequestHandlerStrategy extends AbstractRequestHandler {

    public UnknownContentRequestHandlerStrategy(URI uri, File requestedFile, String handlerFound) {
        super(uri, requestedFile, RequestType.BINARY, handlerFound);
        // TODO Auto-generated constructor stub
    }

    public String[] getResourceLines() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }


}
