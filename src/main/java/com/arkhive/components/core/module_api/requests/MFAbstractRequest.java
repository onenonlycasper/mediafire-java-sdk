package com.arkhive.components.core.module_api.requests;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public abstract class MFAbstractRequest {
    private final MFHost mfHost;
    private final MFApi mfApi;

    protected MFAbstractRequest(MFHost mfHost, MFApi mfApi) {
        if (mfHost == null) {
            throw new IllegalArgumentException("MFHost cannot be null");
        }
        if (mfApi == null) {
            throw new IllegalArgumentException("MFApi cannot be null");
        }
        this.mfHost = mfHost;
        this.mfApi = mfApi;
    }

    protected MFHost getMfHost() {
        return mfHost;
    }

    protected MFApi getMfApi() {
        return mfApi;
    }
}
