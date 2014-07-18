package com.mediafire.sdk.http;

public enum MFApi {
    // contact api calls
    CONTACT_ADD("/api/1.0/contact/add.php", TokenType.SESSION_TOKEN_V2, true),
    CONTACT_DELETE("/api/1.0/contact/delete.php", TokenType.SESSION_TOKEN_V2, true),
    CONTACT_FETCH("/api/1.0/contact/fetch.php", TokenType.SESSION_TOKEN_V2, true),
    // file api calls
    FILE_COPY("/api/1.0/file/copy.php", TokenType.SESSION_TOKEN_V2, true),
    FILE_DELETE("/api/1.0/file/delete.php", TokenType.SESSION_TOKEN_V2, true),
    FILE_PURGE("/api/1.0/file/purge.php", TokenType.SESSION_TOKEN_V2, true),
    FILE_MOVE("/api/1.0/file/move.php", TokenType.SESSION_TOKEN_V2, true),
    FILE_UPDATE("/api/1.0/file/update.php", TokenType.SESSION_TOKEN_V2, true),
    FILE_GET_INFO("/api/file/get_info.php", TokenType.SESSION_TOKEN_V2, true),
    FILE_GET_LINKS("/api/1.0/file/get_links.php", TokenType.SESSION_TOKEN_V2, true),
    // folder api calls
    FOLDER_COPY("/api/1.0/folder/copy.php", TokenType.SESSION_TOKEN_V2, true),
    FOLDER_CREATE("/api/1.0/folder/create.php", TokenType.SESSION_TOKEN_V2, true),
    FOLDER_MOVE("/api/1.0/folder/move.php", TokenType.SESSION_TOKEN_V2, true),
    FOLDER_DELETE("/api/1.0/folder/delete.php", TokenType.SESSION_TOKEN_V2, true),
    FOLDER_PURGE("/api/1.0/folder/purge.php", TokenType.SESSION_TOKEN_V2, true),
    FOLDER_UPDATE("/api/1.0/folder/update.php", TokenType.SESSION_TOKEN_V2, true),
    FOLDER_GET_INFO("/api/1.0/folder/get_info.php", TokenType.SESSION_TOKEN_V2, true),
    FOLDER_GET_CONTENT("/api/folder/get_content.php", TokenType.SESSION_TOKEN_V2, true),
    FOLDER_GET_REVISION("/api/1.0/folder/get_revision.php", TokenType.SESSION_TOKEN_V2, true),
    FOLDER_SEARCH("/api/1.0/folder/search.php", TokenType.SESSION_TOKEN_V2, true),
    // system api calls
    SYSTEM_GET_INFO("/api/1.0/system/get_info.php", TokenType.NONE, true),
    // user api calls
    USER_GET_INFO("/api/1.0/user/get_info.php", TokenType.SESSION_TOKEN_V2, true),
    USER_REGISTER("/api/1.0/user/register.php", TokenType.SESSION_TOKEN_V2, true),
    USER_LINK_FACEBOOK("/api/1.0/user/link_facebook.php", TokenType.SESSION_TOKEN_V2, true),
    USER_LINK_TWITTER("/api/1.0/user/link_twitter.php", TokenType.SESSION_TOKEN_V2, true),
    USER_UNLINK_FACEBOOK("/api/1.0/user/unlink_facebook.php", TokenType.SESSION_TOKEN_V2, true),
    USER_UNLINK_TWITTER("/api/1.0/user/unlink_twitter.php", TokenType.SESSION_TOKEN_V2, true),
    USER_GET_AVATAR("/api/1.0/user/get_avatar.php", TokenType.SESSION_TOKEN_V2, true),
    USER_SET_AVATAR("/api/1.0/user/set_avatar.php", TokenType.SESSION_TOKEN_V2, true),
    USER_GET_SESSION_TOKEN("/api/1.0/user/get_session_token.php", TokenType.UNIQUE, true),
    USER_GET_ACTION_TOKEN("/api/1.0/user/get_action_token.php", TokenType.SESSION_TOKEN_V2, true),
    // upload api calls
    UPLOAD_CHECK("/api/1.0/upload/check.php", TokenType.SESSION_TOKEN_V2, true),
    UPLOAD_INSTANT("/api/1.0/upload/instant.php", TokenType.UPLOAD_ACTION_TOKEN, true),
    UPLOAD_POLL_UPLOAD("/api/1.0/upload/poll_upload.php", TokenType.SESSION_TOKEN_V2, true),
    UPLOAD_RESUMABLE("/api/upload/resumable.php", TokenType.UPLOAD_ACTION_TOKEN, false),
    // device api calls
    DEVICE_GET_CHANGES("/api/1.0/device/get_changes.php", TokenType.SESSION_TOKEN_V2, true),
    DEVICE_GET_STATUS("/api/1.0/device/get_status.php", TokenType.SESSION_TOKEN_V2, true);

    private final String uri;
    private final TokenType type;
    private final boolean queryPostable;

    private MFApi(String uri, TokenType type, boolean queryPostable) {
        this.uri = uri;
        this.type = type;
        this.queryPostable = queryPostable;
    }

    public String getUri() {
        return uri;
    }

    public TokenType getTokenType() {
        return type;
    }

    public boolean isQueryPostable() {
        return queryPostable;
    }

    public enum TokenType {
        SESSION_TOKEN_V2, UPLOAD_ACTION_TOKEN, IMAGE_ACTION_TOKEN, NONE, UNIQUE,
    }
}
