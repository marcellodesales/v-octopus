package edu.sfsu.cs.csc867.msales.voctopus.aspect.loggin;

import java.util.logging.Logger;
import java.util.logging.Level;
import org.aspectj.lang.Signature;

/**
 * Aspect responsible for the loggin of the entire application.
 * @author marcello
 * Feb 16, 2008 8:26:02 AM
 */
public aspect VOctopusExecutionLoggin {
    
    private Logger vologger = Logger.getLogger("tracer");

    private VOctopusExecutionLoggin() {
        this.vologger.setLevel(Level.ALL);
    }
    
    pointcut aspects() : within(VOctopusExecutionLoggin);
    
    pointcut vOcotpusPackage() : execution(* edu.sfsu.cs.csc867.msales.voctopus..*(..)) || execution(edu.sfsu.cs.csc867.msales.voctopus..new(..));

    pointcut excludedObjectCalls() : execution(* Logger.*(..));
      
    pointcut loggableCalls() : vOcotpusPackage() && !aspects() && !excludedObjectCalls();
      
    before() : loggableCalls() {
        
        if (this.vologger.isLoggable(Level.INFO)) {
            Signature sig = thisJoinPointStaticPart.getSignature();
            this.vologger.logp(Level.INFO, sig.getDeclaringType().getName(), sig.getName(), "Entering");
        }
    }
}
