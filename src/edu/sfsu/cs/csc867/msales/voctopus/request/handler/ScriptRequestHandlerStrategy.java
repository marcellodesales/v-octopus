package edu.sfsu.cs.csc867.msales.voctopus.request.handler;

import java.io.File;
import java.util.Map;

public class ScriptRequestHandlerStrategy extends AbstractRequestHandler {
    
    /**
     * The request parameters from the request to be used on the script.
     */
    private Map<String, String> requestParameters;

    public ScriptRequestHandlerStrategy(Map<String, String> requestParameters, File requestedFile, String handlerFound) {
        super(requestedFile, RequestType.ASCII, handlerFound);
        this.requestParameters = requestParameters;
        //TODO: THE ASCII is just to consider execution that returns ASCII, needs refactoring
        //to get the type from the execution from the script.
    }
    
    public String[] getResourceLines() {
        this.requestParameters.containsKey("");
        //TODO: Use the request parameters to pass to the execution of the script.
        return null;
    }
}
