package com.mediafire.sdk.http;

import com.arkhive.components.core.module_token_farm.TokenFarm;
import com.arkhive.components.core.module_token_farm.tokens.Token;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public class HttpClient {
    private final TokenFarm tokenFarm;

    public HttpClient(TokenFarm tokenFarm) {
        this.tokenFarm = tokenFarm;
    }

    private MFResponse sendRequest(MFRequest request) {
        HttpsURLConnection connection = null;
        MFResponse response = null;

        try {
            connection = createConnection(request);

            postData(request, connection);

            response = getResponseFromStream(connection);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response;
    }

    private MFResponse getResponseFromStream(HttpsURLConnection connection) throws IOException {
        int status = connection.getResponseCode();
        MFResponse response = null;

        if (status / 100 != 2) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(connection.getErrorStream());
            byte[] body = readStream(bufferedInputStream);
            response = new MFResponse(status, new Hashtable<String, List<String>>(), body);
        } else {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(connection.getInputStream());
            byte[] body = readStream(bufferedInputStream);
            response = new MFResponse(status, connection.getHeaderFields(), body);
        }

        return response;
    }

    private void postData(MFRequest request, HttpsURLConnection connection) throws IOException {
        byte[] payload = null;
        if (request.getMfApi().isQueryPostable()) {
            payload = makeQueryString(request.getRequestParameters()).getBytes();
        } else if (request.getPayload() != null) {
            payload = request.getPayload();
        }

        if (payload != null) {
            connection.setFixedLengthStreamingMode(payload.length);
            connection.getOutputStream().write(payload);
        }
    }

    private byte[] readStream(InputStream inputStream) throws IOException {
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

        return outputStream.toByteArray();
    }

    private HttpsURLConnection createConnection(MFRequest request) throws IOException {
        URL url = makeFullUrl(request);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        setConnectionParameters(connection, request);
        return connection;
    }

    private void setConnectionParameters(HttpsURLConnection connection, MFRequest request) {
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
        connection.setConnectTimeout(45000);
        connection.setReadTimeout(45000);

        // set request headers (if any)
        if (request.getHeaders() != null) {
            for (String key : request.getHeaders().keySet()) {
                connection.addRequestProperty(key, request.getHeaders().get(key));
            }
        }
    }

    private URL makeFullUrl(MFRequest request) throws MalformedURLException, UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(createBaseUrl(request));

        if (!request.getMfApi().isQueryPostable()) {
            String queryString = makeQueryString(request.getRequestParameters());
            queryString = createAttachableQueryString(queryString);
            stringBuilder.append(queryString);
        }

        return new URL(stringBuilder.toString());
    }

    private String makeQueryString(Map<String, String> requestParameters) throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : requestParameters.keySet()) {
            stringBuilder.append("&");
            stringBuilder.append(key);
            stringBuilder.append("=");
            stringBuilder.append(urlEncodedQueryValue(requestParameters.get(key)));
        }

        return stringBuilder.toString().substring(1);
    }

    private String urlEncodedQueryValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "UTF-8");
    }

    private String createAttachableQueryString(String queryString) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("?");
        stringBuilder.append(queryString);
        return stringBuilder.toString();
    }

    private String createBaseUrl(MFRequest request) {
        String scheme = request.getMfHost().getTransferScheme().getScheme();
        String host = request.getMfHost().getHost();
        String uri = request.getMfApi().getUri();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(scheme);
        stringBuilder.append(host);
        stringBuilder.append(uri);
        return stringBuilder.toString();
    }

    private String hashViaMD5(String target) {
        String signature;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(target.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuilder sb = new StringBuilder();
            for (byte aByteData : byteData) {
                sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
            }
            signature = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            signature = target;
        }

        return signature;
    }

    private Token requestToken(MFRequest request) {
        return null;
    }
}
