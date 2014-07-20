package com.mediafire.sdk.api_responses.file;

import com.mediafire.sdk.api_responses.ApiResponse;

/**
 * Created by Chris Najar on 7/15/2014.
 */
public class MoveResponse extends ApiResponse {
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
