package com.arkhive.components.test_session_manager_fixes.module_http_processor;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_session_token.Token;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public final class HttpPreProcessor {
    public HttpPreProcessor() {}

    public final void processApiRequestObject(ApiRequestObject apiRequestObject) {
        System.out.println("processApiRequestObject()");
        URL constructedUrl = createUrl(apiRequestObject);

        apiRequestObject.setConstructedUrl(constructedUrl);
    }

    private URL createUrl(ApiRequestObject apiPostRequestObject) {
        System.out.println("createUrl(ApiPostRequestObject)");
        String domain = apiPostRequestObject.getDomain();
        String uri = apiPostRequestObject.getUri();
        Map<String, String> requiredParameters = apiPostRequestObject.getRequiredParameters();
        Map<String, String> optionalParameters = apiPostRequestObject.getOptionalParameters();
        Token token = apiPostRequestObject.getToken();

        StringBuilder stringBuilder = new StringBuilder();
        if (domain != null) {
            stringBuilder.append(domain);
        }

        if (uri != null) {
            stringBuilder.append(uri);
        }

        if (token != null) {
            stringBuilder.append(constructParametersForUrl(token));
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
            apiPostRequestObject.addExceptionDuringRequest(e);
            return null;
        }
    }

    private String constructParametersForUrl(Map<String, String> parameters) {
        System.out.println("constructParametersForUrl(HashMap<String, String>)");
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

    private String constructParametersForUrl(Token token) {
        System.out.println("constructParametersForUrl(Token)");
        StringBuilder stringBuilder = new StringBuilder();

        if (token.getTokenString() != null) {
            stringBuilder.append("&session_token=");
            stringBuilder.append(token.getTokenString());
        }

        if (token.getTokenSignature() != null) {
            stringBuilder.append("&signature=");
            stringBuilder.append(token.getTokenSignature());
        }

        return stringBuilder.toString();
    }

    private String cleanupUrlString(String urlString) {
        System.out.println("cleanupUrlString()");
        String cleanedUrlString;
        if (urlString.contains("&")) {
            cleanedUrlString = urlString.replaceFirst("&", "?");
        } else {
            cleanedUrlString = urlString;
        }

        return cleanedUrlString;
    }
}
