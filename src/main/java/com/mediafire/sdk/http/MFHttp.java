package com.mediafire.sdk.http;

import com.mediafire.sdk.config.MFConfiguration;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Chris Najar on 7/16/2014.
 */
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
        mfConfiguration.getMfLogger().logMessage(TAG, "making query string. url encoding: " + urlEncode);
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : requestParameters.keySet()) {
            stringBuilder.append("&");
            stringBuilder.append(key);
            stringBuilder.append("=");
            stringBuilder.append(urlEncodedQueryValue(requestParameters.get(key)));
        }
        String queryString = stringBuilder.toString().substring(1);
        mfConfiguration.getMfLogger().logMessage(TAG, "made query string - " + queryString);
        return queryString;
    }

    protected final String urlEncodedQueryValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "UTF-8");
    }

    protected final String makeUrlAttachableQueryString(String queryString) {
        mfConfiguration.getMfLogger().logMessage(TAG, "making a url attachable query string");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("?");
        stringBuilder.append(queryString);
        mfConfiguration.getMfLogger().logMessage(TAG, "made query string - " + stringBuilder.toString());
        return stringBuilder.toString();
    }

    protected final String makeBaseUrl(MFRequest mfRequest) {
        mfConfiguration.getMfLogger().logMessage(TAG, "making a base url");
        String scheme = mfRequest.getMfHost().getTransferScheme().getScheme();
        String host = mfRequest.getMfHost().getHost();
        String uri = mfRequest.getMfApi().getUri();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(scheme);
        stringBuilder.append(host);
        stringBuilder.append(uri);
        mfConfiguration.getMfLogger().logMessage(TAG, "made base url - " + stringBuilder.toString());
        return stringBuilder.toString();
    }
}
