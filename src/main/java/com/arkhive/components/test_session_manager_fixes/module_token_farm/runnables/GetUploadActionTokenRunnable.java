package com.arkhive.components.test_session_manager_fixes.module_token_farm.runnables;

import com.arkhive.components.test_session_manager_fixes.Configuration;
import com.arkhive.components.test_session_manager_fixes.module_api.Api;
import com.arkhive.components.test_session_manager_fixes.module_api.ApiUris;
import com.arkhive.components.test_session_manager_fixes.module_api.responses.GetActionTokenResponse;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.interfaces.HttpRequestCallback;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.interfaces.GetNewActionTokenCallback;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.interfaces.SessionTokenDistributor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.tokens.ActionToken;
import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Chris Najar on 6/19/2014.
 */
public class GetUploadActionTokenRunnable implements Runnable, HttpRequestCallback {
    private static final String TAG = GetImageActionTokenRunnable.class.getSimpleName();
    private static final String REQUIRED_PARAMETER_TYPE = "type";
    private static final String OPTIONAL_PARAMETER_LIFESPAN = "lifespan";
    private static final String OPTIONAL_PARAMETER_RESPONSE_FORMAT = "response_format";
    private final HttpProcessor httpPreProcessor;
    private final HttpProcessor httpPostProcessor;
    private final SessionTokenDistributor sessionTokenDistributor;
    private GetNewActionTokenCallback actionTokenCallback;
    private HttpPeriProcessor httpPeriProcessor;

    public GetUploadActionTokenRunnable(HttpProcessor httpPreProcessor,
                                       HttpProcessor httpPostProcessor,
                                       GetNewActionTokenCallback actionTokenCallback,
                                       SessionTokenDistributor sessionTokenDistributor,
                                       HttpPeriProcessor httpPeriProcessor) {
        this.httpPreProcessor = httpPreProcessor;
        this.httpPostProcessor = httpPostProcessor;
        this.actionTokenCallback = actionTokenCallback;
        this.sessionTokenDistributor = sessionTokenDistributor;
        this.httpPeriProcessor = httpPeriProcessor;
    }

    @Override
    public void run() {
        System.out.println(TAG + " sendRequest()");
        synchronized (this) {
            // set up our api request object
            ApiRequestObject apiRequestObject = setupApiRequestObjectForNewImageActionToken();
            // borrow a session token from the TokenFarm
            sessionTokenDistributor.borrowSessionToken(apiRequestObject);
            // send request to http handler
            httpPeriProcessor.sendGetRequest(this, httpPreProcessor, httpPostProcessor, apiRequestObject);
            // wait until we get a response from http handler (notify will be called in callback implementation)
            try {
                wait(Configuration.DEFAULT_HTTP_CONNECTION_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // try to return the session token to the TokenFarm
            sessionTokenDistributor.returnSessionToken(apiRequestObject);
            // try to give action token to the TokenFarm
            GetActionTokenResponse response = new Gson().fromJson(Api.getResponseString(apiRequestObject.getHttpResponseString()), GetActionTokenResponse.class);
            if (response != null && !response.hasError()) {
                ActionToken actionToken = ActionToken.newInstance(ActionToken.Type.UPLOAD);
                String actionTokenString = response.getActionToken();
                if (actionTokenString != null) {
                    actionToken.setTokenString(response.getActionToken());
                    actionToken.setExpiration(System.currentTimeMillis() + 86400000);
                    apiRequestObject.setActionToken(actionToken);
                    apiRequestObject.setActionTokenInvalid(false);
                } else {
                    apiRequestObject.setActionTokenInvalid(true);
                }
            } else {
                apiRequestObject.setActionTokenInvalid(true);
            }

            actionTokenCallback.receiveNewUploadActionToken(apiRequestObject);
        }
    }

    private ApiRequestObject setupApiRequestObjectForNewImageActionToken() {
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_USER_GET_ACTION_TOKEN);
        apiRequestObject.setRequiredParameters(constructRequiredParameters());
        apiRequestObject.setOptionalParameters(constructOptionalParameters());
        return apiRequestObject;
    }

    private Map<String, String> constructRequiredParameters() {
        Map<String, String> requiredParameters = new LinkedHashMap<String, String>();
        requiredParameters.put(REQUIRED_PARAMETER_TYPE, "upload");
        return requiredParameters;
    }

    public static Map<String, String> constructOptionalParameters() {
        Map<String, String> optionalParameters = new LinkedHashMap<String, String>();
        optionalParameters.put(OPTIONAL_PARAMETER_LIFESPAN, "1440");
        optionalParameters.put(OPTIONAL_PARAMETER_RESPONSE_FORMAT, "json");
        return optionalParameters;
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
