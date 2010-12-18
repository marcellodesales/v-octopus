package com.googlecode.voctopus.aspect.loggin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.aspectj.lang.Signature;

import com.googlecode.voctopus.RequestResponseMediator;
import com.googlecode.voctopus.RequestResponseMediator.ReasonPhase;
import com.googlecode.voctopus.config.VOctopusConfigurationManager;
import com.googlecode.voctopus.request.AbstractHttpRequest;
import com.googlecode.voctopus.request.HttpInvalidRequest;
import com.googlecode.voctopus.response.AbstractHttpResponse;


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
    
    pointcut sendingResponse(RequestResponseMediator mediator) :
        target(mediator) && execution(* RequestResponseMediator.sendResponse());

    after (RequestResponseMediator mediator) : sendingResponse(mediator) {
        
        synchronized (thisJoinPoint) {
            String ip = mediator.getClientConnection().getClientConnection().getInetAddress().getHostAddress();
            String dateTime = VOctopusConfigurationManager.LogFormats.ACCESS_LOG_FILE.format(new Date());
            this.buffer.append(ip + " - - " + dateTime + " ");
            
            AbstractHttpRequest request = (AbstractHttpRequest)mediator.getRequest();
            AbstractHttpResponse response = (AbstractHttpResponse)mediator.getResponse();
            
            if (request.getUri() != null && request.getRequestVersion() != null && request.getMethodType() != null){
                this.buffer.append("\"" + request.getMethodType() + " " + request.getUri().getRawPath() + " "); 
                this.buffer.append(request.getRequestVersion() + "\" ");
                
                long size = request instanceof HttpInvalidRequest ? 0 : response.getRequestSize();
                
                this.buffer.append(request.getStatus().getCode() + " " + size + "\n");
            } else {
                this.buffer.append("\"" + ReasonPhase.STATUS_400 + "\"");
            }
            this.logAccess();
        }
    }
    
//    pointcut loggingAccess(AbstractHttpResponse abstractResp, OutputStream clientOutput) :
//        target(abstractResp) && args(clientOutput) && execution(* AbstractHttpResponse.sendResponse(OutputStream));
//    
//    after (AbstractHttpResponse abstractResp, OutputStream clientOutput) : loggingAccess(abstractResp, clientOutput) {
//        
//    }
    
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
