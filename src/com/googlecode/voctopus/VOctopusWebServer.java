package com.googlecode.voctopus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.googlecode.voctopus.config.VOctopusConfigurationManager;
import com.googlecode.voctopus.request.validation.HttpRequestInterpreterException;

/**
 * The VoctopusWebServer is the main class from the server. It spawns the threads of the clients on the pool and keeps
 * receiving the connections from them.
 * 
 * @author marcello Feb 15, 2008 1:23:03 PM
 */
public class VOctopusWebServer {

    private static final Logger logger = Logger.getLogger(VOctopusWebServer.class);

    static {
        String serverRootPath = null;
        try {
            serverRootPath = new File(".").getCanonicalPath().toString();
            logger.debug("Configuration set to path: " + serverRootPath);
            VOctopusConfigurationManager.getInstance().setServerRootPath(serverRootPath);

        } catch (FileNotFoundException e) {
            logger.error("############### ERROR INITIALIZING VOCTOPUS WEB SERVER ####################");
            logger.error("# CAUSE: Your environment var 'VOCTOPUS_SERVER_ROOT' is set to: '"
                    + serverRootPath + "' However, I coundn't find the needed configuration files there...");
            logger.error("# MESSAGE: " + e.getMessage());
            logger.error("# SOLUTION: ");
            logger.error("#   * Add the complete deployment of the server on this directory;");
            logger.error("#   * Change the environment variable the place where I can find the configuration " +
                "files;");
            logger.error("#   * A directory block may contain the path for a non-existing file.");
            logger.error("###########################################################################");
            System.exit(2);
            // TODO: LOGGIN NEEDED
        } catch (IOException e) {
            logger.error("############### ERROR INITIALIZING VOCTOPUS WEB SERVER ####################");
            logger.error("# CAUSE: I/O Error: Can't read the configuration file " + serverRootPath + 
                    "/conf/httpd.conf.'");
            logger.error("# MESSAGE: " + e.getMessage());
            logger.error("# SOLUTION: ");
            logger.error("#   * Verify your permissions to this file; ls -la");
            logger.error("#   * If you have permissions, use chmod and chown to get the ownership of the file;");
            logger.error("###########################################################################");
            System.exit(3);
        }
        int threads = 50;
        try {
            threads = Integer.parseInt(VOctopusConfigurationManager.WebServerProperties.HTTPD_CONF
                    .getPropertyValue("MaxThreads"));
        } catch (Exception e) {
        }
        threadsPool = new ThreadPoolExecutor(threads, threads, Long.MAX_VALUE, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(2 * threads), new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    /**
     * The pool of threads will be used to control the requests to the server. If has the following attributes: <BR>
     * <li>corePoolSize is the number of threads that are allowed to be in the pool even if they are in idle; <BR>
     * <li>maximumPoolSize is the maximum number of threads allowed in the pool; <BR>
     * <LI>keepAliveTime and timeUnit are used to indicate how long new threads will wait when the the number of
     * threads in the pool are greater than the corePoolSize; <BR>
     * <LI>workQueue is the queue that will hold the runnables before they execute; <br>
     * <li>retentionPolicy is the policy used to remove the tasks.
     */
    private static Executor threadsPool;

    /**
     * Hands the client connection, handling the Request and Response.
     * 
     * @param clientSocket
     * @throws HttpErrorException
     * @throws HttpRequestInterpreterException
     */
    private static void handleClientConnection(Socket clientSocket) {

        RequestResponseMediator mediator;
        mediator = new RequestResponseMediator(new HttpClientConnection(clientSocket));
        mediator.sendResponse();
    }

    /**
     * Prints the clients thread pool.
     */
    private static void printServerPoolStatus() {
        ThreadPoolExecutor pool = (ThreadPoolExecutor) threadsPool;
        logger.trace("Thread Pool [ " + pool.getCorePoolSize() + " , " + pool.getMaximumPoolSize() + " ]");
        logger.trace("The Largest Pool size: " + pool.getLargestPoolSize());
        logger.trace("# of active threads: " + pool.getActiveCount());
        logger.trace("# of maximum pool size:" + pool.getMaximumPoolSize());
    }

    public static void main(String[] args) {

        String listeningPort = VOctopusConfigurationManager.WebServerProperties.HTTPD_CONF.getPropertyValue("Listen");
        String name = VOctopusConfigurationManager.WebServerProperties.HTTPD_CONF.getPropertyValue("ServerName");
        logger.info("V-Octopus Web Serving running, waiting connections on " + name + ":" + listeningPort);

        try {
            ServerSocket socketServer = new ServerSocket(Integer.parseInt(listeningPort));
            while (true) {
                final Socket clientSocket = socketServer.accept();
                Runnable connectionHandler = new Runnable() {
                    public void run() {
                        long initial = System.currentTimeMillis();
                        handleClientConnection(clientSocket);
                        long end = System.currentTimeMillis();
                        logger.debug("Served the request of " + clientSocket.getInetAddress().getCanonicalHostName() + 
                                " in " + (end - initial) + " ms");
                    }
                };

                printServerPoolStatus();
                threadsPool.execute(connectionHandler);
            }

        } catch (NumberFormatException portNumberMalformedError) {
            logger.error("############### ERROR INITIALIZING VOCTOPUS WEB SERVER ####################");
            logger.error("# CAUSE: The value for the port of the webserver couldn't be determined...");
            logger.error("# SOLUTION:");
            logger.error("#   * Make sure that the constant 'Listen' specifies an integer value for the port,"
                    + "considering that you have sufficient privilegues to use it.");
            logger.error("###########################################################################");
            System.exit(4);

        } catch (BindException otherProcessRunningOnSamePortError) {
            logger.error("############### ERROR INITIALIZING VOCTOPUS WEB SERVER ####################");
            String port = VOctopusConfigurationManager.WebServerProperties.HTTPD_CONF.getPropertyValue("Listen");
            logger.error("# CAUSE: The port specified in the configuration file is in use: " + port);
            logger.error("# SOLUTION:");
            logger.error("#   * Change the specified port on the httpd.conf file an available one;");
            logger.error("#   * Stop the application running on that port and try restarting the server again.");
            logger.error("###########################################################################");
            System.exit(5);

        } catch (IOException otherErrorWhileInitializing) {
            logger.error("############### ERROR INITIALIZING VOCTOPUS WEB SERVER ####################");
            logger.error("Another error while initializing the server", otherErrorWhileInitializing);
            logger.error("###########################################################################");
            System.exit(0);
        }
    }
}