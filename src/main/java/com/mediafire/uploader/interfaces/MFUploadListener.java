package com.mediafire.uploader.interfaces;

import com.mediafire.uploader.uploaditem.MFUploadItem;

public interface MFUploadListener {

    public void onCancelled(MFUploadItem mfUploadItem);

    public void onProgressUpdate(MFUploadItem mfUploadItem, int currentChunk, int totalChunks);

    public void onStarted(MFUploadItem mfUploadItem);

    public void onCompleted(MFUploadItem mfUploadItem);
}
