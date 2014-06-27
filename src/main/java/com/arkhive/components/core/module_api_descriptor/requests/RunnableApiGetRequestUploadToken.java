package com.arkhive.components.core.module_api_descriptor.requests;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.module_api.Api;
import com.arkhive.components.core.module_api.responses.ApiResponse;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_api_descriptor.interfaces.ApiRequestRunnableCallback;
import com.arkhive.components.core.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpRequestCallback;
import com.arkhive.components.core.module_token_farm.interfaces.ActionTokenDistributor;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chris Najar on 6/19/2014.
 */
public class RunnableApiGetRequestUploadToken<T extends ApiResponse> implements Runnable, HttpRequestCallback {
    private static final String TAG = RunnableApiGetRequest.class.getSimpleName();
    private final HttpProcessor httpPreProcessor;
    private final HttpProcessor httpPostProcessor;
    private Class<T> clazz;
    private ApiRequestRunnableCallback<T> callback;
    private ActionTokenDistributor actionTokenDistributor;
    private ApiRequestObject apiRequestObject;
    private HttpPeriProcessor httpPeriProcessor;
    private final Logger logger = LoggerFactory.getLogger(RunnableApiGetRequestUploadToken.class);

    public RunnableApiGetRequestUploadToken(Class<T> clazz,
                                            ApiRequestRunnableCallback<T> callback,
                                            HttpProcessor httpPreProcessor,
                                            HttpProcessor httpPostProcessor,
                                            ActionTokenDistributor actionTokenDistributor,
                                            HttpPeriProcessor httpPeriProcessor,
                                            ApiRequestObject apiRequestObject) {
        this.clazz = clazz;
        this.callback = callback;
        this.httpPreProcessor = httpPreProcessor;
        this.httpPostProcessor = httpPostProcessor;
        this.actionTokenDistributor = actionTokenDistributor;
        this.apiRequestObject = apiRequestObject;
        this.httpPeriProcessor = httpPeriProcessor;
    }

    @Override
    public void run() {
        logger.info(" sendRequest()");
        synchronized (this) {
            // notify our callback that the request is being processed now
            if (callback != null) {
                callback.apiRequestProcessStarted();
            }
            // "borrow" the upload action token from the TokenFarm
            actionTokenDistributor.borrowUploadActionToken(apiRequestObject);
            // send request to http handler
            httpPeriProcessor.sendGetRequest(this, httpPreProcessor, httpPostProcessor, apiRequestObject);
            // wait until we get a response from http handler (or 10 seconds pass)
            try {
                wait(Configuration.DEFAULT_HTTP_CONNECTION_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // notify our callback that the request is being finished now
            if (callback != null) {
                T response = new Gson().fromJson(Api.getResponseString(apiRequestObject.getHttpResponseString()), clazz);
                callback.apiRequestProcessFinished(response);
            }
        }
    }

    @Override
    public void httpRequestStarted(ApiRequestObject apiRequestObject) {
        logger.info(" httpRequestStarted()");
    }

    @Override
    public void httpRequestFinished(ApiRequestObject apiRequestObject) {
        logger.info(" httpRequestFinished()");
        synchronized (this) {
            notify();
        }
    }
}