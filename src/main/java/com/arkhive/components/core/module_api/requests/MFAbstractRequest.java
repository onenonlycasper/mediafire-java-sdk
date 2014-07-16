package com.arkhive.components.core.module_api.requests;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public abstract class MFAbstractRequest {
    private final MFHost mfHost;
    private final MFApi mfApi;
    private final Map<String, String> requestParameters;

    public MFAbstractRequest(MFHost mfHost, MFApi mfApi, Map<String, String> requestParameters) {
        if (mfHost == null) {
            throw new IllegalArgumentException("MFHost cannot be null");
        }
        if (mfApi == null) {
            throw new IllegalArgumentException("MFApi cannot be null");
        }
        if (requestParameters == null) {
            requestParameters = new LinkedHashMap<String, String>();
            requestParameters.put("response_format", "json");
        }
        this.mfHost = mfHost;
        this.mfApi = mfApi;
        this.requestParameters = requestParameters;

    }

    public MFHost getMfHost() {
        return mfHost;
    }

    public MFApi getMfApi() {
        return mfApi;
    }

    public Map<String, String> getRequestParameters() {
        return requestParameters;
    }
}
