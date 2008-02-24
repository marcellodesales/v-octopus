package edu.sfsu.cs.csc867.msales.voctopus.request.validation;

import edu.sfsu.cs.csc867.msales.httpd.validation.HttpRequestInterpreterException;
import static edu.sfsu.cs.csc867.msales.voctopus.request.validation.HttpRequestInterpreterContext.RequestVersion;

public class HttpRequestVersionExpression extends HttpRequestTerminalExpression {
    
    /**
     * Creates a new version expression from the context.
     * @param context
     */
    public HttpRequestVersionExpression(HttpRequestInterpreterContext context) {
        super(context, context.getRequestLine(0).split(" ")[2].trim());
    }
    
    @Override
    public void interpret() throws HttpRequestInterpreterException {
        //TODO:  java.lang.IllegalArgumentException: HTTP/1.1 BEING THROWN 
        RequestVersion version;
        if (this.getEvaluatedToken().equals(RequestVersion.HTTP_1_1.toString())) {
            version = RequestVersion.HTTP_1_1;
        } else {
            version = RequestVersion.HTTP_1_0;
        }
        
        if (version == null) {
            throw new HttpRequestInterpreterException("Http version token not supported", this.getEvaluatedToken());
        } else {
            this.getContext().setRequestVersion(version);
        }
    }
}
