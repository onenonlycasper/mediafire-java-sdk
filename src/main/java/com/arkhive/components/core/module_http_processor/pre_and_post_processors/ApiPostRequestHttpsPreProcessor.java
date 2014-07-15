package com.arkhive.components.core.module_http_processor.pre_and_post_processors;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by  on 7/14/2014.
 */
public class ApiPostRequestHttpsPreProcessor implements HttpProcessor {
    private static final String TAG = ApiRequestHttpPreProcessor.class.getCanonicalName();

    public ApiPostRequestHttpsPreProcessor() {}

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
     * @param apiRequestObject - an api request object which has domain/uri/parameters/etc. values stored.
     * @return a constructed URL object.
     */
    private URL createUrl(ApiRequestObject apiRequestObject) {
        Configuration.getErrorTracker().i(TAG, "createUrl(ApiPostRequestObject)");
        String domain = apiRequestObject.getDomain();
        String uri = apiRequestObject.getUri();
        Map<String, String> requiredParameters = apiRequestObject.getRequiredParameters();

        StringBuilder stringBuilder = new StringBuilder();

        if (uri != null) {
            stringBuilder.append(uri);
        }

        String queryPortionOfUri = createUriExceptForFullKey(requiredParameters);

        StringBuilder fullUrlBuilder = new StringBuilder();
        fullUrlBuilder.append(domain);
        fullUrlBuilder.append(uri);
        fullUrlBuilder.append(queryPortionOfUri);

        String completedUrl = fullUrlBuilder.toString();

        try {
            return new URL(completedUrl);
        } catch (MalformedURLException e) {
            Configuration.getErrorTracker().w(TAG, "Exception: " + e);
            apiRequestObject.addExceptionDuringRequest(e);
            return null;
        }
    }

    private String createUriExceptForFullKey(Map<String, String> requiredParameters) {
        if (requiredParameters != null) {
            if (!requiredParameters.containsKey("response_format")) {
                requiredParameters.put("response_format", "json");
            }
        }

        String remainingUri = "?";

        for (String key : requiredParameters.keySet()) {
            if (!key.equals("full")) {
                remainingUri += key;
                remainingUri += "=";
                remainingUri += requiredParameters.get(key);
                remainingUri += "&";
            }
        }

        return remainingUri.substring(0, remainingUri.length() - 1);
    }
}
