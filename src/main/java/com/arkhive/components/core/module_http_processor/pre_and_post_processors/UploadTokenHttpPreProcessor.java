package com.arkhive.components.core.module_http_processor.pre_and_post_processors;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.core.module_token_farm.tokens.Token;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by on 6/19/2014.
 */
public class UploadTokenHttpPreProcessor implements HttpProcessor {
    private static final String TAG = UploadTokenHttpPreProcessor.class.getCanonicalName();

    public UploadTokenHttpPreProcessor() {}

    /**
     * processes an api request prior to making an http request.
     *
     * @param apiRequestObject - the descriptor object holding necessary values to make the http request.
     */
    public final void processApiRequestObject(ApiRequestObject apiRequestObject) {
        Configuration.getErrorTracker().i(TAG, "processApiRequestObject()");
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
        Configuration.getErrorTracker().i(TAG, "createUrl(ApiPostRequestObject)");
        String domain = apiPostRequestObject.getDomain();
        String uri = apiPostRequestObject.getUri();
        Map<String, String> requiredParameters = apiPostRequestObject.getRequiredParameters();
        Map<String, String> optionalParameters = apiPostRequestObject.getOptionalParameters();
        Token token = apiPostRequestObject.getActionToken();

        StringBuilder stringBuilder = new StringBuilder();

        if (uri != null) {
            stringBuilder.append(uri);
        }

        if (requiredParameters != null) {
            stringBuilder.append(constructParametersForUrl(requiredParameters));
        }

        if (optionalParameters != null) {
            if (!optionalParameters.containsKey("response_format")) {
                optionalParameters.put("response_format", "json");
            }
            stringBuilder.append(constructParametersForUrl(optionalParameters));
        } else {
            optionalParameters = new LinkedHashMap<String, String>();
            optionalParameters.put("response_format", "json");
        }

        if (token != null) {
            stringBuilder.append(constructParametersForUrl(token));
        }

        String generatedUri = stringBuilder.toString();

        // prior to generating the signature we need to clean up the url (replace first & with ?)
        generatedUri = cleanupUrlString(generatedUri);

        String completedUrl = domain + generatedUri;

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
        Configuration.getErrorTracker().i(TAG, "constructParametersForUrl(HashMap<String, String>)");
        StringBuilder stringBuilder = new StringBuilder();
        if (parameters != null && !parameters.isEmpty()) {
            for (String key : parameters.keySet()) {
                stringBuilder.append("&").append(key).append("=").append(parameters.get(key));
            }
        } else {
            stringBuilder.append("");
        }
        return stringBuilder.toString();
    }

    /**
     * Returns the url portion of a request which contains the action token. in other words, &session_token=1234
     * where 1234 is the value retrieved from the parameter passed.
     *
     * @param token - the Token to get the action token string from.
     * @return a completed string.
     */
    private String constructParametersForUrl(Token token) {
        Configuration.getErrorTracker().i(TAG, "constructParametersForUrl(Token)");
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
        Configuration.getErrorTracker().i(TAG, "cleanupUrlString()");
        String cleanedUrlString;
        if (urlString.contains("&") && !urlString.contains("?")) {
            cleanedUrlString = urlString.replaceFirst("&", "?");
        } else {
            cleanedUrlString = urlString;
        }

        return cleanedUrlString;
    }
}