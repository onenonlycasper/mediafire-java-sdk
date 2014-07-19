package com.mediafire.sdk.http;

import com.mediafire.sdk.api_responses.ApiResponse;
import com.mediafire.sdk.api_responses.user.GetActionTokenResponse;
import com.mediafire.sdk.api_responses.user.GetSessionTokenResponse;
import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.token.MFActionToken;
import com.mediafire.sdk.token.MFSessionToken;
import com.mediafire.sdk.token.MFTokenFarmCallback;

public final class MFHttpClientCleanup extends MFHttp {
    private static final String TAG = MFHttpClientCleanup.class.getCanonicalName();
    private final MFTokenFarmCallback mfTokenFarmCallback;

    public MFHttpClientCleanup(MFTokenFarmCallback mfTokenFarmCallback, MFConfiguration mfConfiguration) {
        super(mfConfiguration);
        this.mfTokenFarmCallback = mfTokenFarmCallback;
    }

    public void returnToken(MFRequester mfRequester, MFResponse mfResponse) {
        switch (mfRequester.getTypeOfTokenToReturn()) {
            case NEW:
                GetSessionTokenResponse newSessionTokenResponse = mfResponse.getResponseObject(GetSessionTokenResponse.class);
                MFSessionToken newSessionToken = createNewSessionToken(newSessionTokenResponse);
                MFConfiguration.getStaticMFLogger().v(TAG, "returning new session token: " + newSessionToken.toString());
                mfTokenFarmCallback.receiveNewSessionToken(newSessionToken);
                break;
            case V2:
                ApiResponse apiResponse = mfResponse.getResponseObject(ApiResponse.class);
                if (apiResponse.hasError() && apiResponse.getError() == 105 || apiResponse.getError() == 127) {
                    mfTokenFarmCallback.sessionTokenSpoiled((MFSessionToken) mfResponse.getOriginMFRequester().getToken());
                } else {
                    if (apiResponse.needNewKey()) {
                        MFConfiguration.getStaticMFLogger().v(TAG, "original session token borrowed: " + mfRequester.getToken().toString());
                        ((MFSessionToken) mfRequester.getToken()).updateSessionToken();
                        MFConfiguration.getStaticMFLogger().v(TAG, "returning updated session token: " + mfRequester.getToken().toString());
                    }
                    mfTokenFarmCallback.returnSessionToken(((MFSessionToken) mfRequester.getToken()));
                }
                break;
            case UPLOAD:
                GetActionTokenResponse uploadActionTokenResponse = mfResponse.getResponseObject(GetActionTokenResponse.class);
                if (uploadActionTokenResponse.hasError() && uploadActionTokenResponse.getError() == 105 || uploadActionTokenResponse.getError() == 127) {
                    mfTokenFarmCallback.sessionTokenSpoiled((MFSessionToken) mfResponse.getOriginMFRequester().getToken());
                } else {
                    MFActionToken mfUploadActionToken = createActionToken(MFActionToken.Type.UPLOAD, uploadActionTokenResponse, mfRequester);
                    MFConfiguration.getStaticMFLogger().v(TAG, "returning new upload action token: " + mfUploadActionToken.toString());
                    mfTokenFarmCallback.receiveNewUploadActionToken(mfUploadActionToken);
                }
                break;
            case IMAGE:
                GetActionTokenResponse imageActionTokenResponse = mfResponse.getResponseObject(GetActionTokenResponse.class);
                if (imageActionTokenResponse.hasError() && imageActionTokenResponse.getError() == 105 || imageActionTokenResponse.getError() == 127) {
                    mfTokenFarmCallback.sessionTokenSpoiled((MFSessionToken) mfResponse.getOriginMFRequester().getToken());
                } else {
                    MFActionToken mfImageActionToken = createActionToken(MFActionToken.Type.IMAGE, imageActionTokenResponse, mfRequester);
                    MFConfiguration.getStaticMFLogger().v(TAG, "returning new image action token: " + mfImageActionToken.toString());
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

    public MFActionToken createActionToken(MFActionToken.Type type, GetActionTokenResponse getActionTokenResponse, MFRequester mfRequester) {
        MFConfiguration.getStaticMFLogger().v(TAG, "createActionToken()");
        if (getActionTokenResponse == null) {
            MFConfiguration.getStaticMFLogger().v(TAG, "response was null, returning null");
            return null;
        }

        if (getActionTokenResponse.hasError()) {
            MFConfiguration.getStaticMFLogger().v(TAG, "response had error, returning null");
            return null;
        }

        String tokenString = getActionTokenResponse.getActionToken();
        long tokenExpiry;
        if (mfRequester.getRequestParameters().containsKey("lifespan")) {
            tokenExpiry = Long.valueOf(mfRequester.getRequestParameters().get("lifespan"));
        } else {
            tokenExpiry = 0;
        }
        MFActionToken actionToken = new MFActionToken(tokenString, type, tokenExpiry);
        MFConfiguration.getStaticMFLogger().v(TAG, "created new upload action token: " + actionToken.toString());
        return actionToken;
    }

//    public MFSessionToken updateSessionToken(ApiResponse apiResponse, MFRequester mfRequester) {
//        MFConfiguration.getStaticMFLogger().v(TAG, "updateSessionToken()");
//        if (apiResponse == null) {
//            MFConfiguration.getStaticMFLogger().v(TAG, "response was null, returning original token");
//            return (MFSessionToken) mfRequester.getToken();
//        }
//
//        if (apiResponse.hasError() && (apiResponse.getError() == 105 || apiResponse.getError() == 107)) {
//            MFConfiguration.getStaticMFLogger().v(TAG, "response had error code: " + apiResponse.getErrorCode().toString());
//            return null;
//        }
//
//        MFSessionToken originalToken = (MFSessionToken) mfRequester.getToken();
//        if (apiResponse.needNewKey()) {
//            MFConfiguration.getStaticMFLogger().v(TAG, "needs to recalculate key");
//            ((MFSessionToken) mfRequester.getToken()).updateSessionToken();
//            MFConfiguration.getStaticMFLogger().v(TAG, "original token values: " + originalToken.toString());
//            MFConfiguration.getStaticMFLogger().v(TAG, "adjusted token values: " + originalToken.toString());
//            return originalToken;
//        }
//        MFConfiguration.getStaticMFLogger().v(TAG, "returning original token: " + originalToken.toString());
//        return originalToken;
//    }

    public MFSessionToken createNewSessionToken(GetSessionTokenResponse getSessionTokenResponse) {
        MFConfiguration.getStaticMFLogger().v(TAG, "createNewSessionToken()");
        if (getSessionTokenResponse == null) {
            MFConfiguration.getStaticMFLogger().v(TAG, "response was null, returning null");
            return null;
        }

        if (getSessionTokenResponse.hasError()) {
            MFConfiguration.getStaticMFLogger().v(TAG, "response was null, returning null");
            return null;
        }

        String tokenString = getSessionTokenResponse.getSessionToken();
        String secretKey = getSessionTokenResponse.getSecretKey();
        String time = getSessionTokenResponse.getTime();
        String pkey = getSessionTokenResponse.getPkey();
        String ekey = getSessionTokenResponse.getEkey();
        MFSessionToken mfSessionToken = new MFSessionToken(tokenString, secretKey, time, pkey, ekey);
        MFConfiguration.getStaticMFLogger().v(TAG, "created new token: " + mfSessionToken.toString());
        return mfSessionToken;
    }
}
