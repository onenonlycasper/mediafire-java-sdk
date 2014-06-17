package com.arkhive.components.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Class holding utility functions that are needed throughout the application.
 */
public class Utility {
    private static final String FAIL_RES = "{\"response\":{\"message\":\"Unknown API error\",\"result\":\"Error\"}}";

    /**
     * Transform a string into a JsonElement.
     * <p/>
     * All resposne strings returned from the web api are wrapped in response json element.
     * This method strips the wrapper element, and converts the remaining element into a JsonElement via GSON.
     *
     * @param response A response string from a web API call.
     * @return The JsonElement created from the response string.
     */
    public static JsonElement getResponseElement(String response) {
        if (response.length() == 0 || response.isEmpty() || response == null) {
            response = FAIL_RES;
        }
        JsonElement returnJson = new JsonObject();
        JsonParser parser = new JsonParser();
        JsonElement rootElement = parser.parse(response);
        if (rootElement.isJsonObject()) {
            JsonElement jsonResult = rootElement.getAsJsonObject().get("response");
            if (jsonResult.isJsonObject()) {
                returnJson = jsonResult.getAsJsonObject();
            }
        }
        return returnJson;
    }

    /**
     * converts a String received from JSON format into a response String.
     *
     * @param response - the response received in JSON format
     * @return the response received which can then be parsed into a specific format as per Gson.fromJson()
     */
    public static String getResponseString(String response) {
        if (response.length() == 0 || response.isEmpty() || response == null) {
            response = FAIL_RES;
        }

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(response);
        if (element.isJsonObject()) {
            JsonObject jsonResponse = element.getAsJsonObject().get("response").getAsJsonObject();
//        System.out.println(TAG + " getResponseString() returning response: " + jsonResponse.toString());
            return jsonResponse.toString();
        } else {
            return FAIL_RES;
        }
    }
}
