package com.mediafire.sdk.token;

public interface MFTokenFarmCallback {
    /**
     * called when a session token is being returned.
     * @param mfSessionToken
     */
    public void returnSessionToken(MFSessionToken mfSessionToken);

    /**
     * called when a session token is out of sync and unrecoverable.
     * @param mfSessionToken
     */
    public void sessionTokenSpoiled(MFSessionToken mfSessionToken);

    /**
     * called when a new session token is being returned
     * @param mfSessionToken
     */
    public void receiveNewSessionToken(MFSessionToken mfSessionToken);

    /**
     * called when a new image action token is being returned.
     * @param mfImageActionToken
     */
    public void receiveNewImageActionToken(MFActionToken mfImageActionToken);

    /**
     * called when a new upload action token is being returned.
     * @param mfUploadActionToken
     */
    public void receiveNewUploadActionToken(MFActionToken mfUploadActionToken);

    /**
     * called when there is a request to borrow a MFSessionToken
     * @return a MFSessionToken
     */
    public MFSessionToken borrowMFSessionToken();

    /**
     * called when there is a request to borrow a MFActionToken
     * @return a MFActionToken
     */
    public MFActionToken borrowMFUploadActionToken();

    /**
     * called when there is a requests to borrow a MFActionToken
     * @return a MFActionToken
     */
    public MFActionToken borrowMFImageActionToken();

    /**
     * called when an action token used causes an error 105 ("The supplied Session Token is expired or invalid")
     * this should trigger getting a new action token.
     */
    public void actionTokenSpoiled();
}
