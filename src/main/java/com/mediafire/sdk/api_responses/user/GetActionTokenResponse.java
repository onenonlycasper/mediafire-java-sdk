package com.mediafire.sdk.api_responses.user;

import com.mediafire.sdk.api_responses.ApiResponse;

public class GetActionTokenResponse extends ApiResponse {
    public String action_token;

    public String getActionToken() {
        return action_token;
    }
}
