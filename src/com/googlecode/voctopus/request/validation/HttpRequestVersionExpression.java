package com.googlecode.voctopus.request.validation;

import com.googlecode.voctopus.request.AbstractHttpRequest.RequestVersion;
import com.googlecode.voctopus.request.validation.HttpRequestInterpreterException.ErrorToken;


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
        for (RequestVersion version : RequestVersion.values()) {
            if (version.toString().equalsIgnoreCase(this.getEvaluatedToken())) {
                this.getContext().setRequestVersion(version);
                return;
            }
        }
        throw new HttpRequestInterpreterException("Http version token not supported", 
                ErrorToken.VERSION_TYPE.setToken(this.getEvaluatedToken()));
    }
}
