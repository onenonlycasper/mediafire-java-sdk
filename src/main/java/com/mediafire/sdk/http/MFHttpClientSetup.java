package com.mediafire.sdk.http;

import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.config.MFCredentials;
import com.mediafire.sdk.config.MFDefaultCredentials;
import com.mediafire.sdk.token.MFImageActionToken;
import com.mediafire.sdk.token.MFSessionToken;
import com.mediafire.sdk.token.MFTokenFarmCallback;
import com.mediafire.sdk.token.MFUploadActionToken;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public final class MFHttpClientSetup extends MFHttp {
    private static final String TAG = MFHttpClientSetup.class.getCanonicalName();
    private static final String SHA1 = "SHA-1";
    private static final String MD5 = "MD5";

    private final MFTokenFarmCallback mfTokenFarmCallback;
    private final MFCredentials mfCredentials;

    public MFHttpClientSetup(MFTokenFarmCallback mfTokenFarmCallback, MFConfiguration mfConfiguration) {
        super(mfConfiguration);
        this.mfTokenFarmCallback = mfTokenFarmCallback;
        this.mfCredentials = mfConfiguration.getMfCredentials();
    }

    public void prepareMFRequestForHttpClient(MFRequester mfRequester) throws UnsupportedEncodingException {
        MFConfiguration.getStaticMFLogger().v(TAG, "prepareMFRequestForHttpClient()");
                // borrow token, if necessary
        borrowToken(mfRequester);
        // add token, if necessary, to request parameters
        addTokenToRequestParameters(mfRequester);
        // add signature, if necessary, to request parameters
        addSignatureToRequestParameters(mfRequester);
    }

    private void addRequestParametersForNewSessionToken(MFRequester mfRequest) {
        MFConfiguration.getStaticMFLogger().v(TAG, "addRequestParametersForNewSessionToken()");
        MFConfiguration.getStaticMFLogger().v(TAG, "adding user credentials");
        Map<String, String> credentialsMap = mfCredentials.getCredentials();
        mfRequest.getRequestParameters().putAll(credentialsMap);
        MFConfiguration.getStaticMFLogger().v(TAG, "adding app id");
        mfRequest.getRequestParameters().put("application_id", mfConfiguration.getAppId());
    }

    private void addSignatureToRequestParameters(MFRequester mfRequester) throws UnsupportedEncodingException {
        MFConfiguration.getStaticMFLogger().v(TAG, "addSignatureToRequestParameters()");
        switch (mfRequester.getTypeOfSignatureToAdd()) {
            case V2:
                MFConfiguration.getStaticMFLogger().v(TAG, "adding session token signature to request (api required: " + mfRequester.getTypeOfTokenToReturn() + ")");
                String recycledSessionTokenSignature = calculateSignatureForApiRequest(mfRequester);
                mfRequester.getRequestParameters().put("signature", recycledSessionTokenSignature);
                break;
            case NEW:
                MFConfiguration.getStaticMFLogger().v(TAG, "adding unique token signature to request (api required: " + mfRequester.getTypeOfTokenToReturn() + ")");
                // add additional request parameters required for this signature
                addRequestParametersForNewSessionToken(mfRequester);
                String newSessionTokenSignature = calculateSignatureForNewSessionToken(mfConfiguration, mfCredentials);
                mfRequester.getRequestParameters().put("signature", newSessionTokenSignature);
                break;
            default:
                // for types NONE, UPLOAD, IMAGE
                // there is no need to attach a signature to the request parameters
                MFConfiguration.getStaticMFLogger().v(TAG, "not adding signature to request (api required: " + mfRequester.getTypeOfTokenToReturn() + ")");
                break;
        }
    }

    private String calculateSignatureForNewSessionToken(MFConfiguration mfConfiguration, MFCredentials credentials) {
        MFConfiguration.getStaticMFLogger().v(TAG, "calculateSignatureForNewSessionToken()");
        // email + password + app id + api key
        // fb access token + app id + api key
        // tw oauth token + tw oauth token secret + app id + api key

        String userInfoPortionOfHashTarget = null;
        switch (credentials.getUserCredentialsType()) {
            case FACEBOOK:
                String fb_token_key = MFDefaultCredentials.FACEBOOK_PARAMETER_FB_ACCESS_TOKEN;
                userInfoPortionOfHashTarget = credentials.getCredentials().get(fb_token_key);
                break;
            case TWITTER:
                String tw_oauth_token = MFDefaultCredentials.TWITTER_PARAMETER_TW_OAUTH_TOKEN;
                String tw_oauth_token_secret = MFDefaultCredentials.TWITTER_PARAMETER_TW_OAUTH_TOKEN_SECRET;
                userInfoPortionOfHashTarget = credentials.getCredentials().get(tw_oauth_token) + credentials.getCredentials().get(tw_oauth_token_secret);
                break;
            case MEDIAFIRE:
                String mf_email = MFDefaultCredentials.MEDIAFIRE_PARAMETER_EMAIL;
                String mf_pass = MFDefaultCredentials.MEDIAFIRE_PARAMETER_PASSWORD;
                userInfoPortionOfHashTarget = credentials.getCredentials().get(mf_email) + credentials.getCredentials().get(mf_pass);
                break;
            case UNSET:
                throw new IllegalArgumentException("credentials must be set to call /api/user/get_session_token");
        }

        String appId = mfConfiguration.getAppId();
        String apiKey = mfConfiguration.getApiKey();

        String hashTarget = userInfoPortionOfHashTarget + appId + apiKey;

        return hashString(hashTarget, SHA1);
    }

    private String calculateSignatureForApiRequest(MFRequester mfRequester) throws UnsupportedEncodingException {
        MFConfiguration.getStaticMFLogger().v(TAG, "calculateSignatureForApiRequest()");
        // session token secret key + time + uri (concatenated)
        MFSessionToken sessionToken = (MFSessionToken) mfRequester.getToken();
        int secretKey = Integer.valueOf(sessionToken.getSecretKey()) % 256;
        String time = sessionToken.getTime();

        String nonUrlEncodedQueryString = makeQueryString(mfRequester.getRequestParameters(), false);
        String urlAttachableQueryString = makeUrlAttachableQueryString(nonUrlEncodedQueryString);
        String baseUri = mfRequester.getUri();

        String fullUri = baseUri + urlAttachableQueryString;

        String nonUrlEncodedString = String.valueOf(secretKey) + time + fullUri;
        return hashString(nonUrlEncodedString, MD5);
    }

    private void addTokenToRequestParameters(MFRequester mfRequester) {
        MFConfiguration.getStaticMFLogger().v(TAG, "addTokenToRequestParameters()");
        if (mfRequester.isTokenRequired() && mfRequester.getToken() != null) {
            MFConfiguration.getStaticMFLogger().v(TAG, "adding token to request parameters (required type: " + mfRequester.getTypeOfTokenToReturn() + ")");
            String tokenString = mfRequester.getToken().getTokenString();
            mfRequester.getRequestParameters().put("session_token", tokenString);
        } else {
            MFConfiguration.getStaticMFLogger().v(TAG, "not adding token request parameters (required type: " + mfRequester.getTypeOfTokenToReturn() + ")");
        }
    }

    private void borrowToken(MFRequester mfRequester) {
        MFConfiguration.getStaticMFLogger().v(TAG, "borrowToken()");
        switch (mfRequester.getTypeOfTokenToBorrow()) {
            case V2:
                MFConfiguration.getStaticMFLogger().v(TAG, "need to borrow session token (required type: " + mfRequester.getTypeOfTokenToReturn() + ")");
                MFSessionToken sessionToken = mfTokenFarmCallback.borrowMFSessionToken();
                mfRequester.setToken(sessionToken);
                break;
            case UPLOAD:
                MFConfiguration.getStaticMFLogger().v(TAG, "need to borrow upload action token (required type: " + mfRequester.getTypeOfTokenToReturn() + ")");
                MFUploadActionToken uploadActionToken = mfTokenFarmCallback.borrowMFUploadActionToken();
                mfRequester.setToken(uploadActionToken);
                break;
            case IMAGE:
                MFConfiguration.getStaticMFLogger().v(TAG, "need to borrow image action (required type: " + mfRequester.getTypeOfTokenToReturn() + ")");
                MFImageActionToken imageActionToken = mfTokenFarmCallback.borrowMFImageActionToken();
                mfRequester.setToken(imageActionToken);
                break;
            default:
                // for type NONE, NEW there is no need to request a token.
                MFConfiguration.getStaticMFLogger().v(TAG, "no need to borrow token (required type: " + mfRequester.getTypeOfTokenToReturn() + ")");
                break;
        }
    }

    private String hashString(String target, String hashAlgorithm) {
        MFConfiguration.getStaticMFLogger().v(TAG, "hashString()");
        MFConfiguration.getStaticMFLogger().v(TAG, "hashing to " + hashAlgorithm + " - " + target);
        String hash;
        try {
            MessageDigest md = MessageDigest.getInstance(hashAlgorithm);

            md.update(target.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuilder sb = new StringBuilder();
            for (byte aByteData : byteData) {
                sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
            }
            hash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            hash = target;
        }

        MFConfiguration.getStaticMFLogger().v(TAG, "hashed to " + hashAlgorithm + " - " + hash);
        return hash;
    }
}
