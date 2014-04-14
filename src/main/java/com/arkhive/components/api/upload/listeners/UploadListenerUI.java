package com.arkhive.components.api.upload.listeners;

import com.arkhive.components.uploadmanager.uploaditem.UploadItem;

/**
 * interface aimed at UI class which wants to listen to the upload process.
 * @author Chris Najar
 *
 */
public interface UploadListenerUI {
  
  /**
   * Called when the upload process has been cancelled.
   * @param uploadItem - item whose upload has been cancelled.
   */
  public void onCancelled(UploadItem uploadItem);
  
  /**
   * called when there is an update to the progress of an upload, specifically when each chunk is uploaded.
   * @param uploadItem - item whose upload has been cancelled.
   * @param percentComplete - % completed (# of chunks remaining)
   */
  public void onProgressUpdate(UploadItem uploadItem, int percentComplete);
  
  /**
   * called when the upload process has started.
   * @param uploadItem
   */
  public void onStarted(UploadItem uploadItem);
  
  /**
   * called when the upload process has completed.
   * @param uploadItem
   */
  public void onCompleted(UploadItem uploadItem);
}
