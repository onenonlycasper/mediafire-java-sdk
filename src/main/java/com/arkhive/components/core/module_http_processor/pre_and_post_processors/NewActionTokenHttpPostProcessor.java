package com.arkhive.components.core.module_http_processor.pre_and_post_processors;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.module_api.responses.ApiResponse;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



/**
 * Created by on 6/18/2014.
 */
public class NewActionTokenHttpPostProcessor implements HttpProcessor {
    private static final String TAG = NewActionTokenHttpPostProcessor.class.getCanonicalName();

    @Override
    public void processApiRequestObject(ApiRequestObject apiRequestObject) {
        Configuration.getErrorTracker().i(TAG, "processApiRequestObject()");
        if (apiRequestObject.getActionToken() != null) {
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
            apiRequestObject.setActionTokenInvalid(true);
            Configuration.getErrorTracker().apiError(TAG, apiRequestObject);
        } else {
            apiRequestObject.setActionTokenInvalid(false);
        }
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

        Configuration.getErrorTracker().i(TAG, "token used: " + apiRequestObject.getActionToken().getTokenString());
        Configuration.getErrorTracker().i(TAG, "original url: " + apiRequestObject.getConstructedUrl());
    }

}
