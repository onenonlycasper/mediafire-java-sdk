package com.arkhive.components.uploadmanager.process;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.MediaFire;
import com.arkhive.components.core.module_api.codes.ApiResponseCode;
import com.arkhive.components.core.module_api.responses.UploadInstantResponse;
import com.arkhive.components.uploadmanager.interfaces.UploadListenerManager;
import com.arkhive.components.uploadmanager.uploaditem.UploadItem;

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
public class InstantProcess extends UploadProcess {

    private static final String TAG = InstantProcess.class.getCanonicalName();

    public InstantProcess(MediaFire mediaFire, UploadListenerManager uploadListenerManager, UploadItem uploadItem) {
        super(mediaFire, uploadItem, uploadListenerManager);
    }

    @Override
    protected void doUploadProcess() {
        Configuration.getErrorTracker().i(TAG, "doUploadProcess()");
        Thread.currentThread().setPriority(3); //uploads are set to low priority
        // url encode the filename
        String filename;
        try {
            filename = URLEncoder.encode(uploadItem.getFileName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Configuration.getErrorTracker().i(TAG, "Exception: " + e);
            notifyListenerException(e);
            return;
        }

        // generate map with request parameters
        Map<String, String> keyValue = generateRequestParameters(filename);
        UploadInstantResponse response = mediaFire.apiCall().upload.instantUpload(keyValue, null);

        if (response == null) {
            notifyListenerLostConnection();
            return;
        }

        if (response.getErrorCode() != ApiResponseCode.NO_ERROR) {
            notifyListenerCancelled(response);
            return;
        }

        if (!response.getQuickkey().isEmpty()) {
            // notify listeners that check has completed
            notifyListenerCompleted(response);
        } else {
            notifyListenerCancelled(response);
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
        keyValue.put("response_format", "json");
        if (!uploadItem.getUploadOptions().getUploadPath().isEmpty()) {
            keyValue.put("path", uploadItem.getUploadOptions().getUploadPath());
        } else {
            keyValue.put("folder_key", uploadItem.getUploadOptions().getUploadFolderKey());
        }

        keyValue.put("action_on_duplicate", uploadItem.getUploadOptions().getActionOnDuplicate());
        return keyValue;
    }
}
