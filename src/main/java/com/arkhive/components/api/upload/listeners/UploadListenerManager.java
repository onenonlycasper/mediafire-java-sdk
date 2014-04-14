package com.arkhive.components.api.upload.listeners;

import com.arkhive.components.api.ApiResponse;
import com.arkhive.components.api.upload.responses.CheckResponse;
import com.arkhive.components.api.upload.responses.PollResponse;
import com.arkhive.components.uploadmanager.uploaditem.UploadItem;

/**
 * interface aimed at being used by a class that manages the upload process.
 * @author Chris Najar
 *
 */
public interface UploadListenerManager {
  /**
   * Called when the CheckProcess has completed.
   * @param uploadItem - the item being uploaded.
   * @param response - the response received.
   */
  public void onCheckCompleted(UploadItem uploadItem, CheckResponse response);
  
  /**
   * Called when the InstantProcess has completed.
   * @param uploadItem - the item being uploaded.
   */
  public void onInstantCompleted(UploadItem uploadItem);
  
  /**
   * Called when the UploadProcess has completed it's cycle.
   * @param uploadItem - the item being uploaded.
   */
  public void onResumableCompleted(UploadItem uploadItem);
  
  /**
   * Called when the PollUploadProcess has completed it's cycle.
   * @param uploadItem - the item being uploaded.
   * @param response - the response received.
   */
  public void onPollCompleted(UploadItem uploadItem, PollResponse response);
  
  /**
   * Called when an exception has been caught such as FileNotFoundException for a path.
   * @param uploadItem - the item being uploaded.
   * @param exception - the exception received.
   */
  public void onProcessException(UploadItem uploadItem, Exception exception);
  
  /**
   * Called when an HttpInterface.sendGetRequest() or HttpInterface.sendPostRequest() returns an empty String.
   * <p>
   * HttpInterface will return an empty String when internet connectivity is lost or unavailable.
   * @param uploadItem
   */
  public void onLostConnection(UploadItem uploadItem);
  
  /**
   * called when an upload is cancelled for any reason. The last received response is passed with the upload item.
   * Normally this is called when there is an Api error.
   * @param uploadItem
   */
  public void onCancelled(UploadItem uploadItem, ApiResponse response);
}
