package com.arkhive.components.test_session_manager_fixes.module_api;

import com.arkhive.components.test_session_manager_fixes.MediaFire;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestHttpPostProcessor;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestHttpPreProcessor;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.interfaces.ApiRequestRunnableCallback;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.requests.BlockingApiGetRequest;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.requests.RunnableApiGetRequest;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.TokenFarm;

import java.util.Map;

/**
 * Created by  on 6/17/2014.
 */
public class Api {
    private static MediaFire mediaFire;
    public File file;
    public Folder folder;
    public User user;
    public System system;
    public Device device;
    public Upload upload;

    public Api(MediaFire mediaFire) {
        this.mediaFire = mediaFire;
        file = new File();
        folder = new Folder();
        user = new User();
        system = new System();
        device = new Device();
        upload = new Upload();
    }

    static BlockingApiGetRequest createBlockingApiGetRequest(ApiRequestObject apiRequestObject) {
        TokenFarm tokenFarm = mediaFire.getTokenFarm();
        HttpPeriProcessor httpPeriProcessor = mediaFire.getHttpPeriProcessor();
        return new BlockingApiGetRequest(new ApiRequestHttpPreProcessor(), new ApiRequestHttpPostProcessor(), tokenFarm, httpPeriProcessor, apiRequestObject);
    }

    static RunnableApiGetRequest createApiGetRequestRunnable(ApiRequestRunnableCallback callback, ApiRequestObject apiRequestObject) {
        TokenFarm tokenFarm = mediaFire.getTokenFarm();
        HttpPeriProcessor httpPeriProcessor = mediaFire.getHttpPeriProcessor();
        return new RunnableApiGetRequest(callback, new ApiRequestHttpPreProcessor(), new ApiRequestHttpPostProcessor(), tokenFarm, httpPeriProcessor, apiRequestObject);
    }
}
