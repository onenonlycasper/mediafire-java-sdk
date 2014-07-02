package com.arkhive.components.core.module_http_processor.runnables;

import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpRequestCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by  on 6/16/2014.
 */
public final class HttpGetRequestRunnable extends HttpRequestRunnable {
    private final Logger logger = LoggerFactory.getLogger(HttpGetRequestRunnable.class);

    public HttpGetRequestRunnable(HttpRequestCallback callback, HttpProcessor httpPreProcessor, HttpProcessor httpPostProcessor, ApiRequestObject apiRequestObject, HttpPeriProcessor httpPeriProcessor) {
        super(callback, httpPreProcessor, httpPostProcessor, apiRequestObject, httpPeriProcessor);
    }

    @Override
    protected void doRequest() {
        logger.info("doRequest");
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            URL url = apiRequestObject.getConstructedUrl();
            //create url from request
            //open connection

            if (url == null) {
                apiRequestObject.addExceptionDuringRequest(new Exception("HttpGetRequestRunnable produced a null URL"));
                if (callback != null) {
                    callback.httpRequestFinished(apiRequestObject);
                }
                return;
            }

            connection = (HttpURLConnection) url.openConnection();

            //set connect and read timeout
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);

            //make sure this connection is a GET
            connection.setUseCaches(false);

            //get response code first so we know what type of stream to open
            int httpResponseCode = connection.getResponseCode();
            apiRequestObject.setHttpResponseCode(httpResponseCode);

            //now open the correct stream type based on error or not
            if (httpResponseCode / 100 != 2) {
                inputStream = connection.getErrorStream();
            } else {
                inputStream = connection.getInputStream();
            }
            String httpResponseString = readStream(apiRequestObject, inputStream);
            apiRequestObject.setHttpResponseString(httpResponseString);
        } catch (IOException e) {
            apiRequestObject.addExceptionDuringRequest(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    apiRequestObject.addExceptionDuringRequest(e);
                }
            }
        }
    }
}
