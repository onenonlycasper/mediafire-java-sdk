package com.arkhive.components.test_session_manager_fixes.module_http_processor;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_session_token.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public final class HttpPreProcessor {
    private static Logger logger = LoggerFactory.getLogger(HttpPreProcessor.class);

    public HttpPreProcessor() {}

    public final void processApiRequestObject(ApiRequestObject apiRequestObject) {
        logger.debug("processApiRequestObject()");
        URL constructedUrl = createUrl(apiRequestObject);

        apiRequestObject.setConstructedUrl(constructedUrl);
    }

    private URL createUrl(ApiRequestObject apiPostRequestObject) {
        logger.debug("createUrl(ApiPostRequestObject)");
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

    private final String constructParametersForUrl(Map<String, String> parameters) {
        logger.debug("constructParametersForUrl(HashMap<String, String>)");
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

    private final String constructParametersForUrl(Token token) {
        logger.debug("constructParametersForUrl(Token)");
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

    private final String cleanupUrlString(String urlString) {
        logger.debug("cleanupUrlString()");
        String cleanedUrlString;
        if (urlString.contains("&")) {
            cleanedUrlString = urlString.replaceFirst("&", "?");
        } else {
            cleanedUrlString = urlString;
        }

        return cleanedUrlString;
    }
}
