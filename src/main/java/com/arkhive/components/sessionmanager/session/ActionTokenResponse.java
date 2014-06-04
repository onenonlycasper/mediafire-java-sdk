package com.arkhive.components.sessionmanager.session;

/** Stores a response from a request for an action token.
 */
public class ActionTokenResponse {

    //CHECKSTYLE:OFF    The fields must match the fields returned in the JSON response.
    private String action;
    private String action_token;
    private String result;
    private String current_api_version;
    //CHECKSTYLE:ON

    public ActionTokenResponse() {}

    public String getAction() { return action; }
    public String getSessionToken() { return action_token; }
    public String getResult() { return result; }
    public String getCurrentAPIVersion() { return current_api_version; }
}
