package com.arkhive.components.api;

import com.arkhive.components.httplibrary.HttpInterface;

import java.io.IOException;

/** Class used to send a GET request to the web API. */
public class HttpGetRequest implements Runnable {
    private HttpRequestHandler handler;
    private String call;
    private HttpInterface httpInterface;

    /** Initializes the HttpRequestRunner object.
     *
     * @param call  The URI to use for the API call.
     * @param  httpInterface  The http interface used to make the API call.
     * @param  handler  The callback to be executed when the API call has completed.
     */
    public HttpGetRequest(String call, HttpInterface httpInterface, HttpRequestHandler handler) {
        this.call = call;
        this.httpInterface = httpInterface;
        this.handler = handler;
    }

    /** Perform the web API call.
     *
     * Performs a GET request to the MediaFire web API.  When the request has
     * completed, the callback function is executed.
     */
    public void run() {
        String result = null;
        try {
            result = this.httpInterface.sendGetRequest(this.call);
        } catch (IOException e) {
            e.printStackTrace();
            result = "";
        }
        handler.httpRequestHandler(result);
    }
}
