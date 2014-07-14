package com.arkhive.components.core.module_api;

import com.arkhive.components.core.module_api.responses.ApiResponse;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_api_descriptor.interfaces.ApiRequestRunnableCallback;
import com.arkhive.components.core.module_api_descriptor.requests.*;
import com.arkhive.components.core.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.core.module_http_processor.pre_and_post_processors.*;
import com.arkhive.components.core.module_token_farm.interfaces.ActionTokenDistributor;
import com.arkhive.components.core.module_token_farm.interfaces.SessionTokenDistributor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by  on 6/17/2014.
 */
public class Api {
    private static final String FAIL_RES = "{\"response\":{\"message\":\"Unknown API error\",\"result\":\"Error\"}}";
    private static HttpPeriProcessor httpPeriProcessor;
    private static SessionTokenDistributor sessionTokenDistributor;
    private static ActionTokenDistributor actionTokenDistributor;
    public File file;
    public Folder folder;
    public User user;
    public System system;
    public Device device;
    public Upload upload;
    public Contact contact;

    public Api(SessionTokenDistributor sessionTokenDistributor, ActionTokenDistributor actionTokenDistributor, HttpPeriProcessor httpPeriProcessor) {
        this.sessionTokenDistributor = sessionTokenDistributor;
        this.actionTokenDistributor = actionTokenDistributor;
        this.httpPeriProcessor = httpPeriProcessor;
        file = new File();
        folder = new Folder();
        user = new User();
        system = new System();
        device = new Device();
        upload = new Upload();
        contact = new Contact();
    }

    public static BlockingApiGetRequest createBlockingApiGetRequest(
            ApiRequestObject apiRequestObject) {
        return new BlockingApiGetRequest(
                new ApiRequestHttpPreProcessor(),
                new ApiRequestHttpPostProcessor(),
                sessionTokenDistributor,
                httpPeriProcessor,
                apiRequestObject);
    }

    public static BlockingApiHttpsGetRequest createBlockingApiHttpsGetRequest(
            ApiRequestObject apiRequestObject) {
        return new BlockingApiHttpsGetRequest(
                new ApiRequestHttpPreProcessor(),
                new ApiRequestHttpPostProcessor(),
                sessionTokenDistributor,
                httpPeriProcessor,
                apiRequestObject);
    }

    public static BlockingApiHttpsPostRequest createBlockingApiHttpsPostRequest(ApiRequestObject apiRequestObject) {
        return new BlockingApiHttpsPostRequest(
                new ApiPostRequestHttpsPreProcessor(),
                new ApiPostRequestHttpsPostProcessor(),
                httpPeriProcessor,
                apiRequestObject);
    }

    public static <T extends ApiResponse> RunnableApiGetRequest<T> createApiGetRequestRunnable(
            Class<T> clazz,
            ApiRequestRunnableCallback<T> apiRequestRunnableCallback,
            ApiRequestObject apiRequestObject) {
        return new RunnableApiGetRequest<T>(
                clazz,
                apiRequestRunnableCallback,
                new ApiRequestHttpPreProcessor(),
                new ApiRequestHttpPostProcessor(),
                sessionTokenDistributor,
                httpPeriProcessor,
                apiRequestObject);
    }

    public static BlockingApiGetRequestUploadToken createBlockingApiGetRequestUploadToken(
            ApiRequestObject apiRequestObject) {
        return new BlockingApiGetRequestUploadToken(
                new UploadTokenHttpPreProcessor(),
                new UploadTokenHttpPostProcessor(),
                actionTokenDistributor,
                httpPeriProcessor,
                apiRequestObject);
    }

    public static <T extends ApiResponse>RunnableApiGetRequestUploadToken createApiGetRequestRunnableUploadToken(
            Class<T> uploadInstantResponseClass,
            ApiRequestRunnableCallback<T> apiRequestRunnableCallback,
            ApiRequestObject apiRequestObject) {
        return new RunnableApiGetRequestUploadToken<T>(
                uploadInstantResponseClass,
                apiRequestRunnableCallback,
                new UploadTokenHttpPreProcessor(),
                new UploadTokenHttpPostProcessor(),
                actionTokenDistributor,
                httpPeriProcessor,
                apiRequestObject);
    }

    public static BlockingApiPostRequestUploadToken createBlockingApiPostRequestUploadToken(
            ApiRequestObject apiRequestObject) {
        return new BlockingApiPostRequestUploadToken(
                new UploadTokenHttpPreProcessor(),
                new UploadTokenHttpPostProcessor(),
                actionTokenDistributor,
                httpPeriProcessor,
                apiRequestObject);
    }

    public static <T extends ApiResponse>RunnableApiPostRequestUploadToken createApiPostRequestRunnableUploadToken(
            Class<T> uploadResumableResponseClass,
            ApiRequestRunnableCallback<T> apiRequestRunnableCallback,
            ApiRequestObject apiRequestObject) {
        return new RunnableApiPostRequestUploadToken<T>(
                uploadResumableResponseClass,
                apiRequestRunnableCallback,
                new UploadTokenHttpPreProcessor(),
                new UploadTokenHttpPostProcessor(),
                actionTokenDistributor,
                httpPeriProcessor,
                apiRequestObject);
    }

    public String requestImageActionToken() {
        ApiRequestObject apiRequestObject = new ApiRequestObject(null, null);
//        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_USER_GET_ACTION_TOKEN);
        actionTokenDistributor.borrowImageActionToken(apiRequestObject);
        if (apiRequestObject.getActionToken() != null) {
            return apiRequestObject.getActionToken().getTokenString();
        } else {
            return null;
        }
    }

    public String requestUploadActionToken() {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_USER_GET_ACTION_TOKEN);
        actionTokenDistributor.borrowUploadActionToken(apiRequestObject);
        if (apiRequestObject.getActionToken() != null) {
            return apiRequestObject.getActionToken().getTokenString();
        } else {
            return null;
        }
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
        if (response == null || response.isEmpty()) {
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
        if (response == null || response.isEmpty()) {
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
