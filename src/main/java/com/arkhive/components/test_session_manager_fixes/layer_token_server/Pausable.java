package com.arkhive.components.test_session_manager_fixes.layer_token_server;

/**
 * Created by Chris Najar on 6/15/2014.
 *
 * Just a general interface for objects that may have a necessity for pause/resume function.
 */
public interface Pausable {

    /**
     * call method to pause some behavior.
     */
    public void pause();

    /**
     * call method to resume some behavior.
     */
    public void resume();

    /**
     * call method to check if some behavior is paused.
     * @return
     */
    public boolean isPaused();
}
