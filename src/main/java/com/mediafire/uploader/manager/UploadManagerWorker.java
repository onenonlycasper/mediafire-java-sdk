package com.mediafire.uploader.manager;

import com.mediafire.sdk.api_responses.ApiResponse;
import com.mediafire.sdk.api_responses.upload.CheckResponse;
import com.mediafire.sdk.api_responses.upload.InstantResponse;
import com.mediafire.sdk.api_responses.upload.PollResponse;
import com.mediafire.sdk.api_responses.upload.ResumableResponse;
import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.token.MFTokenFarm;
import com.mediafire.uploader.process.CheckProcess;
import com.mediafire.uploader.process.InstantProcess;
import com.mediafire.uploader.process.PollProcess;
import com.mediafire.uploader.process.ResumableProcess;
import com.mediafire.uploader.uploaditem.UploadItem;

import java.util.concurrent.Executor;

/**
 * Created by Chris Najar on 7/8/2014.
 */
public abstract class UploadManagerWorker {
    private static final String TAG = UploadManagerWorker.class.getCanonicalName();
    protected final int maxUploadAttempts;
    protected final MFTokenFarm mfTokenFarm;
    protected final Executor executor;

    public UploadManagerWorker(MFTokenFarm mfTokenFarm, int maxUploadAttempts, Executor executor) {
        if (mfTokenFarm == null) {
            throw new IllegalArgumentException("MFTokenFarm must not be null");
        }

        if (maxUploadAttempts < 1) {
            throw new IllegalArgumentException("maxUploadAttempts must be greater than 0");
        }

        if (executor == null) {
            throw new IllegalArgumentException("Executor must not be null");
        }
        this.mfTokenFarm = mfTokenFarm;
        this.executor = executor;
        this.maxUploadAttempts = maxUploadAttempts;
    }

    public abstract void addUploadRequest(UploadItem uploadItem);
    protected abstract void notifyUploadListenerStarted(UploadItem uploadItem);
    protected abstract void notifyUploadListenerCompleted(UploadItem uploadItem);
    protected abstract void notifyUploadListenerOnProgressUpdate(UploadItem uploadItem, int chunkNumber, int numChunks);
    protected abstract void notifyUploadListenerCancelled(UploadItem uploadItem);

    public void onStartedUploadProcess(UploadItem uploadItem) {
        MFConfiguration.getStaticMFLogger().v(TAG, "onStartedUploadProcess()");
        notifyUploadListenerStarted(uploadItem);
    }

    public void onCheckCompleted(UploadItem uploadItem, CheckResponse checkResponse) {
        MFConfiguration.getStaticMFLogger().v(TAG, "onCheckCompleted()");
        //as a preventable infinite loop measure, an upload item cannot continue after upload/check.php if it has gone through the process 20x
        //20x is high, but it should never happen and will allow for more information gathering.
        if (uploadItem.getUploadAttemptCount() > maxUploadAttempts || uploadItem.isCancelled()) {
            notifyUploadListenerCancelled(uploadItem);
            return;
        }

        if (checkResponse.getStorageLimitExceeded()) {
            MFConfiguration.getStaticMFLogger().v(TAG, "storage limit is exceeded");
            storageLimitExceeded(uploadItem);
        } else if (checkResponse.getResumableUpload().areAllUnitsReady() && !uploadItem.getPollUploadKey().isEmpty()) {
            // all units are ready and we have the poll upload key. start polling.
            PollProcess process = new PollProcess(mfTokenFarm, this, uploadItem);
            executor.execute(process);
        } else {
            if (checkResponse.doesHashExists()) { //hash does exist for the file
                hashExists(uploadItem, checkResponse);
            } else { // hash does not exist. call resumable.
                hashDoesNotExist(uploadItem, checkResponse);
            }
        }
    }

    public void onProgressUpdate(UploadItem uploadItem, int chunkNumber, int numChunks) {
        MFConfiguration.getStaticMFLogger().v(TAG, "onProgressUpdate()");
        notifyUploadListenerOnProgressUpdate(uploadItem, chunkNumber, numChunks);
    }

    private void storageLimitExceeded(UploadItem uploadItem) {
        MFConfiguration.getStaticMFLogger().v(TAG, "storageLimitExceeded()");
        notifyUploadListenerCancelled(uploadItem);
    }

    private void hashExists(UploadItem uploadItem, CheckResponse checkResponse) {
        MFConfiguration.getStaticMFLogger().v(TAG, "hashExists()");
        if (!checkResponse.isInAccount()) { // hash which exists is not in the account
            hashNotInAccount(uploadItem);
        } else { // hash exists and is in the account
            hashInAccount(uploadItem, checkResponse);
        }
    }

    private void hashNotInAccount(UploadItem uploadItem) {
        MFConfiguration.getStaticMFLogger().v(TAG, "hashNotInAccount()");
        InstantProcess process = new InstantProcess(mfTokenFarm, this, uploadItem);
        executor.execute(process);
    }

    private void hashInAccount(UploadItem uploadItem, CheckResponse checkResponse) {
        MFConfiguration.getStaticMFLogger().v(TAG, "hashInAccount()");
        boolean inFolder = checkResponse.isInFolder();
        InstantProcess process = new InstantProcess(mfTokenFarm, this, uploadItem);
        MFConfiguration.getStaticMFLogger().v(TAG, "ActionOnInAccount: " + uploadItem.getUploadOptions().getActionOnInAccount().toString());
        switch (uploadItem.getUploadOptions().getActionOnInAccount()) {
            case UPLOAD_ALWAYS:
                MFConfiguration.getStaticMFLogger().v(TAG, "uploading...");
                executor.execute(process);
                break;
            case UPLOAD_IF_NOT_IN_FOLDER:
                MFConfiguration.getStaticMFLogger().v(TAG, "uploading if not in folder.");
                if (!inFolder) {
                    MFConfiguration.getStaticMFLogger().v(TAG, "uploading...");
                    executor.execute(process);
                } else {
                    MFConfiguration.getStaticMFLogger().v(TAG, "already in folder, not uploading...");
                    notifyUploadListenerCompleted(uploadItem);
                }
                break;
            case DO_NOT_UPLOAD:
            default:
                MFConfiguration.getStaticMFLogger().v(TAG, "not uploading...");
                notifyUploadListenerCompleted(uploadItem);
                break;
        }
    }

    private void hashDoesNotExist(UploadItem uploadItem, CheckResponse checkResponse) {
        MFConfiguration.getStaticMFLogger().v(TAG, "hashDoesNotExist()");
        if (checkResponse.getResumableUpload().getUnitSize() == 0) {
            MFConfiguration.getStaticMFLogger().v(TAG, "unit size received from unit_size was 0. cancelling");
            notifyUploadListenerCancelled(uploadItem);
            return;
        }

        if (checkResponse.getResumableUpload().getNumberOfUnits() == 0) {
            MFConfiguration.getStaticMFLogger().v(TAG, "number of units received from number_of_units was 0. cancelling");
            notifyUploadListenerCancelled(uploadItem);
            return;
        }

        if (checkResponse.getResumableUpload().areAllUnitsReady() && !uploadItem.getPollUploadKey().isEmpty()) {
            MFConfiguration.getStaticMFLogger().v(TAG, "all units ready and have a poll upload key");
            // all units are ready and we have the poll upload key. start polling.
            PollProcess process = new PollProcess(mfTokenFarm, this, uploadItem);
            executor.execute(process);
        } else {
            MFConfiguration.getStaticMFLogger().v(TAG, "all units not ready or do not have poll upload key");
            // either we don't have the poll upload key or all units are not ready
            ResumableProcess process = new ResumableProcess(mfTokenFarm, this, uploadItem);
            executor.execute(process);
        }
    }

    public void onInstantCompleted(UploadItem uploadItem, InstantResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, "onInstantCompleted()");
        notifyUploadListenerCompleted(uploadItem);
    }

    public void onResumableCompleted(UploadItem uploadItem, ResumableResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, "onResumableCompleted()");
        if (response != null &&
                response.getResumableUpload().areAllUnitsReady() &&
                !response.getDoUpload().getPollUploadKey().isEmpty()) {
            PollProcess process = new PollProcess(mfTokenFarm, this, uploadItem);
            executor.execute(process);
        } else {
            CheckProcess process = new CheckProcess(mfTokenFarm, this, uploadItem);
            executor.execute(process);
        }
    }

    public void onPollCompleted(UploadItem uploadItem, PollResponse pollResponse) {
        MFConfiguration.getStaticMFLogger().v(TAG, "onPollCompleted()");
        // if this method is called then file error and result codes are fine, but we may not have received status 99 so
        // check status code and then possibly send item to the backlog queue.
        PollResponse.DoUpload doUpload = pollResponse.getDoUpload();
        PollResponse.Status pollStatusCode = doUpload.getStatusCode();
        PollResponse.Result pollResultCode = doUpload.getResultCode();
        PollResponse.FileError pollFileErrorCode = doUpload.getFileErrorCode();

        if (pollStatusCode != PollResponse.Status.NO_MORE_REQUESTS_FOR_THIS_KEY && pollResultCode == PollResponse.Result.SUCCESS && pollFileErrorCode == PollResponse.FileError.NO_ERROR) {
            MFConfiguration.getStaticMFLogger().v(TAG, "status code: " + pollResponse.getDoUpload().getStatusCode().toString() + " need to try again");
            notifyUploadListenerCancelled(uploadItem);
            addUploadRequest(uploadItem);
        } else {
            notifyUploadListenerCompleted(uploadItem);
        }
    }

    public void onProcessException(UploadItem uploadItem, Exception exception) {
        MFConfiguration.getStaticMFLogger().v(TAG, "onProcessException()");
        MFConfiguration.getStaticMFLogger().e(TAG, "received exception: " + exception, exception);
        notifyUploadListenerCancelled(uploadItem);
    }

    public void onLostConnection(UploadItem uploadItem) {
        MFConfiguration.getStaticMFLogger().v(TAG, "onLostConnection()");
        notifyUploadListenerCancelled(uploadItem);
        //pause upload manager
        addUploadRequest(uploadItem);
    }

    public void onCancelled(UploadItem uploadItem, ApiResponse apiResponse) {
        MFConfiguration.getStaticMFLogger().v(TAG, "onCancelled()");
        notifyUploadListenerCancelled(uploadItem);
        // if there is an api error then re-add upload request.
        if (apiResponse != null && apiResponse.hasError()) {
            addUploadRequest(uploadItem);
        }
    }
}
