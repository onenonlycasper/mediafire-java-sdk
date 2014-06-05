package com.arkhive.components.api.user;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.arkhive.components.api.ApiRequest;
import com.arkhive.components.api.ApiRequestBuilder;
import com.arkhive.components.api.ApiResponse;
import com.arkhive.components.api.Utility;
import com.arkhive.components.sessionmanager.SessionManager;

/**
 * 
 * @author Chris Najar
 *
 */
public class User {
  private static final String GET_INFO_URI = "/api/user/get_info.php";
  private static final String REGISTER_URI = "/api/user/register.php";
  private static final String LINK_FACEBOOK_URI = "/api/user/link_facebook.php";
  private static final String LINK_TWITTER_URI = "/api/user/link_twitter.php";
  private static final String UNLINK_FACEBOOK_URI = "/api/user/unlink_facebook.php";
  private static final String UNLINK_TWITTER_URI = "/api/user/unlink_twitter.php";
  private static final String GET_AVATAR_URI = "/api/user/get_avatar.php";
  private static final String SET_AVATAR_URI = "/api/user/set_avatar.php";

  /**Attempts to get user information.
   * @param sessionManager - the session manager to be used to process the request.
   * @return a UserGetInfoResponse containing the server response.
   */
  public static UserGetInfoResponse getInfo(SessionManager sessionManager) {
      Map<String, String> parameters = new HashMap<String, String>();
      JsonElement jsonResponse = sendRequest(parameters, GET_INFO_URI, sessionManager);
      return new Gson().fromJson(jsonResponse, UserGetInfoResponse.class);
  }

  /**Attempts to register a user.
   * @param sessionManager - the session manager to be used to process the request.
   * @return a UserRegisterResponse containing the server response.
   */
  public static UserRegisterResponse register(String email, String password, String appId, 
                                              SessionManager sessionManager) {
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("email", email);
    parameters.put("password", password);
    parameters.put("application_id", appId);

    try {
        for (Entry<String, String> e : parameters.entrySet()) {
            e.setValue(URLEncoder.encode(e.getValue(), "UTF-8"));
        }
    } catch (UnsupportedEncodingException e) {
        throw new IllegalStateException(e);
    }
    ApiRequestBuilder builder = new ApiRequestBuilder();
    builder.domain(sessionManager.getDomain().replace("http://", "https://"));
    builder.sessionManager(sessionManager);
    builder.httpInterface(sessionManager.getHttpInterface());
    builder.parameters(parameters);
    builder.uri(REGISTER_URI);

    ApiRequest request = builder.build();

    String responseString = request.submitRequestSync();
    
    JsonElement jsonResponse = Utility.getResponseElement(responseString);
    return new Gson().fromJson(jsonResponse, UserRegisterResponse.class);
  }

  /**Attempts to link a facebook account.
   * @param sessionManager - the session manager to be used to process the request.
   * @return an ApiResponse containing the server response.
   */
  public static ApiResponse linkFacebook(String facebookAccessToken, SessionManager sessionManager) {
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("fb_access_token", facebookAccessToken);
    JsonElement jsonResponse = sendRequest(parameters, LINK_FACEBOOK_URI, sessionManager);
    return new Gson().fromJson(jsonResponse, ApiResponse.class);
  }

  /**Attempts to link a twitter account.
   * @param sessionManager - the session manager to be used to process the request.
   * @return an ApiResponse containing the server response.
   */
  public static ApiResponse linkTwitter(String twitterOauthToken, String twitterOauthTokenSecret, 
                                        SessionManager sessionManager) {
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("tw_oauth_token", twitterOauthToken);
    parameters.put("tw_oauth_token_secret", twitterOauthTokenSecret);
    JsonElement jsonResponse = sendRequest(parameters, LINK_TWITTER_URI, sessionManager);
    return new Gson().fromJson(jsonResponse, ApiResponse.class);
  }

  /**Attempts to unlink a facebook account.
   * @param sessionManager - the session manager to be used to process the request.
   * @return an ApiResponse containing the server response.
   */
  public static ApiResponse unlinkFacebook(SessionManager sessionManager) {
    Map<String, String> parameters = new HashMap<String, String>();
    JsonElement jsonResponse = sendRequest(parameters, UNLINK_FACEBOOK_URI, sessionManager);
    return new Gson().fromJson(jsonResponse, ApiResponse.class);
  }

  /**Attempts to unlink a twitter account.
   * @param sessionManager - the session manager to be used to process the request.
   * @return an ApiResponse containing the server response.
   */
  public static ApiResponse unlinkTwitter(SessionManager sessionManager) {
    Map<String, String> parameters = new HashMap<String, String>();
    JsonElement jsonResponse = sendRequest(parameters, UNLINK_TWITTER_URI, sessionManager);
    return new Gson().fromJson(jsonResponse, ApiResponse.class);
  }
  
  /** Get the avatar of the currently logged in user. 
  * @param  sessionManager  A valid SessionManager.
  * @return  A populated GetAvatarResponse.
  */
  public static UserGetAvatarResponse getAvatar(SessionManager sessionManager) {
    Map<String, String> parameters = new HashMap<String, String>();
    JsonElement jsonResponse = sendRequest(parameters, GET_AVATAR_URI, sessionManager);
    return new Gson().fromJson(jsonResponse, UserGetAvatarResponse.class);
  }
 
  /**Set the avatar of the logged in user.
   * @param value - the quick key or url to use as the avatar.
   * @param sessionManager - the session manager to use to process the request.
   * @param useQuickKey - if true, value will be passed as a quickkey. if false, value will be passed as a URL.
   * @return a UserSetAvatarResponse that the server sends.
   */
  public static UserSetAvatarResponse setAvatar(String value, SessionManager sessionManager, boolean useQuickKey) {
    Map<String, String> parameters = new HashMap<String, String>();
    if (useQuickKey) {
      parameters.put("quick_key", value);
    } else {
      parameters.put("url", value);
      parameters.put("action", "normal");
    }   
    JsonElement jsonResponse = sendRequest(parameters, SET_AVATAR_URI, sessionManager);
    return new Gson().fromJson(jsonResponse, UserSetAvatarResponse.class);
  }

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
