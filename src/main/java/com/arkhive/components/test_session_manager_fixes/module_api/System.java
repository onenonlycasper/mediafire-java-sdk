package com.arkhive.components.test_session_manager_fixes.module_api;

import com.arkhive.components.test_session_manager_fixes.MediaFire;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.interfaces.ApiRequestRunnableCallback;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.requests.BlockingApiGetRequest;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.requests.RunnableApiGetRequest;

import java.util.Map;

/**
 * Created by Chris Najar on 6/18/2014.
 */
public class System {
    public Runnable getInfo(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_SYSTEM_GET_INFO);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest = Api.createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
        return runnableApiGetRequest;
    }

    public void getInfo(Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_SYSTEM_GET_INFO);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
    }
}
