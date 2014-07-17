package com.mediafire.sdk.config;

import com.mediafire.sdk.http.MFHttpRequest;
import com.mediafire.sdk.http.MFHttpResponse;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public interface MFLogger {
    public void logMessage(String src, String message);
    public void logException(String src, Exception exception);
    public void logApiError(String src, MFHttpRequest mfHttpRequest, MFHttpResponse mfHttpResponse);

}
