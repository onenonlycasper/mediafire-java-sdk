package com.arkhive.components.uploadmanager.manager;

import com.arkhive.components.api.ApiResponse;
import com.arkhive.components.api.upload.errors.PollFileErrorCode;
import com.arkhive.components.api.upload.errors.PollResultCode;
import com.arkhive.components.api.upload.errors.PollStatusCode;
import com.arkhive.components.uploadmanager.PausableThreadPoolExecutor;
import com.arkhive.components.uploadmanager.listeners.UploadListenerDatabase;
import com.arkhive.components.uploadmanager.listeners.UploadListenerManager;
import com.arkhive.components.uploadmanager.listeners.UploadListenerUI;
import com.arkhive.components.uploadmanager.process.CheckProcess;
import com.arkhive.components.uploadmanager.process.InstantProcess;
import com.arkhive.components.uploadmanager.process.PollProcess;
import com.arkhive.components.uploadmanager.process.ResumableProcess;
import com.arkhive.components.api.upload.responses.CheckResponse;
import com.arkhive.components.api.upload.responses.PollResponse;
import com.arkhive.components.sessionmanager.SessionManager;
import com.arkhive.components.uploadmanager.uploaditem.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * UploadManager moves UploadItems from a Collection into Threads.
 * Number of threads that will be started is limited to the maximumThreadCount (default = 5)
 *
 * @author Chris Najar
 */
public class UploadManager implements UploadListenerManager {
    private static final String TAG = UploadManager.class.getSimpleName();
    private static final int MAX_CHECK_COUNT = 20;
    private UploadListenerDatabase dbListener;
    private UploadListenerUI uiListener;
    private final PausableThreadPoolExecutor executor;
    private final BlockingQueue<Runnable> workQueue;
    private final SessionManager sessionManager;

    private final Logger logger = LoggerFactory.getLogger(UploadManager.class);

    /**
     * Constructor that takes a SessionManager, HttpInterface, and a maximum thread count.
     *
     * @param sessionManager  The SessionManager to use for API operations.
     * @param maximumThreadCount  The maximum number of threads to use for uploading.
     */
    public UploadManager(SessionManager sessionManager, int maximumThreadCount) {
        this.sessionManager = sessionManager; // set session manager
        workQueue = new LinkedBlockingQueue<Runnable>();
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        executor = new PausableThreadPoolExecutor( // establish thread pool executor
                        maximumThreadCount,
                        maximumThreadCount,
                        5000,
                        TimeUnit.MILLISECONDS,
                        workQueue,
                        threadFactory);
    }

    /**
     * returns the listener that is set as the database listener.
     * @return the UploadListenerDatabase callback.
     */
    public UploadListenerDatabase getDatabaseListener() { return dbListener; }

    /**
     * returns the listener that is set as the UI listener.
     * @return the UploadListenerUI callback.
     */
    public UploadListenerUI getUiListener() { return uiListener; }

    /**
     * sets the content provider listener.
     * @param dbListener database listener to use.
     */
    public void setDatabaseListener(UploadListenerDatabase dbListener) { this.dbListener = dbListener; }

    /**
     * adds a UI listener.
     * @param uiListener - ui listener to use.
     */
    public void setUiListener(UploadListenerUI uiListener) { this.uiListener = uiListener; }

    /**
     * returns all items in the executor thread pool.
     *
     * @return an int representing both active and waiting threads.
     */
    public int getAllItems() {
        return workQueue.size() + executor.getActiveCount();
    }

    public BlockingQueue<Runnable> getAllWaitingRunnables() {
        return workQueue;
    }

    /**
     * removes all items from the executor thread pool and attempts to cancel all threads currently running.
     */
    public void clearUploadQueue() {
        executor.purge();
    }

    /**
     * adds an UploadItem to the backlog queue.
     * If the UploadItem already exists in the backlog queue then we do not add the item.
     *
     * @param uploadItem The UploadItem to add to the backlog queue.
     */
    public void addUploadRequest(UploadItem uploadItem) {
        logger.info("addUploadRequest()");
        //don't add the item to the backlog queue if it is null or the path is null
        if (uploadItem == null
                || uploadItem.getFileData() == null
                || uploadItem.getFileData().getFilePath() == null
                || uploadItem.getFileData().getFilePath().isEmpty()
                || uploadItem.getFileData().getFileHash().isEmpty()
                || uploadItem.getFileData().getFileSize() == 0) {
            logger.info("one or more required parameters are invalid, not adding item to queue");
            return;
        }
        CheckProcess process = new CheckProcess(sessionManager, this, uploadItem);
        executor.execute(process);
    }

    /**
     * Pause moving backlog items to the thread queue.
     */
    public void pause() {
        logger.info("pause()");
        if (executor != null) {
            executor.pause();
        }
    }

    /**
     * Resume moving backlog items to the thread queue.
     */
    public void resume() {
        logger.info("resume()");
        if (executor != null) {
            executor.resume();
        }
    }

    /**
     * Returns whether or not UploadManager is paused or not.
     *
     * @return true if paused (not moving backlog to queue), false otherwise.
     */
    public boolean isPaused() {
        logger.info("isPaused()");
        return executor.isPaused();
    }

    private void notifyListenersStarted(UploadItem uploadItem) {
        logger.info("notifyListenersStarted()");
        if (uiListener != null) {
            uiListener.onStarted(uploadItem);
        }
        if (dbListener != null) {
            dbListener.onStarted(uploadItem);
        }
    }

    private void notifyListenersCompleted(UploadItem uploadItem) {
        logger.info("notifyListenersCompleted()");
        if (uiListener != null) {
            uiListener.onCompleted(uploadItem);
        }
        if (dbListener != null) {
            dbListener.onCompleted(uploadItem);
        }
    }

    private void notifyListenersOnProgressUpdate(UploadItem uploadItem, int chunkNumber, int numChunks) {
        logger.info("notifyListenersOnProgressUpdate()");
        if (uiListener != null) {
            uiListener.onProgressUpdate(uploadItem, chunkNumber, numChunks);
        }
    }

    private void notifyListenersCancelled(UploadItem uploadItem) {
        if (dbListener != null) {
            dbListener.onCancelled(uploadItem);
        }
        if (uiListener != null) {
            uiListener.onCancelled(uploadItem);
        }
    }

    @Override
    public void onStartedUploadProcess(UploadItem uploadItem) {
        logger.info("onStartedUploadProcess()");
        notifyListenersStarted(uploadItem);
    }

    @Override
    public void onCheckCompleted(UploadItem uploadItem, CheckResponse checkResponse) {
        logger.info("onCheckCompleted()");
        //as a failsafe, an upload item cannot continue after upload/check.php if it has gone through the process 20x
        //20x is high, but it should never happen and will allow for more information gathering.
        if (uploadItem.getCheckCount() > MAX_CHECK_COUNT) {
            notifyListenersCancelled(uploadItem);
            return;
        }

        if (checkResponse.getStorageLimitExceeded()) {
            logger.info("--storage limit is exceeded");
            storageLimitExceeded(uploadItem);
        } else {
            logger.info("--storage limit not exceeded");
            if (checkResponse.getHashExists()) { //hash does exist for the file
                hashExists(uploadItem, checkResponse);
            } else { // hash does not exist. call resumable.
                hashDoesNotExist(uploadItem, checkResponse);
            }
        }
    }

    @Override
    public void onProgressUpdate(UploadItem uploadItem, int chunkNumber, int numChunks) {
        logger.info("onProgressUpdate()");
        notifyListenersOnProgressUpdate(uploadItem, chunkNumber, numChunks);
    }

    private void storageLimitExceeded(UploadItem uploadItem) {
        logger.info("storageLimitExceeded()");
        notifyListenersCancelled(uploadItem);
    }

    private void hashExists(UploadItem uploadItem, CheckResponse checkResponse) {
        logger.info("hashExists()");
        if (!checkResponse.getInAccount()) { // hash which exists is not in the account
            hashNotInAccount(uploadItem);
        } else { // hash exists and is in the account
            hashInAccount(uploadItem, checkResponse);
        }
    }

    private void hashNotInAccount(UploadItem uploadItem) {
        logger.info("hashNotInAccount()");
        InstantProcess process = new InstantProcess(sessionManager, this, uploadItem);
        Thread thread = new Thread(process);
        thread.start();
    }

    private void hashInAccount(UploadItem uploadItem, CheckResponse checkResponse) {
        logger.info("hashInAccount()");
        boolean inFolder = checkResponse.getInFolder();
        InstantProcess process = new InstantProcess(sessionManager, this, uploadItem);
        logger.info("--ACTIONONINACCOUNT: " + uploadItem.getUploadOptions().getActionOnInAccount());
        switch (uploadItem.getUploadOptions().getActionOnInAccount()) {
            case UPLOAD_ALWAYS:
                logger.info("--ACTION IN ACCOUNT VIA SWITCH STMT case UPLOAD_ALWAYS");
                executor.execute(process);
                break;
            case UPLOAD_IF_NOT_IN_FOLDER:
                logger.info("--ACTION IN ACCOUNT VIA SWITCH STMT case UPLOAD_ALWAYS");
                if (!inFolder) {
                    logger.info("--NOT IN FOLDER SO UPLOADING");
                    executor.execute(process);
                } else {
                    logger.info("--IN FOLDER SO NOT UPLOADING");
                    notifyListenersCompleted(uploadItem);
                }
                break;
            case DO_NOT_UPLOAD:
            default:
                logger.info("--ACTION IN ACCOUNT VIA SWITCH STMT case do_not_upload/default");
                notifyListenersCompleted(uploadItem);
                break;
        }
    }

    private void hashDoesNotExist(UploadItem uploadItem, CheckResponse checkResponse) {
        logger.info("hashDoesNotExist()");
        if (checkResponse.getResumableUpload().getUnitSize() == 0) {
            logger.info("--unit size received from unit_size was 0. cancelling");
            notifyListenersCancelled(uploadItem);
            return;
        }

        if (checkResponse.getResumableUpload().getNumberOfUnits() == 0) {
            logger.info("--number of units received from number_of_units was 0. cancelling");
            notifyListenersCancelled(uploadItem);
            return;
        }

        if (checkResponse.getResumableUpload().getAllUnitsReady() && !uploadItem.getPollUploadKey().isEmpty()) {
            logger.info("--all units ready and have a poll upload key");
            // all units are ready and we have the poll upload key. start polling.
            uploadItem.getChunkData().setNumberOfUnits(checkResponse.getResumableUpload().getNumberOfUnits());
            uploadItem.getChunkData().setUnitSize(checkResponse.getResumableUpload().getUnitSize());
            PollProcess process = new PollProcess(sessionManager, this, uploadItem);
            executor.execute(process);
        } else {
            logger.info("--all units not ready or do not have poll upload key");
            // either we don't have the poll upload key or all units are not ready
            uploadItem.getChunkData().setNumberOfUnits(checkResponse.getResumableUpload().getNumberOfUnits());
            uploadItem.getChunkData().setUnitSize(checkResponse.getResumableUpload().getUnitSize());
            ResumableProcess process = new ResumableProcess(sessionManager, this, uploadItem);
            executor.execute(process);
        }
    }

    @Override
    public void onInstantCompleted(UploadItem uploadItem) {
        logger.info("onInstantCompleted()");
        notifyListenersCompleted(uploadItem);
    }

    @Override
    public void onResumableCompleted(UploadItem uploadItem) {
        logger.info("onResumableCompleted()");
        // if the item status isn't cancelled or paused then call upload/check to make sure all units are ready
        CheckProcess process = new CheckProcess(sessionManager, this, uploadItem);
        executor.execute(process);
    }

    @Override
    public void onPollCompleted(UploadItem uploadItem, PollResponse pollResponse) {
        logger.info("onPollCompleted()");
        // if this method is called then filerror and result codes are fine, but we may not have received status 99 so
        // check status code and then possibly senditem to the backlog queue.
        PollResponse.DoUpload doUpload = pollResponse.getDoUpload();
        PollStatusCode pollStatusCode = doUpload.getStatusCode();
        PollResultCode pollResultCode = doUpload.getResultCode();
        PollFileErrorCode pollFileErrorCode = doUpload.getFileErrorCode();

        if (pollStatusCode != PollStatusCode.NO_MORE_REQUESTS_FOR_THIS_KEY
                && pollResultCode == PollResultCode.SUCCESS
                && pollFileErrorCode == PollFileErrorCode.NO_ERROR) {
            logger.info("status code: " + pollResponse.getDoUpload().getStatusCode().toString() + " need to try again");
            addUploadRequest(uploadItem);
        } else {
            notifyListenersCompleted(uploadItem);
        }
    }

    @Override
    public void onProcessException(UploadItem uploadItem, Exception exception) {
        logger.info("onProcessException()");
        logger.error(TAG + "received exception: " + exception);
        notifyListenersCancelled(uploadItem);
    }

    @Override
    public void onLostConnection(UploadItem uploadItem) {
        logger.info("onLostConnection()");
        notifyListenersCancelled(uploadItem);
        //pause upload manager
        pause();
        addUploadRequest(uploadItem);
    }

    @Override
    public void onCancelled(UploadItem uploadItem, ApiResponse apiResponse) {
        logger.info("onCancelled()");
        notifyListenersCancelled(uploadItem);
        // if there is an api error then add this item to the backlog queue and decrease current thread count
        if (apiResponse.hasError()) {
            addUploadRequest(uploadItem);
        }
    }
}
