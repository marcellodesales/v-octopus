package edu.sfsu.cs.csc867.msales.voctopus.request.handler;

import java.io.File;
import java.net.URI;

import edu.sfsu.cs.csc867.msales.voctopus.RequestResponseMediator.ReasonPhase;

/**
 * Class responsible for the handling of files with an unknown mime type. It must be handled as an Ascii type.
 * 
 * @author marcello Feb 24, 2008 9:33:00 AM
 */
public class UnknownContentRequestHandlerStrategy extends AsciiContentRequestHandlerStrategy {

    /**
     * Builds a new UnknownContentRequest based on the uri, with the file representation and the handler found.
     * 
     * @param uri
     * @param requestedFile
     * @param reasonPhrase
     */
    public UnknownContentRequestHandlerStrategy(URI uri, File requestedFile, ReasonPhase reasonPhrase) {
        super(uri, requestedFile, "", reasonPhrase);
    }
}
