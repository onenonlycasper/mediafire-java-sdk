package com.arkhive.components.sessionmanager.session;

import java.math.BigInteger;

/** Stores the response from a request for a session token
 *
 * This class is populated via the GSON library with the contents of the
 * JSON returned from a call to the get_session_token api.
 *
 */
public class SessionResponse {

    private String action;
    //CHECKSTYLE:OFF    The fields must match the fields returned in the JSON response.
    private String session_token;
    private BigInteger secret_key;
    //CHECKSTYLE:ON
    private String time;
    private String result;

    public SessionResponse() {}

    /** Returns the session token.
     *
     * @return  The session token if it is set, an empty string otherwise.
     */
    public String getSessionToken() {
        if (this.session_token == null) { this.session_token = ""; }
        return this.session_token;
    }

    /** Returns the action taken.
     *
     * @return  The action value if it is set, an empty string otherwise.
     */
    public String getAction() {
        if (this.action == null) { this.action = ""; }
        return this.action;
    }

    /** Returns the result string.
     *
     * @return The result string if it is set, an empty string otherwise.
     */
    public String getResult() {
        if (this.result == null) { this.result = ""; }
        return this.result;
    }

    /** Return the secret key.
     *
     * @return The secret key.
     */
    public BigInteger getSecretKey() {
        if (secret_key == null) { this.secret_key = BigInteger.valueOf(0); }
        return this.secret_key;
    }

    /** Return the time.
     *
     * @return the time value.
     */
    public String getTime() {
        return this.time;
    }
}
