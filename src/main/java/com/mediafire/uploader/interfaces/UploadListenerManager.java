package com.mediafire.uploader.interfaces;

import com.mediafire.sdk.api_responses.ApiResponse;
import com.mediafire.sdk.api_responses.upload.CheckResponse;
import com.mediafire.sdk.api_responses.upload.InstantResponse;
import com.mediafire.sdk.api_responses.upload.PollResponse;
import com.mediafire.sdk.api_responses.upload.ResumableResponse;
import com.mediafire.uploader.uploaditem.UploadItem;

public interface UploadListenerManager {

    public void onStartedUploadProcess(UploadItem uploadItem);

    public void onCheckCompleted(UploadItem uploadItem, CheckResponse response);

    public void onProgressUpdate(UploadItem uploadItem, int chunkNumber, int numChunks);

    public void onInstantCompleted(UploadItem uploadItem, InstantResponse response);

    public void onResumableCompleted(UploadItem uploadItem, ResumableResponse response);

    public void onPollCompleted(UploadItem uploadItem, PollResponse response);

    public void onProcessException(UploadItem uploadItem, Exception exception);

    public void onLostConnection(UploadItem uploadItem);

    public void onCancelled(UploadItem uploadItem, ApiResponse response);
}
