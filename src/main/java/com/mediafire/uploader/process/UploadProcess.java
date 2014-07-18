package com.mediafire.uploader.process;

import com.mediafire.sdk.api_responses.upload.CheckResponse;
import com.mediafire.sdk.api_responses.upload.InstantResponse;
import com.mediafire.sdk.api_responses.upload.PollResponse;
import com.mediafire.sdk.api_responses.upload.ResumableResponse;
import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.token.MFTokenFarm;
import com.mediafire.uploader.interfaces.UploadListenerManager;
import com.mediafire.uploader.uploaditem.UploadItem;

abstract class UploadProcess implements Runnable {
    private static final String TAG = UploadProcess.class.getCanonicalName();
    protected final MFTokenFarm mfTokenFarm;
    protected final UploadItem uploadItem;
    private final UploadListenerManager uploadListenerManager;
    
    public UploadProcess(MFTokenFarm mfTokenFarm, UploadItem uploadItem, UploadListenerManager uploadListenerManager) {
        this.mfTokenFarm = mfTokenFarm;
        this.uploadItem = uploadItem;
        this.uploadListenerManager = uploadListenerManager;
    }

    protected abstract void doUploadProcess();

    @Override
    public void run() {
        doUploadProcess();
    }

    protected void notifyListenerUploadStarted() {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerUploadStarted()");
        // notify listeners that task has started.
        if (uploadListenerManager != null) {
            uploadListenerManager.onStartedUploadProcess(uploadItem);
        }
    }

    protected void notifyListenerCompleted(CheckResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCompleted()");
        // notify listeners that check is completed
        if (uploadListenerManager != null) {
            uploadListenerManager.onCheckCompleted(uploadItem, response);
        }
    }

    protected void notifyListenerCompleted(ResumableResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCompleted()");
        // notify listeners that check is completed
        if (uploadListenerManager != null) {
            uploadListenerManager.onResumableCompleted(uploadItem, response);
        }
    }

    protected void notifyListenerCompleted(InstantResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCompleted()");
        // notify listeners that check is completed
        if (uploadListenerManager != null) {
            uploadListenerManager.onInstantCompleted(uploadItem, response);
        }
    }

    protected void notifyListenerCompleted(PollResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCompleted()");
        // notify listeners that check is completed
        if (uploadListenerManager != null) {
            uploadListenerManager.onPollCompleted(uploadItem, response);
        }
    }

    protected void notifyListenerOnProgressUpdate(int chunkNumber, int numChunks) {
        MFConfiguration.getStaticMFLogger().v(TAG, "notifyListenerOnProgressUpdate()");
        // notify listeners of progress update
        if (uploadListenerManager != null) {
            uploadListenerManager.onProgressUpdate(uploadItem, chunkNumber, numChunks);
        }
    }

    protected void notifyListenerCancelled(CheckResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCancelled()");
        // notify listeners task cancelled
        if (uploadListenerManager != null) {
            uploadListenerManager.onCancelled(uploadItem, response);
        }
    }

    protected void notifyListenerCancelled(InstantResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCancelled()");
        // notify listeners task cancelled
        if (uploadListenerManager != null) {
            uploadListenerManager.onCancelled(uploadItem, response);
        }
    }

    protected void notifyListenerCancelled(ResumableResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCancelled()");
        // notify listeners task cancelled
        if (uploadListenerManager != null) {
            uploadListenerManager.onCancelled(uploadItem, response);
        }
    }

    protected void notifyListenerCancelled(PollResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCancelled()");
        // notify listeners task cancelled
        if (uploadListenerManager != null) {
            uploadListenerManager.onCancelled(uploadItem, response);
        }
    }

    protected void notifyListenerException(Exception e) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerException()");
        //notify listeners that there has been an exception
        if (uploadListenerManager != null) {
            uploadListenerManager.onProcessException(uploadItem, e);
        }
    }

    protected void notifyListenerLostConnection() {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerLostConnection()");
        // notify listeners that connection was lost
        if (uploadListenerManager != null) {
            uploadListenerManager.onLostConnection(uploadItem);
        }
    }
}
