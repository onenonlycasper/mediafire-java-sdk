package com.arkhive.components.core.module_api;

import com.arkhive.components.core.module_api.responses.SystemGetInfoResponse;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_api_descriptor.requests.BlockingApiGetRequest;
import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by  on 6/18/2014.
 */
public class System {
    public SystemGetInfoResponse getInfo(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_SYSTEM_GET_INFO);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), SystemGetInfoResponse.class);
    }
}
