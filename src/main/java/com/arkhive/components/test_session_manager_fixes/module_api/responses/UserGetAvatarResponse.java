package com.arkhive.components.test_session_manager_fixes.module_api.responses;



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
