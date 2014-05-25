package com.arkhive.components.uploadmanager.process;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.arkhive.components.api.upload.responses.CheckResponse;
import com.arkhive.components.sessionmanager.SessionManager;
import com.arkhive.components.uploadmanager.UploadRunnable;
import com.arkhive.components.uploadmanager.manager.UploadManager;
import com.arkhive.components.uploadmanager.uploaditem.UploadItem;
// CHECKSTYLE:OFF
import com.google.gson.Gson;
//CHECKSTYLE:ON
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**Runnable for making a call to upload/check.php.
 * @author Chris Najar
 */
public class CheckProcess implements UploadRunnable {
  private static final String TAG = CheckProcess.class.getSimpleName();
  private static final String CHECK_URI = "/api/upload/check.php";
  private SessionManager sessionManager;
  private UploadItem uploadItem;
  private Gson gson;
    private final UploadManager uploadManager;
  private Logger logger = LoggerFactory.getLogger(CheckProcess.class);

  public CheckProcess(SessionManager sessionManager, UploadManager uploadManager, UploadItem uploadItem) {
    this.sessionManager = sessionManager;
    this.uploadManager = uploadManager;
    this.uploadItem = uploadItem;
    this.gson = new Gson();
  }

    @Override
    public UploadItem getUploadItem() {
        return uploadItem;
    }

  @Override
  public void run() {
    check();
  }

  /**
   *  1. url encode filename.
   *  2. generate request parameters.
   *  3. create GET request.
   *  4. receive API response.
   *  5. convert response to CheckResponse using Gson.
   *  6. notify listeners of completion.
   */
  private void check() {
      logger.info(TAG + " check()");
    //notify listeners that check started
    notifyListenersStarted();

    // url encode the filename
    String filename;
    try {
      filename = URLEncoder.encode(uploadItem.getShortFileName(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      logger.warn(TAG + " Exception: " + e);
      e.printStackTrace();
      notifyManagerException(e);
      return;
    }

    // generate map with request parameters
    Map<String, String> keyValue =
        generateRequestParameters(filename);

    // generate request
    String request =
        sessionManager.getDomain() + sessionManager.getSession().getQueryString(CHECK_URI, keyValue);

    // receive response
    String jsonResponse =
        sessionManager.getHttpInterface().sendGetRequest(request);
    //check if we did not get a response (json response string is empty)
    if (jsonResponse.isEmpty()) {
      // notify listeners we received an empty json response.
      notifyManagerLostConnection();
      return;
    }

    // convert response to CheckResponse data structure
    CheckResponse response =
        gson.fromJson(getResponseString(jsonResponse), CheckResponse.class);

    // if there is an error code, cancel the upload
    if (response.hasError()) {
      switch (response.getErrorCode()) {
        case NO_ERROR: break;
        default:
          notifyManagerCancelled(response);
          return;
      }
    }

    // notify listeners that check has completed
    notifyListenersCompleted(response);
  }

  /**
   * generates the request parameter after we receive a UTF encoded filename.
   * @param filename - the name of hte file.
   * @return - a map of request paramaters.
   */
  private Map<String, String> generateRequestParameters(String filename) {
    // generate map with request parameters
    Map<String, String> keyValue = new HashMap<String, String>();
    keyValue.put("filename", filename);
    keyValue.put("hash", uploadItem.getFileData().getFileHash());
    keyValue.put("size", Long.toString(uploadItem.getFileData().getFileSize()));
    keyValue.put("resumable", uploadItem.getUploadOptions().isResumable());
    keyValue.put("response_format", "json");
      if (!uploadItem.getUploadOptions().getUploadPath().isEmpty()) {
          keyValue.put("path", uploadItem.getUploadOptions().getUploadPath());
      } else {
          keyValue.put("upload_folder_key", uploadItem.getUploadOptions().getUploadFolderKey());
      }
    return keyValue;
  }

  /**
   * notifies listeners that this process has completed.
   * @param checkResponse - the response from calling check.php.
   */
  private void notifyListenersCompleted(CheckResponse checkResponse) {
      logger.info(TAG + " notifyListenersCompleted()");
    //notify manager that check is completed
    if (uploadManager.getUploadManagerListener() != null) {
        uploadManager.getUploadManagerListener().onCheckCompleted(uploadItem, checkResponse);
    }
  }

  /**
   * lets listeners know that this process has started.
   */
  private void notifyListenersStarted() {
    // notify Ui listeners that task has started.
      if (uploadManager.getUiListener() != null) {
          uploadManager.getUiListener().onStarted(uploadItem);
    }
    //notify database lsitener task has started.
    if (uploadManager.getDatabaseListener() != null) {
        uploadManager.getDatabaseListener().onStarted(uploadItem);
    }
  }

  /**
   * lets listeners know that this process has been cancelled for the upload item.
   */
  private void notifyListenersCancelled() {
    //notify ui listeners that task has been cancelled
      if (uploadManager.getUiListener() != null) {
          uploadManager.getUiListener().onCancelled(uploadItem);
    }
    //notify database listener that task has been cancelled
    if (uploadManager.getDatabaseListener() != null) {
        uploadManager.getDatabaseListener().onCancelled(uploadItem);
    }
  }

  private void notifyManagerCancelled(CheckResponse response) {
    if (uploadManager.getUploadManagerListener() != null) {
        uploadManager.getUploadManagerListener().onCancelled(uploadItem, response);
    }

    notifyListenersCancelled();
  }

  /**
   * lets listeners know that this process has been cancelled for this upload item. manager is informed of exception.
   * @param e - exception that occurred.
   */
  private void notifyManagerException(Exception e) {
    //notify listeners that there has been an exception
    if (uploadManager.getUploadManagerListener() != null) {
        uploadManager.getUploadManagerListener().onProcessException(uploadItem, e);
    }

    notifyListenersCancelled();
  }

  /**
   * lets listeners know that this process has been cancelled for this item. manager is informed of lost connection.
   */
  private void notifyManagerLostConnection() {
    //notify listeners that connection was lost
    if (uploadManager.getUploadManagerListener() != null) {
        uploadManager.getUploadManagerListener().onLostConnection(uploadItem);
    }
    notifyListenersCancelled();
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
