package com.mediafire.sdk.config;

import com.mediafire.sdk.http.MFRequester;
import com.mediafire.sdk.http.MFResponse;

public interface MFHttpProcessor {
    /**
     * make an api request to mediafire.
     * @param mfRequester - the MFRequester implementation.
     * @return - MFResponse containing the MFResponse received from the request.
     */
    public MFResponse doRequest(final MFRequester mfRequester);
}
