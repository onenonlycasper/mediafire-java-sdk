package com.mediafire.sdk.api_responses.folder;


import com.mediafire.sdk.api_responses.ApiResponse;

/**
 * @author
 */
public class MoveResponse extends ApiResponse {

    private MyFilesRevision myfiles_revision;
    private String device_revision;


    public int getDeviceRevision() {
        if (this.device_revision == null) {
            this.device_revision = "0";
        }
        return Integer.valueOf(device_revision);
    }

    public MyFilesRevision getMyFilesRevision() {
        if (this.myfiles_revision == null) {
            this.myfiles_revision = new MyFilesRevision();
        }
        return this.myfiles_revision;
    }

    /**
     * Class representation of "myfiles_revision" JSON object.
     *
     * @author
     */
    public class MyFilesRevision {
        private String revision;
        private String epoch;

        /**
         * Returns JSON object "revision".
         *
         * @return String representation of "revision".
         */
        public String getRevision() {
            if (this.revision == null) {
                this.revision = "";
            }
            return this.revision;
        }

        /**
         * Returns JSON object "epoch".
         *
         * @return long representation of "epoch".
         */
        public long getEpoch() {
            if (this.epoch == null) {
                this.epoch = "0";
            }
            return Long.valueOf(this.epoch);
        }
    }
}
