package com.arkhive.components.test_session_manager_fixes.layer_http;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_session_token.TokenInterface;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public class HttpPreProcessor {
    ApiRequestObject apiRequestObject;

    public HttpPreProcessor(ApiRequestObject apiRequestObject) {
        this.apiRequestObject = apiRequestObject;
    }

    public void processUrl() throws MalformedURLException {
        URL constructedUrl = createUrl(apiRequestObject);
        apiRequestObject.setConstructedUrl(constructedUrl);

    }

    private URL createUrl(ApiRequestObject apiRequestObject) {
        String domain = apiRequestObject.getDomain();
        String uri = apiRequestObject.getUri();
        HashMap<String, String> requiredParameters = apiRequestObject.getRequiredParameters();
        HashMap<String, String> optionalParameters = apiRequestObject.getOptionalParameters();
        TokenInterface tokenInterface = apiRequestObject.getToken();

        StringBuilder stringBuilder = new StringBuilder();
        if (domain != null) {
            stringBuilder.append(domain);
        }
        if (uri != null) {
            stringBuilder.append(uri);
        }
        if (tokenInterface != null) {
            stringBuilder.append(constructParametersForUrl(tokenInterface));
        }
        if (requiredParameters != null) {
            stringBuilder.append(constructParametersForUrl(requiredParameters));
        }
        if (optionalParameters != null) {
            stringBuilder.append(constructParametersForUrl(optionalParameters));
        }

        String urlString = stringBuilder.toString();
        urlString = cleanupUrlString(urlString);

        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            apiRequestObject.addExceptionDuringRequest(e);
            return null;
        }
    }

    private String constructParametersForUrl(HashMap<String, String> parameters) {
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

    private String constructParametersForUrl(TokenInterface tokenInterface) {
        StringBuilder stringBuilder = new StringBuilder();

        if (tokenInterface.getTokenString() != null) {
            stringBuilder.append("&session_token=");
            stringBuilder.append(tokenInterface.getTokenString());
        }

        if (tokenInterface.getTokenSignature() != null) {
            stringBuilder.append("&signature=");
            stringBuilder.append(tokenInterface.getTokenSignature());
        }

        return stringBuilder.toString();
    }

    private String cleanupUrlString(String urlString) {
        String cleanedUrlString;
        if (urlString.contains("&")) {
            cleanedUrlString = urlString.replaceFirst("&", "?");
        } else {
            cleanedUrlString = urlString;
        }

        return cleanedUrlString;
    }
}
