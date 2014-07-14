package com.arkhive.components.uploadmanager.process;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.MediaFire;
import com.arkhive.components.core.module_api.responses.UploadCheckResponse;
import com.arkhive.components.core.module_api.responses.UploadInstantResponse;
import com.arkhive.components.core.module_api.responses.UploadPollResponse;
import com.arkhive.components.core.module_api.responses.UploadResumableResponse;
import com.arkhive.components.uploadmanager.interfaces.UploadListenerManager;
import com.arkhive.components.uploadmanager.uploaditem.UploadItem;



/**
 * Created by  on 7/8/2014.
 */
public abstract class UploadProcess implements Runnable {
    private static final String TAG = UploadProcess.class.getSimpleName();
    protected final MediaFire mediaFire;
    protected final UploadItem uploadItem;
    protected final UploadListenerManager uploadListenerManager;
    
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
        Configuration.getErrorTracker().i(TAG, " notifyListenerUploadStarted()");
        // notify listeners that task has started.
        if (uploadListenerManager != null) {
            uploadListenerManager.onStartedUploadProcess(uploadItem);
        }
    }

    protected void notifyListenerCompleted(UploadCheckResponse response) {
        Configuration.getErrorTracker().i(TAG, " notifyListenerCompleted()");
        // notify listeners that check is completed
        if (uploadListenerManager != null) {
            uploadListenerManager.onCheckCompleted(uploadItem, response);
        }
    }

    protected void notifyListenerCompleted(UploadResumableResponse response) {
        Configuration.getErrorTracker().i(TAG, " notifyListenerCompleted()");
        // notify listeners that check is completed
        if (uploadListenerManager != null) {
            uploadListenerManager.onResumableCompleted(uploadItem, response);
        }
    }

    protected void notifyListenerCompleted(UploadInstantResponse response) {
        Configuration.getErrorTracker().i(TAG, " notifyListenerCompleted()");
        // notify listeners that check is completed
        if (uploadListenerManager != null) {
            uploadListenerManager.onInstantCompleted(uploadItem, response);
        }
    }

    protected void notifyListenerCompleted(UploadPollResponse response) {
        Configuration.getErrorTracker().i(TAG, " notifyListenerCompleted()");
        // notify listeners that check is completed
        if (uploadListenerManager != null) {
            uploadListenerManager.onPollCompleted(uploadItem, response);
        }
    }

    protected void notifyListenerOnProgressUpdate(int chunkNumber, int numChunks) {
        Configuration.getErrorTracker().i(TAG, "notifyListenerOnProgressUpdate()");
        // notify listeners of progress update
        if (uploadListenerManager != null) {
            uploadListenerManager.onProgressUpdate(uploadItem, chunkNumber, numChunks);
        }
    }

    protected void notifyListenerCancelled(UploadCheckResponse response) {
        Configuration.getErrorTracker().i(TAG, " notifyListenerCancelled()");
        // notify listeners task cancelled
        if (uploadListenerManager != null) {
            uploadListenerManager.onCancelled(uploadItem, response);
        }
    }

    protected void notifyListenerCancelled(UploadInstantResponse response) {
        Configuration.getErrorTracker().i(TAG, " notifyListenerCancelled()");
        // notify listeners task cancelled
        if (uploadListenerManager != null) {
            uploadListenerManager.onCancelled(uploadItem, response);
        }
    }

    protected void notifyListenerCancelled(UploadResumableResponse response) {
        Configuration.getErrorTracker().i(TAG, " notifyListenerCancelled()");
        // notify listeners task cancelled
        if (uploadListenerManager != null) {
            uploadListenerManager.onCancelled(uploadItem, response);
        }
    }

    protected void notifyListenerCancelled(UploadPollResponse response) {
        Configuration.getErrorTracker().i(TAG, " notifyListenerCancelled()");
        // notify listeners task cancelled
        if (uploadListenerManager != null) {
            uploadListenerManager.onCancelled(uploadItem, response);
        }
    }

    protected void notifyListenerException(Exception e) {
        Configuration.getErrorTracker().i(TAG, " notifyListenerException()");
        //notify listeners that there has been an exception
        if (uploadListenerManager != null) {
            uploadListenerManager.onProcessException(uploadItem, e);
        }
    }

    protected void notifyListenerLostConnection() {
        Configuration.getErrorTracker().i(TAG, " notifyListenerLostConnection()");
        // notify listeners that connection was lost
        if (uploadListenerManager != null) {
            uploadListenerManager.onLostConnection(uploadItem);
        }
    }
}
