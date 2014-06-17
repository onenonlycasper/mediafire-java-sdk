package com.arkhive.components.api.system;

import com.arkhive.components.api.ApiResponse;

/**
 * api response class used by Gson for the /api/system/get_info.php call.
 */
public class SystemGetInfoResponse extends ApiResponse {
    //CHECKSTYLE:OFF
    private TermsOfService terms_of_service;

    //CHECKSTYLE:ON
    public TermsOfService getTermsOfService() {
        return terms_of_service;
    }

    /**
     * Terms of Service portion of the response.
     */
    public class TermsOfService {
        private String terms;
        private String date;
        private String revision;

        public String getTerms() {
            return this.terms;
        }

        public String getDate() {
            return this.date;
        }

        public String getRevision() {
            return this.revision;
        }
    }
}
