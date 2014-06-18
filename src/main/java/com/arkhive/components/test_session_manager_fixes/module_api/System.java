package com.arkhive.components.test_session_manager_fixes.module_api;

import com.arkhive.components.test_session_manager_fixes.module_api.responses.SystemGetInfoResponse;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.interfaces.ApiRequestRunnableCallback;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.requests.BlockingApiGetRequest;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.requests.RunnableApiGetRequest;
import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by Chris Najar on 6/18/2014.
 */
public class System {
    public Runnable getInfo(ApiRequestRunnableCallback callback,Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_SYSTEM_GET_INFO);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest = Api.createApiGetRequestRunnable(callback, apiRequestObject);
        return runnableApiGetRequest;
    }

    public SystemGetInfoResponse getInfo(Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_SYSTEM_GET_INFO);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), SystemGetInfoResponse.class);
    }
}
