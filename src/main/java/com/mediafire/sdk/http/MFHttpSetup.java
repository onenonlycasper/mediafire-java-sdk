package com.mediafire.sdk.http;

import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.config.MFCredentials;
import com.mediafire.sdk.config.MFDefaultCredentials;
import com.mediafire.sdk.token.MFImageActionToken;
import com.mediafire.sdk.token.MFSessionToken;
import com.mediafire.sdk.token.MFTokenDistributor;
import com.mediafire.sdk.token.MFUploadActionToken;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public final class MFHttpSetup extends MFHttp {
    private static final String SHA1 = "SHA-1";
    private static final String MD5 = "MD5";

    private MFTokenDistributor mfTokenDistributor;
    private MFCredentials mfCredentials;

    public MFHttpSetup(MFTokenDistributor mfTokenDistributor, MFConfiguration mfConfiguration, MFCredentials mfCredentials) {
        super(mfConfiguration);
        this.mfTokenDistributor = mfTokenDistributor;
        this.mfCredentials = mfCredentials;
    }

    public void prepareMFRequestForHttpClient(MFRequest request) throws UnsupportedEncodingException {
        // borrow token, if necessary
        borrowToken(request);
        // add token, if necessary, to request parameters
        addTokenToRequestParameters(request);
        // add signature, if necessary, to request parameters
        addSignatureToRequestParameters(request);
    }

    private void addSignatureToRequestParameters(MFRequest request) throws UnsupportedEncodingException {
        switch (request.getMfApi().getTokenType()) {
            case SESSION_TOKEN_V2:
                String recycledSessionTokenSignature = calculateSignature(request);
                request.getRequestParameters().put("signature", recycledSessionTokenSignature);
                break;
            case UNIQUE:
                String newSessionTokenSignature = calculateSignature(mfConfiguration, mfCredentials);
                request.getRequestParameters().put("signature", newSessionTokenSignature);
                break;
            default:
                // for types NONE, UPLOAD_ACTION_TOKEN, IMAGE_ACTION_TOKEN
                // there is no need to attach a signature to the request parameters
                break;
        }
    }

    private String calculateSignature(MFConfiguration MFConfiguration, MFCredentials credentials) {
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
        switch (request.getMfApi().getTokenType()) {
            case SESSION_TOKEN_V2:
            case UPLOAD_ACTION_TOKEN:
            case IMAGE_ACTION_TOKEN:
                String tokenString = request.getToken().getTokenString();
                request.getRequestParameters().put("session_token", tokenString);
                break;
            default:
                // for types NONE, UNIQUE
                // there is no need to attach a session token to the request parameters
                break;
        }
    }

    private void borrowToken(MFRequest request) {
        switch (request.getMfApi().getTokenType()) {
            case SESSION_TOKEN_V2:
                MFSessionToken sessionToken = mfTokenDistributor.borrowSessionToken();
                request.setToken(sessionToken);
                break;
            case UPLOAD_ACTION_TOKEN:
                MFUploadActionToken uploadActionToken = mfTokenDistributor.borrowUploadActionToken();
                request.setToken(uploadActionToken);
                break;
            case IMAGE_ACTION_TOKEN:
                MFImageActionToken imageActionToken = mfTokenDistributor.borrowImageActionToken();
                request.setToken(imageActionToken);
                break;
            default:
                // for type NONE, UNIQUE there is no need to request a token.
                break;
        }
    }

    private String hashString(String target, String hashAlgorithm) {
        System.out.println("hashing to " + hashAlgorithm + " - " + target);
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

        System.out.println("hashing to " + hashAlgorithm + " - " + hash);
        return hash;
    }
}
