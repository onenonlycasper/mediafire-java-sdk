package com.mediafire.sdk.token;

/**
 * TODO: doc
 */
public interface MFTokenFarmCallback {
    public void returnSessionToken(MFSessionToken sessionToken);

    public void receiveNewSessionToken(MFSessionToken sessionToken);

    public void receiveNewImageActionToken(MFImageActionToken uploadActionToken);

    public void receiveNewUploadActionToken(MFUploadActionToken uploadActionToken);

    public MFSessionToken borrowSessionToken();

    public MFUploadActionToken borrowUploadActionToken();

    public MFImageActionToken borrowImageActionToken();
}
