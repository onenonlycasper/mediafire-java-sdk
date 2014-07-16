package com.arkhive.components.core.module_api.requests;

import java.util.Map;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public class MFGetRequest extends MFAbstractRequest {

    public MFGetRequest(MFHost mfHost, MFApi mfApi) {
        this(mfHost, mfApi, null);
    }

    public MFGetRequest(MFHost mfHost, MFApi mfApi, Map<String, String> requestParameters) {
        super(mfHost, mfApi, requestParameters);
    }
}
