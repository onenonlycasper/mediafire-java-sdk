package com.arkhive.components.test_session_manager_fixes.module_token_farm;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_credentials.ApplicationCredentials;
import com.arkhive.components.test_session_manager_fixes.module_credentials.CredentialsException;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPostProcessor;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.request_runnables.HttpRequestCallback;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.tokens.SessionToken;
import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by Chris Najar on 6/16/2014.
 */
public class GetSessionTokenRunnable implements Runnable, HttpRequestCallback {
    private static final String TAG = GetSessionTokenRunnable.class.getSimpleName();
    private final TokenFarmDistributor callback;
    private ApiRequestObject apiRequestObject;
    private HttpPeriProcessor httpPeriProcessor;
    private ApplicationCredentials applicationCredentials;

    public GetSessionTokenRunnable(TokenFarmDistributor callback,
                                   HttpPeriProcessor httpPeriProcessor,
                                   ApplicationCredentials applicationCredentials) {
        this.callback = callback;
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
            httpPeriProcessor.sendGetRequest(this, apiRequestObject);
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
                    new Gson().fromJson(HttpPostProcessor.getResponseElement(httpResponseString), GetSessionTokenResponse.class);
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
        Map<String, String> optionalParameters = GetSessionTokenRequestParameters.constructOptionalParameters();
        Map<String, String> requiredParameters = null;
        try {
            requiredParameters = GetSessionTokenRequestParameters.constructRequiredParameters(applicationCredentials);
        } catch (CredentialsException e) {
            e.printStackTrace();
        }
        apiRequestObject.setRequiredParameters(optionalParameters);
        apiRequestObject.setOptionalParameters(requiredParameters);

        return apiRequestObject;
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
