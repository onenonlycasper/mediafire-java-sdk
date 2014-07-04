package com.arkhive.components.core.module_api;

/**
 * Created by on 6/17/2014.
 */
public class ApiUris {
    public static final String URI_CONTACT_ADD = "/api/1.0/contact/add.php";
    public static final String URI_CONTACT_DELETE = "/api/1.0/contact/delete.php";
    public static final String URI_CONTACT_FETCH = "/api/1.0/contact/fetch.php";
    public static final String URI_FILE_COPY = "/api/1.0/file/copy.php";
    public static final String URI_FILE_DELETE = "/api/1.0/file/delete.php";
    public static final String URI_FILE_PURGE = "/api/1.0/file/purge.php";
    public static final String URI_FILE_MOVE = "/api/1.0/file/move.php";
    public static final String URI_FILE_UPDATE = "/api/1.0/file/update.php";
    public static final String URI_FILE_GET_INFO = "/api/file/get_info.php";
    public static final String URI_FILE_GET_LINKS = "/api/1.0/file/get_links.php";
    public static final String URI_FOLDER_COPY = "/api/1.0/folder/copy.php";
    public static final String URI_FOLDER_CREATE = "/api/1.0/folder/create.php";
    public static final String URI_FOLDER_MOVE = "/api/1.0/folder/move.php";
    public static final String URI_FOLDER_DELETE = "/api/1.0/folder/delete.php";
    public static final String URI_FOLDER_PURGE = "/api/1.0/folder/purge.php";
    public static final String URI_FOLDER_UPDATE = "/api/1.0/folder/update.php";
    public static final String URI_FOLDER_GET_INFO = "/api/1.0/folder/get_info.php";
    public static final String URI_FOLDER_GET_CONTENT = "/api/folder/get_content.php"; //json object bad in 1.0
    public static final String URI_FOLDER_GET_REVISION = "/api/1.0/folder/get_revision.php";
    public static final String URI_FOLDER_SEARCH = "/api/1.0/folder/search.php";
    public static final String URI_SYSTEM_GET_INFO = "/api/1.0/system/get_info.php";
    public static final String URI_USER_GET_INFO = "/api/1.0/user/get_info.php";
    public static final String URI_USER_REGISTER = "/api/1.0/user/register.php";
    public static final String URI_USER_LINK_FACEBOOK = "/api/1.0/user/link_facebook.php";
    public static final String URI_USER_LINK_TWITTER = "/api/1.0/user/link_twitter.php";
    public static final String URI_USER_UNLINK_FACEBOOK = "/api/1.0/user/unlink_facebook.php";
    public static final String URI_USER_UNLINK_TWITTER = "/api/1.0/user/unlink_twitter.php";
    public static final String URI_USER_GET_AVATAR = "/api/1.0/user/get_avatar.php";
    public static final String URI_USER_SET_AVATAR = "/api/1.0/user/set_avatar.php";
    public static final String URI_UPLOAD_CHECK = "/api/1.0/upload/check.php";
    public static final String URI_UPLOAD_INSTANT = "/api/1.0/upload/instant.php";
    public static final String URI_UPLOAD_POLL_UPLOAD = "/api/1.0/upload/poll_upload.php";
    public static final String URI_UPLOAD_RESUMABLE = "/api/upload/resumable.php";
    public static final String URI_DEVICE_GET_CHANGES = "/api/1.0/device/get_changes.php";
    public static final String URI_DEVICE_GET_STATUS = "/api/1.0/device/get_status.php";

    public static final String LIVE_HTTP = "http://www.mediafire.com";
    public static final String LIVE_HTTPS = "https://www.mediafire.com";
    public static final String DEV_HTTP = "http://dev.mediafire.com";
    public static final String DEV_HTTPS = "https://dev.mediafire.com";

    public static final String URI_USER_GET_SESSION_TOKEN = "/api/1.0/user/get_session_token.php";
    public static final String URI_USER_GET_ACTION_TOKEN = "/api/1.0/user/get_action_token.php";
}
