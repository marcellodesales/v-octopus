package edu.sfsu.cs.csc867.msales.voctopus.request.validation;

import edu.sfsu.cs.csc867.msales.voctopus.request.validation.HttpRequestInterpreterContext.RequestVersion;

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
        RequestVersion version;
        try {
            version = RequestVersion.valueOf(this.getEvaluatedToken());
        } catch (IllegalArgumentException e) {
            throw new HttpRequestInterpreterException("Http version token not supported", this.getEvaluatedToken());
        }
        this.getContext().setRequestVersion(version);
    }
}
