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
        MFConfiguration.getStaticMFLogger().v(TAG, "prepareMFRequestForHttpClient()");
                // borrow token, if necessary
        borrowToken(request);
        // add token, if necessary, to request parameters
        addTokenToRequestParameters(request);
        // add signature, if necessary, to request parameters
        addSignatureToRequestParameters(request);
    }

    private void addRequestParametersForNewSessionToken(MFRequest mfRequest) {
        MFConfiguration.getStaticMFLogger().v(TAG, "addRequestParametersForNewSessionToken()");
        MFConfiguration.getStaticMFLogger().v(TAG, "adding user credentials");
        Map<String, String> credentialsMap = mfCredentials.getCredentials();
        mfRequest.getRequestParameters().putAll(credentialsMap);
        MFConfiguration.getStaticMFLogger().v(TAG, "adding app id");
        mfRequest.getRequestParameters().put("application_id", mfConfiguration.getAppId());
    }

    private void addSignatureToRequestParameters(MFRequest request) throws UnsupportedEncodingException {
        MFConfiguration.getStaticMFLogger().v(TAG, "addSignatureToRequestParameters()");
        switch (request.getMfApi().getTokenType()) {
            case SESSION_TOKEN_V2:
                MFConfiguration.getStaticMFLogger().v(TAG, "adding session token signature to request (api required: " + request.getMfApi().getTokenType() + ")");
                String recycledSessionTokenSignature = calculateSignatureForApiRequest(request);
                request.getRequestParameters().put("signature", recycledSessionTokenSignature);
                break;
            case UNIQUE:
                MFConfiguration.getStaticMFLogger().v(TAG, "adding unique token signature to request (api required: " + request.getMfApi().getTokenType() + ")");
                // add additional request parameters required for this signature
                addRequestParametersForNewSessionToken(request);
                String newSessionTokenSignature = calculateSignatureForNewSessionToken(mfConfiguration, mfCredentials);
                request.getRequestParameters().put("signature", newSessionTokenSignature);
                break;
            default:
                // for types NONE, UPLOAD_ACTION_TOKEN, IMAGE_ACTION_TOKEN
                // there is no need to attach a signature to the request parameters
                MFConfiguration.getStaticMFLogger().v(TAG, "not adding signature to request (api required: " + request.getMfApi().getTokenType() + ")");
                break;
        }
    }

    private String calculateSignatureForNewSessionToken(MFConfiguration MFConfiguration, MFCredentials credentials) {
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

        String appId = MFConfiguration.getAppId();
        String apiKey = MFConfiguration.getApiKey();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(userInfoPortionOfHashTarget);
        stringBuilder.append(appId);
        stringBuilder.append(apiKey);

        String hashTarget = stringBuilder.toString();

        return hashString(hashTarget, SHA1);
    }

    private String calculateSignatureForApiRequest(MFRequest request) throws UnsupportedEncodingException {
        MFConfiguration.getStaticMFLogger().v(TAG, "calculateSignatureForApiRequest()");
        // session token secret key + time + uri (concatenated)
        MFSessionToken sessionToken = (MFSessionToken) request.getToken();
        int secretKey = Integer.valueOf(sessionToken.getSecretKey()) % 256;
        String time = sessionToken.getTime();

        String nonUrlEncodedQueryString = makeQueryString(request.getRequestParameters(), false);
        String urlAttachableQueryString = makeUrlAttachableQueryString(nonUrlEncodedQueryString);
        String baseUri = request.getMfApi().getUri();

        StringBuilder uriStringBuilder = new StringBuilder();
        uriStringBuilder.append(baseUri);
        uriStringBuilder.append(urlAttachableQueryString);

        String fullUri = uriStringBuilder.toString();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(secretKey);
        stringBuilder.append(time);
        stringBuilder.append(fullUri);
        String nonUrlEncodedString = stringBuilder.toString();
        return hashString(nonUrlEncodedString, MD5);
    }

    private void addTokenToRequestParameters(MFRequest request) {
        MFConfiguration.getStaticMFLogger().v(TAG, "addTokenToRequestParameters()");
        switch (request.getMfApi().getTokenType()) {
            case SESSION_TOKEN_V2:
            case UPLOAD_ACTION_TOKEN:
            case IMAGE_ACTION_TOKEN:
                MFConfiguration.getStaticMFLogger().v(TAG, "adding token to request parameters (required type: " + request.getMfApi().getTokenType() + ")");
                String tokenString = request.getToken().getTokenString();
                request.getRequestParameters().put("session_token", tokenString);
                break;
            default:
                // for types NONE, UNIQUE
                // there is no need to attach a session token to the request parameters
                MFConfiguration.getStaticMFLogger().v(TAG, "not adding token request parameters (required type: " + request.getMfApi().getTokenType() + ")");
                break;
        }
    }

    private void borrowToken(MFRequest request) {
        MFConfiguration.getStaticMFLogger().v(TAG, "borrowToken()");
        switch (request.getMfApi().getTokenType()) {
            case SESSION_TOKEN_V2:
                MFConfiguration.getStaticMFLogger().v(TAG, "need to borrow session token (required type: " + request.getMfApi().getTokenType() + ")");
                MFSessionToken sessionToken = mfTokenFarmCallback.borrowSessionToken();
                request.setToken(sessionToken);
                break;
            case UPLOAD_ACTION_TOKEN:
                MFConfiguration.getStaticMFLogger().v(TAG, "need to borrow upload action token (required type: " + request.getMfApi().getTokenType() + ")");
                MFUploadActionToken uploadActionToken = mfTokenFarmCallback.borrowUploadActionToken();
                request.setToken(uploadActionToken);
                break;
            case IMAGE_ACTION_TOKEN:
                MFConfiguration.getStaticMFLogger().v(TAG, "need to borrow image action (required type: " + request.getMfApi().getTokenType() + ")");
                MFImageActionToken imageActionToken = mfTokenFarmCallback.borrowImageActionToken();
                request.setToken(imageActionToken);
                break;
            default:
                // for type NONE, UNIQUE there is no need to request a token.
                MFConfiguration.getStaticMFLogger().v(TAG, "no need to borrow token (required type: " + request.getMfApi().getTokenType() + ")");
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
