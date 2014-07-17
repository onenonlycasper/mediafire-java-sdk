package com.mediafire.sdk.http;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.module_credentials.ApplicationCredentials;
import com.mediafire.sdk.tokenfarm.ImageActionToken;
import com.mediafire.sdk.tokenfarm.SessionToken;
import com.mediafire.sdk.tokenfarm.TokenFarm;
import com.mediafire.sdk.tokenfarm.UploadActionToken;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public class HttpClient {
    private static final String SHA1 = "SHA-1";
    private static final String MD5 = "MD5";

    private final TokenFarm tokenFarm;
    private Configuration configuration;
    private ApplicationCredentials credentials;

    public HttpClient(TokenFarm tokenFarm, Configuration configuration, ApplicationCredentials credentials) {
        this.tokenFarm = tokenFarm;
        this.configuration = configuration;
        this.credentials = credentials;
    }

    public MFResponse sendRequest(MFRequest request) {
        System.out.println("sending request");
        URLConnection connection = null;
        MFResponse response = null;

        try {
            // borrow token, if necessary
            borrowToken(request);
            // add token, if necessary, to request parameters
            addTokenToRequestParameters(request);
            // add signature, if necessary, to request parameters
            addSignatureToRequestParameters(request);
            connection = createHttpConnection(request);
            // send any data possible via POST
            postData(request, connection);
            // receive response from request
            response = getResponseFromStream(connection);
            // return token, if necessary
            returnToken(request);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                ((HttpURLConnection) connection).disconnect();
            }
        }

        return response;
    }

    private void addSignatureToRequestParameters(MFRequest request) throws UnsupportedEncodingException {
        switch (request.getMfApi().getTokenType()) {
            case SESSION_TOKEN_V2:
                String recycledSessionTokenSignature = calculateSignature(request);
                request.getRequestParameters().put("signature", recycledSessionTokenSignature);
                break;
            case UNIQUE:
                String newSessionTokenSignature = calculateSignature(configuration, credentials);
                request.getRequestParameters().put("signature", newSessionTokenSignature);
                break;
            default:
                // for types NONE, UPLOAD_ACTION_TOKEN, IMAGE_ACTION_TOKEN
                // there is no need to attach a signature to the request parameters
                break;
        }
    }

    private String calculateSignature(Configuration configuration, ApplicationCredentials credentials) {
        // email + password + app id + api key
        // fb access token + app id + api key
        // tw oauth token + tw oauth token secret + app id + api key

        String userInfoPortionOfHashTarget = null;
        switch (credentials.getUserCredentialsType()) {
            case FACEBOOK:
                String fb_token_key = ApplicationCredentials.FACEBOOK_PARAMETER_FB_ACCESS_TOKEN;
                userInfoPortionOfHashTarget = credentials.getCredentials().get(fb_token_key);
                break;
            case TWITTER:
                String tw_oauth_token = ApplicationCredentials.TWITTER_PARAMETER_TW_OAUTH_TOKEN;
                String tw_oauth_token_secret = ApplicationCredentials.TWITTER_PARAMETER_TW_OAUTH_TOKEN_SECRET;
                userInfoPortionOfHashTarget = credentials.getCredentials().get(tw_oauth_token) + credentials.getCredentials().get(tw_oauth_token_secret);
                break;
            case MEDIAFIRE:
                String mf_email = ApplicationCredentials.MEDIAFIRE_PARAMETER_EMAIL;
                String mf_pass = ApplicationCredentials.MEDIAFIRE_PARAMETER_PASSWORD;
                userInfoPortionOfHashTarget = credentials.getCredentials().get(mf_email) + credentials.getCredentials().get(mf_pass);
                break;
            case UNSET:
                throw new IllegalArgumentException("credentials must be set to call /api/user/get_session_token");
        }

        String appId = configuration.getAppId();
        String apiKey = configuration.getApiKey();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(userInfoPortionOfHashTarget);
        stringBuilder.append(appId);
        stringBuilder.append(apiKey);

        String hashTarget = stringBuilder.toString();

        return hashString(hashTarget, SHA1);
    }

    private String calculateSignature(MFRequest request) throws UnsupportedEncodingException {
        // session token secret key + time + uri (concatenated)
        SessionToken sessionToken = (SessionToken) request.getToken();
        int secretKey = Integer.valueOf(sessionToken.getSecretKey()) % 256;
        String time = sessionToken.getTime();

        String baseUrl = makeBaseUrl(request);
        String nonUrlEncodedQueryString = makeQueryString(request.getRequestParameters(), false);
        String urlAttachableQueryString = makeUrlAttachableQueryString(nonUrlEncodedQueryString);

        StringBuilder uriStringBuilder = new StringBuilder();
        uriStringBuilder.append(baseUrl);
        uriStringBuilder.append(urlAttachableQueryString);

        String uri = uriStringBuilder.toString();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(secretKey);
        stringBuilder.append(time);
        stringBuilder.append(uri);
        String nonUrlEncodedString = stringBuilder.toString();
        return hashString(nonUrlEncodedString, MD5);
    }

    private void addTokenToRequestParameters(MFRequest request) {
        switch (request.getMfApi().getTokenType()) {
            case SESSION_TOKEN_V2:
            case UPLOAD_ACTION_TOKEN:
            case IMAGE_ACTION_TOKEN:
                String tokenString = request.getToken().getTokenString();
                request.getRequestParameters().put("session_token", tokenString);
                break;
            default:
                // for types NONE, UNIQUE
                // there is no need to attach a session token to the request parameters
                break;
        }
    }

    private void returnToken(MFRequest request) {
        switch (request.getMfApi().getTokenType()) {
            case SESSION_TOKEN_V2:
                SessionToken oldSessionToken = (SessionToken) request.getToken();
                tokenFarm.returnSessionToken(oldSessionToken);
                break;
            case UNIQUE:
            // UNIQUE represents requesting a new session token via /api/user/get_session_token
                SessionToken newSessionToken = (SessionToken) request.getToken();
                tokenFarm.receiveNewSessionToken(newSessionToken);
                break;
            default:
                // for types UPLOAD_ACTION_TOKEN, IMAGE_ACTION_TOKEN, NONE
                // there is no need to return a token
                break;
        }
    }

    private void borrowToken(MFRequest request) {
        switch (request.getMfApi().getTokenType()) {
            case SESSION_TOKEN_V2:
                SessionToken sessionToken = tokenFarm.borrowSessionToken();
                request.setToken(sessionToken);
                break;
            case UPLOAD_ACTION_TOKEN:
                UploadActionToken uploadActionToken = tokenFarm.borrowUploadActionToken();
                request.setToken(uploadActionToken);
                break;
            case IMAGE_ACTION_TOKEN:
                ImageActionToken imageActionToken = tokenFarm.borrowImageActionToken();
                request.setToken(imageActionToken);
                break;
            default:
                // for type NONE, UNIQUE there is no need to request a token.
                break;
        }
    }

    private MFResponse getResponseFromStream(URLConnection connection) throws IOException {
        System.out.println("getting MFResponse from stream");
        int status = ((HttpURLConnection) connection).getResponseCode();
        MFResponse response = null;

        if (status / 100 != 2) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(((HttpURLConnection) connection).getErrorStream());
            byte[] body = readStream(bufferedInputStream);
            response = new MFResponse(status, new Hashtable<String, List<String>>(), body);
        } else {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(connection.getInputStream());
            byte[] body = readStream(bufferedInputStream);
            response = new MFResponse(status, connection.getHeaderFields(), body);
        }

        return response;
    }

    private void postData(MFRequest request, URLConnection connection) throws IOException {
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

    private byte[] readStream(InputStream inputStream) throws IOException {
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

    private HttpURLConnection createHttpConnection(MFRequest request) throws IOException {
        System.out.println("creating http connection");
        URL url = makeFullUrl(request);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        setConnectionParameters(connection, request);
        return connection;
    }

    private void setConnectionParameters(URLConnection connection, MFRequest request) {
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

    private String makeQueryString(Map<String, String> requestParameters) throws UnsupportedEncodingException {
        return makeQueryString(requestParameters, true);
    }

    private String makeQueryString(Map<String, String> requestParameters, boolean urlEncode) throws UnsupportedEncodingException {
        System.out.println("making query string. url encoding: " + urlEncode);
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : requestParameters.keySet()) {
            stringBuilder.append("&");
            stringBuilder.append(key);
            stringBuilder.append("=");
            stringBuilder.append(urlEncodedQueryValue(requestParameters.get(key)));
        }
        System.out.println("made query string - " + stringBuilder.toString());
        return stringBuilder.toString().substring(1);
    }

    private String urlEncodedQueryValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "UTF-8");
    }

    private String makeUrlAttachableQueryString(String queryString) {
        System.out.println("making a url attachable query string");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("?");
        stringBuilder.append(queryString);
        System.out.println("made query string - " + stringBuilder.toString());
        return stringBuilder.toString();
    }

    private String makeBaseUrl(MFRequest request) {
        System.out.println("making a base url");
        String scheme = request.getMfHost().getTransferScheme().getScheme();
        String host = request.getMfHost().getHost();
        String uri = request.getMfApi().getUri();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(scheme);
        stringBuilder.append(host);
        stringBuilder.append(uri);
        System.out.println("made base url - " + stringBuilder.toString());
        return stringBuilder.toString();
    }

    private String hashString(String target, String hashAlgorithm) {
        System.out.println("hashing to " + hashAlgorithm + " - " + target);
        String hash;
        try {
            MessageDigest md = MessageDigest.getInstance(hashAlgorithm);

            md.update(target.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuilder sb = new StringBuilder();
            for (byte aByteData : byteData) {
                sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
            }
            hash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            hash = target;
        }

        System.out.println("hashing to " + hashAlgorithm + " - " + hash);
        return hash;
    }
}
