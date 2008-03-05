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


public class AsciiContentRequestHandlerStrategy extends AbstractRequestHandler {
    
    public AsciiContentRequestHandlerStrategy(URI uri, File requestedFile, String handlerFound) {
        super(uri, requestedFile, RequestType.ASCII, handlerFound);
        // TODO Auto-generated constructor stub
    }

    public String[] getResourceLines() throws IOException {
        
        if (this.requestedResourceExists()) {
            System.out.println("serving the file " + this.getRequestedFile());
            FileChannel channel = new FileInputStream(this.getRequestedFile()).getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, this.getRequestedFile().length());
            
            Charset charset = Charset.forName ( "ISO-8859-1" ) ; 
            CharsetDecoder decoder = charset.newDecoder (  ) ; 
            CharBuffer charBuffer = decoder.decode ( buffer ) ; 
            List<String> lines = new ArrayList<String>();
    
            StringBuilder builder = new StringBuilder();
            for(int i=0, n=charBuffer.length (  ) ; i < n; i++ ) {
    
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
            return new String[]{""};
        }
    }
}
