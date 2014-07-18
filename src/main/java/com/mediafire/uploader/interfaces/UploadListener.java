package com.mediafire.uploader.interfaces;

import com.mediafire.uploader.uploaditem.UploadItem;

public interface UploadListener {

    public void onCancelled(UploadItem uploadItem);

    public void onProgressUpdate(UploadItem uploadItem, int currentChunk, int totalChunks);

    public void onStarted(UploadItem uploadItem);

    public void onCompleted(UploadItem uploadItem);
}
