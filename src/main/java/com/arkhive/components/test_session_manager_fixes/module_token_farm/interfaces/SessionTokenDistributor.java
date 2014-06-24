package com.arkhive.components.test_session_manager_fixes.module_token_farm.interfaces;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;

/**
 * Created by  on 6/16/2014.
 */
public interface SessionTokenDistributor {
    public void borrowSessionToken(ApiRequestObject apiRequestObject);
    public void returnSessionToken(ApiRequestObject apiRequestObject);
}
