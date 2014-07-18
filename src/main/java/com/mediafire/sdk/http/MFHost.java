package com.mediafire.sdk.http;

public enum MFHost {
    LIVE_HTTP("www.mediafire.com", TransferScheme.HTTP),
    LIVE_HTTPS("www.mediafire.com", TransferScheme.HTTPS),
    DEV_HTTP("dev.mediafire.com", TransferScheme.HTTP),
    DEV_HTTPS("dev.mediafire.com", TransferScheme.HTTPS);

    private final String host;
    private final TransferScheme transferScheme;

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

        private final String scheme;

        private TransferScheme(String scheme) {
            this.scheme = scheme;
        }

        public String getScheme() {
            return scheme;
        }
    }
}
