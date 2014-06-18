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
public class Upload {
    public void checkUpload(Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_UPLOAD_CHECK);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
    }

    public void instantUpload(Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_UPLOAD_INSTANT);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
    }

    public void resumableUpload(Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_UPLOAD_RESUMABLE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
    }

    public void pollUpload(Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_UPLOAD_POLL_UPLOAD);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
    }

    public Runnable checkUpload(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_UPLOAD_CHECK);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest = Api.createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
        return runnableApiGetRequest;
    }

    public Runnable instantUpload(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_UPLOAD_INSTANT);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest = Api.createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
        return runnableApiGetRequest;
    }

    public Runnable resumableUpload(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_UPLOAD_RESUMABLE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest = Api.createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
        return runnableApiGetRequest;
    }

    public Runnable pollUpload(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_UPLOAD_POLL_UPLOAD);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest = Api.createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
        return runnableApiGetRequest;
    }
}
