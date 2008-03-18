package edu.sfsu.cs.csc867.msales.voctopus.config;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handler for the trees of protected directories.
 * @author marcello
 * Mar 11, 2008 1:21:01 PM
 */
public class ProtectedDirectoryTree {

    private List<DirectoryConfigHandler> protectedDirectories = new ArrayList<DirectoryConfigHandler>();
    
    /**
     * Adds a new protected directory to the tree
     * @param protectedDirectory is an instance of a protected directory.
     */
    public void add(DirectoryConfigHandler protectedDirectory) {
        this.protectedDirectories.add(protectedDirectory);
    }
    
    /**
     * Verify if a given URI is protected
     * @param uri the requested URI
     * @return if the given URI is protected. It can return 2 different values:
     * <li>{@link DirectoryConfigHandler} = If the directory is protected;
     * <li>null = in case the directory is free to be displayed.
     */
    public DirectoryConfigHandler isDirectoryProtected(URI uri, URI parent) {
        
        if (parent != null && parent.getPath().equals("/")) {
            return null;
        }
        
        DirectoryConfigHandler found = null;
        for(DirectoryConfigHandler protectDir : this.protectedDirectories) {
            
            URI relativeURI = this.getRelativeURI(protectDir.getProtectedDirectory());
            if (relativeURI != null && relativeURI.getPath().contains(uri.getPath())) {
                return protectDir;
            } else {
                found = this.isDirectoryProtected(uri, this.getRelativeURI(protectDir.getProtectedDirectory().getParentFile()));
                if (found == null) {
                    continue;
                } else {
                    return found;
                }
            }
        }
        return null;
    }
    
    /**
     * @param requestedDirectory is the requested directory file.
     * @return the relative URI from the file system directory file.
     */
    public URI getRelativeURI(File requestedDirectory) {
        String documentRoot = VOctopusConfigurationManager.getInstance().getDocumentRootPath();
        try {
            return new URI(requestedDirectory.getAbsolutePath().replace(documentRoot, "") + "/");
        } catch (URISyntaxException e) {
            return null;
        }
    }
    
    /**
     * @param file is the given directory.
     * @return if the given file is a protected directory.
     */
    public boolean containsFilePath(File file) {
        boolean found = false; 
        for(DirectoryConfigHandler dirConf : this.protectedDirectories) {
            if (dirConf.toString().equals(file.getAbsolutePath())) {
                found = true;
                break;
            } else continue;
        }
        return found;
    }
    
    /**
     * @return The size of the number of directories.
     */
    public int size() {
        return this.protectedDirectories.size();
    }
}
