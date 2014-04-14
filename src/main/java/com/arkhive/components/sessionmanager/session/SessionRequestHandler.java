package com.arkhive.components.sessionmanager.session;

import com.arkhive.components.sessionmanager.Session;

/** Handles the response from a session request.
 * <br>
 * This interface is implemented by classes wishing to receive the response from
 * a session request.
 */
public interface SessionRequestHandler {
    /** Handles the response from a session request.
     * <p>
     * This method is invoked when a Session request has been fulfilled.
     *
     * @param session The session returned from the session request
     */
    public void responseHandler(Session session);
}

