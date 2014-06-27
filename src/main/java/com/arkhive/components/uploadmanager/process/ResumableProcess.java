package com.arkhive.components.uploadmanager.process;

import com.arkhive.components.core.MediaFire;
import com.arkhive.components.core.module_api.codes.ResumableResultCode;
import com.arkhive.components.core.module_api.responses.UploadResumableResponse;
import com.arkhive.components.uploadmanager.listeners.UploadListenerManager;
import com.arkhive.components.uploadmanager.uploaditem.ChunkData;
import com.arkhive.components.uploadmanager.uploaditem.FileData;
import com.arkhive.components.uploadmanager.uploaditem.UploadItem;
import com.arkhive.components.uploadmanager.uploaditem.UploadOptions;
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
 * @author
 */
public class ResumableProcess implements Runnable {
    private static final String TAG = ResumableProcess.class.getSimpleName();
    private final UploadItem uploadItem;
    private final MediaFire mediaFire;
    private final UploadListenerManager uploadManager;
    private final Logger logger = LoggerFactory.getLogger(ResumableProcess.class);

    /**
     * Constructor for an upload with a listener.
     *
     * @param mediaFire - the session to use for this upload process
     * @param uploadItem     - the item to be uploaded
     */
    public ResumableProcess(MediaFire mediaFire, UploadListenerManager uploadManager, UploadItem uploadItem) {
        this.mediaFire = mediaFire;
        this.uploadManager = uploadManager;
        this.uploadItem = uploadItem;
    }

    @Override
    public void run() {
        System.out.println(TAG + " sendRequest()");
        resumable();
    }

    /**
     * begin the upload process via the following steps:
     * 1. set the LinkedList of Chunk within the item we want to upload
     * 2. create chunks for all units of the item we want to upload
     * 3. send upload POST request for each chunk
     */
    private void resumable() {
        System.out.println(TAG + " resumable()");
        Thread.currentThread().setPriority(3); //uploads are set to low priority

        // get chunk. these will be used for chunks.
        ChunkData chunkData = uploadItem.getChunkData();
        int numChunks = chunkData.getNumberOfUnits();
        int unitSize = chunkData.getUnitSize();
        System.out.println(TAG + " number of chunks: " + numChunks);
        System.out.println(TAG + " size of units: " + unitSize);

        //get file size. this will be used for chunks.
        FileData fileData = uploadItem.getFileData();
        long fileSize = fileData.getFileSize();
        System.out.println(TAG + " size of file: " + fileSize);

        // get upload options. these will be passed as request parameters
        UploadOptions uploadOptions = uploadItem.getUploadOptions();
        String actionOnDuplicate = uploadOptions.getActionOnDuplicate();
        String versionControl = uploadOptions.getVersionControl();
        String uploadFolderKey = uploadOptions.getUploadFolderKey();
        System.out.println(TAG + " action on duplicate: " + actionOnDuplicate);
        System.out.println(TAG + " version control: " + versionControl);
        System.out.println(TAG + " upload folder key: " + uploadFolderKey);

        String encodedShortFileName;
        try {
            encodedShortFileName = URLEncoder.encode(uploadItem.getFileName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println(TAG + " Exception while encoding file name: " + e);
            notifyManagerException(e);
            return;
        }

        // loop through our chunks and create http post with header data and send after we are done looping,
        // let the listener know we are completed

        for (int chunkNumber = 0; chunkNumber < numChunks; chunkNumber++) {
            System.out.println(TAG + "    enter chunk upload loop()");
            // if the bitmap says this chunk number is uploaded then we can just skip it, if not, we upload it.
            if (uploadItem.getBitmap().isUploaded(chunkNumber)) {
                System.out.println(TAG + " chunk #" + chunkNumber + " already uploaded");
            } else {
                System.out.println(TAG + " chunk #" + chunkNumber + " not uploaded yet");
                // get the chunk size for this chunk
                int chunkSize = getChunkSize(chunkNumber, numChunks, fileSize, unitSize);

                // generate the chunk
                FileInputStream fis;
                BufferedInputStream bis;
                String chunkHash;
                byte[] uploadChunk;
                try {
                    fis = new FileInputStream(uploadItem.getFileData().getFilePath());
                    bis = new BufferedInputStream(fis);
                    uploadChunk = createUploadChunk(unitSize, chunkNumber, bis);
                    chunkHash = getSHA256(uploadChunk);

                    System.out.println(TAG + " chunk #" + chunkNumber + " hash: " + chunkHash);
                    System.out.println(TAG + " chunk #" + chunkNumber + " size: " + chunkSize);
                    System.out.println(TAG + " chunk #" + chunkNumber + " name: " + encodedShortFileName);

                    fis.close();
                    bis.close();
                } catch (FileNotFoundException e) {
                    this.exceptionHandler(e);
                    return;
                } catch (NoSuchAlgorithmException e) {
                    this.exceptionHandler(e);
                    return;
                } catch (IOException e) {
                    this.exceptionHandler(e);
                    return;
                }

                // generate the post headers
                HashMap<String, String> headers = generatePostHeaders(encodedShortFileName, fileSize, chunkNumber, chunkHash, chunkSize);

                // generate the get parameters
                HashMap<String, String> parameters = generateGetParameters(actionOnDuplicate, versionControl, uploadFolderKey);

                UploadResumableResponse response = mediaFire.apiCall().upload.resumableUpload(parameters, null, headers, uploadChunk);


                // set poll upload key if possible
                if (shouldSetPollUploadKey(response)) {
                    System.out.println(TAG + " have a poll upload key: " + response.getDoUpload().getPollUploadKey());
                    uploadItem.setPollUploadKey(response.getDoUpload().getPollUploadKey());
                }

                // if API response code OR Upload Response Result code have an error then we need to terminate the process
                if (response.hasError()) {
                    System.out.println(TAG + " response has an error # " + response.getError() + ": " + response.getMessage());
                    notifyManagerCancelled(response);
                    return;
                }

                if (response.getDoUpload().getResultCode() != ResumableResultCode.NO_ERROR) {
                    // let the listeners know we are done with this process (because there was an error in this case)
                    if (response.getDoUpload().getResultCode() != ResumableResultCode.SUCCESS_FILE_MOVED_TO_ROOT) {
                        // let the listeners know we are done with this process (because there was an error in this case)
                        System.out.println(TAG + " cancelling because result code: " + response.getDoUpload().getResultCode().toString());
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
        System.out.println(TAG + "exceptionHandler: " + e);
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
        System.out.println(TAG + " notifyListenersProgressUpdate()");
        if (uploadManager != null) {
            uploadManager.onProgressUpdate(uploadItem, chunkNumber, numChunks);
        }
    }

    /**
     * notifies the upload manager attached to the upload item that this process has finished.
     */
    private void notifyManagerCompleted() {
        System.out.println(TAG + " notifyManagerCompleted()");
        if (uploadManager != null) {
            uploadManager.onResumableCompleted(uploadItem);
        }
    }

    /**
     * lets listeners know that this process has been cancelled for this item. manager is informed of lost connection.
     */
    private void notifyManagerLostConnection() {
        System.out.println(TAG + " notifyManagerLostConnection()");
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
    private HashMap<String, String> generateGetParameters(String actionOnDuplicate, String versionControl, String uploadFolderKey) {
        System.out.println(TAG + " generateGetParameters()");
        String actionToken = mediaFire.apiCall().requestUploadActionToken();
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("session_token", actionToken);
        parameters.put("action_on_duplicate", actionOnDuplicate);
        parameters.put("response_format", "json");
        parameters.put("version_control", versionControl);
        if (!uploadItem.getUploadOptions().getUploadPath().isEmpty()) {
            parameters.put("path", uploadItem.getUploadOptions().getUploadPath());
        } else {
            parameters.put("folder_key", uploadItem.getUploadOptions().getUploadFolderKey());
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
     * @return A HashMap<String, String> containing the parameters to use with the HTTP POST request.
     */
    private HashMap<String, String> generatePostHeaders(String encodedShortFileName, long fileSize, int chunkNumber, String chunkHash, int chunkSize) {
        System.out.println(TAG + " generatePostHeaders()");
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
        System.out.println(TAG + " notifyManagerException()");
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
    private void notifyManagerCancelled(UploadResumableResponse response) {
        System.out.println(TAG + " notifyManagerCancelled()");
        if (uploadManager != null) {
            uploadManager.onCancelled(uploadItem, response);
        }
    }

    /**
     * only set the upload key for the upload item if response/doupload/result is 14 or 0.
     *
     * @param response The response from the resumable upload API request.
     * @return Flag indicating if the upload key should be set.
     */
    private boolean shouldSetPollUploadKey(UploadResumableResponse response) {
        System.out.println(TAG + " shouldSetPollUploadKey()");
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
     * @return The actual chunk size.
     */
    private int getChunkSize(int chunkNumber, int numChunks, long fileSize, int unitSize) {
        System.out.println(TAG + " getChunkSize()");
        int chunkSize;
        if (chunkNumber >= numChunks) {
            chunkSize = 0; // represents bad size
        } else {
            if (fileSize % unitSize == 0) { // all units will be of unitSize
                chunkSize = unitSize;
            } else if (chunkNumber < numChunks - 1) { // this unit is of unitSize
                chunkSize = unitSize;
            } else { // this unit is "special" and is the modulo of fileSize and unitSize;
                chunkSize = (int) (fileSize % unitSize);
            }
        }

        System.out.println(TAG + " RETURNING CHUNK SIZE OF: " + chunkSize);
        return chunkSize;
    }

    /**
     * creates an upload chunk array of bytes based on a position in a file.
     */
    private byte[] createUploadChunk(long unitSize, int chunkNumber, BufferedInputStream fileStream) throws IOException {
        System.out.println(TAG + " createUploadChunk()");
        byte[] readBytes = new byte[(int) unitSize];
        int offset = (int) (unitSize * chunkNumber);
        fileStream.skip(offset);
        int readSize = fileStream.read(readBytes, 0, (int) unitSize);
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
     * @return The SHA-256 hash of an upload chunk.
     */
    private String getSHA256(byte[] chunkData) throws NoSuchAlgorithmException, IOException {
        System.out.println(TAG + " getSHA256()");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        //test code
        InputStream in = new ByteArrayInputStream(chunkData, 0, chunkData.length);
        byte[] bytes = new byte[8192];
        int byteCount;
        while ((byteCount = in.read(bytes)) > 0) {
            md.update(bytes, 0, byteCount);
        }
        byte[] hashBytes = md.digest();
        //test code
        //byte[] hashBytes = md.digest(chunkData); //original code

        return convertHashBytesToString(hashBytes);
    }

    /**
     * Convert hash bytes to string.
     *
     * @param hashBytes
     * @return byte array converted to string.
     */
    private String convertHashBytesToString(byte[] hashBytes) {
        System.out.println(TAG + " convertHashBytesToString()");
        StringBuilder sb = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String tempString = Integer.toHexString((hashByte & 0xFF) | 0x100).substring(1, 3);
            sb.append(tempString);
        }
        String hash = sb.toString();
        System.out.println(TAG + " HASH FOR THIS CHUNK IS: " + hash);
        return hash;
    }

    /**
     * converts a String received from JSON format into a response String.
     *
     * @param response The response received in JSON format.
     * @return The response received which can then be parsed into a specific format as per Gson.fromJson().
     */
    private String getResponseString(String response) {
        System.out.println(TAG + " getResponseString()");
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
