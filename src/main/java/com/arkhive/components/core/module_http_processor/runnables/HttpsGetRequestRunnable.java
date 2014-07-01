package com.arkhive.components.core.module_http_processor.runnables;

import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpRequestCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by Chris Najar on 6/30/2014.
 */
public class HttpsGetRequestRunnable implements Runnable {
    private final HttpRequestCallback callback;
    private final ApiRequestObject apiRequestObject;
    private final HttpPeriProcessor httpPeriProcessor;
    private final HttpProcessor httpPreProcessor;
    private final HttpProcessor httpPostProcessor;
    private final Logger logger = LoggerFactory.getLogger(HttpsGetRequestRunnable.class);

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
            } else {
                logger.info("url ok");
            }

            // trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException { }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException { }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                    }
            };

            // Create SSL Context using TrustManager
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            // set the ssl socket factory
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            // create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            };
            connection.setDefaultHostnameVerifier(allHostsValid);

            // open connection
            connection = (HttpsURLConnection) url.openConnection();
            //set connect and read timeout
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);
            //make sure this connection is a GET
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);

            connection.connect();


            //get response code first so we know what type of stream to open
            int httpResponseCode = connection.getResponseCode();
            apiRequestObject.setHttpResponseCode(httpResponseCode);

            //now open the correct stream type based on error or not
            if (httpResponseCode / 100 != 2) {
                logger.info("opening error stream");
                inputStream = connection.getErrorStream();
            } else {
                logger.info("opening input stream");
                inputStream = connection.getInputStream();
            }
            String httpResponseString = readStream(apiRequestObject, inputStream);
            apiRequestObject.setHttpResponseString(httpResponseString);
        } catch (NoSuchAlgorithmException e) {
            apiRequestObject.addExceptionDuringRequest(e);
            logger.info("exception: " + e);
        } catch (KeyManagementException e) {
            apiRequestObject.addExceptionDuringRequest(e);
            logger.info("exception: " + e);
        } catch (FileNotFoundException e) {
            apiRequestObject.addExceptionDuringRequest(e);
            logger.info("exception: " + e);
        } catch (IOException e) {
            apiRequestObject.addExceptionDuringRequest(e);
            logger.info("exception: " + e);
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
