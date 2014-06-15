package com.arkhive.components.test_session_manager_fixes.module_api_descriptor;

import com.arkhive.components.test_session_manager_fixes.module_api_response.ApiResponse;
import com.arkhive.components.test_session_manager_fixes.module_session_token.TokenInterface;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public abstract class ApiRequestObject {
    private String domain;
    private String uri;
    private TokenInterface token;
    private ApiResponse apiResponse;
    private HashMap<String, String> requiredParameters;
    private HashMap<String, String> optionalParameters;
    private URL constructedUrl;
    private LinkedList<Exception> exceptionDuringRequest;
    private String httpResponseString;
    private int httpResponseCode;

    ApiRequestObject() {
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

    public final void setToken(TokenInterface token) {
        this.token = token;
    }

    public final TokenInterface getToken() {
        return token;
    }

    public final void setRequiredParameters(HashMap<String, String> requiredParameters) {
        this.requiredParameters = requiredParameters;
    }

    public final HashMap<String, String> getRequiredParameters() {
        return requiredParameters;
    }

    public final void setOptionalParameters(HashMap<String, String> optionalParameters) {
        this.optionalParameters = optionalParameters;
    }

    public final HashMap<String, String> getOptionalParameters() {
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
}
