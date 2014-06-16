package com.arkhive.components.test_session_manager_fixes.module_http_processor;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public interface HttpInterface {
    /** Send a GET request.
     * <p>
     * Send a GET request via HTTP/HTTPS.
     *
     * @param  apiGetRequestObject  api get request descriptor.
     *
     * @return  The ApiRequestObject.
     */
    public ApiRequestObject sendGetRequest(ApiRequestObject apiGetRequestObject);

    /** Send a POST request.
     * <p>
     * Send a POST request via HTTP/HTTPS.
     *
     * @param  apiPostRequestObject  api post request descriptor.
     *
     * @return  The ApiRequestObject.
     */
    public ApiRequestObject sendPostRequest(ApiRequestObject apiPostRequestObject);
}
