package com.arkhive.components.sessionmanager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.arkhive.components.api.ApiRequest;
import com.arkhive.components.api.ApiRequestBuilder;
import com.arkhive.components.api.ApiRequestHandler;
import com.arkhive.components.api.Utility;
import com.arkhive.components.credentials.Credentials;
import com.arkhive.components.httplibrary.HttpInterface;
import com.arkhive.components.sessionmanager.session.ActionTokenRequestHandler;
import com.arkhive.components.sessionmanager.session.ActionTokenResponse;
import com.arkhive.components.sessionmanager.session.SessionRequest;
import com.arkhive.components.sessionmanager.session.SessionRequestHandler;

/** Manages a collection of session tokens.
 * <p>
 * Maintains a collection of Sessions, retrieving more as needed. There is
 * a fixed minimum and maximum number of Sessions that will be held at one time.
 * <p>
 * The session manager is also responsible for updating the session with a new
 * secret key if the Session is to be reused.
 */
public class SessionManager implements SessionRequestHandler {
    /** The ID for the specific application making an API call.*/
    private final String applicationId;
    /** The API key for the application making the API call. */
    private final String apiKey;
    /** A {@link LinkedList} functioning as a queue used to hold the {@link Session} objects. */
    private final Queue<Session> sessionList = new LinkedList<Session>();
    /** The minimum number of {@link Session} objects to hold. */
    private final int minSessions;
    /** The maximum number of {@link Session} objects to hold. */
    private final int maxSessions;
    /** The domain name to use for API requests. */
    private final String domain;
    /** The {@link HttpInterface} to use for API requests. */
    private final HttpInterface httpInterface;
    /** The {@link Credentials} to use for API requests. */
    private final Credentials credentials;
    /** The upload action token. */
    private ActionTokenResponse uploadActionToken;
    /** The image action token. */
    private ActionTokenResponse imageActionToken;
    /** Flag indicating if the user has been authenticated. */
    private boolean isAuthenticated = false;

    Logger logger = LoggerFactory.getLogger(SessionManager.class);

    /** Constructs a {@link SessionManager} from a {@link ApiRequestBuilder}. */
    protected SessionManager(SessionManagerBuilder sessionManagerBuilder) {
        this.applicationId = sessionManagerBuilder.applicationId;
        this.apiKey = sessionManagerBuilder.apiKey;
        this.domain = sessionManagerBuilder.domain;
        this.minSessions = sessionManagerBuilder.minSessions;
        this.maxSessions = sessionManagerBuilder.maxSessions;
        this.credentials = sessionManagerBuilder.credentials;
        this.httpInterface = sessionManagerBuilder.httpInterface;
    }

    /*   ______     __  __                */
    /*  / ____/__  / /_/ /____  __________*/
    /* / / __/ _ \/ __/ __/ _ \/ ___/ ___/*/
    /*/ /_/ /  __/ /_/ /_/  __/ /  (__  ) */
    /*\____/\___/\__/\__/\___/_/  /____/  */
    /** Get the domain used for API calls. */
    public String           getDomain()         { return this.domain; }
    /** Get the application Id. */
    public String           getApplicationId()  { return this.applicationId; }
    /** Get the API key. */
    public String           getApiKey()         { return this.apiKey; }
    /** Get the HttpInterface that will be used for all API calls. */
    public HttpInterface    getHttpInterface()  { return this.httpInterface; }
    /** Get the Credentials needed to request a session token. */
    public Credentials      getCredentials()    { return this.credentials; }
    /** The size of the current session pool. */
    public int              getSessionCount()   { return sessionList.size(); }
    /** Check if the user is authenticated. */
    public boolean          isAuthenticated()   { return this.isAuthenticated; }

    /*   _____      __  __                */
    /*  / ___/___  / /_/ /____  __________*/
    /*  \__ \/ _ \/ __/ __/ _ \/ ___/ ___/*/
    /* ___/ /  __/ /_/ /_/  __/ /  (__  ) */
    /*/____/\___/\__/\__/\___/_/  /____/  */
    /** Set the action token for images. */
    protected void setImageActionToken(ActionTokenResponse value) { this.imageActionToken = value; }
    /** Set the action token for uploads. */
    protected void setUploadActionToken(ActionTokenResponse value) { this.uploadActionToken = value; }

    /*    ____  __           __   _                __  ___     __  __              __    */
    /*   / __ )/ /___  _____/ /__(_)___  ____ _   /  |/  /__  / /_/ /_  ____  ____/ /____*/
    /*  / __  / / __ \/ ___/ //_/ / __ \/ __ `/  / /|_/ / _ \/ __/ __ \/ __ \/ __  / ___/*/
    /* / /_/ / / /_/ / /__/ ,< / / / / / /_/ /  / /  / /  __/ /_/ / / / /_/ / /_/ (__  ) */
    /*/_____/_/\____/\___/_/|_/_/_/ /_/\__, /  /_/  /_/\___/\__/_/ /_/\____/\__,_/____/  */
    /*                                /____/                                             */

    /** Requests a session token
     * <br>
     * Gets a session token in a blocking manner. The request will not return until a
     * Session is located.
     *
     * @return  A fully constructed Session.
     */
    public Session getSession() {
        if (sessionList.size() > minSessions) {
            synchronized (this) {
                return sessionList.remove();
            }
        } else {
            SessionRequest request = new SessionRequest(this, null);
            return request.executeSync();
        }
    }

    /** Returns a Session back to the Session pool
     * <br>
     * Returns a Session object back to the Session pool, if the
     * number of Sessions in the pool exceeds the maximum number of
     * Sessions, the Session is discarded. If the Session is accepted
     * back into the pool, the secret key is updated to allow it to be used
     * again.
     *
     * @param session The Session to return to the pool.
     */
    public void releaseSession(Session session) {
//        if (sessionList.size() >= maxSessions - 1) { return; }
//        synchronized (this) {
//            sessionList.add(session);
//        }
    }

    /** Request an upload action token.
     * <p>
     * Returns an existing action token if it is available, otherwise request a new
     * upload action token, and return that.
     *
     * @return  The requested action token.
     */
    public ActionTokenResponse requestUploadActionToken() {
        if (uploadActionToken != null) {
            return uploadActionToken;
        } else {
            return getActionToken("upload");
        }
    }

    /** Request an image action token.
     * <p>
     * Return an existing image action token if one is available, otherwise request a new
     * image action token and return that.
     *
     * @return An image action token.
     */
    public ActionTokenResponse requestImageActionToken() {
        if (imageActionToken != null) {
            return imageActionToken;
        } else {
            return getActionToken("image");
        }
    }

    /** Destroy an upload action token. */
    public void destroyUploadActionToken() {
        if (uploadActionToken == null) {
            return;
        }
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("action_token", uploadActionToken.getSessionToken());
        manageActionToken("/api/user/destroy_action_token.php", parameters);
    }

    /** Destroy an image action token. */
    public void destroyImageActionToken() {
        if (imageActionToken == null) {
            return;
        }
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("action_token", imageActionToken.getSessionToken());
        manageActionToken("/api/user/destroy_action_token.php", parameters);
    }

    /** Authenticate a user.
     * <p>
     * To authenticate a user, a request for a new session token is made. If the session is valid, the user
     * is considered authenticated.
     *
     * @return  The authentication status.
     */
    public boolean authenticate() {
        Session session = new SessionRequest(this, null).executeSync();
        if (!"".equals(session.getSessionToken())) {
            this.sessionList.add(session);
            this.isAuthenticated = true;
            return true;
        } else {
            this.isAuthenticated = false;
            return false;
        }
    }

    /** Clear the existing session queue. */
    public void clearSessionQueue() {
        this.sessionList.clear();
    }

    /*    ___                             __  ___     __  __              __    */
    /*   /   |  _______  ______  _____   /  |/  /__  / /_/ /_  ____  ____/ /____*/
    /*  / /| | / ___/ / / / __ \/ ___/  / /|_/ / _ \/ __/ __ \/ __ \/ __  / ___/*/
    /* / ___ |(__  ) /_/ / / / / /__   / /  / /  __/ /_/ / / / /_/ / /_/ (__  ) */
    /*/_/  |_/____/\__, /_/ /_/\___/  /_/  /_/\___/\__/_/ /_/\____/\__,_/____/  */
    /*            /____/                                                        */

    /** Requests a session token
     * <br>
     * Requests a session token from the session manager, and
     * returns it to the callback handler. If a session does not
     * exist in the session pool, an new session is created.
     *
     * @param handler A callback handler to receive the requested session token.
     */
    public void getSession(SessionRequestHandler handler) {
//      logger.info("SESSION REQUESTED");
        if (sessionList.size() > minSessions) {
            synchronized (this) {
                Session session = sessionList.remove();
                handler.responseHandler(session);
            }
        } else {
            SessionRequest request = new SessionRequest(this, handler);
            request.execute();
        }
    }

    /** Requests an upload action token.
     * <p>
     * If an upload action token is available, it is sent to all handlers in the list, otherwise a
     * new upload token is requested, and then sent to the handlers.
     *
     * @param  handlers  A list of ActionTokenRequestHandler expecting the upload token.
     */
    public void requestUploadActionToken(List<ActionTokenRequestHandler> handlers) {
        if (this.uploadActionToken != null) {
            for (ActionTokenRequestHandler e : handlers) {
                e.receiveActionToken(this.uploadActionToken);
            }
        } else {
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("type", "upload");
            ApiRequestHandler tokenHandler = new InternalUploadTokenHandler(this, handlers);
            manageActionToken(tokenHandler, "/api/user/get_action_token.php", parameters);
        }
    }

    /**
     * Requests an upload action token.
     * <p>
     * If an upload action token is available, it is sent to all handlers in the list, otherwise a
     * new upload token is requested, and then sent to the handlers.
     *
     * @param handler A single ActionTokenRequestHandler expecting the upload token.
     */
    public void requestUploadActionToken(ActionTokenRequestHandler handler) {
        List<ActionTokenRequestHandler> singleHandler = new ArrayList<ActionTokenRequestHandler>();
        singleHandler.add(handler);
        requestUploadActionToken(singleHandler);
    }

    /** Request an image action token.
     * <p>
     * If an new image action token is available, it is sent to all handlers in the list, otherwise a
     * new image token is requested, and then sent to the handlers.
     *
     * @param  handlers  A list of ActionTokenRequestHandler expecting the image token.
     */
    public void requestImageActionToken(List<ActionTokenRequestHandler> handlers) {
        if (this.imageActionToken != null) {
            for (ActionTokenRequestHandler e : handlers) {
                e.receiveActionToken(this.imageActionToken);
            }
        } else {
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("type", "image");
            ApiRequestHandler tokenHandler = new InternalImageTokenHandler(this, handlers);
            manageActionToken(tokenHandler, "/api/user/get_action_token.php", parameters);
        }
    }

    /** Handler to accept a new Session
     * <br>
     * Accepts a newly created Session, and adds it to the
     * session pool. If the session pool is full, the new session
     * is discarded. This method is called from within a session
     * request as a callback.
     *
     * @param session A new Session to be added to the pool.
     */
    public void responseHandler(Session session) {
        synchronized (this) {
            if (sessionList.size() < maxSessions) {
                sessionList.add(session);
            }
        }
    }

    /*    ____       _             __          __  ___     __  __              __    */
    /*   / __ \_____(_)   ______ _/ /____     /  |/  /__  / /_/ /_  ____  ____/ /____*/
    /*  / /_/ / ___/ / | / / __ `/ __/ _ \   / /|_/ / _ \/ __/ __ \/ __ \/ __  / ___/*/
    /* / ____/ /  / /| |/ / /_/ / /_/  __/  / /  / /  __/ /_/ / / / /_/ / /_/ (__  ) */
    /*/_/   /_/  /_/ |___/\__,_/\__/\___/  /_/  /_/\___/\__/_/ /_/\____/\__,_/____/  */

    /** Manages an action token.
     * <p>
     * Handles request to either destroy or create a new upload/image action token.
     *
     * @param  handler  The ApiRequestHandler accepting the response from the API call.
     * @param  uri  The URI of the API call.
     * @param  parameters  The parameters Map to pass to the API call.
     */
    private void manageActionToken(ApiRequestHandler handler, String uri, Map<String, String> parameters) {
        ApiRequest tokenRequest = new ApiRequestBuilder()
                .domain(domain)
                .uri(uri)
                .parameters(parameters)
                .httpInterface(httpInterface)
                .sessionManager(this)
                .requestHandler(handler)
                .build();
        tokenRequest.submitRequest();
    }

    /** Manages an action token.
     * <p>
     * Handles a request to create or destroy an action token.
     *
     * @param  uri  The URI of the API call.
     * @param  parameters  The parameters to pass to the API call.
     *
     * @return  The result of the API call.
     */
    private String manageActionToken(String uri, Map<String, String> parameters) {
        ApiRequest tokenRequest = new ApiRequestBuilder()
                .domain(domain)
                .uri(uri)
                .parameters(parameters)
                .httpInterface(httpInterface)
                .sessionManager(this)
                .build();
        return tokenRequest.submitRequestSync();
    }

    /** Converts an API response string into an action token.
     *
     * @param  type  The type of action token to generate.
     *
     * @return  A fully constructed action token.
     */
    private ActionTokenResponse getActionToken(String type) {
        Gson gson = new Gson();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", type);
        String response = this.manageActionToken("/api/user/get_action_token.php", parameters);
        JsonElement jsonResponse = Utility.getResponseElement(response);
        return gson.fromJson(jsonResponse, ActionTokenResponse.class);
    }
}

