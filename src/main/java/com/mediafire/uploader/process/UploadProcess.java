package com.mediafire.uploader.process;

import com.mediafire.sdk.api_responses.upload.CheckResponse;
import com.mediafire.sdk.api_responses.upload.InstantResponse;
import com.mediafire.sdk.api_responses.upload.PollResponse;
import com.mediafire.sdk.api_responses.upload.ResumableResponse;
import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.token.MFTokenFarm;
import com.mediafire.uploader.manager.UploadManagerWorker;
import com.mediafire.uploader.uploaditem.UploadItem;

abstract class UploadProcess implements Runnable {
    private static final String TAG = UploadProcess.class.getCanonicalName();
    protected final MFTokenFarm mfTokenFarm;
    protected final UploadItem uploadItem;
    protected final UploadManagerWorker uploadManagerWorker;
    
    public UploadProcess(MFTokenFarm mfTokenFarm, UploadItem uploadItem, UploadManagerWorker uploadManagerWorker) {
        this.mfTokenFarm = mfTokenFarm;
        this.uploadItem = uploadItem;
        this.uploadManagerWorker = uploadManagerWorker;
    }

    protected abstract void doUploadProcess();

    @Override
    public void run() {
        if (!uploadManagerWorker.haveStoredCredentials()) {
            MFConfiguration.getStaticMFLogger().v(TAG, "no credentials stored, task cancelling()");
        }
        doUploadProcess();
    }

    protected void notifyListenerUploadStarted() {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerUploadStarted()");
        // notify listeners that task has started.
        if (uploadManagerWorker != null) {
            uploadManagerWorker.onStartedUploadProcess(uploadItem);
        }
    }

    protected void notifyListenerCompleted(CheckResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCompleted()");
        // notify listeners that check is completed
        if (uploadManagerWorker != null) {
            uploadManagerWorker.onCheckCompleted(uploadItem, response);
        }
    }

    protected void notifyListenerCompleted(ResumableResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCompleted()");
        // notify listeners that check is completed
        if (uploadManagerWorker != null) {
            uploadManagerWorker.onResumableCompleted(uploadItem, response);
        }
    }

    protected void notifyListenerCompleted(InstantResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCompleted()");
        // notify listeners that check is completed
        if (uploadManagerWorker != null) {
            uploadManagerWorker.onInstantCompleted(uploadItem, response);
        }
    }

    protected void notifyListenerCompleted(PollResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCompleted()");
        // notify listeners that check is completed
        if (uploadManagerWorker != null) {
            uploadManagerWorker.onPollCompleted(uploadItem, response);
        }
    }

    protected void notifyListenerOnProgressUpdate(int chunkNumber, int numChunks) {
        MFConfiguration.getStaticMFLogger().v(TAG, "notifyListenerOnProgressUpdate()");
        // notify listeners of progress update
        if (uploadManagerWorker != null) {
            uploadManagerWorker.onProgressUpdate(uploadItem, chunkNumber, numChunks);
        }
    }

    protected void notifyListenerCancelled(CheckResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCancelled()");
        // notify listeners task cancelled
        if (uploadManagerWorker != null) {
            uploadManagerWorker.onCancelled(uploadItem, response);
        }
    }

    protected void notifyListenerCancelled(InstantResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCancelled()");
        // notify listeners task cancelled
        if (uploadManagerWorker != null) {
            uploadManagerWorker.onCancelled(uploadItem, response);
        }
    }

    protected void notifyListenerCancelled(ResumableResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCancelled()");
        // notify listeners task cancelled
        if (uploadManagerWorker != null) {
            uploadManagerWorker.onCancelled(uploadItem, response);
        }
    }

    protected void notifyListenerCancelled(PollResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerCancelled()");
        // notify listeners task cancelled
        if (uploadManagerWorker != null) {
            uploadManagerWorker.onCancelled(uploadItem, response);
        }
    }

    protected void notifyListenerException(Exception e) {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerException()");
        //notify listeners that there has been an exception
        if (uploadManagerWorker != null) {
            uploadManagerWorker.onProcessException(uploadItem, e);
        }
    }

    protected void notifyListenerLostConnection() {
        MFConfiguration.getStaticMFLogger().v(TAG, " notifyListenerLostConnection()");
        // notify listeners that connection was lost
        if (uploadManagerWorker != null) {
            uploadManagerWorker.onLostConnection(uploadItem);
        }
    }
}
