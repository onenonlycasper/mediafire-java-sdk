package com.arkhive.components.api.upload.process;

import com.arkhive.components.api.upload.errors.ResumableResultCode;
import com.arkhive.components.api.upload.listeners.UploadListenerUI;
import com.arkhive.components.api.upload.responses.ResumableResponse;
import com.arkhive.components.sessionmanager.SessionManager;
import com.arkhive.components.uploadmanager.uploaditem.UploadItem;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Runnable for making api call to upload/resumable.php. *
 *
 * @author Chris Najar
 */
public class ResumableProcess implements Runnable {
    private static final String TAG        = ResumableProcess.class.getSimpleName();
    private static final String UPLOAD_URI = "/api/upload/resumable.php";

    private final UploadItem     uploadItem;
    private final Gson           gson;
    private final SessionManager sessionManager;
    private final Logger logger = LoggerFactory.getLogger(ResumableProcess.class);

    /**
     * Constructor for an upload with a listener.
     *
     * @param sessionManager - the session to use for this upload process
     * @param uploadItem     - the item to be uploaded
     */
    public ResumableProcess(SessionManager sessionManager, UploadItem uploadItem) {
        super();
        this.sessionManager = sessionManager;
        this.uploadItem = uploadItem;
        this.gson = new Gson();
    }

    @Override
    public void run() {
        resumable();
    }

    /**
     * begin the upload process via the following steps:
     * 1. set the LinkedList of Chunk within the item we want to upload
     * 2. create chunks for all units of the item we want to upload
     * 3. send upload POST request for each chunk
     */
    private void resumable() {
        ResumableResponse response = new ResumableResponse();
        int numChunks = uploadItem.getChunkData().getNumberOfUnits();
        int unitSize = uploadItem.getChunkData().getUnitSize();
        long fileSize = uploadItem.getFileData().getFileSize();
        // loop through our chunks and create http post with header data and send after we are done looping,
        // let the listener know we are completed

        for (int chunkNumber = 0; chunkNumber < numChunks; chunkNumber++) {
            // if the bitmap says this chunk number is uploaded then we can just skip it, if not, we upload it.
            if (!uploadItem.getBitmap().isUploaded(chunkNumber)) {
                // get the chunk size for this chunk
                int chunkSize = getChunkSize(chunkNumber, numChunks, fileSize, unitSize);

                // generate the chunk
                FileInputStream fis;
                BufferedInputStream bis;
                String chunkHash;
                String encodedShortFileName;
                byte[] chunkData;
                try {
                    fis = new FileInputStream(uploadItem.getPath());
                    bis = new BufferedInputStream(fis);
                    chunkData = createUploadChunk(chunkSize, chunkNumber, bis);
                    chunkHash = getSHA256(chunkData);
                    encodedShortFileName = URLEncoder.encode(uploadItem.getShortFileName(), "UTF-8");
                    fis.close();
                    bis.close();
                } catch (FileNotFoundException e) {
                    this.exceptionHandler(e);
                    return;
                } catch (NoSuchAlgorithmException e) {
                    this.exceptionHandler(e);
                    return;
                } catch (UnsupportedEncodingException e) {
                    this.exceptionHandler(e);
                    return;
                } catch (IOException e) {
                    this.exceptionHandler(e);
                    return;
                }

                // generate the post headers
                HashMap<String, String> headers =
                        generatePostHeaders(encodedShortFileName, fileSize, chunkNumber, chunkHash, chunkSize);

                // generate the get parameters
                HashMap<String, String> parameters =
                        generateGetParameters();

                // now send the http post request
                String jsonResponse =
                        sessionManager.getHttpInterface().
                                sendPostRequest(sessionManager.getDomain(), UPLOAD_URI, parameters, headers, chunkData);

                // if jsonResponse is empty, then HttpInterface.sendGetRequest() has no internet connectivity so we
                // call lostInternetConnectivity() and UploadManager will move this item to the backlog queue.
                if (jsonResponse.isEmpty()) {
                    notifyManagerLostConnection();
                    return;
                }

                // generate the ResumableResponse object
                response =
                        gson.fromJson(getResponseString(jsonResponse), ResumableResponse.class);

                // set poll upload key if possible
                if (shouldSetPollUploadKey(response)) {
                    uploadItem.setPollUploadKey(response.getDoUpload().getKey());
                }

                // if API response code OR Upload Response Result code have an error then we need to terminate the process
                if (response.hasError()) {
                    notifyManagerCancelled(response);
                    return;
                }

                if (response.getDoUpload().getResultCode() != ResumableResultCode.NO_ERROR
                        && response.getDoUpload().getResultCode() != ResumableResultCode.SUCCESS_FILE_MOVED_TO_ROOT) {
                    // let the listeners know we are done with this process (because there was an error in this case)
                    notifyManagerCancelled(response);
                    return;
                }
            }

            // update listeners on progress each loop
            notifyListenersProgressUpdate(chunkNumber, numChunks);

            // check if we need to cancel upload because another entitiy changes the UploadItem status to CANCELLED
            switch (uploadItem.getStatus()) {
                case CANCELLED: //paused, cancel upload
                    this.notifyManagerCancelled(response);
                    return;
                case PAUSED: // paused, cancel upload.
                    this.notifyManagerCancelled(response);
                    return;
                case READY: // continue upload
                    break;
                default: // this should never happen
                    break;
            }
        } // end loop

        // let the listeners know that upload has attempted to upload all chunks.
        notifyManagerCompleted(response);
    }

    private void exceptionHandler(Exception e) {
        logger.warn(TAG + " Exception: " + e);
        e.printStackTrace();
        // if we catch FileNotFoundException, NoSuchAlgorithmException,
        // or UnsupportedEncoding Exception, notify manager and listeners
        // that exception was caught and process has been cancelled
        notifyManagerException(e);
    }

    /**
     * gives the listeners a progress update of the number of chunks completed.
     */
    private void notifyListenersProgressUpdate(int chunkNumber, int numChunks) {
        for (UploadListenerUI listener : uploadItem.getUiListeners()) {
            //we multiply the % by 90 because we allocate 5% to upload/poll_upload and 5% to upload/check
            double chunkPercent = (double) chunkNumber / (double) numChunks;
            chunkPercent *= 100;
            chunkPercent *= 0.9;
            double percentCompleted = 5 + chunkPercent;
            listener.onProgressUpdate(uploadItem, (int) percentCompleted);
        }
    }

    /**
     * notifies the upload manager attached to the upload item that this process has finished.
     *
     * @param response The response from the resumable upload API request.
     */
    private void notifyManagerCompleted(ResumableResponse response) {
        if (uploadItem.getUploadManagerListener() != null) {
            uploadItem.getUploadManagerListener().onResumableCompleted(uploadItem);
        }
    }

    /**
     * lets listeners know that this process has been cancelled for this item. manager is informed of lost connection.
     */
    private void notifyManagerLostConnection() {
        // notify listeners that connection was lost
        if (uploadItem.getUploadManagerListener() != null) {
            uploadItem.getUploadManagerListener().onLostConnection(uploadItem);
        }
        notifyListenersCancelled();
    }

    /**
     * generates a HashMap of the GET parameters.
     *
     * @return The parameters to use for the upload API request.
     */
    private HashMap<String, String> generateGetParameters() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("session_token", sessionManager.requestUploadActionToken().getSessionToken());
        parameters.put("action_on_duplicate", uploadItem.getUploadOptions().getActionOnDuplicate());
        parameters.put("response_format", "json");
        parameters.put("version_control", uploadItem.getUploadOptions().getVersionControl());
        parameters.put("upload_folder_key", uploadItem.getUploadOptions().getUploadFolderKey());
        if (!uploadItem.getUploadOptions().getUploadPath().isEmpty()) {
            parameters.put("path", uploadItem.getUploadOptions().getUploadPath());
        }
        return parameters;
    }

    /**
     * generates a HashMap of the POST headers.
     *
     * @param encodedShortFileName The file name after being URLEncoded.
     * @param fileSize             The size of the file in bytes.
     * @param chunkNumber          The number of the current chunk.
     * @param chunkHash            The hash of the current chunk.
     * @param chunkSize            The size of the current chunk in bytes.
     *
     * @return A HashMap<String, String> containing the parameters to use with the HTTP POST request.
     */
    private HashMap<String, String> generatePostHeaders(String encodedShortFileName,
                                                        long fileSize, int chunkNumber, String chunkHash, int chunkSize) {
        HashMap<String, String> headers = new HashMap<String, String>();
        // these headers are related to the entire file
        headers.put("x-filename", encodedShortFileName);
        headers.put("x-filesize", String.valueOf(fileSize));
        headers.put("x-filehash", uploadItem.getFileData().getFileHash());
        // these headers are related to the individual chunk
        headers.put("x-unit-id", Integer.toString(chunkNumber));
        headers.put("x-unit-hash", chunkHash);
        headers.put("x-unit-size", Integer.toString(chunkSize));
        return headers;
    }

    /**
     * lets listeners know that this process has been cancelled for the upload item.
     */
    private void notifyListenersCancelled() {
        // notify ui listeners that task has been cancelled
        for (UploadListenerUI listener : uploadItem.getUiListeners()) {
            listener.onCancelled(uploadItem);
        }
        // notify database listener that task has been cancelled
        if (uploadItem.getDatabaseListener() != null) {
            uploadItem.getDatabaseListener().onCancelled(uploadItem);
        }
    }

    /**
     * lets listeners know that this process has been cancelled for this upload item. manager is informed of exception.
     *
     * @param e The exception thrown by the upload process.
     */
    private void notifyManagerException(Exception e) {
        // notify listeners that there has been an exception
        if (uploadItem.getUploadManagerListener() != null) {
            uploadItem.getUploadManagerListener().onProcessException(uploadItem, e);
        }

        notifyListenersCancelled();
    }

    /**
     * notifies the upload manager that the process has been cancelled and then notifies other listeners.
     *
     * @param response The response from the resumable upload API request.
     */
    private void notifyManagerCancelled(ResumableResponse response) {
        if (uploadItem.getUploadManagerListener() != null) {
            uploadItem.getUploadManagerListener().onCancelled(uploadItem, response);
        }
        notifyListenersCancelled();
    }

    /**
     * calculates the chunk size.
     *
     * @param chunkNumber The current chunk number.
     * @param numChunks   The total number of chunks.
     * @param fileSize    The file size in bytes.
     * @param unitSize    The size of a single chunk.
     *
     * @return The actual chunk size.
     */
    private int getChunkSize(int chunkNumber, int numChunks, long fileSize, int unitSize) {
        if (chunkNumber >= numChunks) {
            return 0; // represents bad size
        }

        if (fileSize % unitSize == 0) { // all units will be of unitSize
            return unitSize;
        } else if (chunkNumber < numChunks - 1) { // this unit is of unitSize
            return unitSize;
        } else { // this unit is "special" and is the modulo of fileSize and unitSize
            return (int) (fileSize % unitSize);
        }
    }

    /**
     * only set the upload key for the upload item if response/doupload/result is 14 or 0.
     *
     * @param response The response from the resumable upload API request.
     *
     * @return Flag indicating if the upload key should be set.
     */
    private boolean shouldSetPollUploadKey(ResumableResponse response) {
        switch (response.getDoUpload().getResultCode()) {
            case NO_ERROR:
            case SUCCESS_FILE_MOVED_TO_ROOT:
                return true;
            default:
                return false;
        }
    }

    /**
     * creates an upload chunk array of bytes.
     *
     * @param unitSize   The size of the chunk to create.
     * @param unitNumber The unit number of this chunk.
     * @param fileStream The BufferedInputStream containing the upload file.
     *
     * @return an array of bytes with unitSize size, or 0 size if IOException is caught
     */
    private byte[] createUploadChunk(long unitSize, int unitNumber, BufferedInputStream fileStream) throws IOException {
        byte[] readBytes = new byte[(int) unitSize];
        int readSize;
        readSize = fileStream.read(readBytes, 0, (int) unitSize);
        if (readSize != unitSize) {
            byte[] temp = new byte[readSize];
            System.arraycopy(readBytes, 0, temp, 0, readSize);
            readBytes = temp;
        }
        return readBytes;
    }

    /**
     * converts an array of bytes into a SHA256 hash.
     *
     * @param chunkData The chunk to hash.
     *
     * @return The SHA-256 hash of an upload chunk.
     */
    private String getSHA256(byte[] chunkData) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(chunkData);
        StringBuilder sb = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String tempString = Integer.toHexString((hashByte & 0xFF) | 0x100).substring(1, 3);
            sb.append(tempString);
        }
        return sb.toString();
    }

    /**
     * converts a String received from JSON format into a response String.
     *
     * @param response The response received in JSON format.
     *
     * @return The response received which can then be parsed into a specific format as per Gson.fromJson().
     */
    private String getResponseString(String response) {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(response);
        if (element.isJsonObject()) {
            JsonObject jsonResponse = element.getAsJsonObject().get("response").getAsJsonObject();
            return jsonResponse.toString();
        } else {
            return "";
        }
    }
}
