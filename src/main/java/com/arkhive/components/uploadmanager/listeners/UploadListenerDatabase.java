package com.arkhive.components.uploadmanager.listeners;

import com.arkhive.components.uploadmanager.uploaditem.UploadItem;

/**
 * interface aimed at being used by a custom ContentProvider.
 * @author Chris Najar
 *
 */
public interface UploadListenerDatabase {
  
  /**
   * Called when the upload process has been cancelled. Usually called when there is an API error response.
   * @param uploadItem - item whose upload has been cancelled.
   */
  public void onCancelled(UploadItem uploadItem);
  
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
