package edu.sfsu.cs.csc867.msales.voctopus.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
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

    private File authGroupFile;
    private List<String> userGroups;
    private List<String> users;

    private String[] allowedUsers;
    private String[] allowedGroups;
    
    public DirectoryConfigHandler(String[] directoryLines) throws IOException {
        
        for(String line : directoryLines) {
            line = line.replace("\"", "");
            if (line.contains("<Directory ")) {
                String path = line.substring("<Directory ".length(), line.length() - 1).trim();
                
                if (!path.contains(VOctopusConfigurationManager.getInstance().getDocumentRoot())) {
                    path = VOctopusConfigurationManager.getInstance().getDocumentRoot() + path;
                }
                File theFile = new File(path);
                if (!theFile.exists()) {
                    throw new FileNotFoundException("Directory not found on configuration: " + path);
                }
                this.protectedDirectory = theFile;
            } else
            if (line.contains("AuthName ")) {
                this.authName = line.replace("AuthName ", "").trim();
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
     * @throws IOException
     */
    private void loadUSersFromFile() throws IOException {
        
        BufferedReader configReader = new BufferedReader(new FileReader(this.authUserFile));
        String usrPwd = null;
        List<String> lines = new ArrayList<String>();
        
        while ((usrPwd = configReader.readLine()) != null) {
            if (usrPwd.equals("") || usrPwd.charAt(0) == '#') {
                continue;
            } else {
                Pattern p = Pattern.compile("[^:]:.");
                if (p.matcher(usrPwd).matches()){
                    lines.add(usrPwd);
                } else {
                    //throw new IllegalArgumentException("The username and password must match the format username:password
                    //and username can't contain collon ":");
                }
            }
        }
        this.usersPasswords = lines.toArray(new String[lines.size()]);
    }

    /**
     * Generates the MD5 hash from a given string.
     * @param msg
     * @return
     */
    private String getHash(String msg) {
        byte buf[] = msg.getBytes();
        StringBuffer hexString = new StringBuffer();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(buf);
            byte[] digest = algorithm.digest();
            for (int i = 0; i < digest.length; i++) {
                hexString.append(this.pad(Integer.toHexString(0xFF & digest[i]), 2));
        
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hexString.toString();
    }
    
    /**
     * @param i
     * @param l
     * @return the modified version of the string based on l
     */
    private String pad(String i, int l) {
        while (i.length() < l) {
            i = '0' + i;
        }
        return i;
    }
    
    public static void main(String[] args) throws IOException {
        System.out.println(new DirectoryConfigHandler(null).getHash(""));
    }


    public String getAuthName() {
        return authName;
    }


    public AuthType getAuthType() {
        return authType;
    }


    public File getAuthUserFile() {
        return authUserFile;
    }


    public File getAuthGroupFile() {
        return authGroupFile;
    }


    public String[] getAllowedUsers() {
        return allowedUsers;
    }


    public String[] getAllowedGroups() {
        return allowedGroups;
    }


    public File getProtectedDirectory() {
        return protectedDirectory;
    }

}
