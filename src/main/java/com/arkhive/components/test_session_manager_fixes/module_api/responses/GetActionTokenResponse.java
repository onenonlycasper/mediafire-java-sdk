package com.arkhive.components.test_session_manager_fixes.module_api.responses;

import com.arkhive.components.test_session_manager_fixes.module_api.responses.ApiResponse;

/**
 * Created by Chris Najar on 6/18/2014.
 */
public class GetActionTokenResponse extends ApiResponse {
    public String action_token;

    public String getActionToken() {
        return action_token;
    }
}
