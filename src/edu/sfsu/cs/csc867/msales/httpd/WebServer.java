package edu.sfsu.cs.csc867.msales.httpd;

import java.io.*;
import java.util.*;
import java.net.*;

import edu.sfsu.cs.csc867.msales.httpd.config.HttpdConf;

/**
 * <p>Title: WebServer.java</p>
 *
 * <p>Description: This is the main class of the server. This is where
 * everything is instantiated and configured. Here is also where multithreading
 * of the server will occur.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 * @author Tracie Hong and Luping May
 * @version 1.0
 */
public class WebServer {

  /**
   * Here you will create your server, configure all the settings and listen to
   * the port designated within your configuration. Whenever a request comes in,
   * you will need to process that request appropriately and respond as needed.
   *
   * @param args String[]
   */
  public static void main(String[] args) {
    
  }

  /**
   * Private class used for multithreading
   */
  class clientThread extends Thread {
    /**
     * Constructor used to start a thread.
     *
     * @param incoming Socket value which designates the socket used by the client
     * @param hcf HttpdConf object created upon server startup.
     */
    public clientThread(Socket incoming, HttpdConf hcf) {

    }

    /**
     * Used to run your server thread. Here is where you will be processing all
     * requests and returning your responses.
     */
    public void run() {

    }
  }
}