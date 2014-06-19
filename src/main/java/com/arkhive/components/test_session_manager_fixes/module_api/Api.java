package com.arkhive.components.test_session_manager_fixes.module_api;

import com.arkhive.components.test_session_manager_fixes.module_api.responses.ApiResponse;
import com.arkhive.components.test_session_manager_fixes.module_api.responses.UploadInstantResponse;
import com.arkhive.components.test_session_manager_fixes.module_api.responses.UploadResumableResponse;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestHttpPostProcessor;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestHttpPreProcessor;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.interfaces.ApiRequestRunnableCallback;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.requests.*;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.TokenFarm;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.interfaces.TokenFarmDistributor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by  on 6/17/2014.
 */
public class Api {
    private static final String FAIL_RES = "{\"response\":{\"message\":\"Unknown API error\",\"result\":\"Error\"}}";
    private static HttpPeriProcessor httpPeriProcessor;
    private static TokenFarmDistributor tokenFarm;
    public File file;
    public Folder folder;
    public User user;
    public System system;
    public Device device;
    public Upload upload;
    public Contact contact;

    public Api(TokenFarm tokenFarm, HttpPeriProcessor httpPeriProcessor) {
        this.tokenFarm = tokenFarm;
        this.httpPeriProcessor = httpPeriProcessor;
        file = new File();
        folder = new Folder();
        user = new User();
        system = new System();
        device = new Device();
        upload = new Upload();
        contact = new Contact();
    }

    static BlockingApiGetRequest createBlockingApiGetRequest(
            ApiRequestObject apiRequestObject) {
        return new BlockingApiGetRequest(
                new ApiRequestHttpPreProcessor(),
                new ApiRequestHttpPostProcessor(),
                tokenFarm,
                httpPeriProcessor,
                apiRequestObject);
    }

    static <T extends ApiResponse> RunnableApiGetRequest<T> createApiGetRequestRunnable(
            Class<T> clazz,
            ApiRequestRunnableCallback<T> callback,
            ApiRequestObject apiRequestObject) {
        return new RunnableApiGetRequest(
                clazz, callback,
                new ApiRequestHttpPreProcessor(),
                new ApiRequestHttpPostProcessor(),
                tokenFarm, httpPeriProcessor,
                apiRequestObject);
    }


    public static BlockingApiGetRequestUploadToken createBlockingApiGetRequestUploadToken(
            ApiRequestObject apiRequestObject) {
        return null;
    }

    static <T extends ApiResponse>RunnableApiGetRequestUploadToken createApiGetRequestRunnableUploadToken(
            Class<UploadInstantResponse> uploadInstantResponseClass,
            ApiRequestRunnableCallback callback,
            ApiRequestObject apiRequestObject) {
        return null;
    }

    public static BlockingApiPostRequestUploadToken createBlockingApiPostRequestUploadToken(
            ApiRequestObject apiRequestObject) {
        return null;
    }

    static <T extends ApiResponse>RunnableApiPostRequestUploadToken createApiPostRequestRunnableUploadToken(
            Class<UploadResumableResponse> uploadResumableResponseClass,
            ApiRequestRunnableCallback callback,
            ApiRequestObject apiRequestObject) {
        return null;
    }


    /**
     * Transform a string into a JsonElement.
     * <p/>
     * All response strings returned from the web api are wrapped in response json element.
     * This method strips the wrapper element, and converts the remaining element into a JsonElement via GSON.
     *
     * @param response A response string from a web API call.
     * @return The JsonElement created from the response string.
     */
    public static JsonElement getResponseElement(String response) {
        if (response.length() == 0 || response.isEmpty() || response == null) {
            response = FAIL_RES;
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

    /**
     * converts a String received from JSON format into a response String.
     *
     * @param response - the response received in JSON format
     * @return the response received which can then be parsed into a specific format as per Gson.fromJson()
     */
    public static String getResponseString(String response) {
        if (response.length() == 0 || response.isEmpty() || response == null) {
            response = FAIL_RES;
        }

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(response);
        if (element.isJsonObject()) {
            JsonObject jsonResponse = element.getAsJsonObject().get("response").getAsJsonObject();
            return jsonResponse.toString();
        } else {
            return FAIL_RES;
        }
    }
}
