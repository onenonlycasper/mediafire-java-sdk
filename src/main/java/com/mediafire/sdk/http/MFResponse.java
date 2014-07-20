package com.mediafire.sdk.http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mediafire.sdk.api_responses.ApiResponse;

import java.util.List;
import java.util.Map;

public final class MFResponse {
    private final int status;
    private final Map<String, List<String>> headers;
    private final byte[] bodyBytes;
    private final MFRequester mfRequester;

    public MFResponse(int status, Map<String, List<String>> headers, byte[] bodyBytes, MFRequester mfRequester) {
        this.status = status;
        this.headers = headers;
        this.bodyBytes = bodyBytes;
        this.mfRequester = mfRequester;
    }

    public int getStatus() {
        return status;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public byte[] getResponseAsBytes() {
        return bodyBytes;
    }

    public String getResponseAsString() {
        return new String(bodyBytes);
    }

    public <ResponseClass extends ApiResponse> ResponseClass getResponseObject(Class<ResponseClass> responseClass) {
        String responseString = getResponseAsString();
        if (responseString == null) {
            return null;
        }
        return new Gson().fromJson(getResponseStringForGson(responseString), responseClass);
    }

    public MFRequester getOriginMFRequester() {
        return mfRequester;
    }

    private String getResponseStringForGson(String response) {
        if (response == null || response.isEmpty()) {
            return null;
        }

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(response);
        if (element.isJsonObject()) {
            JsonObject jsonResponse = element.getAsJsonObject().get("response").getAsJsonObject();
            return jsonResponse.toString();
        } else {
            return null;
        }
    }
}