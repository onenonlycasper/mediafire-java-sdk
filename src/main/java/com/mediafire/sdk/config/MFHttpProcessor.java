package com.mediafire.sdk.config;

import com.mediafire.sdk.http.MFRequester;
import com.mediafire.sdk.http.MFResponse;

/**
 * Created by Chris Najar on 7/20/2014.
 */
public interface MFHttpProcessor {
    public MFResponse doRequest(final MFRequester mfRequester);
}
