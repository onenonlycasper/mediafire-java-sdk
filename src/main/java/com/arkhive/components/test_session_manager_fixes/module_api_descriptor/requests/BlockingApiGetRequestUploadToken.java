package com.arkhive.components.test_session_manager_fixes.module_api_descriptor.requests;

import com.arkhive.components.test_session_manager_fixes.Configuration;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.interfaces.HttpRequestCallback;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.interfaces.ActionTokenDistributor;

/**
 * Created by Chris Najar on 6/19/2014.
 */
public class BlockingApiGetRequestUploadToken implements HttpRequestCallback {
    private static final String TAG = BlockingApiGetRequest.class.getSimpleName();
    private final HttpProcessor httpPreProcessor;
    private final HttpProcessor httpPostProcessor;
    private ActionTokenDistributor actionTokenDistributor;
    private ApiRequestObject apiRequestObject;
    private HttpPeriProcessor httpPeriProcessor;

    public BlockingApiGetRequestUploadToken(HttpProcessor httpPreProcessor,
                                            HttpProcessor httpPostProcessor,
                                            ActionTokenDistributor actionTokenDistributor,
                                            HttpPeriProcessor httpPeriProcessor,
                                            ApiRequestObject apiRequestObject) {
        this.httpPreProcessor = httpPreProcessor;
        this.httpPostProcessor = httpPostProcessor;
        this.actionTokenDistributor = actionTokenDistributor;
        this.apiRequestObject = apiRequestObject;
        this.httpPeriProcessor = httpPeriProcessor;
    }

    public ApiRequestObject sendRequest() {
        System.out.println(TAG + " sendRequest()");
        synchronized (this) {
            // "borrow" an upload action token from the TokenFarm
            actionTokenDistributor.borrowUploadActionToken(apiRequestObject);
            // send request to http handler
            httpPeriProcessor.sendGetRequest(this, httpPreProcessor, httpPostProcessor, apiRequestObject);
            // wait until we get a response from http handler (or 10 seconds pass)
            try {
                wait(Configuration.DEFAULT_HTTP_CONNECTION_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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
