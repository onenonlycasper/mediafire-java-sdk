package com.mediafire.sdk;

/**
 * Created by Chris Najar on 7/17/2014.
 */
public enum MFHost {
    LIVE_HTTP("www.mediafire.com", TransferScheme.HTTP),
    LIVE_HTTPS("www.mediafire.com", TransferScheme.HTTPS),
    DEV_HTTP("dev.mediafire.com", TransferScheme.HTTP),
    DEV_HTTPS("dev.mediafire.com", TransferScheme.HTTPS);

    private String host;
    private TransferScheme transferScheme;

    private MFHost(String host, TransferScheme transferScheme) {
        this.host = host;
        this.transferScheme = transferScheme;
    }

    public String getHost() {
        return host;
    }

    public TransferScheme getTransferScheme() {
        return transferScheme;
    }

    public enum TransferScheme {
        HTTP("http://"),
        HTTPS("https://");

        private String scheme;

        private TransferScheme(String scheme) {
            this.scheme = scheme;
        }

        public String getScheme() {
            return scheme;
        }
    }
}
