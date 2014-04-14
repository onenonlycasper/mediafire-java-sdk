package com.arkhive.components.credentials;

import java.util.Map;

/** An object used to store and return credentials to be used
 * in a call to the MediaFire web API.
 */
public interface Credentials {
    /** Return the credentials needed to perform the API request.
     *
     * @return A Map containing the credentials needed.
     */
    public Map<String, String> getCredentials();
    /** Store the credentials needed for a web API request.
     *
     * @param credentials  A Map containing the credentials information
     * to store.
     */
    public void setCredentials(Map<String, String> credentials);
}
