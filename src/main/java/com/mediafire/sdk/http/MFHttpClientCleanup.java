package com.mediafire.sdk.http;

import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.token.MFImageActionToken;
import com.mediafire.sdk.token.MFSessionToken;
import com.mediafire.sdk.token.MFTokenFarmCallback;
import com.mediafire.sdk.token.MFUploadActionToken;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public final class MFHttpClientCleanup extends MFHttp {
    private static final String TAG = MFHttpClientCleanup.class.getCanonicalName();
    private MFTokenFarmCallback mfTokenFarmCallback;

    public MFHttpClientCleanup(MFTokenFarmCallback mfTokenFarmCallback, MFConfiguration mfConfiguration) {
        super(mfConfiguration);
        this.mfTokenFarmCallback = mfTokenFarmCallback;
    }

    public void returnToken(MFRequest request) {
        mfConfiguration.getMfLogger().logMessage(TAG, "returning token");

        switch (request.getMfApi().getTokenType()) {
            case SESSION_TOKEN_V2:
                mfConfiguration.getMfLogger().logMessage(TAG, "not returning a token (api was " + request.getMfApi().getTokenType().toString() + ")");
                MFSessionToken mfSessionToken = (MFSessionToken) request.getToken();
                MFSessionToken updatedMFSessionToken = mfSessionToken.getUpdatedSessionToken();
                mfTokenFarmCallback.returnSessionToken(updatedMFSessionToken);
                break;
            case UNIQUE:
                // UNIQUE represents requesting a new session token via /api/user/get_session_token or /api/user/get_action_token
                if (request.getMfApi() == MFApi.USER_GET_SESSION_TOKEN) {
                    mfConfiguration.getMfLogger().logMessage(TAG, "returning session token (api was " + request.getMfApi().getTokenType().toString() + ")");
                    MFSessionToken newSessionToken = (MFSessionToken) request.getToken();
                    mfTokenFarmCallback.receiveNewSessionToken(newSessionToken);
                } else if (request.getMfApi() == MFApi.USER_GET_ACTION_TOKEN && request.getToken() instanceof MFImageActionToken) {
                    mfConfiguration.getMfLogger().logMessage(TAG, "returning image action token (api was " + request.getMfApi().getTokenType().toString() + ")");
                    MFImageActionToken newActionToken = (MFImageActionToken) request.getToken();
                    mfTokenFarmCallback.receiveNewImageActionToken(newActionToken);
                } else if (request.getMfApi() == MFApi.USER_GET_ACTION_TOKEN && request.getToken() instanceof MFUploadActionToken) {
                    mfConfiguration.getMfLogger().logMessage(TAG, "returning upload action token (api was " + request.getMfApi().getTokenType().toString() + ")");
                    MFUploadActionToken newActionToken = (MFUploadActionToken) request.getToken();
                    mfTokenFarmCallback.receiveNewUploadActionToken(newActionToken);
                } else {
                    // don't need to return anything to the token distributor.
                    mfConfiguration.getMfLogger().logMessage(TAG, "not returning a token (api was " + request.getMfApi().getTokenType().toString() + ")");
                }
                break;
            default:
                // for types UPLOAD_ACTION_TOKEN, IMAGE_ACTION_TOKEN, NONE
                // there is no need to return a token
                mfConfiguration.getMfLogger().logMessage(TAG, "not returning a token (api was " + request.getMfApi().getTokenType().toString() + ")");
                break;
        }
    }
}
