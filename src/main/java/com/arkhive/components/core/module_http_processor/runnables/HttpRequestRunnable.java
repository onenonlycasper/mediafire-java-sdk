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
 * Created by Chris Najar on 7/2/2014.
 */
public abstract class HttpRequestRunnable implements Runnable {
    private static final String TAG = HttpRequestRunnable.class.getSimpleName();
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
    public void run() {
        if (callback != null) {
            callback.httpRequestStarted(apiRequestObject);
        }

        connectionTimeout = httpPeriProcessor.getConnectionTimeout();
        readTimeout = httpPeriProcessor.getReadTimeout();

        doRequest();

        if (httpPreProcessor != null) {
            httpPreProcessor.processApiRequestObject(apiRequestObject);
        }

        sendApiErrorIfExists();
        if (httpPostProcessor != null) {
            httpPostProcessor.processApiRequestObject(apiRequestObject);
        }
        if (callback != null) {
            callback.httpRequestFinished(apiRequestObject);
        }
    }

    protected void sendApiErrorIfExists() {
        if (Configuration.getErrorTracker() == null) {
            return;
        }

        if (apiRequestObject == null) {
            return;
        }

        if (apiRequestObject.getApiResponse() != null && apiRequestObject.getApiResponse().hasError()) {
            Configuration.getErrorTracker().trackApiError(HttpsGetRequestRunnable.class.getSimpleName(), apiRequestObject);
        }

        StringBuilder builder = new StringBuilder();
        if (apiRequestObject.getExceptionsDuringRequest() != null) {
            for (Exception exception : apiRequestObject.getExceptionsDuringRequest()) {
                builder.append(exception.toString()).append(": ");
                for (StackTraceElement element : exception.getStackTrace()) {
                    builder.append(element.toString()).append("\n");
                }
                builder.append("\n");
            }

            Configuration.getErrorTracker().trackError(TAG, 1, 5, "exceptions during http", builder.toString());
        }
    }

    protected String readStream(ApiRequestObject apiRequestObject, InputStream in) {
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
