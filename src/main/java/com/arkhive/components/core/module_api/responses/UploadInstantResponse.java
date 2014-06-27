package com.arkhive.components.core.module_api.responses;



/**
 * This class represents the response received as a data structure when making the api call api/upload/pre_upload.php.
 *
 * @author
 */
public class UploadInstantResponse extends ApiResponse {
    private String quickkey;
    private String filename;

    private String device_revision;

    private Revision newrevision;
    private Revision newfolderrevision;

    /**
     * This class represents part of the data structure for the upload/instant.php response.
     *
     * @author
     */
    public class Revision {
        private String revision;
        private String epoch;

        public String getRevision() {
            if (revision == null) {
                return "";
            }
            return revision;
        }

        public long getEpoch() {
            if (epoch == null) {
                return 0;
            }
            return Long.parseLong(epoch);
        }
    }

    public String getQuickkey() {
        if (quickkey == null) {
            return "";
        }
        return quickkey;
    }

    public int getDeviceRevision() {
        if (device_revision == null || device_revision.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(device_revision);
    }

    public Revision getNewRevision() {
        if (newrevision == null) {
            return new Revision();
        }
        return newrevision;
    }

    public Revision getNewFolderRevision() {
        if (newfolderrevision == null) {
            return new Revision();
        }
        return newfolderrevision;
    }

    public String getFileName() {
        if (filename == null) {
            return "";
        }
        return filename;
    }
}

