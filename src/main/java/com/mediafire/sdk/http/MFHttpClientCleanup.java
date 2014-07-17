package com.mediafire.sdk.http;

import com.mediafire.sdk.api_responses.ApiResponse;
import com.mediafire.sdk.api_responses.user.GetActionTokenResponse;
import com.mediafire.sdk.api_responses.user.GetSessionTokenResponse;
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

    public void returnToken(MFRequest mfRequest, MFResponse mfResponse) {
        mfConfiguration.getMfLogger().logMessage(TAG, "returning token");

        switch (mfRequest.getMfApi().getTokenType()) {
            case SESSION_TOKEN_V2:
                mfConfiguration.getMfLogger().logMessage(TAG, "not returning a token (api was " + mfRequest.getMfApi().getTokenType().toString() + ")");
                ApiResponse apiResponse = mfResponse.getResponseObject(ApiResponse.class);
                MFSessionToken mfSessionToken = updateSessionToken(apiResponse, mfRequest);
                mfTokenFarmCallback.returnSessionToken(mfSessionToken);
                break;
            case UNIQUE:
                // UNIQUE represents requesting a new session token via /api/user/get_session_token or /api/user/get_action_token
                if (mfRequest.getMfApi() == MFApi.USER_GET_SESSION_TOKEN) {
                    mfConfiguration.getMfLogger().logMessage(TAG, "returning session token (api was " + mfRequest.getMfApi().getTokenType().toString() + ", " + mfRequest.getMfApi().toString() + " )");
                    GetSessionTokenResponse newSessionTokenResponse = mfResponse.getResponseObject(GetSessionTokenResponse.class);
                    MFSessionToken newSessionToken = createNewSessionToken(newSessionTokenResponse);
                    mfTokenFarmCallback.receiveNewSessionToken(newSessionToken);
                } else if (mfRequest.getMfApi() == MFApi.USER_GET_ACTION_TOKEN && mfRequest.getToken() instanceof MFImageActionToken) {
                    mfConfiguration.getMfLogger().logMessage(TAG, "returning image action token (api was " + mfRequest.getMfApi().getTokenType().toString() + ", " + mfRequest.getMfApi().toString() + " )");
                    GetActionTokenResponse imageActionTokenResponse = mfResponse.getResponseObject(GetActionTokenResponse.class);
                    MFImageActionToken newActionToken = createImageActionToken(imageActionTokenResponse, mfRequest);
                    mfTokenFarmCallback.receiveNewImageActionToken(newActionToken);
                } else if (mfRequest.getMfApi() == MFApi.USER_GET_ACTION_TOKEN && mfRequest.getToken() instanceof MFUploadActionToken) {
                    mfConfiguration.getMfLogger().logMessage(TAG, "returning upload action token (api was " + mfRequest.getMfApi().getTokenType().toString() + ", " + mfRequest.getMfApi().toString() + " )");
                    GetActionTokenResponse uploadActionTokenResponse = mfResponse.getResponseObject(GetActionTokenResponse.class);
                    MFUploadActionToken newActionToken = createUploadActionToken(uploadActionTokenResponse, mfRequest);
                    mfTokenFarmCallback.receiveNewUploadActionToken(newActionToken);
                } else {
                    // don't need to return anything to the token distributor.
                    mfConfiguration.getMfLogger().logMessage(TAG, "not returning a token (api was " + mfRequest.getMfApi().getTokenType().toString() + ", token was class '" + mfRequest.getToken().getClass().getName() + ")");
                }
                break;
            default:
                // for types UPLOAD_ACTION_TOKEN, IMAGE_ACTION_TOKEN, NONE
                // there is no need to return a token
                mfConfiguration.getMfLogger().logMessage(TAG, "not returning a token (api was " + mfRequest.getMfApi().getTokenType().toString() + ")");
                break;
        }
    }

    public MFImageActionToken createImageActionToken(GetActionTokenResponse imageActionTokenResponse, MFRequest mfRequest) {
        mfConfiguration.getMfLogger().logMessage(TAG, "createImageActionToken()");
        if (imageActionTokenResponse == null) {
            mfConfiguration.getMfLogger().logMessage(TAG, "response was null, returning null");
            return null;
        }

        if (imageActionTokenResponse.hasError()) {
            mfConfiguration.getMfLogger().logMessage(TAG, "response had error, returning null");
            return null;
        }

        String tokenString = imageActionTokenResponse.getActionToken();
        long tokenExpiry;
        if (mfRequest.getRequestParameters().containsKey("lifespan")) {
            tokenExpiry = Long.valueOf(mfRequest.getRequestParameters().get("lifespan"));
        } else {
            tokenExpiry = 0;
        }

        mfConfiguration.getMfLogger().logMessage(TAG, "returning new upload action token");
        return new MFImageActionToken(tokenString, tokenExpiry);
    }

    public MFUploadActionToken createUploadActionToken(GetActionTokenResponse uploadActionTokenResponse, MFRequest mfRequest) {
        mfConfiguration.getMfLogger().logMessage(TAG, "createUploadActionToken()");
        if (uploadActionTokenResponse == null) {
            mfConfiguration.getMfLogger().logMessage(TAG, "response was null, returning null");
            return null;
        }

        if (uploadActionTokenResponse.hasError()) {
            mfConfiguration.getMfLogger().logMessage(TAG, "response had error, returning null");
            return null;
        }

        String tokenString = uploadActionTokenResponse.getActionToken();
        long tokenExpiry;
        if (mfRequest.getRequestParameters().containsKey("lifespan")) {
            tokenExpiry = Long.valueOf(mfRequest.getRequestParameters().get("lifespan"));
        } else {
            tokenExpiry = 0;
        }
        MFUploadActionToken mfUploadActionToken = new MFUploadActionToken(tokenString, tokenExpiry);
        mfConfiguration.getMfLogger().logMessage(TAG, "returning new upload action token: " + mfUploadActionToken.toString());
        return mfUploadActionToken;
    }

    public MFSessionToken updateSessionToken(ApiResponse apiResponse, MFRequest mfRequest) {
        mfConfiguration.getMfLogger().logMessage(TAG, "updateSessionToken()");
        if (apiResponse == null) {
            mfConfiguration.getMfLogger().logMessage(TAG, "response was null, returning original token");
            return (MFSessionToken) mfRequest.getToken();
        }

        if (apiResponse.hasError() && (apiResponse.getErrorCode() == ApiResponse.ResponseCode.ERROR_INVALID_SIGNATURE || apiResponse.getErrorCode() == ApiResponse.ResponseCode.ERROR_INVALID_TOKEN)) {
            mfConfiguration.getMfLogger().logMessage(TAG, "response had error code: " + apiResponse.getErrorCode().toString());
            return null;
        }

        MFSessionToken originalToken = (MFSessionToken) mfRequest.getToken();
        MFSessionToken newToken;
        if (apiResponse.needNewKey()) {
            mfConfiguration.getMfLogger().logMessage(TAG, "needs to recalculate key");
            newToken = ((MFSessionToken) mfRequest.getToken()).getUpdatedSessionToken();
            mfConfiguration.getMfLogger().logMessage(TAG, "original token: " + originalToken.toString());
            mfConfiguration.getMfLogger().logMessage(TAG, "returning new token: " + newToken.toString());
            return newToken;
        }
        mfConfiguration.getMfLogger().logMessage(TAG, "returning original token: " + originalToken.toString());
        return originalToken;
    }

    public MFSessionToken createNewSessionToken(GetSessionTokenResponse newSessionTokenResponse) {
        mfConfiguration.getMfLogger().logMessage(TAG, "createNewSessionToken");
        if (newSessionTokenResponse == null) {
            mfConfiguration.getMfLogger().logMessage(TAG, "response was null, returning null");
            return null;
        }

        if (newSessionTokenResponse.hasError()) {
            mfConfiguration.getMfLogger().logMessage(TAG, "response was null, returning null");
            return null;
        }

        String tokenString = newSessionTokenResponse.getSessionToken();
        String secretKey = newSessionTokenResponse.getSessionToken();
        String time = newSessionTokenResponse.getTime();
        String pkey = newSessionTokenResponse.getPkey();
        String ekey = newSessionTokenResponse.getEkey();
        MFSessionToken mfSessionToken = new MFSessionToken(tokenString, secretKey, time, pkey, ekey);
        mfConfiguration.getMfLogger().logMessage(TAG, "returning new token: " + mfSessionToken.toString());
        return mfSessionToken;
    }
}
