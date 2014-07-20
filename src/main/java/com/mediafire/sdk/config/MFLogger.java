package com.mediafire.sdk.config;

import com.mediafire.sdk.http.MFRequester;
import com.mediafire.sdk.http.MFResponse;

/**
 * TODO: doc
 */
public interface MFLogger {
    public void v(String source, String message);
    public void v(String source, String message, Throwable throwable);
    public void d(String source, String message);
    public void d(String source, String message, Throwable throwable);
    public void i(String source, String message);
    public void i(String source, String message, Throwable throwable);
    public void w(String source, String message);
    public void w(String source, String message, Throwable throwable);
    public void e(String source, String message);
    public void e(String source, String message, Throwable throwable);
    public void logApiError(String source, MFRequester mfRequester, MFResponse mfResponse);

}
