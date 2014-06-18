package com.arkhive.components.test_session_manager_fixes.module_api_descriptor.requests;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.interfaces.HttpRequestCallback;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.interfaces.TokenFarmDistributor;

/**
 * Created by Chris Najar on 6/17/2014.
 */
public class BlockingApiPostRequest implements HttpRequestCallback {
    private static final String TAG = BlockingApiGetRequest.class.getSimpleName();
    private final HttpProcessor httpPreProcessor;
    private final HttpProcessor httpPostProcessor;
    private TokenFarmDistributor tokenFarmDistributor;
    private ApiRequestObject apiRequestObject;
    private HttpPeriProcessor httpPeriProcessor;

    public BlockingApiPostRequest(HttpProcessor httpPreProcessor,
                                 HttpProcessor httpPostProcessor,
                                 TokenFarmDistributor tokenFarmDistributor,
                                 HttpPeriProcessor httpPeriProcessor,
                                 ApiRequestObject apiRequestObject) {
        this.httpPreProcessor = httpPreProcessor;
        this.httpPostProcessor = httpPostProcessor;
        this.tokenFarmDistributor = tokenFarmDistributor;
        this.apiRequestObject = apiRequestObject;
        this.httpPeriProcessor = httpPeriProcessor;
    }

    public ApiRequestObject sendRequest() {
        System.out.println(TAG + " sendRequest()");
        synchronized (this) {
            // borrow a session token from the TokenFarm
            tokenFarmDistributor.borrowSessionToken(apiRequestObject);

            // send request to http handler
            httpPeriProcessor.sendPostRequest(this, httpPreProcessor, httpPostProcessor, apiRequestObject);
            // wait until we get a response from http handler (or 10 seconds pass)
            try {
                wait(35000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // try to return the session token to the TokenFarm
            tokenFarmDistributor.returnSessionToken(apiRequestObject);
            return apiRequestObject;
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