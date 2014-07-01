package com.arkhive.components.core.module_http_processor.pre_and_post_processors;

import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.core.module_token_farm.tokens.SessionToken;
import com.arkhive.components.core.module_token_farm.tokens.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by  on 6/15/2014.
 */
public final class ApiRequestHttpPreProcessor implements HttpProcessor {
    private final Logger logger = LoggerFactory.getLogger(ApiRequestHttpPreProcessor.class);

    public ApiRequestHttpPreProcessor() {}

    /**
     * processes an api request prior to making an http request.
     *
     * @param apiRequestObject - the descriptor object holding necessary values to make the http request.
     */
    public final void processApiRequestObject(ApiRequestObject apiRequestObject) {
        logger.info(" processApiRequestObject()");
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
        logger.info(" createUrl(ApiPostRequestObject)");
        String domain = apiPostRequestObject.getDomain();
        String uri = apiPostRequestObject.getUri();
        Map<String, String> requiredParameters = apiPostRequestObject.getRequiredParameters();
        Map<String, String> optionalParameters = apiPostRequestObject.getOptionalParameters();
        Token token = apiPostRequestObject.getSessionToken();

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

        // if there is a token attached to the api request object then we need to calculate the signature correctly
        // using information from the attached session token and the uri (which contains uri, token, parameters, etc.
        String signature;
        signature = createPreHashStringForApiCallSignature(apiPostRequestObject, generatedUri);
        signature = createHash(signature);

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

    private String createPreHashStringForApiCallSignature(ApiRequestObject apiRequestObject, String generatedUri) {
        logger.info("createPreHashStringForApiCallSignature()");
        // formula is session token secret key + time + uri (concatenated)
        // get session token from api request object
        SessionToken sessionToken = apiRequestObject.getSessionToken();
        logger.info("session token: " + sessionToken.getTokenString());
        // get secret key from session token
        String secretKeyString = sessionToken.getSecretKey();
        logger.info("stored secret key: " + secretKeyString);
        int secretKey = Integer.valueOf(secretKeyString) % 256;
        logger.info("stored secret key % 256: " + secretKey);
        // get time from session token
        String time = sessionToken.getTime();
        logger.info("stored time: " + time);
        // construct pre hash signature
        // return constructed pre hash signature
        logger.info("pre hash signature: " + (String.valueOf(secretKey) + time + generatedUri));
        return String.valueOf(secretKey) + time + generatedUri;
    }

    /**
     * constructs parameters in the &key=value format for a url.
     *
     * @param parameters - the key/value pairs to be formatted
     * @return a formatted string with the key/value paris for a url.
     */
    private String constructParametersForUrl(Map<String, String> parameters) {
        logger.info(" constructParametersForUrl(HashMap<String, String>)");
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
     * Returns the url portion of a request which contains the session token. in other words, &session_token=1234
     * where 1234 is the value retrieved from the parameter passed.
     *
     * @param token - the Token to get the session token string from.
     * @return a completed string.
     */
    private String constructParametersForUrl(Token token) {
        logger.info(" constructParametersForUrl(Token)");
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
        logger.info(" cleanupUrlString()");
        String cleanedUrlString;
        if (urlString.contains("&") && !urlString.contains("?")) {
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
    private String createHash(String hashTarget) {
        logger.info("createHash()");
        logger.info("hashing: " + hashTarget);
        String signature;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(hashTarget.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuilder sb = new StringBuilder();
            for (byte aByteData : byteData) {
                sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
            }
            signature = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            signature = hashTarget;
        }
        logger.info("hashed to: " + signature);
        return signature;
    }
}
