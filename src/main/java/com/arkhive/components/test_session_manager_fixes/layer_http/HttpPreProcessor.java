package com.arkhive.components.test_session_manager_fixes.layer_http;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiGetRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiPostRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_session_token.TokenInterface;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public final class HttpPreProcessor {
    private ApiRequestObject apiRequestObject;

    public HttpPreProcessor(ApiRequestObject apiRequestObject) {
        this.apiRequestObject = apiRequestObject;
    }

    public final void processUrl() throws MalformedURLException {
        URL constructedUrl;
        if (ApiPostRequestObject.class.isInstance(apiRequestObject)) {
            System.out.println("API POST REQUEST API POST REQUEST");
            constructedUrl = createUrl((ApiPostRequestObject) apiRequestObject);
        } else {
            System.out.println("API GET REQUEST API GET REQUEST");
            constructedUrl = createUrl((ApiGetRequestObject) apiRequestObject);
        }

        apiRequestObject.setConstructedUrl(constructedUrl);
    }

    private URL createUrl(ApiGetRequestObject apiGetRequestObject) {
        String domain = apiGetRequestObject.getDomain();
        String uri = apiGetRequestObject.getUri();
        HashMap<String, String> requiredParameters = apiGetRequestObject.getRequiredParameters();
        HashMap<String, String> optionalParameters = apiGetRequestObject.getOptionalParameters();
        TokenInterface tokenInterface = apiGetRequestObject.getToken();

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
            apiGetRequestObject.addExceptionDuringRequest(e);
            return null;
        }
    }

    private URL createUrl(ApiPostRequestObject apiPostRequestObject) {
        String domain = apiPostRequestObject.getDomain();
        String uri = apiPostRequestObject.getUri();
        HashMap<String, String> requiredParameters = apiPostRequestObject.getRequiredParameters();
        HashMap<String, String> optionalParameters = apiPostRequestObject.getOptionalParameters();
        TokenInterface tokenInterface = apiPostRequestObject.getToken();

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
            apiPostRequestObject.addExceptionDuringRequest(e);
            return null;
        }
    }

    protected final String constructParametersForUrl(HashMap<String, String> parameters) {
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

    protected final String constructParametersForUrl(TokenInterface tokenInterface) {
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

    protected final String cleanupUrlString(String urlString) {
        String cleanedUrlString;
        if (urlString.contains("&")) {
            cleanedUrlString = urlString.replaceFirst("&", "?");
        } else {
            cleanedUrlString = urlString;
        }

        return cleanedUrlString;
    }
}
