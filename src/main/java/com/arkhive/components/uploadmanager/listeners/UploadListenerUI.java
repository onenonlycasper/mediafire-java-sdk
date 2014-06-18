package com.arkhive.components.uploadmanager.listeners;

import com.arkhive.components.uploadmanager.uploaditem.UploadItem;

/**
 * interface aimed at UI class which wants to listen to the upload process.
 *
 * @author
 */
public interface UploadListenerUI {

    /**
     * Called when the upload process has been cancelled.
     *
     * @param uploadItem - item whose upload has been cancelled.
     */
    public void onCancelled(UploadItem uploadItem);

    /**
     * called when there is an update to the progress of an upload, specifically when each chunk is uploaded.
     *
     * @param uploadItem   - item whose upload has been cancelled.
     * @param currentChunk - the current chunk.
     * @param totalChunks  - the total number of chunks.
     */
    public void onProgressUpdate(UploadItem uploadItem, int currentChunk, int totalChunks);

    /**
     * called when the upload process has started.
     *
     * @param uploadItem - the upload item.
     */
    public void onStarted(UploadItem uploadItem);

    /**
     * called when the upload process has completed.
     *
     * @param uploadItem - the upload item.
     */
    public void onCompleted(UploadItem uploadItem);
}
