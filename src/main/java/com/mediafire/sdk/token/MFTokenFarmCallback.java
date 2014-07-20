package com.mediafire.sdk.token;

/**
 * TODO: doc
 */
public interface MFTokenFarmCallback {
    public void returnSessionToken(MFSessionToken mfSessionToken);

    public void sessionTokenSpoiled(MFSessionToken mfSessionToken);

    public void receiveNewSessionToken(MFSessionToken mfSessionToken);

    public void receiveNewImageActionToken(MFActionToken mfImageActionToken);

    public void receiveNewUploadActionToken(MFActionToken mfUploadActionToken);

    public MFSessionToken borrowMFSessionToken();

    public MFActionToken borrowMFUploadActionToken();

    public MFActionToken borrowMFImageActionToken();
}
