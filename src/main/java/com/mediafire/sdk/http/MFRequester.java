package com.mediafire.sdk.http;

import com.mediafire.sdk.token.MFToken;

import java.util.Map;

/**
 * Created by Chris Najar on 7/18/2014.
 */
public interface MFRequester {
    public Map<String, String> getRequestParameters();

    public Map<String, String> getHeaders();

    public byte[] getPayload();

    public MFToken getToken();

    public void setToken(MFToken MFToken);

    public String getHost();

    public MFHost.TransferProtocol getTransferProtocol();

    public String getUri();

    public MFApi.TokenType getTypeOfTokenToReturn();

    public boolean isQueryPostable();

    public MFApi.TokenType getTypeOfTokenToBorrow();

    public MFApi.TokenType getTypeOfSignatureToAdd();

    public boolean isTokenRequired();
}
