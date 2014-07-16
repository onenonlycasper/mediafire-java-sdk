package com.arkhive.components.core.module_api;

import com.arkhive.components.core.module_api.responses.UploadCheckResponse;
import com.arkhive.components.core.module_api.responses.UploadInstantResponse;
import com.arkhive.components.core.module_api.responses.UploadPollResponse;
import com.arkhive.components.core.module_api.responses.UploadResumableResponse;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_api_descriptor.requests.BlockingApiGetRequest;
import com.arkhive.components.core.module_api_descriptor.requests.BlockingApiGetRequestUploadToken;
import com.arkhive.components.core.module_api_descriptor.requests.BlockingApiPostRequestUploadToken;
import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by  on 6/18/2014.
 */
public class Upload {
    public UploadCheckResponse checkUpload(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_UPLOAD_CHECK);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), UploadCheckResponse.class);
    }

    public UploadInstantResponse instantUpload(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_UPLOAD_INSTANT);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequestUploadToken apiGetRequestRunnable = Api.createBlockingApiGetRequestUploadToken(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), UploadInstantResponse.class);
    }

    public UploadResumableResponse resumableUpload(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters,
            Map<String, String> headers,
            byte[] payload) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_UPLOAD_RESUMABLE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        apiRequestObject.setPostHeaders(headers);
        apiRequestObject.setPayload(payload);
        BlockingApiPostRequestUploadToken apiGetRequestRunnable = Api.createBlockingApiPostRequestUploadToken(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), UploadResumableResponse.class);
    }

    public UploadPollResponse pollUpload(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_UPLOAD_POLL_UPLOAD);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), UploadPollResponse.class);
    }
}
