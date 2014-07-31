package com.mediafire.sdk.api_responses.user;

import com.mediafire.sdk.api_responses.ApiResponse;

public class GetSessionTokenResponse extends ApiResponse {
    private String session_token;
    private String secret_key;
    private String pkey;
    private String ekey;
    private String time;

    public String getSessionToken() {
        return session_token;
    }

    public String getSecretKey() {
        return secret_key;
    }

    public String getPkey() {
        return pkey;
    }

    public String getEkey() {
        return ekey;
    }

    public String getTime() {
        return time;
    }
}
