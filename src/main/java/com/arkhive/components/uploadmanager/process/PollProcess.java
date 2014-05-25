package com.arkhive.components.uploadmanager.process;

import com.arkhive.components.uploadmanager.UploadRunnable;
import com.arkhive.components.uploadmanager.manager.UploadManager;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

import com.arkhive.components.api.upload.errors.PollFileErrorCode;
import com.arkhive.components.api.upload.errors.PollResultCode;
import com.arkhive.components.api.upload.errors.PollStatusCode;
import com.arkhive.components.api.upload.responses.PollResponse;
import com.arkhive.components.sessionmanager.SessionManager;
import com.arkhive.components.uploadmanager.uploaditem.UploadItem;

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
public class PollProcess implements UploadRunnable {
  private static final String POLL_UPLOAD_URI = "/api/upload/poll_upload.php";
  private SessionManager sessionManager;
  private UploadItem uploadItem;
  private Gson gson;
  private long sleepTime;
  private int maxLoopAttempts;
    private UploadManager uploadManager;
  
  /**
   * Constructor for an upload with a listener. This constructor uses sleepTime for the loop sleep time with 
   * loopAttempts for the loop attempts.
   * @param sessionManager - the session to use for this upload process
   * @param uploadItem - the item to be uploaded
   * @param sleepTime - milliseconds to wait between polls
   * @param maxLoopAttempts - max number of polls
   */
  public PollProcess(SessionManager sessionManager, UploadManager uploadManager, UploadItem uploadItem, long sleepTime, int maxLoopAttempts) {
    this.sessionManager = sessionManager;
      this.uploadManager = uploadManager;
    this.uploadItem = uploadItem;
    this.gson = new Gson();
    this.sleepTime = sleepTime;
    this.maxLoopAttempts = maxLoopAttempts;
  }
  
  /**
   * Constructor for an upload with a listener. This constructor uses 2000ms for the loop sleep time with 60 attempts
   * for the loop attempts
   * @param sessionManager - the session to use for this upload process
   * @param uploadItem - the item to be uploaded
   */
  public PollProcess(SessionManager sessionManager, UploadManager uploadManager, UploadItem uploadItem) {
    this(sessionManager, uploadManager, uploadItem, 2000, 60);
  }

    @Override
    public UploadItem getUploadItem() {
        return uploadItem;
    }

  @Override
  public void run() {
    pollUpload();
  }

  /**
   * start the poll upload process with a maximum of 2 minutes of polling with the following process:
   * 1. create GET request
   * 2. send GET request
   * 3. get response
   * 4. check response data
   * 5. step 1 again until 2 minutes is up, there is an error, or status code 99 (no more requests for this key)
   */
  private void pollUpload() {
    //generate our request string
    HashMap<String, String> keyValue = generateGetParameters();
    
    //generate request
    String request = 
        sessionManager.getDomain() + getQueryString(POLL_UPLOAD_URI, keyValue);
      
      //loop until we have made 60 attempts (2 minutes)
      int pollCount = 0;
      PollResponse response;
      do {
        //increment counter
        pollCount++;
        //send the get request and receive the json response
        String jsonResponse = 
            sessionManager.getHttpInterface().sendGetRequest(request);
        
        //if jsonResponse is empty, then HttpInterface.sendGetRequest() has no internet connectivity so we
        //call lostInternetConnectivity() and UploadManager will move this item to the backlog queue.
        if (jsonResponse.isEmpty()) {
          notifyManagerLostConnection();
          return;
        }
        
        //create the pollupload response from the String we received from the get request sent by our httpinterface
        response = 
            gson.fromJson(getResponseString(jsonResponse), PollResponse.class);
        
        //check to see if we need to call pollUploadCompleted or loop again
        switch(response.getErrorCode()) {
          case NO_ERROR:
            //just because we had response/result "Success" doesn't mean everything is good. 
            //we need to find out if we should continue polling or not
            //  conditions to check:
            //      first   -   result code no error? yes, keep calm and poll on. no, cancel upload because error.
            //      second  -   fileerror code no error? yes, carry on old chap!. no, cancel upload because error.
            //      third   -   status code 99 (no more requests)? yes, weee! done!. no, continue.
            if (response.getDoUpload().getResultCode() != PollResultCode.SUCCESS || 
            response.getDoUpload().getFileErrorCode() != PollFileErrorCode.NO_ERROR) {
              //cancel upload because of a doupload/fileerrorcode or a bad doupload/resultcode
              notifyManagerCancelled(response);
              return;
            }
            
            if (response.getDoUpload().getStatusCode() == PollStatusCode.NO_MORE_REQUESTS_FOR_THIS_KEY) {
              //done uploading this file.
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
          Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
          e.printStackTrace();
          Thread.currentThread().interrupt();
        }
        
      } while (pollCount < maxLoopAttempts); 
      
      // we exceeded our attempts. inform listener that the upload is cancelled. in this case it is because
      // we ran out of attempts.
      notifyManagerCancelled(response);
  }
  
  /**
   * notifies the listeners that this upload has successfully completed.
   * @param response - poll response.
   */
  public void notifyManagerCompleted(PollResponse response) {
    if (uploadManager.getUploadManagerListener() != null) {
        uploadManager.getUploadManagerListener().onPollCompleted(uploadItem, response);
    } 
    notifyListenersCompleted();
  }
  
  /**
   * notifies listeners that this process has completed successfully.
   */
  public void notifyListenersCompleted() {
    // notify ui listeners that the upload has completed
      if (uploadManager.getUiListener() != null) {
          //allocated the last % to this process.
          uploadManager.getUiListener().onCompleted(uploadItem);
      }

    // notify database listener that the upload has completed
    if (uploadManager.getDatabaseListener() != null) {
        uploadManager.getDatabaseListener().onCompleted(uploadItem);
    }
  }
  
  /**
   * notifies the upload manager that the process has been cancelled and then notifies other listeners.
   * @param response - poll response.
   */
  private void notifyManagerCancelled(PollResponse response) {
    if (uploadManager.getUploadManagerListener() != null) {
        uploadManager.getUploadManagerListener().onCancelled(uploadItem, response);
    }
    notifyListenersCancelled();
  }
  
  /**
   * generates a HashMap of the GET parameters.
   * @return - map of request parameters.
   */
  private HashMap<String, String> generateGetParameters() {
    HashMap<String, String> keyValue = new HashMap<String, String>();
    keyValue.put("key", uploadItem.getPollUploadKey());
    keyValue.put("response_format", "json");
    return keyValue;
  }
  
  /**
   * lets listeners know that this process has been cancelled for this item. manager is informed of lost connection.
   */
  private void notifyManagerLostConnection() {
    // notify listeners that connection was lost
    if (uploadManager.getUploadManagerListener() != null) {
        uploadManager.getUploadManagerListener().onLostConnection(uploadItem);
    }
    notifyListenersCancelled();
  }
  
  /**
   * lets listeners know that this process has been cancelled for the upload item.
   */
  private void notifyListenersCancelled() {
    // notify ui listeners that task has been cancelled
    if (uploadManager.getUiListener() != null) {
        uploadManager.getUiListener().onCancelled(uploadItem);
    }
    // notify database listener that task has been cancelled
    if (uploadManager.getDatabaseListener() != null) {
        uploadManager.getDatabaseListener().onCancelled(uploadItem);
    }
  }
  
  public String getQueryString(String uri, Map<String, String> keyValue) {
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
