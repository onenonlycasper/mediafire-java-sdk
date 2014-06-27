package com.arkhive.components.core.module_http_processor.interfaces;

import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;

/**
 * Created by  on 6/16/2014.
 */
public interface HttpRequestCallback {
    public void httpRequestStarted(ApiRequestObject apiRequestObject);
    public void httpRequestFinished(ApiRequestObject apiRequestObject);
}
