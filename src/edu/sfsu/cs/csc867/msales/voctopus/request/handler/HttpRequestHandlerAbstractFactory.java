package edu.sfsu.cs.csc867.msales.voctopus.request.handler;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
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
        } else
        if (abstractHttpRequest.getMethodType().equals(RequestMethodType.PUT) && !file.exists()) {
            return new EmptyBodyRequestHandler(abstractHttpRequest.getUri(), file, RequestType.INVALID, 
                    ReasonPhase.STATUS_400);
        } else
        if (!abstractHttpRequest.getMethodType().isImplemented()) {
            return new EmptyBodyRequestHandler(abstractHttpRequest.getUri(), file, RequestType.INVALID, 
                    ReasonPhase.STATUS_501);
        } else 
        if (!abstractHttpRequest.getRequestVersion().isValid()) {
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
                } else 
                if (headerVarValue.startsWith("Authorization: Digest ")) {
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

        // Show the contents of the directory if there is no index file on the directory
        if (file.isDirectory()) {

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

            ReasonPhase requestStatus = null;
            if (foundIndexFile) {
                requestStatus = CacheStateControl.getRequestReasonPhase(file, abstractHttpRequest.getRequestHeaders());
            } else {
                requestStatus = CacheStateControl.getRequestReasonPhase(file = new File(fileSystem),
                        abstractHttpRequest.getRequestHeaders());
            }

            if (requestStatus.equals(ReasonPhase.STATUS_304) || requestStatus.equals(ReasonPhase.STATUS_405)
                    || requestStatus.equals(ReasonPhase.STATUS_204)) {
                return new CachedRequestHandler(abstractHttpRequest.getUri(), file);
            }

            if (foundIndexFile) {
                handler = new AsciiContentRequestHandlerStrategy(uri, file, "text/html", ReasonPhase.STATUS_200);
            } else {
                handler = new DirectoryContentRequestHandlerStrategy(uri, new File(fileSystem), ReasonPhase.STATUS_200);
            }

        } else {
            String scriptPath = "";
            if (uri.getPath().startsWith("/cgi-bin/")) {
                scriptPath = uri.getPath().substring(uri.getPath().indexOf("/cgi-bin/") + "/cgi-bin/".length());
                scriptPath = VOctopusConfigurationManager.WebServerProperties.ALIAS.getProperties().get(
                        "/" + scriptPath + "/");
                // No match found on the aliases. Scripts need to be registered!
                if (scriptPath != null) {
                    file = new File(scriptPath);
                } else {
                    scriptPath = VOctopusConfigurationManager.getDefaultCGIPath();
                    scriptPath = scriptPath + uri.getPath().replace("/cgi-bin/", "");
                    file = new File(scriptPath);
                }
            }

            if ((scriptPath == null) || !file.exists()) {

                // maybe icons
                String otherOnAliasPath = VOctopusConfigurationManager.getInstance().getServerRootPath()
                        + uri.getPath();
                file = new File(otherOnAliasPath);
                if (!file.exists()) {
                    if (uri.getPath().startsWith("/icons/")) {
                        otherOnAliasPath = VOctopusConfigurationManager.getInstance().getServerRootPath()
                                + "/icons/broken.gif";
                        file = new File(otherOnAliasPath);
                        if (!file.exists()) {
                            try {
                                return this.getFileNotFoundHander(new URI("/icons/broken.gif"), abstractHttpRequest);
                            } catch (URISyntaxException e) {
                                // return this.getFileNotFoundHander(uri);
                                // This will never happen!
                            }
                        } else {
                            handler = createFileHandler(uri, file, abstractHttpRequest);
                        }
                    } else {
                        return this.getFileNotFoundHander(uri, abstractHttpRequest);
                    }
                } else {
                    handler = createFileHandler(uri, file, abstractHttpRequest);
                }

            } else {
                handler = createFileHandler(uri, file, abstractHttpRequest);
            }
        }
        return handler;
    }

    /**
     * Validates the permission on a requested URI
     * 
     * @param uri is the request URI from the client.
     * @return if the client has permission on the uri.
     */
    private boolean doesUserhavePermission(URI uri) {
        return !uri.getPath().equals("/cgi-bin/") && !uri.getPath().equals("/ws-soa/");
        // TODO GET THE LIST OF PROTECTED DIRECTORIES FROM THE CONFIGURATION FILE!!! JUST HARD-CODING /cgi-bin /ws-soa
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

        if (handlerFound.equals("")) {
            return new UnknownContentRequestHandlerStrategy(uri, file, ReasonPhase.STATUS_200);
        } else if (handlerFound.contains("text/")) {
            return new AsciiContentRequestHandlerStrategy(uri, file, handlerFound, ReasonPhase.STATUS_200);
        } else {
            return new BinaryContentRequestHandlerStrategy(uri, file, handlerFound, ReasonPhase.STATUS_200);
        }
    }
}