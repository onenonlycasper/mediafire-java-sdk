package com.mediafire.uploader.manager;

import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.token.MFTokenFarm;
import com.mediafire.uploader.interfaces.UploadListener;
import com.mediafire.uploader.process.CheckProcess;
import com.mediafire.uploader.uploaditem.UploadItem;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class UploadManager extends UploadManagerWorker {
    private static final String TAG = UploadManager.class.getCanonicalName();
    private UploadListener uiListener;
    private Logger logger;

    public UploadManager(MFTokenFarm mfTokenFarm, int maximumThreadCount, int maximumUploadAttempts) {
        super(mfTokenFarm, maximumUploadAttempts, maximumUploadAttempts);
    }

    public void setUploadListener(UploadListener uiListener) {
        MFConfiguration.getStaticMFLogger().v(TAG, "setUploadListener");
        this.uiListener = uiListener;
    }

    public int getAllItems() {
        MFConfiguration.getStaticMFLogger().v(TAG, "getAllItems()");
        return workQueue.size() + executor.getActiveCount();
    }

    public BlockingQueue<Runnable> getAllWaitingRunnables() {
        MFConfiguration.getStaticMFLogger().v(TAG, "getAllWaitingRunnables()");
        return workQueue;
    }

    public void clearUploadQueue() {
        MFConfiguration.getStaticMFLogger().v(TAG, "clearUploadQueue()");
        boolean isPaused = isPaused();
        if (!isPaused) {
            pause();
        }
        executor.purge();
        if (!isPaused) {
            resume();
        }
    }

    public void addUploadRequest(UploadItem uploadItem) {
        MFConfiguration.getStaticMFLogger().v(TAG, "addUploadRequest()");
        //don't add the item to the backlog queue if it is null or the path is null
        if (uploadItem == null) {
            MFConfiguration.getStaticMFLogger().v(TAG, "one or more required parameters are invalid, not adding item to queue");
            return;
        }

        if (uploadItem.getFileData() == null
                || uploadItem.getFileData().getFilePath() == null
                || uploadItem.getFileData().getFilePath().isEmpty()
                || uploadItem.getFileData().getFileHash().isEmpty()
                || uploadItem.getFileData().getFileSize() == 0) {
            MFConfiguration.getStaticMFLogger().v(TAG, "one or more required parameters are invalid, not adding item to queue");
            return;
        }

        if (uploadItem.getUploadAttemptCount() < MAX_UPLOAD_ATTEMPTS) {
            CheckProcess process = new CheckProcess(mfTokenFarm, this, uploadItem);
            executor.execute(process);
        }
    }

    protected void notifyUploadListenerStarted(UploadItem uploadItem) {
        MFConfiguration.getStaticMFLogger().v(TAG, "notifyUploadListenerStarted()");
        if (uiListener != null) {
            uiListener.onStarted(uploadItem);
        }
    }

    protected void notifyUploadListenerCompleted(UploadItem uploadItem) {
        MFConfiguration.getStaticMFLogger().v(TAG, "notifyUploadListenerCompleted()");
        if (uiListener != null) {
            uiListener.onCompleted(uploadItem);
        }
    }

    protected void notifyUploadListenerOnProgressUpdate(UploadItem uploadItem, int chunkNumber, int numChunks) {
        MFConfiguration.getStaticMFLogger().v(TAG, "notifyUploadListenerOnProgressUpdate()");
        if (uiListener != null) {
            uiListener.onProgressUpdate(uploadItem, chunkNumber, numChunks);
        }
    }

    protected void notifyUploadListenerCancelled(UploadItem uploadItem) {
        MFConfiguration.getStaticMFLogger().v(TAG, "notifyUploadListenerCancelled()");
        if (uiListener != null) {
            uiListener.onCancelled(uploadItem);
        }
    }
}
