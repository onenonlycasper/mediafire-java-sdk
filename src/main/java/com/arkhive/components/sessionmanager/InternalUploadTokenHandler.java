package com.arkhive.components.sessionmanager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.List;

import com.arkhive.components.api.ApiRequestHandler;
import com.arkhive.components.api.Utility;
import com.arkhive.components.sessionmanager.session.ActionTokenRequestHandler;
import com.arkhive.components.sessionmanager.session.ActionTokenResponse;


/** Handler for a request for a new upload action token.
 * <p>
 * Called when the request for a new upload action token is completed.
 * When the API call returns, create a new ActionTokenResponse, and notify
 * all of the handlers in the list.
 *
 */
class InternalUploadTokenHandler implements ApiRequestHandler {
    /**
     *
     */
    private final SessionManager sessionManager;
    private final List<ActionTokenRequestHandler> handlers;
    private final Gson gson = new Gson();

    protected InternalUploadTokenHandler(SessionManager sessionManager, List<ActionTokenRequestHandler> handlers) {
        this.sessionManager = sessionManager;
        this.handlers = handlers;
    }

    @Override
    public void onRequestComplete(String response) {
        JsonElement jsonResponse = Utility.getResponseElement(response);
        ActionTokenResponse actionTokenResponse;
        actionTokenResponse = gson.fromJson(jsonResponse, ActionTokenResponse.class);
        sessionManager.setUploadActionToken(actionTokenResponse);
        for (ActionTokenRequestHandler e : handlers) {
            e.receiveActionToken(actionTokenResponse);
        }
    }
}
