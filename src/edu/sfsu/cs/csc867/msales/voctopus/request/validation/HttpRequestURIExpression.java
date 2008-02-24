package edu.sfsu.cs.csc867.msales.voctopus.request.validation;

import edu.sfsu.cs.csc867.msales.httpd.validation.HttpRequestInterpreterException;
import edu.sfsu.cs.csc867.msales.voctopus.request.validation.HttpRequestInterpreterContext.RequestType;

/**
 * Evaluates the URI token from the request. It extracts the
 * 
 * @author marcello Feb 19, 2008 12:16:01 AM
 */
public class HttpRequestURIExpression extends HttpRequestNonTerminalExpression {

    /**
     * Constructs an Http Request URI
     * 
     * @param context is the shared context.
     * @param next is the next expression.
     */
    public HttpRequestURIExpression(HttpRequestInterpreterContext context, AbstractHttpRequestExpression next) {
        super(context, next, context.getRequestLine(0).split(" ")[1].trim());
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void validate() throws HttpRequestInterpreterException {
        if (!this.getEvaluatedToken().startsWith("/")) {
            throw new HttpRequestInterpreterException("The URI token is invalid", this.getEvaluatedToken());
        } else if (this.getEvaluatedToken().contains("/cgi-bin/")) {
            this.getContext().setRequestType(RequestType.SCRIPT_EXECUTION);
        } else if (this.getEvaluatedToken().contains("/ws/")) {
            this.getContext().setRequestType(RequestType.WEB_SERVICE);
        } else {
            this.getContext().setRequestType(RequestType.STATIC_CONTENT);
        }
        this.getContext().setURI(this.getEvaluatedToken());
    }
}