package com.arkhive.components.sessionmanager.session;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Map.Entry;

import com.arkhive.components.api.HttpGetRequest;
import com.arkhive.components.api.HttpRequestHandler;
import com.arkhive.components.api.Utility;
import com.arkhive.components.credentials.Credentials;
import com.arkhive.components.httplibrary.HttpInterface;
import com.arkhive.components.sessionmanager.Session;
import com.arkhive.components.sessionmanager.SessionManager;

/**
 * Requests a session from the web API.
 * <br>
 * This class requests a session from the web API.  It is responsible for
 * populating the request, as well as handling the response from the web API.
 */
public class SessionRequest implements HttpRequestHandler {
    /*uri for a session token request*/
    private static final String URI = "/api/user/get_session_token.php?";
    private final Credentials credentials;
    private final String applicationId;
    private final String apiKey;
    private final String domain;
    private final HttpInterface httpInterface;
    private final SessionRequestHandler callback;
    private final Gson gson = new Gson();

    /**
     * Requests a session from the web API.
     *
     * @param sessionManager The session manager that will accept the requested session.
     * @param callback       The SessionRequestHandler that needs the session.
     */
    public SessionRequest(SessionManager sessionManager, SessionRequestHandler callback) {
        this.credentials = sessionManager.getCredentials();
        this.applicationId = sessionManager.getApplicationId();
        this.apiKey = sessionManager.getApiKey();
        this.domain = sessionManager.getDomain();
        this.httpInterface = sessionManager.getHttpInterface();
        this.callback = callback;
    }

    /**
     * Executes the request for a session.
     * <br>
     * When this method is invoked, a request for a session token is submitted to the server.
     * The communication to the server is completed through a different thread, and when
     * the result is received, it is parsed into a Session object, and the
     * callback method is invoked.
     */
    public void execute() {
        String queryString = prepareQueryString();
        String call = domain + URI + queryString;
        HttpGetRequest runner = new HttpGetRequest(call, httpInterface, this);
        Thread t = new Thread(runner);
        t.start();
    }

    /**
     * Executes a request for a Session.
     * <p/>
     * Makes a request for a session token, and then blocks until the Session is returned.
     *
     * @return A fresh session object.
     */
    public Session executeSync() {
        // Prepare query and call httpInterface to get the session JSON
        String queryString = prepareQueryString();
        String call = domain + URI + queryString;
        String response;
        try {
            response = httpInterface.sendGetRequest(call);
        } catch (IOException e) {
            return prepareSession("");
        }

        return prepareSession(response);
    }

    /**
     * Handle the response from a request for a Session.
     * This method accepts the response as a string, and then processes it into a Session object.
     *
     * @param response The response from the HTTP request.
     */
    public void httpRequestHandler(String response) {
        Session session = prepareSession(response);
        callback.responseHandler(session);
    }

    /*    ____       _             __          __  ___     __  __              __    */
    /*   / __ \_____(_)   ______ _/ /____     /  |/  /__  / /_/ /_  ____  ____/ /____*/
    /*  / /_/ / ___/ / | / / __ `/ __/ _ \   / /|_/ / _ \/ __/ __ \/ __ \/ __  / ___/*/
    /* / ____/ /  / /| |/ / /_/ / /_/  __/  / /  / /  __/ /_/ / / / /_/ / /_/ (__  ) */
    /*/_/   /_/  /_/ |___/\__,_/\__/\___/  /_/  /_/\___/\__/_/ /_/\____/\__,_/____/  */

    /**
     * Create a Session from a JSON string.
     * <p/>
     * Convert the JSON response from a session request into a Session object
     *
     * @param response The JSON response from the web request.
     * @return A fully constructed Session.
     */
    private Session prepareSession(String response) {
        //Populate a SessionResponse with the response string in order to use the values to populate the
        //new Session object.
        JsonElement jsonResponse = Utility.getResponseElement(response);
        SessionResponse sessionResponse;
        sessionResponse = gson.fromJson(jsonResponse, SessionResponse.class);

        // Create a new session object and pass it to the callback function
        Session.Builder sessionBuilder = new Session.Builder();
        sessionBuilder.sessionToken(sessionResponse.getSessionToken());
        sessionBuilder.time(sessionResponse.getTime());
        sessionBuilder.secretKey(sessionResponse.getSecretKey());
        return sessionBuilder.build();
    }

    /**
     * Convert a Credentials map into a query string.
     * <p/>
     * Prepares the query string needed to submit a request for a session token.
     *
     * @return The completed query string.
     */
    private String prepareQueryString() {
        Map<String, String> credentialsMap = credentials.getCredentials();
        StringBuilder queryString = new StringBuilder();
        StringBuilder credentialsString = new StringBuilder();
        for (Entry<String, String> e : credentialsMap.entrySet()) {
            // append the map value to the credentials string
            credentialsString.append(e.getValue());

            // append the completed queryParameter to the query string
            String queryParameter = createQueryParameter(e.getKey(), e.getValue());
            queryString.append(queryParameter);
        }
        // Remove the trailing "&" from the query string
        if (queryString.length() > 0) {
            queryString.setLength(queryString.length() - 1);
        }
        // append the final required parameters to the credentials string
        credentialsString.append(applicationId).append(apiKey);

        // Complete the construction of the query string
        String signature = calculateSignature(credentialsString.toString());
        queryString.append("&signature=").append(signature);
        queryString.append("&application_id=").append(applicationId);
        queryString.append("&token_version=2&response_format=json");
        return queryString.toString();
    }

    /**
     * Construct a query paramter string from a key/value set.
     * <p/>
     * Creates a query parameter by URL encoding the value, then creating a string in the format:
     * "key=value&".
     *
     * @param key   The key of the query parameter.
     * @param value The value of the query parameter.
     * @return The URL encoded key/value pair, converted to a String.
     */
    private String createQueryParameter(String key, String value) {
        try {
            // convert the parameter value into UTF-8 to avoid issues
            // with special characters
            String encodedValue = URLEncoder.encode(value, "UTF-8");
            return key + "=" + encodedValue + "&";
        } catch (UnsupportedEncodingException e) {
            // This exception should never be thrown because a UTF-8 encoder
            // is part of the Java standard library.
            throw new IllegalStateException("UTF-8 encoder not found");
        }
    }

    /**
     * Performs SHA-1 hashing on the signature string to produce a signature hash.
     * <p/>
     * A signature hash is required for any API request.
     *
     * @param signatureString String to be hashed.
     * @return The hash of the signature string.
     */
    private String calculateSignature(String signatureString) {
        String signature;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            digest.update(signatureString.getBytes("UTF-8"));
            byte[] digestBytes = digest.digest();
            signature = new BigInteger(1, digestBytes).toString(16);
        } catch (NoSuchAlgorithmException e) {
            // This state should not occur because all JVM implementations contain a
            // SHA-1 encoding algorithim.
            throw new IllegalStateException(e);
        } catch (UnsupportedEncodingException e) {
            // This state should not occur because all JVM implementations contain a
            // UTF-8 encoder.
            throw new IllegalStateException(e);
        }
        return signature;
    }
}
