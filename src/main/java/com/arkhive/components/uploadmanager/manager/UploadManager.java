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
    private UploadListenerDatabase dbListener;
    private UploadListenerUI uiListener;
    private UploadListenerManager mgrListener;
    public PausableThreadPoolExecutor executor;
    private BlockingQueue<Runnable> workQueue;
    private ThreadFactory threadFactory;
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
        mgrListener = this;
        workQueue = new LinkedBlockingQueue<Runnable>();
        threadFactory = Executors.defaultThreadFactory();
        executor =
                new PausableThreadPoolExecutor( // establish thread pool executor
                        maximumThreadCount,
                        maximumThreadCount,
                        5000,
                        TimeUnit.MILLISECONDS,
                        workQueue,
                        threadFactory);
    }

    /*============================
     * public getters
     *============================*/
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
     * returns the listener that is set as the upload manager listener.
     * @return the UploadListenerManager (this).
     */
    public UploadListenerManager getUploadManagerListener() { return mgrListener; }

    /*============================
     * public setters
     *============================*/
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
     * sets the upload manager listener.
     * @param mgrListener - upload manager listener to use (this).
     */
    private void setUploadManagerListener(UploadListenerManager mgrListener) { this.mgrListener = mgrListener; }

    /*============================
     * public methods
     *============================*/

    /**
     * returns all items in the executor thread pool.
     *
     * @return an int representing both active and waiting threads.
     */
    public int getAllItems() {
        int workQueueSize = executor.getPoolSize();
        int executorActiveCount = executor.getActiveCount();
        return workQueueSize + executorActiveCount;
    }

    public BlockingQueue<Runnable> getAllWaitingRunnables() {
        return workQueue;
    }

    /**
     * removes all items from the executor thread pool and attempts to cancel all threads currently running.
     */
    public void clearUploadQueue() {
        workQueue.clear();
        executor.shutdownNow();
    }

    /**
     * adds an UploadItem to the backlog queue.
     * If the UploadItem already exists in the backlog queue then we do not add the item.
     *
     * @param uploadItem The UploadItem to add to the backlog queue.
     */
    public void addUploadRequest(UploadItem uploadItem) {
        logger.info(TAG + "addUploadRequest()");
        //don't add the item to the backlog queue if it is null or the path is null
        if (uploadItem == null || uploadItem.getPath() == null) {
            logger.info(TAG + "--UploadItem is null");
            return;
        }
        CheckProcess process = new CheckProcess(sessionManager, this, uploadItem);
        executor.execute(process);
    }

    /**
     * Pause moving backlog items to the thread queue.
     */
    public void pause() {
        logger.info(TAG + "pause()");
        if (executor != null) {
            executor.pause();
        }
    }

    /**
     * Resume moving backlog items to the thread queue.
     */
    public void resume() {
        logger.info(TAG + "resume()");
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
        logger.info(TAG + "isPaused()");
        if (executor != null) {
            return executor.isPaused();
        } else {
            return true;
        }
    }

    /*============================
     * interface methods
     *============================*/

    @Override
    public void onCheckCompleted(UploadItem uploadItem, CheckResponse response) {
        logger.info(TAG + "onCheckCompleted()");

        if (!response.getStorageLimitExceeded()) { //storage limit not exceeded
            logger.info("--storage limit not exceeded");
            if (response.getHashExists()) { //hash does exist for the file
                logger.info("--hash exists");
                if (!response.getInAccount()) { // hash which exists is not in the account
                    logger.info("--hash not in account");
                    InstantProcess process = new InstantProcess(sessionManager, this, uploadItem);
                    Thread thread = new Thread(process);
                    thread.start();
                } else { // hash exists and is in the account
                    logger.info("--hash in account");
                    boolean inFolder = response.getInFolder();
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
                                logger.info("--NOT IN FOLDER SO NOT UPLOADING");
                            }
                            break;
                        case DO_NOT_UPLOAD:
                        default:
                            logger.info("--ACTION IN ACCOUNT VIA SWITCH STMT case do_not_upload/default");
                            if (uiListener != null) {
                                uiListener.onCompleted(uploadItem);
                            }
                            if (dbListener != null) {
                                dbListener.onCompleted(uploadItem);
                            }
                            break;
                    }
                }
            } else { // hash does not exist. call resumable.
                logger.info("--hash does not exist");
                if (response.getResumableUpload().getAllUnitsReady() && !uploadItem.getPollUploadKey().isEmpty()) {
                    logger.info("--all units ready and have a poll upload key");
                    // all units are ready and we have the poll upload key. start polling.
                    uploadItem.getChunkData().setNumberOfUnits(response.getResumableUpload().getNumberOfUnits());
                    uploadItem.getChunkData().setUnitSize(response.getResumableUpload().getUnitSize());
                    PollProcess process = new PollProcess(sessionManager, this, uploadItem);
                    executor.execute(process);
                } else {
                    logger.info("--all units not ready or do not have poll upload key");
                    // either we don't have the poll upload key or all units are not ready
                    uploadItem.getChunkData().setNumberOfUnits(response.getResumableUpload().getNumberOfUnits());
                    uploadItem.getChunkData().setUnitSize(response.getResumableUpload().getUnitSize());
                    ResumableProcess process = new ResumableProcess(sessionManager, this, uploadItem);
                    executor.execute(process);
                }
            }
        } else { //user exceeded storage space.
            logger.info("--storage limit is exceeded");
        }
    }

    @Override
    public void onInstantCompleted(UploadItem uploadItem) {
        logger.info(TAG + "onInstantCompleted()");
    }

    @Override
    public void onResumableCompleted(UploadItem uploadItem) {
        logger.info(TAG + "onResumableCompleted()");
        // if the item status isn't cancelled or paused then call upload/check to make sure all units are ready
        CheckProcess process = new CheckProcess(sessionManager, this, uploadItem);
        executor.execute(process);
    }

    @Override
    public void onPollCompleted(UploadItem uploadItem, PollResponse response) {
        logger.info(TAG + "onPollCompleted()");
        // if this method is called then filerror and result codes are fine,
        // but we may not have received status 99 so check status code and
        // then possibly senditem to the backlog queue.
        if (response.getDoUpload().getStatusCode() != PollStatusCode.NO_MORE_REQUESTS_FOR_THIS_KEY) {
            addUploadRequest(uploadItem);
        }
    }

    @Override
    public void onProcessException(UploadItem uploadItem, Exception exception) {
        logger.info(TAG + " onProcessException()");
        //decrease the thread count
        logger.error(TAG + "received exception: " + exception);
    }

    @Override
    public void onLostConnection(UploadItem uploadItem) {
        logger.info(TAG + "onLostConnection()");
        //pause upload manager
        pause();
        addUploadRequest(uploadItem);
    }

    @Override
    public void onCancelled(UploadItem uploadItem, ApiResponse response) {
        logger.info(TAG + "onCancelled");

        // if there is an api error then add this item to the backlog queue and decrease current thread count
        if (response.hasError()) {
            addUploadRequest(uploadItem);
            return;
        }

        // if the response does not have an api error, then onCancelled was called by PollProcess or ResumableProcess
        if (response instanceof PollResponse) {
            PollResponse castResponse = (PollResponse) response;
            // if poll upload called onCancelled() because it ran past its attempts, add this to the backlog queue
            // if poll upload called onCancelled() because of a file error code or a result error code then drop it from
            // the queue (via not adding it back to the queue)
            if (castResponse.getDoUpload().getFileErrorCode() == PollFileErrorCode.NO_ERROR &&
                    castResponse.getDoUpload().getResultCode() == PollResultCode.SUCCESS) {
                addUploadRequest(uploadItem);
            }
        }
    }
}
