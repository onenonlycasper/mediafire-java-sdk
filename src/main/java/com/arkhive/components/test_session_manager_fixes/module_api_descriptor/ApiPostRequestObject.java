package com.arkhive.components.test_session_manager_fixes.module_api_descriptor;

import java.util.HashMap;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public class ApiPostRequestObject extends ApiRequestObject {
    private HashMap<String, String> postHeaders;
    private byte[] payload;

    public HashMap<String, String> getPostHeaders() {
        return postHeaders;
    }

    public void setPostHeaders(HashMap<String, String> postHeaders) {
        this.postHeaders = postHeaders;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}
