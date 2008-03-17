package edu.sfsu.cs.csc867.msales.voctopus.request.validation;

import edu.sfsu.cs.csc867.msales.voctopus.request.AbstractHttpRequest.RequestMethodType;
import edu.sfsu.cs.csc867.msales.voctopus.request.validation.HttpRequestInterpreterException.ErrorToken;

/**
 * @author marcello Feb 15, 2008 1:59:10 PM
 */
public class HttpRequestMethodExpression extends HttpRequestNonTerminalExpression {

    /**
     * Constructs a new Method expression to validate the request method received.
     * 
     * @param context is the context for the http request.
     * @param next is the next expression to be evaluated. In this case, the URI version.
     */
    public HttpRequestMethodExpression(HttpRequestInterpreterContext context, AbstractHttpRequestExpression next) {
        super(context, next, context.getRequestLine(0).split(" ")[0].trim());
    }

    @Override
    protected void validate() throws HttpRequestInterpreterException {
        RequestMethodType method = null;
        try {
            method = RequestMethodType.valueOf(this.getEvaluatedToken().toUpperCase());
        } catch (IllegalArgumentException e){
            this.getContext().setMethodType(RequestMethodType.NOT_SUPPORTED);
            throw new HttpRequestInterpreterException("The request method is incorrect!", 
                    ErrorToken.METHOD_TYPE.setToken(this.getEvaluatedToken()));
        }
        if (method == null) {
            this.getContext().setMethodType(RequestMethodType.NOT_SUPPORTED);
            throw new HttpRequestInterpreterException("Method request invalid", 
                    ErrorToken.METHOD_TYPE.setToken(this.getEvaluatedToken()));
                    
        } else {
            this.getContext().setMethodType(method);
            if (method.equals(RequestMethodType.POST)) {
                String[] lines = this.getContext().getRequestLines();
                if (lines[lines.length - 1].contains("ADDITIONAL")){
                    this.getContext().setAdditionalEncodedData(lines[lines.length - 1].replace("ADDITIONAL", ""));
                    String[] newRequestLines = new String[lines.length-1];
                    for (int i = 0; i < newRequestLines.length; i++) {
                        newRequestLines[i] = lines[i];
                    }
                    this.getContext().setRequestLines(newRequestLines);
                    
                }
            }
        }
    }

}
