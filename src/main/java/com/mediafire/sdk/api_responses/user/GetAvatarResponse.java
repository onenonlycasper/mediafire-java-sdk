package com.mediafire.sdk.api_responses.user;

import com.mediafire.sdk.api_responses.ApiResponse;

public class GetAvatarResponse extends ApiResponse {
    private String avatar;

    public String getAvatarUrl() {
        if (avatar == null) {
            avatar = "";
        }
        return avatar;
    }
}
