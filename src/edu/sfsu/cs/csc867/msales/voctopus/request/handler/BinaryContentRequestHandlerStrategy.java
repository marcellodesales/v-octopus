package edu.sfsu.cs.csc867.msales.voctopus.request.handler;

import java.io.File;
import java.io.IOException;
import java.net.URI;


/**
 * @author marcello
 * Feb 24, 2008 8:36:09 AM
 */
public class BinaryContentRequestHandlerStrategy extends AbstractRequestHandler {

    public BinaryContentRequestHandlerStrategy(URI uri, File requestedFile, String handlerFound) {
        super(uri, requestedFile, RequestType.BINARY, handlerFound);
        // TODO Auto-generated constructor stub
    }

    public String[] getResourceLines() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

}
