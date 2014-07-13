package com.arkhive.components.core.module_token_farm.runnables;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.module_api.ApiUris;
import com.arkhive.components.core.module_api.responses.GetSessionTokenResponse;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_credentials.ApplicationCredentials;
import com.arkhive.components.core.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpRequestCallback;
import com.arkhive.components.core.module_token_farm.interfaces.GetNewSessionTokenCallback;
import com.arkhive.components.core.module_token_farm.tokens.SessionToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by  on 6/16/2014.
 */
public class GetSessionTokenRunnable implements Runnable, HttpRequestCallback {
    private static final String OPTIONAL_PARAMETER_TOKEN_VERSION = "token_version";
    private static final String OPTIONAL_PARAMETER_EKEY = "ekey";
    private static final String OPTIONAL_PARAMETER_RESPONSE_FORMAT = "response_format";
    private static final String REQUIRED_PARAMETER_APPLICATION_ID = "application_id";
    private static final String REQUIRED_PARAMETER_SIGNATURE = "signature";
    private static final String TAG = GetSessionTokenRunnable.class.getSimpleName();
    private final GetNewSessionTokenCallback getNewSessionTokenCallback;
    private final HttpProcessor httpPreProcessor;
    private final HttpProcessor httpPostProcessor;
    private ApiRequestObject apiRequestObject;
    private final HttpPeriProcessor httpPeriProcessor;
    private final ApplicationCredentials applicationCredentials;

    public GetSessionTokenRunnable(GetNewSessionTokenCallback getNewSessionTokenCallback,
                                   HttpProcessor httpPreProcessor,
                                   HttpProcessor httpPostProcessor,
                                   HttpPeriProcessor httpPeriProcessor,
                                   ApplicationCredentials applicationCredentials) {
        this.getNewSessionTokenCallback = getNewSessionTokenCallback;
        this.httpPreProcessor = httpPreProcessor;
        this.httpPostProcessor = httpPostProcessor;
        this.httpPeriProcessor = httpPeriProcessor;
        this.applicationCredentials = applicationCredentials;
    }

    @Override
    public void run() {
        Configuration.getErrorTracker().i(TAG, "sendRequest()");
        synchronized (this) {
            // create request object
            apiRequestObject = createApiRequestObjectForNewSessionToken();
            // send request to http handler
            httpPeriProcessor.sendHttpsGetRequest(this, httpPreProcessor, httpPostProcessor, apiRequestObject);
            try {
                wait(Configuration.DEFAULT_HTTP_CONNECTION_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
                apiRequestObject.addExceptionDuringRequest(e);
            }
            printData(apiRequestObject);
            // get the response string
            String httpResponseString = apiRequestObject.getHttpResponseString();
            // get the actual response in the form of GetSessionTokenResponse
            GetSessionTokenResponse response =
                    new Gson().fromJson(getResponseElement(httpResponseString), GetSessionTokenResponse.class);
            // attach the response to the object
            apiRequestObject.setApiResponse(response);
            // extract the SessionToken from the response
            apiRequestObject.setSessionToken(getSessionTokenFromApiRequestObject());
            // now that we have our token, we need to make a getNewSessionTokenCallback to the token factory
            getNewSessionTokenCallback.receiveNewSessionToken(apiRequestObject);
        }
    }

    private void printData(ApiRequestObject apiRequestObject) {
        Configuration.getErrorTracker().i(TAG, "printData()");
        Configuration.getErrorTracker().i(TAG, "response code: " + apiRequestObject.getHttpResponseCode());
        Configuration.getErrorTracker().i(TAG, "response string: " + apiRequestObject.getHttpResponseString());
        Configuration.getErrorTracker().i(TAG, "domain used: " + apiRequestObject.getDomain());
        Configuration.getErrorTracker().i(TAG, "uri used: " + apiRequestObject.getUri());
        for (String key : apiRequestObject.getRequiredParameters().keySet()) {
            Configuration.getErrorTracker().i(TAG, "required parameter passed (key, value): " + key + ", " + apiRequestObject.getRequiredParameters().get(key));
        }
        for (String key : apiRequestObject.getOptionalParameters().keySet()) {
            Configuration.getErrorTracker().i(TAG, "required parameter passed (key, value): " + key + ", " + apiRequestObject.getOptionalParameters().get(key));
        }

        if (apiRequestObject.getSessionToken() != null) {
            SessionToken sessionToken = apiRequestObject.getSessionToken();
            Configuration.getErrorTracker().i(TAG, "session token secret key used: " + sessionToken.getSecretKey());
            Configuration.getErrorTracker().i(TAG, "session token time used: " + sessionToken.getTime());
        }
        Configuration.getErrorTracker().i(TAG, "original url: " + apiRequestObject.getConstructedUrl());
    }

    private SessionToken getSessionTokenFromApiRequestObject() {
        Configuration.getErrorTracker().i(TAG, "getSessionTokenFromApiRequestObject()");
        // create a session token object
        SessionToken sessionToken = null;
        // get the response from the api request object
        GetSessionTokenResponse response = (GetSessionTokenResponse) apiRequestObject.getApiResponse();
        if (response == null) {
            return null;
        }
        // extract the token string and other values from the response object to set the values to the session token
        String tokenString = response.getSessionToken();
        String secretKey = response.getSecretKey();
        String time = response.getTime();
        String pKey = response.getPkey();
        // if the token string is not null then finish instantiating the session token object and attach it back
        // to the api request object
        // if the token string is null then there was some sort of a problem so do not attach the session token object
        // to the api request object
        if (tokenString != null) {
            sessionToken = SessionToken.newInstance();
            sessionToken.setTokenString(tokenString);
            sessionToken.setSecretKey(secretKey);
            sessionToken.setTime(time);
            sessionToken.setPkey(pKey);
        }
        return sessionToken;
    }

    private ApiRequestObject createApiRequestObjectForNewSessionToken() {
        Configuration.getErrorTracker().i(TAG, "createApiRequestObjectForNewSessionToken()");
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTPS, ApiUris.URI_USER_GET_SESSION_TOKEN);
        Map<String, String> optionalParameters = constructOptionalParameters();
        Map<String, String> requiredParameters = constructRequiredParameters(applicationCredentials);
        apiRequestObject.setRequiredParameters(requiredParameters);
        apiRequestObject.setOptionalParameters(optionalParameters);

        return apiRequestObject;
    }

    private Map<String, String> constructRequiredParameters(ApplicationCredentials applicationCredentials) {
        Configuration.getErrorTracker().i(TAG, "constructRequiredParameters()");
        Map<String, String> requiredParameters = new LinkedHashMap<String, String>();
        requiredParameters.putAll(applicationCredentials.getCredentials());
        requiredParameters.put(REQUIRED_PARAMETER_APPLICATION_ID, applicationCredentials.getAppId());
        requiredParameters.put(REQUIRED_PARAMETER_SIGNATURE, calculateSignature(applicationCredentials));
        return requiredParameters;
    }

    private Map<String, String> constructOptionalParameters() {
        Configuration.getErrorTracker().i(TAG, "constructOptionalParameters()");
        Map<String, String> optionalParameters = new LinkedHashMap<String, String>();
        optionalParameters.put(OPTIONAL_PARAMETER_TOKEN_VERSION, "2");
        optionalParameters.put(OPTIONAL_PARAMETER_RESPONSE_FORMAT, "json");
        return optionalParameters;
    }

    private String calculateSignature(ApplicationCredentials applicationCredentials) {
        Configuration.getErrorTracker().i(TAG, "calculateSignature()");
        Map<String, String> credentialsMap = applicationCredentials.getCredentials();
        String appId = applicationCredentials.getAppId();
        String apiKey = applicationCredentials.getApiKey();

        StringBuilder stringBuilder = new StringBuilder();
        for (String key : credentialsMap.keySet()) {
            stringBuilder.append(credentialsMap.get(key));
        }
        stringBuilder.append(appId);
        stringBuilder.append(apiKey);

        String preHashString = stringBuilder.toString();

        return calculateSignatureForString(preHashString);
    }

    /**
     * calculates a SHA-1 hash for a given hash string.
     * @param hashTarget - target string to hash.
     * @return hashed string.
     */
    private String calculateSignatureForString(String hashTarget) {
        Configuration.getErrorTracker().i(TAG, "calculateSignatureForString()");
        Configuration.getErrorTracker().i(TAG, "pre hashed string: " + hashTarget);
        String signature;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

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
        Configuration.getErrorTracker().i(TAG, "hashed string: " + signature);
        return signature;
    }

    /**
     * All response strings returned from the web api are wrapped in "response" json element.
     * This method strips the "response" element, and converts the remaining element into a JsonElement via GSON.
     *
     * @param response A response string from a web API call.
     * @return The JsonElement created from the response string.
     */
    private JsonElement getResponseElement(String response) {
        Configuration.getErrorTracker().i(TAG, "getResponseElement()");
        if (response == null) {
            return null;
        }
        if (response.isEmpty()) {
            return null;
        }
        JsonElement returnJson = new JsonObject();
        JsonParser parser = new JsonParser();
        JsonElement rootElement = parser.parse(response);
        if (rootElement.isJsonObject()) {
            JsonElement jsonResult = rootElement.getAsJsonObject().get("response");
            if (jsonResult.isJsonObject()) {
                returnJson = jsonResult.getAsJsonObject();
            }
        }
        return returnJson;
    }

    @Override
    public void httpRequestStarted(ApiRequestObject apiRequestObject) {
        Configuration.getErrorTracker().i(TAG, "httpRequestStarted()");
    }

    @Override
    public void httpRequestFinished(ApiRequestObject apiRequestObject) {
        Configuration.getErrorTracker().i(TAG, "httpRequestFinished()");
        synchronized (this) {
            notify();
        }
    }
}
