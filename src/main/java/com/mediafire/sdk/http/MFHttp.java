package com.mediafire.sdk.http;

import com.mediafire.sdk.config.MFConfiguration;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public abstract class MFHttp {
    private static final String TAG = MFHttp.class.getCanonicalName();
    protected final MFConfiguration mfConfiguration;

    public MFHttp (MFConfiguration mfConfiguration) {
        if (mfConfiguration == null) {
            throw new IllegalArgumentException("configuration cannot be null");
        }
        this.mfConfiguration = mfConfiguration;
    }

    protected final String makeQueryString(Map<String, String> requestParameters, boolean urlEncode) throws UnsupportedEncodingException {
        MFConfiguration.getStaticMFLogger().v(TAG, "making query string. url encoding: " + urlEncode);
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : requestParameters.keySet()) {
            stringBuilder.append("&");
            stringBuilder.append(key);
            stringBuilder.append("=");
            stringBuilder.append(urlEncodedQueryValue(requestParameters.get(key)));
        }
        String queryString = stringBuilder.toString().substring(1);
        MFConfiguration.getStaticMFLogger().v(TAG, "made query string - " + queryString);
        return queryString;
    }

    protected final String urlEncodedQueryValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "UTF-8");
    }

    protected final String makeUrlAttachableQueryString(String queryString) {
        MFConfiguration.getStaticMFLogger().v(TAG, "making a url attachable query string");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("?");
        stringBuilder.append(queryString);
        MFConfiguration.getStaticMFLogger().v(TAG, "made query string - " + stringBuilder.toString());
        return stringBuilder.toString();
    }

    protected final String makeBaseUrl(MFRequester mfRequester) {
        MFConfiguration.getStaticMFLogger().v(TAG, "making a base url");
        String scheme = mfRequester.getTransferProtocol().getScheme();
        String host = mfRequester.getHost();
        String uri = mfRequester.getUri();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(scheme);
        stringBuilder.append(host);
        stringBuilder.append(uri);
        MFConfiguration.getStaticMFLogger().v(TAG, "made base url - " + stringBuilder.toString());
        return stringBuilder.toString();
    }
}
