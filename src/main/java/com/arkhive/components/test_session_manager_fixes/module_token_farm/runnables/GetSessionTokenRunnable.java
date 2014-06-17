package com.arkhive.components.test_session_manager_fixes.module_token_farm.runnables;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_credentials.ApplicationCredentials;
import com.arkhive.components.test_session_manager_fixes.module_credentials.CredentialsException;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.interfaces.HttpRequestCallback;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.token_session.GetSessionTokenResponse;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.token_session.NewSessionTokenHttpPostProcessor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.interfaces.TokenFarmDistributor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.tokens.SessionToken;
import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Chris Najar on 6/16/2014.
 */
public class GetSessionTokenRunnable implements Runnable, HttpRequestCallback {
    private static final String TAG = GetSessionTokenRunnable.class.getSimpleName();
    private static String OPTIONAL_PARAMETER_TOKEN_VERSION = "token_version";
    private static String OPTIONAL_PARAMETER_EKEY = "ekey";
    private static String OPTIONAL_PARAMETER_RESPONSE_FORMAT = "response_format";
    private static String REQUIRED_PARAMETER_APPLICATION_ID = "application_id";
    private static String REQUIRED_PARAMETER_SIGNATURE = "signature";
    private final TokenFarmDistributor callback;
    private final HttpProcessor httpPreProcessor;
    private final HttpProcessor httpPostProcessor;
    private ApiRequestObject apiRequestObject;
    private HttpPeriProcessor httpPeriProcessor;
    private ApplicationCredentials applicationCredentials;

    public GetSessionTokenRunnable(TokenFarmDistributor callback,
                                   HttpProcessor httpPreProcessor,
                                   HttpProcessor httpPostProcessor,
                                   HttpPeriProcessor httpPeriProcessor,
                                   ApplicationCredentials applicationCredentials) {
        this.callback = callback;
        this.httpPreProcessor = httpPreProcessor;
        this.httpPostProcessor = httpPostProcessor;
        this.httpPeriProcessor = httpPeriProcessor;
        this.applicationCredentials = applicationCredentials;
    }

    @Override
    public void run() {
        System.out.println(TAG + " run()");
        synchronized (this) {
            // create request object
            apiRequestObject = createApiRequestObjectForNewSessionToken();
            // send request to http handler
            httpPeriProcessor.sendGetRequest(this, httpPreProcessor, httpPostProcessor, apiRequestObject);
            try {
                wait(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                apiRequestObject.addExceptionDuringRequest(e);
            }
            // get the response string
            String httpResponseString = apiRequestObject.getHttpResponseString();
            // get the actual response in the form of GetSessionTokenResponse
            GetSessionTokenResponse response =
                    new Gson().fromJson(NewSessionTokenHttpPostProcessor.getResponseElement(httpResponseString), GetSessionTokenResponse.class);
            // attach the response to the object
            apiRequestObject.setApiResponse(response);
            // extract the SessionToken from the response
            apiRequestObject.setToken(getSessionTokenFromApiRequestObject());
            // now that we have our token, we need to make a callback to the token factory
            callback.receiveNewSessionToken(apiRequestObject);
        }
    }

    private SessionToken getSessionTokenFromApiRequestObject() {
        // create a session token object
        SessionToken sessionToken = null;
        // get the response from the api request object
        GetSessionTokenResponse response = (GetSessionTokenResponse) apiRequestObject.getApiResponse();
        // extract the token string from the response object
        String tokenString = response.getSessionToken();
        String secretKey = response.getSecretKey();
        String time = response.getTime();
        String pKey = response.getPkey();
        // if the token string is not null then finish instantiating the session token object and attach it back
        // to the api request object
        // if the token string is null then there was some sort of a problem so do not attach the session token object
        // to the api request object
        if (tokenString != null) {
            sessionToken = SessionToken.newInstance("ST from " + Thread.currentThread().getName());
            sessionToken.setTokenString(tokenString);
            sessionToken.setSecretKey(secretKey);
            sessionToken.setTime(time);
            sessionToken.setPkey(pKey);
        }
        return sessionToken;
    }

    private ApiRequestObject createApiRequestObjectForNewSessionToken() {
        ApiRequestObject apiRequestObject = new ApiRequestObject("https://www.mediafire.com", "/api/1.0/user/get_session_token.php");
        Map<String, String> optionalParameters = constructOptionalParameters();
        Map<String, String> requiredParameters = null;
        try {
            requiredParameters = constructRequiredParameters(applicationCredentials);
        } catch (CredentialsException e) {
            e.printStackTrace();
        }
        apiRequestObject.setRequiredParameters(optionalParameters);
        apiRequestObject.setOptionalParameters(requiredParameters);

        return apiRequestObject;
    }

    private Map<String, String> constructRequiredParameters(ApplicationCredentials applicationCredentials) throws CredentialsException {
        Map<String, String> requiredParameters = new LinkedHashMap<String, String>();
        requiredParameters.putAll(applicationCredentials.getCredentials());
        requiredParameters.put(REQUIRED_PARAMETER_APPLICATION_ID, applicationCredentials.getAppId());
        requiredParameters.put(REQUIRED_PARAMETER_SIGNATURE, calculateSignature(applicationCredentials));
        return requiredParameters;
    }

    public static Map<String, String> constructOptionalParameters() {
        Map<String, String> optionalParameters = new LinkedHashMap<String, String>();
        optionalParameters.put(OPTIONAL_PARAMETER_TOKEN_VERSION, "2");
        optionalParameters.put(OPTIONAL_PARAMETER_RESPONSE_FORMAT, "json");
        return optionalParameters;
    }

    private String calculateSignature(ApplicationCredentials applicationCredentials) throws CredentialsException {
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

        String signature = calculateSignatureForString(preHashString);

        return signature;
    }

    /**
     * calculates a SHA-1 hash for a given hash string.
     * @param hashTarget
     * @return
     */
    private String calculateSignatureForString(String hashTarget) {
        String signature;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            md.update(hashTarget.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            signature = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            signature = hashTarget;
        }
        return signature;
    }

    @Override
    public void httpRequestStarted(ApiRequestObject apiRequestObject) {
        System.out.println(TAG + " httpRequestStarted()");
    }

    @Override
    public void httpRequestFinished(ApiRequestObject apiRequestObject) {
        System.out.println(TAG + " httpRequestFinished()");
        synchronized (this) {
            notify();
        }
    }
}
