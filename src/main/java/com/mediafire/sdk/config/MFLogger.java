package com.mediafire.sdk.config;

import com.mediafire.sdk.http.MFRequest;
import com.mediafire.sdk.http.MFResponse;

/**
 * Created by Chris Najar on 7/16/2014.
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
    public void logApiError(String source, MFRequest mfRequest, MFResponse mfResponse);

}
