package com.arkhive.components.core.module_api;

import com.arkhive.components.core.module_api.responses.UserSetAvatarResponse;
import com.arkhive.components.core.module_api.responses.ApiResponse;
import com.arkhive.components.core.module_api.responses.UserGetAvatarResponse;
import com.arkhive.components.core.module_api.responses.UserGetInfoResponse;
import com.arkhive.components.core.module_api.responses.UserRegisterResponse;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_api_descriptor.interfaces.ApiRequestRunnableCallback;
import com.arkhive.components.core.module_api_descriptor.requests.BlockingApiGetRequest;
import com.arkhive.components.core.module_api_descriptor.requests.RunnableApiGetRequest;
import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by  on 6/18/2014.
 */
public class User {
    public UserGetInfoResponse getInfo(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_USER_GET_INFO);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), UserGetInfoResponse.class);
    }

    public Runnable getInfo(
            ApiRequestRunnableCallback callback,
            Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_USER_GET_INFO);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest =
                Api.createApiGetRequestRunnable(UserGetInfoResponse.class, callback,  apiRequestObject);
        return runnableApiGetRequest;
    }

    public UserRegisterResponse register(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_USER_REGISTER);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), UserRegisterResponse.class);
    }

    public Runnable register(
            ApiRequestRunnableCallback callback,
            Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTPS, ApiUris.URI_USER_REGISTER);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest =
                Api.createApiGetRequestRunnable(UserRegisterResponse.class, callback,  apiRequestObject);
        return runnableApiGetRequest;
    }

    public ApiResponse linkFacebook(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_USER_LINK_FACEBOOK);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), ApiResponse.class);
    }

    public Runnable linkFacebook(
            ApiRequestRunnableCallback callback,
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_USER_LINK_FACEBOOK);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest =
                Api.createApiGetRequestRunnable(ApiResponse.class, callback,  apiRequestObject);
        return runnableApiGetRequest;
    }

    public ApiResponse linkTwitter(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_USER_LINK_TWITTER);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), ApiResponse.class);
    }

    public Runnable linkTwitter(
            ApiRequestRunnableCallback callback,
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_USER_LINK_TWITTER);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest =
                Api.createApiGetRequestRunnable(ApiResponse.class, callback,  apiRequestObject);
        return runnableApiGetRequest;
    }

    public ApiResponse unlinkFacebook(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_USER_UNLINK_FACEBOOK);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), ApiResponse.class);
    }

    public Runnable unlinkFacebook(
            ApiRequestRunnableCallback callback,
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_USER_UNLINK_FACEBOOK);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest =
                Api.createApiGetRequestRunnable(ApiResponse.class, callback,  apiRequestObject);
        return runnableApiGetRequest;
    }

    public ApiResponse unlinkTwitter(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_USER_UNLINK_TWITTER);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), ApiResponse.class);
    }

    public Runnable unlinkTwitter(
            ApiRequestRunnableCallback callback,
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_USER_UNLINK_TWITTER);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest =
                Api.createApiGetRequestRunnable(ApiResponse.class, callback,  apiRequestObject);
        return runnableApiGetRequest;
    }

    public ApiResponse getAvatar(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_USER_GET_AVATAR);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), ApiResponse.class);
    }

    public Runnable getAvatar(
            ApiRequestRunnableCallback callback,
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_USER_GET_AVATAR);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest =
                Api.createApiGetRequestRunnable(UserGetAvatarResponse.class, callback,  apiRequestObject);
        return runnableApiGetRequest;
    }

    public ApiResponse setAvatar(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_USER_SET_AVATAR);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), ApiResponse.class);
    }

    public Runnable setAvatar(
            ApiRequestRunnableCallback callback,
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_USER_SET_AVATAR);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest =
                Api.createApiGetRequestRunnable(UserSetAvatarResponse.class, callback,  apiRequestObject);
        return runnableApiGetRequest;
    }
}
