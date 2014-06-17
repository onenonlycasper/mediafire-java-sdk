package com.arkhive.components.api.upload.responses;

import com.arkhive.components.api.ApiResponse;
import com.arkhive.components.api.upload.errors.ResumableResultCode;

/**
 * This data structure represents response received by the response received from api call /api/upload/resumable.php.
 *
 * @author Chris Najar
 */
public class ResumableResponse extends ApiResponse {
    private String server;
    private DoUpload doupload;

    /**
     * Data structure which is part of the response received by /api/upload/resumable.php.
     *
     * @author Chris Najar
     */
    public class DoUpload {
        private String result;
        private String key;

        public ResumableResultCode getResultCode() {
            if (result == null || result.isEmpty()) {
                return ResumableResultCode.fromInt(0);
            }
            return ResumableResultCode.fromInt(Integer.parseInt(result));
        }

        public String getPollUploadKey() {
            if (key == null) {
                return "";
            }
            return key;
        }
    }

    public String getServer() {
        if (server == null) {
            return "";
        }
        return server;
    }

    public DoUpload getDoUpload() {
        if (doupload == null) {
            return new DoUpload();
        }
        return doupload;
    }
}

