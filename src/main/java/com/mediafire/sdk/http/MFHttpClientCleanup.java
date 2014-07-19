package com.mediafire.sdk.http;

import com.mediafire.sdk.api_responses.ApiResponse;
import com.mediafire.sdk.api_responses.user.GetActionTokenResponse;
import com.mediafire.sdk.api_responses.user.GetSessionTokenResponse;
import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.token.MFImageActionToken;
import com.mediafire.sdk.token.MFSessionToken;
import com.mediafire.sdk.token.MFTokenFarmCallback;
import com.mediafire.sdk.token.MFUploadActionToken;

public final class MFHttpClientCleanup extends MFHttp {
    private static final String TAG = MFHttpClientCleanup.class.getCanonicalName();
    private final MFTokenFarmCallback mfTokenFarmCallback;

    public MFHttpClientCleanup(MFTokenFarmCallback mfTokenFarmCallback, MFConfiguration mfConfiguration) {
        super(mfConfiguration);
        this.mfTokenFarmCallback = mfTokenFarmCallback;
    }

    public void returnToken(MFRequester mfRequester, MFResponse mfResponse) {
        MFConfiguration.getStaticMFLogger().v(TAG, "returning token");

        switch (mfRequester.getTypeOfTokenToReturn()) {
            case NEW:
                MFConfiguration.getStaticMFLogger().v(TAG, "returning new session token (api was " + mfRequester.getTypeOfTokenToReturn().toString() + ", " + mfRequester.toString() + " )");
                GetSessionTokenResponse newSessionTokenResponse = mfResponse.getResponseObject(GetSessionTokenResponse.class);
                MFSessionToken newSessionToken = createNewSessionToken(newSessionTokenResponse);
                mfTokenFarmCallback.receiveNewSessionToken(newSessionToken);
                break;
            case V2:
                MFConfiguration.getStaticMFLogger().v(TAG, "returning updated session token (api was " + mfRequester.getTypeOfTokenToReturn().toString() + ")");
                ApiResponse apiResponse = mfResponse.getResponseObject(ApiResponse.class);
                if (apiResponse.hasError() && apiResponse.getError() == 105 || apiResponse.getError() == 127) {
                    mfTokenFarmCallback.sessionTokenSpoiled((MFSessionToken) mfResponse.getOriginMFRequester().getToken());
                } else {
                    MFSessionToken mfSessionToken = updateSessionToken(apiResponse, mfRequester);
                    mfTokenFarmCallback.returnSessionToken(mfSessionToken);
                }
                break;
            case UPLOAD:
                MFConfiguration.getStaticMFLogger().v(TAG, "returning new upload action token (api was " + mfRequester.getTypeOfTokenToReturn().toString() + ", " + mfRequester.toString() + " )");
                GetActionTokenResponse uploadActionTokenResponse = mfResponse.getResponseObject(GetActionTokenResponse.class);
                if (uploadActionTokenResponse.hasError() && uploadActionTokenResponse.getError() == 105 || uploadActionTokenResponse.getError() == 127) {
                    mfTokenFarmCallback.sessionTokenSpoiled((MFSessionToken) mfResponse.getOriginMFRequester().getToken());
                } else {
                    MFUploadActionToken mfUploadActionToken = createUploadActionToken(uploadActionTokenResponse, mfRequester);
                    mfTokenFarmCallback.receiveNewUploadActionToken(mfUploadActionToken);
                }
                break;
            case IMAGE:
                MFConfiguration.getStaticMFLogger().v(TAG, "returning new image action token (api was " + mfRequester.getTypeOfTokenToReturn().toString() + ", " + mfRequester.toString() + " )");
                GetActionTokenResponse imageActionTokenResponse = mfResponse.getResponseObject(GetActionTokenResponse.class);
                if (imageActionTokenResponse.hasError() && imageActionTokenResponse.getError() == 105 || imageActionTokenResponse.getError() == 127) {
                    mfTokenFarmCallback.sessionTokenSpoiled((MFSessionToken) mfResponse.getOriginMFRequester().getToken());
                } else {
                    MFImageActionToken mfImageActionToken = createImageActionToken(imageActionTokenResponse, mfRequester);
                    mfTokenFarmCallback.receiveNewImageActionToken(mfImageActionToken);
                }
                break;
            case NONE:
                // for types NONE
                // there is no need to return a token
                MFConfiguration.getStaticMFLogger().v(TAG, "not returning a token (api was " + mfRequester.getTypeOfTokenToReturn().toString() + ")");
                break;
        }
    }

    public MFImageActionToken createImageActionToken(GetActionTokenResponse imageActionTokenResponse, MFRequester mfRequester) {
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
        if (mfRequester.getRequestParameters().containsKey("lifespan")) {
            tokenExpiry = Long.valueOf(mfRequester.getRequestParameters().get("lifespan"));
        } else {
            tokenExpiry = 0;
        }

        MFConfiguration.getStaticMFLogger().v(TAG, "returning new upload action token");
        return new MFImageActionToken(tokenString, tokenExpiry);
    }

    public MFUploadActionToken createUploadActionToken(GetActionTokenResponse uploadActionTokenResponse, MFRequester mfRequester) {
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
        if (mfRequester.getRequestParameters().containsKey("lifespan")) {
            tokenExpiry = Long.valueOf(mfRequester.getRequestParameters().get("lifespan"));
        } else {
            tokenExpiry = 0;
        }
        MFUploadActionToken mfUploadActionToken = new MFUploadActionToken(tokenString, tokenExpiry);
        MFConfiguration.getStaticMFLogger().v(TAG, "returning new upload action token: " + mfUploadActionToken.toString());
        return mfUploadActionToken;
    }

    public MFSessionToken updateSessionToken(ApiResponse apiResponse, MFRequester mfRequester) {
        MFConfiguration.getStaticMFLogger().v(TAG, "updateSessionToken()");
        if (apiResponse == null) {
            MFConfiguration.getStaticMFLogger().v(TAG, "response was null, returning original token");
            return (MFSessionToken) mfRequester.getToken();
        }

        if (apiResponse.hasError() && (apiResponse.getErrorCode() == ApiResponse.ResponseCode.ERROR_INVALID_SIGNATURE || apiResponse.getErrorCode() == ApiResponse.ResponseCode.ERROR_INVALID_TOKEN)) {
            MFConfiguration.getStaticMFLogger().v(TAG, "response had error code: " + apiResponse.getErrorCode().toString());
            return null;
        }

        MFSessionToken originalToken = (MFSessionToken) mfRequester.getToken();
        MFSessionToken newToken;
        if (apiResponse.needNewKey()) {
            MFConfiguration.getStaticMFLogger().v(TAG, "needs to recalculate key");
            newToken = ((MFSessionToken) mfRequester.getToken()).getUpdatedSessionToken();
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
