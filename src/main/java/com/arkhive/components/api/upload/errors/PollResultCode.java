package com.arkhive.components.api.upload.errors;

/**
 * This enumeration covers the result codes in the response
 * received when calling /api/upload/poll_upload.php.
 * @author Chris Najar
 *
 */
public enum PollResultCode {
    SUCCESS(0),
    INVALID_UPLOAD_KEY(-20),
    UPLOAD_KEY_NOT_FOUND(-80),
    ;

    private final int value;

    private PollResultCode(int value) { this.value = value; }

    public int getValue() { return this.value; }

    public static PollResultCode fromInt(int value) {
        for (final PollResultCode e: values()) {
            if (e.getValue() == value) {
                return e;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        String returnMessage;
        switch(this.value) {
            case 0:         returnMessage = "Success"; break;
            case -20:       returnMessage = "Invalid Upload Key"; break;
            case -80:       returnMessage = "Upload Key not found"; break;
            default:        returnMessage = "No result code associated with: " + this.value; break;
        }
        return returnMessage;
    }
}
