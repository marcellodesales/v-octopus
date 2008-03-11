package edu.sfsu.cs.csc867.msales.httpd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Company: V-Ocean, Inc.
 * </p>
 * 
 * @author Marcello de Sales (marcellosales@acm.org)
 * @version 0.1
 */
public class VOctopusWebServer {

    public static final Properties serverProp = new Properties();
    public static Properties aliasesProp = new Properties();
    
    static {
        final String VOCTOPUS_SERVER_ROOT = "VOCTOPUS_SERVER_ROOT";
        
        //TODO: Change this property to the configuration file....
        System.setProperty(VOCTOPUS_SERVER_ROOT, "/home/marcello/development/workspace-sfsu/voctopusHttpd");

        try {
            
            File configFile = new File(System.getProperty(VOCTOPUS_SERVER_ROOT) + "/conf/httpd.conf");
            BufferedReader configReader = new BufferedReader(new FileReader(configFile));
            String configProperty = null;
            String[] vals;
            while ((configProperty = configReader.readLine()) != null) {
                if (configProperty.charAt(0) == '#') {
                    continue;
                } else
                if (configProperty.contains("\"")) {
                    configProperty = configProperty.replace("\"","");
                }
                vals = configProperty.split(" ");
                if (vals.length == 2) {
                    String serverRoot = System.getProperty(VOCTOPUS_SERVER_ROOT);
                    vals[1] = vals[1].trim().replace("$VOCTOPUS_SERVER_ROOT", serverRoot);
                    serverProp.setProperty(vals[0], vals[1]);
                } else {
                    aliasesProp.setProperty(vals[1].trim(), vals[2].trim());
                }
            }
            
        } catch (FileNotFoundException e) {
            System.out.println("YOU HAVE TO SET THE FOLLOWING ROOT DIRECTORY");
            System.out.println(VOCTOPUS_SERVER_ROOT);
            //TODO: LOGGIN NEEDED
        } catch (IOException e) {
            System.out.println("Error occurred while starting the server... Check the logs");
          //TODO: LOGGIN NEEDED
        }
    }
    
	/**
	 * The pool of threads will be used to control the requests to the server. If has the following
	 * attributes: 
	 * <BR><li>corePoolSize is the number of threads that are allowed to be in the pool even if they
	 * are in idle;
	 * <BR><li>maximumPoolSize is the maximum number of threads allowed in the pool;
	 * <BR><LI>keepAliveTime and timeUnit are used to indicate how long new threads will wait when the
	 * the number of threads in the pool are greater than the corePoolSize;
	 * <BR><LI>workQueue is the queue that will hold the runnables before they execute;
	 * <br><li>retentionPolicy is the policy used to remove the tasks.
	 */
	private static Executor threadsPool = new ThreadPoolExecutor(10, 10,
			Long.MAX_VALUE, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>(100),
			new ThreadPoolExecutor.DiscardOldestPolicy());

	/**
	 * Prints the clients thread pool.
	 */
	private static void printServerPoolStatus() {

		ThreadPoolExecutor pool = (ThreadPoolExecutor) threadsPool;
		System.out.println("Aquarium Pool [ " + pool.getCorePoolSize() + " , "
				+ pool.getMaximumPoolSize() + " ]");
		System.out.println("The maximun number of threads executing on the aquarium: "
						+ pool.getLargestPoolSize());
		System.out.println("# of open Connection " + pool.getActiveCount());
		System.out.println("# of open Connection " + pool.getMaximumPoolSize());
	}

	/**
	 * Hands the client connection, handling the Request and Response.
	 * @param clientSocket
	 * @throws HttpErrorException
	 */
	private static void handleClientConnection(Socket clientSocket) throws HttpErrorException {
		try {

			Request req = Request.buildNewRequest(clientSocket);
			req.print();
			Response res = Response.buildNewResponse(req);
			res.processRequest(clientSocket.getOutputStream());
			
		} catch (IOException ioe) {
			throw HttpErrorException.buildNewException(ioe);
		}
	}
	
	/**
	 * Here you will create your server, configure all the settings and listen
	 * to the port designated within your configuration. Whenever a request
	 * comes in, you will need to process that request appropriately and respond
	 * as needed.
	 * 
	 * All the configuration should be given through the main method, either by the configuration file
	 * (by default httpd.conf is searched).
	 * 
	 * @param args String[]
	 */
	public static void main(String[] args) {
		try {
			ServerSocket socketServer = new ServerSocket(Integer.parseInt(serverProp.getProperty("Listen")));
			while (true) {
			    System.out.println();
			    System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			    System.out.println("V-Octopus Web Serving running, waiting connections on port " + serverProp.getProperty("Listen"));
				final Socket clientSocket = socketServer.accept();
				Runnable connectionHandler = new Runnable() {
					public void run() {
						try {
						    long initial = System.currentTimeMillis();
							handleClientConnection(clientSocket);
							long end = System.currentTimeMillis();
							System.out.println("Served " + clientSocket.getInetAddress().getHostAddress() 
							        + " in " + (end-initial) + "ms");
						} catch (HttpErrorException e) {
						    e.printStackTrace();
						}
					}
				};
				
				printServerPoolStatus();
				threadsPool.execute(connectionHandler);
			}

		} catch (IOException e) {
			// TODO generate output to the log files
			e.printStackTrace();
			System.out.println("An unexpected error occurred with the server... please check the " +
					"the log file at " );
			System.exit(0);
		}
	}
}