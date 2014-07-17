package com.mediafire.sdk.config;

import com.mediafire.sdk.http.MFApi;
import com.mediafire.sdk.http.MFHost;
import com.mediafire.sdk.http.MFRequest;
import com.mediafire.sdk.http.MFResponse;
import com.mediafire.sdk.token.MFToken;

import java.util.List;
import java.util.Map;

/**
 * Created by Chris Najar on 7/17/2014.
 */
public class MFDefaultLogger implements MFLogger {
    @Override
    public void logMessage(String source, String message) {
        System.out.println("[" + Thread.currentThread().getName() + "] [" + source + "] - " + message);
    }

    @Override
    public void logException(String source, Exception exception) {
        StackTraceElement[] stackTraceElements = exception.getStackTrace();
        StringBuilder stringBuilder = new StringBuilder();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            stringBuilder.append(stackTraceElement.toString());
            stringBuilder.append("\n");
        }
        System.out.println("[" + Thread.currentThread().getName() + "] [" + source + "]  - " + stringBuilder.toString());
    }

    @Override
    public void logApiError(String source, MFRequest mfRequest, MFResponse mfResponse) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("...");
        stringBuilder.append("\n");
        stringBuilder.append(createRequestStringLog(mfRequest));
        stringBuilder.append(createResponseStringLog(mfResponse));
        System.out.println("[" + Thread.currentThread().getName() + "] [" + source + "] - " + stringBuilder.toString());
    }
    
    private String createRequestStringLog(MFRequest mfRequest) {
        MFApi mfApi = mfRequest.getMfApi();
        String apiEnumName = mfApi.toString();
        boolean queryPostable = mfApi.isQueryPostable();
        String uri = mfApi.getUri();
        String tokenTypeEnumName = mfApi.getTokenType().toString();

        MFHost mfHost = mfRequest.getMfHost();
        String hostEnumName = mfHost.toString();
        String host = mfHost.getHost();
        String schemeEnumName = mfHost.getTransferScheme().toString();
        String scheme = mfHost.getTransferScheme().getScheme();

        Map<String, String> requestHeaders = mfRequest.getHeaders();

        Map<String, String> requestParameters = mfRequest.getRequestParameters();

        MFToken mfToken = mfRequest.getToken();

        String tokenString;
        int payloadLength;
        if (mfToken != null) {
            tokenString = mfToken.getTokenString();
            payloadLength = mfRequest.getPayload().length;
        } else {
            tokenString = null;
            payloadLength = 0;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("MFHttpRequest").append("\n");
        stringBuilder.append("apiEnumName").append(": ").append(apiEnumName).append("\n");
        stringBuilder.append("queryPostable").append(": ").append(queryPostable).append("\n");
        stringBuilder.append("uri").append(": ").append(uri).append("\n");
        stringBuilder.append("tokenTypeEnumName").append(": ").append(tokenTypeEnumName).append("\n");
        stringBuilder.append("hostEnumName").append(": ").append(hostEnumName).append("\n");
        stringBuilder.append("host").append(": ").append(host).append("\n");
        stringBuilder.append("schemeEnumName").append(": ").append(schemeEnumName).append("\n");
        stringBuilder.append("scheme").append(": ").append(scheme).append("\n");
        stringBuilder.append("requestHeaders").append(": ").append(requestHeaders.toString()).append("\n");
        stringBuilder.append("requestParameters").append(": ").append(requestParameters.toString()).append("\n");
        stringBuilder.append("tokenString").append(": ").append(tokenString).append("\n");
        stringBuilder.append("payloadLength").append(": ").append(payloadLength).append("\n");
        stringBuilder.append("\n\n");
        return stringBuilder.toString();
    }

    private String createResponseStringLog(MFResponse mfResponse) {
        int statusCode = mfResponse.getStatus();
        int responseBodyByteLength = mfResponse.getResponseAsBytes().length;
        String responseString = mfResponse.getResponseAsString();
        Map<String, List<String>> mfHttpResponseHeaders = mfResponse.getHeaders();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("MFHttpResponse").append("\n");
        stringBuilder.append("statusCode").append(": ").append(statusCode).append("\n");
        stringBuilder.append("responseBodyByteLength").append(": ").append(responseBodyByteLength).append("\n");
        stringBuilder.append("responseString").append(": ").append(responseString).append("\n");
        stringBuilder.append("mfHttpResponseHeaders").append(": ").append(mfHttpResponseHeaders.toString()).append("\n");
        return stringBuilder.toString();
    }
}
