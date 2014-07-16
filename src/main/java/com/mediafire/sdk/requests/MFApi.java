package com.mediafire.sdk.requests;

/**
 * Created by on 6/17/2014.
 */
public enum MFApi {
    // contact api calls
    URI_CONTACT_ADD("/api/1.0/contact/add.php", TokenType.SESSION_TOKEN_V2),
    URI_CONTACT_DELETE("/api/1.0/contact/delete.php", TokenType.SESSION_TOKEN_V2),
    URI_CONTACT_FETCH("/api/1.0/contact/fetch.php", TokenType.SESSION_TOKEN_V2),
    // file api calls
    URI_FILE_COPY("/api/1.0/file/copy.php", TokenType.SESSION_TOKEN_V2),
    URI_FILE_DELETE("/api/1.0/file/delete.php", TokenType.SESSION_TOKEN_V2),
    URI_FILE_PURGE("/api/1.0/file/purge.php", TokenType.SESSION_TOKEN_V2),
    URI_FILE_MOVE("/api/1.0/file/move.php", TokenType.SESSION_TOKEN_V2),
    URI_FILE_UPDATE("/api/1.0/file/update.php", TokenType.SESSION_TOKEN_V2),
    URI_FILE_GET_INFO("/api/file/get_info.php", TokenType.SESSION_TOKEN_V2),
    URI_FILE_GET_LINKS("/api/1.0/file/get_links.php", TokenType.SESSION_TOKEN_V2),
    // folder api calls
    URI_FOLDER_COPY("/api/1.0/folder/copy.php", TokenType.SESSION_TOKEN_V2),
    URI_FOLDER_CREATE("/api/1.0/folder/create.php", TokenType.SESSION_TOKEN_V2),
    URI_FOLDER_MOVE("/api/1.0/folder/move.php", TokenType.SESSION_TOKEN_V2),
    URI_FOLDER_DELETE("/api/1.0/folder/delete.php", TokenType.SESSION_TOKEN_V2),
    URI_FOLDER_PURGE("/api/1.0/folder/purge.php", TokenType.SESSION_TOKEN_V2),
    URI_FOLDER_UPDATE("/api/1.0/folder/update.php", TokenType.SESSION_TOKEN_V2),
    URI_FOLDER_GET_INFO("/api/1.0/folder/get_info.php", TokenType.SESSION_TOKEN_V2),
    URI_FOLDER_GET_CONTENT("/api/folder/get_content.php", TokenType.SESSION_TOKEN_V2),
    URI_FOLDER_GET_REVISION("/api/1.0/folder/get_revision.php", TokenType.SESSION_TOKEN_V2),
    URI_FOLDER_SEARCH("/api/1.0/folder/search.php", TokenType.SESSION_TOKEN_V2),
    // system api calls
    URI_SYSTEM_GET_INFO("/api/1.0/system/get_info.php", TokenType.NONE),
    // user api calls
    URI_USER_GET_INFO("/api/1.0/user/get_info.php", TokenType.SESSION_TOKEN_V2),
    URI_USER_REGISTER("/api/1.0/user/register.php", TokenType.SESSION_TOKEN_V2),
    URI_USER_LINK_FACEBOOK("/api/1.0/user/link_facebook.php", TokenType.SESSION_TOKEN_V2),
    URI_USER_LINK_TWITTER("/api/1.0/user/link_twitter.php", TokenType.SESSION_TOKEN_V2),
    URI_USER_UNLINK_FACEBOOK("/api/1.0/user/unlink_facebook.php", TokenType.SESSION_TOKEN_V2),
    URI_USER_UNLINK_TWITTER("/api/1.0/user/unlink_twitter.php", TokenType.SESSION_TOKEN_V2),
    URI_USER_GET_AVATAR("/api/1.0/user/get_avatar.php", TokenType.SESSION_TOKEN_V2),
    URI_USER_SET_AVATAR("/api/1.0/user/set_avatar.php", TokenType.SESSION_TOKEN_V2),
    URI_USER_GET_SESSION_TOKEN("/api/1.0/user/get_session_token.php", TokenType.NONE),
    URI_USER_GET_ACTION_TOKEN("/api/1.0/user/get_action_token.php", TokenType.SESSION_TOKEN_V2),
    // upload api calls
    URI_UPLOAD_CHECK("/api/1.0/upload/check.php", TokenType.SESSION_TOKEN_V2),
    URI_UPLOAD_INSTANT("/api/1.0/upload/instant.php", TokenType.UPLOAD_ACTION_TOKEN),
    URI_UPLOAD_POLL_UPLOAD("/api/1.0/upload/poll_upload.php", TokenType.SESSION_TOKEN_V2),
    URI_UPLOAD_RESUMABLE("/api/upload/resumable.php", TokenType.UPLOAD_ACTION_TOKEN),
    // device api calls
    URI_DEVICE_GET_CHANGES("/api/1.0/device/get_changes.php", TokenType.SESSION_TOKEN_V2),
    URI_DEVICE_GET_STATUS("/api/1.0/device/get_status.php", TokenType.SESSION_TOKEN_V2);

    private String uri;
    private TokenType type;

    private MFApi(String uri, TokenType type) {
        this.uri = uri;
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public TokenType getTokenType() {
        return type;
    }

    public enum TokenType {
        SESSION_TOKEN_V2, UPLOAD_ACTION_TOKEN, IMAGE_ACTION_TOKEN, NONE,
    }
}
