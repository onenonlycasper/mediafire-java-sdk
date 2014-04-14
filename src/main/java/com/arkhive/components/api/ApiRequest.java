package com.arkhive.components.api;

import java.util.Map;

import com.arkhive.components.httplibrary.HttpInterface;
import com.arkhive.components.sessionmanager.Session;
import com.arkhive.components.sessionmanager.SessionManager;
import com.arkhive.components.sessionmanager.session.SessionRequestHandler;

/** Interface for a request to the MediaFire web API. */
public class ApiRequest implements HttpRequestHandler, SessionRequestHandler {
    private String domain;
    private String uri;
    private Map<String, String> parameters;
    private HttpInterface httpInterface;
    private ApiRequestHandler requestHandler;
    private SessionManager sessionManager;
    private Session session;

    protected ApiRequest(ApiRequestBuilder b) {
        this.domain = b.domain;
        this.uri = b.uri;
        this.parameters = b.parameters;
        this.httpInterface = b.httpInterface;
        this.requestHandler = b.requestHandler;
        this.sessionManager = b.sessionManager;
    }

    /** Submits a request to the MediaFire web API.
     * <p>
     * Begins the process to submit a request to the web API.
     * The process is started with request for a session. When the session is available, the responseHandler method
     * is invoked. The responseHandler method processes the Session and the query information into a
     * HttpGetRequest, and submits the request to the web API. The callback for the HttpRequest is httpRequestHandler.
     * When the httpRequestHandler method is invoked, the Session is released, and the response callback is invoked.
     */
    public void submitRequest() {
        sessionManager.getSession(this);
    }

    public String submitRequestSync() {
        Session session = sessionManager.getSession();
        parameters.put("response_format", "json");
        String queryString = session.getQueryString(uri, parameters);
        return httpInterface.sendGetRequest(domain + queryString);
    }

    /** Handles the response from the http request.
     * <p>
     * Accepts the response from an HTTP request, then releases the session and passes the response to the
     * request handler.
     *
     * @param  response  The response from the HTTP request.*/
    @Override
    public void httpRequestHandler(String response) {
        this.sessionManager.releaseSession(this.session);
        this.requestHandler.onRequestComplete(response);
    }

    /** Handles the response from the request for a new session.
     * <p>
     * Accepts a response from a request for a session. Once a session is received, the query string is created.
     * A new HttpGetRequest is created and passed to a new thread to be processed.
     *
     * @param  session  The session returned from the session request.
     */
    @Override
    public void responseHandler(Session session) {
        this.session = session;
        parameters.put("response_format", "json");
        String queryString = session.getQueryString(uri, parameters);
        HttpGetRequest request = new HttpGetRequest(domain + queryString, httpInterface, this);
        Thread t = new Thread(request);
        t.start();
    }
}
