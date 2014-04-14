package com.arkhive.components.sessionmanager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.List;

import com.arkhive.components.api.ApiRequestHandler;
import com.arkhive.components.api.Utility;
import com.arkhive.components.sessionmanager.session.ActionTokenRequestHandler;
import com.arkhive.components.sessionmanager.session.ActionTokenResponse;

/** Handler for a request for a new image action token.
 * <p>
 * Called when the request for a new image action token is completed.
 * When the API call returns, create a new ActionTokenResponse, and notify
 * all handlers in the list.
 *
 * @param  handlers  A list of ActionTokenRequestHandler awaiting a new image action token.
 */
class InternalImageTokenHandler implements ApiRequestHandler {
    /**
     *
     */
    private final SessionManager sessionManager;
    private List<ActionTokenRequestHandler> handlers;
    private Gson gson = new Gson();

    public InternalImageTokenHandler(SessionManager sessionManager, List<ActionTokenRequestHandler> handlers) {
        this.sessionManager = sessionManager;
        this.handlers = handlers;
    }

    @Override
    public void onRequestComplete(String response) {
        JsonElement jsonResponse = Utility.getResponseElement(response);
        ActionTokenResponse actionTokenResponse = new ActionTokenResponse();
        actionTokenResponse = gson.fromJson(jsonResponse, ActionTokenResponse.class);
        sessionManager.setImageActionToken(actionTokenResponse);
        for (ActionTokenRequestHandler e : handlers) {
            e.receiveActionToken(actionTokenResponse);
        }
    }
}

