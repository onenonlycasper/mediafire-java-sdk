package com.mediafire.sdk.http;

public enum MFHost {
    LIVE_HTTP("www.mediafire.com", TransferProtocol.HTTP),
    LIVE_HTTPS("www.mediafire.com", TransferProtocol.HTTPS),
    DEV_HTTP("dev.mediafire.com", TransferProtocol.HTTP),
    DEV_HTTPS("dev.mediafire.com", TransferProtocol.HTTPS);

    private final String host;
    private final TransferProtocol transferProtocol;

    private MFHost(String host, TransferProtocol transferProtocol) {
        this.host = host;
        this.transferProtocol = transferProtocol;
    }

    public String getHost() {
        return host;
    }

    public TransferProtocol getTransferProtocol() {
        return transferProtocol;
    }

    public enum TransferProtocol {
        HTTP("http://"),
        HTTPS("https://");

        private final String scheme;

        private TransferProtocol(String scheme) {
            this.scheme = scheme;
        }

        public String getScheme() {
            return scheme;
        }
    }
}
