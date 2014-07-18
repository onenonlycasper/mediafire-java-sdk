package com.mediafire.sdk.http;

import com.mediafire.sdk.config.MFConfiguration;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public final class MFHttpClient extends MFHttp {
    private static final String TAG = MFHttpClient.class.getCanonicalName();
    private final int readTimeout;
    private final int connectionTimeout;
    public MFHttpClient(MFConfiguration mfConfiguration) {
        super(mfConfiguration);
        this.readTimeout = mfConfiguration.getHttpReadTimeout();
        this.connectionTimeout = mfConfiguration.getHttpConnectionTimeout();
    }

    public MFResponse sendRequest(MFRequest mfRequest) {
        MFConfiguration.getStaticMFLogger().v(TAG, "sendRequest()");
        URLConnection connection = null;
        MFResponse mfResponse = null;

        try {
            MFConfiguration.getStaticMFLogger().v(TAG, "creating connection");
            // create the connection
            connection = createHttpConnection(mfRequest);
            MFConfiguration.getStaticMFLogger().v(TAG, "posting data, if possible");
            // send any data possible via POST
            postData(mfRequest, connection);
            MFConfiguration.getStaticMFLogger().v(TAG, "receiving response");
            // receive response from request
            mfResponse = getResponseFromStream(connection, mfRequest);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                ((HttpURLConnection) connection).disconnect();
            }
        }

        return mfResponse;
    }

    private MFResponse getResponseFromStream(URLConnection connection, MFRequest mfRequest) throws IOException {
        MFConfiguration.getStaticMFLogger().v(TAG, "getResponseFromStream()");
        int status = ((HttpURLConnection) connection).getResponseCode();
        MFResponse mfResponse;

        if (status / 100 != 2) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(((HttpURLConnection) connection).getErrorStream());
            byte[] body = readStream(bufferedInputStream);
            mfResponse = new MFResponse(status, new Hashtable<String, List<String>>(), body, mfRequest);
        } else {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(connection.getInputStream());
            byte[] body = readStream(bufferedInputStream);
            mfResponse = new MFResponse(status, connection.getHeaderFields(), body, mfRequest);
        }

        return mfResponse;
    }

    private void postData(MFRequest mfRequest, URLConnection connection) throws IOException {
        MFConfiguration.getStaticMFLogger().v(TAG, "postData()");
        byte[] payload = null;
        if (mfRequest.getMfApi().isQueryPostable()) {
            payload = makeQueryString(mfRequest.getRequestParameters()).getBytes();
        } else if (mfRequest.getPayload() != null) {
            payload = mfRequest.getPayload();
        }

        if (payload != null) {
            ((HttpURLConnection) connection).setFixedLengthStreamingMode(payload.length);
            connection.getOutputStream().write(payload);
        }
    }

    private byte[] readStream(InputStream inputStream) throws IOException {
        MFConfiguration.getStaticMFLogger().v(TAG, "readStream()");
        if (inputStream == null) {
            return null;
        }
        byte[] buffer = new byte[1024];
        int count;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        while ((count = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, count);
        }

        inputStream.close();
        byte[] bytes = outputStream.toByteArray();
        outputStream.close();
        return bytes;
    }

    private HttpURLConnection createHttpConnection(MFRequest mfRequest) throws IOException {
        MFConfiguration.getStaticMFLogger().v(TAG, "createHttpConnection()");
        URL url = makeFullUrl(mfRequest);
        MFConfiguration.getStaticMFLogger().v(TAG, "opening connection to: " + url.toString());
        URLConnection connection;
        switch (mfRequest.getMfHost().getTransferScheme()) {
            case HTTP:
                MFConfiguration.getStaticMFLogger().v(TAG, "transfer scheme for this request: " + mfRequest.getMfHost().getTransferScheme().toString());
                connection = url.openConnection();
                setConnectionParameters(connection, mfRequest);
                return (HttpURLConnection) connection;
            case HTTPS:
                MFConfiguration.getStaticMFLogger().v(TAG, "transfer scheme for this request: " + mfRequest.getMfHost().getTransferScheme().toString());
                connection = url.openConnection();
                setConnectionParameters(connection, mfRequest);
                // TODO set ssl context and trust manager
                return (HttpsURLConnection) connection;
            default:
                throw new IllegalStateException("MFHost.TransferScheme must be HTTP or HTTPS");
        }
    }

    private void setConnectionParameters(URLConnection connection, MFRequest mfRequest) {
        MFConfiguration.getStaticMFLogger().v(TAG, "setConnectionParameters()");

        // if query can be made via POST then set to post
        if (mfRequest.getMfApi().isQueryPostable() || mfRequest.getPayload() != null) {
            MFConfiguration.getStaticMFLogger().v(TAG, "query can be sent via POST");
            connection.setDoOutput(true);
        }

        MFConfiguration.getStaticMFLogger().v(TAG, "setting connection timeout");
        // set timeouts
        connection.setConnectTimeout(connectionTimeout);
        connection.setReadTimeout(readTimeout);

        MFConfiguration.getStaticMFLogger().v(TAG, "setting request headers");
        // set request headers (if any)
        if (mfRequest.getHeaders() != null) {
            for (String key : mfRequest.getHeaders().keySet()) {
                connection.addRequestProperty(key, mfRequest.getHeaders().get(key));
            }
        }
    }

    private URL makeFullUrl(MFRequest mfRequest) throws MalformedURLException, UnsupportedEncodingException {
        MFConfiguration.getStaticMFLogger().v(TAG, "makeFullUrl");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makeBaseUrl(mfRequest));

        if (!mfRequest.getMfApi().isQueryPostable()) {
            MFConfiguration.getStaticMFLogger().v(TAG, "query is not postable. appending query to url");
            String queryString = makeQueryString(mfRequest.getRequestParameters());
            queryString = makeUrlAttachableQueryString(queryString);
            stringBuilder.append(queryString);
        }

        MFConfiguration.getStaticMFLogger().v(TAG, "attempting to create url - " + stringBuilder.toString());
        return new URL(stringBuilder.toString());
    }

    private String makeQueryString(Map<String, String> requestParameters) throws UnsupportedEncodingException {
        return makeQueryString(requestParameters, true);
    }
}
