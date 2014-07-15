package com.arkhive.components.core.module_http_processor.runnables;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpRequestCallback;

import javax.net.ssl.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Created by  on 7/14/2014.
 */
public class HttpsPostRequestRunnable extends HttpRequestRunnable {
    private static final String TAG = HttpsPostRequestRunnable.class.getSimpleName();
    public HttpsPostRequestRunnable(HttpRequestCallback callback, HttpProcessor httpPreProcessor, HttpProcessor httpPostProcessor, ApiRequestObject apiRequestObject, HttpPeriProcessor httpPeriProcessor) {
        super(callback, httpPreProcessor, httpPostProcessor, apiRequestObject, httpPeriProcessor);
    }

    @Override
    protected void doRequest() {
        Configuration.getErrorTracker().i(TAG, "doRequest");
        HttpsURLConnection connection = null;
        InputStream inputStream = null;
        DataOutputStream outputStream = null;

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

            //sets to POST
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(false);

            Map<String, String> parameters = apiRequestObject.getOptionalParameters();
            parameters.putAll(apiRequestObject.getRequiredParameters());

            String requestBody = constructParametersForUrl(parameters);

            if (requestBody != null) {
                connection.setFixedLengthStreamingMode(requestBody.getBytes().length);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeChars(requestBody);
                outputStream.close();
            }

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
    private String constructParametersForUrl(Map<String, String> parameters) {
        Configuration.getErrorTracker().i(TAG, "constructParametersForUrl(HashMap<String, String>)");
        String constructedString = "";
        if (parameters != null && !parameters.isEmpty()) {
            for (String key : parameters.keySet()) {
                constructedString += "&";
                constructedString += key;
                constructedString += "=";
                constructedString += parameters.get(key);
            }
        }
        constructedString = constructedString.substring(1);
        return constructedString;
    }
}
