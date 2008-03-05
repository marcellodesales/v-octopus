package edu.sfsu.cs.csc867.msales.voctopus.aspect.loggin;

import java.io.IOException;

import java.io.File;
import java.io.FileWriter;

import edu.sfsu.cs.csc867.msales.voctopus.VOctopusConfigurationManager;

import java.net.Socket;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.aspectj.lang.Signature;

import edu.sfsu.cs.csc867.msales.voctopus.HttpClientConnection;

/**
 * Aspect responsible for the loggin of the entire application.
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
        
        //"127.0.0.1 - - [27/Feb/2008:08:56:41 -0800] "GET /?C=N;O=A HTTP/1.1" 200 1590 
        //"-" "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1.12) Gecko/20080207 
        //Ubuntu/7.10 (gutsy) Firefox/2.0.0.12"

        this.buffer.append(socket.getInetAddress().getHostAddress() + "\n\r");
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
