package edu.sfsu.cs.csc867.msales.voctopus.request.handler;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import edu.sfsu.cs.csc867.msales.voctopus.RequestResponseMediator.ReasonPhrase;
import edu.sfsu.cs.csc867.msales.voctopus.config.VOctopusConfigurationManager;
import edu.sfsu.cs.csc867.msales.voctopus.request.AbstractHttpRequest;

/**
 * This abstract factory is responsible for the construction of the handlers.
 * 
 * @author marcello Feb 20, 2008 2:48:59 PM
 */
public class HttpRequestHandlerAbstractFactory {

    private static final String TEXT_HTML = "text/html";
    /**
     * TheradLocal instance for this singleton.
     */
    private static ThreadLocal<HttpRequestHandlerAbstractFactory> singleton = new ThreadLocal<HttpRequestHandlerAbstractFactory>() {
        @Override
        protected HttpRequestHandlerAbstractFactory initialValue() {
            // TODO Auto-generated method stub
            return new HttpRequestHandlerAbstractFactory();
        }
    };

    /**
     * Private constructor for the singleton.
     */
    private HttpRequestHandlerAbstractFactory() {
    }

    /**
     * @return the unique instance of this class.
     */
    public static HttpRequestHandlerAbstractFactory getInstance() {
        return singleton.get();
    }
    
    /**
     * Creates a request handler for a given. It internally changes the status (Reason Phase) of the request
     * based on the handling process, cache mechanisms, etc.

     * @param abstractHttpRequest is the request used
     * @return an instance of an HttpRequestHandler, with the correct ReasonPhase changed
     */
    public HttpRequestHandler createRequestHandler(AbstractHttpRequest abstractHttpRequest) {
        
        URI uri = abstractHttpRequest.getUri();
        String fileSystem = VOctopusConfigurationManager.getInstance().getDocumentRoot() 
                                + uri.getPath();
        File file = new File(fileSystem);
        HttpRequestHandler handler = null;
        
        // Show the contents of the directory if there is no index file on the directory
        if (file.isDirectory()) {

            boolean foundIndexFile = false;
            String[] exts = VOctopusConfigurationManager.getInstance().getDirectoryIndexExtensions();
            for (String dirExts : exts) {
                file = new File(fileSystem + dirExts);
                if (!file.exists()) {
                    continue;
                } else {
                    foundIndexFile = true;
                    break;
                }
            }
            
            ReasonPhrase requestStatus = null;
            if (foundIndexFile) {
                requestStatus = CacheStateControl.getRequestReasonPhase(file, 
                    abstractHttpRequest.getRequestHeaders());
            } else {
                requestStatus = CacheStateControl.getRequestReasonPhase(file = new File(fileSystem), 
                        abstractHttpRequest.getRequestHeaders());
            }
            
            if (requestStatus.equals(ReasonPhrase.STATUS_304) || requestStatus.equals(ReasonPhrase.STATUS_405) 
                    || requestStatus.equals(ReasonPhrase.STATUS_204)) {
                return this.requestedFileExistsWithStatus(requestStatus, new EmptyBodyRequestHandler(
                        abstractHttpRequest.getUri(), file));
            }
            
            if (foundIndexFile) {
                handler = this.createFileHandler(uri, file);
            } else {
                handler = this.createDirectoryHandler(uri, new File(fileSystem));
            }

        } else {
            String scriptPath = "";
            if (uri.getPath().contains("/cgi-bin/")) {
                scriptPath = uri.getPath().substring(uri.getPath().indexOf("/cgi-bin/") + "/cgi-bin/".length());
                scriptPath = VOctopusConfigurationManager.WebServerProperties.ALIAS.getProperties().get(
                        "/" + scriptPath + "/");
                // No match found on the aliases. Scripts need to be registered!
                if (scriptPath != null) {
                    file = new File(scriptPath);
                } else {
                    scriptPath = VOctopusConfigurationManager.getDefaultCGIPath()
                            + uri.getPath().replace("/cgi-bin", "");
                    file = new File(scriptPath);
                }
            }

            if (scriptPath == null || !file.exists()) {
                return this.getFileNotFoundHander(uri);
            } else {
                handler = createFileHandler(uri, file);
            }
        }
        return handler;
    }

    /**
     * Creates a handler for the the directory listing.
     * 
     * @param dirFile is the directory file from the file system.
     * @return an instance for the Directory content request.
     */
    private HttpRequestHandler createDirectoryHandler(URI uri, File dirFile) {
        return requestedFileExistsWithStatus(ReasonPhrase.STATUS_200, new DirectoryContentRequestHandlerStrategy(uri,
                dirFile, TEXT_HTML));
    }

    /**
     * @param status
     * @param handler
     * @return The updates the request handler with the given status 
     */
    private HttpRequestHandler requestedFileExistsWithStatus(ReasonPhrase status, HttpRequestHandler handler) {
        handler.setStatus(status);
        return handler;
    }
    
    /**
     * @param uri
     * @return
     */
    public HttpRequestHandler getFileNotFoundHander(URI uri) {
        File file = VOctopusConfigurationManager.get404ErrorFile();
        return new ScriptRequestHandlerStrategy(null, uri, file, TEXT_HTML, ReasonPhrase.STATUS_404);
    }

    /**
     * Creates a handler for a request for a file from the file system.
     * 
     * @param fileExists
     * @param uri is the request identification of the request.
     * @param file is the file located on the file system.
     * @return The request handler for the file on the file system.
     */
    private HttpRequestHandler createFileHandler(URI uri, File file) {
        String path = file.getPath();
        String requestedExtension = path.substring(path.lastIndexOf(".") + 1);
        Map<String, String> mimes = VOctopusConfigurationManager.WebServerProperties.MIME_TYPES.getProperties();
        String cgiPath = VOctopusConfigurationManager.getDefaultCGIPath();
        
        if (path.contains(cgiPath)) {
            // remove the ? question mark
            String params = null;
            Map<String, String> cgiParams = null;
            if (uri.getQuery() != null) {
                params = uri.getQuery();
                String[] paramVals = params.split("&");
                cgiParams = new HashMap<String, String>(paramVals.length);
                for (String cgiP : paramVals) {
                    String[] varValue = cgiP.split("=");
                    cgiParams.put(varValue[0], varValue[1]);
                }
            }
            return new ScriptRequestHandlerStrategy(cgiParams, uri, file, requestedExtension, ReasonPhrase.STATUS_200);

        } else if (path.contains(VOctopusConfigurationManager.getDefaultWebservicesPath())) {
            
            return this.requestedFileExistsWithStatus(ReasonPhrase.STATUS_200, new WebServiceRequestHandlerStrategy(
                    uri, file, requestedExtension));
        }

        String handlerFound = "";
        String mimeValue = "";
        outer: for (String handlerType : mimes.keySet()) {
            mimeValue = mimes.get(handlerType);
            for (String mimeExts : mimeValue.replace(",", "").split(" ")) {
                if (mimeExts.equals(requestedExtension)) {
                    handlerFound = handlerType;
                    break outer;
                }
            }
        }

        if (handlerFound.equals("")) {
            return this.requestedFileExistsWithStatus(ReasonPhrase.STATUS_200,
                    new UnknownContentRequestHandlerStrategy(uri, file));
        } else if (handlerFound.contains("text/")) {
            return this.requestedFileExistsWithStatus(ReasonPhrase.STATUS_200, new AsciiContentRequestHandlerStrategy(
                    uri, file, handlerFound));
        } else {
            return this.requestedFileExistsWithStatus(ReasonPhrase.STATUS_200, new BinaryContentRequestHandlerStrategy(
                    uri, file, handlerFound));
        }
    }
}