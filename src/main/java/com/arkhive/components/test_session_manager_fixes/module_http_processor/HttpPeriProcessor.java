package com.arkhive.components.test_session_manager_fixes.module_http_processor;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public final class HttpPeriProcessor implements HttpInterface {
    private final int connectionTimeout;
    private final int readTimeout;
    private final HttpPreProcessor httpPreProcessor;
    private final HttpPostProcessor httpPostProcessor;

    private Logger logger = LoggerFactory.getLogger(HttpPeriProcessor.class);

    public HttpPeriProcessor(int connectionTimeout, int readTimeout) {
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
        httpPreProcessor = new HttpPreProcessor();
        httpPostProcessor = new HttpPostProcessor();
    }

    @Override
    public ApiRequestObject sendGetRequest(ApiRequestObject apiRequestObject) {
        logger.debug("sendGetRequest()");

        httpPreProcessor.processApiRequestObject(apiRequestObject);

        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            URL url = apiRequestObject.getConstructedUrl();
            //create url from request
            //open connection

            if (url == null) {
                apiRequestObject.addExceptionDuringRequest(new HttpException("HttpPreProcessorGET produced a null URL"));
                return apiRequestObject;
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

        httpPostProcessor.processApiRequestObject(apiRequestObject);
        return apiRequestObject;
    }

    @Override
    public ApiRequestObject sendPostRequest(ApiRequestObject apiRequestObject) {
        logger.debug("sendPostRequest()");

        httpPreProcessor.processApiRequestObject(apiRequestObject);

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            URL url = apiRequestObject.getConstructedUrl();

            if (url == null) {
                apiRequestObject.addExceptionDuringRequest(new HttpException("HttpPreProcessorGET produced a null URL"));
                return apiRequestObject;
            }

            connection = (HttpURLConnection) url.openConnection();

            //sets to POST
            connection.setDoOutput(true);

            byte[] payload = apiRequestObject.getPayload();
            if (payload != null) {
                connection.setFixedLengthStreamingMode(payload.length);
                connection.setRequestProperty("Content-Type", "application/octet-stream");

                HashMap<String, String> headers = apiRequestObject.getPostHeaders();
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
        } catch(SocketException e) {
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

        httpPostProcessor.processApiRequestObject(apiRequestObject);

        return apiRequestObject;
    }

    private String readStream(ApiRequestObject apiRequestObject, InputStream in){
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
