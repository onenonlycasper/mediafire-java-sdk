package com.arkhive.components.core.module_http_processor.runnables;


import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpRequestCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * Created by on 6/16/2014.
 */
public class HttpsGetRequestRunnable implements Runnable {
    private final HttpRequestCallback callback;
    private final ApiRequestObject apiRequestObject;
    private final HttpPeriProcessor httpPeriProcessor;
    private final HttpProcessor httpPreProcessor;
    private final HttpProcessor httpPostProcessor;
    private final Logger logger = LoggerFactory.getLogger(HttpGetRequestRunnable.class);

    public HttpsGetRequestRunnable(HttpRequestCallback callback, HttpProcessor httpPreProcessor, HttpProcessor httpPostProcessor, ApiRequestObject apiRequestObject, HttpPeriProcessor httpPeriProcessor) {
        this.callback = callback;
        this.httpPreProcessor = httpPreProcessor;
        this.httpPostProcessor = httpPostProcessor;
        this.apiRequestObject = apiRequestObject;
        this.httpPeriProcessor = httpPeriProcessor;
    }

    @Override
    public void run() {
        logger.info(" sendRequest()");
        if (callback != null) {
            callback.httpRequestStarted(apiRequestObject);
        }

        int connectionTimeout = httpPeriProcessor.getConnectionTimeout();
        int readTimeout = httpPeriProcessor.getReadTimeout();

        if (httpPreProcessor != null) {
            httpPreProcessor.processApiRequestObject(apiRequestObject);
        }

        HttpsURLConnection connection = null;
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

            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                    }
            };

            // Install trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            connection = (HttpsURLConnection) url.openConnection();

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
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
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

