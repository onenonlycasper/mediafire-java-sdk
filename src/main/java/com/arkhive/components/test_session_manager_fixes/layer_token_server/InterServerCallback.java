package com.arkhive.components.test_session_manager_fixes.layer_token_server;

import com.arkhive.components.test_session_manager_fixes.module_session_token.SessionToken;

/**
 * Created by Chris Najar on 6/15/2014.
 *
 * Interface used so ActionTokenServer can request a new SessionToken. This is done via the TokenServerDirector
 * because the ActionTokenServer should not communicate with the SessionTokenServer.
 */
public interface InterServerCallback {
    public void requestSessionTokenInternal();
    public void grantSessionTokenInternal(SessionToken sessionToken);
    public void returnSessionTokenInternal(SessionToken sessionToken);
}
