package com.arkhive.components.api;

import com.arkhive.components.httplibrary.HttpInterface;
import com.arkhive.components.sessionmanager.Session;
import com.arkhive.components.sessionmanager.SessionManager;
import com.arkhive.components.sessionmanager.session.SessionRequestHandler;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

/**
 * Interface for a request to the MediaFire web API.
 */
public class ApiRequest implements HttpRequestHandler, SessionRequestHandler {
    private static final String TAG = ApiRequest.class.getSimpleName();
    private static final int RETRY_MAX = 10;
    private final String domain;
    private final String uri;
    private final Map<String, String> parameters;
    private final HttpInterface httpInterface;
    private final ApiRequestHandler requestHandler;
    private final SessionManager sessionManager;

    private int retryCount = 0;

    private Session session;

    protected ApiRequest(ApiRequestBuilder b) {
        super();
        this.domain = b.domain;
        this.uri = b.uri;
        this.parameters = b.parameters;
        this.httpInterface = b.httpInterface;
        this.requestHandler = b.requestHandler;
        this.sessionManager = b.sessionManager;
    }

    /**
     * Submits a request to the MediaFire web API.
     * <p/>
     * Begins the process to submit a request to the web API.
     * The process is started with request for a session. When the session is available, the responseHandler method
     * is invoked. The responseHandler method processes the Session and the query information into a
     * HttpGetRequest, and submits the request to the web API. The callback for the HttpRequest is httpRequestHandler.
     * When the httpRequestHandler method is invoked, the Session is released, and the response callback is invoked.
     */
    public void submitRequest() {
        sessionManager.getSession(this);
    }

    /**
     * Submits a request to the MediaFire web API.
     *
     * @return The response from the MediaFire web API.
     */
    public String submitRequestSync() {
        Session session = sessionManager.getSession();
        parameters.put("response_format", "json");
        String queryString = session.getQueryString(uri, parameters);
        String responseString;
        try {
            responseString = httpInterface.sendGetRequest(domain + queryString);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        ApiResponse response = new Gson().fromJson(Utility.getResponseElement(responseString), ApiResponse.class);
        if (response.hasError() && response.getErrorCode() == ApiResponseCode.ERROR_INVALID_SIGNATURE) {
            if (retryCount < RETRY_MAX) {
                retryCount++;
                this.submitRequestSync();
            } else {
                return responseString;
            }
        } else {
            sessionManager.releaseSession(session);
        }
        return responseString;
    }

    /**
     * Handles the response from the http request.
     * <p/>
     * Accepts the response from an HTTP request, then releases the session and passes the response to the
     * request handler.
     *
     * @param responseString The response from the HTTP request.
     */
    @Override
    public void httpRequestHandler(String responseString) {
        ApiResponse response = new Gson().fromJson(Utility.getResponseElement(responseString), ApiResponse.class);
        if (response.hasError() && response.getErrorCode() == ApiResponseCode.ERROR_INVALID_SIGNATURE) {
            if (retryCount < RETRY_MAX) {
                retryCount++;
                this.submitRequest();
            }
        } else {
            this.sessionManager.releaseSession(this.session);
        }
        this.requestHandler.onRequestComplete(responseString);
    }

    /**
     * Handles the response from the request for a new session.
     * <p/>
     * Accepts a response from a request for a session. Once a session is received, the query string is created.
     * A new HttpGetRequest is created and passed to a new thread to be processed.
     *
     * @param session The session returned from the session request.
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
