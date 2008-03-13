package edu.sfsu.cs.csc867.msales.voctopus.aspect.loggin;

import edu.sfsu.cs.csc867.msales.voctopus.request.AbstractHttpRequest;
import edu.sfsu.cs.csc867.msales.voctopus.HttpClientConnection;
import edu.sfsu.cs.csc867.msales.voctopus.config.VOctopusConfigurationManager;
import edu.sfsu.cs.csc867.msales.voctopus.response.AbstractHttpResponse;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.aspectj.lang.Signature;

/**
 * Aspect responsible for the loggin cross-cut concern of the entire application.
 * @author marcello
 * Feb 16, 2008 8:26:02 AM
 */
public aspect VOctopusExecutionLoggin {
    
    private Logger vologger = Logger.getLogger("tracer");
    private Logger acclogger = Logger.getLogger("accessLogger");

    private StringBuffer buffer = new StringBuffer();
    
    private VOctopusExecutionLoggin() {
        this.vologger.setLevel(Level.ALL);
        this.acclogger.setLevel(Level.ALL);
    }

    pointcut excludedObjectCalls() : execution(* Logger.*(..));
    pointcut aspects() : within(VOctopusExecutionLoggin);

    pointcut openingConnection(HttpClientConnection clientConnection, Socket socket): 
        target(clientConnection) && args(socket) &&  execution(*.new(Socket));
    
    after (HttpClientConnection clientConnection, Socket socket): openingConnection(clientConnection, socket) {
        String ip = socket.getInetAddress().getHostAddress();
        String dateTime = VOctopusConfigurationManager.LogFormats.ACCESS_LOG_FILE.format(new Date());
        this.buffer.append(ip + " - - " + dateTime + " ");
    }
    
//    pointcut constructingRequest(AbstractHttpRequest abstrRequest, String methodType, URI uri, String version, 
//            Map<String, String> headerVars) : target(abstrRequest) && args(methodType, uri, version, headerVars) 
//              && execution(*.new(String, URI, String, Map<String, String> ));
//    
//    after (AbstractHttpRequest abstrRequest, String methodType, URI uri, String version, 
//            Map<String, String> headerVars) : constructingRequest(abstrRequest, methodType, uri, version, headerVars) {
//        this.buffer.append("\"" + abstrRequest.getMethodType() + " " + uri.getRawPath() + " " + version + "\" ");
//        this.buffer.append(abstrRequest.getStatus().getCode() + " ");
//    }
    
    pointcut loggingAccess(AbstractHttpResponse abstractResp, OutputStream clientOutput) :
        target(abstractResp) && args(clientOutput) && execution(* AbstractHttpResponse.sendResponse(OutputStream));
    
    after (AbstractHttpResponse abstractResp, OutputStream clientOutput) : loggingAccess(abstractResp, clientOutput) {
        AbstractHttpRequest request = (AbstractHttpRequest)abstractResp.getRequest();
        this.buffer.append("\"" + request.getMethodType() + " " + request.getUri().getRawPath() + " "); 
        this.buffer.append(request.getVersion() + "\" ");
        this.buffer.append(request.getStatus().getCode() + " " + abstractResp.getRequestSize() + "\n");
        this.logAccess();
    }
    
    private void logAccess() {
        File accessLogFile = VOctopusConfigurationManager.getInstance().getAccessLogFile();
        if (accessLogFile.exists()) {
            try {
                FileWriter writer = new FileWriter(accessLogFile, true);
                writer.append(this.buffer.toString());
                writer.flush();
                writer.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
    
    pointcut vOcotpusPackage() : execution(* edu.sfsu.cs.csc867.msales.voctopus..public(..)) 
                                 || execution(edu.sfsu.cs.csc867.msales.voctopus..new(..));
      
    pointcut loggableCalls() : vOcotpusPackage() && !aspects() && !excludedObjectCalls();
      
    before() : loggableCalls() {
        
        if (this.vologger.isLoggable(Level.INFO)) {
            Signature sig = thisJoinPointStaticPart.getSignature();
            this.vologger.logp(Level.INFO, sig.getDeclaringTypeName(), sig.getName(), "Entering");
        }
    }
}
