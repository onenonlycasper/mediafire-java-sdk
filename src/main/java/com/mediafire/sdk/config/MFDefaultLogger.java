package com.mediafire.sdk.config;

import com.mediafire.sdk.http.MFApi;
import com.mediafire.sdk.http.MFHost;
import com.mediafire.sdk.http.MFRequest;
import com.mediafire.sdk.http.MFResponse;
import com.mediafire.sdk.token.MFToken;

import java.util.List;
import java.util.Map;

public class MFDefaultLogger implements MFLogger {
    @Override
    public void v(String source, String message) {
        e(source, message);
    }

    @Override
    public void v(String source, String message, Throwable throwable) {
        e(source, message, throwable);
    }

    @Override
    public void d(String source, String message) {
        e(source, message);
    }

    @Override
    public void d(String source, String message, Throwable throwable) {
        e(source, message, throwable);
    }

    @Override
    public void i(String source, String message) {
        e(source, message);
    }

    @Override
    public void i(String source, String message, Throwable throwable) {
        e(source, message, throwable);
    }

    @Override
    public void w(String source, String message) {
        e(source, message);
    }

    @Override
    public void w(String source, String message, Throwable throwable) {
        e(source, message, throwable);
    }

    @Override
    public void e(String source, String message) {
        System.out.println("[" + Thread.currentThread().getName() + "] [" + source + "] - " + message);
    }

    @Override
    public void e(String source, String message, Throwable throwable) {
        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        StringBuilder stringBuilder = new StringBuilder();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            stringBuilder.append(stackTraceElement.toString());
            stringBuilder.append("\n");
        }
        System.out.println("[" + Thread.currentThread().getName() + "] [" + source + "]  - " + stringBuilder.toString());
    }

    @Override
    public void logApiError(String source, MFRequest mfRequest, MFResponse mfResponse) {
        System.out.println("[" + Thread.currentThread().getName() + "] [" + source + "] - " + "..." + "\n" + createRequestStringLog(mfRequest) + createResponseStringLog(mfResponse));
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

        return "MFHttpRequest" + "\n" +
                "apiEnumName" + ": " + apiEnumName + "\n" +
                "queryPostable" + ": " + queryPostable + "\n" +
                "uri" + ": " + uri + "\n" +
                "tokenTypeEnumName" + ": " + tokenTypeEnumName + "\n" +
                "hostEnumName" + ": " + hostEnumName + "\n" +
                "host" + ": " + host + "\n" +
                "schemeEnumName" + ": " + schemeEnumName + "\n" +
                "scheme" + ": " + scheme + "\n" +
                "requestHeaders" + ": " + requestHeaders.toString() + "\n" +
                "requestParameters" + ": " + requestParameters.toString() + "\n" +
                "tokenString" + ": " + tokenString + "\n" +
                "payloadLength" + ": " + payloadLength + "\n" + "\n\n";
    }

    private String createResponseStringLog(MFResponse mfResponse) {
        int statusCode = mfResponse.getStatus();
        int responseBodyByteLength = mfResponse.getResponseAsBytes().length;
        String responseString = mfResponse.getResponseAsString();
        Map<String, List<String>> mfHttpResponseHeaders = mfResponse.getHeaders();

        return "MFHttpResponse" + "\n" + "statusCode" + ": " + statusCode + "\n" + "responseBodyByteLength" + ": " + responseBodyByteLength + "\n" + "responseString" + ": " + responseString + "\n" + "mfHttpResponseHeaders" + ": " + mfHttpResponseHeaders.toString() + "\n";
    }
}
