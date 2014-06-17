package com.arkhive.components.test_session_manager_fixes.module_api.responses;

import com.arkhive.components.api.ApiResponse;

/**
 * Created by Chris Najar on 4/15/2014.
 */
public class DeviceGetStatusResponse extends ApiResponse {
    private String async_jobs_in_progress;
    private String device_revision;

    public int getRevision() {
        if (device_revision == null) {
            device_revision = "0";
        }
        return Integer.valueOf(device_revision);
    }

    public boolean isAsyncJobInProgress() {
        if (async_jobs_in_progress == null) {
            async_jobs_in_progress = "no";
        }

        return "yes".equalsIgnoreCase(async_jobs_in_progress);
    }
}
