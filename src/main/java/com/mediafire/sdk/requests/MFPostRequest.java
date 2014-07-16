package com.mediafire.sdk.requests;

import java.util.Map;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public class MFPostRequest extends MFAbstractRequest {
    private Map<String, String> headers;
    private byte[] payload;

    public MFPostRequest(MFHost mfHost, MFApi mfApi, Map<String, String> requestParameters, Map<String, String> headers, byte[] payload) {
        super(mfHost, mfApi, requestParameters);
        this.headers = headers;
        this.payload = payload;
    }

    public MFPostRequest(MFHost mfHost, MFApi mfApi, Map<String, String> requestParameters, Map<String, String> headers) {
        this(mfHost, mfApi, requestParameters, headers, null);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getPayload() {
        return payload;
    }
}
