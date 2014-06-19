package com.arkhive.components.test_session_manager_fixes.module_token_farm.interfaces;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.tokens.ActionToken;

/**
 * Created by  on 6/16/2014.
 */
public interface TokenFarmDistributor {
    public void borrowSessionToken(ApiRequestObject apiRequestObject);
    public void returnSessionToken(ApiRequestObject apiRequestObject);
    public void receiveNewSessionToken(ApiRequestObject apiRequestObject);

    public void borrowActionToken(ApiRequestObject apiRequestObject, ActionToken.Type type);
    public void receiveNewImageActionToken(ApiRequestObject apiRequestObject);
    public void receiveNewUploadActionToken(ApiRequestObject apiRequestObject);
}
