package com.arkhive.components.test_session_manager_fixes.layer_http;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiGetRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiPostRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public final class HttpLayer implements HttpInterface {
    private final int connectionTimeout;
    private final int readTimeout;

    public HttpLayer() {
        this(5000, 5000);
    }

    public HttpLayer(int connectionTimeout, int readTimeout) {
        super();
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
    }

    @Override
    public ApiGetRequestObject sendGetRequest(ApiGetRequestObject apiGetRequestObject) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            URL url = apiGetRequestObject.getConstructedUrl();
            //create url from request
            //open connection

            if (url == null) {
                apiGetRequestObject.addExceptionDuringRequest(new HttpLayerException("HttpPreProcessorGET produced a null URL"));
                return apiGetRequestObject;
            }

            connection = (HttpURLConnection) url.openConnection();

            //set connect and read timeout
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);

            //make sure this connection is a GET
            connection.setUseCaches(false);

            //get response code first so we know what type of stream to open
            int httpResponseCode = connection.getResponseCode();
            apiGetRequestObject.setHttpResponseCode(httpResponseCode);

            //now open the correct stream type based on error or not
            if (httpResponseCode / 100 != 2) {
                inputStream = connection.getErrorStream();
            } else {
                inputStream = connection.getInputStream();
            }
            String httpResponseString = readStream(apiGetRequestObject, inputStream);
            apiGetRequestObject.setHttpResponseString(httpResponseString);
        } catch (IOException e) {
            apiGetRequestObject.addExceptionDuringRequest(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    apiGetRequestObject.addExceptionDuringRequest(e);
                }
            }
        }

        return apiGetRequestObject;
    }

    @Override
    public ApiPostRequestObject sendPostRequest(ApiPostRequestObject apiPostRequestObject) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            URL url = apiPostRequestObject.getConstructedUrl();

            if (url == null) {
                apiPostRequestObject.addExceptionDuringRequest(new HttpLayerException("HttpPreProcessorGET produced a null URL"));
                return apiPostRequestObject;
            }

            connection = (HttpURLConnection) url.openConnection();

            //sets to POST
            connection.setDoOutput(true);

            byte[] payload = apiPostRequestObject.getPayload();
            if (payload != null) {
                connection.setFixedLengthStreamingMode(payload.length);
                connection.setRequestProperty("Content-Type", "application/octet-stream");

                HashMap<String, String> headers = apiPostRequestObject.getPostHeaders();
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
            apiPostRequestObject.setHttpResponseCode(httpResponseCode);

            String responseString;
            if (httpResponseCode / 100 != 2) {
                inputStream = connection.getErrorStream();
            } else {
                inputStream = connection.getInputStream();
            }

            responseString = readStream(apiPostRequestObject, inputStream);
            apiPostRequestObject.setHttpResponseString(responseString);

        } catch (ProtocolException e) {
            apiPostRequestObject.addExceptionDuringRequest(e);
        } catch(SocketException e) {
            apiPostRequestObject.addExceptionDuringRequest(e);
        } catch (IOException e) {
            apiPostRequestObject.addExceptionDuringRequest(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    apiPostRequestObject.addExceptionDuringRequest(e);
                }
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    apiPostRequestObject.addExceptionDuringRequest(e);
                }
            }
        }

        return apiPostRequestObject;
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
