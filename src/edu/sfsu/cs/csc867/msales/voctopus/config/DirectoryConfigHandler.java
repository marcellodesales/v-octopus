package edu.sfsu.cs.csc867.msales.voctopus.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Parses the configuration of a directory
 * 
 * @author marcello Mar 5, 2008 10:49:57 PM
 */
public final class DirectoryConfigHandler {

    /**
     * The AuthType from the server.
     * 
     * @author marcello Mar 5, 2008 10:55:42 PM
     */
    public static enum AuthType {

        BASIC("Basic"), 
        ADVANCED("Advanced");
        
        private String value;
        
        private AuthType(String value) {
            this.value = value;
        }
        
        @Override
        public String toString() {
            return this.value;
        }
        
        public static AuthType getAuthType(String stringValue) {
            for (AuthType type : values()) {
                if (stringValue.equalsIgnoreCase(stringValue)) {
                    return type;
                } else {
                    continue;
                }
            }
            return null;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        return this.protectedDirectory.equals(((DirectoryConfigHandler)obj).protectedDirectory);
    }
    /**
     * The protected directory
     */
    private File protectedDirectory;
    /**
     * AuthName is the message
     */
    private String authName;
    /**
     * AuthType handler
     */
    private AuthType authType;
    /**
     * AuthUserFile handler where the passwords are stored
     */
    private File authUserFile;
    /**
     * The list of users and respective passwords from each of them. Must match both using the contains
     * username:password marcello:80nM/1quPJi12
     */
    private String[] usersPasswords;
    /**
     * A list of users "Require user csc667,yoon,wmac01" It can also be for groups Require Group newsGroup
     */
    private String require;
    /**
     * The authentication file for the group of users
     */
    private File authGroupFile;
    /**
     * The list of the allowed users
     */
    private String[] allowedUsers;
    /**
     * The list with the allowed group of users
     */
    private String[] allowedGroups;
    
    @Override
    public String toString() {
        return this.protectedDirectory.getAbsolutePath();
    }
    
    @Override
    public int hashCode() {
        return this.protectedDirectory.getAbsolutePath().hashCode();
    }
    
    /**
     * Creates a new DirectoryConfigHandler with the lines from the configuration file.
     * @param directoryLines
     * @throws IOException
     */
    public DirectoryConfigHandler(String[] directoryLines) throws IOException {
        
        for(String line : directoryLines) {
            line = line.replace("\"", "");
            if (line.contains("<Directory ")) {
                line = line.substring("<Directory ".length(), line.length()).trim();
                
                File theFile = new File(line);
                if (!theFile.exists()) {
                    throw new FileNotFoundException("Directory not found on configuration: " + line);
                }
                this.protectedDirectory = theFile;
            } else
            if (line.contains("AuthName ")) {
                this.authName = line.replace("AuthName ", "").trim();
            } else 
            if (line.contains("Require ")) {
                this.require = line.replace("Require ", "").trim();
            } else 
            if (line.contains("AuthType")) {
            this.authType = AuthType.getAuthType(line.replace("AuthType ", "").trim());
            } else
            if (line.contains("AuthUserFile ")) {
                String path = line.replace("AuthUserFile ", "").trim();
                File authFile = new File(path);
                if (!authFile.exists()) {
                    throw new FileNotFoundException("AuthUserFile not found on the system: " + authFile);
                }
                this.authUserFile = authFile;
                this.loadUSersFromFile();
            } else
                
            if (line.toLowerCase().contains("require ")) {
                
                if (line.contains("user")) {
                    this.allowedUsers = line.split(",");
                    this.allowedUsers[0] = this.allowedUsers[0].substring(this.allowedUsers[0].lastIndexOf(" ") + 1);
                    
                    for(String username : this.allowedUsers) {
                        if (username.contains(":")) {
                            throw new IllegalArgumentException("Username contains illegal character ':'" + username);
                        }
                    }
                    
                } else {
                    this.allowedGroups = line.split(",");
                    this.allowedGroups[0] = this.allowedGroups[0].substring(this.allowedGroups[0].lastIndexOf(" ") + 1);
                }
                
                this.require = line.replace("Require ", "").trim().replace(" ", "");
            }
            
        }
    }
    
    
    /**
     * Load the users and passwords from the file system
     * @throws IOException problems with opening and close the file.
     */
    private void loadUSersFromFile() throws IOException {
        
        BufferedReader configReader = new BufferedReader(new FileReader(this.authUserFile));
        String usrPwd = null;
        List<String> lines = new ArrayList<String>();
        
        while ((usrPwd = configReader.readLine()) != null) {
            if (usrPwd.equals("") || usrPwd.charAt(0) == '#') {
                continue;
            } else {
                String[] values = usrPwd.split(":");
                if (values.length > 2) {
                    throw new IllegalArgumentException("Wrong format of username/password on the .htpasswd: Must be  " +
                    		"username:password. Username nor password can't contain collon");
                }
                
                Pattern pp = Pattern.compile("[^:]*");
                if (pp.matcher(values[0]).matches()){
                    lines.add(usrPwd);
                } else {
                    throw new IllegalArgumentException("Wrong format of username/password on the .htpasswd: Must be  " +
                    "username:password. Username nor password can't contain collon");
                }
            }
        }
        this.usersPasswords = lines.toArray(new String[lines.size()]);
    }
    
    /**
     * @return The real information to be displayed in the browser
     */
    public String getAuthName() {
        return authName;
    }

    /**
     * @return The type of the authentication
     */
    public AuthType getAuthType() {
        return authType;
    }


    /**
     * @return The authentication file with the list of the users
     */
    public File getAuthUserFile() {
        return authUserFile;
    }


    /**
     * @return The authentication file for the group of users
     */
    public File getAuthGroupFile() {
        return authGroupFile;
    }


    /**
     * @return The restricted group of users to be allowed for this protected directory.
     */
    public String[] getAllowedUsers() {
        return allowedUsers;
    }


    /**
     * @return The list of allowed groups
     */
    public String[] getAllowedGroups() {
        return allowedGroups;
    }


    /**
     * @return The directory file to be protected.
     */
    public File getProtectedDirectory() {
        return protectedDirectory;
    }
    
    /**
     * @return the list of users and passwords located on the .htpasswd file represented by this handler.
     */
    public String[] getHtpasswdUsersPasswords() {
        return this.usersPasswords;
    }
    
    /**
     * @return The required users for this request.
     */
    public String getRequiredUsers() {
        return this.require;
    }
}