package com.mediafire.sdk.config;

import com.mediafire.sdk.http.MFRequest;
import com.mediafire.sdk.http.MFResponse;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public interface MFLogger {
    public void logMessage(String src, String message);
    public void logException(String src, Exception exception);
    public void logApiError(String src, MFRequest mfRequest, MFResponse mfResponse);

}
