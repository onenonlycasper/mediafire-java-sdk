package com.arkhive.components.core.module_api.responses;

import java.util.LinkedList;
import java.util.List;

/**
 */
public class FileGetLinksResponse extends ApiResponse {

    private String direct_download_free_bandwidth;
    private String one_time_download_request_count;

    private List<Link> links;

    public int getDirectDownloadFreeBandwidth() {
        if (this.direct_download_free_bandwidth == null) {
            this.direct_download_free_bandwidth = "0";
        }
        return Integer.valueOf(this.direct_download_free_bandwidth);
    }

    public int getOneTimeDownloadRequestCount() {
        if (this.one_time_download_request_count == null) {
            this.one_time_download_request_count = "0";
        }
        return Integer.valueOf(this.one_time_download_request_count);
    }

    public List<Link> getLinks() {
        if (this.links == null) {
            this.links = new LinkedList<Link>();
        }

        return links;
    }

    /**
     * class representing a link.
     */
    public class Link {
        private String quickkey;
        private String view;
        private String edit;
        private String error;

        private String direct_download_error_message;
        private String direct_download_error;
        private String one_time_download_error_message;
        private String edit_error_message;
        private String one_time_download;
        private String normal_download;
        private String direct_download;


        public String getEditErrorMessage() {
            if (this.edit_error_message == null) {
                this.edit_error_message = "";
            }
            return edit_error_message;
        }

        public String getDirectDownloadErrorMessage() {
            if (this.direct_download_error_message == null) {
                this.direct_download_error_message = "";
            }
            return direct_download_error_message;
        }

        public int getDirectDownloadErrorCode() {
            if (this.direct_download_error == null) {
                this.direct_download_error = "0";
            }
            return Integer.valueOf(direct_download_error);
        }

        public String getOneTimeDownloadErrorMessage() {
            if (this.one_time_download_error_message == null) {
                this.one_time_download_error_message = "";
            }
            return one_time_download_error_message;
        }

        public String getQuickkey() {
            if (this.quickkey == null) {
                this.quickkey = "";
            }
            return this.quickkey;
        }

        public String getViewLink() {
            if (this.view == null) {
                this.view = "";
            }
            return this.view;
        }

        public String getNormalDownloadLink() {
            if (this.normal_download == null) {
                this.normal_download = "";
            }
            return this.normal_download;
        }

        public String getDirectDownloadLink() {
            if (this.direct_download == null) {
                this.direct_download = "";
            }
            return this.direct_download;
        }

        public String getEditLink() {
            if (this.edit == null) {
                this.edit = "";
            }
            return this.edit;
        }

        public String getOneTimeDownloadLink() {
            if (this.one_time_download == null) {
                this.one_time_download = "";
            }
            return this.one_time_download;
        }

        public String getError() {
            if (this.error == null) {
                this.error = "";
            }
            return this.error;
        }

    }
}
