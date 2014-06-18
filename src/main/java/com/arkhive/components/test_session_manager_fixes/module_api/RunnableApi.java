package com.arkhive.components.test_session_manager_fixes.module_api;

import com.arkhive.components.test_session_manager_fixes.MediaFire;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestHttpPostProcessor;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestHttpPreProcessor;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.interfaces.ApiRequestRunnableCallback;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.requests.RunnableApiGetRequest;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.TokenFarm;

import java.util.Map;

/**
 * Created by on 6/17/2014.
 * TODO: runnableApiGetRequest.run() needs to be changed. The method needs to either pass an Executor or start a Thread.
 */
public class RunnableApi {
    private static RunnableApiGetRequest createApiGetRequestRunnable(ApiRequestRunnableCallback callback, MediaFire mediaFire, ApiRequestObject apiRequestObject) {
        TokenFarm tokenFarm = mediaFire.getTokenFarm();
        HttpPeriProcessor httpPeriProcessor = mediaFire.getHttpPeriProcessor();
        return new RunnableApiGetRequest(callback, new ApiRequestHttpPreProcessor(), new ApiRequestHttpPostProcessor(), tokenFarm, httpPeriProcessor, apiRequestObject);
    }
    public static class File {
        public static void copy(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FILE_COPY);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void delete(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FILE_DELETE);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void purge(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FILE_PURGE);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void move(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FILE_MOVE);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void update(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FILE_UPDATE);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void getInfo(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FILE_GET_INFO);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }


        public static void getLinks(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FILE_GET_LINKS);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }
    }

    public static class Folder {
        public static void copy(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FOLDER_COPY);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void getRevision(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FOLDER_GET_REVISION);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void purge(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FOLDER_PURGE);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void move(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FOLDER_MOVE);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void create(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FOLDER_CREATE);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void delete(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FOLDER_DELETE);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void search(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FOLDER_SEARCH);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void update(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FOLDER_UPDATE);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void getContents(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FOLDER_GET_CONTENT);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void getInfo(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_FOLDER_GET_INFO);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }
    }

    public static class System {
        public static void getInfo(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_SYSTEM_GET_INFO);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }
    }

    public static class Device {
        public static void getChanges(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_DEVICE_GET_CHANGES);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void getStatus(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_DEVICE_GET_STATUS);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }
    }

    public static class Upload {
        public static void checkUpload(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_UPLOAD_CHECK);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void instantUpload(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_UPLOAD_INSTANT);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void resumableUpload(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_UPLOAD_RESUMABLE);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void pollUpload(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_UPLOAD_POLL_UPLOAD);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }
    }

    public static class User {
        public static void getInfo(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_USER_GET_INFO);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void register(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_USER_REGISTER);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void linkFacebook(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_USER_LINK_FACEBOOK);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void linkTwitter(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_USER_LINK_TWITTER);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void unlinkFacebook(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_USER_UNLINK_FACEBOOK);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void unlinkTwitter(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_USER_UNLINK_TWITTER);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void getAvatar(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_USER_GET_AVATAR);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void setAvatar(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_USER_SET_AVATAR);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }
    }

    public static class Contact {
        public static void addContact(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_CONTACT_ADD);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void deleteContact(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_CONTACT_DELETE);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }

        public static void fetchContacts(ApiRequestRunnableCallback callback, MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.DOMAIN_HTTP, ApiUris.URI_CONTACT_FETCH);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            RunnableApiGetRequest runnableApiGetRequest = createApiGetRequestRunnable(callback, mediaFire, apiRequestObject);
            runnableApiGetRequest.run();
        }
    }
}
