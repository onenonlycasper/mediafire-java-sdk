package com.mediafire.sdk.api_responses.system;


import com.mediafire.sdk.api_responses.ApiResponse;

/**
 * api response class used by Gson for the /api/system/get_info.php call.
 */
public class GetInfoResponse extends ApiResponse {

    private TermsOfService terms_of_service;


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
