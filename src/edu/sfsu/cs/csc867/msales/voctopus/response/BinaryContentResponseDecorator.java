package edu.sfsu.cs.csc867.msales.voctopus.response;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest;

/**
 * @author marcello Feb 16, 2008 11:02:53 AM
 */
public class BinaryContentResponseDecorator extends AbstractHttpResponse {

    public BinaryContentResponseDecorator(HttpRequest originatingRequest) {
        super(originatingRequest);
        // TODO Auto-generated constructor stub
    }

    public void sendHeader(PrintWriter writer) {
        // writer.println(this.getResponseHeader());
        //        
        // String[] more = new String[] {
        // "Content-Type: " + this.getRequest().getContentType(),
        // "Last-Modified: " + LogFormats.HEADER_DATE_TIME.format(this.getLastModified(this.getRequest().getStatus())),
        // "Expires: " + new SimpleDateFormat(RESPONSE_DATE_FORMAT).format(new Date(2038,1,1)),
        // "Server: " + VOctopusConfigurationManager.getInstance().getServerVersion(),
        // "Content-Length: " + this.getRequestSize(),
        // "Date: " + LogFormats.HEADER_DATE_TIME.format(new Date())
        // };
        // for (String headerVar : more) {
        // writer.println(headerVar);
        // }
    }

    public void sendBody(OutputStream outputStream, PrintWriter writer) throws IOException {
        int BUF_LEN = 2048;
        byte[] inBuffer = new byte[BUF_LEN];
        FileInputStream in = new FileInputStream(this.getRequest().getRequestedResource());
        int bytesRead = 0;
        while ((bytesRead = in.read(inBuffer)) > 0) {
            outputStream.write(inBuffer, 0, bytesRead);
        }
    }
}
