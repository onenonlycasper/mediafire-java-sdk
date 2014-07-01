package com.arkhive.components.core.module_http_processor;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpRequestCallback;
import com.arkhive.components.core.module_http_processor.runnables.HttpGetRequestRunnable;
import com.arkhive.components.core.module_http_processor.runnables.HttpPostRequestRunnable;
import com.arkhive.components.uploadmanager.PausableThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by  on 6/15/2014.
 */
public final class HttpPeriProcessor {
    private final Configuration configuration;
    private final BlockingQueue<Runnable> workQueue;
    private final PausableThreadPoolExecutor executor;
    private final Logger logger = LoggerFactory.getLogger(HttpPeriProcessor.class);

    public HttpPeriProcessor(Configuration configuration) {
        this.configuration = configuration;
        workQueue = new LinkedBlockingQueue<Runnable>();
        executor = new PausableThreadPoolExecutor(configuration.getHttpPoolSize(), workQueue);
    }

    public int getConnectionTimeout() {
        return configuration.getHttpConnectionTimeout();
    }

    public int getReadTimeout() {
        return configuration.getHttpReadTimeout();
    }

    public void sendGetRequest(HttpRequestCallback callback, HttpProcessor httpPreProcessor, HttpProcessor httpPostProcessor, ApiRequestObject apiRequestObject) {
        logger.info(" sendGetRequest()");
        HttpGetRequestRunnable httpGetRequestRunnable = new HttpGetRequestRunnable(callback, httpPreProcessor, httpPostProcessor, apiRequestObject, this);
        executor.execute(httpGetRequestRunnable);
    }

    public void sendHttpsGetRequest(HttpRequestCallback callback, HttpProcessor httpPreProcessor, HttpProcessor httpPostProcessor, ApiRequestObject apiRequestObject) {
        HttpGetRequestRunnable httpsGetRequestRunnable = new HttpGetRequestRunnable(callback, httpPreProcessor, httpPostProcessor, apiRequestObject, this);
        executor.execute(httpsGetRequestRunnable);
    }

    public void sendPostRequest(HttpRequestCallback callback, HttpProcessor httpPreProcessor, HttpProcessor httpPostProcessor, ApiRequestObject apiRequestObject) {
        logger.info(" sendPostRequest()");
        HttpPostRequestRunnable httpPostRequestRunnable = new HttpPostRequestRunnable(callback, httpPreProcessor, httpPostProcessor, apiRequestObject);
        executor.execute(httpPostRequestRunnable);
    }

    public void sendHttpsPostRequest(HttpRequestCallback callback, HttpProcessor httpPreProcessor, HttpProcessor httpPostProcessor, ApiRequestObject apiRequestObject) {
        HttpPostRequestRunnable httpsPostRequestRunnable = new HttpPostRequestRunnable(callback, httpPreProcessor, httpPostProcessor, apiRequestObject);
        executor.execute(httpsPostRequestRunnable);
    }

    public void shutdown() {
        logger.info(" HttpPeriProcessor shutting down");
        executor.pause();
        workQueue.clear();
    }
}
