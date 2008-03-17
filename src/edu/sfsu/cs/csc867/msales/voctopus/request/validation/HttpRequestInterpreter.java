package edu.sfsu.cs.csc867.msales.voctopus.request.validation;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import edu.sfsu.cs.csc867.msales.voctopus.HttpClientConnection;
import edu.sfsu.cs.csc867.msales.voctopus.request.HttpRequest;
import edu.sfsu.cs.csc867.msales.voctopus.request.AbstractHttpRequest.RequestMethodType;
import edu.sfsu.cs.csc867.msales.voctopus.request.AbstractHttpRequest.RequestVersion;
import edu.sfsu.cs.csc867.msales.voctopus.request.validation.HttpRequestInterpreterException.ErrorToken;

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

    public HttpRequestInterpreter(String[] requestedLines, HttpClientConnection httpClientConnection)
            throws HttpRequestInterpreterException {
        this.context = new HttpRequestInterpreterContext(requestedLines);
        this.clientAddres = httpClientConnection.getClientConnection().getInetAddress();
    }
    
    /**
     * Interprets the request tokens
     * 
     * @return an instance of the HttpRequest based on the type of the request.
     * @throws HttpRequestInterpreterException if the request contains tokens not identified on HttpRequests. This
     *             behavior should return a correct http response header status code.
     */
    public HttpRequest interpret() {

        AbstractHttpRequestExpression versionExpr = new HttpRequestVersionExpression(context);
        HttpRequestURIExpression uriExpr = new HttpRequestURIExpression(context, versionExpr);
        HttpRequestMethodExpression methodExpr = new HttpRequestMethodExpression(context, uriExpr);
        try {
            methodExpr.interpret();
        } catch (HttpRequestInterpreterException e) {
            
            this.context.signalMalformedRequest();
            this.context.setRequestType(HttpRequestInterpreterContext.RequestType.INVALID);
            
            if (e.getToken().equals(ErrorToken.VERSION_TYPE)) {
                this.context.setRequestVersion(RequestVersion.INVALID);;
            } else
            if (e.getToken().equals(ErrorToken.URI_TYPE) || e.getToken().equals(ErrorToken.METHOD_TYPE)) {
                this.context.setRequestVersion(RequestVersion.HTTP_1_1);
                this.context.setMethodType(RequestMethodType.NOT_SUPPORTED);
            }
        }

        String[] lines = this.context.getRequestLines();
        List<HttpRequestHeaderFieldVarExpression> vars = new ArrayList<HttpRequestHeaderFieldVarExpression>();
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].indexOf(": ") < 0) continue; 
            String[] headerVarValue = lines[i].split(": ");
            HttpRequestHeaderFieldValueExpression value = new HttpRequestHeaderFieldValueExpression(context,
                    headerVarValue[1]);
            HttpRequestHeaderFieldVarExpression var = new HttpRequestHeaderFieldVarExpression(context, value,
                    headerVarValue[0]);
            vars.add(var);
        }
        //don't validate the parameters... relax on them, too much stuff...
        // If no exception is thrown, then this section is executed.
        return this.context.getParsedRequest(methodExpr, vars.toArray(new HttpRequestHeaderFieldVarExpression[vars
                .size()]), this.clientAddres);
    }
}