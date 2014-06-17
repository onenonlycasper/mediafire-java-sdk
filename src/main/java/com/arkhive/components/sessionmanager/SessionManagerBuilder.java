package com.arkhive.components.sessionmanager;

import com.arkhive.components.credentials.Credentials;
import com.arkhive.components.httplibrary.HttpInterface;

/**
 * Builder for a SessionManager object
 * <br>
 * Allows for construction of a SessionManager object, and verifies that
 * all of the required parameters are set to acceptable values.
 */
public class SessionManagerBuilder {
    protected String applicationId = "";
    protected String apiKey = "";
    protected String domain = "";
    protected int minSessions = -1;
    protected int maxSessions = -1;
    protected Credentials credentials = null;
    protected HttpInterface httpInterface = null;

    /**
     * Sets the application id for the session manager
     * <br>
     * The application id is required to request a session
     * from the API.  If a null value is passed, the application
     * id will be left at the default state of an empty string.
     *
     * @param value The value of the application id.
     */
    public SessionManagerBuilder applicationId(String value) {
        if (value != null) {
            this.applicationId = value;
        }
        return this;
    }

    /**
     * Sets the API key for the session manager
     * <br>
     * An API key is required to request a session from the API.  If
     * a null value is passed, the API key is left at the default value
     * of an empty string.
     *
     * @param value The value of the API key.
     */
    public SessionManagerBuilder apiKey(String value) {
        if (value != null) {
            this.apiKey = value;
        }
        return this;
    }

    /**
     * Sets the domain used for API requests
     * <br>
     * This is the domain that is prepended to the API
     * URI in order to make a request to the web API system.
     * If a null value is passed, the domain is left at the
     * default value of an empty string.
     *
     * @param value The domain used for API requests.
     */
    public SessionManagerBuilder domain(String value) {
        if (value != null) {
            this.domain = value;
        }
        return this;
    }

    /**
     * The minimum number of sessions to store in the
     * session pool.
     * <br>
     * This is the minimum number of sessions to store in the
     * session pool.  If at any point the number of sessions
     * drops below the minimum, a new session will be requested.
     * Passing a value less that zero to this method will
     * not change the minimum session value.
     *
     * @param value The minimum number of sessions to hold in the
     *              session pool.
     */
    public SessionManagerBuilder minSessions(int value) {
        if (value > 0) {
            this.minSessions = value;
        }
        return this;
    }

    /**
     * The maximum number of sessions to store in the
     * session pool.
     * <br>
     * This is the maximum number of session to store in the
     * session pool.  When the number of sessions is equal to this
     * value, any attempts to add a session to the pool results in the
     * session being discarded.  The value passed to this function must
     * be greater than zero, or else the maximum sessions value is not
     * updated.
     *
     * @param value The maximum sessions to hold in the session pool.
     */
    public SessionManagerBuilder maxSessions(int value) {
        if (value > 0) {
            this.maxSessions = value;
        }
        return this;
    }

    /**
     * Sets the Credentials used by the session manager.
     * <br>
     * A Credentials object is required to request a session from the API.
     * Passing a null to this method will result in the value not being
     * updated.
     *
     * @param value The Credentials to be used by the session manager.
     */
    public SessionManagerBuilder credentials(Credentials value) {
        if (value != null) {
            this.credentials = value;
        }
        return this;
    }

    /**
     * Sets the HttpInterface to be used by the session manager.
     * <br>
     * A HttpInterface is required to request a session from the API.
     * Passing a null value to this method will result in the value not
     * being updated.
     *
     * @param value The HttpInterface to be used by the session manager.
     */
    public SessionManagerBuilder httpInterface(HttpInterface value) {
        if (value != null) {
            this.httpInterface = value;
        }
        return this;
    }

    /**
     * Constructs a new {@link SessionManager} object.
     * <br>
     * This method creates a new SessionManager, using the values
     * set in the other methods.  Before the SessionManager is created,
     * all of the fields are validated, and an IllegalStateException is
     * thrown if any of them  fail validation.
     * <br>
     *
     * @return A new {@link SessionManager}.
     * @throws IllegalStateException
     */
    public SessionManager build() {
        validateApplicationId();
        validateApiKey();
        validateDomain();
        validateMinSessions();
        validateMaxSessions();
        validateCredentials();
        validateHttpInterface();
        return new SessionManager(this);
    }

    /**
     * Validates the value of the application id.
     * <br>
     * Validates that the application id is set to something other
     * than an empty string.  If this validation fails, an IllegalStateException
     * is thrown.
     *
     * @throws IllegalStateException
     */
    private void validateApplicationId() {
        if (this.applicationId.isEmpty()) {
            throw new IllegalStateException("ApplicationId not set");
        }
    }

    /**
     * Validates the value of the API key.
     * <br>
     * Validates that the API key is not set to an empty string.  If
     * this validation falies, an IllegalStateException is thrown.
     *
     * @throws IllegalStateException
     */
    private void validateApiKey() {
        if (this.apiKey.isEmpty()) {
            throw new IllegalStateException("ApiKey is not set");
        }
    }

    /**
     * Validates the value of the domain setting.
     * <br>
     * Validates that the domain is not set to an empty string.
     * If this validation fails, an IllegalStateException is thrown.
     *
     * @throws IllegalStateException
     */
    private void validateDomain() {
        if (this.domain.isEmpty()) {
            throw new IllegalStateException("Domain is not set");
        }
    }

    /**
     * Validates the minimum sesssions setting.
     * <br>
     * Validates that the minimum number of sessions is set to a value
     * greater than zero, and that it is less than the number of maximum sessions.
     * If the validation fails, an IllegalStateException is thrown.
     *
     * @throws IllegalStateException
     */
    private void validateMinSessions() {
        if (this.minSessions < 0) {
            throw new IllegalStateException("MinSessions set incorrectly");
        }
        if (this.minSessions > this.maxSessions) {
            throw new IllegalStateException("MinSessions must be less than MaxSessions");
        }
    }

    /**
     * Validates the maximum sessions setting.
     * <br>
     * Validates that the maximum number of sessions is greater than zero, and
     * greater than the minimum number of sessions.  If the validation fails, an
     * IllegalStateException is thrown.
     *
     * @throws IllegalStateException
     */
    private void validateMaxSessions() {
        if (this.maxSessions < 0) {
            throw new IllegalStateException("MaxSessions not set");
        }
        if (this.maxSessions < this.minSessions) {
            throw new IllegalStateException("MaxSessions must be greater than MinSessions");
        }
    }

    /**
     * Validates the credentials setting.
     * <br>
     * Validates that the credentials value is not set to a null value.
     * If this validation fails then an IllegalStateException is thrown.
     *
     * @throws IllegalStateException
     */
    private void validateCredentials() {
        if (this.credentials == null) {
            throw new IllegalStateException("Credentials not set");
        }
    }

    /**
     * Validates the HttpInterface setting.
     * <br>
     * Validates that the HttpInterface is not set to a null value.
     * If the validation fails, an IllegalStateException is thrown.
     *
     * @throws IllegalStateException
     */
    private void validateHttpInterface() {
        if (this.httpInterface == null) {
            throw new IllegalStateException("HttpInterface not set");
        }
    }
}
