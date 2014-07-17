package com.mediafire.uploader.manager;

import com.mediafire.sdk.api_responses.ApiResponse;
import com.mediafire.sdk.api_responses.upload.CheckResponse;
import com.mediafire.sdk.api_responses.upload.InstantResponse;
import com.mediafire.sdk.api_responses.upload.PollResponse;
import com.mediafire.sdk.api_responses.upload.ResumableResponse;
import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.config.MFLogger;
import com.mediafire.uploader.PausableThreadPoolExecutor;
import com.mediafire.uploader.interfaces.Pausable;
import com.mediafire.uploader.interfaces.UploadListenerManager;
import com.mediafire.uploader.process.CheckProcess;
import com.mediafire.uploader.process.InstantProcess;
import com.mediafire.uploader.process.PollProcess;
import com.mediafire.uploader.process.ResumableProcess;
import com.mediafire.uploader.uploaditem.UploadItem;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by  on 7/8/2014.
 */
public abstract class UploadManagerWorker implements UploadListenerManager, Pausable {
    private static final String TAG = UploadManagerWorker.class.getCanonicalName();
    protected final int MAX_UPLOAD_ATTEMPTS;
    protected final MediaFire mediaFire;
    protected final PausableThreadPoolExecutor executor;
    protected final LinkedBlockingQueue<Runnable> workQueue;
    protected MFLogger errorTracker;

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

    public void setErrorTracker(MFLogger errorTracker) {
        this.errorTracker = errorTracker;
    }

    @Override
    public void onStartedUploadProcess(UploadItem uploadItem) {
        MFConfiguration.getErrorTracker().i(TAG, "onStartedUploadProcess()");
        notifyUploadListenerStarted(uploadItem);
    }

    @Override
    public void onCheckCompleted(UploadItem uploadItem, CheckResponse checkResponse) {
        MFConfiguration.getErrorTracker().i(TAG, "onCheckCompleted()");
        //as a failsafe, an upload item cannot continue after upload/check.php if it has gone through the process 20x
        //20x is high, but it should never happen and will allow for more information gathering.
        if (uploadItem.getUploadAttemptCount() > MAX_UPLOAD_ATTEMPTS || uploadItem.isCancelled()) {
            notifyUploadListenerCancelled(uploadItem);
            return;
        }

        if (checkResponse.getStorageLimitExceeded()) {
            MFConfiguration.getErrorTracker().i(TAG, "storage limit is exceeded");
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
        MFConfiguration.getErrorTracker().i(TAG, "onProgressUpdate()");
        notifyUploadListenerOnProgressUpdate(uploadItem, chunkNumber, numChunks);
    }

    private void storageLimitExceeded(UploadItem uploadItem) {
        MFConfiguration.getErrorTracker().i(TAG, "storageLimitExceeded()");
        notifyUploadListenerCancelled(uploadItem);
    }

    private void hashExists(UploadItem uploadItem, UploadCheckResponse checkResponse) {
        MFConfiguration.getErrorTracker().i(TAG, "hashExists()");
        if (!checkResponse.isInAccount()) { // hash which exists is not in the account
            hashNotInAccount(uploadItem);
        } else { // hash exists and is in the account
            hashInAccount(uploadItem, checkResponse);
        }
    }

    private void hashNotInAccount(UploadItem uploadItem) {
        MFConfiguration.getErrorTracker().i(TAG, "hashNotInAccount()");
        InstantProcess process = new InstantProcess(mediaFire, this, uploadItem);
        Thread thread = new Thread(process);
        thread.start();
    }

    private void hashInAccount(UploadItem uploadItem, CheckResponse checkResponse) {
        MFConfiguration.getErrorTracker().i(TAG, "hashInAccount()");
        boolean inFolder = checkResponse.isInFolder();
        InstantProcess process = new InstantProcess(mediaFire, this, uploadItem);
        MFConfiguration.getErrorTracker().i(TAG, "ActionOnInAccount: " + uploadItem.getUploadOptions().getActionOnInAccount().toString());
        switch (uploadItem.getUploadOptions().getActionOnInAccount()) {
            case UPLOAD_ALWAYS:
                MFConfiguration.getErrorTracker().i(TAG, "uploading...");
                executor.execute(process);
                break;
            case UPLOAD_IF_NOT_IN_FOLDER:
                MFConfiguration.getErrorTracker().i(TAG, "uploading if not in folder.");
                if (!inFolder) {
                    MFConfiguration.getErrorTracker().i(TAG, "uploading...");
                    executor.execute(process);
                } else {
                    MFConfiguration.getErrorTracker().i(TAG, "already in folder, not uploading...");
                    notifyUploadListenerCompleted(uploadItem);
                }
                break;
            case DO_NOT_UPLOAD:
            default:
                MFConfiguration.getErrorTracker().i(TAG, "not uploading...");
                notifyUploadListenerCompleted(uploadItem);
                break;
        }
    }

    private void hashDoesNotExist(UploadItem uploadItem, CheckResponse checkResponse) {
        MFConfiguration.getErrorTracker().i(TAG, "hashDoesNotExist()");
        if (checkResponse.getResumableUpload().getUnitSize() == 0) {
            MFConfiguration.getErrorTracker().i(TAG, "unit size received from unit_size was 0. cancelling");
            notifyUploadListenerCancelled(uploadItem);
            return;
        }

        if (checkResponse.getResumableUpload().getNumberOfUnits() == 0) {
            MFConfiguration.getErrorTracker().i(TAG, "number of units received from number_of_units was 0. cancelling");
            notifyUploadListenerCancelled(uploadItem);
            return;
        }

        if (checkResponse.getResumableUpload().areAllUnitsReady() && !uploadItem.getPollUploadKey().isEmpty()) {
            MFConfiguration.getErrorTracker().i(TAG, "all units ready and have a poll upload key");
            // all units are ready and we have the poll upload key. start polling.
            PollProcess process = new PollProcess(mediaFire, this, uploadItem);
            executor.execute(process);
        } else {
            MFConfiguration.getErrorTracker().i(TAG, "all units not ready or do not have poll upload key");
            // either we don't have the poll upload key or all units are not ready
            ResumableProcess process = new ResumableProcess(mediaFire, this, uploadItem);
            executor.execute(process);
        }
    }

    @Override
    public void onInstantCompleted(UploadItem uploadItem, InstantResponse response) {
        MFConfiguration.getErrorTracker().i(TAG, "onInstantCompleted()");
        notifyUploadListenerCompleted(uploadItem);
    }

    @Override
    public void onResumableCompleted(UploadItem uploadItem, ResumableResponse response) {
        MFConfiguration.getErrorTracker().i(TAG, "onResumableCompleted()");
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
    public void onPollCompleted(UploadItem uploadItem, PollResponse pollResponse) {
        MFConfiguration.getErrorTracker().i(TAG, "onPollCompleted()");
        // if this method is called then filerror and result codes are fine, but we may not have received status 99 so
        // check status code and then possibly senditem to the backlog queue.
        PollResponse.DoUpload doUpload = pollResponse.getDoUpload();
        PollResponse.Status pollStatusCode = doUpload.getStatusCode();
        PollResponse.Result pollResultCode = doUpload.getResultCode();
        PollResponse.FileError pollFileErrorCode = doUpload.getFileErrorCode();

        if (pollStatusCode != PollResponse.Status.NO_MORE_REQUESTS_FOR_THIS_KEY && pollResultCode == PollResponse.Result.SUCCESS && pollFileErrorCode == PollResponse.FileError.NO_ERROR) {
            MFConfiguration.getErrorTracker().i(TAG, "status code: " + pollResponse.getDoUpload().getStatusCode().toString() + " need to try again");
            notifyUploadListenerCancelled(uploadItem);
            addUploadRequest(uploadItem);
        } else {
            notifyUploadListenerCompleted(uploadItem);
        }
    }

    @Override
    public void onProcessException(UploadItem uploadItem, Exception exception) {
        MFConfiguration.getErrorTracker().i(TAG, "onProcessException()");
        MFConfiguration.getErrorTracker().i(TAG, "received exception: " + exception);
        if (errorTracker != null) {
            errorTracker.e(UploadManagerWorker.class.getCanonicalName(), exception);
        }
        notifyUploadListenerCancelled(uploadItem);
    }

    @Override
    public void onLostConnection(UploadItem uploadItem) {
        MFConfiguration.getErrorTracker().i(TAG, "onLostConnection()");
        notifyUploadListenerCancelled(uploadItem);
        //pause upload manager
        pause();
        addUploadRequest(uploadItem);
    }

    @Override
    public void onCancelled(UploadItem uploadItem, ApiResponse apiResponse) {
        MFConfiguration.getErrorTracker().i(TAG, "onCancelled()");
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
        MFConfiguration.getErrorTracker().i(TAG, "pause()");
        executor.pause();
    }

    public void resume() {
        MFConfiguration.getErrorTracker().i(TAG, "resume()");
        executor.resume();
    }

    public boolean isPaused() {
        MFConfiguration.getErrorTracker().i(TAG, "isPaused()");
        return executor.isPaused();
    }
}
