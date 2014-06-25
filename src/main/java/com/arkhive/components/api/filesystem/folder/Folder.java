package com.arkhive.components.api.filesystem.folder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.arkhive.components.api.ApiRequest;
import com.arkhive.components.api.ApiRequestBuilder;
import com.arkhive.components.api.ApiResponse;
import com.arkhive.components.api.Utility;
import com.arkhive.components.api.filesystem.FileSystemItem;
import com.arkhive.components.sessionmanager.SessionManager;
//CHECKSTYLE:OFF
import com.google.gson.Gson;
//CHECKSTYLE:ON
import com.google.gson.JsonElement;

/**
 * @author
 */
public class Folder {
    private static final String COPY_URI = "/api/folder/copy.php";
    private static final String CREATE_URI = "/api/folder/create.php";
    private static final String MOVE_URI = "/api/folder/move.php";
    private static final String DELETE_URI = "/api/folder/delete.php";
    private static final String PURGE_URI = "/api/folder/purge.php";
    private static final String UPDATE_URI = "/api/folder/update.php";
    private static final String GET_INFO_URI = "/api/folder/get_info.php";
    private static final String GET_CONTENTS_URI = "/api/folder/get_content.php";
    private static final String GET_REVISION_URI = "/api/folder/get_revision.php";
    private static final String SEARCH_URI = "/api/folder/search.php";

  /*=================================================================================================================
   * FOLDER COPY API
   ==================================================================================================================*/

    /**
     * Makes a call to folder/copy.php.
     * <p/>
     * This method will try to copy one folder to another with a key of "destinationkey".
     *
     * @param folder      The FileSystemItem to copy.
     * @param destination The FileSystemItem destination.
     * @param sm          - session manager.
     * @return a FolderCopyREsponse object.
     */
    public static FolderCopyResponse copy(FileSystemItem folder, FileSystemItem destination, SessionManager sm) {
        String destinationKey;
        if (destination == null) {
            destinationKey = "";
        } else {
            destinationKey = destination.getKey();
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("folder_key_src", folder.getKey());
        map.put("folder_key_dst", destinationKey);

        JsonElement jsonResponse = sendRequest(map, COPY_URI, sm);
        return new Gson().fromJson(jsonResponse, FolderCopyResponse.class);
    }

    /**
     * Overloaded method which calls copy(SessionManager, String, String).
     * <p/>
     * This method will try to copy one folder to the root.
     *
     * @param folder The FileSystemItem to copy.
     * @param sm     - session manager.
     * @return a FolderCopyResponse object.
     */
    public static FolderCopyResponse copy(FileSystemItem folder, SessionManager sm) {
        return copy(folder, null, sm);
    }

  /*=================================================================================================================
   * FOLDER MOVE API
   ==================================================================================================================*/

    /**
     * Makes a call to folder/move.php.
     * <p/>
     * Calling this method will attempt to move a folder to a folder with a key of 'destinationKey'.
     *
     * @param folder      The FileSystemItem to move.
     * @param destination The FileSystemItem destination.
     * @param sm          - session manager.
     * @return a FolderMoveResponse object.
     */
    public static FolderMoveResponse move(FileSystemItem folder, FileSystemItem destination, SessionManager sm) {
        String folderKey = folder.getKey();
        String destinationKey;
        if (destination == null) {
            destinationKey = "";
        } else {
            destinationKey = destination.getKey();
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("folder_key_src", folderKey);
        map.put("folder_key_dst", destinationKey);

        JsonElement jsonResponse = sendRequest(map, MOVE_URI, sm);
        return new Gson().fromJson(jsonResponse, FolderMoveResponse.class);
    }

    /**
     * Overloaded method which calls move(SessionManager, String, String).
     * <p/>
     * This method will move the folder to the root directory.
     *
     * @param folder The FileSystemItem to move.
     * @param sm     - session manager.
     * @return a FolderMoveResponse object.
     */
    public static FolderMoveResponse move(FileSystemItem folder, SessionManager sm) {
        return move(folder, null, sm);
    }

  /*=================================================================================================================
   * FOLDER CREATE API
   ==================================================================================================================*/

    /**
     * Makes a call to folder/create.php.
     * <p/>
     * Calling this will create a folder in the folder with key of 'parentKey'.
     *
     * @param folder         The FileSystemItem to create.
     * @param name           - name of the folder to be created.
     * @param allowDuplicate - true if create folder with changed name if folder exists, false if not.
     * @param sm             - sesssion manager.
     * @return a FolderCreateResponse object.
     */
    public static FolderCreateResponse create(FileSystemItem folder, String name, boolean allowDuplicate,
                                              SessionManager sm) {
        String destinationKey;
        if (folder == null) {
            destinationKey = "";
        } else {
            destinationKey = folder.getKey();
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("foldername", name);
        map.put("parent_key", destinationKey);
        map.put("allow_duplicate_name", convertBoolean(allowDuplicate));

        JsonElement jsonResponse = sendRequest(map, CREATE_URI, sm);
        return new Gson().fromJson(jsonResponse, FolderCreateResponse.class);
    }

    /**
     * Overloaded method for calling the create(SessionManager, String, String, boolean) method.
     * <p/>
     * Calling this will create a folder in the root directory and will alter the requested name if there is a duplicate.
     *
     * @param name - name of the new folder.
     * @param sm   - session manager.
     * @return a FolderCreateResponse.
     */
    public static FolderCreateResponse create(String name, SessionManager sm) {
        return create(null, name, true, sm);
    }

    /**
     * Converts a boolean to a 'yes' or 'no' String.
     *
     * @param b - boolean to convert.
     * @return "yes" if b is true, "no" if b is false.
     */
    private static String convertBoolean(boolean b) {
        if (b) {
            return "yes";
        } else {
            return "no";
        }
    }

  /*=================================================================================================================
   * FOLDER DELETE/PURGE API
   ==================================================================================================================*/

    /**
     * Makes a call to folder/delete.php.
     *
     * @param folder The FileSystemItem to delete.
     * @param purge  - true if calling 'purge', false if calling 'delete'. true will permanently delete the folder.
     * @param sm     - session manager.
     * @return a FolderDeleteResponse object.
     */
    public static FolderDeleteResponse delete(FileSystemItem folder, boolean purge, SessionManager sm) {
        String key;
        if (folder == null) {
            key = "";
        } else {
            key = folder.getKey();
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("folder_key", key);

        JsonElement jsonResponse;
        if (purge) {
            jsonResponse = sendRequest(map, PURGE_URI, sm);
        } else {
            jsonResponse = sendRequest(map, DELETE_URI, sm);
        }

        return new Gson().fromJson(jsonResponse, FolderDeleteResponse.class);
    }

  /*=================================================================================================================
   * FOLDER SEARCH API
   ==================================================================================================================*/

    public static FolderSearchResponse search(String searchText, SessionManager sm) {
        if (searchText == null) {
            searchText = "";
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("search_text", searchText);

        JsonElement jsonResponse = sendRequest(map, SEARCH_URI, sm);
        return new Gson().fromJson(jsonResponse, FolderSearchResponse.class);
    }

  /*=================================================================================================================
   * FOLDER UPDATE API
   ==================================================================================================================*/

    /**
     * Makes a call to folder/update.php.
     *
     * @param folder             - filesystem item which contains a key that will be updated.
     * @param optionalParameters - optional parameters (can pass null if there are none).
     * @param sm                 - session manager.
     * @return a FolderUpdateResponse object.
     */
    public static FolderUpdateResponse update(FileSystemItem folder, Map<String, String> optionalParameters, SessionManager sm) {
        String key;
        if (folder == null) {
            key = "";
        } else {
            key = folder.getKey();
        }
        if (optionalParameters == null) {
            optionalParameters = new HashMap<String, String>();
        }
        optionalParameters.put("folder_key", key);

        JsonElement jsonResponse = sendRequest(optionalParameters, UPDATE_URI, sm);
        return new Gson().fromJson(jsonResponse, FolderUpdateResponse.class);
    }

    public static ApiResponse setPublic(FileSystemItem folder, SessionManager sm) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("folder_key", folder.getKey());
        parameters.put("privacy", "public");

        JsonElement jsonResponse = sendRequest(parameters, UPDATE_URI, sm);
        return new Gson().fromJson(jsonResponse, FolderUpdateResponse.class);
    }

    public static ApiResponse setPrivate(FileSystemItem folder, SessionManager sm) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("folder_key", folder.getKey());
        parameters.put("privacy", "private");

        JsonElement jsonResponse = sendRequest(parameters, UPDATE_URI, sm);
        return new Gson().fromJson(jsonResponse, FolderUpdateResponse.class);
    }

    /*=================================================================================================================
     * FOLDER GET CONTENTS API
     ==================================================================================================================*/
    public static FolderGetContentsResponse getContents(FileSystemItem item, SessionManager sm, int chunk, ContentType type) {
        String key;
        if (item == null) {
            key = "";
        } else {
            key = item.getKey();
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("folder_key", key);
        map.put("content_type", ContentType.toString(type));
        map.put("chunk", String.valueOf(chunk));

        ApiRequestBuilder builder = new ApiRequestBuilder();
        builder.domain(sm.getDomain());
        builder.uri(GET_CONTENTS_URI);
        builder.sessionManager(sm);
        builder.httpInterface(sm.getHttpInterface());
        builder.parameters(map);

        ApiRequest request = builder.build();

        String responseString = request.submitRequestSync();
        Gson gson = new Gson();
        return gson.fromJson(Utility.getResponseString(responseString), FolderGetContentsResponse.class);
    }

    public static List<FileSystemItem> getContents(FileSystemItem item, SessionManager sm,
                                                   GetContentsUpdateListener listener) {
        List<FileSystemItem> allItems = new LinkedList<FileSystemItem>();
        //get folders first
        FolderGetContentsResponse response;
        int chunk = 1;
        do {
            response = getContents(item, sm, chunk, ContentType.FOLDERS);
            // add contents to return value
            List<FileSystemItem> itemsToAdd =
                    Converter.convertFolders(response.getFolderContents().getFolders(), item.getParentFolderKey());
            allItems.addAll(itemsToAdd);

            //give listener some data
            if (listener != null) {
                listener.contentsReceived(itemsToAdd);
            }

            //increment next chunk
            chunk++;
        } while (response.getFolderContents().getFolders().size() != 0);

        //get folders (reset chunk)
        chunk = 1;
        do {
            response = getContents(item, sm, chunk, ContentType.FILES);
            //add contents to return value
            List<FileSystemItem> itemsToAdd =
                    Converter.convertFiles(response.getFolderContents().getFiles(), item.getParentFolderKey());
            allItems.addAll(itemsToAdd);

            //give listener some data
            if (listener != null) {
                listener.contentsReceived(itemsToAdd);
            }

            //increment next chunk
            chunk++;
        } while (response.getFolderContents().getFiles().size() != 0);

        if (listener != null) {
            listener.finishedReceivingContents();
        }
        return allItems;
    }

    /**
     * Content Type for folder/get_content.php parameter.
     *
     * @author
     */
    public enum ContentType {
        FILES, FOLDERS;

        public static String toString(ContentType type) {
            String ret;
            switch (type) {
                case FILES:
                    ret = "files";
                    break;
                case FOLDERS:
                    ret = "folders";
                    break;
                default:
                    ret = "";
            }
            return ret;
        }
    }

  /*=================================================================================================================
   * FOLDER GET INFO API
   ==================================================================================================================*/

    /**
     * Makes a call to folder/get_info.php.
     *
     * @param item - FileSystemItem
     * @param sm   - session manager.
     * @return a FolderGetInfoResponse object.
     */
    public static FolderGetInfoResponse getInfo(FileSystemItem item, SessionManager sm) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("folder_key", item.getKey());

        JsonElement jsonResponse = sendRequest(map, GET_INFO_URI, sm);
        return new Gson().fromJson(jsonResponse, FolderGetInfoResponse.class);
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
