package com.arkhive.components.api;

import java.util.Map;

import com.arkhive.components.httplibrary.HttpInterface;
import com.arkhive.components.sessionmanager.SessionManager;

/**
 * Creates an ApiRequest.
 * <p/>
 * Sets all of the parameters needed to construct an ApiRequest object.
 */
public class ApiRequestBuilder {
    protected String domain;
    protected String uri;
    protected Map<String, String> parameters;
    protected HttpInterface httpInterface;
    protected ApiRequestHandler requestHandler;
    protected SessionManager sessionManager;

    /**
     * Set the value of the domain string.
     * <p/>
     * If the value passed is null, then the domain string retains its current value.
     *
     * @param value The new value for the domain string.
     */
    public ApiRequestBuilder domain(String value) {
        if (value != null) {
            this.domain = value;
        }
        return this;
    }

    /**
     * Set the value of the uri.
     * <p/>
     * If the value passed is null, the uri string retains its current value.
     *
     * @param value The new value for the uri string.
     */
    public ApiRequestBuilder uri(String value) {
        if (value != null) {
            this.uri = value;
        }
        return this;
    }

    /**
     * Set the value of the request parameters.
     * <p/>
     * If the value passed is null, then the parameters map retains its current value.
     *
     * @param value The new Map<String, String> of parameters.
     */
    public ApiRequestBuilder parameters(Map<String, String> value) {
        if (value != null) {
            this.parameters = value;
        }
        return this;
    }

    /**
     * Set the HttpInterface to use for the API call.
     * <p/>
     * If the value passed is null, the current HttpInterface is unchanged.
     *
     * @param value The new HttpInterface to use.
     */
    public ApiRequestBuilder httpInterface(HttpInterface value) {
        if (value != null) {
            this.httpInterface = value;
        }
        return this;
    }

    /**
     * Set the request handler.
     * <p/>
     * If the value passed is null, the current RequestHandler is used.
     *
     * @param value The new RequestHandler to use with the API call.
     */
    public ApiRequestBuilder requestHandler(ApiRequestHandler value) {
        if (value != null) {
            this.requestHandler = value;
        }
        return this;
    }

    /**
     * Set the SessionManager.
     * <p/>
     * The SessionManager to use.
     *
     * @param value The new SessionManager to use with the API call.
     */
    public ApiRequestBuilder sessionManager(SessionManager value) {
        if (value != null) {
            this.sessionManager = value;
        }
        return this;
    }

    /**
     * Construct a new ApiRequest from the given parameters.
     * <p/>
     * If any of the parameters are not set to a reasonable value, throw an Exception indicating that there
     * are invalid parameters.
     *
     * @return A complete ApiRequest object.
     * @throws IllegalStateException
     */
    public ApiRequest build() {
        return new ApiRequest(this);
    }
}

