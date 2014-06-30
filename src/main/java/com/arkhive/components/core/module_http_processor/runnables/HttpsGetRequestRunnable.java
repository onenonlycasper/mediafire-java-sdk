package com.arkhive.components.core.module_http_processor.runnables;

import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpRequestCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
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

            logger.info("creating certificate factory");
            // Load Certificate of Authority from file
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            logger.info("finished creating certificate factory");
            // read certificate
            logger.info("creating input stream to certificate");
            InputStream caInput = new BufferedInputStream(new FileInputStream("mf-c.crt"));
            logger.info("finished creating input stream to certificate");
            logger.info("creating certificate");
            Certificate ca = cf.generateCertificate(caInput);
            logger.info("finished creating certificate");
            logger.info("ca=" + ((X509Certificate) ca).getSubjectDN());
            logger.info("closing input stream to certificate");
            caInput.close();

            // create keystore containing trusted CAs
            logger.info("creating keystore");
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            logger.info("finished creating keystore");
            // create trust manager that trusts certificates of authority from keystore
            logger.info("creating trust manager");
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
            logger.info("finished creating trust manager");
            // Create SSL Context using TrustManager
            logger.info("creating ssl context");
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
            logger.info("finished creating ssl context");
            // open connection
            logger.info("opening connection to url");
            connection = (HttpsURLConnection) url.openConnection();
            // set the ssl socket factory
            logger.info("setting sslsocketfactory to connection");
            connection.setSSLSocketFactory(context.getSocketFactory());
            //set connect and read timeout
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);
            //make sure this connection is a GET
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);

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
        } catch (CertificateException e) {
            apiRequestObject.addExceptionDuringRequest(e);
            logger.info("exception: " + e);
        } catch (KeyStoreException e) {
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
