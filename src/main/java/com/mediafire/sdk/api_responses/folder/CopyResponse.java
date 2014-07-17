package com.mediafire.sdk.api_responses.folder;



import com.mediafire.sdk.api_responses.ApiResponse;

import java.util.LinkedList;
import java.util.List;

/**
 */
public class CopyResponse extends ApiResponse {
    private String asynchronous;

    private List<String> new_folderkeys;
    private String device_revision;

    /**
     * Returns the JSON object "asynchronous" as a boolean
     *
     * @return true if "yes", false if "no"
     */
    public boolean isAsynchronous() {
        if (this.asynchronous == null) {
            this.asynchronous = "no";
        }

        return !"no".equalsIgnoreCase(this.asynchronous);
    }

    /**
     * Returns the JSON object "new_folderkeys".
     *
     * @return
     */
    public List<String> getNewFolderKeys() {
        if (this.new_folderkeys == null) {
            this.new_folderkeys = new LinkedList<String>();
        }
        return this.new_folderkeys;
    }

    /**
     * Represents the JSON object "device_revision".
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
