package edu.sfsu.cs.csc867.msales.voctopus.request.handler;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import edu.sfsu.cs.csc867.msales.voctopus.RequestResponseMediator.ReasonPhase;
import edu.sfsu.cs.csc867.msales.voctopus.config.DirectoryConfigHandler;
import edu.sfsu.cs.csc867.msales.voctopus.config.VOctopusConfigurationManager;
import edu.sfsu.cs.csc867.msales.voctopus.request.AbstractHttpRequest;
import edu.sfsu.cs.csc867.msales.voctopus.request.HttpInvalidRequest;
import edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest;
import edu.sfsu.cs.csc867.msales.voctopus.request.HttpScriptRequest;
import edu.sfsu.cs.csc867.msales.voctopus.request.HttpWebServiceRequest;
import edu.sfsu.cs.csc867.msales.voctopus.request.AbstractHttpRequest.RequestMethodType;
import edu.sfsu.cs.csc867.msales.voctopus.request.handler.AbstractRequestHandler.RequestType;

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
     * Creates a request handler for a given. It internally changes the status (Reason Phase) of the request based on
     * the handling process, cache mechanisms, etc.
     * 
     * @param abstractHttpRequest is the request used
     * @return an instance of an HttpRequestHandler, with the correct ReasonPhase changed
     */
    public HttpRequestHandler createRequestHandler(AbstractHttpRequest abstractHttpRequest) {

        if (abstractHttpRequest instanceof HttpInvalidRequest) {
            return new InvalidRequestHandler();
        }

        URI uri = abstractHttpRequest.getUri();
        String fileSystem = VOctopusConfigurationManager.getInstance().getDocumentRootPath() + uri.getPath();
        File file = new File(fileSystem);
        HttpRequestHandler handler = null;

        if (abstractHttpRequest.getMethodType().equals(RequestMethodType.PUT) && file.exists()) {
            return new EmptyBodyRequestHandler(abstractHttpRequest.getUri(), file, RequestType.BINARY,
                    ReasonPhase.STATUS_201);
        } else if (abstractHttpRequest.getMethodType().equals(RequestMethodType.PUT) && !file.exists()) {
            return new EmptyBodyRequestHandler(abstractHttpRequest.getUri(), file, RequestType.INVALID,
                    ReasonPhase.STATUS_400);
        } else if (abstractHttpRequest.getMethodType().equals(RequestMethodType.NOT_SUPPORTED)) {
            return new EmptyBodyRequestHandler(abstractHttpRequest.getUri(), file, RequestType.INVALID,
                    ReasonPhase.STATUS_400);
        } else if (!abstractHttpRequest.getMethodType().isImplemented()) {
            return new EmptyBodyRequestHandler(abstractHttpRequest.getUri(), file, RequestType.INVALID,
                    ReasonPhase.STATUS_501);
        } else if (!abstractHttpRequest.getRequestVersion().isValid()) {
            return new EmptyBodyRequestHandler(abstractHttpRequest.getUri(), file, RequestType.INVALID,
                    ReasonPhase.STATUS_505);
        }

        if (!this.doesUserhavePermission(uri)) {
            file = VOctopusConfigurationManager.get403ErrorFile();
            return new ScriptRequestHandlerStrategy(null, uri, file, TEXT_HTML, ReasonPhase.STATUS_403,
                    abstractHttpRequest);
        }

        DirectoryConfigHandler dirHandler = VOctopusConfigurationManager.getInstance().isRequestedURIProtected(uri);
        if (dirHandler != null) {
            String authorization = null;
            for (String headerVarValue : abstractHttpRequest.getRequestHeaders()) {
                if (headerVarValue.startsWith("Authorization: Basic ")) {
                    authorization = headerVarValue.replace("Authorization: Basic ", "").trim();
                    break;
                } else if (headerVarValue.startsWith("Authorization: Digest ")) {
                    authorization = headerVarValue.replace("Authorization: Digest ", "").trim();
                    break;
                }
            }
            if (authorization != null) {
                if (!new ProtectedContentRequestHandlerStrategy(uri, file, dirHandler, authorization)
                        .isAuthorizationValid()) {

                    File unAuthFile = VOctopusConfigurationManager.get401ErrorFile();
                    return new ScriptRequestHandlerStrategy(null, uri, unAuthFile, TEXT_HTML, ReasonPhase.STATUS_401,
                            abstractHttpRequest);
                }
            } else {
                return new ProtectedContentRequestHandlerStrategy(uri, file, dirHandler, null);
            }
        }
        
        file = ScriptRequestHandlerStrategy.getScriptPathForAlias(uri);
        if (file != null) {
            return createFileHandler(uri, file, abstractHttpRequest);
//        if (uri.getPath().startsWith("/soa-ws/")
//                || VOctopusConfigurationManager.getWebServicesAlias().get("/" + alias + "/") != null) {
//            //TODO: implement the handler for webservices here....
//            
        } 
        
        file = AsciiContentRequestHandlerStrategy.getFilePathForAlias(uri);
        if (file != null) {
            
            if (!file.canRead()) {
                return new EmptyBodyRequestHandler(abstractHttpRequest.getUri(), file, RequestType.INVALID,
                        ReasonPhase.STATUS_403);
            } else
            if (file.exists() && file.isDirectory()) {
                return handleDirectoryRequest(abstractHttpRequest, uri, file.getAbsolutePath(), file);
            } else
            if (file.exists() && file.isFile())
                return createFileHandler(uri, file, abstractHttpRequest);

        } else {

            uri = abstractHttpRequest.getUri();
            fileSystem = VOctopusConfigurationManager.getInstance().getDocumentRootPath() + uri.getPath();            
            if (!fileSystem.endsWith("/")) {
                fileSystem = fileSystem + "/";
            }
            file = new File(fileSystem);

            if (!file.exists()) {
//            file = VOctopusConfigurationManager.getInstance().matchAlias(uri);
//            if (file == null) {
                return this.getFileNotFoundHander(uri, abstractHttpRequest);
            
            } else if (file.isDirectory()) { // updating the file system var with the updated alias path
                handler = handleDirectoryRequest(abstractHttpRequest, uri, file.getAbsolutePath(), file);
            } else {
                handler = createFileHandler(uri, file, abstractHttpRequest);
            }
        }
        return handler;
    }

    private HttpRequestHandler handleDirectoryRequest(AbstractHttpRequest abstractHttpRequest, URI uri,
            String fileSystem, File file) {

        boolean foundIndexFile = false;
        String[] exts = VOctopusConfigurationManager.getInstance().getDirectoryIndexExtensions();
        for (String dirExts : exts) {

            if (!fileSystem.endsWith("/")) {
                fileSystem = fileSystem + "/";
            }
            file = new File(fileSystem + dirExts);
            if (!file.exists()) {
                continue;
            } else {
                foundIndexFile = true;
                break;
            }
        }

        ReasonPhase requestStatus = ReasonPhase.STATUS_200;
        if (foundIndexFile) {
            requestStatus = CacheStateControl.getRequestReasonPhase(file, abstractHttpRequest.getRequestHeaders());
        } 

        if (requestStatus.equals(ReasonPhase.STATUS_304) || requestStatus.equals(ReasonPhase.STATUS_204)) {
            return new CachedRequestHandler(abstractHttpRequest.getUri(), file);

        } else if (foundIndexFile) {
            return new AsciiContentRequestHandlerStrategy(uri, file, "text/html", ReasonPhase.STATUS_200);
        } else {
            return new DirectoryContentRequestHandlerStrategy(uri, new File(fileSystem), ReasonPhase.STATUS_200);
        }
    }

    /**
     * Validates the permission on a requested URI
     * 
     * @param uri is the request URI from the client.
     * @return if the client has permission on the uri.
     */
    private boolean doesUserhavePermission(URI uri) {
        return !uri.getPath().equals("/cgi-bin/") && !uri.getPath().contains("/soa-ws/");
        // TODO GET THE LIST OF PROTECTED DIRECTORIES FROM THE CONFIGURATION FILE!!! JUST HARD-CODING /cgi-bin /ws-soa
        //the contains must be changed later on...
    }

    /**
     * @param uri
     * @return
     */
    public HttpRequestHandler getFileNotFoundHander(URI uri, HttpRequest originRequest) {
        File file = VOctopusConfigurationManager.get404ErrorFile();
        return new ScriptRequestHandlerStrategy(null, uri, file, TEXT_HTML, ReasonPhase.STATUS_404, originRequest);
    }

    /**
     * Creates a handler for a request for a file from the file system.
     * 
     * @param fileExists
     * @param uri is the request identification of the request.
     * @param file is the file located on the file system.
     * @return The request handler for the file on the file system.
     */
    private HttpRequestHandler createFileHandler(URI uri, File file, HttpRequest originRequest) {
        String path = file.getPath();
        String requestedExtension = path.lastIndexOf(".") > 0 ? path.substring(path.lastIndexOf(".") + 1) : "";

        if (originRequest instanceof HttpScriptRequest) {
            // remove the ? question mark
            String params = null;
            Map<String, String> cgiParams = null;
            if (uri.getQuery() != null) {
                params = uri.getQuery();
                String[] paramVals = params.split("&");
                cgiParams = new HashMap<String, String>(paramVals.length);
                for (String cgiP : paramVals) {
                    String[] varValue = cgiP.split("=");
                    if (varValue.length == 2) {
                        cgiParams.put(varValue[0], varValue[1]);
                    }
                }
            }
            return new ScriptRequestHandlerStrategy(cgiParams, uri, file, requestedExtension, ReasonPhase.STATUS_200,
                    originRequest);
        } else if (originRequest instanceof HttpWebServiceRequest) {
            return new WebServiceRequestHandlerStrategy(uri, file, requestedExtension, ReasonPhase.STATUS_200);
        }

        String handlerFound = "";
        String mimeValue = "";
        Map<String, String> mimes = VOctopusConfigurationManager.WebServerProperties.MIME_TYPES.getProperties();
        outer: for (String handlerType : mimes.keySet()) {
            mimeValue = mimes.get(handlerType);
            for (String mimeExts : mimeValue.replace(",", "").split(" ")) {
                if (mimeExts.equals(requestedExtension)) {
                    handlerFound = handlerType;
                    break outer;
                }
            }
        }
        
        ReasonPhase requestStatus = CacheStateControl.getRequestReasonPhase(file, originRequest.getRequestHeaders());
        if (requestStatus.equals(ReasonPhase.STATUS_304) || requestStatus.equals(ReasonPhase.STATUS_204)) {
            return new CachedRequestHandler(originRequest.getUri(), file);
        }

        if (handlerFound.equals("")) {
            return new UnknownContentRequestHandlerStrategy(uri, file, ReasonPhase.STATUS_200);
        
        } else if (handlerFound.contains("text/")) {
            return new AsciiContentRequestHandlerStrategy(uri, file, handlerFound, ReasonPhase.STATUS_200);
        
        } else {
            return new BinaryContentRequestHandlerStrategy(uri, file, handlerFound, ReasonPhase.STATUS_200);
        }
    }
}