package com.arkhive.components.core.module_http_processor.runnables;

import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpRequestCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;
import java.util.Map;

/**
 * Created by  on 6/16/2014.
 */
public class HttpPostRequestRunnable implements Runnable {
    private final HttpRequestCallback callback;
    private final ApiRequestObject apiRequestObject;
    private final HttpProcessor httpPreProcessor;
    private final HttpProcessor httpPostProcessor;
    private final Logger logger = LoggerFactory.getLogger(HttpPostRequestRunnable.class);

    public HttpPostRequestRunnable(HttpRequestCallback callback, HttpProcessor httpPreProcessor, HttpProcessor httpPostProcessor, ApiRequestObject apiRequestObject) {
        this.callback = callback;
        this.httpPreProcessor = httpPreProcessor;
        this.httpPostProcessor = httpPostProcessor;
        this.apiRequestObject = apiRequestObject;
    }

    @Override
    public void run() {
        logger.info(" sendRequest()");
        if (callback != null) {
            callback.httpRequestStarted(apiRequestObject);
        }

        if (httpPreProcessor != null) {
            httpPreProcessor.processApiRequestObject(apiRequestObject);
        }

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            URL url = apiRequestObject.getConstructedUrl();

            if (url == null) {
                apiRequestObject.addExceptionDuringRequest(new Exception("HttpPostRequestRunnable produced a null URL"));
                if (callback != null) {
                    callback.httpRequestFinished(apiRequestObject);
                }
                return;
            }

            connection = (HttpURLConnection) url.openConnection();

            //sets to POST
            connection.setDoOutput(true);

            byte[] payload = apiRequestObject.getPayload();
            if (payload != null) {
                connection.setFixedLengthStreamingMode(payload.length);
                connection.setRequestProperty("Content-Type", "application/octet-stream");

                Map<String, String> headers = apiRequestObject.getPostHeaders();
                if (headers != null) {
                    //set headers
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        connection.addRequestProperty(entry.getKey(), entry.getValue());
                    }
                }

                outputStream = connection.getOutputStream();
                outputStream.write(payload, 0, payload.length);
            }


            int httpResponseCode = connection.getResponseCode();
            apiRequestObject.setHttpResponseCode(httpResponseCode);

            String responseString;
            if (httpResponseCode / 100 != 2) {
                inputStream = connection.getErrorStream();
            } else {
                inputStream = connection.getInputStream();
            }

            responseString = readStream(apiRequestObject, inputStream);
            apiRequestObject.setHttpResponseString(responseString);

        } catch (ProtocolException e) {
            apiRequestObject.addExceptionDuringRequest(e);
        } catch (SocketException e) {
            apiRequestObject.addExceptionDuringRequest(e);
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

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    apiRequestObject.addExceptionDuringRequest(e);
                }
            }
        }

        if (httpPostProcessor != null) {
            httpPostProcessor.processApiRequestObject(apiRequestObject);
        }
        if (callback != null) {
            callback.httpRequestFinished(apiRequestObject);
        }
    }

    private String readStream(ApiRequestObject apiRequestObject, InputStream in) {
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
