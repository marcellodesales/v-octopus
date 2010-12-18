package com.googlecode.voctopus.aspect.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.googlecode.voctopus.request.handler.AsciiContentRequestHandlerStrategy;
import com.googlecode.voctopus.request.handler.HttpRequestHandler;


/**
 * Caching mechanism for ascii files. 
 *
 * @author marcello
 * Feb 29, 2008 10:31:18 AM
 */
public aspect CachingStaticContentAspect {

    /**
     * The main cache mechanism for the ascii files. The key for the map is the complete
     * file path in the system, and the value is the list of the lines containing on that file.
     */
    private Map<String, String[]> asciiCache = new ConcurrentHashMap<String, String[]>();
    
    /**
     * This pointcut identifies the call for the getResourceLines from any request handler
     * @param handler
     */
    pointcut returningAsciiFiles(AsciiContentRequestHandlerStrategy handler) : 
            target(handler) && execution(* AsciiContentRequestHandlerStrategy.getResourceLines()); 
    
    /**
     * Advice around the pointcut "returningAsciiFiles".
     * @param handler is an instance of any handler. The idea is to have a polymorphic version
     * of this method.
     * @return the list of the lines of a given ascii files from the cache after the first
     * request to this method and a file.
     */
    String[] around (AsciiContentRequestHandlerStrategy handler) : returningAsciiFiles(handler) {
        if (handler instanceof AsciiContentRequestHandlerStrategy) {
            String key = handler.getRequestedFile().getAbsolutePath();
            String[] cachedLines = this.asciiCache.get(key);
            if (cachedLines == null) {
                System.out.println("No Cache available");
                cachedLines = proceed(handler);
                this.asciiCache.put(key, cachedLines);
            } 
            System.out.println("Now Cached");
            return cachedLines;
        } else {
            return proceed(handler);
        }
    }

//    pointcut returningAsciiFiles(AsciiContentRequestHandlerStrategy handler) : 
//        target(handler) && execution(String[] AsciiContentRequestHandlerStrategy.getResourceLines()); 

//    String[] around (AsciiContentRequestHandlerStrategy handler) : returningAsciiFiles(handler) {
//        String key = handler.getRequestedFile().getAbsolutePath();
//        String[] cachedLines = this.asciiCache.get(key);
//        if (cachedLines == null) {
//            System.out.println("No Cache");
//            cachedLines = proceed(handler);
//            this.asciiCache.put(key, cachedLines);
//        } 
//        System.out.println("Cached");
//        return cachedLines;
//    }
}
