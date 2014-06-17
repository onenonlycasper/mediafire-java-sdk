package com.arkhive.components.api.system;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.arkhive.components.api.ApiRequest;
import com.arkhive.components.api.ApiRequestBuilder;
import com.arkhive.components.api.Utility;
import com.arkhive.components.sessionmanager.SessionManager;

/**
 * api calls to /api/system.
 *
 * @author Chris Najar
 */
public class System {
    private static final String GET_INFO_URI = "/api/system/get_info.php";

    /**
     * Makes a call to the api system/get_info.
     *
     * @param sm - session manager to make the request.
     * @return a SystemGetInfoResponse containing the server response.
     */
    public static SystemGetInfoResponse getInfo(SessionManager sm) {
        Map<String, String> parameters = new HashMap<String, String>();
        JsonElement jsonResponse = sendRequest(parameters, GET_INFO_URI, sm);
        return new Gson().fromJson(jsonResponse, SystemGetInfoResponse.class);
    }

    /**
     * Submit a request to the API.
     *
     * @param parameters A Map<String, String> of parameters to pass to the API.
     * @param apiCall    The API call to make.
     * @param sm         The SessionManager to use for the API operation.
     * @return A JsonElement containing the response from the API call.
     */
    private static JsonElement sendRequest(Map<String, String> parameters, String apiCall, SessionManager sm) {
        try {
            for (Entry<String, String> e : parameters.entrySet()) {
                e.setValue(URLEncoder.encode(e.getValue(), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
        ApiRequestBuilder builder = new ApiRequestBuilder();
        builder.domain(sm.getDomain());
        builder.sessionManager(sm);
        builder.httpInterface(sm.getHttpInterface());
        builder.parameters(parameters);
        builder.uri(apiCall);

        ApiRequest request = builder.build();

        String responseString = request.submitRequestSync();
        return Utility.getResponseElement(responseString);
    }
}
