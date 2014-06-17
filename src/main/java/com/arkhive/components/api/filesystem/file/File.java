package com.arkhive.components.api.filesystem.file;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.arkhive.components.api.ApiRequest;
import com.arkhive.components.api.ApiRequestBuilder;
import com.arkhive.components.api.ApiResponse;
import com.arkhive.components.api.Utility;
import com.arkhive.components.api.filesystem.FileSystemItem;
import com.arkhive.components.sessionmanager.SessionManager;

/**
 * File manipulation methods.
 */
public class File {
    private static final String COPY_URI = "/api/file/copy.php";
    private static final String DELETE_URI = "/api/file/delete.php";
    private static final String PURGE_URI = "/api/file/purge.php";
    private static final String MOVE_URI = "/api/file/move.php";
    private static final String UPDATE_URI = "/api/file/update.php";
    private static final String GET_INFO_URI = "/api/file/get_info.php";
    private static final String GET_LINKS_URI = "/api/file/get_links.php";

    /**
     * Copy a file to a new location on the filesystem.
     *
     * @param file           The FileSystemItem to copy.
     * @param folder         The FileSystemItem to copy the file to.
     * @param sessionManager The SessionManager to use for the API operations.
     * @return A FileCopyResponse containing the results of the copy operation.
     */
    public static FileCopyResponse copy(FileSystemItem file, FileSystemItem folder, SessionManager sessionManager) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("quick_key", file.getKey());
        parameters.put("folder_key", folder.getKey());
        JsonElement jsonResponse = sendRequest(parameters, COPY_URI, sessionManager);
        return new Gson().fromJson(jsonResponse, FileCopyResponse.class);
    }

    /**
     * Delete a file from the filesystem.
     *
     * @param file           The FileSystemItem to delete.
     * @param sessionManager The SessionManager to use for the API operations.
     * @return A FileDeleteResponse containing the result of the delete operation.
     */
    public static ApiResponse delete(FileSystemItem file, SessionManager sessionManager) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("quick_key", file.getKey());
        JsonElement jsonResponse = sendRequest(parameters, DELETE_URI, sessionManager);
        return new Gson().fromJson(jsonResponse, ApiResponse.class);
    }

    /**
     * Purge a file from the filesystem.
     * This operation will completly remove a file from the remote file system.
     *
     * @param file           The FileSystemItem to delete.
     * @param sessionManager The SessionManager to use for API operations.
     * @return A FileDeleteResponse containing the results of the purge operation.
     */
    public static ApiResponse purge(FileSystemItem file, SessionManager sessionManager) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("quick_key", file.getKey());
        JsonElement jsonResponse = sendRequest(parameters, PURGE_URI, sessionManager);
        return new Gson().fromJson(jsonResponse, ApiResponse.class);
    }

    /**
     * Move a file to a new location on the file system.
     *
     * @param file           The FileSystemItem to move.
     * @param folder         The FileSystemItem destination.
     * @param sessionManager The SessionManager to use for API operations.
     * @return A FileMoveResponse containing the results of the move operation.
     */
    public static ApiResponse move(FileSystemItem file, FileSystemItem folder, SessionManager sessionManager) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("quick_key", file.getKey());
        parameters.put("folder_key", folder.getKey());
        JsonElement jsonResponse = sendRequest(parameters, MOVE_URI, sessionManager);
        return new Gson().fromJson(jsonResponse, ApiResponse.class);
    }

    /**
     * Mark a file as private.
     *
     * @param file           The FileSystemItem to mark as private.
     * @param sessionManager The SessionManager to use for the update operation.
     * @return A FileUpdateResponse containing the result of the update operation.
     */
    public static ApiResponse setPrivate(FileSystemItem file, SessionManager sessionManager) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("quick_key", file.getKey());
        parameters.put("privacy", "private");
        JsonElement jsonResponse = sendRequest(parameters, UPDATE_URI, sessionManager);
        return new Gson().fromJson(jsonResponse, ApiResponse.class);
    }

    /**
     * Mark a file as public.
     *
     * @param file           The FileSystemItem to make as public.
     * @param sessionManager The SessionManager to use for the update operation.
     * @return A FileUpdateResponse containing the result of the update operation.
     */
    public static ApiResponse setPublic(FileSystemItem file, SessionManager sessionManager) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("quick_key", file.getKey());
        parameters.put("privacy", "public");
        JsonElement jsonResponse = sendRequest(parameters, UPDATE_URI, sessionManager);
        return new Gson().fromJson(jsonResponse, ApiResponse.class);
    }

    /**
     * Rename a file.
     *
     * @param file The FileSystemItem to rename.
     * @param name The new name for the FileSystemItem.
     * @param sm   The SessionManager to use for the rename operation.
     * @return A FileUpdateRespone containing the result of the rename operation.
     */
    public static ApiResponse rename(FileSystemItem file, String name, SessionManager sm) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("quick_key", file.getKey());
        parameters.put("filename", name);
        JsonElement jsonResponse = sendRequest(parameters, UPDATE_URI, sm);
        return new Gson().fromJson(jsonResponse, ApiResponse.class);
    }

    /**
     * Update the description for a file.
     *
     * @param file        The FileSystemItem to update.
     * @param description The new description for the FileSystemItem.
     * @param sm          The SessionManager to use for the update operation.
     * @return A FileUpdateResponse containing the result of the update operation.
     */
    public static ApiResponse updateDescription(FileSystemItem file, String description, SessionManager sm) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("quick_key", file.getKey());
        parameters.put("description", description);
        JsonElement jsonResponse = sendRequest(parameters, UPDATE_URI, sm);
        return new Gson().fromJson(jsonResponse, ApiResponse.class);
    }

    /**
     * Update the tags for a FileSystemItem.
     *
     * @param file The FileSystemItem to update.
     * @param tags A space seperated list of tags to add to the FileSystemItem.
     * @param sm   The SessionManager to use for the update operation.
     * @return A FileUpdateReponse containing the result of the update operation.
     */
    public static ApiResponse updateTags(FileSystemItem file, String tags, SessionManager sm) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("quick_key", file.getKey());
        parameters.put("tags", tags);
        JsonElement jsonResponse = sendRequest(parameters, UPDATE_URI, sm);
        return new Gson().fromJson(jsonResponse, ApiResponse.class);
    }

    /**
     * Get the file info for a FileSystemItem.
     *
     * @param file - The FileSystemItem to get the info of.
     * @param sm   - the SessionMAnager to use for the operation.
     * @return A FileGetInfoResponse containing the result of the operation.
     */
    public static FileGetInfoResponse getInfo(FileSystemItem file, SessionManager sm) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("quick_key", file.getKey());
        JsonElement jsonResponse = sendRequest(parameters, GET_INFO_URI, sm);
        return new Gson().fromJson(jsonResponse, FileGetInfoResponse.class);
    }

    /**
     * Get the links for a FileSystemItem.
     *
     * @param file - the FileSystemItem to get the links of.
     * @param sm   - the SessionManager to use for the operation.
     * @return a FileGetLinksResponse containing the result of the operation.
     */
    public static FileGetLinksResponse getLinks(FileSystemItem file, SessionManager sm) {
        return getLinks(file, sm, LinkType.ALL);
    }

    public static FileGetLinksResponse getLinks(FileSystemItem file, SessionManager sm, LinkType type) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("quick_key", file.getKey());
        switch (type) {
            case ALL:
                break;
            case DIRECT_DOWNLOAD:
                parameters.put("link_type", "direct_download");
                break;
            case EDIT:
                parameters.put("link_type", "edit");
                break;
            case NORMAL:
                parameters.put("link_type", "normal_download");
                break;
            case ONE_TIME_DOWNLOAD:
                parameters.put("link_type", "one_time_download");
                break;
            case VIEW:
                parameters.put("link_type", "view");
                break;
            default:
                break;

        }
        JsonElement jsonResponse = sendRequest(parameters, GET_LINKS_URI, sm);
        return new Gson().fromJson(jsonResponse, FileGetLinksResponse.class);
    }

    /**
     * A simple enum for passing link type which is used in the getLinks() method.
     *
     * @author Chris Najar
     */
    public enum LinkType {
        VIEW, EDIT, NORMAL, ONE_TIME_DOWNLOAD, DIRECT_DOWNLOAD, ALL,
    }

    /**
     * Submit a request to the API.
     *
     * @param parameters A Map<String, String> of parameters to pass to the API.
     * @param apiCall    The API call to make.
     * @param sm         The SessionManager to use for the API operation.
     * @return A JsonElement containing the response from the API call.
     */
    private static JsonElement sendRequest(Map<String, String> parameters, String apiCall, SessionManager sm) {
        try {
            for (Entry<String, String> e : parameters.entrySet()) {
                e.setValue(URLEncoder.encode(e.getValue(), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
        ApiRequestBuilder builder = new ApiRequestBuilder();
        builder.domain(sm.getDomain());
        builder.sessionManager(sm);
        builder.httpInterface(sm.getHttpInterface());
        builder.parameters(parameters);
        builder.uri(apiCall);

        ApiRequest request = builder.build();

        String responseString = request.submitRequestSync();
        return Utility.getResponseElement(responseString);
    }
}
