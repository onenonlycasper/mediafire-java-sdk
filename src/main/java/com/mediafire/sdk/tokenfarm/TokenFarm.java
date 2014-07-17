package com.mediafire.sdk.tokenfarm;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.module_credentials.ApplicationCredentials;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by  on 6/16/2014.
 */
public class TokenFarm {
    private final Lock lockBorrowImageToken = new ReentrantLock();
    private final Lock lockBorrowUploadToken = new ReentrantLock();
    private final Condition conditionImageTokenNotExpired = lockBorrowImageToken.newCondition();
    private final Condition conditionUploadTokenNotExpired = lockBorrowUploadToken.newCondition();
    private final ApplicationCredentials applicationCredentials;
    private final BlockingQueue<SessionToken> sessionTokens;
    private UploadActionToken uploadActionToken;
    private ImageActionToken imageActionToken;
    private final Object imageTokenLock = new Object();
    private final Object uploadTokenLock = new Object();

    public TokenFarm(Configuration configuration, ApplicationCredentials applicationCredentials) {
        int maximumSessionTokens = configuration.getMaximumSessionTokens();
        sessionTokens = new LinkedBlockingQueue<SessionToken>(maximumSessionTokens);
        this.applicationCredentials = applicationCredentials;
    }

    public void getNewSessionToken() {
    }

    private void getNewImageActionToken() {
    }

    private void getNewUploadActionToken() {

    }

    public void shutdown() {
        sessionTokens.clear();
        uploadActionToken = null;
        imageActionToken = null;
    }

    public void receiveNewSessionToken(SessionToken sessionToken) {

    }

    public void returnSessionToken(SessionToken sessionToken) {

    }

    public void receiveNewImageActionToken(UploadActionToken uploadActionToken) {

    }

    public void receiveNewUploadActionToken(UploadActionToken uploadActionToken) {

    }

    public SessionToken borrowSessionToken() {
        return null;
    }

    public UploadActionToken borrowUploadActionToken() {
        return null;
    }

    public ImageActionToken borrowImageActionToken() {
        return null;
    }
}
