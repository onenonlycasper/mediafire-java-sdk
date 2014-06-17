package com.arkhive.components.test_session_manager_fixes.module_http_processor;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.tokens.SessionToken;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.tokens.Token;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public final class HttpPreProcessor {
    private static final String TAG = HttpPreProcessor.class.getSimpleName();
    public HttpPreProcessor() {}

    /**
     * processes an api request prior to making an http request.
     *
     * @param apiRequestObject - the descriptor object holding necessary values to make the http request.
     */
    public final void processApiRequestObject(ApiRequestObject apiRequestObject) {
        System.out.println(TAG + " processApiRequestObject()");
        // generate a url object using values from a descriptor object
        URL constructedUrl = createUrl(apiRequestObject);
        // sets the constructed url to the descriptor object
        apiRequestObject.setConstructedUrl(constructedUrl);
    }

    /**
     * creates a url given the values stored in an api request object
     *
     * @param apiPostRequestObject - an api request object which has domain/uri/parameters/etc. values stored.
     * @return a constructed URL object.
     */
    private URL createUrl(ApiRequestObject apiPostRequestObject) {
        System.out.println(TAG + " createUrl(ApiPostRequestObject)");
        String domain = apiPostRequestObject.getDomain();
        String uri = apiPostRequestObject.getUri();
        Map<String, String> requiredParameters = apiPostRequestObject.getRequiredParameters();
        Map<String, String> optionalParameters = apiPostRequestObject.getOptionalParameters();
        Token token = apiPostRequestObject.getToken();

        StringBuilder stringBuilder = new StringBuilder();

        if (uri != null) {
            stringBuilder.append(uri);
        }

        if (requiredParameters != null) {
            stringBuilder.append(constructParametersForUrl(requiredParameters));
        }

        if (optionalParameters != null) {
            stringBuilder.append(constructParametersForUrl(optionalParameters));
        }

        if (token != null) {
            stringBuilder.append(constructParametersForUrl(token));
        }

        String generatedUri = stringBuilder.toString();

        String signature = calculateSignatureForString(generatedUri);

        StringBuilder fullUrlBuilder = new StringBuilder();
        fullUrlBuilder.append(domain);
        fullUrlBuilder.append(generatedUri);

        if (token != null && SessionToken.class.isInstance(token)) {
            fullUrlBuilder.append("&signature=");
            fullUrlBuilder.append(signature);
        }

        String completedUrl = fullUrlBuilder.toString();


        completedUrl = cleanupUrlString(completedUrl);

        try {
            return new URL(completedUrl);
        } catch (MalformedURLException e) {
            apiPostRequestObject.addExceptionDuringRequest(e);
            return null;
        }
    }

    /**
     * constructs parameters in the &key=value format for a url.
     *
     * @param parameters - the key/value pairs to be formatted
     * @return a formatted string with the key/value paris for a url.
     */
    private String constructParametersForUrl(Map<String, String> parameters) {
        System.out.println(TAG + " constructParametersForUrl(HashMap<String, String>)");
        StringBuilder stringBuilder = new StringBuilder();
        if (parameters != null && parameters.size() > 0) {
            for (String key : parameters.keySet()) {
                stringBuilder.append("&").append(key).append("=").append(parameters.get(key));
            }
        } else {
            stringBuilder.append("");
        }
        return stringBuilder.toString();
    }

    /**
     * Returns the url portion of a request which contains the session token. in other words, &session_token=1234
     * where 1234 is the value retrieved from the parameter passed.
     *
     * @param token - the Token to get the session token string from.
     * @return a completed string.
     */
    private String constructParametersForUrl(Token token) {
        System.out.println(TAG + " constructParametersForUrl(Token)");
        StringBuilder stringBuilder = new StringBuilder();

        if (token.getTokenString() != null) {
            stringBuilder.append("&session_token=");
            stringBuilder.append(token.getTokenString());
        }

        return stringBuilder.toString();
    }

    /**
     * replaces the first instance of & with ? to generate a proper uri:
     * example: /api/user/get_session_token&blah=something would be changed to /api/user/get_session_token?blah=something
     *
     * @param urlString - a url string.
     * @return a url string with the first instance of & replaced with ?
     */
    private String cleanupUrlString(String urlString) {
        System.out.println(TAG + " cleanupUrlString()");
        String cleanedUrlString;
        if (urlString.contains("&")) {
            cleanedUrlString = urlString.replaceFirst("&", "?");
        } else {
            cleanedUrlString = urlString;
        }

        return cleanedUrlString;
    }

    /**
     * calculates an MD5 hash for a given string
     *
     * @param hashTarget - the string to get the md5 hash of.
     * @return - a String which represents the MD5 hash of the parameter passed UNLESS a NoSuchAlgorithmException occurs
     * in that case the original string will be returned.
     */
    private String calculateSignatureForString(String hashTarget) {
        String signature;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(hashTarget.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            signature = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            signature = hashTarget;
        }
        return signature;
    }
}
