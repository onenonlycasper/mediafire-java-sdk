package com.arkhive.components.core.module_api.responses;

/**
 * Created by  on 6/16/2014.
 */
public class GetSessionTokenResponse extends ApiResponse {
    private String session_token;
    private String secret_key;
    private String pkey;

    public String getSessionToken() {
        return session_token;
    }

    public String getSecretKey() {
        return secret_key;
    }

    public String getPkey() {
        return pkey;
    }
}
