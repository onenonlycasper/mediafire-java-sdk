package com.mediafire.sdk.http;

import com.mediafire.sdk.token.MFToken;

import java.util.LinkedHashMap;
import java.util.Map;

public final class MFRequest implements MFRequester {
    private final MFHost mfHost;
    private final MFApi mfApi;
    private final Map<String, String> requestParameters;
    private final Map<String, String> headers;
    private final byte[] payload;
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
        if (payload == null) {
            payload = new byte[0];
        }

        requestParameters.put("response_format", "json");

        this.mfHost = mfHost;
        this.mfApi = mfApi;
        this.requestParameters = requestParameters;
        this.headers = headers;
        this.payload = payload;
    }

    public MFRequest(MFHost mfHost, MFApi mfApi, Map<String, String> requestParameters, Map<String, String> headers) {
        this(mfHost, mfApi, requestParameters, headers, new byte[0]);
    }

    public MFRequest(MFHost mfHost, MFApi mfApi, Map<String, String> requestParameters) {
        this(mfHost, mfApi, requestParameters, new LinkedHashMap<String, String>(), new byte[0]);
    }

    public MFRequest(MFHost mfHost, MFApi mfApi) {
        this(mfHost, mfApi, new LinkedHashMap<String, String>(), new LinkedHashMap<String, String>(), new byte[0]);
    }

    @Override
    public Map<String, String> getRequestParameters() {
        return requestParameters;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public byte[] getPayload() {
        return payload;
    }

    @Override
    public MFToken getToken() {
        return mfToken;
    }

    @Override
    public void setToken(MFToken MFToken) {
        this.mfToken = MFToken;
    }

    @Override
    public String getHost() {
        return mfHost.getHost();
    }

    @Override
    public MFHost.TransferProtocol getTransferProtocol() {
        return mfHost.getTransferProtocol();
    }

    @Override
    public String getUri() {
        return mfApi.getUri();
    }

    @Override
    public MFApi.TokenType getTokenType() {
        return mfApi.getTokenType();
    }

    @Override
    public boolean isQueryPostable() {
        return mfApi.isQueryPostable();
    }
}
