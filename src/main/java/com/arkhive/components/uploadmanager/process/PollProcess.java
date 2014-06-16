package com.arkhive.components.uploadmanager.process;

import com.arkhive.components.uploadmanager.listeners.UploadListenerManager;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.arkhive.components.api.upload.errors.PollFileErrorCode;
import com.arkhive.components.api.upload.errors.PollResultCode;
import com.arkhive.components.api.upload.errors.PollStatusCode;
import com.arkhive.components.api.upload.responses.PollResponse;
import com.arkhive.components.sessionmanager.SessionManager;
import com.arkhive.components.uploadmanager.uploaditem.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the Runnable which executes the call /api/upload/poll_upload.
 * The process is as follows:
 * 1. create GET request
 * 2. send GET request
 * 3. get response
 * 4. check response data
 * 5. step 1 again until 2 minutes is up, there is an error, or status code 99 (no more requests for this key)
 * @author Chris Najar
 *
 */
public class PollProcess implements Runnable {
    private static final String TAG = PollProcess.class.getSimpleName();
    private static final String POLL_UPLOAD_URI = "/api/upload/poll_upload.php";
    private static final long TIME_BETWEEN_POLLS = 10000;
    private static final int MAX_POLLS = 12;
    private final SessionManager sessionManager;
    private final UploadItem uploadItem;
    private final UploadListenerManager uploadManager;
    private final Logger logger = LoggerFactory.getLogger(PollProcess.class);

    /**
     * Constructor for an upload with a listener. This constructor uses sleepTime for the loop sleep time with
     * loopAttempts for the loop attempts.
     * @param sessionManager - the session to use for this upload process
     * @param uploadItem - the item to be uploaded
     */
    public PollProcess(SessionManager sessionManager, UploadListenerManager uploadManager, UploadItem uploadItem) {
        this.sessionManager = sessionManager;
        this.uploadManager = uploadManager;
        this.uploadItem = uploadItem;
    }

    @Override
    public void run() {
        System.out.println("run()");
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

        //generate request
        String request = sessionManager.getDomain() + getQueryString(POLL_UPLOAD_URI, keyValue);

        //loop until we have made 60 attempts (2 minutes)
        int pollCount = 0;
        PollResponse response;
        do {
            //increment counter
            pollCount++;
            //send the get request and receive the json response
            String jsonResponse;
            try {
                jsonResponse =
                        sessionManager.getHttpInterface().sendGetRequest(request);
            } catch (IOException e) {
                notifyListenersException(uploadItem, e);
                return;
            }

            //if jsonResponse is empty, then HttpInterface.sendGetRequest() has no internet connectivity so we
            //call lostInternetConnectivity() and UploadManager will move this item to the backlog queue.
            if (jsonResponse.isEmpty()) {
                notifyManagerLostConnection();
                return;
            }

            Gson gson = new Gson();
            //create the pollupload response from the String we received from the get request sent by our httpinterface
            response = gson.fromJson(getResponseString(jsonResponse), PollResponse.class);

            System.out.println("received error code: " + response.getErrorCode());
            //check to see if we need to call pollUploadCompleted or loop again
            switch(response.getErrorCode()) {
                case NO_ERROR:
                    //just because we had response/result "Success" doesn't mean everything is good.
                    //we need to find out if we should continue polling or not
                    //  conditions to check:
                    //      first   -   result code no error? yes, keep calm and poll on. no, cancel upload because error.
                    //      second  -   fileerror code no error? yes, carry on old chap!. no, cancel upload because error.
                    //      third   -   status code 99 (no more requests)? yes, weee! done!. no, continue.
                    if (response.getDoUpload().getResultCode() != PollResultCode.SUCCESS) {
                        System.out.println("result code: " + response.getDoUpload().getResultCode().toString() + " need to cancel");
                        notifyManagerCancelled(response);
                        return;
                    }

                    if (response.getDoUpload().getFileErrorCode() != PollFileErrorCode.NO_ERROR) {
                        System.out.println("result code: " + response.getDoUpload().getFileErrorCode().toString() + " need to cancel");
                        System.out.println("file path: " + uploadItem.getFileData().getFilePath());
                        System.out.println("file hash: " + uploadItem.getFileData().getFileHash());
                        System.out.println("file size: " + uploadItem.getFileData().getFileSize());
                        notifyManagerCancelled(response);
                        return;
                    }

                    if (response.getDoUpload().getStatusCode() == PollStatusCode.NO_MORE_REQUESTS_FOR_THIS_KEY) {
                        System.out.println("status code: " + response.getDoUpload().getStatusCode().toString() + " we are done");
                        notifyManagerCompleted(response);
                        return;
                    }
                    break;
                default:
                    // stop polling and inform listeners we cancel because API result wasn't "Success"
                    notifyManagerCancelled(response);
                    return;
            }

            //wait 2 seconds before next api call
            try {
                Thread.sleep(TIME_BETWEEN_POLLS);
            } catch (InterruptedException e) {
                System.out.println("Exception: " + e);
                notifyManagerCompleted(response);
                Thread.currentThread().interrupt();
            }

        } while (pollCount < MAX_POLLS);

        // we exceeded our attempts. inform listener that the upload is cancelled. in this case it is because
        // we ran out of attempts.
        notifyManagerCompleted(response);
    }

    public void notifyListenersException(UploadItem uploadItem, Exception exception) {
        System.out.println("notifyListenersException()");
        if (uploadManager != null) {
            uploadManager.onProcessException(uploadItem, exception);
        }
    }

    /**
     * notifies the listeners that this upload has successfully completed.
     * @param response - poll response.
     */
    public void notifyManagerCompleted(PollResponse response) {
        System.out.println("notifyManagerCompleted()");
        if (uploadManager != null) {
            uploadManager.onPollCompleted(uploadItem, response);
        }
    }

    /**
     * notifies the upload manager that the process has been cancelled and then notifies other listeners.
     * @param response - poll response.
     */
    private void notifyManagerCancelled(PollResponse response) {
        System.out.println("notifyManagerCancelled()");
        if (uploadManager != null) {
            uploadManager.onCancelled(uploadItem, response);
        }
    }

    /**
     * generates a HashMap of the GET parameters.
     * @return - map of request parameters.
     */
    private HashMap<String, String> generateGetParameters() {
        System.out.println("generateGetParameters()");
        HashMap<String, String> keyValue = new HashMap<String, String>();
        keyValue.put("key", uploadItem.getPollUploadKey());
        keyValue.put("response_format", "json");
        return keyValue;
    }

    /**
     * lets listeners know that this process has been cancelled for this item. manager is informed of lost connection.
     */
    private void notifyManagerLostConnection() {
        System.out.println("notifyManagerLostConnection()");
        // notify listeners that connection was lost
        if (uploadManager != null) {
            uploadManager.onLostConnection(uploadItem);
        }
    }

    private String getQueryString(String uri, Map<String, String> keyValue) {
        StringBuilder str = new StringBuilder();
        String builtQuery;
        str.append("?");

        for (Map.Entry<String, String> e : keyValue.entrySet()) {
            str.append(e.getKey()).append("=").append(e.getValue()).append("&");
        }

        builtQuery = str.toString();
        builtQuery = uri + builtQuery.substring(0, builtQuery.length() - 1);
        return builtQuery;
    }

    /**
     * converts a String received from JSON format into a response String.
     * @param response - the response received in JSON format
     * @return the response received which can then be parsed into a specific format as per Gson.fromJson()
     */
    private String getResponseString(String response) {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(response);
        if (element.isJsonObject()) {
            JsonObject jsonResponse = element.getAsJsonObject().get("response").getAsJsonObject();
            return jsonResponse.toString();
        } else {
            return "";
        }
    }
}
