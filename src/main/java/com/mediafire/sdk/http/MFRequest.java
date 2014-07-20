package com.mediafire.sdk.http;

import com.mediafire.sdk.token.MFToken;

import java.util.LinkedHashMap;
import java.util.Map;

public final class MFRequest implements MFRequester {
    private MFHost.TransferProtocol transferProtocol;
    private MFHost.Host host;
    private String uri;
    private MFApi.TokenType typeOfTokenToBorrow;
    private MFApi.TokenType typeOfSignatureToAdd;
    private MFApi.TokenType typeOfTokenToReturn;
    private boolean isQueryPostable;
    private boolean isTokenRequired;
    private final Map<String, String> requestParameters;
    private final Map<String, String> headers;
    private final byte[] payload;
    private MFToken mfToken;

    private MFRequest(MFRequestBuilder mfRequestBuilder) {
        this.transferProtocol = mfRequestBuilder.transferProtocol;
        this.host = mfRequestBuilder.host;
        this.uri = mfRequestBuilder.uri;
        this.typeOfTokenToBorrow = mfRequestBuilder.typeOfTokenToBorrow;
        this.typeOfSignatureToAdd = mfRequestBuilder.typeOfSignatureToAdd;
        this.typeOfTokenToReturn = mfRequestBuilder.typeOfTokenToReturn;
        this.isQueryPostable = mfRequestBuilder.isQueryPostable;
        this.isTokenRequired = mfRequestBuilder.isTokenRequired;
        this.requestParameters = mfRequestBuilder.requestParameters;
        this.requestParameters.put("response_format", "json");
        this.headers = mfRequestBuilder.headers;
        this.payload = mfRequestBuilder.payload;
    }

    public MFRequest(MFHost mfHost, MFApi mfApi, Map<String, String> requestParameters) {
        if (requestParameters == null) {
            requestParameters = new LinkedHashMap<String, String>();
        }

        requestParameters.put("response_format", "json");

        this.transferProtocol = mfHost.getTransferProtocol();
        this.host = mfHost.getHost();
        this.uri = mfApi.getUri();
        this.typeOfTokenToBorrow = mfApi.getTypeOfTokenToBorrow();
        this.typeOfSignatureToAdd = mfApi.getTypeOfSignatureToAdd();
        this.typeOfTokenToReturn = mfApi.getTypeOfTokenToReturn();
        this.isQueryPostable = mfApi.isQueryPostable();
        this.isTokenRequired = mfApi.isTokenRequired();
        this.requestParameters = requestParameters;
        this.headers = new LinkedHashMap<String, String>();
        this.payload = new byte[0];
    }

    public MFRequest(MFHost mfHost, MFApi mfApi) {
        this(mfHost, mfApi, new LinkedHashMap<String, String>());
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
    public MFHost.Host getHost() {
        return host;
    }

    @Override
    public MFHost.TransferProtocol getProtocol() {
        return transferProtocol;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public MFApi.TokenType getTypeOfTokenToBorrow() {
        return typeOfTokenToBorrow;
    }

    @Override
    public MFApi.TokenType getTypeOfSignatureToAdd() {
        return typeOfSignatureToAdd;
    }

    @Override
    public MFApi.TokenType getTypeOfTokenToReturn() {
        return typeOfTokenToReturn;
    }

    @Override
    public boolean isQueryPostable() {
        return isQueryPostable;
    }

    @Override
    public boolean isTokenRequired() {
        return isTokenRequired;
    }

    public static class MFRequestBuilder {
        private MFHost.TransferProtocol transferProtocol = MFHost.TransferProtocol.HTTP;
        private MFHost.Host host = MFHost.Host.LIVE;
        private String uri = "/api/system/get_info.php";
        private MFApi.TokenType typeOfTokenToBorrow = MFApi.TokenType.NONE;
        private MFApi.TokenType typeOfSignatureToAdd = MFApi.TokenType.NONE;
        private MFApi.TokenType typeOfTokenToReturn = MFApi.TokenType.NONE;
        private boolean isQueryPostable;
        private boolean isTokenRequired;
        private Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        private Map<String, String> headers = new LinkedHashMap<String, String>();
        private byte[] payload = new byte[0];

        public MFRequestBuilder(MFHost mfHost, MFApi mfApi) {
            if (mfHost == null) {
                throw new IllegalArgumentException("MFHost cannot be null");
            }
            this.transferProtocol = mfHost.getTransferProtocol();
            this.host = mfHost.getHost();
            this.uri = mfApi.getUri();
            this.typeOfTokenToBorrow = mfApi.getTypeOfTokenToBorrow();
            this.typeOfSignatureToAdd = mfApi.getTypeOfSignatureToAdd();
            this.typeOfTokenToReturn = mfApi.getTypeOfTokenToReturn();
            this.isQueryPostable = mfApi.isQueryPostable();
            this.isTokenRequired = mfApi.isTokenRequired();
        }

        public MFRequestBuilder(MFHost mfHost, String uri) {
            if (mfHost == null) {
                throw new IllegalArgumentException("MFHost cannot be null");
            }
            this.transferProtocol = mfHost.getTransferProtocol();
            this.host = mfHost.getHost();
            this.uri = uri;
        }

        public MFRequestBuilder typeOfTokenToBorrow(MFApi.TokenType typeOfTokenToBorrow) {
            if (typeOfTokenToBorrow == null) {
                typeOfTokenToBorrow = MFApi.TokenType.NONE;
            }
            this.typeOfTokenToBorrow = typeOfTokenToBorrow;
            return this;
        }

        public MFRequestBuilder typeOfSignatureToAdd(MFApi.TokenType typeOfSignatureToAdd) {
            if (typeOfSignatureToAdd == null) {
                typeOfSignatureToAdd = MFApi.TokenType.NONE;
            }
            this.typeOfSignatureToAdd = typeOfSignatureToAdd;
            return this;
        }

        public MFRequestBuilder typeOfTokenToReturn(MFApi.TokenType typeOfTokenToReturn) {
            if (typeOfTokenToReturn == null) {
                typeOfTokenToReturn = MFApi.TokenType.NONE;
            }
            this.typeOfTokenToReturn = typeOfTokenToReturn;
            return this;
        }

        public MFRequestBuilder isQueryPostable(boolean isQueryPostable) {
            this.isQueryPostable = isQueryPostable;
            return this;
        }

        public MFRequestBuilder isTokenRequired(boolean isTokenRequired) {
            this.isTokenRequired = isTokenRequired;
            return this;
        }

        public MFRequestBuilder requestParameters(Map<String, String> requestParameters) {
            if (requestParameters == null) {
                requestParameters = new LinkedHashMap<String, String>();
            }
            this.requestParameters = requestParameters;
            return this;
        }

        public MFRequestBuilder headers(Map<String, String> headers) {
            if (headers == null) {
                headers = new LinkedHashMap<String, String>();
            }
            this.headers = headers;
            return this;
        }

        public MFRequestBuilder payload(byte[] payload) {
            if (payload == null) {
                payload = new byte[0];
            }
            this.payload = payload;
            return this;
        }

        public MFRequest build() {
            if (uri == null) {
                throw new IllegalArgumentException("uri cannot be null");
            }

            return new MFRequest(this);
        }
    }
}
