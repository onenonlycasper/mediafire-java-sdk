package com.arkhive.components.test_session_manager_fixes.module_token_farm.token_session;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.interfaces.HttpProcessor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by  on 6/15/2014.
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
     * @param apiRequestObject - an api request object which has domain/uri/parameters/etc. values stored.
     * @return a constructed URL object.
     */
    private URL createUrl(ApiRequestObject apiRequestObject) {
        System.out.println(TAG + " createUrl(ApiPostRequestObject)");
        // get references to the apiPostRequestObject that we will use.
        String domain = apiRequestObject.getDomain();
        String uri = apiRequestObject.getUri();
        Map<String, String> requiredParameters = apiRequestObject.getRequiredParameters();
        Map<String, String> optionalParameters = apiRequestObject.getOptionalParameters();

        StringBuilder stringBuilder = new StringBuilder();
        //append domain if it exists
        if (domain != null) {
            stringBuilder.append(domain);
        }
        // append uri if it exists
        if (uri != null) {
            stringBuilder.append(uri);
        }
        // append required parameters if they exist. get session token (for new st) is unique in that the signature
        // is calculated in advance and attached to required parameters because the ApplicationCredentials are
        // passed to the runnable so the signature is calculated there. it could be moved here though
        if (requiredParameters != null) {
            stringBuilder.append(constructParametersForUrl(requiredParameters));
        }
        // append any optional parameters
        if (optionalParameters != null) {
            stringBuilder.append(constructParametersForUrl(optionalParameters));
        }
        // create a string based on the uri data.
        String completedUrl = stringBuilder.toString();
        // replace the first & with ? if no ? already exists.
        completedUrl = cleanupUrlString(completedUrl);
        // return a URL object or null if the URL is malformed.
        try {
            return new URL(completedUrl);
        } catch (MalformedURLException e) {
            apiRequestObject.addExceptionDuringRequest(e);
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
