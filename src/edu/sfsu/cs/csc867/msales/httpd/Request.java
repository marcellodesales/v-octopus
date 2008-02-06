package edu.sfsu.cs.csc867.msales.httpd;

import java.io.BufferedReader;

import edu.sfsu.cs.csc867.msales.httpd.config.HttpdConf;

/**
 * <p>Title: Request.java</p>
 *
 * <p>Description: Used to store and process requests that come from the client
 * to the server. </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 * @author Tracie Hong and Luping May
 * @version 1.0
 */
public class Request {

  private String methodType;
private String URI;
private String query;
private String version;
private String body;

/**
   * Default constructor used to reset your variables and data structures for
   * each new incoming request.
   */
  public Request() {

  }

  /**
   * Parse the incoming request depending on the type of request you are
   * receiving. This information is found from the first line of the incoming
   * request. You will also want to check and make sure the request you are
   * receiving is a valid request. If the request is not valid, throw an error
   * using the http error codes.
   *
   * @param inMsg BufferedReader which grabs the incoming message from the
   *   client socket
   */
  public void parse(BufferedReader inMsg) {

  }

  /**
   * Used to first check whether a requested file path has an alias set within
   * the configuration file and if so, replaces the alias of the file or
   * directory with the real path. This way, the server can find the right file
   * in the tree.
   *
   * HINT: Remember that any one path can have multiple aliases found within the
   * httpd.conf file. For example, the URI
   * http://www.blah.net/blah/help/hello.html could have an alias where blah is
   * equivalent to http://www.blah.net/blah_blah_blah and help could be an alias
   * for http://www.blah.net/blah_blah_blah/bleh/help. Another thing to note is
   * that your URI could also include script aliases which means you may be
   * required to run a cgi script.
   *
   * @param config HttpdConf Object which contains all the information on how
   *   the server is configured.
   */
  public void setHttpdConf(HttpdConf config) {

  }
	//function to print out all the information from the request. Used for debugging
  /**
   * Print function used for debugging purposes. Helpful to make sure you are
   * parsing the incoming request properly.
   */
  public void print() {
    System.out.println("The method was " + methodType);
    System.out.println("The Request URL was " + URI);
    System.out.println("The query string was " + query);
    System.out.println("The HTTP version is " + version);
    System.out.println("The following headers were included:");
//    for (int i = 0; i < tags.size(); i++) {
//      System.out.println(tags.get(i) + ": " + header.get(tags.get(i)));
//    }
    System.out.println("The message body was: \n" + body);
  }

  /**
   * private function used by request object to parse the information passed
   * through from the client to the server and save it for future use. The type
   * of request can be found on this first line of the request.
   *
   * @param first String representation of the first line of the request from
   *   the client. Passed in as one long string which can easily be parsed.
   */
  private void parseFirstLine(String first) {

  }

  /**
   * private function used by the request object to determine whether an incoming
   * request is a valid request or not. Useful when throwing error messages.
   *
   * @return true if request is valid, false otherwise
   */
  private boolean checkRequest() {
	  return false;
  }

  /**
   * private function used by the request object to grab variables that may have
   * been passed to the server when the request was made. Remember that GET and
   * HEAD requests include their variables on the first line of the request while
   * POST and PUT requests include their variables within the body of the
   * message.
   */
  private void setVarFirstLine() {

  }

  /**
   * private function used by the request object to grab variables that may have
   * been passed to the server when the request was made. Remember that POST and
   * PUT requests include their variables within the body of the message and not
   * in the first line, so another method is needed to retrieve these variables.
   */
  private void setVarNotFirstLine() {

  }

  /**
   * private function used by the request object to parse the rest of the request
   * message (e.g. other headers and the body of the message) from the client so
   * it can be used later when actual processing of the request happens.
   *
   * @param inFile BufferedReader object that comes through the socket. Needs to
   *   be processed properly before the data stored within it can be used.
   */
  private void createRequest(BufferedReader inFile) {

  }
}
