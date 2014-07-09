package com.arkhive.components.uploadmanager.process;

import com.arkhive.components.core.MediaFire;
import com.arkhive.components.core.module_api.responses.UploadCheckResponse;
import com.arkhive.components.core.module_api.responses.UploadInstantResponse;
import com.arkhive.components.core.module_api.responses.UploadPollResponse;
import com.arkhive.components.core.module_api.responses.UploadResumableResponse;
import com.arkhive.components.uploadmanager.interfaces.UploadListenerManager;
import com.arkhive.components.uploadmanager.uploaditem.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chris Najar on 7/8/2014.
 */
public abstract class UploadProcess implements Runnable {
    protected final MediaFire mediaFire;
    protected final UploadItem uploadItem;
    protected final UploadListenerManager uploadListenerManager;
    private final Logger logger = LoggerFactory.getLogger(UploadProcess.class);
    
    public UploadProcess(MediaFire mediaFire, UploadItem uploadItem, UploadListenerManager uploadListenerManager) {
        this.mediaFire = mediaFire;
        this.uploadItem = uploadItem;
        this.uploadListenerManager = uploadListenerManager;
    }

    protected abstract void doUploadProcess();

    @Override
    public void run() {
        doUploadProcess();
    }


    protected void notifyListenerUploadStarted() {
        logger.info("  notifyListenerUploadStarted()");
        // notify listeners that task has started.
        if (uploadListenerManager != null) {
            uploadListenerManager.onStartedUploadProcess(uploadItem);
        }
    }

    protected void notifyListenerCompleted(UploadCheckResponse response) {
        logger.info("  notifyListenerCompleted()");
        // notify listeners that check is completed
        if (uploadListenerManager != null) {
            uploadListenerManager.onCheckCompleted(uploadItem, response);
        }
    }

    protected void notifyListenerCompleted(UploadResumableResponse response) {
        logger.info("  notifyListenerCompleted()");
        // notify listeners that check is completed
        if (uploadListenerManager != null) {
            uploadListenerManager.onResumableCompleted(uploadItem, response);
        }
    }

    protected void notifyListenerCompleted(UploadInstantResponse response) {
        logger.info("  notifyListenerCompleted()");
        // notify listeners that check is completed
        if (uploadListenerManager != null) {
            uploadListenerManager.onInstantCompleted(uploadItem, response);
        }
    }

    protected void notifyListenerCompleted(UploadPollResponse response) {
        logger.info("  notifyListenerCompleted()");
        // notify listeners that check is completed
        if (uploadListenerManager != null) {
            uploadListenerManager.onPollCompleted(uploadItem, response);
        }
    }

    protected void notifyListenerOnProgressUpdate(int chunkNumber, int numChunks) {
        logger.info(" notifyListenerOnProgressUpdate()");
        // notify listeners of progress update
        if (uploadListenerManager != null) {
            uploadListenerManager.onProgressUpdate(uploadItem, chunkNumber, numChunks);
        }
    }

    protected void notifyListenerCancelled(UploadCheckResponse response) {
        logger.info("  notifyListenerCancelled()");
        // notify listeners task cancelled
        if (uploadListenerManager != null) {
            uploadListenerManager.onCancelled(uploadItem, response);
        }
    }

    protected void notifyListenerCancelled(UploadInstantResponse response) {
        logger.info("  notifyListenerCancelled()");
        // notify listeners task cancelled
        if (uploadListenerManager != null) {
            uploadListenerManager.onCancelled(uploadItem, response);
        }
    }

    protected void notifyListenerCancelled(UploadResumableResponse response) {
        logger.info("  notifyListenerCancelled()");
        // notify listeners task cancelled
        if (uploadListenerManager != null) {
            uploadListenerManager.onCancelled(uploadItem, response);
        }
    }

    protected void notifyListenerCancelled(UploadPollResponse response) {
        logger.info("  notifyListenerCancelled()");
        // notify listeners task cancelled
        if (uploadListenerManager != null) {
            uploadListenerManager.onCancelled(uploadItem, response);
        }
    }

    protected void notifyListenerException(Exception e) {
        logger.info("  notifyListenerException()");
        //notify listeners that there has been an exception
        if (uploadListenerManager != null) {
            uploadListenerManager.onProcessException(uploadItem, e);
        }
    }

    protected void notifyListenerLostConnection() {
        logger.info("  notifyListenerLostConnection()");
        // notify listeners that connection was lost
        if (uploadListenerManager != null) {
            uploadListenerManager.onLostConnection(uploadItem);
        }
    }
}
