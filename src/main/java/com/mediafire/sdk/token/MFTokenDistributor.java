package com.mediafire.sdk.token;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public interface MFTokenDistributor {
    public void returnSessionToken(MFSessionToken sessionToken);

    public void receiveNewSessionToken(MFSessionToken sessionToken);

    public void receiveNewImageActionToken(MFImageActionToken uploadActionToken);

    public void receiveNewUploadActionToken(MFUploadActionToken uploadActionToken);

    public MFSessionToken borrowSessionToken();

    public MFUploadActionToken borrowUploadActionToken();

    public MFImageActionToken borrowImageActionToken();
}
