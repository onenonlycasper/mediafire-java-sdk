package com.arkhive.components.core.module_api.requests;

import java.util.Map;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public class MFPostRequest extends MFAbstractRequest {
    private Map<String, String> headers;

    public MFPostRequest(MFHost mfHost, MFApi mfApi, Map<String, String> requestParameters, Map<String, String> headers) {
        super(mfHost, mfApi, requestParameters);
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
