package com.mediafire.sdk;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public final class MFHttpAfter {

    private MFTokenDistributor mfTokenDistributor;

    public MFHttpAfter(MFTokenDistributor mfTokenDistributor) {
        this.mfTokenDistributor = mfTokenDistributor;
    }

    public void returnToken(MFHttpRequest request) {
        if (request.getToken() == null) {
            return;
        }
        switch (request.getMfApi().getTokenType()) {
            case SESSION_TOKEN_V2:
                MFSessionToken mfSessionToken = (MFSessionToken) request.getToken();
                MFSessionToken updatedMFSessionToken = mfSessionToken.getUpdatedSessionToken();
                mfTokenDistributor.returnSessionToken(updatedMFSessionToken);
                break;
            case UNIQUE:
                // UNIQUE represents requesting a new session token via /api/user/get_session_token or /api/user/get_action_token
                if (request.getMfApi() == MFApi.URI_USER_GET_SESSION_TOKEN) {
                    MFSessionToken newSessionToken = (MFSessionToken) request.getToken();
                    mfTokenDistributor.receiveNewSessionToken(newSessionToken);
                } else if (request.getMfApi() == MFApi.URI_USER_GET_ACTION_TOKEN && request.getToken() instanceof MFImageActionToken) {
                    MFImageActionToken newActionToken = (MFImageActionToken) request.getToken();
                    mfTokenDistributor.receiveNewImageActionToken(newActionToken);
                } else if (request.getMfApi() == MFApi.URI_USER_GET_SESSION_TOKEN && request.getToken() instanceof MFUploadActionToken) {
                    MFUploadActionToken newActionToken = (MFUploadActionToken) request.getToken();
                    mfTokenDistributor.receiveNewUploadActionToken(newActionToken);
                } else {
                    // don't need to return anything to the token distributor.
                }
                break;
            default:
                // for types UPLOAD_ACTION_TOKEN, IMAGE_ACTION_TOKEN, NONE
                // there is no need to return a token
                break;
        }
    }
}
