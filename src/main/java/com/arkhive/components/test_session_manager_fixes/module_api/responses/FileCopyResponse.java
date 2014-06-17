package com.arkhive.components.test_session_manager_fixes.module_api.responses;

import com.arkhive.components.api.ApiResponse;

import java.util.LinkedList;
import java.util.List;

/**
 * Response from a file copy request.
 */
public class FileCopyResponse extends ApiResponse {
    //CHECKSTYLE:OFF
    private int skipped_count;
    private int other_count;
    private int device_revision;
    private List<String> new_quickkeys;
    //CHECKSTYLE:ON

    public int getSkippedCount() {
        return skipped_count;
    }

    public int getOtherCount() {
        return other_count;
    }

    public int getDeviceRevision() {
        return device_revision;
    }

    public List<String> getNewQuickKeys() {
        if (new_quickkeys == null) {
            new_quickkeys = new LinkedList<String>();
        }
        return new_quickkeys;
    }
}
