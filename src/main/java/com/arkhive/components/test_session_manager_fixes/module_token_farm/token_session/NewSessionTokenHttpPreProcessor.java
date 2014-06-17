package com.arkhive.components.test_session_manager_fixes.module_token_farm.token_session;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.tokens.Token;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public final class NewSessionTokenHttpPreProcessor implements HttpProcessor {
    private static final String TAG = NewSessionTokenHttpPreProcessor.class.getSimpleName();
    public NewSessionTokenHttpPreProcessor() {}

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

        String generatedUri = stringBuilder.toString();

        StringBuilder fullUrlBuilder = new StringBuilder();
        fullUrlBuilder.append(domain);
        fullUrlBuilder.append(generatedUri);

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
     * replaces the first instance of & with ? to generate a proper uri:
     * example: /api/user/get_session_token&blah=something would be changed to /api/user/get_session_token?blah=something
     *
     * @param urlString - a url string.
     * @return a url string with the first instance of & replaced with ?
     */
    private String cleanupUrlString(String urlString) {
        System.out.println(TAG + " cleanupUrlString()");
        String cleanedUrlString;
        if (urlString.contains("&") && !urlString.contains("?")) {
            cleanedUrlString = urlString.replaceFirst("&", "?");
        } else {
            cleanedUrlString = urlString;
        }

        return cleanedUrlString;
    }
}
