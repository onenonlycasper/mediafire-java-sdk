package com.arkhive.components.uploadmanager;

import com.arkhive.components.uploadmanager.uploaditem.UploadItem;

/**
 * Created by Chris Najar on 5/24/2014.
 */
public interface UploadRunnable extends Runnable {
    public UploadItem getUploadItem();
}
