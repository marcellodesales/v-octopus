package edu.sfsu.cs.csc867.msales.voctopus.request.validation;

import java.net.InetAddress;

import edu.sfsu.cs.csc867.msales.httpd.validation.HttpRequestInterpreterException;
import edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest;

/**
 * The interpreter is responsible in parsing the incoming request from the client. The HttpRequest instance is then
 * instantiated based on the parsing results, by using the Abstract Factory for the requests.
 * 
 * @author marcello Feb 16, 2008 10:35:51 AM
 */
public class HttpRequestInterpreter {

    /**
     * The context used on this interpreter.
     */
    private HttpRequestInterpreterContext context;
    
    /**
     * Client'st address information
     */
    private InetAddress clientAddres;

    public HttpRequestInterpreter(String[] requestedLines, InetAddress clientAddress)
            throws HttpRequestInterpreterException {
        this.context = new HttpRequestInterpreterContext(requestedLines);
        this.clientAddres = clientAddress;
    }

    /**
     * Interprets the request tokens
     * @return an instance of the HttpRequest based on the type of the request.
     * @throws HttpRequestInterpreterException if the request contains tokens not identified on HttpRequests.
     * This behavior should return a correct http response header status code.
     */
    public HttpRequest interpret() throws HttpRequestInterpreterException {

        AbstractHttpRequestExpression version = new HttpRequestVersionExpression(context);
        AbstractHttpRequestExpression uri = new HttpRequestURIExpression(context, version);
        HttpRequestMethodExpression method = new HttpRequestMethodExpression(context, uri);
        method.interpret();

        String[] lines = this.context.getRequestLines();
        HttpRequestHeaderFieldVarExpression[] vars = new HttpRequestHeaderFieldVarExpression[lines.length - 1];
        for (int i = 1; i < lines.length; i++) {
            String[] headerVarValue = lines[i].split(": ");
            HttpRequestHeaderFieldValueExpression value = new HttpRequestHeaderFieldValueExpression(context,
                    headerVarValue[1]);
            HttpRequestHeaderFieldVarExpression var = new HttpRequestHeaderFieldVarExpression(context, value,
                    headerVarValue[0]);
            vars[i - 1] = var;
            var.interpret();
        }

        // If no exception is thrown, then this section is executed.
        return this.context.getParsedRequest(method, vars, this.clientAddres);
    }
}