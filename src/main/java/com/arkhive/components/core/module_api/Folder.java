package com.arkhive.components.core.module_api;

import com.arkhive.components.core.module_api.responses.*;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_api_descriptor.interfaces.ApiRequestRunnableCallback;
import com.arkhive.components.core.module_api_descriptor.requests.BlockingApiGetRequest;
import com.arkhive.components.core.module_api_descriptor.requests.RunnableApiGetRequest;
import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by Chris Najar on 6/18/2014.
 */
public class Folder {
    public Runnable copy(
            ApiRequestRunnableCallback callback,
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_FOLDER_COPY);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest =
                Api.createApiGetRequestRunnable(FolderCopyResponse.class, callback, apiRequestObject);
        return runnableApiGetRequest;
    }

    public FolderCopyResponse copy(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_FOLDER_COPY);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), FolderCopyResponse.class);
    }

    public Runnable getRevision(
            ApiRequestRunnableCallback callback,
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_FOLDER_GET_REVISION);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest =
                Api.createApiGetRequestRunnable(FolderGetRevisionResponse.class, callback, apiRequestObject);
        return runnableApiGetRequest;
    }

    public FolderGetRevisionResponse getRevision(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_FOLDER_GET_REVISION);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), FolderGetRevisionResponse.class);
    }

    public Runnable purge(
            ApiRequestRunnableCallback callback,
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_FOLDER_PURGE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest =
                Api.createApiGetRequestRunnable(FolderPurgeResponse.class, callback, apiRequestObject);
        return runnableApiGetRequest;
    }

    public FolderPurgeResponse purge(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_FOLDER_PURGE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), FolderPurgeResponse.class);
    }

    public Runnable move(
            ApiRequestRunnableCallback callback,
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_FOLDER_MOVE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest =
                Api.createApiGetRequestRunnable(FolderMoveResponse.class, callback, apiRequestObject);
        return runnableApiGetRequest;
    }

    public FolderMoveResponse move(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_FOLDER_MOVE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), FolderMoveResponse.class);
    }

    public Runnable create(
            ApiRequestRunnableCallback callback,
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_FOLDER_CREATE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest =
                Api.createApiGetRequestRunnable(FolderCreateResponse.class, callback, apiRequestObject);
        return runnableApiGetRequest;
    }

    public FolderCreateResponse create(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_FOLDER_CREATE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), FolderCreateResponse.class);
    }

    public Runnable delete(
            ApiRequestRunnableCallback callback,
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_FOLDER_DELETE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest =
                Api.createApiGetRequestRunnable(FolderDeleteResponse.class, callback, apiRequestObject);
        return runnableApiGetRequest;
    }

    public FolderDeleteResponse delete(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_FOLDER_DELETE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), FolderDeleteResponse.class);
    }


    public Runnable search(
            ApiRequestRunnableCallback callback,
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_FOLDER_SEARCH);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest =
                Api.createApiGetRequestRunnable(FolderSearchResponse.class, callback, apiRequestObject);
        return runnableApiGetRequest;
    }

    public FolderSearchResponse search(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_FOLDER_SEARCH);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), FolderSearchResponse.class);
    }

    public Runnable update(
            ApiRequestRunnableCallback callback,
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_FOLDER_UPDATE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest =
                Api.createApiGetRequestRunnable(FolderUpdateResponse.class, callback, apiRequestObject);
        return runnableApiGetRequest;
    }

    public FolderUpdateResponse update(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_FOLDER_UPDATE);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), FolderUpdateResponse.class);
    }

    public Runnable getContents(
            ApiRequestRunnableCallback callback,
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_FOLDER_GET_CONTENT);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest =
                Api.createApiGetRequestRunnable(FolderGetContentsResponse.class, callback, apiRequestObject);
        return runnableApiGetRequest;
    }

    public FolderGetContentsResponse getContents(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_FOLDER_GET_CONTENT);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), FolderGetContentsResponse.class);
    }

    public Runnable getInfo(
            ApiRequestRunnableCallback callback,
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_FOLDER_GET_INFO);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        RunnableApiGetRequest runnableApiGetRequest =
                Api.createApiGetRequestRunnable(FolderGetInfoResponse.class, callback, apiRequestObject);
        return runnableApiGetRequest;
    }


    public FolderGetInfoResponse getInfo(
            Map<String, String> requiredParameters,
            Map<String, String> optionalParameters) {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_FOLDER_GET_INFO);
        apiRequestObject.setOptionalParameters(optionalParameters);
        apiRequestObject.setRequiredParameters(requiredParameters);
        BlockingApiGetRequest apiGetRequestRunnable = Api.createBlockingApiGetRequest(apiRequestObject);
        apiGetRequestRunnable.sendRequest();
        String response = apiRequestObject.getHttpResponseString();
        return new Gson().fromJson(Api.getResponseString(response), FolderGetInfoResponse.class);

    }
}
