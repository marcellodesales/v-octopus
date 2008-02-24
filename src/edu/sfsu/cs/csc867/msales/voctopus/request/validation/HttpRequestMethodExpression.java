package edu.sfsu.cs.csc867.msales.voctopus.request.validation;

import edu.sfsu.cs.csc867.msales.httpd.validation.HttpRequestInterpreterException;
import static edu.sfsu.cs.csc867.msales.voctopus.request.validation.HttpRequestInterpreterContext.RequestMethod;

/**
 * @author marcello
 * Feb 15, 2008 1:59:10 PM
 */
public class HttpRequestMethodExpression extends HttpRequestNonTerminalExpression {

    
    public HttpRequestMethodExpression(HttpRequestInterpreterContext context, AbstractHttpRequestExpression next) {
        super(context, next, context.getRequestLine(0).split(" ")[0].trim());
    }

    @Override
    protected void validate() throws HttpRequestInterpreterException {
        RequestMethod method = RequestMethod.valueOf(this.getEvaluatedToken());
        if (method == null) {
            throw new HttpRequestInterpreterException("The request sent by the client is not support or invalid!",
                    this.getEvaluatedToken());
        } else {
            this.getContext().setMethodType(method);
        }
    }

}
