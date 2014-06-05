package com.arkhive.components.api.contacts;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

import com.arkhive.components.api.ApiRequest;
import com.arkhive.components.api.ApiRequestBuilder;
import com.arkhive.components.api.ApiResponse;
import com.arkhive.components.api.Utility;
import com.arkhive.components.sessionmanager.SessionManager;

/** Adds a contact via the web api. */
public class ContactManagement {
    private static final String GET_URI = "/api/contact/add.php";
    private static final String DELETE_URI = "/api/contact/delete.php";
    private static final String FETCH_URI = "/api/contact/fetch.php";

    public static ApiResponse addContact(Contact contact, SessionManager sessionManager) {
        ApiRequestBuilder builder = new ApiRequestBuilder();
        builder.domain(sessionManager.getDomain());
        builder.sessionManager(sessionManager);
        builder.httpInterface(sessionManager.getHttpInterface());
        builder.parameters(contact.getParameterList());
        builder.uri(GET_URI);

        ApiRequest request = builder.build();

        String responseString = request.submitRequestSync();
        JsonElement jsonResponse = Utility.getResponseElement(responseString);
        Gson gson = new Gson();
        return gson.fromJson(jsonResponse, ApiResponse.class);
    }

    public static ApiResponse deleteContact(String contactId, SessionManager sessionManager) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("contact_key", contactId);

        ApiRequestBuilder builder = new ApiRequestBuilder();
        builder.domain(sessionManager.getDomain())
                .sessionManager(sessionManager)
                .httpInterface(sessionManager.getHttpInterface())
                .parameters(parameters)
                .uri(DELETE_URI);

        ApiRequest request = builder.build();

        String responseString = request.submitRequestSync();
        JsonElement jsonResponse = Utility.getResponseElement(responseString);
        Gson gson = new Gson();
        return gson.fromJson(jsonResponse, ApiResponse.class);
    }

    public static ContactResponse fetchContacts(SessionManager sessionManager) {
        Map<String, String> parameters = new HashMap<String, String>();

        ApiRequest request = new ApiRequestBuilder()
                .sessionManager(sessionManager)
                .httpInterface(sessionManager.getHttpInterface())
                .parameters(parameters)
                .uri(FETCH_URI)
                .build();

        String responseString = request.submitRequestSync();
        JsonElement jsonResponse = Utility.getResponseElement(responseString);
        Gson gson = new Gson();
        return gson.fromJson(jsonResponse, ContactResponse.class);
    }
}

