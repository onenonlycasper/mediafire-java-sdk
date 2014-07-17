package com.mediafire.sdk.api_responses.user;


import com.mediafire.sdk.api_responses.ApiResponse;

/**
 * Stores response from user/set_avatar.
 */
public class SetAvatarResponse extends ApiResponse {
    private String quick_key;
    private String upload_key;

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
