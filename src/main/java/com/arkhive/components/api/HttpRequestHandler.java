package com.arkhive.components.api;

/** Interface for a callback from a web API call.
 *
 * This interface is implemented by classes wishing to function
 * as callback handlers for a web API request.  This interface is called
 * when a web API request has finished, and the return JSON is passed back
 * to the callback handler as a string.
 */
public interface HttpRequestHandler {
    /** Callback for a web API request.
     *
     */
    public void httpRequestHandler(String response);
}
