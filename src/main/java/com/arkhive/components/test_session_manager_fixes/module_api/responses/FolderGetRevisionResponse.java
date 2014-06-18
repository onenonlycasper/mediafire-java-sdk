package com.arkhive.components.test_session_manager_fixes.module_api.responses;

/**
 * Created by Chris Najar on 6/18/2014.
 */
public class FolderGetRevisionResponse extends ApiResponse {
    private String revision;
    private String epoch;

    public long getRevision() {
        if (revision == null) {
            revision = "-1";
        }
        return Long.valueOf(revision);
    }

    public long getEpoch() {
        if (epoch == null) {
            epoch = "-1";
        }
        return Long.valueOf(epoch);
    }
}
