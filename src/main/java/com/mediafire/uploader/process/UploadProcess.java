package com.mediafire.uploader.process;

import com.mediafire.sdk.api_responses.upload.CheckResponse;
import com.mediafire.sdk.api_responses.upload.InstantResponse;
import com.mediafire.sdk.api_responses.upload.PollResponse;
import com.mediafire.sdk.api_responses.upload.ResumableResponse;
import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.token.MFTokenFarm;
import com.mediafire.uploader.manager.UploadManager;
import com.mediafire.uploader.uploaditem.UploadItem;

abstract class UploadProcess implements Runnable {
    private static final String TAG = UploadProcess.class.getCanonicalName();
    protected final MFTokenFarm mfTokenFarm;
    protected final UploadItem uploadItem;
    protected final UploadManager uploadManager;
    
    public UploadProcess(MFTokenFarm mfTokenFarm, UploadItem uploadItem, UploadManager uploadManager) {
        this.mfTokenFarm = mfTokenFarm;
        this.uploadItem = uploadItem;
        this.uploadManager = uploadManager;
    }

    protected abstract void doUploadProcess();

    @Override
    public void run() {
        if (!uploadManager.haveStoredCredentials()) {
            MFConfiguration.getStaticMFLogger().v(TAG, "no credentials stored, task cancelling()");
        }
        doUploadProcess();
    }

    protected void notifyListenerUploadStarted() {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerUploadStarted()");
        // notify listeners that task has started.
        if (uploadManager != null) {
            uploadManager.onStartedUploadProcess(uploadItem);
        }
    }

    protected void notifyListenerCompleted(CheckResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCompleted()");
        // notify listeners that check is completed
        if (uploadManager != null) {
            uploadManager.onCheckCompleted(uploadItem, response);
        }
    }

    protected void notifyListenerCompleted(ResumableResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCompleted()");
        // notify listeners that check is completed
        if (uploadManager != null) {
            uploadManager.onResumableCompleted(uploadItem, response);
        }
    }

    protected void notifyListenerCompleted(InstantResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCompleted()");
        // notify listeners that check is completed
        if (uploadManager != null) {
            uploadManager.onInstantCompleted(uploadItem, response);
        }
    }

    protected void notifyListenerCompleted(PollResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCompleted()");
        // notify listeners that check is completed
        if (uploadManager != null) {
            uploadManager.onPollCompleted(uploadItem, response);
        }
    }

    protected void notifyListenerOnProgressUpdate(int chunkNumber, int numChunks) {
        MFConfiguration.getStaticMFLogger().v(TAG, "notifyListenerOnProgressUpdate()");
        // notify listeners of progress update
        if (uploadManager != null) {
            uploadManager.onProgressUpdate(uploadItem, chunkNumber, numChunks);
        }
    }

    protected void notifyListenerCancelled(CheckResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCancelled()");
        // notify listeners task cancelled
        if (uploadManager != null) {
            uploadManager.onCancelled(uploadItem, response);
        }
    }

    protected void notifyListenerCancelled(InstantResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCancelled()");
        // notify listeners task cancelled
        if (uploadManager != null) {
            uploadManager.onCancelled(uploadItem, response);
        }
    }

    protected void notifyListenerCancelled(ResumableResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCancelled()");
        // notify listeners task cancelled
        if (uploadManager != null) {
            uploadManager.onCancelled(uploadItem, response);
        }
    }

    protected void notifyListenerCancelled(PollResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCancelled()");
        // notify listeners task cancelled
        if (uploadManager != null) {
            uploadManager.onCancelled(uploadItem, response);
        }
    }

    protected void notifyListenerException(Exception e) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerException()");
        //notify listeners that there has been an exception
        if (uploadManager != null) {
            uploadManager.onProcessException(uploadItem, e);
        }
    }

    protected void notifyListenerLostConnection() {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerLostConnection()");
        // notify listeners that connection was lost
        if (uploadManager != null) {
            uploadManager.onLostConnection(uploadItem);
        }
    }
}
