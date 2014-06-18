package com.arkhive.components.test_session_manager_fixes.module_api;

import com.arkhive.components.test_session_manager_fixes.MediaFire;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestHttpPostProcessor;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestHttpPreProcessor;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.requests.BlockingApiGetRequest;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.TokenFarm;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by  on 6/17/2014.
 */
public class BlockingApi {
    private static BlockingApiGetRequest createBlockingApiGetRequest(MediaFire mediaFire, ApiRequestObject apiRequestObject) {
        TokenFarm tokenFarm = mediaFire.getTokenFarm();
        HttpPeriProcessor httpPeriProcessor = mediaFire.getHttpPeriProcessor();
        return new BlockingApiGetRequest(new ApiRequestHttpPreProcessor(), new ApiRequestHttpPostProcessor(), tokenFarm, httpPeriProcessor, apiRequestObject);
    }
    public static class File {
        public static void copy(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_FILE_COPY);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void delete(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_FILE_DELETE);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void purge(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_FILE_PURGE);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void move(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_FILE_MOVE);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void update(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_FILE_UPDATE);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void getInfo(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_FILE_GET_INFO);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }


        public static void getLinks(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_FILE_GET_LINKS);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }
    }

    public static class Folder {
        public static void copy(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_FOLDER_COPY);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void move(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_FOLDER_MOVE);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void create(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_FOLDER_CREATE);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void delete(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_FOLDER_DELETE);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void search(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_FOLDER_SEARCH);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void update(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_FOLDER_UPDATE);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void getContents(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_FOLDER_GET_CONTENT);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void getInfo(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_FOLDER_GET_INFO);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }
    }

    public static class System {
        public static void getInfo(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_SYSTEM_GET_INFO);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }
    }

    public static class Device {
        public static void getChanges(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_DEVICE_GET_CHANGES);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void getStatus(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_DEVICE_GET_STATUS);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }
    }

    public static class Upload {
        public static void checkUpload(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_UPLOAD_CHECK);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void instantUpload(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_UPLOAD_INSTANT);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void resumableUpload(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_UPLOAD_RESUMABLE);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void pollUpload(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_UPLOAD_POLL_UPLOAD);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }
    }

    public static class User {
        public static void getInfo(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_USER_GET_INFO);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void register(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_USER_REGISTER);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void linkFacebook(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_USER_LINK_FACEBOOK);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void linkTwitter(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_USER_LINK_TWITTER);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void unlinkFacebook(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_USER_UNLINK_FACEBOOK);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void unlinkTwitter(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_USER_UNLINK_TWITTER);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void getAvatar(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_USER_GET_AVATAR);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void setAvatar(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_USER_SET_AVATAR);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }
    }

    public static class Contact {
        public static void addContact(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_CONTACT_ADD);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void deleteContact(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_CONTACT_DELETE);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }

        public static void fetchContacts(MediaFire mediaFire, Map<String, String> requiredParameters, Map<String, String> optionalParameters) {
            ApiRequestObject apiRequestObject = new ApiRequestObject(MediaFireApi.DOMAIN_HTTP, MediaFireApi.URI_CONTACT_FETCH);
            apiRequestObject.setOptionalParameters(optionalParameters);
            apiRequestObject.setRequiredParameters(requiredParameters);
            BlockingApiGetRequest apiGetRequestRunnable = createBlockingApiGetRequest(mediaFire, apiRequestObject);
            apiGetRequestRunnable.sendRequest();
        }
    }
}
