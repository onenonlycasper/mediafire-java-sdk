package com.arkhive.components.uploadmanager.manager;

import com.arkhive.components.core.MediaFire;
import com.arkhive.components.core.module_api.codes.PollFileErrorCode;
import com.arkhive.components.core.module_api.codes.PollResultCode;
import com.arkhive.components.core.module_api.codes.PollStatusCode;
import com.arkhive.components.core.module_api.responses.*;
import com.arkhive.components.core.module_errors.ErrorTracker;
import com.arkhive.components.uploadmanager.PausableThreadPoolExecutor;
import com.arkhive.components.uploadmanager.interfaces.Pausable;
import com.arkhive.components.uploadmanager.interfaces.UploadListenerManager;
import com.arkhive.components.uploadmanager.process.CheckProcess;
import com.arkhive.components.uploadmanager.process.InstantProcess;
import com.arkhive.components.uploadmanager.process.PollProcess;
import com.arkhive.components.uploadmanager.process.ResumableProcess;
import com.arkhive.components.uploadmanager.uploaditem.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Chris Najar on 7/8/2014.
 */
public abstract class UploadManagerWorker implements UploadListenerManager, Pausable {
    private final Logger logger = LoggerFactory.getLogger(UploadManagerWorker.class);
    protected final int MAX_UPLOAD_ATTEMPTS;
    protected final MediaFire mediaFire;
    protected final PausableThreadPoolExecutor executor;
    protected final LinkedBlockingQueue<Runnable> workQueue;
    protected ErrorTracker errorTracker;

    public UploadManagerWorker(MediaFire mediaFire, int maxUploadAttempts, int maxThreadQueue) {
        this.mediaFire = mediaFire;
        this.workQueue = new LinkedBlockingQueue<Runnable>();
        executor = new PausableThreadPoolExecutor(maxThreadQueue, workQueue);
        this.MAX_UPLOAD_ATTEMPTS = maxUploadAttempts;
    }

    public abstract void addUploadRequest(UploadItem uploadItem);
    protected abstract void notifyUploadListenerStarted(UploadItem uploadItem);
    protected abstract void notifyUploadListenerCompleted(UploadItem uploadItem);
    protected abstract void notifyUploadListenerOnProgressUpdate(UploadItem uploadItem, int chunkNumber, int numChunks);
    protected abstract void notifyUploadListenerCancelled(UploadItem uploadItem);

    public void setErrorTracker(ErrorTracker errorTracker) {
        this.errorTracker = errorTracker;
    }

    @Override
    public void onStartedUploadProcess(UploadItem uploadItem) {
        logger.info("onStartedUploadProcess()");
        notifyUploadListenerStarted(uploadItem);
    }

    @Override
    public void onCheckCompleted(UploadItem uploadItem, UploadCheckResponse checkResponse) {
        logger.info("onCheckCompleted()");
        //as a failsafe, an upload item cannot continue after upload/check.php if it has gone through the process 20x
        //20x is high, but it should never happen and will allow for more information gathering.
        if (uploadItem.getUploadAttemptCount() > MAX_UPLOAD_ATTEMPTS || uploadItem.isCancelled()) {
            notifyUploadListenerCancelled(uploadItem);
            return;
        }

        if (checkResponse.getStorageLimitExceeded()) {
            logger.info("storage limit is exceeded");
            storageLimitExceeded(uploadItem);
        } else if (checkResponse.getResumableUpload().areAllUnitsReady() && !uploadItem.getPollUploadKey().isEmpty()) {
            // all units are ready and we have the poll upload key. start polling.
            PollProcess process = new PollProcess(mediaFire, this, uploadItem);
            executor.execute(process);
        } else {
            if (checkResponse.doesHashExists()) { //hash does exist for the file
                hashExists(uploadItem, checkResponse);
            } else { // hash does not exist. call resumable.
                hashDoesNotExist(uploadItem, checkResponse);
            }
        }
    }

    @Override
    public void onProgressUpdate(UploadItem uploadItem, int chunkNumber, int numChunks) {
        logger.info("onProgressUpdate()");
        notifyUploadListenerOnProgressUpdate(uploadItem, chunkNumber, numChunks);
    }

    private void storageLimitExceeded(UploadItem uploadItem) {
        logger.info("storageLimitExceeded()");
        notifyUploadListenerCancelled(uploadItem);
    }

    private void hashExists(UploadItem uploadItem, UploadCheckResponse checkResponse) {
        logger.info("hashExists()");
        if (!checkResponse.isInAccount()) { // hash which exists is not in the account
            hashNotInAccount(uploadItem);
        } else { // hash exists and is in the account
            hashInAccount(uploadItem, checkResponse);
        }
    }

    private void hashNotInAccount(UploadItem uploadItem) {
        logger.info("hashNotInAccount()");
        InstantProcess process = new InstantProcess(mediaFire, this, uploadItem);
        Thread thread = new Thread(process);
        thread.start();
    }

    private void hashInAccount(UploadItem uploadItem, UploadCheckResponse checkResponse) {
        logger.info("hashInAccount()");
        boolean inFolder = checkResponse.isInFolder();
        InstantProcess process = new InstantProcess(mediaFire, this, uploadItem);
        logger.info("ActionOnInAccount: "+ uploadItem.getUploadOptions().getActionOnInAccount().toString());
        switch (uploadItem.getUploadOptions().getActionOnInAccount()) {
            case UPLOAD_ALWAYS:
                logger.info("uploading...");
                executor.execute(process);
                break;
            case UPLOAD_IF_NOT_IN_FOLDER:
                logger.info("uploading if not in folder.");
                if (!inFolder) {
                    logger.info("uploading...");
                    executor.execute(process);
                } else {
                    logger.info("already in folder, not uploading...");
                    notifyUploadListenerCompleted(uploadItem);
                }
                break;
            case DO_NOT_UPLOAD:
            default:
                logger.info("not uploading...");
                notifyUploadListenerCompleted(uploadItem);
                break;
        }
    }

    private void hashDoesNotExist(UploadItem uploadItem, UploadCheckResponse checkResponse) {
        logger.info("hashDoesNotExist()");
        if (checkResponse.getResumableUpload().getUnitSize() == 0) {
            logger.info("unit size received from unit_size was 0. cancelling");
            notifyUploadListenerCancelled(uploadItem);
            return;
        }

        if (checkResponse.getResumableUpload().getNumberOfUnits() == 0) {
            logger.info("number of units received from number_of_units was 0. cancelling");
            notifyUploadListenerCancelled(uploadItem);
            return;
        }

        if (checkResponse.getResumableUpload().areAllUnitsReady() && !uploadItem.getPollUploadKey().isEmpty()) {
            logger.info("all units ready and have a poll upload key");
            // all units are ready and we have the poll upload key. start polling.
            PollProcess process = new PollProcess(mediaFire, this, uploadItem);
            executor.execute(process);
        } else {
            logger.info("all units not ready or do not have poll upload key");
            // either we don't have the poll upload key or all units are not ready
            ResumableProcess process = new ResumableProcess(mediaFire, this, uploadItem);
            executor.execute(process);
        }
    }

    @Override
    public void onInstantCompleted(UploadItem uploadItem, UploadInstantResponse response) {
        logger.info("onInstantCompleted()");
        notifyUploadListenerCompleted(uploadItem);
    }

    @Override
    public void onResumableCompleted(UploadItem uploadItem, UploadResumableResponse response) {
        logger.info("onResumableCompleted()");
        if (response != null &&
                response.getResumableUpload().areAllUnitsReady() &&
                !response.getDoUpload().getPollUploadKey().isEmpty()) {
            PollProcess process = new PollProcess(mediaFire, this, uploadItem);
            executor.execute(process);
        } else {
            CheckProcess process = new CheckProcess(mediaFire, this, uploadItem);
            executor.execute(process);
        }
    }

    @Override
    public void onPollCompleted(UploadItem uploadItem, UploadPollResponse pollResponse) {
        logger.info("onPollCompleted()");
        // if this method is called then filerror and result codes are fine, but we may not have received status 99 so
        // check status code and then possibly senditem to the backlog queue.
        UploadPollResponse.DoUpload doUpload = pollResponse.getDoUpload();
        PollStatusCode pollStatusCode = doUpload.getStatusCode();
        PollResultCode pollResultCode = doUpload.getResultCode();
        PollFileErrorCode pollFileErrorCode = doUpload.getFileErrorCode();

        if (pollStatusCode != PollStatusCode.NO_MORE_REQUESTS_FOR_THIS_KEY && pollResultCode == PollResultCode.SUCCESS && pollFileErrorCode == PollFileErrorCode.NO_ERROR) {
            logger.info("status code: "+ pollResponse.getDoUpload().getStatusCode().toString() + " need to try again");
            notifyUploadListenerCancelled(uploadItem);
            addUploadRequest(uploadItem);
        } else {
            notifyUploadListenerCompleted(uploadItem);
        }
    }

    @Override
    public void onProcessException(UploadItem uploadItem, Exception exception) {
        logger.info("onProcessException()");
        logger.info("received exception: "+ exception);
        if (errorTracker != null) {
            errorTracker.trackError(UploadManagerWorker.class.getSimpleName(), exception);
        }
        notifyUploadListenerCancelled(uploadItem);
    }

    @Override
    public void onLostConnection(UploadItem uploadItem) {
        logger.info("onLostConnection()");
        notifyUploadListenerCancelled(uploadItem);
        //pause upload manager
        pause();
        addUploadRequest(uploadItem);
    }

    @Override
    public void onCancelled(UploadItem uploadItem, ApiResponse apiResponse) {
        logger.info("onCancelled()");
        notifyUploadListenerCancelled(uploadItem);
        // if there is an api error then re-add upload request.
        if (apiResponse != null && apiResponse.hasError()) {
            addUploadRequest(uploadItem);
        }
    }

    /**
     * Pausable interface
     */
    public void pause() {
        logger.info("pause()");
        executor.pause();
    }

    public void resume() {
        logger.info("resume()");
        executor.resume();
    }

    public boolean isPaused() {
        logger.info("isPaused()");
        return executor.isPaused();
    }
}
