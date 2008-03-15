package edu.sfsu.cs.csc867.msales.voctopus.request.validation;

import java.util.Properties;
import java.util.StringTokenizer;

import edu.sfsu.cs.csc867.msales.voctopus.request.validation.HttpRequestInterpreterContext.RequestType;

/**
 * Evaluates the URI token from the request. It extracts the
 * 
 * @author marcello Feb 19, 2008 12:16:01 AM
 */
public class HttpRequestURIExpression extends HttpRequestNonTerminalExpression {

    /**
     * Parameters from the request.
     */
    private Properties parameters;

    /**
     * Constructs an HTTP Request URI, decoding the values of the string
     * 
     * @param context is the shared context.
     * @param next is the next expression, an instance of the version.
     */
    public HttpRequestURIExpression(HttpRequestInterpreterContext context, AbstractHttpRequestExpression next) {
        super(context, next, null);
        this.evaluatedToken = context.getRequestLine(0).split(" ")[1].trim();
    }

    /**
     * Decodes the percentage encoding scheme. <br/> e.g.: "an+example%20string" -> "an example string"
     */
    public static String decodePercentage(String str) {
        try {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                switch (c) {
                case '+':
                    sb.append(' ');
                    break;
                case '%':
                    sb.append((char) Integer.parseInt(str.substring(i + 1, i + 3), 16));
                    i += 2;
                    break;
                default:
                    sb.append(c);
                    break;
                }
            }
            return new String(sb.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decodes parameters in percent-encoded URI-format ( e.g. "name=Marcello%20Sales&pass=do%20it" ) and adds them to
     * the internal Properties.
     */
    public void decodeParmeters(String parms) {
        if (parms == null)
            return;

        StringTokenizer st = new StringTokenizer(parms, "&");
        while (st.hasMoreTokens()) {
            String e = st.nextToken();
            int sep = e.indexOf('=');
            if (sep >= 0)
                this.parameters.put(decodePercentage(e.substring(0, sep)).trim(),
                        decodePercentage(e.substring(sep + 1)));
        }
    }

    @Override
    protected void validate() throws HttpRequestInterpreterException {
        if (!this.getEvaluatedToken().startsWith("/")) {
            throw new HttpRequestInterpreterException("The URI token is invalid", this.getEvaluatedToken());
        } else if (this.getEvaluatedToken().startsWith("/cgi-bin/")) {
            this.getContext().setRequestType(RequestType.SCRIPT_EXECUTION);
        } else if (this.getEvaluatedToken().startsWith("/ws/")) {
            this.getContext().setRequestType(RequestType.WEB_SERVICE);
        } else {
            this.getContext().setRequestType(RequestType.STATIC_CONTENT);
        }
        this.getContext().setURI(this.getEvaluatedToken());
    }

    public Properties getParameters() {
        return parameters;
    }
}