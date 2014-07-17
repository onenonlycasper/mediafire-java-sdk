package com.mediafire.sdk.http;

import com.mediafire.sdk.config.MFConfiguration;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public abstract class MFHttp {
    protected final MFConfiguration mfConfiguration;

    public MFHttp (MFConfiguration mfConfiguration) {
        if (mfConfiguration == null) {
            throw new IllegalArgumentException("configuration cannot be null");
        }
        this.mfConfiguration = mfConfiguration;
    }
        
    protected final String makeQueryString(Map<String, String> requestParameters, boolean urlEncode) throws UnsupportedEncodingException {
        System.out.println("making query string. url encoding: " + urlEncode);
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : requestParameters.keySet()) {
            stringBuilder.append("&");
            stringBuilder.append(key);
            stringBuilder.append("=");
            stringBuilder.append(urlEncodedQueryValue(requestParameters.get(key)));
        }
        System.out.println("made query string - " + stringBuilder.toString());
        return stringBuilder.toString().substring(1);
    }

    protected final String urlEncodedQueryValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "UTF-8");
    }

    protected final String makeUrlAttachableQueryString(String queryString) {
        System.out.println("making a url attachable query string");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("?");
        stringBuilder.append(queryString);
        System.out.println("made query string - " + stringBuilder.toString());
        return stringBuilder.toString();
    }

    protected final String makeBaseUrl(MFRequest request) {
        System.out.println("making a base url");
        String scheme = request.getMfHost().getTransferScheme().getScheme();
        String host = request.getMfHost().getHost();
        String uri = request.getMfApi().getUri();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(scheme);
        stringBuilder.append(host);
        stringBuilder.append(uri);
        System.out.println("made base url - " + stringBuilder.toString());
        return stringBuilder.toString();
    }
}
