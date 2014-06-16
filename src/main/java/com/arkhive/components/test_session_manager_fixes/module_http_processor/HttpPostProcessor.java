package com.arkhive.components.test_session_manager_fixes.module_http_processor;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_api_response.ApiResponse;
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
        apiRequestObject.setTokenValid(isTokenStillValid(apiRequestObject));
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

    /** Transform a string into a JsonElement.
     *
     * All response strings returned from the web api are wrapped in response json element.
     * This method strips the wrapper element, and converts the remaining element into a JsonElement via GSON.
     *
     * @param  response  A response string from a web API call.
     *
     * @return  The JsonElement created from the response string.
     */
    private JsonElement getResponseElement(String response) {
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
