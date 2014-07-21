package com.mediafire.uploader.process;

import com.mediafire.sdk.api_responses.ApiResponse;
import com.mediafire.sdk.api_responses.upload.InstantResponse;
import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.http.*;
import com.mediafire.sdk.token.MFTokenFarm;
import com.mediafire.uploader.manager.UploadManager;
import com.mediafire.uploader.uploaditem.UploadItem;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class InstantProcess extends UploadProcess {
    private static final String TAG = InstantProcess.class.getCanonicalName();

    public InstantProcess(MFTokenFarm mfTokenFarm, UploadManager uploadManager, UploadItem uploadItem) {
        super(mfTokenFarm, uploadItem, uploadManager);
    }

    @Override
    protected void doUploadProcess() {
        MFConfiguration.getStaticMFLogger().v(TAG, "doUploadProcess()");
        // url encode the filename
        String filename;
        try {
            filename = URLEncoder.encode(uploadItem.getFileName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            MFConfiguration.getStaticMFLogger().v(TAG, "Exception: " + e);
            notifyListenerException(e);
            return;
        }

        // generate map with request parameters
        Map<String, String> keyValue = generateRequestParameters(filename);
        MFRequest.MFRequestBuilder mfRequestBuilder = new MFRequest.MFRequestBuilder(MFHost.LIVE_HTTP, MFApi.UPLOAD_INSTANT);
        mfRequestBuilder.requestParameters(keyValue);
        MFRequest mfRequest = mfRequestBuilder.build();

        MFResponse mfResponse = mfTokenFarm.getMFHttpRunner().doRequest(mfRequest);
        InstantResponse response = mfResponse.getResponseObject(InstantResponse.class);

        if (response == null) {
            notifyListenerLostConnection();
            return;
        }

        if (response.getErrorCode() != ApiResponse.ResponseCode.NO_ERROR) {
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
