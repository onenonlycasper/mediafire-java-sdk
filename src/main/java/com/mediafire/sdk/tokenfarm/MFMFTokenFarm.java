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
public class MFMFTokenFarm implements MFTokenDistributor {
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

    public MFMFTokenFarm(Configuration configuration, ApplicationCredentials applicationCredentials) {
        int maximumSessionTokens = configuration.getMaximumSessionTokens();
        sessionTokens = new LinkedBlockingQueue<MFSessionToken>(maximumSessionTokens);
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
