package edu.sfsu.cs.csc867.msales.httpd;
import java.net.Socket;

import edu.sfsu.cs.csc867.msales.httpd.config.HttpdConf;


/**
 * <p>Title: Environment.java</p>
 *
 * <p>Description: Helper class to save all the environment variables that are
 * needed to ensure proper configuration and execution of CGI scripts
 * </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author Luping May and Tracie Hong
 * @version 1.0
 */
public class Environment {

  /**
   * Constructor for Environment object. Saves the information from the server
   * configuration file as well as from the request into a data structure so if
   * the request is for a cgi script, it can be executed properly.
   *
   * @param c HttpdConf file which contains some variables which need to be
   *   saved in order for cgi scripts to execute properly.
   * @param r Request object which contains request specific variables needed
   *   to execute cgi scripts
   * @param cl Socket used by the client to connect to the server
   */
  public Environment(HttpdConf c, Request r, Socket cl) {

  }

  /**
   * Function to save static variables into your data structure which can be
   * then used during script execution. Variables that can also be accessed
   * directly from the request object or the socket (objects passed in during
   * instantiation) can be saved here as well.
   * HINT: Most request variables that need to be saved need to have an HTTP_
   * variable name so take this into consideration when planning how to design
   * and save environment variables.
   */
  public void setEnv() {

  }

  /**
   * Function to return a data structure that contains all the environment
   * variables that were saved.
   *
   * @return String[]. Chose as data type upon return due to parsing of variable
   *   content and name which both need to be stored.
   */
  public String[] getEnv() {
	  return null;
  }

  /**
   * Used to add another variable to the data structure. Usually used when the
   * data has not be stored yet through the setEnv function.
   *
   * @param var String that contains both the variable name and data. Used this
   *   way for parsing purposes later on.
   */
  public void addEnvVar(String var) {

  }
}