package com.mediafire.uploader.process;

import com.mediafire.sdk.api_responses.upload.PollResponse;
import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.http.MFApi;
import com.mediafire.sdk.http.MFHost;
import com.mediafire.sdk.http.MFRequest;
import com.mediafire.sdk.http.MFResponse;
import com.mediafire.sdk.token.MFTokenFarm;
import com.mediafire.uploader.manager.UploadManagerWorker;
import com.mediafire.uploader.uploaditem.UploadItem;

import java.util.HashMap;

public class PollProcess extends UploadProcess {
    private static final String TAG = PollProcess.class.getCanonicalName();
    private static final long TIME_BETWEEN_POLLS = 2000;
    private static final int MAX_POLLS = 60;

    public PollProcess(MFTokenFarm mfTokenFarm, UploadManagerWorker uploadListenerManager, UploadItem uploadItem) {
        super(mfTokenFarm, uploadItem, uploadListenerManager);
    }

    @Override
    protected void doUploadProcess() {
        MFConfiguration.getStaticMFLogger().v(TAG, "doUploadProcess()");
        //generate our request string
        HashMap<String, String> keyValue = generateGetParameters();

        int pollCount = 0;
        do {
            // increment counter
            pollCount++;
            // get api response.

            MFRequest.MFRequestBuilder mfRequestBuilder = new MFRequest.MFRequestBuilder(MFHost.LIVE_HTTP, MFApi.UPLOAD_POLL_UPLOAD);
            mfRequestBuilder.requestParameters(keyValue);
            MFRequest mfRequest = mfRequestBuilder.build();
            MFResponse mfResponse = mfTokenFarm.getMFHttpRunner().doRequest(mfRequest);
            PollResponse response = mfResponse.getResponseObject(PollResponse.class);

            if (response == null) {
                notifyListenerLostConnection();
                return;
            }

            MFConfiguration.getStaticMFLogger().v(TAG, "received error code: " + response.getErrorCode());
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
                        MFConfiguration.getStaticMFLogger().v(TAG, "result code: " + response.getDoUpload().getResultCode().toString() + " need to cancel");
                        notifyListenerCancelled(response);
                        return;
                    }

                    if (response.getDoUpload().getFileErrorCode() != PollResponse.FileError.NO_ERROR) {
                        MFConfiguration.getStaticMFLogger().v(TAG, "result code: " + response.getDoUpload().getFileErrorCode().toString() + " need to cancel");
                        notifyListenerCancelled(response);
                        return;
                    }

                    if (response.getDoUpload().getStatusCode() == PollResponse.Status.NO_MORE_REQUESTS_FOR_THIS_KEY) {
                        MFConfiguration.getStaticMFLogger().v(TAG, "status code: " + response.getDoUpload().getStatusCode().toString());
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
                MFConfiguration.getStaticMFLogger().v(TAG, "Exception: " + e);
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
        MFConfiguration.getStaticMFLogger().v(TAG, "generateGetParameters()");
        HashMap<String, String> keyValue = new HashMap<String, String>();
        keyValue.put("key", uploadItem.getPollUploadKey());
        keyValue.put("response_format", "json");
        return keyValue;
    }

}
