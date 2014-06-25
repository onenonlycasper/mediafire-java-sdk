package com.arkhive.components.api.user.avatar;

import com.arkhive.components.api.ApiResponse;

/**
 * Stores response from user/set_avatar.
 */
public class SetAvatarResponse extends ApiResponse {

    // CHECKSTYLE:OFF
    private String quick_key;
    private String upload_key;
    // CHECKSTYLE:ON

    public String getQuickKey() {
        if (quick_key == null) {
            quick_key = "";
        }
        return quick_key;
    }

    public String getUploadKey() {
        if (upload_key == null) {
            upload_key = "";
        }
        return upload_key;
    }
}
