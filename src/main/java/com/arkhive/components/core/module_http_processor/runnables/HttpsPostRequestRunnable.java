package com.arkhive.components.core.module_http_processor.runnables;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpRequestCallback;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Created by  on 7/14/2014.
 */
public class HttpsPostRequestRunnable extends HttpRequestRunnable {
    private static final String TAG = HttpsPostRequestRunnable.class.getCanonicalName();
    public HttpsPostRequestRunnable(HttpRequestCallback callback, HttpProcessor httpPreProcessor, HttpProcessor httpPostProcessor, ApiRequestObject apiRequestObject, HttpPeriProcessor httpPeriProcessor) {
        super(callback, httpPreProcessor, httpPostProcessor, apiRequestObject, httpPeriProcessor);
    }

    @Override
    protected void doRequest() {
        Configuration.getErrorTracker().i(TAG, "doRequest");
        HttpsURLConnection connection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            URL url = apiRequestObject.getConstructedUrl();
            Configuration.getErrorTracker().v(TAG, "url used: " + url.toString());
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
            //sets to POST
            connection.setDoOutput(true);

            Map<String, String> parameters = apiRequestObject.getOptionalParameters();
            parameters.putAll(apiRequestObject.getRequiredParameters());

            String requestBody = getFullKeyFromParameters(parameters);
//            requestBody = URLEncoder.encode(requestBody, "UTF-8");
            Configuration.getErrorTracker().v(TAG, "request body: " + requestBody);
            byte[] requestBodyBytes = requestBody.getBytes();

            if (requestBody != null) {
                Configuration.getErrorTracker().v(TAG, "request body length: " + requestBody.length());
                connection.setFixedLengthStreamingMode(requestBodyBytes.length);
                connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                outputStream = connection.getOutputStream();
                outputStream.write(requestBodyBytes);
            } else {
                Configuration.getErrorTracker().v(TAG, "request body was null");
            }

            Configuration.getErrorTracker().v(TAG, "request body length: " + requestBody.length());
            Configuration.getErrorTracker().v(TAG, "request body bytes: " + requestBodyBytes.length);

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

            Configuration.getErrorTracker().w(TAG, "http response string: " + httpResponseString);

            apiRequestObject.setHttpResponseString(httpResponseString);
        } catch (IOException e) {
            apiRequestObject.addExceptionDuringRequest(e);
        } catch (NoSuchAlgorithmException e) {
            apiRequestObject.addExceptionDuringRequest(e);
        } catch (KeyManagementException e) {
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
    }

    /**
     * constructs parameters in the &key=value format for a url.
     *
     * @param parameters - the key/value pairs to be formatted
     * @return a formatted string with the key/value paris for a url.
     */
    private String getFullKeyFromParameters(Map<String, String> parameters) {
        Configuration.getErrorTracker().i(TAG, "getFullKeyFromParameters(HashMap<String, String>)");
        if (!parameters.containsKey("full")) {
            return "";
        }
        String fullValueUrlEncoded;
        try {
            fullValueUrlEncoded = URLEncoder.encode(parameters.get("full"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            fullValueUrlEncoded = parameters.get("full");
            e.printStackTrace();
        }
        String constructedString = "full=" + fullValueUrlEncoded;
        return constructedString;
    }
}
