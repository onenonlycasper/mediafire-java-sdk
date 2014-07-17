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

/**
 * Created by Chris Najar on 7/16/2014.
 */
public final class MFHttpClientSetup extends MFHttp {
    private static final String TAG = MFHttpClientSetup.class.getCanonicalName();
    private static final String SHA1 = "SHA-1";
    private static final String MD5 = "MD5";

    private MFTokenFarmCallback mfTokenFarmCallback;
    private MFCredentials mfCredentials;

    public MFHttpClientSetup(MFTokenFarmCallback mfTokenFarmCallback, MFConfiguration mfConfiguration) {
        super(mfConfiguration);
        this.mfTokenFarmCallback = mfTokenFarmCallback;
        this.mfCredentials = mfConfiguration.getMfCredentials();
    }

    public void prepareMFRequestForHttpClient(MFRequest request) throws UnsupportedEncodingException {
        mfConfiguration.getMfLogger().logMessage(TAG, "prepareMFRequestForHttpClient()");
                // borrow token, if necessary
        borrowToken(request);
        // add token, if necessary, to request parameters
        addTokenToRequestParameters(request);
        // add signature, if necessary, to request parameters
        addSignatureToRequestParameters(request);
    }

    private void addRequestParametersForNewSessionToken(MFRequest mfRequest) {
        mfConfiguration.getMfLogger().logMessage(TAG, "addRequestParametersForNewSessionToken()");
        mfConfiguration.getMfLogger().logMessage(TAG, "adding user credentials");
        Map<String, String> credentialsMap = mfCredentials.getCredentials();
        mfRequest.getRequestParameters().putAll(credentialsMap);
        mfConfiguration.getMfLogger().logMessage(TAG, "adding app id");
        mfRequest.getRequestParameters().put("application_id", mfConfiguration.getAppId());
    }

    private void addSignatureToRequestParameters(MFRequest request) throws UnsupportedEncodingException {
        mfConfiguration.getMfLogger().logMessage(TAG, "addSignatureToRequestParameters()");
        switch (request.getMfApi().getTokenType()) {
            case SESSION_TOKEN_V2:
                mfConfiguration.getMfLogger().logMessage(TAG, "adding session token signature to request (api required: " + request.getMfApi().getTokenType() + ")");
                String recycledSessionTokenSignature = calculateSignature(request);
                request.getRequestParameters().put("signature", recycledSessionTokenSignature);
                break;
            case UNIQUE:
                mfConfiguration.getMfLogger().logMessage(TAG, "adding unique token signature to request (api required: " + request.getMfApi().getTokenType() + ")");
                // add additional request parameters required for this signature
                addRequestParametersForNewSessionToken(request);
                String newSessionTokenSignature = calculateSignature(mfConfiguration, mfCredentials);
                request.getRequestParameters().put("signature", newSessionTokenSignature);
                break;
            default:
                // for types NONE, UPLOAD_ACTION_TOKEN, IMAGE_ACTION_TOKEN
                // there is no need to attach a signature to the request parameters
                mfConfiguration.getMfLogger().logMessage(TAG, "not adding signature to request (api required: " + request.getMfApi().getTokenType() + ")");
                break;
        }
    }

    private String calculateSignature(MFConfiguration MFConfiguration, MFCredentials credentials) {
        mfConfiguration.getMfLogger().logMessage(TAG, "calculateSignature()");
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

        String appId = MFConfiguration.getAppId();
        String apiKey = MFConfiguration.getApiKey();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(userInfoPortionOfHashTarget);
        stringBuilder.append(appId);
        stringBuilder.append(apiKey);

        String hashTarget = stringBuilder.toString();

        return hashString(hashTarget, SHA1);
    }

    private String calculateSignature(MFRequest request) throws UnsupportedEncodingException {
        mfConfiguration.getMfLogger().logMessage(TAG, "calculateSignature()");
        // session token secret key + time + uri (concatenated)
        MFSessionToken sessionToken = (MFSessionToken) request.getToken();
        int secretKey = Integer.valueOf(sessionToken.getSecretKey()) % 256;
        String time = sessionToken.getTime();

        String baseUrl = makeBaseUrl(request);
        String nonUrlEncodedQueryString = makeQueryString(request.getRequestParameters(), false);
        String urlAttachableQueryString = makeUrlAttachableQueryString(nonUrlEncodedQueryString);

        StringBuilder uriStringBuilder = new StringBuilder();
        uriStringBuilder.append(baseUrl);
        uriStringBuilder.append(urlAttachableQueryString);

        String uri = uriStringBuilder.toString();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(secretKey);
        stringBuilder.append(time);
        stringBuilder.append(uri);
        String nonUrlEncodedString = stringBuilder.toString();
        return hashString(nonUrlEncodedString, MD5);
    }

    private void addTokenToRequestParameters(MFRequest request) {
        mfConfiguration.getMfLogger().logMessage(TAG, "addTokenToRequestParameters()");
        switch (request.getMfApi().getTokenType()) {
            case SESSION_TOKEN_V2:
            case UPLOAD_ACTION_TOKEN:
            case IMAGE_ACTION_TOKEN:
                mfConfiguration.getMfLogger().logMessage(TAG, "adding token to request parameters (required type: " + request.getMfApi().getTokenType() + ")");
                String tokenString = request.getToken().getTokenString();
                request.getRequestParameters().put("session_token", tokenString);
                break;
            default:
                // for types NONE, UNIQUE
                // there is no need to attach a session token to the request parameters
                mfConfiguration.getMfLogger().logMessage(TAG, "not adding token request parameters (required type: " + request.getMfApi().getTokenType() + ")");
                break;
        }
    }

    private void borrowToken(MFRequest request) {
        mfConfiguration.getMfLogger().logMessage(TAG, "borrowToken()");
        switch (request.getMfApi().getTokenType()) {
            case SESSION_TOKEN_V2:
                mfConfiguration.getMfLogger().logMessage(TAG, "need to borrow session token (required type: " + request.getMfApi().getTokenType() + ")");
                MFSessionToken sessionToken = mfTokenFarmCallback.borrowSessionToken();
                request.setToken(sessionToken);
                break;
            case UPLOAD_ACTION_TOKEN:
                mfConfiguration.getMfLogger().logMessage(TAG, "need to borrow upload action token (required type: " + request.getMfApi().getTokenType() + ")");
                MFUploadActionToken uploadActionToken = mfTokenFarmCallback.borrowUploadActionToken();
                request.setToken(uploadActionToken);
                break;
            case IMAGE_ACTION_TOKEN:
                mfConfiguration.getMfLogger().logMessage(TAG, "need to borrow image action (required type: " + request.getMfApi().getTokenType() + ")");
                MFImageActionToken imageActionToken = mfTokenFarmCallback.borrowImageActionToken();
                request.setToken(imageActionToken);
                break;
            default:
                // for type NONE, UNIQUE there is no need to request a token.
                mfConfiguration.getMfLogger().logMessage(TAG, "no need to borrow token (required type: " + request.getMfApi().getTokenType() + ")");
                break;
        }
    }

    private String hashString(String target, String hashAlgorithm) {
        mfConfiguration.getMfLogger().logMessage(TAG, "hashString()");
        mfConfiguration.getMfLogger().logMessage(TAG, "hashing to " + hashAlgorithm + " - " + target);
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

        mfConfiguration.getMfLogger().logMessage(TAG, "hashed to " + hashAlgorithm + " - " + hash);
        return hash;
    }
}
