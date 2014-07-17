package com.mediafire.uploader.process;

import com.mediafire.sdk.api_responses.upload.PollResponse;
import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.http.*;
import com.mediafire.sdk.token.MFTokenFarm;
import com.mediafire.uploader.interfaces.UploadListenerManager;
import com.mediafire.uploader.uploaditem.UploadItem;

import java.util.HashMap;

/**
 * This is the Runnable which executes the call /api/upload/poll_upload.
 * The process is as follows:
 * 1. create GET request
 * 2. send GET request
 * 3. get response
 * 4. check response data
 * 5. step 1 again until 2 minutes is up, there is an error, or status code 99 (no more requests for this key)
 *
 * @author
 */
public class PollProcess extends UploadProcess {
    private static final String TAG = PollProcess.class.getCanonicalName();
    private static final long TIME_BETWEEN_POLLS = 2000;
    private static final int MAX_POLLS = 60;

    /**
     * Constructor for an upload with a listener. This constructor uses sleepTime for the loop sleep time with
     * loopAttempts for the loop attempts.
     *
     * @param mfTokenFarm - the session to use for this upload process
     * @param uploadItem     - the item to be uploaded
     */
    public PollProcess(MFTokenFarm mfTokenFarm, UploadListenerManager uploadListenerManager, UploadItem uploadItem) {
        super(mfTokenFarm, uploadItem, uploadListenerManager);
    }

    @Override
    protected void doUploadProcess() {
        MFConfiguration.getStaticMFLogger().logMessage(TAG, "doUploadProcess()");
        //generate our request string
        HashMap<String, String> keyValue = generateGetParameters();

        int pollCount = 0;
        do {
            // increment counter
            pollCount++;
            // get api response.

            MFRequest mfRequest = new MFRequest(MFHost.LIVE_HTTP, MFApi.UPLOAD_POLL_UPLOAD, keyValue);
            MFHttpRunner.RunnerHolder runnerHolder = mfTokenFarm.getMfHttpRunner().doRequest(mfRequest);
            MFResponse receivedMFResponse = runnerHolder.getMfResponse();
            PollResponse response = receivedMFResponse.getResponseObject(PollResponse.class);

            if (response == null) {
                notifyListenerLostConnection();
                return;
            }

            MFConfiguration.getStaticMFLogger().logMessage(TAG, "received error code: " + response.getErrorCode());
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
                        MFConfiguration.getStaticMFLogger().logMessage(TAG, "result code: " + response.getDoUpload().getResultCode().toString() + " need to cancel");
                        notifyListenerCancelled(response);
                        return;
                    }

                    if (response.getDoUpload().getFileErrorCode() != PollResponse.FileError.NO_ERROR) {
                        MFConfiguration.getStaticMFLogger().logMessage(TAG, "result code: " + response.getDoUpload().getFileErrorCode().toString() + " need to cancel");
                        MFConfiguration.getStaticMFLogger().logMessage(TAG, "file path: " + uploadItem.getFileData().getFilePath());
                        MFConfiguration.getStaticMFLogger().logMessage(TAG, "file hash: " + uploadItem.getFileData().getFileHash());
                        MFConfiguration.getStaticMFLogger().logMessage(TAG, "file size: " + uploadItem.getFileData().getFileSize());
                        notifyListenerCancelled(response);
                        return;
                    }

                    if (response.getDoUpload().getStatusCode() == PollResponse.Status.NO_MORE_REQUESTS_FOR_THIS_KEY) {
                        MFConfiguration.getStaticMFLogger().logMessage(TAG, "status code: " + response.getDoUpload().getStatusCode().toString() + " we are done");
                        notifyListenerCompleted(response);
                        return;
                    }
                    break;
                default:
                    // stop polling and inform listeners we cancel because API result wasn't "Success"
                    notifyListenerCancelled(response);
                    return;
            }

            //wait before next api call
            try {
                Thread.sleep(TIME_BETWEEN_POLLS);
            } catch (InterruptedException e) {
                MFConfiguration.getStaticMFLogger().logMessage(TAG, "Exception: " + e);
                notifyListenerException(e);
                return;
            }

            if (uploadItem.isCancelled()) {
                pollCount = MAX_POLLS;
            }

            if (pollCount >= MAX_POLLS) {
                // we exceeded our attempts. inform listener that the upload is cancelled. in this case it is because
                // we ran out of attempts.
                notifyListenerCompleted(response);
            }
        } while (pollCount < MAX_POLLS);
    }

    private HashMap<String, String> generateGetParameters() {
        MFConfiguration.getStaticMFLogger().logMessage(TAG, "generateGetParameters()");
        HashMap<String, String> keyValue = new HashMap<String, String>();
        keyValue.put("key", uploadItem.getPollUploadKey());
        keyValue.put("response_format", "json");
        return keyValue;
    }

}
