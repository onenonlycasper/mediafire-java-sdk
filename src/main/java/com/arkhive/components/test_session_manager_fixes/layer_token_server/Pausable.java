package com.arkhive.components.test_session_manager_fixes.layer_token_server;

/**
 * Created by Chris Najar on 6/15/2014.
 *
 * Just a general interface for objects that may have a necessity for pause/resume function.
 */
public interface Pausable {

    public void pause();

    public void resume();

    public boolean isPaused();
}
