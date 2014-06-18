package com.arkhive.components.test_session_manager_fixes.module_api;

import com.arkhive.components.test_session_manager_fixes.module_api.responses.ApiResponse;
import com.arkhive.components.test_session_manager_fixes.module_api.responses.ContactResponse;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.interfaces.ApiRequestRunnableCallback;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.requests.BlockingApiGetRequest;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.requests.RunnableApiGetRequest;
import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by on 6/18/2014.
 */
public class Contact {
    public ApiResponse addContact(Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_CONTACT_ADD);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), ApiResponse.class);
    }

    public ApiResponse deleteContact(Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_CONTACT_DELETE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), ApiResponse.class);
    }

    public ContactResponse fetchContacts(Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_CONTACT_FETCH);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), ContactResponse.class);
    }

    public Runnable addContact(ApiRequestRunnableCallback callback, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_CONTACT_ADD);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest = Api.createApiGetRequestRunnable(callback, apiRequestObject);
        return runnableApiGetRequest;
    }

    public Runnable deleteContact(ApiRequestRunnableCallback callback, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_CONTACT_DELETE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest = Api.createApiGetRequestRunnable(callback, apiRequestObject);
        return runnableApiGetRequest;
    }

    public Runnable fetchContacts(ApiRequestRunnableCallback callback, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_CONTACT_FETCH);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest = Api.createApiGetRequestRunnable(callback, apiRequestObject);
        return runnableApiGetRequest;
    }
}
