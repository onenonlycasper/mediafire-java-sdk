package com.arkhive.components.api.user.avatar;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

import com.arkhive.components.api.ApiRequest;
import com.arkhive.components.api.ApiRequestBuilder;
import com.arkhive.components.api.Utility;
import com.arkhive.components.sessionmanager.SessionManager;

/** Gets and sets the avatar information. */
public class Avatar {
    private static final String GET_URI = "/api/user/get_avatar.php";
    private static final String SET_URI = "/api/user/set_avatar.php";

    /** Get the avatar of the currently logged in user.
     *
     * @param  sessionManager  A valid SessionManager.
     *
     * @return  A populated GetAvatarResponse.
     */
    public static GetAvatarResponse getAvatar(SessionManager sessionManager) {
        ApiRequestBuilder builder = new ApiRequestBuilder();
        builder.domain(sessionManager.getDomain());
        builder.sessionManager(sessionManager);
        builder.httpInterface(sessionManager.getHttpInterface());
        builder.parameters(new HashMap<String, String>());
        builder.uri(GET_URI);
        ApiRequest request = builder.build();
        String responseString = request.submitRequestSync();
        JsonElement jsonResponse = Utility.getResponseElement(responseString);
        Gson gson = new Gson();
        return gson.fromJson(jsonResponse, GetAvatarResponse.class);
    }

    /** Set the avatar of the logged in user with an image stored on the web.
     *
     * @param  avatarUrl  The URL of the image to use.
     * @param  sessionManager  A valid SessionManager.
     *
     * @return A populated SetAvatarResponse.
     */
    public static SetAvatarResponse setAvatarWithUrl(String avatarUrl, SessionManager sessionManager) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("url", avatarUrl);
        parameters.put("action", "normal");
        ApiRequest request = new ApiRequestBuilder()
            .parameters(parameters)
            .uri(SET_URI)
            .domain(sessionManager.getDomain())
            .sessionManager(sessionManager)
            .httpInterface(sessionManager.getHttpInterface())
            .build();
        String responseString = request.submitRequestSync();
        JsonElement jsonResponse = Utility.getResponseElement(responseString);
        Gson gson = new Gson();
        return gson.fromJson(jsonResponse, SetAvatarResponse.class);
    }

    /** Set the avatar of the logged in user with a quick_key.
     *
     * @param  quickKey  The quick_key of the image.
     * @param  sessionManager  A valid SessionManager.
     *
     * @return  A populated SetAvatarResponse.
     */
    public static SetAvatarResponse setAvatarWithQuickKey(String quickKey, SessionManager sessionManager) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("quick_key", quickKey);
        ApiRequest request = new ApiRequestBuilder()
            .parameters(parameters)
            .uri(SET_URI)
            .domain(sessionManager.getDomain())
            .sessionManager(sessionManager)
            .httpInterface(sessionManager.getHttpInterface())
            .build();
        String responseString = request.submitRequestSync();
        JsonElement jsonResponse = Utility.getResponseElement(responseString);
        Gson gson = new Gson();
        return gson.fromJson(jsonResponse, SetAvatarResponse.class);
    }

    /** Unsets the avatar of the logged in user.
     * <p>
     * Calling this method will change the users avatar back to the default avatar. Their avatar will still
     * be stored in their account, and they are able to restore it from their preferences menu on the website.
     *
     * @param  sessionManager  A valid SessionManager.
     *
     * @return  A populated SetAvatarResponse.
     */
    public static SetAvatarResponse deleteAvatar(SessionManager sessionManager) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("action", "set_default");
        ApiRequest request = new ApiRequestBuilder()
            .parameters(parameters)
            .uri(SET_URI)
            .domain(sessionManager.getDomain())
            .sessionManager(sessionManager)
            .httpInterface(sessionManager.getHttpInterface())
            .build();
        String responseString = request.submitRequestSync();
        JsonElement jsonResponse = Utility.getResponseElement(responseString);
        Gson gson = new Gson();
        return gson.fromJson(jsonResponse, SetAvatarResponse.class);
    }
}

