package com.mediafire.sdk.uploader;

import com.mediafire.sdk.api_responses.ApiResponse;
import com.mediafire.sdk.api_responses.upload.CheckResponse;
import com.mediafire.sdk.api_responses.upload.InstantResponse;
import com.mediafire.sdk.api_responses.upload.PollResponse;
import com.mediafire.sdk.api_responses.upload.ResumableResponse;
import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.http.MFApi;
import com.mediafire.sdk.http.MFHost;
import com.mediafire.sdk.http.MFRequest;
import com.mediafire.sdk.http.MFResponse;
import com.mediafire.sdk.token.MFTokenFarm;
import com.mediafire.sdk.uploader.uploaditem.*;

import java.io.*;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chris Najar on 7/21/2014.
 */
public class MFUploadRunnable implements Runnable {
    private static final String TAG = MFUploadRunnable.class.getCanonicalName();
    private final int maxPolls;
    private final long millisecondsBetweenPolls;
    private final MFTokenFarm mfTokenFarm;
    private final MFUploadItem mfUploadItem;
    private final MFUploadListener mfUploadListener;
    private final int maxUploadAttempts;
    private String utf8EncodedFileName;

    private MFUploadRunnable(Builder builder) {
        this.maxPolls = builder.maxPolls;
        this.millisecondsBetweenPolls = builder.millisecondsBetweenPolls;
        this.mfTokenFarm = builder.mfTokenFarm;
        this.mfUploadItem = builder.mfUploadItem;
        this.mfUploadListener = builder.mfUploadListener;
        this.maxUploadAttempts = builder.maxUploadAttempts;
    }

    @Override
    public void run() {
        MFConfiguration.getStaticMFLogger().w(TAG, "run()");
        notifyUploadListenerStarted();

        try {
            encodeFileNameUTF8();
            startOrRestartUpload();
        } catch (UnsupportedEncodingException e) {
            MFConfiguration.getStaticMFLogger().e(TAG, "UnsupportedEncodingException during MFUploadRunnable", e);
            notifyUploadListenerCancelled();
        } catch (NoSuchAlgorithmException e) {
            MFConfiguration.getStaticMFLogger().e(TAG, "NoSuchAlgorithmException during MFUploadRunnable", e);
            notifyUploadListenerCancelled();
        } catch (IOException e) {
            MFConfiguration.getStaticMFLogger().e(TAG, "IOException during MFUploadRunnable", e);
            notifyUploadListenerCancelled();
        }
    }

    private void encodeFileNameUTF8() throws UnsupportedEncodingException {
        MFConfiguration.getStaticMFLogger().w(TAG, "encodeFileNameUTF8");
        utf8EncodedFileName = new String(mfUploadItem.getFileName().getBytes("UTF-8"), "UTF-8");
    }

    private void checkUploadFinished(MFUploadItem mfUploadItem, CheckResponse checkResponse) throws NoSuchAlgorithmException, IOException {
        MFConfiguration.getStaticMFLogger().w(TAG, "checkUploadFinished()");
        //as a preventable infinite loop measure, an upload item cannot continue after upload/check.php if it has gone through the process 20x
        //20x is high, but it should never happen and will allow for more information gathering.
        if (mfUploadItem.getUploadAttemptCount() > maxUploadAttempts || mfUploadItem.isCancelled()) {
            notifyUploadListenerCancelled();
            return;
        }

        if (checkResponse.getStorageLimitExceeded()) {
            MFConfiguration.getStaticMFLogger().w(TAG, "storage limit is exceeded");
            storageLimitExceeded();
        } else if (checkResponse.getResumableUpload().areAllUnitsReady() && !mfUploadItem.getPollUploadKey().isEmpty()) {
            // all units are ready and we have the poll upload key. start polling.
            doInstantUpload();
        } else {
            if (checkResponse.doesHashExists()) { //hash does exist for the file
                hashExists(checkResponse);
            } else { // hash does not exist. call resumable.
                hashDoesNotExist(checkResponse);
            }
        }
    }

    private void instantUploadFinished() {
        MFConfiguration.getStaticMFLogger().w(TAG, "instantUploadFinished()");
        notifyUploadListenerCompleted();
    }

    private void resumableUploadFinished(ResumableResponse response) throws IOException, NoSuchAlgorithmException {
        MFConfiguration.getStaticMFLogger().w(TAG, "resumableUploadFinished()");
        if (response != null && response.getResumableUpload().areAllUnitsReady() && !response.getDoUpload().getPollUploadKey().isEmpty()) {
            doPollUpload();
        } else {
            doCheckUpload();
        }
    }

    private void pollUploadFinished(PollResponse pollResponse) throws IOException, NoSuchAlgorithmException {
        MFConfiguration.getStaticMFLogger().w(TAG, "pollUploadFinished()");
        // if this method is called then file error and result codes are fine, but we may not have received status 99 so
        // check status code and then possibly send item to the backlog queue.
        PollResponse.DoUpload doUpload = pollResponse.getDoUpload();
        PollResponse.Status pollStatusCode = doUpload.getStatusCode();
        PollResponse.Result pollResultCode = doUpload.getResultCode();
        PollResponse.FileError pollFileErrorCode = doUpload.getFileErrorCode();

        if (pollStatusCode != PollResponse.Status.NO_MORE_REQUESTS_FOR_THIS_KEY && pollResultCode == PollResponse.Result.SUCCESS && pollFileErrorCode == PollResponse.FileError.NO_ERROR) {
            MFConfiguration.getStaticMFLogger().w(TAG, "status code: " + pollResponse.getDoUpload().getStatusCode().toString() + " need to try again");
            startOrRestartUpload();
        }
    }

    private void storageLimitExceeded() {
        MFConfiguration.getStaticMFLogger().w(TAG, "storageLimitExceeded()");
        notifyUploadListenerCancelled();
    }

    private void hashExists(CheckResponse checkResponse) {
        MFConfiguration.getStaticMFLogger().w(TAG, "hashExists()");
        if (!checkResponse.isInAccount()) { // hash which exists is not in the account
            hashNotInAccount();
        } else { // hash exists and is in the account
            hashInAccount(checkResponse);
        }
    }

    private void hashNotInAccount() {
        MFConfiguration.getStaticMFLogger().w(TAG, "hashNotInAccount()");
        doInstantUpload();
    }

    private void hashInAccount(CheckResponse checkResponse) {
        MFConfiguration.getStaticMFLogger().w(TAG, "hashInAccount()");
        boolean inFolder = checkResponse.isInFolder();
        MFConfiguration.getStaticMFLogger().w(TAG, "ActionOnInAccount: " + mfUploadItem.getUploadOptions().getActionOnInAccount().toString());
        switch (mfUploadItem.getUploadOptions().getActionOnInAccount()) {
            case UPLOAD_ALWAYS:
                MFConfiguration.getStaticMFLogger().w(TAG, "uploading...");
                doInstantUpload();
                break;
            case UPLOAD_IF_NOT_IN_FOLDER:
                MFConfiguration.getStaticMFLogger().w(TAG, "uploading if not in folder.");
                if (!inFolder) {
                    MFConfiguration.getStaticMFLogger().w(TAG, "uploading...");
                    doInstantUpload();
                } else {
                    MFConfiguration.getStaticMFLogger().w(TAG, "already in folder, not uploading...");
                    notifyUploadListenerCompleted();
                }
                break;
            case DO_NOT_UPLOAD:
            default:
                MFConfiguration.getStaticMFLogger().w(TAG, "not uploading...");
                notifyUploadListenerCancelled();
                break;
        }
    }

    private void hashDoesNotExist(CheckResponse checkResponse) throws IOException, NoSuchAlgorithmException {
        MFConfiguration.getStaticMFLogger().w(TAG, "hashDoesNotExist()");
        if (checkResponse.getResumableUpload().getUnitSize() == 0) {
            MFConfiguration.getStaticMFLogger().w(TAG, "unit size received from unit_size was 0. cancelling");
            notifyUploadListenerCancelled();
            return;
        }

        if (checkResponse.getResumableUpload().getNumberOfUnits() == 0) {
            MFConfiguration.getStaticMFLogger().w(TAG, "number of units received from number_of_units was 0. cancelling");
            notifyUploadListenerCancelled();
            return;
        }

        if (checkResponse.getResumableUpload().areAllUnitsReady() && !mfUploadItem.getPollUploadKey().isEmpty()) {
            MFConfiguration.getStaticMFLogger().w(TAG, "all units ready and have a poll upload key");
            // all units are ready and we have the poll upload key. start polling.
            doPollUpload();
        } else {
            MFConfiguration.getStaticMFLogger().w(TAG, "all units not ready or do not have poll upload key");
            // either we don't have the poll upload key or all units are not ready
            doResumableUpload();
        }
    }

    private void doCheckUpload() throws IOException, NoSuchAlgorithmException {
        MFConfiguration.getStaticMFLogger().w(TAG, "doCheckUpload()");
        if (mfUploadItem.isCancelled()) {
            MFConfiguration.getStaticMFLogger().w(TAG, "upload was cancelled for " + mfUploadItem.getFileName());
            notifyUploadListenerCancelled();
            return;
        }

        if (!haveStoredCredentials()) {
            MFConfiguration.getStaticMFLogger().w(TAG, "no credentials stored, task cancelling()");
            mfUploadItem.cancelUpload();
            return;
        }
        // generate map with request parameters
        Map<String, String> keyValue = generateCheckUploadRequestParameters();
        MFRequest.MFRequestBuilder mfRequestBuilder = new MFRequest.MFRequestBuilder(MFHost.LIVE_HTTP, MFApi.UPLOAD_CHECK);
        mfRequestBuilder.requestParameters(keyValue);
        MFRequest mfRequest = mfRequestBuilder.build();
        MFResponse mfResponse = mfTokenFarm.getMFHttpRunner().doRequest(mfRequest);
        CheckResponse response = mfResponse.getResponseObject(CheckResponse.class);

        if (response == null) {
            notifyUploadListenerCancelled();
            return;
        }

        // if there is an error code, cancel the upload
        if (response.getErrorCode() != ApiResponse.ResponseCode.NO_ERROR) {
            notifyUploadListenerCancelled();
            return;
        }

        mfUploadItem.getChunkData().setNumberOfUnits(response.getResumableUpload().getNumberOfUnits());
        mfUploadItem.getChunkData().setUnitSize(response.getResumableUpload().getUnitSize());
        int count = response.getResumableUpload().getBitmap().getCount();
        List<Integer> words = response.getResumableUpload().getBitmap().getWords();
        MFResumableBitmap bitmap = new MFResumableBitmap(count, words);
        mfUploadItem.setBitmap(bitmap);
        MFConfiguration.getStaticMFLogger().w(TAG, mfUploadItem.getFileData().getFilePath() + " upload item bitmap: " + mfUploadItem.getBitmap().getCount() + " count, " + mfUploadItem.getBitmap().getWords().toString() + " words.");

        // notify listeners that check has completed
        checkUploadFinished(mfUploadItem, response);
    }

    private void doInstantUpload() {
        MFConfiguration.getStaticMFLogger().w(TAG, "doInstantUpload()");
        if (mfUploadItem.isCancelled()) {
            MFConfiguration.getStaticMFLogger().w(TAG, "upload was cancelled for " + mfUploadItem.getFileName());
            notifyUploadListenerCancelled();
            return;
        }

        if (!haveStoredCredentials()) {
            MFConfiguration.getStaticMFLogger().w(TAG, "no credentials stored, task cancelling()");
            mfUploadItem.cancelUpload();
            return;
        }
        // url encode the filename
        String filename;
        try {
            filename = URLEncoder.encode(mfUploadItem.getFileName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            MFConfiguration.getStaticMFLogger().w(TAG, "Exception: " + e);
            notifyUploadListenerCancelled();
            return;
        }

        // generate map with request parameters
        Map<String, String> keyValue = generateInstantUploadRequestParameters(filename);
        MFRequest.MFRequestBuilder mfRequestBuilder = new MFRequest.MFRequestBuilder(MFHost.LIVE_HTTP, MFApi.UPLOAD_INSTANT);
        mfRequestBuilder.requestParameters(keyValue);
        MFRequest mfRequest = mfRequestBuilder.build();

        MFResponse mfResponse = mfTokenFarm.getMFHttpRunner().doRequest(mfRequest);
        InstantResponse response = mfResponse.getResponseObject(InstantResponse.class);

        if (response == null) {
            notifyUploadListenerCancelled();
            return;
        }

        if (response.getErrorCode() != ApiResponse.ResponseCode.NO_ERROR) {
            notifyUploadListenerCancelled();
            return;
        }

        if (!response.getQuickkey().isEmpty()) {
            // notify listeners that check has completed
            instantUploadFinished();
        } else {
            notifyUploadListenerCancelled();
        }
    }

    private void doResumableUpload() throws IOException, NoSuchAlgorithmException {
        MFConfiguration.getStaticMFLogger().w(TAG, "doResumableUpload()");

        //get file size. this will be used for chunks.
        MFFileData fileData = mfUploadItem.getFileData();
        long fileSize = fileData.getFileSize();

        // get chunk. these will be used for chunks.
        MFChunkData MFChunkData = mfUploadItem.getChunkData();
        int numChunks = MFChunkData.getNumberOfUnits();
        int unitSize = MFChunkData.getUnitSize();

        // loop through our chunks and create http post with header data and send after we are done looping,
        // let the listener know we are completed
        ResumableResponse response = null;
        for (int chunkNumber = 0; chunkNumber < numChunks; chunkNumber++) {
            if (mfUploadItem.isCancelled()) {
                MFConfiguration.getStaticMFLogger().w(TAG, "upload was cancelled for " + mfUploadItem.getFileName());
                notifyUploadListenerCancelled();
                return;
            }

            if (!haveStoredCredentials()) {
                MFConfiguration.getStaticMFLogger().w(TAG, "no credentials stored, task cancelling()");
                mfUploadItem.cancelUpload();
                return;
            }

            // if the bitmap says this chunk number is uploaded then we can just skip it, if not, we upload it.
            if (!mfUploadItem.getBitmap().isUploaded(chunkNumber)) {
                // get the chunk size for this chunk
                int chunkSize = getChunkSize(chunkNumber, numChunks, fileSize, unitSize);

                MFResumableChunkInfo mfResumableChunkInfo = createResumableChunkInfo(unitSize, chunkNumber);
                if (mfResumableChunkInfo == null) {
                    notifyUploadListenerCancelled();
                    return;
                }

                String chunkHash = mfResumableChunkInfo.getChunkHash();
                byte[] uploadChunk = mfResumableChunkInfo.getUploadChunk();

                printDebugCurrentChunk(chunkNumber, numChunks, chunkSize, unitSize, fileSize, chunkHash, uploadChunk);

                // generate the post headers
                Map<String, String> headers = generatePostHeaders(utf8EncodedFileName, fileSize, chunkNumber, chunkHash, chunkSize);
                // generate the get parameters
                Map<String, String> parameters = generateResumableRequestParameters();

                printDebugRequestData(headers, parameters);

                MFRequest.MFRequestBuilder mfRequestBuilder = new MFRequest.MFRequestBuilder(MFHost.LIVE_HTTP, MFApi.UPLOAD_RESUMABLE);
                mfRequestBuilder.requestParameters(parameters);
                mfRequestBuilder.headers(headers);
                mfRequestBuilder.payload(uploadChunk);
                MFRequest mfRequest = mfRequestBuilder.build();

                MFResponse mfResponse = mfTokenFarm.getMFHttpRunner().doRequest(mfRequest);

                MFConfiguration.getStaticMFLogger().w(TAG, "response string: " + mfResponse.getResponseAsString());
                response = mfResponse.getResponseObject(ResumableResponse.class);

                // set poll upload key if possible
                if (shouldSetPollUploadKey(response)) {
                    mfUploadItem.setPollUploadKey(response.getDoUpload().getPollUploadKey());
                }

                if (shouldCancelUpload(response)) {
                    notifyUploadListenerCancelled();
                    return;
                }

                // update the response bitmap
                int count = response.getResumableUpload().getBitmap().getCount();
                List<Integer> words = response.getResumableUpload().getBitmap().getWords();
                MFResumableBitmap bitmap = new MFResumableBitmap(count, words);
                mfUploadItem.setBitmap(bitmap);
                MFConfiguration.getStaticMFLogger().w(TAG, "(" + mfUploadItem.getFileData().getFilePath() + ") upload item bitmap: " + mfUploadItem.getBitmap().getCount() + " count, (" + mfUploadItem.getBitmap().getWords().toString() + ") words.");

                clearReferences(chunkSize, chunkHash, uploadChunk, headers, parameters);
            }
            notifyUploadListenerOnProgressUpdate(numChunks);

        } // end loop

        // let the listeners know that upload has attempted to upload all chunks.
        resumableUploadFinished(response);
    }

    private void doPollUpload() throws IOException, NoSuchAlgorithmException {
        MFConfiguration.getStaticMFLogger().w(TAG, "doPollUpload()");
        if (mfUploadItem.isCancelled()) {
            MFConfiguration.getStaticMFLogger().w(TAG, "upload was cancelled for " + mfUploadItem.getFileName());
            notifyUploadListenerCancelled();
            return;
        }

        //generate our request string
        Map<String, String> keyValue = generatePollRequestParameters();

        int pollCount = 0;
        do {

            if (!haveStoredCredentials()) {
                MFConfiguration.getStaticMFLogger().w(TAG, "no credentials stored, task cancelling()");
                mfUploadItem.cancelUpload();
                return;
            }
            // increment counter
            pollCount++;
            // get api response.

            MFRequest.MFRequestBuilder mfRequestBuilder = new MFRequest.MFRequestBuilder(MFHost.LIVE_HTTP, MFApi.UPLOAD_POLL_UPLOAD);
            mfRequestBuilder.requestParameters(keyValue);
            MFRequest mfRequest = mfRequestBuilder.build();
            MFResponse mfResponse = mfTokenFarm.getMFHttpRunner().doRequest(mfRequest);

            if (mfResponse == null) {
                startOrRestartUpload();
                return;
            }

            PollResponse response = mfResponse.getResponseObject(PollResponse.class);

            if (mfUploadItem.isCancelled()) {
                MFConfiguration.getStaticMFLogger().w(TAG, "upload was cancelled for " + mfUploadItem.getFileName());
                notifyUploadListenerCancelled();
                return;
            }

            if (!haveStoredCredentials()) {
                MFConfiguration.getStaticMFLogger().w(TAG, "no credentials stored, task cancelling()");
                mfUploadItem.cancelUpload();
                return;
            }

            if (response == null) {
                notifyUploadListenerCancelled();
                return;
            }

            MFConfiguration.getStaticMFLogger().w(TAG, "received error code: " + response.getErrorCode());
            //check to see if we need to call pollUploadCompleted or loop again
            switch (response.getErrorCode()) {
                case NO_ERROR:
                    //just because we had response/result "Success" doesn't mean everything is good.
                    //we need to find out if we should continue polling or not
                    //  conditions to check:
                    //      first   -   result code no error? yes, keep calm and poll on. no, cancel upload because error.
                    //      second  -   fileerror code no error? yes, carry on old chap!. no, cancel upload because error.
                    //      third   -   status code 99 (no more requests)? yes, done. no, continue.
                    if (response.getDoUpload().getResultCode() != PollResponse.Result.SUCCESS) {
                        MFConfiguration.getStaticMFLogger().w(TAG, "result code: " + response.getDoUpload().getResultCode().toString() + " need to cancel");
                        notifyUploadListenerCancelled();
                        return;
                    }

                    if (response.getDoUpload().getFileErrorCode() != PollResponse.FileError.NO_ERROR) {
                        MFConfiguration.getStaticMFLogger().w(TAG, "result code: " + response.getDoUpload().getFileErrorCode().toString() + " need to cancel");
                        notifyUploadListenerCancelled();
                        return;
                    }

                    if (response.getDoUpload().getStatusCode() == PollResponse.Status.NO_MORE_REQUESTS_FOR_THIS_KEY) {
                        MFConfiguration.getStaticMFLogger().w(TAG, "status code: " + response.getDoUpload().getStatusCode().toString());
                        pollUploadFinished(response);
                        return;
                    }
                    break;
                default:
                    // stop polling and inform listeners we cancel because API result wasn't "Success"
                    notifyUploadListenerCancelled();
                    return;
            }

            //wait before next api call
            try {
                Thread.sleep(millisecondsBetweenPolls);
            } catch (InterruptedException e) {
                MFConfiguration.getStaticMFLogger().w(TAG, "Exception: " + e);
                notifyUploadListenerCancelled();
                return;
            }

            if (mfUploadItem.isCancelled()) {
                pollCount = maxPolls;
            }
        } while (pollCount < maxPolls);

        startOrRestartUpload();
    }

    private Map<String, String> generatePollRequestParameters() {
        MFConfiguration.getStaticMFLogger().w(TAG, "generatePollRequestParameters()");
        LinkedHashMap<String, String> keyValue = new LinkedHashMap<String, String>();
        keyValue.put("key", mfUploadItem.getPollUploadKey());
        keyValue.put("response_format", "json");
        return keyValue;
    }

    @SuppressWarnings({"ParameterCanBeLocal", "UnusedParameters", "UnusedAssignment"})
    private void clearReferences(int chunkSize, String chunkHash, byte[] uploadChunk, Map<String, String> headers, Map<String, String> parameters) {
        chunkSize = 0;
        chunkHash = null;
        uploadChunk = null;
        headers = null;
        parameters = null;
    }

    private void printDebugRequestData(Map<String, String> headers, Map<String, String> parameters) {
        MFConfiguration.getStaticMFLogger().w(TAG, "printDebugRequestData()");
        MFConfiguration.getStaticMFLogger().w(TAG, "headers: " + headers.toString());
        MFConfiguration.getStaticMFLogger().w(TAG, "parameters: " + parameters.toString());
    }

    @SuppressWarnings("UnusedParameters")
    private void printDebugCurrentChunk(int chunkNumber, int numChunks, int chunkSize, int unitSize, long fileSize, String chunkHash, byte[] uploadChunk) {
        MFConfiguration.getStaticMFLogger().w(TAG, "printDebugCurrentChunk()");
        MFConfiguration.getStaticMFLogger().w(TAG, "current thread: " + Thread.currentThread().getName());
        MFConfiguration.getStaticMFLogger().w(TAG, "current chunk: " + chunkNumber);
        MFConfiguration.getStaticMFLogger().w(TAG, "total chunks: " + numChunks);
        MFConfiguration.getStaticMFLogger().w(TAG, "current chunk size: " + chunkSize);
        MFConfiguration.getStaticMFLogger().w(TAG, "normal chunk size: " + unitSize);
        MFConfiguration.getStaticMFLogger().w(TAG, "total file size: " + fileSize);
        MFConfiguration.getStaticMFLogger().w(TAG, "current chunk hash: " + chunkHash);
        MFConfiguration.getStaticMFLogger().w(TAG, "upload chunk ");
    }

    private boolean shouldCancelUpload(ResumableResponse response) {
        MFConfiguration.getStaticMFLogger().w(TAG, "shouldCancelUpload()");
        // if API response code OR Upload Response Result code have an error then we need to terminate the process
        if (response.hasError()) {
            return true;
        }

        if (response.getDoUpload().getResultCode() != ResumableResponse.Result.NO_ERROR) {
            if (response.getDoUpload().getResultCode() != ResumableResponse.Result.SUCCESS_FILE_MOVED_TO_ROOT) {
                return true;
            }
        }

        return false;
    }

    private MFResumableChunkInfo createResumableChunkInfo(int unitSize, int chunkNumber) throws IOException, NoSuchAlgorithmException {
        MFConfiguration.getStaticMFLogger().w(TAG, "createResumableChunkInfo");
        MFResumableChunkInfo mfResumableChunkInfo;
        // generate the chunk
        FileInputStream fis;
        BufferedInputStream bis;
        String chunkHash;
        byte[] uploadChunk;
        fis = new FileInputStream(mfUploadItem.getFileData().getFilePath());
        bis = new BufferedInputStream(fis);
        uploadChunk = createUploadChunk(unitSize, chunkNumber, bis);
        chunkHash = getSHA256(uploadChunk);
        mfResumableChunkInfo = new MFResumableChunkInfo(chunkHash, uploadChunk);
        fis.close();
        bis.close();
        return mfResumableChunkInfo;
    }

    private Map<String, String> generateResumableRequestParameters() {
        MFConfiguration.getStaticMFLogger().w(TAG, "generateResumableRequestParameters()");
        // get upload options. these will be passed as request parameters
        MFUploadItemOptions mfUploadItemOptions = mfUploadItem.getUploadOptions();
        String actionOnDuplicate = mfUploadItemOptions.getActionOnDuplicate();
        String versionControl = mfUploadItemOptions.getVersionControl();
        String uploadFolderKey = mfUploadItemOptions.getUploadFolderKey();
        String uploadPath = mfUploadItemOptions.getUploadPath();

        String actionToken = mfTokenFarm.borrowMFUploadActionToken().getTokenString();
        LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("session_token", actionToken);
        parameters.put("action_on_duplicate", actionOnDuplicate);
        parameters.put("response_format", "json");
        parameters.put("version_control", versionControl);
        if (uploadPath != null && !uploadPath.isEmpty()) {
            parameters.put("path", uploadPath);
        } else {
            parameters.put("folder_key", uploadFolderKey);
        }

        return parameters;
    }

    private Map<String, String> generatePostHeaders(String encodedShortFileName, long fileSize, int chunkNumber, String chunkHash, int chunkSize) {
        MFConfiguration.getStaticMFLogger().w(TAG, "generatePostHeaders()");
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        // these headers are related to the entire file
        headers.put("x-filename", encodedShortFileName);
        headers.put("x-filesize", String.valueOf(fileSize));
        headers.put("x-filehash", mfUploadItem.getFileData().getFileHash());
        // these headers are related to the individual chunk
        headers.put("x-unit-id", Integer.toString(chunkNumber));
        headers.put("x-unit-hash", chunkHash);
        headers.put("x-unit-size", Integer.toString(chunkSize));
        return headers;
    }

    private boolean shouldSetPollUploadKey(ResumableResponse response) {
        MFConfiguration.getStaticMFLogger().w(TAG, "shouldSetPollUploadKey()");
        switch (response.getDoUpload().getResultCode()) {
            case NO_ERROR:
            case SUCCESS_FILE_MOVED_TO_ROOT:
                return true;
            default:
                return false;
        }
    }

    private int getChunkSize(int chunkNumber, int numChunks, long fileSize, int unitSize) {
        MFConfiguration.getStaticMFLogger().w(TAG, "getChunkSize()");
        int chunkSize;
        if (chunkNumber >= numChunks) {
            chunkSize = 0; // represents bad size
        } else {
            if (fileSize % unitSize == 0) { // all units will be of unitSize
                chunkSize = unitSize;
            } else if (chunkNumber < numChunks - 1) { // this unit is of unitSize
                chunkSize = unitSize;
            } else { // this unit is "special" and is the modulo of fileSize and unitSize;
                chunkSize = (int) (fileSize % unitSize);
            }
        }

        return chunkSize;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private byte[] createUploadChunk(long unitSize, int chunkNumber, BufferedInputStream fileStream) throws IOException {
        MFConfiguration.getStaticMFLogger().w(TAG, "createUploadChunk()");
        byte[] readBytes = new byte[(int) unitSize];
        int offset = (int) (unitSize * chunkNumber);
        fileStream.skip(offset);
        int readSize = fileStream.read(readBytes, 0, (int) unitSize);
        if (readSize != unitSize) {
            byte[] temp = new byte[readSize];
            System.arraycopy(readBytes, 0, temp, 0, readSize);
            readBytes = temp;
        }

        return readBytes;
    }

    private String getSHA256(byte[] chunkData) throws NoSuchAlgorithmException, IOException {
        MFConfiguration.getStaticMFLogger().w(TAG, "getSHA256()");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        //test code
        InputStream in = new ByteArrayInputStream(chunkData, 0, chunkData.length);
        byte[] bytes = new byte[8192];
        int byteCount;
        while ((byteCount = in.read(bytes)) > 0) {
            md.update(bytes, 0, byteCount);
        }
        byte[] hashBytes = md.digest();
        //test code
        //byte[] hashBytes = md.digest(chunkData); //original code

        return convertHashBytesToString(hashBytes);
    }

    private String convertHashBytesToString(byte[] hashBytes) {
        MFConfiguration.getStaticMFLogger().w(TAG, "convertHashBytesToString()");
        StringBuilder sb = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String tempString = Integer.toHexString((hashByte & 0xFF) | 0x100).substring(1, 3);
            sb.append(tempString);
        }

        return sb.toString();
    }

    private Map<String, String> generateInstantUploadRequestParameters(String filename) {
        MFConfiguration.getStaticMFLogger().w(TAG, "generateInstantUploadRequestParameters()");
        // generate map with request parameters
        Map<String, String> keyValue = new LinkedHashMap<String, String>();
        keyValue.put("filename", filename);
        keyValue.put("hash", mfUploadItem.getFileData().getFileHash());
        keyValue.put("size", Long.toString(mfUploadItem.getFileData().getFileSize()));
        keyValue.put("response_format", "json");
        if (mfUploadItem.getUploadOptions().getUploadPath() != null && !mfUploadItem.getUploadOptions().getUploadPath().isEmpty()) {
            keyValue.put("path", mfUploadItem.getUploadOptions().getUploadPath());
        } else {
            keyValue.put("folder_key", mfUploadItem.getUploadOptions().getUploadFolderKey());
        }

        keyValue.put("action_on_duplicate", mfUploadItem.getUploadOptions().getActionOnDuplicate());
        return keyValue;
    }

    private Map<String, String> generateCheckUploadRequestParameters() {
        MFConfiguration.getStaticMFLogger().w(TAG, "generateCheckUploadRequestParameters()");
        // generate map with request parameters
        Map<String, String> keyValue = new LinkedHashMap<String, String>();
        keyValue.put("filename", utf8EncodedFileName);
        keyValue.put("hash", mfUploadItem.getFileData().getFileHash());
        keyValue.put("size", Long.toString(mfUploadItem.getFileData().getFileSize()));
        keyValue.put("resumable", mfUploadItem.getUploadOptions().getResumable());
        keyValue.put("response_format", "json");
        if (mfUploadItem.getUploadOptions().getUploadPath() != null && !mfUploadItem.getUploadOptions().getUploadPath().isEmpty()) {
            keyValue.put("path", mfUploadItem.getUploadOptions().getUploadPath());
        } else {
            keyValue.put("folder_key", mfUploadItem.getUploadOptions().getUploadFolderKey());
        }
        return keyValue;
    }

    private boolean haveStoredCredentials() {
        MFConfiguration.getStaticMFLogger().w(TAG, "haveStoredCredentials()");
        return !mfTokenFarm.getMFConfiguration().getMfCredentials().getCredentials().isEmpty();
    }

    private void startOrRestartUpload() throws IOException, NoSuchAlgorithmException {
        MFConfiguration.getStaticMFLogger().w(TAG, "startOrRestartUpload()");
        //don't add the item to the backlog queue if it is null or the path is null
        if (mfUploadItem == null) {
            MFConfiguration.getStaticMFLogger().w(TAG, "one or more required parameters are invalid, not adding item to queue");
            notifyUploadListenerCancelled();
            return;
        }

        if (mfUploadItem.getFileData() == null || mfUploadItem.getFileData().getFilePath() == null || mfUploadItem.getFileData().getFilePath().isEmpty() || mfUploadItem.getFileData().getFileHash().isEmpty() || mfUploadItem.getFileData().getFileSize() == 0) {
            MFConfiguration.getStaticMFLogger().w(TAG, "one or more required parameters are invalid, not adding item to queue");
            notifyUploadListenerCancelled();
            return;
        }

        if (mfUploadItem.getUploadAttemptCount() <= maxUploadAttempts) {
            doCheckUpload();
        } else {
            notifyUploadListenerCancelled();
        }
    }

    private void notifyUploadListenerStarted() {
        MFConfiguration.getStaticMFLogger().w(TAG, "notifyUploadListenerStarted()");
        if (mfUploadListener != null) {
            mfUploadListener.onStarted(mfUploadItem);
        }
    }

    private void notifyUploadListenerCompleted() {
        MFConfiguration.getStaticMFLogger().w(TAG, "notifyUploadListenerCompleted()");
        if (mfUploadListener != null) {
            mfUploadListener.onCompleted(mfUploadItem);
        }
    }

    private void notifyUploadListenerOnProgressUpdate(int totalChunks) {
        MFConfiguration.getStaticMFLogger().w(TAG, "notifyUploadListenerOnProgressUpdate()");
        if (mfUploadListener != null) {
            // give number of chunks/numChunks for onProgressUpdate
            int numUploaded = 0;
            for (int chunkNumber = 0; chunkNumber < totalChunks; chunkNumber++) {
                if (mfUploadItem.getBitmap().isUploaded(chunkNumber)) {
                    numUploaded++;
                }
            }
            MFConfiguration.getStaticMFLogger().w(TAG, numUploaded + "/" + totalChunks + " chunks uploaded");
            mfUploadListener.onProgressUpdate(mfUploadItem, numUploaded, totalChunks);
        }
    }

    private void notifyUploadListenerCancelled() {
        MFConfiguration.getStaticMFLogger().w(TAG, "notifyUploadListenerCancelled()");
        if (mfUploadListener != null) {
            mfUploadListener.onCancelled(mfUploadItem);
        }
    }

    public static class Builder {
        private static final int DEFAULT_MAX_POLLS = 60;
        private static final int DEFAULT_MILLISECONDS_BETWEEN_POLLS = 2000;
        private static final int DEFAULT_MAX_UPLOAD_ATTEMPTS = 3;

        private int maxPolls = DEFAULT_MAX_POLLS;
        private long millisecondsBetweenPolls = DEFAULT_MILLISECONDS_BETWEEN_POLLS;
        private int maxUploadAttempts = DEFAULT_MAX_UPLOAD_ATTEMPTS;
        private final MFTokenFarm mfTokenFarm;
        private final MFUploadItem mfUploadItem;
        private MFUploadListener mfUploadListener;

        public Builder(MFTokenFarm mfTokenFarm, MFUploadItem mfUploadItem) {
            if (mfTokenFarm == null) {
                throw new IllegalArgumentException("MFTokenFarm cannot be null");
            }

            if (!mfTokenFarm.isStarted()) {
                throw new IllegalStateException("MFTokenFarm must be started via startup()");
            }

            if (mfUploadItem == null) {
                throw new IllegalArgumentException("MFUploadItem cannot be null");
            }

            this.mfTokenFarm = mfTokenFarm;
            this.mfUploadItem = mfUploadItem;
        }

        public Builder maxPolls(int maxPolls) {
            if (maxPolls < 1) {
                throw new IllegalArgumentException("max polls cannot be less than 0");
            }
            this.maxPolls = maxPolls;
            return this;
        }

        public Builder millisecondsBetweenPolls(long millisecondsBetweenPolls) {
            if (millisecondsBetweenPolls < 0) {
                throw new IllegalArgumentException("time between polls cannot be less than 0");
            }
            this.millisecondsBetweenPolls = millisecondsBetweenPolls;
            return this;
        }

        public Builder maxUploadAttempts(int maxUploadAttempts) {
            if (maxUploadAttempts < 1) {
                throw new IllegalArgumentException("max upload attempts cannot be less than 1");
            }
            this.maxUploadAttempts = maxUploadAttempts;
            return this;
        }

        public Builder uploadListener(MFUploadListener mfUploadListener) {
            this.mfUploadListener = mfUploadListener;
            return this;
        }

        public MFUploadRunnable build() {
            return new MFUploadRunnable(this);
        }
    }
}
