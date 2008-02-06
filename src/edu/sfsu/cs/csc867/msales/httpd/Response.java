package edu.sfsu.cs.csc867.msales.httpd;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.Hashtable;


/**
 * <p>Title: Response.java</p>
 *
 * <p>Description: Used to process incoming requests to the server and generate
 * the appropriate response to that request. This is where most of the server
 * processing is done, thus making it a very important class to the
 * implementation of your server.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 * @author Tracie Hong and Luping May
 * @version 1.0
 */
public class Response {

  /**
   * Default constructor for the response object. Variables are reset and/or
   * intialized here. These variables will be used throughout request processing
   * and response generation.
   */
  public Response() {

  }

  /**
   * Used to process the request that came from the client to the server. There
   * are many things that need to be checked and verified during the processing
   * of a request. You will need to check for authentication, errors, cgi
   * scripts, type of request, etc, and handle each condition appropriately.
   *
   * HINT: it helps to use boolean flags throughout your code to check for the
   * various conditions that may or may not occur.
   *
   * @param myRequest Request object that was generated and stores all the
   *   request information that came in from the client
   * @param MIMETable Hashtable which contains the various mime types the
   *   server can handle.
   * @param env Environment object that contains the environment variables
   *   necessary for cgi script execution.
   */
  public void processRequest(Request myRequest, Hashtable MIMETable, Environment env) {

  }

  /**
   * Used to output a correctly formatted response to the client. This function
   * will need to process any output from a cgi script as well as generate the
   * appropriate headers and body required by an HTTP response.
   *
   * @param output OutputStream object that will be used to send the response
   *   back to the socket.
   */
  public void writeOutput(OutputStream output) {

  }

  /**
   * Used to test for authentication. If the .htaccess file shows that
   * authentication is needed for access to the file or directory then set the
   * appropriate headers and set the appropriate status codes unless the user
   * has included their authentication. If this is the case, check to make sure
   * their authentication is valid.
   *
   * @param req Request object which is needed to check for authentication
   */
  public void checkAuthentication(Request req){

  }

  /**
   * Used to set the reason for each HTTP status code as designated by the
   * protocol.
   *
   * @param code int value which corresponds to each status code
   */
  public void setStatus(int code) {

  }

  /**
   * Private function used to return the appropriate mime type for the file that
   * is being requested
   *
   * @param MIMETable Hashtable of mime types from your mime.types file
   * @param extension String value which designates the extension of the file
   *   being requested. This will be used to determine the mime type
   * @return String value that contains the mime type of the file
   */
  private String getMIME(Hashtable MIMETable, String extension) {
	  return null;
  }

  /**
   * Private function used to determine whether the mime type requested is a
   * valid mime type
   *
   * @param MIMETable Hashtable value of the available mime types as designated
   *   by the mime.types file
   * @param extension String value which consists of the extension type
   *   requested. Used to determine the correct mime type
   * @return true if mime type if valid, false otherwise
   */
  private boolean checkMIME(Hashtable MIMETable, String extension) {
	  return false;
  }

  /**
   * private function used when processing a request from the client. Here, you
   * will check for mime type validity and handle a put request if it is
   * requested. If the request is PUT, you will need to use the body of the
   * request to modify the existing file.
   *
   * @param MIMETable Hashtable that contains the valid mime types as
   *   determined by the mime.types file
   * @param body String value that contains the body of the request.
   */
  private void processWithExistence(Hashtable MIMETable, String body) {

  }

  /**
   * Private function specifically used to handle output from a cgi script. You
   * will need to check the header passed back from the cgi script to determine
   * the status code of the response. From there, add your headers, attach the
   * body and add any other server directives that need to be included.
   *
   * @param dataOut BufferedOutputStream object that will write to the client
   */
  private void processCGI(BufferedOutputStream dataOut) {

   }

  /**
   * Used to write the appropriate information to the log file.
   *
   * @param logPath String value which contains the location of your log file
   * @param host String value that contains the address of the client who made
   *   the request
   */
  public void writeToLog(String logPath, String host) {

   }
}