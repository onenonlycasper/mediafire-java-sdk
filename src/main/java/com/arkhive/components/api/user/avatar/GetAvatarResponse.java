package com.arkhive.components.api.user.avatar;

import com.arkhive.components.api.ApiResponse;

/** Response from user/get_avatar. */
public class GetAvatarResponse extends ApiResponse {
    private String avatar;

    public GetAvatarResponse() {}

    public String getAvatarUrl() { 
        if (avatar == null) { avatar = ""; }
        return avatar;
    }
}
