package com.mediafire.sdk;

/**
 * Created by  on 7/2/2014.
 */
public interface MFLogger {
    public void logMessage(String src, String message);
    public void logException(String src, Exception exception);
    public void logApiError(String src, MFHttpRequest mfHttpRequest, MFHttpResponse mfHttpResponse);

}
