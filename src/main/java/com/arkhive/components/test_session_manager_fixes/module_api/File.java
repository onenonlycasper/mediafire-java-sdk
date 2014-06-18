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
public class File {
    public Runnable copy(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FILE_COPY);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest = Api.createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
        return runnableApiGetRequest;
    }

    public Runnable delete(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FILE_DELETE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest = Api.createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
        return runnableApiGetRequest;
    }

    public Runnable purge(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FILE_PURGE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest = Api.createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
        return runnableApiGetRequest;
    }

    public Runnable move(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FILE_MOVE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest = Api.createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
        return runnableApiGetRequest;
    }

    public Runnable update(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FILE_UPDATE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest = Api.createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
        return runnableApiGetRequest;
    }

    public Runnable getInfo(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FILE_GET_INFO);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest = Api.createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
        return runnableApiGetRequest;
    }


    public Runnable getLinks(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FILE_GET_LINKS);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest = Api.createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
        return runnableApiGetRequest;
    }

    public void copy(Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FILE_COPY);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
    }

    public void delete(Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FILE_DELETE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
    }

    public void purge(Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FILE_PURGE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
    }

    public void move(Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FILE_MOVE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
    }

    public void update(Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FILE_UPDATE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
    }

    public void getInfo(Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FILE_GET_INFO);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
    }


    public void getLinks(Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FILE_GET_LINKS);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
    }
}
