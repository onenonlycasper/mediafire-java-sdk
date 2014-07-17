package com.mediafire.sdk.http;

/**
 * Created by Chris Najar on 7/17/2014.
 */
public interface MFHttpClient {
    public MFHttpResponse sendRequest(MFHttpRequest request);
}
