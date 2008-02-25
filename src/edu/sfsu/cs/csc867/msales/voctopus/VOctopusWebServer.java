package edu.sfsu.cs.csc867.msales.voctopus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.sfsu.cs.csc867.msales.httpd.HttpErrorException;
import edu.sfsu.cs.csc867.msales.httpd.validation.HttpRequestInterpreterException;

/**
 * The VoctopusWebServer is the main class from the server. It spawns the threads of the clients on the pool and keeps
 * receiving the connections from them.
 * 
 * @author marcello Feb 15, 2008 1:23:03 PM
 */
public class VOctopusWebServer {

    static {
        final String VOCTOPUS_SERVER_ROOT = "VOCTOPUS_SERVER_ROOT";

        try {

            VOctopusConfigurationManager.getInstance().setServerRootPath(System.getenv(VOCTOPUS_SERVER_ROOT));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("YOU HAVE TO SET THE FOLLOWING ROOT DIRECTORY: " + VOCTOPUS_SERVER_ROOT);
            System.exit(0);
            // TODO: LOGGIN NEEDED
        } catch (IOException e) {
            System.out.println("I/O Error: verify if you can read the configuration. Verify the Logs file");
            // TODO: LOGGIN NEEDED
            System.exit(0);
        }
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
    private static Executor threadsPool = new ThreadPoolExecutor(10, 10, Long.MAX_VALUE, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(100), new ThreadPoolExecutor.DiscardOldestPolicy());

    /**
     * Hands the client connection, handling the Request and Response.
     * 
     * @param clientSocket
     * @throws HttpErrorException
     * @throws HttpRequestInterpreterException 
     */
    private static void handleClientConnection(Socket clientSocket) {
            
        RequestResponseMediator mediator;
        try {
            mediator = new RequestResponseMediator(new HttpClientConnection(clientSocket));
            mediator.sendResponse();
        } catch (HttpRequestInterpreterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        
        //            
        //            Request req = Request.buildNewRequest(clientSocket);
        //            req.print();
        //            Response res = Response.buildNewResponse(req);
        //            res.processRequest(clientSocket.getOutputStream());
    }

    /**
     * Prints the clients thread pool.
     */
    private static void printServerPoolStatus() {

        ThreadPoolExecutor pool = (ThreadPoolExecutor) threadsPool;
        System.out.println("Aquarium Pool [ " + pool.getCorePoolSize() + " , " + pool.getMaximumPoolSize() + " ]");
        System.out.println("The maximun number of threads executing on the aquarium: " + pool.getLargestPoolSize());
        System.out.println("# of open Connection " + pool.getActiveCount());
        System.out.println("# of open Connection " + pool.getMaximumPoolSize());
    }

    public static void main(String[] args) {

        String listeningPort = VOctopusConfigurationManager.WebServerProperties.HTTPD_CONF.getPropertyValue("Listen");

        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("V-Octopus Web Serving running, waiting connections on port " + listeningPort);

        try {
            ServerSocket socketServer = new ServerSocket(Integer.parseInt(listeningPort));
            while (true) {
                final Socket clientSocket = socketServer.accept();
                Runnable connectionHandler = new Runnable() {
                    public void run() {
                        long initial = System.currentTimeMillis();
                        handleClientConnection(clientSocket);
                        long end = System.currentTimeMillis();
                        System.out.println("Served " + clientSocket.getInetAddress().getHostAddress() + " in "
                                + (end - initial) + "ms");
                    }
                };

                printServerPoolStatus();
                threadsPool.execute(connectionHandler);
            }

        } catch (NumberFormatException nfe) {
            System.out
                    .println("The server port must be specified correctly. Check the configuration file for the Listen value");
            System.exit(0);
        } catch (IOException ioe) {
            // TODO generate output to the log files
            System.out.println("An unexpected error occurred with the server... please check the " + "the log file");
            System.out.println(ioe.getMessage());
            System.exit(0);
        }
    }
}