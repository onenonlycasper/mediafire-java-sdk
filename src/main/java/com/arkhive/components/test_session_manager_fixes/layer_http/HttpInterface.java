package com.arkhive.components.test_session_manager_fixes.layer_http;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiGetRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiPostRequestObject;

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
    public ApiGetRequestObject sendGetRequest(ApiGetRequestObject apiGetRequestObject);

    /** Send a POST request.
     * <p>
     * Send a POST request via HTTP/HTTPS.
     *
     * @param  apiPostRequestObject  api post request descriptor.
     *
     * @return  The ApiRequestObject.
     */
    public ApiPostRequestObject sendPostRequest(ApiPostRequestObject apiPostRequestObject);
}
