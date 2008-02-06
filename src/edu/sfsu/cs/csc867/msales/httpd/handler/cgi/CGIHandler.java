package edu.sfsu.cs.csc867.msales.httpd.handler.cgi;
import java.io.BufferedInputStream;

import edu.sfsu.cs.csc867.msales.httpd.Environment;
import edu.sfsu.cs.csc867.msales.httpd.Request;


/**
 * <p>Title: CGIHandler.java</p>
 *
 * <p>Description: Used to handle cgi script requests. It will create a runnable
 * thread which will execute the script on the server and read the output from
 * that script, which will then be sent back to the client.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 * @author Luping May and Tracie Hong
 * @version 1.0
 */
public class CGIHandler {

  /**
   * Constructor for CGIHandler class. Used to save the variables necessary for
   * the cgi script to run properly and for the response to be sent back to the
   * client.
   *
   * @param rq Request variable which correlates to the request that was sent
   *   from the client to the server.
   * @param ev Environment variable used to get the state of the server
   *   environment. This information should have been saved when the server was
   *   configured.
   */
  public CGIHandler(Request rq, Environment ev) {

  }

  /**
   * Function used to execute the cgi script that was being requested by the
   * client.
   *
   * HINT: Try using the Runtime.getRuntime().exec() function to execute
   * the script. Also look into using both Data and Buffered Streams.
   *
   * @return BufferedInputStream which is the output from the script execution.
   *   This is sent back to the server and then back to the client.
   */
  public BufferedInputStream runScript() {
	  return null;
  }
}
