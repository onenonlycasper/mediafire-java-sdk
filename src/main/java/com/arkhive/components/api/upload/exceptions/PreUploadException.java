package com.arkhive.components.api.upload.exceptions;

import com.arkhive.components.api.upload.errors.CheckResponseErrorCode;

/**
 * Custom exceptions to be used within the PreUploadProcess.java.
 * These exceptions should be thrown when an error code is received.
 * @author Chris Najar
 *
 */
public class PreUploadException extends Exception {
  private static final long serialVersionUID = 5821118986053921715L;
    private CheckResponseErrorCode errorCode;
    
    /**
     * Exception given an error code.
     * @param errorCode
     */
    public PreUploadException(CheckResponseErrorCode errorCode) {
        this.errorCode = errorCode;
    }
    
    /**
     * retrieves the error code.
     * @return
     */
    public CheckResponseErrorCode getErrorCode() {
      if (errorCode == null) {
        return CheckResponseErrorCode.NO_ERROR;
      }
        return this.errorCode;
    }
}
