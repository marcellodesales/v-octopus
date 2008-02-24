package edu.sfsu.cs.csc867.msales.voctopus.request.validation;

import edu.sfsu.cs.csc867.msales.httpd.validation.HttpRequestInterpreterException;

/**
 * Marker class for the non-terminal expressions for the HttpRequestInterpreter, which contains the link to the
 * expression to be interpreted.
 * 
 * @author marcello Feb 17, 2008 12:08:45 AM
 */
public abstract class HttpRequestNonTerminalExpression extends AbstractHttpRequestExpression {

    /**
     * The instance of the next expression to be evaluated by this non-terminal expression.
     */
    private AbstractHttpRequestExpression next;

    /**
     * Constructs a new Non-Terminal expression for the HttpRequestInterprer.
     * 
     * @param context is the context used with the Interpreter
     * @param next is the next expression to be interpreted.
     */
    public HttpRequestNonTerminalExpression(HttpRequestInterpreterContext context, AbstractHttpRequestExpression next, 
            String token) {
        super(context, token);
        this.next = next;
        // TODO Auto-generated constructor stub
    }
    
    public AbstractHttpRequestExpression getNext() {
        return next;
    }

    /**
     * The validate method is the one used to implement the logic to interpret the context.
     * @throws HttpRequestInterpreterException
     */
    protected abstract void validate() throws HttpRequestInterpreterException;

    /*
     * (non-Javadoc)
     * 
     * @see edu.sfsu.cs.csc867.msales.voctopus.AbstractHttpRequestExpression#interpret()
     */
    public final void interpret() throws HttpRequestInterpreterException {
        this.validate();
        this.next.interpret();
    }
}
