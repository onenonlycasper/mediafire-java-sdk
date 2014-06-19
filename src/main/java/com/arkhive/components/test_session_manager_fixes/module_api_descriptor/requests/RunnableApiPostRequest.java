package com.arkhive.components.test_session_manager_fixes.module_api_descriptor.requests;

import com.arkhive.components.test_session_manager_fixes.Configuration;
import com.arkhive.components.test_session_manager_fixes.module_api.Api;
import com.arkhive.components.test_session_manager_fixes.module_api.responses.ApiResponse;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.interfaces.ApiRequestRunnableCallback;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.interfaces.HttpRequestCallback;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.interfaces.TokenFarmDistributor;
import com.google.gson.Gson;

public class RunnableApiPostRequest<T extends ApiResponse> implements Runnable, HttpRequestCallback {
    private static final String TAG = RunnableApiGetRequest.class.getSimpleName();
    private final HttpProcessor httpPreProcessor;
    private final HttpProcessor httpPostProcessor;
    private ApiRequestRunnableCallback<T> callback;
    private TokenFarmDistributor tokenFarmDistributor;
    private ApiRequestObject apiRequestObject;
    private HttpPeriProcessor httpPeriProcessor;
    private Class<T> clazz;

    public RunnableApiPostRequest(Class<T> clazz,
                                      ApiRequestRunnableCallback<T> callback,
                                      HttpProcessor httpPreProcessor,
                                      HttpProcessor httpPostProcessor,
                                      TokenFarmDistributor tokenFarmDistributor,
                                      HttpPeriProcessor httpPeriProcessor,
                                      ApiRequestObject apiRequestObject) {
        this.clazz = clazz;
        this.callback = callback;
        this.httpPreProcessor = httpPreProcessor;
        this.httpPostProcessor = httpPostProcessor;
        this.tokenFarmDistributor = tokenFarmDistributor;
        this.apiRequestObject = apiRequestObject;
        this.httpPeriProcessor = httpPeriProcessor;
    }

    @Override
    public void run() {
        System.out.println(TAG + " sendRequest()");
        synchronized (this) {
            // notify our callback that the request is being processed now
            if (callback != null) {
                callback.apiRequestProcessStarted();
            }
            // borrow a session token from the TokenFarm
            tokenFarmDistributor.borrowSessionToken(apiRequestObject);
            // send request to http handler
            httpPeriProcessor.sendPostRequest(this, httpPreProcessor, httpPostProcessor, apiRequestObject);
            // wait until we get a response from http handler (or 10 seconds pass)
            try {
                wait(Configuration.DEFAULT_HTTP_CONNECTION_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // try to return the session token to the TokenFarm
            tokenFarmDistributor.returnSessionToken(apiRequestObject);
            // notify our callback that the request is being finished now
            if (callback != null) {
                T response = new Gson().fromJson(Api.getResponseString(apiRequestObject.getHttpResponseString()), clazz);
                callback.apiRequestProcessFinished(response);
            }
        }
    }

    @Override
    public void httpRequestStarted(ApiRequestObject apiRequestObject) {
        System.out.println(TAG + " httpRequestStarted()");
    }

    @Override
    public void httpRequestFinished(ApiRequestObject apiRequestObject) {
        System.out.println(TAG + " httpRequestFinished()");
        synchronized (this) {
            notify();
        }
    }
}