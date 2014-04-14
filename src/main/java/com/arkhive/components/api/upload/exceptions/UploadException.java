package com.arkhive.components.api.upload.exceptions;

import com.arkhive.components.api.upload.errors.ResumableResultCode;

/**
 * Custom exception to be used during the upload process.
 * If an exception arises, the developer can throw 
 * an exception from this custom exception class.
 * @author Chris Najar
 *
 */
public class UploadException extends Exception {
  private static final long serialVersionUID = 6753915152839841142L;
    private ResumableResultCode resultCode;

    /**
     * Use this exception and pass result code.
     * @param resultCode
     */
    public UploadException(ResumableResultCode resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * Use this exception when you only want to pass a message and the throwable.
     * @param message
     * @param throwable
     */
    public UploadException(String message, Throwable throwable) {
        super(throwable);
    }

    /**
     * Use this exception when you only want to pass a throwable..
     * @param e
     */
    public UploadException(Throwable e) {
        this(null, e);
    }

    /**
     * Retrieve the result code.
     * @return
     */
    public ResumableResultCode getResultCode() {
      if (resultCode == null) {
        return ResumableResultCode.NO_ERROR;
      }
        return resultCode;
    }
}
