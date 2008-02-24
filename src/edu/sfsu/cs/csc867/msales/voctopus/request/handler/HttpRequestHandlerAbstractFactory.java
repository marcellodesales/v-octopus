package edu.sfsu.cs.csc867.msales.voctopus.request.handler;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import edu.sfsu.cs.csc867.msales.voctopus.VOctopusConfigurationManager;

/**
 * This abstract factory is responsible for the construction of the handlers.
 * @author marcello
 * Feb 20, 2008 2:48:59 PM
 */
public class HttpRequestHandlerAbstractFactory {
    
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
    private HttpRequestHandler createDirectoryHandler(File dirFile) {
        return new DirectoryContentRequestHandlerStrategy(dirFile);
    }
    
    /**
     * Creates a handler for a request for a file from the file system.
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
            //remove the ? question mark
            String params = uri.getQuery().substring(1);
            String[] paramVals = params.split("&");
            Map<String, String> cgiParams = new HashMap<String, String>(paramVals.length);
            for (String cgiP : paramVals) {
                String[] varValue = cgiP.split("=");
                cgiParams.put(varValue[0], varValue[1]);
            }
            return new ScriptRequestHandlerStrategy(cgiParams, file);
            
        } else 
        if (path.contains(VOctopusConfigurationManager.getDefaultWebservicesPath())) {
            return new WebServiceRequestHandlerStrategy(file);
        }
        
        String handlerFound = null;
        String mimeValue = null;
        for (String handlerType : mimes.keySet()) {
            boolean contains = mimes.get(handlerType).contains(requestedExtension);
            mimeValue = mimes.get(handlerType);
            if (contains || (mimeValue != null && !mimeValue.equals("") && mimeValue.contains(requestedExtension))) {
                handlerFound = handlerType;
                break;
            }
        }
        
        if (handlerFound.contains("text/")) {
            return new AsciiContentRequestHandlerStrategy(file);
        } else {
            return new BinaryContentRequestHandlerStrategy(file);
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
        File dir = null;
        HttpRequestHandler handler = null;
        if (file.isDirectory()) {
            dir = file;
            file = new File(file.getAbsolutePath() + "/index.html");
            if (!file.exists()) {
                file = new File(file.getAbsolutePath() + "/index.htm");
                if (!file.exists()) {
                    handler = createDirectoryHandler(dir);
                } else {
                    handler = createFileHandler(uri, file);
                }
            } else {
                handler = createFileHandler(uri, file);
            }
            
        } else {
            handler = createFileHandler(uri, file);
        }
        return handler;
    }
}