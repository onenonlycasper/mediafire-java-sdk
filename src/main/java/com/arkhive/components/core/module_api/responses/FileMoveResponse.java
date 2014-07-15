package com.arkhive.components.core.module_api.responses;

/**
 * Created by Chris Najar on 7/15/2014.
 */
public class FileMoveResponse extends ApiResponse {
    private MyFilesRevision myfiles_revision;

    public MyFilesRevision getMyfilesRevision() {
        return myfiles_revision;
    }

    private class MyFilesRevision {
        public String revision;
        public String epoch;

        public String getRevision() {
            return revision;
        }

        public String getEpoch() {
            return epoch;
        }
    }
}
