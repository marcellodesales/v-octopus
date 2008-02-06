package edu.sfsu.cs.csc867.msales.httpd.config;

/**
 * <p>Title: HttpdConf.java</p>
 *
 * <p>Description: This class will configure your server to the specifications
 * found within the httpd.conf file. </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 * @author Luping May and Tracie Hong
 * @version 1.0
 */
public class HttpdConf {

  private static final String SERVER_ROOT = null;
private static final String DOCUMENT_ROOT = null;
private static final String PORT = null;
private static final String LOG_FILE = null;
private static final String ACCESS_FILE_NAME = null;
private static final String SCRIPT_ALIAS = null;

/**
   * Default constructor to reset your variables and data structures.
   */
  public HttpdConf(){

  }

  /**
   * Reads in a httpd.conf file, parses it and saves the data stored within that
   * file. This allows for proper configuration of your server since the
   * information stored in your configuration file should allow for your server
   * to function.
   *
   * @param path path to your httpd.conf file
   */
  public void readHttpd(String path){

  }

  /**
   * Function to convert aliases set within your httpd.conf file to their
   * absolute path. This allows for aliases to be found on your server and
   * returned back to the client.
   * HINT: You may find it helpful to create a private class to store your
   * aliases.
   *
   * @param fakeName String which contains the alias of the file or directory
   * @return String value which contains the absolute path to the file or
   *   directory as determined within the httpd.conf file
   */
  public String solveAlias(String fakeName){
	  return null;
  }

  /**
   * Used to read the mime.types file and save all the information from that file
   * into a data structure that can be used to validate file types when
   * generating response messages.
   *
   * @param path String value of path to mime.types file
   */
  public void readMIME (String path) {

  }

  /**
   * Helper function to determine whether the name of a file or directory is an
   * alias for another file or directory as noted in the httpd.conf file.
   *
   * @param name String value of the alias we want to check to determine
   *   whether it is or is not an alias for another file or directory
   * @return true if it is an alias, false otherwise
   */
  public boolean isScript(String name)	{
	  return false;
  }

  /**
   * Helper function to see if you've parsed your httpd.conf file properly. Used
   * for debugging purposes.
   */
  public void testPrint(){
    System.out.println("ServerRoot: " + SERVER_ROOT);
    System.out.println("DocumentRoot: " + DOCUMENT_ROOT);
    System.out.println("ListenPort: "+ PORT);
    System.out.println("LogFile: " + LOG_FILE);
    System.out.println("AccessFileName: " + ACCESS_FILE_NAME);
    System.out.println("ScriptAlias: " + SCRIPT_ALIAS + " " + solveAlias(SCRIPT_ALIAS));
  }
}
