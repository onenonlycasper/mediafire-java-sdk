package com.mediafire.sdk;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.module_credentials.ApplicationCredentials;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by  on 6/16/2014.
 */
public final class MFTokenFarm implements MFTokenDistributor {
    private final Lock lockBorrowImageToken = new ReentrantLock();
    private final Lock lockBorrowUploadToken = new ReentrantLock();
    private final Condition conditionImageTokenNotExpired = lockBorrowImageToken.newCondition();
    private final Condition conditionUploadTokenNotExpired = lockBorrowUploadToken.newCondition();
    private final ApplicationCredentials applicationCredentials;
    private final BlockingQueue<MFSessionToken> sessionTokens;
    private MFUploadActionToken uploadActionToken;
    private MFImageActionToken imageActionToken;
    private final Object imageTokenLock = new Object();
    private final Object uploadTokenLock = new Object();

    public MFTokenFarm(Configuration configuration, ApplicationCredentials applicationCredentials) {
        int maximumSessionTokens = configuration.getMaximumSessionTokens();
        sessionTokens = new LinkedBlockingQueue<MFSessionToken>(maximumSessionTokens);
        this.applicationCredentials = applicationCredentials;
    }

    public void getNewSessionToken() {
        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("token_version", "2");
        MFHttpRequest mfHttpRequest = new MFHttpRequest(MFHost.LIVE_HTTPS, MFApi.URI_USER_GET_SESSION_TOKEN, requestParameters);

    }

    private void getNewImageActionToken() {
        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("lifespan", "1440");
        requestParameters.put("type", "image");
        MFHttpRequest mfHttpRequest = new MFHttpRequest(MFHost.LIVE_HTTP, MFApi.URI_USER_GET_ACTION_TOKEN, requestParameters);
    }

    private void getNewUploadActionToken() {
        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("lifespan", "1440");
        requestParameters.put("type", "upload");
        MFHttpRequest mfHttpRequest = new MFHttpRequest(MFHost.LIVE_HTTP, MFApi.URI_USER_GET_ACTION_TOKEN, requestParameters);
    }

    public void shutdown() {
        sessionTokens.clear();
        uploadActionToken = null;
        imageActionToken = null;
    }

    @Override
    public void returnSessionToken(MFSessionToken sessionToken) {

    }

    @Override
    public void receiveNewSessionToken(MFSessionToken sessionToken) {

    }

    @Override
    public void receiveNewImageActionToken(MFImageActionToken uploadActionToken) {

    }

    @Override
    public void receiveNewUploadActionToken(MFUploadActionToken uploadActionToken) {

    }

    @Override
    public MFSessionToken borrowSessionToken() {
        return null;
    }

    @Override
    public MFUploadActionToken borrowUploadActionToken() {
        return null;
    }

    @Override
    public MFImageActionToken borrowImageActionToken() {
        return null;
    }
}
