package com.arkhive.components.test_session_manager_fixes.layer_token_server;

import com.arkhive.components.test_session_manager_fixes.module_session_token.ActionToken;
import com.arkhive.components.test_session_manager_fixes.module_session_token.SessionToken;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public interface TokenServerCallback {
    /**
     * Called when a pre-existing valid action token is returned from an api request.
     * @param actionToken - the action token returned.
     */
    public void actionTokenReturned(ActionToken actionToken);

    /**
     * Called when an expired action token is returned from an api request.
     * @param actionToken
     */
    public void actionTokenExpired(ActionToken actionToken);

    /**
     * Called when a new action token is returned from an api request.
     * @param actionToken
     */
    public void newActionTokenReturned(ActionToken actionToken);

    /**
     * Called when a pre-existing valid action token is returned from an api request.
     * @param sessionToken - the action token returned.
     */
    public void sessionTokenReturned(SessionToken sessionToken);

    /**
     * Called when an expired action token is returned from an api request.
     * @param sessionToken
     */
    public void sessionTokenExpired(SessionToken sessionToken);

    /**
     * Called when a new action token is returned from an api request.
     * @param sessionToken
     */
    public void newSessionTokenReturned(SessionToken sessionToken);
}
