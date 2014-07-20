package com.mediafire.sdk.http;

public enum MFHost {
    LIVE_HTTP(Host.LIVE, TransferProtocol.HTTP),
    LIVE_HTTPS(Host.LIVE, TransferProtocol.HTTPS),
    DEV_HTTP(Host.DEV, TransferProtocol.HTTP),
    DEV_HTTPS(Host.DEV, TransferProtocol.HTTPS);

    private final Host host;
    private final TransferProtocol transferProtocol;

    private MFHost(Host host, TransferProtocol transferProtocol) {
        this.host = host;
        this.transferProtocol = transferProtocol;
    }

    public Host getHost() {
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

    public enum Host {
        LIVE("www.mediafire.com"),
        DEV("www.mediafire.com");

        private final String host;

        private Host(String host) {
            this.host = host;
        }

        public String getSubDomainAndHostName() {
            return host;
        }
    }
}
