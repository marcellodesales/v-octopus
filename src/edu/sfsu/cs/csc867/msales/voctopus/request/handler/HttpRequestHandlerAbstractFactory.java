package edu.sfsu.cs.csc867.msales.voctopus.request.handler;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import edu.sfsu.cs.csc867.msales.voctopus.VOctopusConfigurationManager;
import edu.sfsu.cs.csc867.msales.voctopus.RequestResponseMediator.ReasonPhrase;

/**
 * This abstract factory is responsible for the construction of the handlers.
 * @author marcello
 * Feb 20, 2008 2:48:59 PM
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
     * Creates a handler for the the directory listing.
     * @param dirFile is the directory file from the file system.
     * @return an instance for the Directory  content request.
     */
    private HttpRequestHandler createDirectoryHandler(URI uri, File dirFile) {
        return requestedFileExistsWithStatus(ReasonPhrase.STATUS_200, 
                new DirectoryContentRequestHandlerStrategy(uri, dirFile, TEXT_HTML));
    }
    
    private HttpRequestHandler requestedFileExistsWithStatus(ReasonPhrase status, HttpRequestHandler handler) {
        handler.setStatus(status);
        return handler;
    }
    
    /**
     * Creates a handler for a request for a file from the file system.
     * @param fileExists 
     * @param uri is the request identification of the request.
     * @param file is the file located on the file system.
     * @return The request handler for the file on the file system.
     */
    private HttpRequestHandler createFileHandler(boolean fileExists, URI uri, File file) {
        String path = file.getPath();
        String requestedExtension = path.substring(path.lastIndexOf(".") + 1);
        Map<String, String> mimes = VOctopusConfigurationManager.WebServerProperties.MIME_TYPES.getProperties();
        String cgiPath = VOctopusConfigurationManager.getDefaultCGIPath();
     
        if (!fileExists) {
            return this.requestedFileExistsWithStatus(ReasonPhrase.STATUS_404, 
                    new ScriptRequestHandlerStrategy(null, uri, file, TEXT_HTML));
        }

        if (path.contains(cgiPath)) {
            //remove the ? question mark
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
            return this.requestedFileExistsWithStatus(ReasonPhrase.STATUS_200, 
                    new ScriptRequestHandlerStrategy(cgiParams, uri, file, requestedExtension));
            
        } else 
        if (path.contains(VOctopusConfigurationManager.getDefaultWebservicesPath())) {
            return this.requestedFileExistsWithStatus(ReasonPhrase.STATUS_200,
                    new WebServiceRequestHandlerStrategy(uri, file, requestedExtension));
        }
        
        String handlerFound = "";
        String mimeValue = "";
        outer:
        for (String handlerType : mimes.keySet()) {
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
        } else
        if (handlerFound.contains("text/")) {
            return this.requestedFileExistsWithStatus(ReasonPhrase.STATUS_200,
                    new AsciiContentRequestHandlerStrategy(uri, file, handlerFound));
        } else {
            return this.requestedFileExistsWithStatus(ReasonPhrase.STATUS_200,
                    new BinaryContentRequestHandlerStrategy(uri, file, handlerFound));
        }
    }

    /**
     * Creates a request handler for a given
     * @param uri is the identification of the request
     * @param headerVars holds the header variables
     * @return The request handler for any type of request. CGI, Web Services, ASCII and Binary handlers.
     */
    public HttpRequestHandler createRequestHandler(URI uri, Map<String, String> headerVars) {
        String fileSystem = VOctopusConfigurationManager.getInstance().getDocumentRoot() + uri.getPath();
        File file = new File(fileSystem);
        HttpRequestHandler handler = null;
        
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
            if (foundIndexFile) {
                handler = createFileHandler(true, uri, file);
            } else {
                handler = createDirectoryHandler(uri, new File(fileSystem));
            }
            
        } else {
            //File Not Found, then return an ascii handler.
            if (uri.getPath().contains("/cgi-bin/")) {
                String scriptPath = uri.getPath().substring(uri.getPath().indexOf("/cgi-bin/") + "/cgi-bin/".length());
                scriptPath = VOctopusConfigurationManager.WebServerProperties.ALIAS.getProperties().get("/" + scriptPath + "/");
                file = new File(scriptPath);
            }
            
            if (!file.exists()) {
                file = new File(VOctopusConfigurationManager.getInstance().getDocumentRoot() + "/errors/404.html");
                handler = createFileHandler(false, uri, file);
            } else {
                handler = createFileHandler(true, uri, file);
            }
        }
        return handler;
    }
}