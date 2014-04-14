package com.arkhive.components.sessionmanager.session;

/** Handles the response from a request for an action token.
 */
public interface ActionTokenRequestHandler {
    public void receiveActionToken(ActionTokenResponse token);
}
