package com.arkhive.components.core.module_http_processor.runnables;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpRequestCallback;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by on 7/2/2014.
 */
public abstract class HttpRequestRunnable implements Runnable {
    private static final String TAG = HttpRequestRunnable.class.getCanonicalName();
    protected final ApiRequestObject apiRequestObject;
    protected final HttpRequestCallback callback;
    protected final HttpProcessor httpPreProcessor;
    protected final HttpProcessor httpPostProcessor;
    protected final HttpPeriProcessor httpPeriProcessor;
    protected int connectionTimeout;
    protected int readTimeout;

    public HttpRequestRunnable(HttpRequestCallback callback, HttpProcessor httpPreProcessor, HttpProcessor httpPostProcessor, ApiRequestObject apiRequestObject, HttpPeriProcessor httpPeriProcessor) {
        this.apiRequestObject = apiRequestObject;
        this.callback = callback;
        this.httpPreProcessor = httpPreProcessor;
        this.httpPostProcessor = httpPostProcessor;
        this.httpPeriProcessor = httpPeriProcessor;
    }

    protected abstract void doRequest();

    @Override
    public final void run() {
        Configuration.getErrorTracker().i(TAG, "run()");
        notifyHttpRequestStarted();
        setTimeouts();
        doPreProcess();
        doRequest();
        sendApiErrorIfExists();
        doPostProcess();
        notifyHttpRequestFinished();
    }

    private void notifyHttpRequestStarted() {
        Configuration.getErrorTracker().i(TAG, "notifyHttpRequestStarted()");
        if (callback != null) {
            callback.httpRequestStarted(apiRequestObject);
        }
    }

    private void doPreProcess() {
        Configuration.getErrorTracker().i(TAG, "doPreProcess()");
        if (httpPreProcessor != null) {
            httpPreProcessor.processApiRequestObject(apiRequestObject);
        }
    }

    private void setTimeouts() {
        Configuration.getErrorTracker().i(TAG, "setTimeouts()");
        connectionTimeout = httpPeriProcessor.getConnectionTimeout();
        readTimeout = httpPeriProcessor.getReadTimeout();
    }

    private void doPostProcess() {
        Configuration.getErrorTracker().i(TAG, "doPostProcess()");
        if (httpPostProcessor != null) {
            httpPostProcessor.processApiRequestObject(apiRequestObject);
        }
    }

    private void notifyHttpRequestFinished() {
        Configuration.getErrorTracker().i(TAG, "notifyHttpRequestFinished()");
        if (callback != null) {
            callback.httpRequestFinished(apiRequestObject);
        }
    }

    protected void sendApiErrorIfExists() {
        Configuration.getErrorTracker().i(TAG, "sendApiErrorIfExists()");
        if (Configuration.getErrorTracker() == null) {
            return;
        }

        if (apiRequestObject == null) {
            return;
        }

        if (apiRequestObject.getApiResponse() != null && apiRequestObject.getApiResponse().hasError()) {
            Configuration.getErrorTracker().apiError(HttpsGetRequestRunnable.class.getCanonicalName(), apiRequestObject);
        }

        StringBuilder builder = new StringBuilder();
        if (apiRequestObject.getExceptionsDuringRequest() != null && !apiRequestObject.getExceptionsDuringRequest().isEmpty()) {
            for (Exception exception : apiRequestObject.getExceptionsDuringRequest()) {
                builder.append(exception.toString()).append(": ");
                for (StackTraceElement element : exception.getStackTrace()) {
                    builder.append(element.toString()).append("\n");
                }
                builder.append("\n");
            }

            Configuration.getErrorTracker().w(TAG, builder.toString());
        }
    }

    protected String readStream(ApiRequestObject apiRequestObject, InputStream in) {
        Configuration.getErrorTracker().i(TAG, "readStream()");
        if (in == null) {
            return null;
        }
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        String stream = "";

        try {
            inputStreamReader = new InputStreamReader(in);
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stream += line;
            }
        } catch (IOException e) {
            apiRequestObject.addExceptionDuringRequest(e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    apiRequestObject.addExceptionDuringRequest(e);
                }
            }

            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    apiRequestObject.addExceptionDuringRequest(e);
                }
            }
        }

        return stream;
    }
}
