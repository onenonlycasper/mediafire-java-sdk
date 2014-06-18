package com.arkhive.components.test_session_manager_fixes.module_http_processor;

import com.arkhive.components.test_session_manager_fixes.Configuration;
import com.arkhive.components.test_session_manager_fixes.MediaFire;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.runnables.HttpGetRequestRunnable;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.runnables.HttpPostRequestRunnable;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.interfaces.HttpRequestCallback;
import com.arkhive.components.uploadmanager.PausableThreadPoolExecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by  on 6/15/2014.
 */
public final class HttpPeriProcessor {
    private static final String TAG = HttpPeriProcessor.class.getSimpleName();
    private int connectionTimeout = Configuration.DEFAULT_HTTP_CONNECTION_TIMEOUT;
    private int readTimeout = Configuration.DEFAULT_HTTP_READ_TIMEOUT;
    private BlockingQueue<Runnable> workQueue;
    private PausableThreadPoolExecutor executor;

    public HttpPeriProcessor() {
        workQueue = new LinkedBlockingQueue<Runnable>();
        executor = new PausableThreadPoolExecutor(10, workQueue);
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void sendGetRequest(HttpRequestCallback callback, HttpProcessor httpPreProcessor, HttpProcessor httpPostProcessor, ApiRequestObject apiRequestObject) {
        System.out.println(TAG + " sendGetRequest()");
        HttpGetRequestRunnable httpGetRequestRunnable = new HttpGetRequestRunnable(callback, httpPreProcessor, httpPostProcessor, apiRequestObject, this);
        executor.execute(httpGetRequestRunnable);
    }

    public void sendPostRequest(HttpRequestCallback callback, HttpProcessor httpPreProcessor, HttpProcessor httpPostProcessor, ApiRequestObject apiRequestObject) {
        System.out.println(TAG + " sendPostRequest()");
        HttpPostRequestRunnable httpPostRequestRunnable = new HttpPostRequestRunnable(callback, httpPreProcessor, httpPostProcessor, apiRequestObject, this);
        executor.execute(httpPostRequestRunnable);
    }

    public void setCorePoolSize(Configuration configuration) {
        executor.setCorePoolSize(configuration.getHttpPoolSize());
    }

    public void shutdown() {
        System.out.println(TAG + " HttpPeriProcessor shutting down");
        executor.pause();
        workQueue.clear();
        executor.shutdownNow();
    }
}
