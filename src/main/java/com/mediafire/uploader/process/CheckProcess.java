package com.mediafire.uploader.process;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.MediaFire;
import com.arkhive.components.core.module_api.codes.ApiResponseCode;
import com.arkhive.components.core.module_api.responses.UploadCheckResponse;
import com.mediafire.uploader.interfaces.UploadListenerManager;
import com.mediafire.uploader.uploaditem.ResumableBitmap;
import com.mediafire.uploader.uploaditem.UploadItem;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckProcess extends UploadProcess {

    private static final String TAG = CheckProcess.class.getCanonicalName();

    public CheckProcess(MediaFire mediaFire, UploadListenerManager uploadListenerManager, UploadItem uploadItem) {
        super(mediaFire, uploadItem, uploadListenerManager);
    }

    @Override
    protected void doUploadProcess() {
        Configuration.getErrorTracker().i(TAG, "doUploadProcess()");
        uploadItem.getFileData().setFileSize();
        uploadItem.getFileData().setFileHash();
        //notify listeners that check started
        notifyListenerUploadStarted();

        // url encode the filename
        String filename;
        try {
            filename = URLEncoder.encode(uploadItem.getFileName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Configuration.getErrorTracker().i(TAG, "Exception: " + e);
            e.printStackTrace();
            notifyListenerException(e);
            return;
        }

        // generate map with request parameters
        Map<String, String> keyValue = generateRequestParameters(filename);
        UploadCheckResponse response = mediaFire.apiCall().upload.checkUpload(keyValue, null);

        if (response == null) {
            notifyListenerLostConnection();
            return;
        }

        // if there is an error code, cancel the upload
        if (response.getErrorCode() != ApiResponseCode.NO_ERROR) {
            notifyListenerCancelled(response);
            return;
        }

        uploadItem.getChunkData().setNumberOfUnits(response.getResumableUpload().getNumberOfUnits());
        uploadItem.getChunkData().setUnitSize(response.getResumableUpload().getUnitSize());
        int count = response.getResumableUpload().getBitmap().getCount();
        List<Integer> words = response.getResumableUpload().getBitmap().getWords();
        ResumableBitmap bitmap = new ResumableBitmap(count, words);
        uploadItem.setBitmap(bitmap);
        Configuration.getErrorTracker().i(TAG, uploadItem.getFileData().getFilePath() + " upload item bitmap: " + uploadItem.getBitmap().getCount() + " count, " + uploadItem.getBitmap().getWords().toString() + " words.");

        // notify listeners that check has completed
        notifyListenerCompleted(response);
    }

    /**
     * generates the request parameter after we receive a UTF encoded filename.
     *
     * @param filename - the name of hte file.
     * @return - a map of request parameters.
     */
    private Map<String, String> generateRequestParameters(String filename) {
        // generate map with request parameters
        Map<String, String> keyValue = new HashMap<String, String>();
        keyValue.put("filename", filename);
        keyValue.put("hash", uploadItem.getFileData().getFileHash());
        keyValue.put("size", Long.toString(uploadItem.getFileData().getFileSize()));
        keyValue.put("resumable", uploadItem.getUploadOptions().getResumable());
        keyValue.put("response_format", "json");
        if (!uploadItem.getUploadOptions().getUploadPath().isEmpty()) {
            keyValue.put("path", uploadItem.getUploadOptions().getUploadPath());
        } else {
            keyValue.put("folder_key", uploadItem.getUploadOptions().getUploadFolderKey());
        }
        return keyValue;
    }
}
