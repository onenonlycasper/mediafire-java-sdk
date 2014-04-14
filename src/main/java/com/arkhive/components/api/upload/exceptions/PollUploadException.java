package com.arkhive.components.api.upload.exceptions;

import com.arkhive.components.api.upload.errors.PollFileErrorCode;
import com.arkhive.components.api.upload.errors.PollResultCode;
import com.arkhive.components.api.upload.errors.PollStatusCode;

/**
 * Custom exception that can be thrown.
 * This is meant to be thrown ny PollUploadProcess.java
 * @author Chris Najar
 *
 */
public class PollUploadException extends Exception {
  private static final long serialVersionUID = 4077464328988623761L;
    private PollFileErrorCode fileErrorCode;
    private PollResultCode resultCode;
    private PollStatusCode statusCode;
    
    /**
     * Exception with the fileErrorCode and a custom message.
     * @param message - custom message
     * @param fileErrorCode - code which caused this exception to be thrown
     */
    public PollUploadException(String message, PollFileErrorCode fileErrorCode) {
        super(message);
        this.fileErrorCode = fileErrorCode;
    }

    /**
     * Exception with the resultCode and a custom message.
     * @param message - custom message
     * @param resultCode - code which caused this exception to be thrown
     */
    public PollUploadException(String message, PollResultCode resultCode) {
        super(message);
        this.resultCode = resultCode;
    }

    /**
     * Exception with the statusCode and a custom message.
     * @param message - custom message
     * @param statusCode - code which caused this exception to be thrown
     */
    public PollUploadException(String message, PollStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    /**
     * Exception with a throwable and a custom message.
     * @param message - custom message
     * @param throwable - throwable which caused this exception to be thrown
     */
    public PollUploadException(String message, Throwable throwable) {
        super(message, throwable);
    }
    
    /**
     * Exception with a throwable.
     * @param throwable - whatever throwable is passed
     */
    public PollUploadException(Throwable throwable) {
        super(throwable);
    }
    
    /**
     * Exception with a message.
     * @param message - custom message
     */
    public PollUploadException(String message) {
        super(message);
    }

    /**
     * returns the file error code.
     * @return
     */
    public PollFileErrorCode getFileErrorCode() {
      if (fileErrorCode == null) {
        return PollFileErrorCode.NO_ERROR;
      }
        return fileErrorCode;
    }

    /**
     * returns the result code.
     * @return
     */
    public PollResultCode getResultCode() {
      if (resultCode == null) {
        return PollResultCode.SUCCESS;
      }
        return resultCode;
    }

    /**
     * returns the status code.
     * @return
     */
    public PollStatusCode getStatusCode() {
      if (statusCode == null) {
        return PollStatusCode.UNKNOWN_OR_NO_STATUS_AVAILABLE_FOR_THIS_KEY;
      }
        return statusCode;
    }
}
