package com.arkhive.components.uploadmanager.process;

import com.arkhive.components.core.MediaFire;
import com.arkhive.components.core.module_api.codes.PollFileErrorCode;
import com.arkhive.components.core.module_api.codes.PollResultCode;
import com.arkhive.components.core.module_api.codes.PollStatusCode;
import com.arkhive.components.core.module_api.responses.UploadPollResponse;
import com.arkhive.components.uploadmanager.listeners.UploadListenerManager;
import com.arkhive.components.uploadmanager.uploaditem.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class PollProcess implements Runnable {
    private static final long TIME_BETWEEN_POLLS = 2000;
    private static final int MAX_POLLS = 60;
    private final MediaFire mediaFire;
    private final UploadItem uploadItem;
    private final UploadListenerManager uploadManager;
    private final Logger logger = LoggerFactory.getLogger(PollProcess.class);

    /**
     * Constructor for an upload with a listener. This constructor uses sleepTime for the loop sleep time with
     * loopAttempts for the loop attempts.
     *
     * @param mediaFire - the session to use for this upload process
     * @param uploadItem     - the item to be uploaded
     */
    public PollProcess(MediaFire mediaFire, UploadListenerManager uploadManager, UploadItem uploadItem) {
        this.mediaFire = mediaFire;
        this.uploadManager = uploadManager;
        this.uploadItem = uploadItem;
    }

    @Override
    public void run() {
        logger.info(" sendRequest()");
        poll();
    }

    /**
     * start the poll upload process with a maximum of 2 minutes of polling with the following process:
     * 1. create GET request
     * 2. send GET request
     * 3. get response
     * 4. check response data
     * 5. step 1 again until 2 minutes is up, there is an error, or status code 99 (no more requests for this key)
     */
    private void poll() {
        Thread.currentThread().setPriority(3); //uploads are set to low priority
        //generate our request string
        HashMap<String, String> keyValue = generateGetParameters();

        int pollCount = 0;
        do {
            // increment counter
            pollCount++;
            // get api response.
            UploadPollResponse response = mediaFire.apiCall().upload.pollUpload(keyValue, null);

            if (response == null) {
                notifyManagerLostConnection();
                return;
            }

            logger.info(" received error code: " + response.getErrorCode());
            //check to see if we need to call pollUploadCompleted or loop again
            switch (response.getErrorCode()) {
                case NO_ERROR:
                    //just because we had response/result "Success" doesn't mean everything is good.
                    //we need to find out if we should continue polling or not
                    //  conditions to check:
                    //      first   -   result code no error? yes, keep calm and poll on. no, cancel upload because error.
                    //      second  -   fileerror code no error? yes, carry on old chap!. no, cancel upload because error.
                    //      third   -   status code 99 (no more requests)? yes, done. no, continue.
                    if (response.getDoUpload().getResultCode() != PollResultCode.SUCCESS) {
                        logger.info(" result code: " + response.getDoUpload().getResultCode().toString() + " need to cancel");
                        notifyManagerCancelled(response);
                        return;
                    }

                    if (response.getDoUpload().getFileErrorCode() != PollFileErrorCode.NO_ERROR) {
                        logger.info(" result code: " + response.getDoUpload().getFileErrorCode().toString() + " need to cancel");
                        logger.info(" file path: " + uploadItem.getFileData().getFilePath());
                        logger.info(" file hash: " + uploadItem.getFileData().getFileHash());
                        logger.info(" file size: " + uploadItem.getFileData().getFileSize());
                        notifyManagerCancelled(response);
                        return;
                    }

                    if (response.getDoUpload().getStatusCode() == PollStatusCode.NO_MORE_REQUESTS_FOR_THIS_KEY) {
                        logger.info(" status code: " + response.getDoUpload().getStatusCode().toString() + " we are done");
                        notifyManagerCompleted(response);
                        return;
                    }
                    break;
                default:
                    // stop polling and inform listeners we cancel because API result wasn't "Success"
                    notifyManagerCancelled(response);
                    return;
            }

            //wait before next api call
            try {
                Thread.sleep(TIME_BETWEEN_POLLS);
            } catch (InterruptedException e) {
                logger.info(" Exception: " + e);
                notifyManagerException(uploadItem, e);
                return;
            }

            if (uploadItem.isCancelled()) {
                pollCount = MAX_POLLS;
            }

            if (pollCount >= MAX_POLLS) {
                // we exceeded our attempts. inform listener that the upload is cancelled. in this case it is because
                // we ran out of attempts.
                notifyManagerCompleted(response);
            }
        } while (pollCount < MAX_POLLS);
    }

    public void notifyManagerException(UploadItem uploadItem, Exception exception) {
        logger.info(" notifyManagerException()");
        if (uploadManager != null) {
            uploadManager.onProcessException(uploadItem, exception);
        }
    }

    public void notifyManagerCompleted(UploadPollResponse response) {
        logger.info(" notifyManagerCompleted()");
        if (uploadManager != null) {
            uploadManager.onPollCompleted(uploadItem, response);
        }
    }

    private void notifyManagerCancelled(UploadPollResponse response) {
        logger.info(" notifyManagerCancelled()");
        if (uploadManager != null) {
            uploadManager.onCancelled(uploadItem, response);
        }
    }

    private void notifyManagerLostConnection() {
        logger.info(" notifyManagerLostConnection()");
        // notify listeners that connection was lost
        if (uploadManager != null) {
            uploadManager.onLostConnection(uploadItem);
        }
    }

    private HashMap<String, String> generateGetParameters() {
        logger.info(" generateGetParameters()");
        HashMap<String, String> keyValue = new HashMap<String, String>();
        keyValue.put("key", uploadItem.getPollUploadKey());
        keyValue.put("response_format", "json");
        return keyValue;
    }

}
