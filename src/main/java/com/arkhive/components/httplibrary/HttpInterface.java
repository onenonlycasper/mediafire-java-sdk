package com.arkhive.components.httplibrary;

import java.io.IOException;
import java.util.Map;

/**
 * Interface to a HTTP library.
 * <p/>
 * Classes implement this interface to provide an application agnostic method.
 */
public interface HttpInterface {
    /**
     * Send a GET request.
     * <p/>
     * Send a GET request via HTTP/HTTPS.
     *
     * @param request The full URI of the web request.
     * @return The response from the GET request.
     */
    public String sendGetRequest(String request) throws IOException;

    /**
     * Send a POST request.
     * <p/>
     * Send a POST request via HTTP/HTTPS.
     *
     * @param domain     The domain of the server.
     * @param uri        The URI of the request.
     * @param parameters The parameters to include with the web request.
     * @return The response from the POST request.
     */
    public String sendPostRequest(String domain, String uri, Map<String, String> parameters) throws IOException;

    /**
     * Send a POST request.
     * <p/>
     * Send a POST request via HTTP/HTTPS. The request can contain POST parameters, modified HTTP headers, and a
     * multi-part entity.  This method is used primarily for POST requests involving file transfers.
     *
     * @param domain     The domain of the server.
     * @param uri        The URI of the request.
     * @param parameters The POST parameters to pass with the POST request.
     * @param headers    The headers to include with the POST request.
     * @param fileData   The multi-part entity to include with the POST request.
     * @return The response from the POST request.
     */
    public String sendPostRequest(String domain, String uri, Map<String, String> parameters,
                                  Map<String, String> headers, byte[] fileData) throws IOException;
}
