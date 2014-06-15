package com.arkhive.components.test_session_manager_fixes.layer_token_server;

import com.arkhive.components.test_session_manager_fixes.module_session_token.ActionToken;
import com.arkhive.components.test_session_manager_fixes.module_session_token.SessionToken;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public interface TokenServerCallback {
    public void actionTokenReturned(ActionToken token);
    public void actionTokenExpired(ActionToken token);
    public void sessionTokenReturned(SessionToken token);
    public void sessionTokenExpired(SessionToken token);
    public void newSessionTokenReturned(SessionToken token);
}
