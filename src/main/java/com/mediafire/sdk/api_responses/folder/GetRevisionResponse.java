package com.mediafire.sdk.api_responses.folder;

import com.mediafire.sdk.api_responses.ApiResponse;

public class GetRevisionResponse extends ApiResponse {
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
