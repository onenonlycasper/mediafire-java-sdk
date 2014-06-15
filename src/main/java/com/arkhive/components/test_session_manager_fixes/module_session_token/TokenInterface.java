package com.arkhive.components.test_session_manager_fixes.module_session_token;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public interface TokenInterface {
    public String getTokenString();
    public void setTokenString(String token);
    public void updateToken(ApiRequestObject apiRequestObject);
    public String getTokenSignature();
    public void setTokenSignature(String signature);
}
