package com.arkhive.components.core.module_api;

import com.arkhive.components.core.module_api.responses.DeviceGetChangesResponse;
import com.arkhive.components.core.module_api.responses.DeviceGetStatusResponse;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_api_descriptor.requests.BlockingApiGetRequest;
import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by  on 6/18/2014.
 */
public class Device {
    public DeviceGetChangesResponse getChanges(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_DEVICE_GET_CHANGES);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), DeviceGetChangesResponse.class);
    }

    public DeviceGetStatusResponse getStatus(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_DEVICE_GET_STATUS);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), DeviceGetStatusResponse.class);
    }
}
