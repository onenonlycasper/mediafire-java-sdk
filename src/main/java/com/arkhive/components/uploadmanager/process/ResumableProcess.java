package com.arkhive.components.uploadmanager.process;

import com.arkhive.components.api.upload.errors.ResumableResultCode;
import com.arkhive.components.api.upload.responses.ResumableResponse;
import com.arkhive.components.sessionmanager.SessionManager;
import com.arkhive.components.uploadmanager.listeners.UploadListenerManager;
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
    private final SessionManager sessionManager;
    private final UploadListenerManager uploadManager;
    private final Logger logger = LoggerFactory.getLogger(ResumableProcess.class);

    /**
     * Constructor for an upload with a listener.
     *
     * @param sessionManager - the session to use for this upload process
     * @param uploadItem     - the item to be uploaded
     */
    public ResumableProcess(SessionManager sessionManager, UploadListenerManager uploadManager, UploadItem uploadItem) {
        this.sessionManager = sessionManager;
        this.uploadManager = uploadManager;
        this.uploadItem = uploadItem;
    }

    @Override
    public void run() {
        logger.info("run()");
        resumable();
    }

    /**
     * begin the upload process via the following steps:
     * 1. set the LinkedList of Chunk within the item we want to upload
     * 2. create chunks for all units of the item we want to upload
     * 3. send upload POST request for each chunk
     */
    private void resumable() {
        logger.info("resumable()");
        int numChunks = uploadItem.getChunkData().getNumberOfUnits();
        int unitSize = uploadItem.getChunkData().getUnitSize();
        long fileSize = uploadItem.getFileData().getFileSize();
        logger.info("number of chunks: " + numChunks);
        logger.info("size of units: " + unitSize);
        logger.info("size of file: " + fileSize);
        // loop through our chunks and create http post with header data and send after we are done looping,
        // let the listener know we are completed

        for (int chunkNumber = 0; chunkNumber < numChunks; chunkNumber++) {
            logger.info("   enter chunk upload loop()");
            // if the bitmap says this chunk number is uploaded then we can just skip it, if not, we upload it.
            if (uploadItem.getBitmap().isUploaded(chunkNumber)) {
                logger.info("chunk #" + chunkNumber + " already uploaded");
            } else {
                logger.info("chunk #" + chunkNumber + " not uploaded yet");
                // get the chunk size for this chunk
                int chunkSize = getChunkSize(chunkNumber, numChunks, fileSize, unitSize);

                // generate the chunk
                FileInputStream fis;
                BufferedInputStream bis;
                String chunkHash;
                String encodedShortFileName;
                byte[] chunkData;
                try {
                    fis = new FileInputStream(uploadItem.getFileData().getFilePath());
                    bis = new BufferedInputStream(fis);
                    chunkData = createUploadChunk(unitSize, chunkNumber, bis);
                    chunkHash = getSHA256(chunkData);
                    encodedShortFileName = URLEncoder.encode(uploadItem.getFileName(), "UTF-8");

                    logger.info("chunk #" + chunkNumber + " hash: " + chunkHash);
                    logger.info("chunk #" + chunkNumber + " size: " + chunkSize);
                    logger.info("chunk #" + chunkNumber + " name: " + encodedShortFileName);

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
                HashMap<String, String> headers = generatePostHeaders(encodedShortFileName, fileSize, chunkNumber, chunkHash, chunkSize);

                // generate the get parameters
                HashMap<String, String> parameters = generateGetParameters();

                // now send the http post request
                String jsonResponse;
                try {
                    jsonResponse = sessionManager.getHttpInterface().sendPostRequest(sessionManager.getDomain(), UPLOAD_URI, parameters, headers, chunkData);
                } catch (IOException e) {
                    e.printStackTrace();
                    notifyManagerException(e);
                    return;
                }

                // if jsonResponse is empty, then HttpInterface.sendGetRequest() has no internet connectivity so we
                // call lostInternetConnectivity() and UploadManager will move this item to the backlog queue.
                if (jsonResponse.isEmpty()) {
                    notifyManagerLostConnection();
                    return;
                }

                Gson gson = new Gson();
                // generate the ResumableResponse object
                ResumableResponse response = gson.fromJson(getResponseString(jsonResponse), ResumableResponse.class);

                // set poll upload key if possible
                if (shouldSetPollUploadKey(response)) {
                    logger.info("have a poll upload key: " + response.getDoUpload().getPollUploadKey());
                    uploadItem.setPollUploadKey(response.getDoUpload().getPollUploadKey());
                }

                // if API response code OR Upload Response Result code have an error then we need to terminate the process
                if (response.hasError()) {
                    logger.info("response has an error # " + response.getErrorNumber() + ": " + response.getMessage());
                    notifyManagerCancelled(response);
                    return;
                }

                if (response.getDoUpload().getResultCode() != ResumableResultCode.NO_ERROR) {
                    // let the listeners know we are done with this process (because there was an error in this case)
                    if (response.getDoUpload().getResultCode() != ResumableResultCode.SUCCESS_FILE_MOVED_TO_ROOT) {
                        // let the listeners know we are done with this process (because there was an error in this case)
                        logger.info("cancelling because result code: " + response.getDoUpload().getResultCode().toString());
                        notifyManagerCancelled(response);
                        return;
                    }
                }

            }

            // update listeners on progress each loop
            notifyListenersProgressUpdate(chunkNumber, numChunks);
        } // end loop

        // let the listeners know that upload has attempted to upload all chunks.
        notifyManagerCompleted();
    }

    private void exceptionHandler(Exception e) {
        logger.warn(TAG + "exceptionHandler: " + e);
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
        logger.info("notifyListenersProgressUpdate()");
        if (uploadManager != null) {
            uploadManager.onProgressUpdate(uploadItem, chunkNumber, numChunks);
        }
    }

    /**
     * notifies the upload manager attached to the upload item that this process has finished.
     *
     */
    private void notifyManagerCompleted() {
        logger.info("notifyManagerCompleted()");
        if (uploadManager != null) {
            uploadManager.onResumableCompleted(uploadItem);
        }
    }

    /**
     * lets listeners know that this process has been cancelled for this item. manager is informed of lost connection.
     */
    private void notifyManagerLostConnection() {
        logger.info("notifyManagerLostConnection()");
        // notify listeners that connection was lost
        if (uploadManager != null) {
            uploadManager.onLostConnection(uploadItem);
        }
    }

    /**
     * generates a HashMap of the GET parameters.
     *
     * @return The parameters to use for the upload API request.
     */
    private HashMap<String, String> generateGetParameters() {
        logger.info("generateGetParameters()");
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("session_token", sessionManager.requestUploadActionToken().getSessionToken());
        parameters.put("action_on_duplicate", uploadItem.getUploadOptions().getActionOnDuplicate());
        parameters.put("response_format", "json");
        parameters.put("version_control", uploadItem.getUploadOptions().getVersionControl());
        parameters.put("upload_folder_key", uploadItem.getUploadOptions().getUploadFolderKey());

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
    private HashMap<String, String> generatePostHeaders(String encodedShortFileName, long fileSize, int chunkNumber, String chunkHash, int chunkSize) {
        logger.info("generatePostHeaders()");
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
     * lets listeners know that this process has been cancelled for this upload item. manager is informed of exception.
     *
     * @param e The exception thrown by the upload process.
     */
    private void notifyManagerException(Exception e) {
        logger.info("notifyManagerException()");
        // notify listeners that there has been an exception
        if (uploadManager != null) {
            uploadManager.onProcessException(uploadItem, e);
        }
    }

    /**
     * notifies the upload manager that the process has been cancelled and then notifies other listeners.
     *
     * @param response The response from the resumable upload API request.
     */
    private void notifyManagerCancelled(ResumableResponse response) {
        logger.info("notifyManagerCancelled()");
        if (uploadManager != null) {
            uploadManager.onCancelled(uploadItem, response);
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
        logger.info("shouldSetPollUploadKey()");
        switch (response.getDoUpload().getResultCode()) {
            case NO_ERROR:
            case SUCCESS_FILE_MOVED_TO_ROOT:
                return true;
            default:
                return false;
        }
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
        logger.info("getChunkSize()");
        int chunkSize;
        if (chunkNumber >= numChunks) {
            chunkSize = 0; // represents bad size
        } else {
            if (fileSize % unitSize == 0) { // all units will be of unitSize
                logger.info("CHUNK SIZE IS: " + unitSize);
                chunkSize = unitSize;
            } else if (chunkNumber < numChunks - 1) { // this unit is of unitSize
                logger.info("CHUNK SIZE IS: " + unitSize);
                chunkSize = unitSize;
            } else { // this unit is "special" and is the modulo of fileSize and unitSize
                logger.info("CHUNK SIZE IS: " + unitSize);
                chunkSize = (int) (fileSize % unitSize);
            }
        }

        logger.info("RETURNING CHUNK SIZE OF: " + chunkSize);
        return chunkSize;
    }

    /**
     * creates an upload chunk array of bytes based on a position in a file.
     */
    private byte[] createUploadChunk(long unitSize, int chunkNumber, BufferedInputStream fileStream) throws IOException {
        logger.info("createUploadChunk()");
        byte[] readBytes = new byte[(int) unitSize];
        logger.info("created byte array of size: " +readBytes.length);
        int offset = (int) (unitSize * chunkNumber);
        logger.info("offset is: " + offset);
        logger.info("using unit size of: " + unitSize);
        logger.info("starting read of file which has available bytes to read of: " + fileStream.available());
        int readSize = fileStream.read(readBytes, offset, (int) unitSize);
        logger.info("got read size of: " + readSize);
        if (readSize != unitSize) {
            byte[] temp = new byte[readSize];
            System.arraycopy(readBytes, 0, temp, 0, readSize);
            readBytes = temp;
        }

        //debug
        StringBuilder sb = new StringBuilder();
        for (Byte b : readBytes) {
            sb.append(b.toString());
        }
        logger.info("CREATED UPLOAD CHUNK OF: " + sb.toString());

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
        logger.info("getSHA256()");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(chunkData);
        StringBuilder sb = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String tempString = Integer.toHexString((hashByte & 0xFF) | 0x100).substring(1, 3);
            sb.append(tempString);
        }
        String hash = sb.toString();
        logger.info("HASH FOR THIS CHUNK IS: " + hash);
        return hash;
    }

    /**
     * converts a String received from JSON format into a response String.
     *
     * @param response The response received in JSON format.
     *
     * @return The response received which can then be parsed into a specific format as per Gson.fromJson().
     */
    private String getResponseString(String response) {
        logger.info("getResponseString()");
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
