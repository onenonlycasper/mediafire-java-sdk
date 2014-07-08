package com.arkhive.components.uploadmanager.process;

import com.arkhive.components.core.MediaFire;
import com.arkhive.components.core.module_api.responses.UploadCheckResponse;
import com.arkhive.components.uploadmanager.listeners.UploadListenerManager;
import com.arkhive.components.uploadmanager.uploaditem.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chris Najar on 7/8/2014.
 */
public abstract class UploadProcess {
    protected final MediaFire mediaFire;
    protected final UploadItem uploadItem;
    protected final UploadListenerManager uploadListenerManager;
    private final Logger logger = LoggerFactory.getLogger(UploadProcess.class);
    
    public UploadProcess(MediaFire mediaFire, UploadItem uploadItem, UploadListenerManager uploadListenerManager) {
        this.mediaFire = mediaFire;
        this.uploadItem = uploadItem;
        this.uploadListenerManager = uploadListenerManager;
    }

    /**
     * notifies listeners that this process has completed.
     *
     * @param checkResponse - the response from calling check.php.
     */
    protected void notifyListenersCompleted(UploadCheckResponse checkResponse) {
        logger.info("  notifyListenersCompleted()");
        //notify manager that check is completed
        if (uploadListenerManager != null) {
            uploadListenerManager.onCheckCompleted(uploadItem, checkResponse);
        }
    }

    /**
     * lets listeners know that this process has started.
     */
    protected void notifyManagerUploadStarted() {
        // notify Ui listeners that task has started.
        if (uploadListenerManager != null) {
            uploadListenerManager.onStartedUploadProcess(uploadItem);
        }
    }

    protected void notifyManagerCancelled(UploadCheckResponse response) {
        if (uploadListenerManager != null) {
            uploadListenerManager.onCancelled(uploadItem, response);
        }
    }

    /**
     * lets listeners know that this process has been cancelled for this upload item. manager is informed of exception.
     *
     * @param e - exception that occurred.
     */
    protected void notifyManagerException(Exception e) {
        //notify listeners that there has been an exception
        if (uploadListenerManager != null) {
            uploadListenerManager.onProcessException(uploadItem, e);
        }
    }

    /**
     * lets listeners know that this process has been cancelled for this item. manager is informed of lost connection.
     */
    protected void notifyManagerLostConnection() {
        //notify listeners that connection was lost
        if (uploadListenerManager != null) {
            uploadListenerManager.onLostConnection(uploadItem);
        }
    }

    /**
     * gives the listeners a progress update of the number of chunks completed.
     */
    private void notifyManagerOnProgressUpdate(int chunkNumber, int numChunks) {
        logger.info(" notifyManagerOnProgressUpdate()");
        if (uploadListenerManager != null) {
            uploadListenerManager.onProgressUpdate(uploadItem, chunkNumber, numChunks);
        }
    }
}
