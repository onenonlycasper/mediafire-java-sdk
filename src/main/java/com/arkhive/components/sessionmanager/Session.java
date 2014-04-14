package com.arkhive.components.sessionmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/** Contains a session, which is needed to call the MediaFire REST API. */
public class Session {
    /** Constant defined by the web API, used to calculate the new secret key. */
    private static final int DIVISOR = 2147483647;
    /** Constant defined by the web API, used to calculate the new secret key. */
    private static final int MULTIPLIER = 16807;
    /** The secret key used to create an API signature.
     * <br>
     * This value is updated after every API call.
     *
     * @author jmoore
     */
    private BigInteger secretKey;
    /* The time value used to create an API signature. */
    private String time;
    /* The session token used to create an API signature. */
    private String sessionToken;

    Logger logger = LoggerFactory.getLogger(Session.class);

    /** Constructs a {@link Session} from a Builder.
     * <br>
     * This is the only way to construct a Session.  It guarantees
     * that the Session is created with all of the fields set to
     * reasonable values.
     */
    private Session(Builder b) {
        this.secretKey = b.secretKey;
        this.time = b.time;
        this.sessionToken = b.sessionToken;

    }

    public BigInteger   getSecretKey()      { return this.secretKey; }
    public String       getTime()           { return this.time; }
    public String       getSessionToken()   { return this.sessionToken; }

    /** Converts a URI and parameter map to a query string.
     * <p>
     *  This method calculates the signature needed to complete a web API call,
     *  and appends it to the end of the query string.  The signature is calculated by
     *  performing the following steps:
     *  <p>
     *  Take the current secret key modulo 256.
     *  Prepend this value to the current time value.
     *  Add the session token as the first parameter to the query string.
     *  Merge the secret key modulo, the time, and the API request as so:
     *  <br>
     *  {@code (secret key + time + API request) }.
     *
     * @param uri  The URI of the web API call.
     * @param parameters  A {@link Map}&lt;String, String&gt; holding the parameters
     *                    for the web API call.
     * @return  A String containing the the full query string.
     */
    public String getQueryString(String uri, Map<String, String> parameters) {
        String queryString = "";
        // Handle cases where null parameters are passed.
        if (uri == null) { uri = ""; }
        if (parameters == null) { parameters = new HashMap<String, String>(); }

        // Iterate over the parameters list and create the query string.
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry<String, String> e : parameters.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();
            if (key != null && !key.isEmpty()) {
                if (value != null && !value.isEmpty()) {
                    stringBuilder.append("&").append(key).append("=").append(value);
                }
            }
        }

        // Create the full API request string.
        queryString = uri + "?session_token=" + this.sessionToken + stringBuilder.toString();
        String keyModulus = String.valueOf(secretKey.mod(BigInteger.valueOf(256)));
        String timeString = time;
        String signatureBase = keyModulus + timeString + queryString;

        // Get the signature string.
        String signature = calculateMD5Hash(signatureBase);
        this.updateSession();
        return queryString + "&signature=" + signature;
    }

    private void updateSession() {
        BigInteger tempKey = this.getSecretKey().multiply(BigInteger.valueOf(MULTIPLIER));
        this.secretKey = tempKey.mod(BigInteger.valueOf(DIVISOR));
    }

    /** Calculate the MD5 of the signature.
     * <br>
     * The MD5 hash of a signature string is needed for all API calls.
     *
     * @param signatureBase The signature to be converted to a MD5 hash.
     *
     * @return The signature converted into a MD5 hash.
     */
    private String calculateMD5Hash(String signatureBase) {
        String signatureString = "";
        try {
            byte[] signatureBytes = signatureBase.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(signatureBytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            // Convert the byte array returned from the message digest
            // function to a String.
            signatureString = sb.toString();
        } catch (final NoSuchAlgorithmException e) {
            // This exception will only be thrown if there is no MD5 algorithim
            // that the JVM is able to use.  All compliant JVMs will have a
            // working implementation of MD5 in their standard library.
            throw new IllegalStateException(e);
        } catch (final UnsupportedEncodingException e) {
            // This exception will only be thrown if there is no UTF-8 encoder
            // present in the JVM.  All compliant JVMs will have a working
            // UTF-8 encoder.
            throw new IllegalStateException(e);
        }

        // The MD5 function will truncate the leading 0 of a hash.
        // If the hash does not have 32 characters, the leading 0 was truncated, so add
        // it back.
        if (signatureString.length() == 31) { signatureString = "0" + signatureString; }
        return signatureString;
    }

    /** Builder for a Session object
     * <p>
     * The only way to construct a Session object is through the
     * Session Builder.  This is done to ensure that the Session
     * object is fully built with all of the appropriate values
     * initialized.
     */
    public static class Builder {
        private BigInteger secretKey = BigInteger.valueOf(0);
        private String time = "";
        private String sessionToken = "";

        /** Set the value of the secret key for this session
         * <p>
         * The secret key is used to calculate the signature
         * required for a session token v2 request.
         *
         * @param value  The value of the secret key.
         */
        public Builder secretKey(BigInteger value) {
            this.secretKey = value;
            return this;
        }

        /** Set the time value for this session
         * <p>
         * The time value is used to calculate the signature
         * required for the session token v2 request.
         *
         * @param value  The value of the session time.
         */
        public Builder time(String value) {
            this.time = value;
            return this;
        }

        /** Set the session token
         * <p>
         * The session token is required for all session
         * token v2 requests.
         *
         * @param value  The value of the session token.
         */
        public Builder sessionToken(String value) {
            if (value != null) {
                this.sessionToken = value;
            }
            return this;
        }

        /** Create a new Session object.
         * <p>
         * Constructs a new Session from the Builder properties.
         *
         * @return A fully constructed Session.
         */
        public Session build() {
            return new Session(this);
        }
    }
}
