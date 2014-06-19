package com.arkhive.components.test_session_manager_fixes.module_token_farm.token_session;

import com.arkhive.components.test_session_manager_fixes.module_api.responses.ApiResponse;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.interfaces.HttpProcessor;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by on 6/18/2014.
 */
public class NewActionTokenHttpPostProcessor implements HttpProcessor {
    private static final String TAG = NewActionTokenHttpPostProcessor.class.getSimpleName();

    @Override
    public void processApiRequestObject(ApiRequestObject apiRequestObject) {
    }

}
