package com.mediafire.sdk.token;

/**
 * TODO: doc
 */
public interface MFTokenFarmCallback {
    public void returnSessionToken(MFSessionToken mfSessionToken);

    public void sessionTokenSpoiled(MFSessionToken mfSessionToken);

    public void receiveNewSessionToken(MFSessionToken mfSessionToken);

    public void receiveNewImageActionToken(MFImageActionToken mfImageActionToken);

    public void receiveNewUploadActionToken(MFUploadActionToken mfUploadActionToken);

    public MFSessionToken borrowMFSessionToken();

    public MFUploadActionToken borrowMFUploadActionToken();

    public MFImageActionToken borrowMFImageActionToken();
}
