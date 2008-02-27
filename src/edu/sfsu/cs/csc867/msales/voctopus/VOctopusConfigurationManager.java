package edu.sfsu.cs.csc867.msales.voctopus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The management of the configuration files from the server. This sinpleton controls must load the following files:
 * <li>Load httpd.conf file as the main configuration resource.
 * <li>Load mime.types values
 * 
 * @author marcello Feb 14, 2008 12:57:13 PM
 */
public final class VOctopusConfigurationManager {

    private static final String VOCTOPUS_VERSION = "vOctopus 0.2";

    /**
     * TheradLocal instance for this singleton.
     */
    private static ThreadLocal<VOctopusConfigurationManager> singleton = new ThreadLocal<VOctopusConfigurationManager>() {
        @Override
        protected VOctopusConfigurationManager initialValue() {
            // TODO Auto-generated method stub
            return new VOctopusConfigurationManager();
        }
    };

    /**
     * Private constructor for the singleton.
     */
    private VOctopusConfigurationManager() {
    }

    /**
     * @return the unique instance of this class.
     */
    public static VOctopusConfigurationManager getInstance() {
        return singleton.get();
    }

    /**
     * The root directory for the server.
     */
    private String serverRootPath;
    
    /**
     * It is the alias reserved for the execution of cgi-bin scripts (python, perl, ruby...)
     */
    private static String[] scriptAlias = new String[2];

    private static String[] wsAlias = new String[2];

    /**
     * All the Properties from the server.
     * 
     * @author marcello Feb 15, 2008 12:21:13 PM
     */
    public static enum WebServerProperties {
        /**
         * The main configuration file that must exist before the execution.
         */
        HTTPD_CONF("httpd.conf"),
        
        /**
         * Complete list of aliases from the httpd.conf file. 
         *     #ScriptAlias: This controls which directories contain server scripts.
         *     # ScriptAliases are essentially the same as Aliases, except that
         *     # documents in the realname directory are treated as applications and
         *     # run by the server when requested rather than as documents sent to the client.
         *     # The same rules about trailing "/" apply to ScriptAlias directives as to
         *     # Alias.
         *     #
         */
        ALIAS("httpd.conf"),
        /**
         * The mime types mappings from the file.
         */
        MIME_TYPES("mime.types");

        /**
         * It is the name of the file
         */
        private String fileName;
        /**
         * The httpd.conf properties as key values. Aliases are stored on the aliases map.
         */
        private Map<String, String> serverProps;
        /**
         * The mime types properties. It can contain null as the value in case it doesn't have a value.
         */
        private Map<String, String> mimeTypesProps;
        /**
         * The aliases from the httpd.conf file are stored on the form of Alias/Value
         */
        private Map<String, String> aliasesProps;

        /**
         * Constructs a new WebServerProperties with the name of the file defined on the enum constant.
         * 
         * @param fileName is the name of the file only. The complete system path should be provided by the system.
         */
        private WebServerProperties(String fileName) {
            this.fileName = fileName;
        }

        /**
         * @return the properties for a given WebServerProperty enum.
         */
        public Map<String, String> getProperties() {
            switch (this) {
            case HTTPD_CONF:
                if (this.serverProps == null) {
                    this.serverProps = new HashMap<String, String>();
                }
                return this.serverProps;

            case ALIAS:
                if (this.aliasesProps == null) {
                    this.aliasesProps = new LinkedHashMap<String, String>();
                }
                return this.aliasesProps;

            case MIME_TYPES:
                if (this.mimeTypesProps == null) {
                    this.mimeTypesProps = new LinkedHashMap<String, String>();
                }
                return this.mimeTypesProps;
            default:
                return null;
            }
        }

        /**
         * @param key is the key that identifies a value on the configuration files.
         * @return the value of the key from the configuration file instance of the enumeration.
         */
        public String getPropertyValue(String key) {
            return this.getProperties().get(key);
        }

        /**
         * @return the complete file path for the configuration file.
         */
        public String getFilePath() {
            return VOctopusConfigurationManager.getInstance().serverRootPath + this.fileName;
        }
    }

    /**
     * @return The root path for the web server.
     */
    public String getServerRootPath() {
        return serverRootPath;
    }
    
    /**
     * @return The document root of the server.
     */
    public String getDocumentRoot() {
        return WebServerProperties.HTTPD_CONF.serverProps.get("DocumentRoot");
    }
    
    /**
     * @return The default path for the cgi scripts from the url.
     */
    public static String getDefaultCGIPath() {
        return scriptAlias[0];
    }
    
    /**
     * @return The directory file where CGI scripts must be located based on the script alias configuration.
     */
    public static File getCGIServerPath() {
        return new File(scriptAlias[1]);
    }

    /**
     * @return The default path for the web services jars from the url.
     */
    public static String getDefaultWebservicesPath() {
        return wsAlias[0];
    }
    
    /**
     * @return The directory file where web services jars must be located based on the web services alias configuration.
     */
    public static File getWebServicesServerPath() {
        return new File(wsAlias[1]);
    }
    
    /**
     * Sets the path of the root to the server
     * 
     * @param serverRootPath is the complete path to the webserver root folder.
     * @throws IOException in case an error occurs or if the configuration file does not exist.
     */
    public void setServerRootPath(String serverRootPath) throws IOException {

        File configFile = new File(serverRootPath + "/conf/httpd.conf");
        BufferedReader configReader = new BufferedReader(new FileReader(configFile));
        this.serverRootPath = serverRootPath;
        String configProperty = null;
        String[] vals;
        while ((configProperty = configReader.readLine()) != null) {
            if (configProperty.charAt(0) == '#' || configProperty.equals("")) {
                continue;
            } else if (configProperty.contains("\"")) {
                configProperty = configProperty.replace("\"", "");
            }
            vals = configProperty.replace("$VOCTOPUS_SERVER_ROOT", serverRootPath).split(" ");
            if (vals.length == 2) {
                vals[1] = vals[1];
                WebServerProperties.HTTPD_CONF.getProperties().put(vals[0].trim(), vals[1].trim());
            } else {
             
                if (vals[0].equals("ScriptAlias")) {
                    scriptAlias[0] = vals[1];
                    scriptAlias[1] = vals[2];
                } else
                if (vals[0].equals("WebServices")) {
                    wsAlias[0] = vals[1];
                    wsAlias[1] = vals[2];
                } 
                WebServerProperties.ALIAS.getProperties().put(vals[0].trim(), vals[1].trim());
            }
        }
        configReader.close();
        this.setMimeTypes(serverRootPath);
        this.serverRootPath = serverRootPath;
    }

    /**
     * Reads the mime types configuration file.
     * 
     * @param serverRootPath is the server root path that is set on the httpd.conf file. The var is "ServerRoot"
     * @throws IOException if any problem while reading the file occur. Usually when it's not found or no access
     *             privileges.
     */
    private void setMimeTypes(String serverRootPath) throws IOException {
        File mimeTypes = new File(serverRootPath + "/conf/mime.types");
        BufferedReader mimeReader = new BufferedReader(new FileReader(mimeTypes));

        String mimeTypesProperty = null;
        String[] vals;
        while ((mimeTypesProperty = mimeReader.readLine()) != null) {
            if (mimeTypesProperty.equals("") || mimeTypesProperty.charAt(0) == '#') {
                continue;
            }
            if (mimeTypesProperty.contains("\t")) {
                vals = mimeTypesProperty.trim().split("\t");
                if (vals.length > 2) {
                    for (int ind = 2; ind < vals.length; ind++) {
                        if (!vals[ind].equals("") && (vals[1].equals(""))) {
                            vals[1] = vals[ind];
                        } 
                    }
                }
                WebServerProperties.MIME_TYPES.getProperties().put(vals[0].trim(), vals[1]);
            } else {
                WebServerProperties.MIME_TYPES.getProperties().put(mimeTypesProperty, "");
            }
        }
        mimeReader.close();
    }

    public String getServerVersion() {
        return VOCTOPUS_VERSION;
    }
    
    public String getServerName() {
        return WebServerProperties.HTTPD_CONF.getPropertyValue("ServerName");
    }

    public String getServerPort() {
        return WebServerProperties.HTTPD_CONF.getPropertyValue("Listen");
    }
}