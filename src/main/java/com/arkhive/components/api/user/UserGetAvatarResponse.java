package com.arkhive.components.api.user;

import com.arkhive.components.api.ApiResponse;

/**
 * Response from user/get_avatar.
 */
public class UserGetAvatarResponse extends ApiResponse {
    private String avatar;

    public String getAvatarUrl() {
        if (avatar == null) {
            avatar = "";
        }
        return avatar;
    }
}
