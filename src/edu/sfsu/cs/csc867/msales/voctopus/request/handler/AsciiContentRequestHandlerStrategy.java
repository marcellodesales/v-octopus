package edu.sfsu.cs.csc867.msales.voctopus.request.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;

import edu.sfsu.cs.csc867.msales.voctopus.RequestResponseMediator.ReasonPhrase;

/**
 * Handler for ASCII-based files.
 * 
 * @author marcello Feb 11, 2008 4:16:43 PM
 */
public class AsciiContentRequestHandlerStrategy extends AbstractRequestHandler {

    /**
     * Creates a new handler for ASCII based content
     * 
     * @param uri
     * @param requestedFile
     * @param handlerFound
     * @param reasonPhrase
     */
    public AsciiContentRequestHandlerStrategy(URI uri, File requestedFile, String handlerFound,
            ReasonPhrase reasonPhrase) {
        super(uri, requestedFile, RequestType.ASCII, handlerFound);
        this.status = reasonPhrase;
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.sfsu.cs.csc867.msales.voctopus.request.handler.HttpRequestHandler#getResourceLines()
     */
    public String[] getResourceLines() throws IOException {

        if (this.requestedResourceExists()) {
            System.out.println("serving the file " + this.getRequestedFile());
            FileChannel channel = new FileInputStream(this.getRequestedFile()).getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, this.getRequestedFile().length());

            Charset charset = Charset.forName("ISO-8859-1");
            CharsetDecoder decoder = charset.newDecoder();
            CharBuffer charBuffer = decoder.decode(buffer);
            List<String> lines = new ArrayList<String>();

            StringBuilder builder = new StringBuilder();

            // in case there's no response, the reason phase is 204
            if (charBuffer.length() == 0) {
                this.setStatus(ReasonPhrase.STATUS_204);
                return new String[] { "" };
            }

            for (int i = 0, n = charBuffer.length(); i < n; i++) {

                char charValue = charBuffer.get();
                if (charValue != '\n') {
                    builder.append(charValue);
                } else {
                    lines.add(builder.toString());
                    builder.delete(0, builder.capacity());
                }
            }
            return lines.toArray(new String[lines.size()]);
        } else {
            return new String[] { "" };
        }
    }

    public String[] getParticularResponseHeaders() {
        // TODO Auto-generated method stub
        return null;
    }
}
