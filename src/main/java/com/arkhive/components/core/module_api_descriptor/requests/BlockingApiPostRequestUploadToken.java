package com.arkhive.components.core.module_api_descriptor.requests;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpRequestCallback;
import com.arkhive.components.core.module_token_farm.interfaces.ActionTokenDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chris Najar on 6/19/2014.
 */
public class BlockingApiPostRequestUploadToken implements HttpRequestCallback {
    private static final String TAG = BlockingApiGetRequest.class.getSimpleName();
    private final HttpProcessor httpPreProcessor;
    private final HttpProcessor httpPostProcessor;
    private ActionTokenDistributor actionTokenDistributor;
    private ApiRequestObject apiRequestObject;
    private HttpPeriProcessor httpPeriProcessor;
    private final Logger logger = LoggerFactory.getLogger(BlockingApiPostRequestUploadToken.class);

    public BlockingApiPostRequestUploadToken(HttpProcessor httpPreProcessor,
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
        logger.info(" sendRequest()");
        synchronized (this) {
            // "borrow" an upload action token from the TokenFarm
            actionTokenDistributor.borrowUploadActionToken(apiRequestObject);
            // send request to http handler
            httpPeriProcessor.sendPostRequest(this, httpPreProcessor, httpPostProcessor, apiRequestObject);
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