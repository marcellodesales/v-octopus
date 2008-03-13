package edu.sfsu.cs.csc867.msales.voctopus.request;

import java.io.File;
import java.net.URI;
import java.util.Map;

import edu.sfsu.cs.csc867.msales.voctopus.RequestResponseMediator.ReasonPhase;
import edu.sfsu.cs.csc867.msales.voctopus.config.VOctopusConfigurationManager;


/**
 * @author marcello
 * Feb 16, 2008 11:02:29 AM
 */
public class HttpScriptRequest extends AbstractHttpRequest {

    public HttpScriptRequest(String methodType, URI uri, String version, Map<String, String> headerVars) {
        super(methodType, uri, version, headerVars);
    }
    
    /* (non-Javadoc)
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest#getRequestedResource()
     */
    public File getRequestedResource() {
        ReasonPhase status = this.getStatus();
        if (status != null && status.equals(ReasonPhase.STATUS_500)) {
            return VOctopusConfigurationManager.get500ErrorFile();
        }
        //TODO REVIEW THIS SSSS...
        return null;
    }

}