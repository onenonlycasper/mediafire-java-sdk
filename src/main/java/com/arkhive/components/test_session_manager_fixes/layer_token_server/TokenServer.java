package com.arkhive.components.test_session_manager_fixes.layer_token_server;

import com.arkhive.components.test_session_manager_fixes.layer_http.HttpPostProcessor;
import com.arkhive.components.test_session_manager_fixes.module_session_token.ActionToken;
import com.arkhive.components.test_session_manager_fixes.module_session_token.SessionToken;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public final class TokenServer implements HttpPostProcessor.Callback {
    private int actionTokenLifeSpan;
    private int minSessionTokens;
    private int maxSessionTokens;

    public TokenServer(int actionTokenLifeSpan, int minSessionTokens, int maxSessionTokens) {
    }

    @Override
    public void actionTokenReturned(ActionToken token) {

    }

    @Override
    public void actionTokenExpired(ActionToken token) {

    }

    @Override
    public void sessionTokenReturned(SessionToken token) {

    }

    @Override
    public void sessionTokenExpired(SessionToken token) {

    }
}
