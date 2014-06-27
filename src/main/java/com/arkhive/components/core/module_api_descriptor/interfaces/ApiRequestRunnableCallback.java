package com.arkhive.components.core.module_api_descriptor.interfaces;


import com.arkhive.components.core.module_api.responses.ApiResponse;

/**
 * Created by  on 6/16/2014.
 */
public interface ApiRequestRunnableCallback<T extends ApiResponse> {
    public void apiRequestProcessStarted();
    public void apiRequestProcessFinished(T gsonResponse);
}
