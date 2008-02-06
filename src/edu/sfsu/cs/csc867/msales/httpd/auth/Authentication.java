package edu.sfsu.cs.csc867.msales.httpd.auth;

/**
 * <p>Title: Authentication.java</p>
 *
 * <p>Description: Used when authentication of the user is needed before access
 * is given to certain files. This class will take the information submitted by
 * the user and check the .htaccess file to see if that user has access to the
 * file he/she is trying to view. Two main functions exist in this class. One
 * function to check if authentication is needed, and another to decode and
 * validate the authentication data once it has been received by the server.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * @author Luping May and Tracie Hong
 * @version 1.0
 */
public class Authentication {

  public Authentication() {
    /* dummy constructor */
  }

  /**
   * This function should check to see if the .htaccess file exists anywhere in
   * your directory structure. If it exists, grab the information out of the
   * file and save it somewhere for use.
   *
   * @param path The path to the working directory. It should not contain a
   *   file name at the end since we only want the directory the .htaccess file
   *   would be located in.
   * @return true if the .htaccess file is found and the data from it has been
   *   parsed out properly. Return false otherwise
   */
  public boolean authIsNeeded(String path){
	  return false;
  }

  /**
   * Checks the incoming information from the client against the user file to
   * see if the authentication information is correct. If it is correct, the
   * user can proceed, otherwise he or she is blocked and not allowed to access
   * files. This class uses the Base64Decoder class to check information.
   *
   * @param input String passed in through the header which is encoded. Use the
   *   Base64Decoder to decode this information so it can be used to check
   *   against in the user file.
   * @return true if data passed in matches what is in the user file, false
   *   otherwise
   */
  public boolean checkAuth(String input) {
	  return false;
  }
}
