package com.mediafire.sdk.http;

import com.mediafire.sdk.tokenfarm.MFToken;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public class MFRequest {
    private final MFHost mfHost;
    private final MFApi mfApi;
    private final Map<String, String> requestParameters;
    private Map<String, String> headers;
    private byte[] payload;
    private MFToken mfToken;

    public MFRequest(MFHost mfHost, MFApi mfApi, Map<String, String> requestParameters, Map<String, String> headers, byte[] payload) {
        if (mfHost == null) {
            throw new IllegalArgumentException("MFHost cannot be null");
        }
        if (mfApi == null) {
            throw new IllegalArgumentException("MFApi cannot be null");
        }
        if (requestParameters == null) {
            requestParameters = new LinkedHashMap<String, String>();
        }

        requestParameters.put("response_format", "json");

        this.mfHost = mfHost;
        this.mfApi = mfApi;
        this.requestParameters = requestParameters;
        this.headers = headers;
        this.payload = payload;
    }

    public MFRequest(MFHost mfHost, MFApi mfApi, Map<String, String> requestParameters) {
        this(mfHost, mfApi, requestParameters, null, null);
    }

    public MFRequest(MFHost mfHost, MFApi mfApi) {
        this(mfHost, mfApi, null, null, null);
    }

    public MFHost getMfHost() {
        return mfHost;
    }

    public MFApi getMfApi() {
        return mfApi;
    }

    public Map<String, String> getRequestParameters() {
        return requestParameters;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getPayload() {
        return payload;
    }

    public MFToken getToken() {
        return mfToken;
    }

    public void setToken(MFToken MFToken) {
        this.mfToken = MFToken;
    }
}
