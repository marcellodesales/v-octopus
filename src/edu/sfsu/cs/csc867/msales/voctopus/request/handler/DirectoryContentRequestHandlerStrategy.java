package edu.sfsu.cs.csc867.msales.voctopus.request.handler;

import java.io.File;
import java.io.IOException;

/**
 * @author marcello
 * Feb 24, 2008 8:34:25 AM
 */
public class DirectoryContentRequestHandlerStrategy extends AbstractRequestHandler  {

    public DirectoryContentRequestHandlerStrategy(File requestedFile) {
        super(requestedFile, RequestType.ASCII);
        // TODO Auto-generated constructor stub
    }

    public String[] getResourceLines() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
}
