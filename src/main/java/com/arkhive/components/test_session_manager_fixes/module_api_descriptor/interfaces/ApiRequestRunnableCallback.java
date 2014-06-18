package com.arkhive.components.test_session_manager_fixes.module_api_descriptor.interfaces;

import com.arkhive.components.test_session_manager_fixes.module_api.responses.ApiResponse;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;

/**
 * Created by  on 6/16/2014.
 */
public interface ApiRequestRunnableCallback<T extends ApiResponse> {
    public void apiRequestProcessStarted();
    public void apiRequestProcessFinished(T apiResponse);
}
