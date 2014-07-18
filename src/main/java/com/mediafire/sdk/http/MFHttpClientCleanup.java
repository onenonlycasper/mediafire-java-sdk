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
        MFConfiguration.getStaticMFLogger().v(TAG, "returning token");

        switch (mfRequest.getMfApi().getTokenType()) {
            case SESSION_TOKEN_V2:
                MFConfiguration.getStaticMFLogger().v(TAG, "not returning a token (api was " + mfRequest.getMfApi().getTokenType().toString() + ")");
                ApiResponse apiResponse = mfResponse.getResponseObject(ApiResponse.class);
                MFSessionToken mfSessionToken = updateSessionToken(apiResponse, mfRequest);
                mfTokenFarmCallback.returnSessionToken(mfSessionToken);
                break;
            case UNIQUE:
                // UNIQUE represents requesting a new session token via /api/user/get_session_token or /api/user/get_action_token
                if (mfRequest.getMfApi() == MFApi.USER_GET_SESSION_TOKEN) {
                    MFConfiguration.getStaticMFLogger().v(TAG, "returning session token (api was " + mfRequest.getMfApi().getTokenType().toString() + ", " + mfRequest.getMfApi().toString() + " )");
                    GetSessionTokenResponse newSessionTokenResponse = mfResponse.getResponseObject(GetSessionTokenResponse.class);
                    MFSessionToken newSessionToken = createNewSessionToken(newSessionTokenResponse);
                    mfTokenFarmCallback.receiveNewSessionToken(newSessionToken);
                } else if (mfRequest.getMfApi() == MFApi.USER_GET_ACTION_TOKEN && mfRequest.getToken() instanceof MFImageActionToken) {
                    MFConfiguration.getStaticMFLogger().v(TAG, "returning image action token (api was " + mfRequest.getMfApi().getTokenType().toString() + ", " + mfRequest.getMfApi().toString() + " )");
                    GetActionTokenResponse imageActionTokenResponse = mfResponse.getResponseObject(GetActionTokenResponse.class);
                    MFImageActionToken newActionToken = createImageActionToken(imageActionTokenResponse, mfRequest);
                    mfTokenFarmCallback.receiveNewImageActionToken(newActionToken);
                } else if (mfRequest.getMfApi() == MFApi.USER_GET_ACTION_TOKEN && mfRequest.getToken() instanceof MFUploadActionToken) {
                    MFConfiguration.getStaticMFLogger().v(TAG, "returning upload action token (api was " + mfRequest.getMfApi().getTokenType().toString() + ", " + mfRequest.getMfApi().toString() + " )");
                    GetActionTokenResponse uploadActionTokenResponse = mfResponse.getResponseObject(GetActionTokenResponse.class);
                    MFUploadActionToken newActionToken = createUploadActionToken(uploadActionTokenResponse, mfRequest);
                    mfTokenFarmCallback.receiveNewUploadActionToken(newActionToken);
                } else {
                    // don't need to return anything to the token distributor.
                    MFConfiguration.getStaticMFLogger().v(TAG, "not returning a token (api was " + mfRequest.getMfApi().getTokenType().toString() + ", token was class '" + mfRequest.getToken().getClass().getName() + ")");
                }
                break;
            default:
                // for types UPLOAD_ACTION_TOKEN, IMAGE_ACTION_TOKEN, NONE
                // there is no need to return a token
                MFConfiguration.getStaticMFLogger().v(TAG, "not returning a token (api was " + mfRequest.getMfApi().getTokenType().toString() + ")");
                break;
        }
    }

    public MFImageActionToken createImageActionToken(GetActionTokenResponse imageActionTokenResponse, MFRequest mfRequest) {
        MFConfiguration.getStaticMFLogger().v(TAG, "createImageActionToken()");
        if (imageActionTokenResponse == null) {
            MFConfiguration.getStaticMFLogger().v(TAG, "response was null, returning null");
            return null;
        }

        if (imageActionTokenResponse.hasError()) {
            MFConfiguration.getStaticMFLogger().v(TAG, "response had error, returning null");
            return null;
        }

        String tokenString = imageActionTokenResponse.getActionToken();
        long tokenExpiry;
        if (mfRequest.getRequestParameters().containsKey("lifespan")) {
            tokenExpiry = Long.valueOf(mfRequest.getRequestParameters().get("lifespan"));
        } else {
            tokenExpiry = 0;
        }

        MFConfiguration.getStaticMFLogger().v(TAG, "returning new upload action token");
        return new MFImageActionToken(tokenString, tokenExpiry);
    }

    public MFUploadActionToken createUploadActionToken(GetActionTokenResponse uploadActionTokenResponse, MFRequest mfRequest) {
        MFConfiguration.getStaticMFLogger().v(TAG, "createUploadActionToken()");
        if (uploadActionTokenResponse == null) {
            MFConfiguration.getStaticMFLogger().v(TAG, "response was null, returning null");
            return null;
        }

        if (uploadActionTokenResponse.hasError()) {
            MFConfiguration.getStaticMFLogger().v(TAG, "response had error, returning null");
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
        MFConfiguration.getStaticMFLogger().v(TAG, "returning new upload action token: " + mfUploadActionToken.toString());
        return mfUploadActionToken;
    }

    public MFSessionToken updateSessionToken(ApiResponse apiResponse, MFRequest mfRequest) {
        MFConfiguration.getStaticMFLogger().v(TAG, "updateSessionToken()");
        if (apiResponse == null) {
            MFConfiguration.getStaticMFLogger().v(TAG, "response was null, returning original token");
            return (MFSessionToken) mfRequest.getToken();
        }

        if (apiResponse.hasError() && (apiResponse.getErrorCode() == ApiResponse.ResponseCode.ERROR_INVALID_SIGNATURE || apiResponse.getErrorCode() == ApiResponse.ResponseCode.ERROR_INVALID_TOKEN)) {
            MFConfiguration.getStaticMFLogger().v(TAG, "response had error code: " + apiResponse.getErrorCode().toString());
            return null;
        }

        MFSessionToken originalToken = (MFSessionToken) mfRequest.getToken();
        MFSessionToken newToken;
        if (apiResponse.needNewKey()) {
            MFConfiguration.getStaticMFLogger().v(TAG, "needs to recalculate key");
            newToken = ((MFSessionToken) mfRequest.getToken()).getUpdatedSessionToken();
            MFConfiguration.getStaticMFLogger().v(TAG, "original token: " + originalToken.toString());
            MFConfiguration.getStaticMFLogger().v(TAG, "returning new token: " + newToken.toString());
            return newToken;
        }
        MFConfiguration.getStaticMFLogger().v(TAG, "returning original token: " + originalToken.toString());
        return originalToken;
    }

    public MFSessionToken createNewSessionToken(GetSessionTokenResponse newSessionTokenResponse) {
        MFConfiguration.getStaticMFLogger().v(TAG, "createNewSessionToken()");
        if (newSessionTokenResponse == null) {
            MFConfiguration.getStaticMFLogger().v(TAG, "response was null, returning null");
            return null;
        }

        if (newSessionTokenResponse.hasError()) {
            MFConfiguration.getStaticMFLogger().v(TAG, "response was null, returning null");
            return null;
        }

        String tokenString = newSessionTokenResponse.getSessionToken();
        String secretKey = newSessionTokenResponse.getSecretKey();
        String time = newSessionTokenResponse.getTime();
        String pkey = newSessionTokenResponse.getPkey();
        String ekey = newSessionTokenResponse.getEkey();
        MFSessionToken mfSessionToken = new MFSessionToken(tokenString, secretKey, time, pkey, ekey);
        MFConfiguration.getStaticMFLogger().v(TAG, "returning new token: " + mfSessionToken.toString());
        return mfSessionToken;
    }
}
