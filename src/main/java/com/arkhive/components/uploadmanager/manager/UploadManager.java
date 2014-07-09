package com.arkhive.components.uploadmanager.manager;

import com.arkhive.components.core.MediaFire;
import com.arkhive.components.uploadmanager.interfaces.UploadListener;
import com.arkhive.components.uploadmanager.process.CheckProcess;
import com.arkhive.components.uploadmanager.uploaditem.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

/**
 * UploadManager moves UploadItems from a Collection into Threads.
 * Number of threads that will be started is limited to the maximumThreadCount (default = 5)
 *
 * @author
 */
public class UploadManager extends UploadManagerWorker {
    private final Logger logger = LoggerFactory.getLogger(UploadManager.class);
    private UploadListener uiListener;

    /**
     * Constructor that takes a SessionManager, HttpInterface, and a maximum thread count.
     *
     * @param mediaFire     The SessionManager to use for API operations.
     * @param maximumThreadCount The maximum number of threads to use for uploading.
     */
    public UploadManager(MediaFire mediaFire, int maximumThreadCount, int maximumUploadAttempts) {
        super(mediaFire, maximumUploadAttempts, maximumUploadAttempts);
    }

    public void setUploadListener(UploadListener uiListener) {
        logger.info("setUploadListener");
        this.uiListener = uiListener;
    }

    public int getAllItems() {
        logger.info("getAllItems()");
        return workQueue.size() + executor.getActiveCount();
    }

    public BlockingQueue<Runnable> getAllWaitingRunnables() {
        logger.info("getAllWaitingRunnables()");
        return workQueue;
    }

    public void clearUploadQueue() {
        logger.info("clearUploadQueue()");
        boolean isPaused = isPaused();
        if (!isPaused) {
            pause();
        }
        executor.purge();
        if (!isPaused) {
            resume();
        }
    }

    /**
     * adds an UploadItem to the backlog queue.
     * If the UploadItem already exists in the backlog queue then we do not add the item.
     *
     * @param uploadItem The UploadItem to add to the backlog queue.
     */
    public void addUploadRequest(UploadItem uploadItem) {
        logger.info(" addUploadRequest()");
        //don't add the item to the backlog queue if it is null or the path is null
        if (uploadItem == null) {
            logger.info(" one or more required parameters are invalid, not adding item to queue");
            return;
        }

        if (uploadItem.getFileData() == null
                || uploadItem.getFileData().getFilePath() == null
                || uploadItem.getFileData().getFilePath().isEmpty()
                || uploadItem.getFileData().getFileHash().isEmpty()
                || uploadItem.getFileData().getFileSize() == 0) {
            logger.info(" one or more required parameters are invalid, not adding item to queue");
            return;
        }

        if (uploadItem.getUploadAttemptCount() < MAX_UPLOAD_ATTEMPTS) {
            CheckProcess process = new CheckProcess(mediaFire, this, uploadItem);
            executor.execute(process);
        }
    }

    protected void notifyUploadListenerStarted(UploadItem uploadItem) {
        logger.info(" notifyUploadListenerStarted()");
        if (uiListener != null) {
            uiListener.onStarted(uploadItem);
        }
    }

    protected void notifyUploadListenerCompleted(UploadItem uploadItem) {
        logger.info(" notifyUploadListenerCompleted()");
        if (uiListener != null) {
            uiListener.onCompleted(uploadItem);
        }
    }

    protected void notifyUploadListenerOnProgressUpdate(UploadItem uploadItem, int chunkNumber, int numChunks) {
        logger.info(" notifyUploadListenerOnProgressUpdate()");
        if (uiListener != null) {
            uiListener.onProgressUpdate(uploadItem, chunkNumber, numChunks);
        }
    }

    protected void notifyUploadListenerCancelled(UploadItem uploadItem) {
        logger.info("notifyUploadListenerCancelled()");
        if (uiListener != null) {
            uiListener.onCancelled(uploadItem);
        }
    }
}
