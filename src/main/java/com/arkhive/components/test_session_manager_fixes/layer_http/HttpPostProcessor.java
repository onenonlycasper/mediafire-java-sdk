package com.arkhive.components.test_session_manager_fixes.layer_http;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_api_response.ApiResponse;
import com.arkhive.components.test_session_manager_fixes.module_session_token.ActionToken;
import com.arkhive.components.test_session_manager_fixes.module_session_token.SessionToken;
import com.arkhive.components.test_session_manager_fixes.module_session_token.Token;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public final class HttpPostProcessor {
    private final ApiRequestObject apiRequestObject;

    public HttpPostProcessor(ApiRequestObject apiRequestObject) {
        super();
        this.apiRequestObject = apiRequestObject;
    }

    public void processApiRequestObject() {
        Token token = apiRequestObject.getToken();
        if (ActionToken.class.isInstance(token)) {
            boolean stillValid = isTokenStillValid(apiRequestObject);
            if (stillValid) {
                returnActionToken((ActionToken) token);
            } else {
                notifyExpiredActionToken((ActionToken) token);
            }
        } else if (SessionToken.class.isInstance(token)) {
            boolean stillValid = isTokenStillValid(apiRequestObject);
            if (stillValid) {
                returnSessionToken((SessionToken) token);
            } else {
                notifyExpiredSessionToken((SessionToken) token);
            }
        }
    }

    private boolean isTokenStillValid(ApiRequestObject apiRequestObject) {
        String jsonResponse = apiRequestObject.getHttpResponseString();
        if (jsonResponse == null) {
            return true;
        }

        JsonElement jsonElement = getResponseElement(jsonResponse);
        if (jsonElement == null) {
            return true;
        }

        ApiResponse apiResponse = new Gson().fromJson(jsonElement, ApiResponse.class);
        if (apiResponse == null) {
            return true;
        }

        if (apiResponse.needNewKey()) {
            return false;
        }

        if (apiResponse.getError() == 105 || apiResponse.getError() == 127) {
            return false;
        }

        return true;
    }

    private void returnActionToken(ActionToken actionToken) {
        if (apiRequestObject.getTokenServerCallback() != null) {
            apiRequestObject.getTokenServerCallback().actionTokenReturned(actionToken);
        }
    }

    private void notifyExpiredActionToken(ActionToken actionToken) {
        if (apiRequestObject.getTokenServerCallback() != null) {
            apiRequestObject.getTokenServerCallback().actionTokenExpired(actionToken);
        }
    }

    private void returnSessionToken(SessionToken sessionToken) {
        if (apiRequestObject.getTokenServerCallback() != null) {
            apiRequestObject.getTokenServerCallback().sessionTokenReturned(sessionToken);
        }
    }

    private void notifyExpiredSessionToken(SessionToken sessionToken) {
        if (apiRequestObject.getTokenServerCallback() != null) {
            apiRequestObject.getTokenServerCallback().sessionTokenExpired(sessionToken);
        }
    }

    /** Transform a string into a JsonElement.
     *
     * All response strings returned from the web api are wrapped in response json element.
     * This method strips the wrapper element, and converts the remaining element into a JsonElement via GSON.
     *
     * @param  response  A response string from a web API call.
     *
     * @return  The JsonElement created from the response string.
     */
    private static JsonElement getResponseElement(String response) {
        if (response == null) {
            return null;
        }
        if (response.isEmpty()) {
            return null;
        }
        JsonElement returnJson = new JsonObject();
        JsonParser parser = new JsonParser();
        JsonElement rootElement = parser.parse(response);
        if (rootElement.isJsonObject()) {
            JsonElement jsonResult = rootElement.getAsJsonObject().get("response");
            if (jsonResult.isJsonObject()) {
                returnJson = jsonResult.getAsJsonObject();
            }
        }
        return returnJson;
    }
}
