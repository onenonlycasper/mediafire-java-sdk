package com.arkhive.components.core.module_http_processor.pre_and_post_processors;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.module_api.responses.ApiResponse;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.core.module_token_farm.tokens.SessionToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



/**
 * Created by  on 6/15/2014.
 */
public final class ApiRequestHttpPostProcessor implements HttpProcessor {
    private static final String TAG = ApiRequestHttpPostProcessor.class.getCanonicalName();

    public ApiRequestHttpPostProcessor() {}

    public void processApiRequestObject(ApiRequestObject apiRequestObject) {
        Configuration.getErrorTracker().i(TAG, "processApiRequestObject()");
        if (apiRequestObject.getSessionToken() != null) {
            printData(apiRequestObject);
        }
        String jsonResponse = apiRequestObject.getHttpResponseString();
        JsonElement jsonElement = getResponseElement(jsonResponse);
        if (jsonElement == null) {
            return;
        }

        // get the generic api response from the response.
        ApiResponse apiResponse = new Gson().fromJson(jsonElement, ApiResponse.class);
        // if the session token is invalid or expired then set the flag (so TokenFarm knows)
        // if the signature is invalid then set the flag (so TokenFarm knows)
        if (apiResponse.getError() == 105 || apiResponse.getError() == 127) {
            apiRequestObject.setSessionTokenInvalid(true);
            Configuration.getErrorTracker().apiError(TAG, apiRequestObject);
        } else {
            apiRequestObject.setSessionTokenInvalid(false);
        }

        if (apiResponse.needNewKey()) {
            Configuration.getErrorTracker().i(TAG, "need new key");
            SessionToken sessionToken = apiRequestObject.getSessionToken();
            updateSecretKey(sessionToken);
        }
    }

    public void updateSecretKey(SessionToken sessionToken) {
        Configuration.getErrorTracker().i(TAG, "updateSecretKey()");
        String secretKey = sessionToken.getSecretKey();
        long newKey = Long.valueOf(secretKey) * 16807;
        newKey = newKey % 2147483647;
        Configuration.getErrorTracker().i(TAG, "updated secret key from: " + secretKey + " to " + newKey);
        secretKey = String.valueOf(newKey);
        sessionToken.setSecretKey(secretKey);

    }

    /**
     * All response strings returned from the web api are wrapped in "response" json element.
     * This method strips the "response" element, and converts the remaining element into a JsonElement via GSON.
     *
     * @param response A response string from a web API call.
     * @return The JsonElement created from the response string.
     */
    public JsonElement getResponseElement(String response) {
        Configuration.getErrorTracker().i(TAG, "getResponseElement()");
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

    public void printData(ApiRequestObject apiRequestObject) {
        Configuration.getErrorTracker().i(TAG, "response code: " + apiRequestObject.getHttpResponseCode());
        Configuration.getErrorTracker().i(TAG, "response string: " + apiRequestObject.getHttpResponseString());
        Configuration.getErrorTracker().i(TAG, "domain used: " + apiRequestObject.getDomain());
        Configuration.getErrorTracker().i(TAG, "uri used: " + apiRequestObject.getUri());
        for (String key : apiRequestObject.getRequiredParameters().keySet()) {
            Configuration.getErrorTracker().i(TAG, "required parameter passed (key, value): " + key + ", " + apiRequestObject.getRequiredParameters().get(key));
        }
        for (String key : apiRequestObject.getOptionalParameters().keySet()) {
            Configuration.getErrorTracker().i(TAG, "required parameter passed (key, value): " + key + ", " + apiRequestObject.getOptionalParameters().get(key));
        }

        Configuration.getErrorTracker().i(TAG, "token used: " + apiRequestObject.getSessionToken().getTokenString());
        if (SessionToken.class.isInstance(apiRequestObject.getSessionToken())) {
            SessionToken sessionToken = apiRequestObject.getSessionToken();
            Configuration.getErrorTracker().i(TAG, "session token secret key used: " + sessionToken.getSecretKey());
            Configuration.getErrorTracker().i(TAG, "session token time used: " + sessionToken.getTime());
        }
        Configuration.getErrorTracker().i(TAG, "original url: " + apiRequestObject.getConstructedUrl());
    }
}
