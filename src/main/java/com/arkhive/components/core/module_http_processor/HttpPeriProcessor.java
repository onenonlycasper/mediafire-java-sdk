package com.arkhive.components.core.module_http_processor;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpRequestCallback;
import com.arkhive.components.core.module_http_processor.runnables.HttpGetRequestRunnable;
import com.arkhive.components.core.module_http_processor.runnables.HttpPostRequestRunnable;
import com.arkhive.components.core.module_http_processor.runnables.HttpsGetRequestRunnable;
import com.arkhive.components.core.module_http_processor.runnables.HttpsPostRequestRunnable;
import com.arkhive.components.uploadmanager.PausableThreadPoolExecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by  on 6/15/2014.
 */
public final class HttpPeriProcessor {
    private static final String TAG = HttpPeriProcessor.class.getCanonicalName();
    private final Configuration configuration;
    private final BlockingQueue<Runnable> workQueue;
    private final PausableThreadPoolExecutor executor;

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
        Configuration.getErrorTracker().i(TAG, "sendGetRequest()");
        HttpGetRequestRunnable httpGetRequestRunnable = new HttpGetRequestRunnable(callback, httpPreProcessor, httpPostProcessor, apiRequestObject, this);
        executor.execute(httpGetRequestRunnable);
    }

    public void sendHttpsGetRequest(HttpRequestCallback callback, HttpProcessor httpPreProcessor, HttpProcessor httpPostProcessor, ApiRequestObject apiRequestObject) {
        Configuration.getErrorTracker().i(TAG, "sendHttpsGetRequest");
        Configuration.getErrorTracker().i(TAG, "sendHttpsGetRequest() on thread: " + Thread.currentThread().getName());
        HttpsGetRequestRunnable httpsGetRequestRunnable = new HttpsGetRequestRunnable(callback, httpPreProcessor, httpPostProcessor, apiRequestObject, this);
        executor.execute(httpsGetRequestRunnable);
    }

    public void sendPostRequest(HttpRequestCallback callback, HttpProcessor httpPreProcessor, HttpProcessor httpPostProcessor, ApiRequestObject apiRequestObject) {
        Configuration.getErrorTracker().i(TAG, "sendPostRequest()");
        HttpPostRequestRunnable httpPostRequestRunnable = new HttpPostRequestRunnable(callback, httpPreProcessor, httpPostProcessor, apiRequestObject, this);
        executor.execute(httpPostRequestRunnable);
    }

    public void sendHttpsPostRequest(HttpRequestCallback callback, HttpProcessor httpPreProcessor, HttpProcessor httpPostProcessor, ApiRequestObject apiRequestObject) {
        Configuration.getErrorTracker().i(TAG, "sendHttpsPostRequest");
        HttpsPostRequestRunnable httpsPostRequestRunnable = new HttpsPostRequestRunnable(callback, httpPreProcessor, httpPostProcessor, apiRequestObject, this);
        executor.execute(httpsPostRequestRunnable);
    }

    public void shutdown() {
        Configuration.getErrorTracker().i(TAG, "HttpPeriProcessor shutting down");
        executor.pause();
        workQueue.clear();
    }
}
