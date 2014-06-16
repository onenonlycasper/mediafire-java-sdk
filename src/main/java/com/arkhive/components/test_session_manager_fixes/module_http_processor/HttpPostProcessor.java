package com.arkhive.components.test_session_manager_fixes.module_http_processor;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_api_response.ApiResponse;
import com.arkhive.components.test_session_manager_fixes.module_session_token.SessionToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public final class HttpPostProcessor {
    public HttpPostProcessor() { }

    public void processApiRequestObject(ApiRequestObject apiRequestObject) {
        String jsonResponse = apiRequestObject.getHttpResponseString();
        JsonElement jsonElement = getResponseElement(jsonResponse);
        if (jsonElement == null) {
            return;
        }

        // get the generic api response from the response.
        ApiResponse apiResponse = new Gson().fromJson(jsonElement, ApiResponse.class);
        // if the session token is invalid or expired then set the flag (so TokenFarm knows)
        if (isSessionTokenInvalidOrExpired(apiResponse)) {
            apiRequestObject.setSessionTokenInvalid(true);
        }

        // if the signature is invalid or expired then print something for debugging (for now, remove this later)
        if (isSignatureInvalid(apiResponse)) {
            System.out.println("*******************");
            System.out.println("*signature invalid*");
            System.out.println("*******************");
        }

        if (apiResponse.needNewKey()) {
            SessionToken sessionToken = (SessionToken) apiRequestObject.getToken();
            sessionToken.updateSecretKey();
        }

    }

    private boolean isSessionTokenInvalidOrExpired(ApiResponse apiResponse) {
        return apiResponse.getError() == 105;
    }

    private boolean isSignatureInvalid(ApiResponse apiResponse) {
        return apiResponse.getError() == 127;
    }

    /**
     * All response strings returned from the web api are wrapped in "response" json element.
     * This method strips the "response" element, and converts the remaining element into a JsonElement via GSON.
     *
     * @param  response  A response string from a web API call.
     *
     * @return  The JsonElement created from the response string.
     */
    public static JsonElement getResponseElement(String response) {
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
