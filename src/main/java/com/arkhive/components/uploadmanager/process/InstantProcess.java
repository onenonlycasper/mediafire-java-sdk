package com.arkhive.components.uploadmanager.process;

import com.arkhive.components.core.MediaFire;
import com.arkhive.components.core.module_api.codes.ApiResponseCode;
import com.arkhive.components.core.module_api.responses.UploadInstantResponse;
import com.arkhive.components.uploadmanager.listeners.UploadListenerManager;
import com.arkhive.components.uploadmanager.uploaditem.UploadItem;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

//CHECKSTYLE:OFF
//CHECKSTYLE:ON

/**
 * Runnable for making a call to upload/instant.php.
 *
 * @author
 */
public class InstantProcess implements Runnable {
    private static final String TAG = InstantProcess.class.getSimpleName();
    private final MediaFire mediaFire;
    private final UploadItem uploadItem;
    private final UploadListenerManager uploadManager;
    private final Logger logger = LoggerFactory.getLogger(InstantProcess.class);

    public InstantProcess(MediaFire mediaFire, UploadListenerManager uploadManager, UploadItem uploadItem) {
        this.mediaFire = mediaFire;
        this.uploadManager = uploadManager;
        this.uploadItem = uploadItem;
    }

    @Override
    public void run() {
        System.out.println(TAG + " sendRequest()");
        instant();
    }

    /**
     * 1. url encode filename.
     * 2. generate request parameters.
     * 3. create GET request.
     * 4. receive API response.
     * 5. convert response to CheckResponse using Gson.
     * 6. notify listeners of completion.
     */
    private void instant() {
        logger.info("instant called");
        Thread.currentThread().setPriority(3); //uploads are set to low priority
        // url encode the filename
        String filename;
        try {
            filename = URLEncoder.encode(uploadItem.getFileName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println(TAG + " Exception: " + e);
            e.printStackTrace();
            notifyManagerException(e);
            return;
        }

        // generate map with request parameters
        Map<String, String> keyValue = generateRequestParameters(filename);
        UploadInstantResponse response = mediaFire.apiCall().upload.instantUpload(keyValue, null);


        if (response.getErrorCode() != ApiResponseCode.NO_ERROR) {
            notifyManagerCancelled(response);
            return;
        }

        if (!response.getQuickkey().isEmpty()) {
            // notify listeners that check has completed
            notifyManagerCompleted();
        } else {
            notifyManagerCancelled(response);
        }
    }

    /**
     * cancels this upload because of an api error.
     *
     * @param response - response from calling instant.php.
     */
    private void notifyManagerCancelled(UploadInstantResponse response) {
        if (uploadManager != null) {
            uploadManager.onCancelled(uploadItem, response);
        }
    }

    /**
     * generates the request parameter after we receive a UTF encoded filename.
     *
     * @param filename - the filename used to construct request parameter.
     * @return - a map containing the request parameter.
     */
    private Map<String, String> generateRequestParameters(String filename) {
        // generate map with request parameters
        Map<String, String> keyValue = new HashMap<String, String>();
        keyValue.put("filename", filename);
        keyValue.put("hash", uploadItem.getFileData().getFileHash());
        keyValue.put("size", Long.toString(uploadItem.getFileData().getFileSize()));
        keyValue.put("mtime", uploadItem.getUploadOptions().getModificationTime());
        keyValue.put("response_format", "json");
        if (!uploadItem.getUploadOptions().getUploadPath().isEmpty()) {
            keyValue.put("path", uploadItem.getUploadOptions().getUploadPath());
        } else {
            keyValue.put("folder_key", uploadItem.getUploadOptions().getUploadFolderKey());
        }

        keyValue.put("action_on_duplicate", uploadItem.getUploadOptions().getActionOnDuplicate());
        return keyValue;
    }

    /**
     * notifies listeners that this process has completed successfully.
     */
    private void notifyManagerCompleted() {
        //notify manager that the upload is completed
        if (uploadManager != null) {
            uploadManager.onInstantCompleted(uploadItem);
        }
    }


    /**
     * lets listeners know that this process has been cancelled for this upload item. manager is informed of exception.
     *
     * @param e - the exception that occurred
     */
    private void notifyManagerException(Exception e) {
        //notify listeners that there has been an exception
        if (uploadManager != null) {
            uploadManager.onProcessException(uploadItem, e);
        }
    }

    /**
     * lets listeners know that this process has been cancelled for this item. manager is informed of lost connection.
     */
    private void notifyManagerLostConnection() {
        //notify listeners that connection was lost
        if (uploadManager != null) {
            uploadManager.onLostConnection(uploadItem);
        }
    }

    /**
     * converts a String received from JSON format into a response String.
     *
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
