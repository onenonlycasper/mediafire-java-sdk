package com.arkhive.components.uploadmanager.manager;

/**
 * Created by Chris Najar on 5/23/2014.
 */
public interface NetChecker {
    public void lostConnection();
    public void acquiredConnection();
}
