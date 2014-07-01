package com.arkhive.components.core.module_http_processor.pre_and_post_processors;

import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_api.responses.ApiResponse;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by  on 6/15/2014.
 */
public final class NewSessionTokenHttpPostProcessor implements HttpProcessor {
    private static final String TAG = NewSessionTokenHttpPostProcessor.class.getSimpleName();
    private final Logger logger = LoggerFactory.getLogger(NewSessionTokenHttpPostProcessor.class);

    public NewSessionTokenHttpPostProcessor() {}

    public void processApiRequestObject(ApiRequestObject apiRequestObject) {
        logger.info(" processApiRequestObject()");
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
        } else {
            apiRequestObject.setSessionTokenInvalid(false);
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
        logger.info(" getResponseElement()");
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
