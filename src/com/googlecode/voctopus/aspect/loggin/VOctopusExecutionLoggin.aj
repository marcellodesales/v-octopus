package com.googlecode.voctopus.aspect.loggin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;

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
    
    private static final Logger logger = Logger.getLogger(VOctopusExecutionLoggin.class);

    private StringBuffer buffer = new StringBuffer();
    
    private VOctopusExecutionLoggin() {
    }

    /**
     * The cross-cutting concern of sending the response.
     * @param mediator is the mediator instance.
     */
    pointcut sendingResponse(RequestResponseMediator mediator) :
        target(mediator) && execution(* RequestResponseMediator.sendResponse());

    /**
     * The advice of after the execution of the cross-cutting concern that sends the response. That is, after the
     * method execution, the Access Log is updated.
     * @param mediator is the instance of the request-response mediator.
     */
    after (RequestResponseMediator mediator) : sendingResponse(mediator) {
        
        synchronized (thisJoinPoint) {
            String ip = mediator.getClientConnection().getClientConnection().getInetAddress().getCanonicalHostName();
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
            this.writeRequestToAccessLog();
        }
    }

    private void writeRequestToAccessLog() {
        File accessLogFile = VOctopusConfigurationManager.getInstance().getAccessLogFile();
        if (!accessLogFile.exists()) {
            try {
                logger.debug("Attempting to create the log file '" + accessLogFile + "'");
                boolean fileCreated = accessLogFile.createNewFile();
                if (!fileCreated) {
                    logger.error("It seems that the webserver does not have write permissions to create the file '" 
                            + accessLogFile + "'");
                } else {
                    logger.debug("Access Log file created successfully: '" + accessLogFile + "'");
                }
            } catch (IOException ioe) {
                logger.error("An error occurred while creating the access log: '" + accessLogFile + "'", ioe);
            }
        }
        if (accessLogFile.exists()) {
            try {
                FileWriter writer = new FileWriter(accessLogFile, true);
                writer.append(this.buffer.toString());
                writer.flush();
                writer.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            logger.error("Records for the Access Log can't be created: '" + accessLogFile + "'. Verify " +
                "permissions for the running user.");
        }
    }
}
