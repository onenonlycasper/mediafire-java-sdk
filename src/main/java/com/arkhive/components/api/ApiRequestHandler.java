package com.arkhive.components.api;

/** Submits an API request.
 *
 * This interface is implemented by classes needing to submit a requst
 * to the MediaFire API.
 */
public interface ApiRequestHandler {
    /** Handler for an API request.
     * <p>
     * Called when an API request is completed.
     *
     * @param  response  The response from the API request.
     */

    public void onRequestComplete(String response);
}
