package com.googlecode.voctopus.request.handler;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.voctopus.RequestResponseMediator.ReasonPhase;
import com.googlecode.voctopus.config.VOctopusConfigurationManager;


/**
 * Lists a given directory's files in the system.
 * 
 * @author marcello Feb 24, 2008 8:34:25 AM
 */
public class DirectoryContentRequestHandlerStrategy extends AbstractRequestHandler {

    private static final String RESPONSE_DATE_FORMAT = "MMM-dd-yyyy kk:mm";

    public DirectoryContentRequestHandlerStrategy(URI uri, File requestedFile, ReasonPhase status) {
        super(uri, requestedFile, RequestType.ASCII, "directory");
        this.status = status;
    }

    private static final StringBuilder builder = new StringBuilder();

    static {
        builder.append("<html><head>\n");
        builder.append("<meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\">\n");
        builder.append("<title>Index of $REQUESTED_DIRECTORY</title>\n");
        builder.append("<style><!--\n");
        builder.append("body {font-family: arial,sans-serif}\n");
        builder.append("div.nav {margin-top: 1ex}\n");
        builder.append("div.nav A {font-size: 10pt; font-family: arial,sans-serif}\n");
        builder.append("span.nav {font-size: 10pt; font-family: arial,sans-serif; font-weight: bold}\n");
        builder.append("div.nav A,span.big {font-size: 12pt; color: #0000cc}\n");
        builder.append("div.nav A {font-size: 10pt; color: black}\n");
        builder.append("A.l:link {color: #6f6f6f}\n");
        builder.append("A.u:link {color: green}\n");
        builder.append("//--></style>\n");
        builder.append("</head>\n");
        builder.append("<body text=#000000 bgcolor=#ffffff>\n");
        builder.append("<table border=0 cellpadding=2 cellspacing=0 width=100%><tr><td rowspan=3 width=1% nowrap>\n");
        builder.append("<b><font face=times color=#0039b6 size=10>v</font><font face=times color=#c41200 size=10>O"
                + "</font><font face=times color=#f3c518 size=10>c</font><font face=times color=#0039b6 size=10>t"
                + "</font><font face=times color=#30a72f size=10>o</font><font face=times color=#c41200 size=10>p"
                + "</font><font face=times color=#f3c518 size=10>u</font><font face=times color=#30a72f size=10>s"
                + "</font>&nbsp;&nbsp;</b>\n");
        builder.append("\n");
        builder.append("<td>&nbsp;</td></tr>\n");
        builder.append("<tr><td bgcolor=#3366cc><font face=arial,sans-serif color=#ffffff>\n");
        builder.append("<b>Server running...</b></td></tr>\n");
        builder.append("<tr><td>&nbsp;</td></tr></table>\n");
        builder.append("<blockquote>\n");
        builder.append("<H1>Index of $REQUESTED_DIRECTORY</H1>\n");
        builder.append("<TABLE width=\"40%\">\n");

        builder.append("   $LISTING_LINES\n");

        builder.append("</TABLE><p>\n");
        builder.append("</blockquote>\n");
        builder.append("<table width=100% cellpadding=0 cellspacing=0><tr><td bgcolor=#3366cc></td></tr></table>\n");

        builder.append("$SERVERNAME_DOMAIN_PORT_INFO</body></html>\n");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.googlecode.voctopus.request.handler.HttpRequestHandler#getResourceLines()
     */
    public String[] getResourceLines() throws IOException {

        List<String> files = new ArrayList<String>();

        files.add("<tr><th><a href=\"?C=N;O=D\">Name</a></th>\n");
        files.add("<th><a href=\"?C=M;O=A\">Last modified</a></th>\n");
        files.add("<th align=\"right\"><a href=\"?C=S;O=A\">Size</a></th>\n");
        files.add("<tr><th colspan=\"3\"><hr></th></tr>\n");

        File currentFile = getRequestedFile();
        URI uri = this.getRequestedResource();
        String parentPath = this.getRequestedFile().getParentFile().getAbsolutePath();
        String documentRootPath = VOctopusConfigurationManager.getInstance().getDocumentRootPath();
        
        if (!parentPath.equals(documentRootPath) && parentPath.length() > documentRootPath.length()) {
            files.add("<tr>\n");
            files.add("<td width=\"50%\"><img src=\"/icons/back.gif\" alt=\"Parent Directory\" border=\"0\">"
                    + " <a href=\"../\">Parent Directory</a></td>\n");
            files.add("<td width=\"40%\" align=\"right\">"
                    + new SimpleDateFormat(RESPONSE_DATE_FORMAT).format(currentFile.lastModified()) + "</td>\n");
            files.add("<td width=\"10%\" align=\"right\">=</td></tr>\n");
        }

        String extension = "", link = "";
        // this time, the file that comes is the dirs.html file, but this time we are going to get the list of the
       // files.
        for (File file : currentFile.listFiles()) {

            files.add("<tr>\n");
            if (file.isDirectory()) {
                files.add("<td width=\"50%\"><img src=\"/icons/folder.gif\" alt=\"" + file.getName() + "\">");
            } else {
                int x = file.getName().lastIndexOf(".");
                if (x != -1) {
                    extension = file.getName().substring(x);
                }
                String contentType = null;
                if (this.contentType.indexOf("/") > -1) {
                    contentType = this.contentType.substring(0, this.contentType.indexOf("/"));
                }
                URI icon = VOctopusConfigurationManager.getInstance().getIcon(extension, contentType);

                files.add("<td width=\"50%\"><img src=\"" + icon.getPath() + "\" alt=\"" + file.getName() + "\">");
            }

            link = uri.getPath() + (uri.getPath().endsWith("/") ? "" : "/");
            link = link + file.getName() + (file.isDirectory() ? "/" : "");
            files.add("<a href=\"" + link + "\">" + file.getName() + "</a></td>\n");
            files.add("<td width=\"40%\" align=\"right\">"
                    + new SimpleDateFormat(RESPONSE_DATE_FORMAT).format(file.lastModified()) + "</td>\n");
            files.add("<td width=\"10%\" align=\"right\">" + (file.isDirectory() ? "=" : file.length())
                    + "</td></tr>\n");
        }

        StringBuilder listingsBuilder = new StringBuilder();
        for (String line : files) {
            listingsBuilder.append(line);
        }

        if (this.requestedResourceExists()) {
            System.out.println("serving the directory " + this.getRequestedFile());

            String nameDomainPort = VOctopusConfigurationManager.getInstance().getServerVersion() + " Server at "
                    + VOctopusConfigurationManager.getInstance().getServerName() + " Port "
                    + VOctopusConfigurationManager.getInstance().getServerPort();
            String line = builder.toString();
            line = line.replace("$REQUESTED_DIRECTORY", this.getRequestedResource().getPath());
            line = line.replace("$LISTING_LINES", listingsBuilder.toString());
            line = line.replace("$SERVERNAME_DOMAIN_PORT_INFO", nameDomainPort);

            return line.split("\n\r");
        } else {
            return new String[] { "" };
        }
    }

    public String[] getParticularResponseHeaders() {
        // TODO Auto-generated method stub
        return null;
    }
}
