package com.arkhive.components.test_session_manager_fixes.module_api_descriptor;

import com.arkhive.components.test_session_manager_fixes.module_api_response.ApiResponse;
import com.arkhive.components.test_session_manager_fixes.module_session_token.ActionToken;
import com.arkhive.components.test_session_manager_fixes.module_session_token.SessionToken;
import com.arkhive.components.test_session_manager_fixes.module_session_token.Token;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public abstract class ApiRequestObject {
    private String domain;
    private String uri;
    private ApiResponse apiResponse;
    private Map<String, String> requiredParameters;
    private Map<String, String> optionalParameters;
    private URL constructedUrl;
    private LinkedList<Exception> exceptionDuringRequest;
    private String httpResponseString;
    private int httpResponseCode;
    private HashMap<String, String> postHeaders;
    private byte[] payload;
    private ActionToken actionToken;
    private SessionToken sessionToken;
    private boolean tokenValid;

    public ApiRequestObject() {
        super();
    }

    public final void setDomain(String domain) {
        this.domain = domain;
    }

    public final String getDomain() {
        return domain;
    }

    public final void setUri(String uri) {
        this.uri = uri;
    }

    public final String getUri() {
        return uri;
    }

    public final void setRequiredParameters(Map<String, String> requiredParameters) {
        this.requiredParameters = requiredParameters;
    }

    public final Map<String, String> getRequiredParameters() {
        return requiredParameters;
    }

    public final void setOptionalParameters(Map<String, String> optionalParameters) {
        this.optionalParameters = optionalParameters;
    }

    public final Map<String, String> getOptionalParameters() {
        return optionalParameters;
    }

    public final void setConstructedUrl(URL constructedUrl) {
        this.constructedUrl = constructedUrl;
    }

    public final URL getConstructedUrl() {
        return constructedUrl;
    }

    public final void setApiResponse(ApiResponse apiResponse) {
        this.apiResponse = apiResponse;
    }

    public final ApiResponse getApiResponse() {
        return apiResponse;
    }

    public final LinkedList<Exception> getExceptionsDuringRequest() {
        return exceptionDuringRequest;
    }

    public final void addExceptionDuringRequest(Exception exceptionDuringRequest) {
        if (this.exceptionDuringRequest != null) {
            this.exceptionDuringRequest.add(exceptionDuringRequest);
        } else {
            this.exceptionDuringRequest = new LinkedList<Exception>();
            this.exceptionDuringRequest.add(exceptionDuringRequest);
        }
    }

    public String getHttpResponseString() {
        return httpResponseString;
    }

    public void setHttpResponseString(String httpResponseString) {
        this.httpResponseString = httpResponseString;
    }

    public int getHttpResponseCode() {
        return httpResponseCode;
    }

    public void setHttpResponseCode(int httpResponseCode) {
        this.httpResponseCode = httpResponseCode;
    }

    public HashMap<String, String> getPostHeaders() {
        return postHeaders;
    }

    public void setPostHeaders(HashMap<String, String> postHeaders) {
        this.postHeaders = postHeaders;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public Token getToken() {
        return actionToken == null ? sessionToken : actionToken;
    }

    public void setToken(Token token) {
        if(ActionToken.class.isInstance(token)) {
            actionToken = (ActionToken) token;
            sessionToken = null;
        } else {
            sessionToken = (SessionToken) token;
            actionToken = null;
        }
    }

    public boolean isTokenValid() {
        return tokenValid;
    }

    public void setTokenValid(boolean tokenValid) {
        this.tokenValid = tokenValid;
    }
}
