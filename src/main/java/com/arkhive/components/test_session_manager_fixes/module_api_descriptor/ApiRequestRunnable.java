package com.arkhive.components.test_session_manager_fixes.module_api_descriptor;

import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.request_runnables.HttpRequestCallback;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.TokenFarmDistributor;

/**
 * Created by Chris Najar on 6/16/2014.
 */
public class ApiRequestRunnable implements Runnable, HttpRequestCallback {
    private static final String TAG = ApiRequestRunnable.class.getSimpleName();
    private ApiRequestRunnableCallback callback;
    private TokenFarmDistributor tokenFarmDistributor;
    private ApiRequestObject apiRequestObject;
    private HttpPeriProcessor httpPeriProcessor;

    public ApiRequestRunnable(ApiRequestRunnableCallback callback,
                              TokenFarmDistributor tokenFarmDistributor,
                              HttpPeriProcessor httpPeriProcessor,
                              ApiRequestObject apiRequestObject) {
        this.callback = callback;
        this.tokenFarmDistributor = tokenFarmDistributor;
        this.apiRequestObject = apiRequestObject;
        this.httpPeriProcessor = httpPeriProcessor;
    }

    @Override
    public void run() {
        System.out.println(TAG + " run()");
        // notify our callback that the request is being processed now
        if (callback != null) {
            callback.apiRequestProcessStarted();
        }
        // borrow a session token from the TokenFarm
        tokenFarmDistributor.borrowSessionToken(apiRequestObject);

        // send request to http handler
        httpPeriProcessor.sendGetRequest(this, apiRequestObject);
        // wait until we get a response from http handler (or 10 seconds pass)
        try {
            wait(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // try to return the session token to the TokenFarm
        tokenFarmDistributor.returnSessionToken(apiRequestObject);
        // notify our callback that the request is being finished now
        if (callback != null) {
            callback.apiRequestProcessFinished(apiRequestObject);
        }
    }

    @Override
    public void httpRequestStarted(ApiRequestObject apiRequestObject) {
        System.out.println(TAG + " httpRequestStarted()");
    }

    @Override
    public void httpRequestFinished(ApiRequestObject apiRequestObject) {
        System.out.println(TAG + " httpRequestFinished()");
        notify();
    }
}
