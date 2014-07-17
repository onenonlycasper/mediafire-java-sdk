package com.mediafire.sdk.http;

import com.mediafire.sdk.config.MFConfiguration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public final class MFHttpRequestSender extends MFHttp implements MFHttpClient {
    public MFHttpRequestSender(MFConfiguration mfConfiguration) {
        super(mfConfiguration);
    }

    @Override
    public MFHttpResponse sendRequest(MFHttpRequest request) {
        System.out.println("sending request");
        URLConnection connection = null;
        MFHttpResponse response = null;

        try {
            // create the connection
            connection = createHttpConnection(request);
            // send any data possible via POST
            postData(request, connection);
            // receive response from request
            response = getResponseFromStream(connection);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                ((HttpURLConnection) connection).disconnect();
            }
        }

        return response;
    }

    protected MFHttpResponse getResponseFromStream(URLConnection connection) throws IOException {
        System.out.println("getting MFResponse from stream");
        int status = ((HttpURLConnection) connection).getResponseCode();
        MFHttpResponse response = null;

        if (status / 100 != 2) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(((HttpURLConnection) connection).getErrorStream());
            byte[] body = readStream(bufferedInputStream);
            response = new MFHttpResponse(status, new Hashtable<String, List<String>>(), body);
        } else {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(connection.getInputStream());
            byte[] body = readStream(bufferedInputStream);
            response = new MFHttpResponse(status, connection.getHeaderFields(), body);
        }

        return response;
    }

    protected void postData(MFHttpRequest request, URLConnection connection) throws IOException {
        System.out.println("trying to post data if possible");
        byte[] payload = null;
        if (request.getMfApi().isQueryPostable()) {
            payload = makeQueryString(request.getRequestParameters()).getBytes();
        } else if (request.getPayload() != null) {
            payload = request.getPayload();
        }

        if (payload != null) {
            ((HttpURLConnection) connection).setFixedLengthStreamingMode(payload.length);
            connection.getOutputStream().write(payload);
        }
    }

    protected byte[] readStream(InputStream inputStream) throws IOException {
        System.out.println("reading input stream");
        if (inputStream == null) {
            return null;
        }
        byte[] buffer = new byte[1024];
        int count = 0;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        while ((count = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, count);
        }

        inputStream.close();
        byte[] bytes = outputStream.toByteArray();
        outputStream.close();
        return bytes;
    }

    protected HttpURLConnection createHttpConnection(MFHttpRequest request) throws IOException {
        System.out.println("creating http connection");
        URL url = makeFullUrl(request);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        setConnectionParameters(connection, request);
        return connection;
    }

    protected void setConnectionParameters(URLConnection connection, MFHttpRequest request) {
        System.out.println("setting connection parameters");
        switch (request.getMfHost().getTransferScheme()) {
            case HTTP:
                break;
            case HTTPS:
                // set ssl context and trust manager
                break;
        }

        // if query can be made via POST then set to post
        if (request.getMfApi().isQueryPostable() || request.getPayload() != null) {
            connection.setDoOutput(true);
        }

        // set timeouts
        connection.setConnectTimeout(mfConfiguration.getHttpConnectionTimeout());
        connection.setReadTimeout(mfConfiguration.getHttpReadTimeout());

        // set request headers (if any)
        if (request.getHeaders() != null) {
            for (String key : request.getHeaders().keySet()) {
                connection.addRequestProperty(key, request.getHeaders().get(key));
            }
        }
    }

    protected URL makeFullUrl(MFHttpRequest request) throws MalformedURLException, UnsupportedEncodingException {
        System.out.println("creating url");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makeBaseUrl(request));

        if (!request.getMfApi().isQueryPostable()) {
            String queryString = makeQueryString(request.getRequestParameters());
            queryString = makeUrlAttachableQueryString(queryString);
            stringBuilder.append(queryString);
        }

        System.out.println("attempted to create url - " + stringBuilder.toString());
        return new URL(stringBuilder.toString());
    }

    protected String makeQueryString(Map<String, String> requestParameters) throws UnsupportedEncodingException {
        return makeQueryString(requestParameters, true);
    }
}
