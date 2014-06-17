package com.arkhive.components.api.filesystem.folder;

import com.arkhive.components.api.ApiResponse;

/**
 * @author Chris Najar
 */
public class FolderCreateResponse extends ApiResponse {
    //CHECKSTYLE:OFF
    private String folder_key;
    private String upload_key;
    private String device_revision;
    //CHECKSTYLE:ON

    /**
     * Returns the json object "folder_key".
     *
     * @return
     */
    public String getFolderKey() {
        if (this.folder_key == null) {
            this.folder_key = "";
        }
        return this.folder_key;
    }

    /**
     * Returns the JSON object "upload_key".
     *
     * @return
     */
    public String getUploadKey() {
        if (this.upload_key == null) {
            this.upload_key = "";
        }
        return this.upload_key;
    }

    /**
     * Returns the JSON object "device_revision".
     *
     * @return
     */
    public int getDeviceRevision() {
        if (this.device_revision == null) {
            this.device_revision = "0";
        }
        return Integer.valueOf(this.device_revision);
    }
}
