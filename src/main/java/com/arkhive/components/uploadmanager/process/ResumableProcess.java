package com.arkhive.components.uploadmanager.process;

import com.arkhive.components.core.MediaFire;
import com.arkhive.components.core.module_api.codes.ResumableResultCode;
import com.arkhive.components.core.module_api.responses.UploadResumableResponse;
import com.arkhive.components.uploadmanager.interfaces.UploadListenerManager;
import com.arkhive.components.uploadmanager.uploaditem.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

/**
 * Runnable for making api call to upload/resumable.php. *
 *
 * @author
 */
public class ResumableProcess extends UploadProcess {
    private final Logger logger = LoggerFactory.getLogger(ResumableProcess.class);

    /**
     * Constructor for an upload with a listener.
     *
     * @param mediaFire - the session to use for this upload process
     * @param uploadItem     - the item to be uploaded
     */
    public ResumableProcess(MediaFire mediaFire, UploadListenerManager uploadListenerManager, UploadItem uploadItem) {
        super(mediaFire, uploadItem, uploadListenerManager);
    }

    @Override
    protected void doUploadProcess() {
        logger.info(" doUploadProcess()");
        Thread.currentThread().setPriority(3); //uploads are set to low priority

        //get file size. this will be used for chunks.
        FileData fileData = uploadItem.getFileData();
        long fileSize = fileData.getFileSize();

        String encodedShortFileName;
        try {
            encodedShortFileName = URLEncoder.encode(uploadItem.getFileName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.info(" Exception while encoding file name: " + e);
            notifyListenerException(e);
            return;
        }

        // get chunk. these will be used for chunks.
        ChunkData chunkData = uploadItem.getChunkData();
        int numChunks = chunkData.getNumberOfUnits();
        int unitSize = chunkData.getUnitSize();

        // loop through our chunks and create http post with header data and send after we are done looping,
        // let the listener know we are completed
        UploadResumableResponse response = null;

        for (int chunkNumber = 0; chunkNumber < numChunks; chunkNumber++) {
            if (uploadItem.isCancelled()) {
                notifyListenerCancelled(response);
                return;
            }

            // if the bitmap says this chunk number is uploaded then we can just skip it, if not, we upload it.
            if (!uploadItem.getBitmap().isUploaded(chunkNumber)) {
                // get the chunk size for this chunk
                int chunkSize = getChunkSize(chunkNumber, numChunks, fileSize, unitSize);

                ResumableChunkInfo resumableChunkInfo = createResumableChunkInfo(unitSize, chunkNumber);
                if (resumableChunkInfo == null || resumableChunkInfo.hasException()) {
                    notifyListenerException(resumableChunkInfo.getException());
                    return;
                }

                String chunkHash = resumableChunkInfo.getChunkHash();
                byte[] uploadChunk = resumableChunkInfo.getUploadChunk();

                printDebugCurrentChunk(chunkNumber, numChunks, chunkSize, unitSize, fileSize, chunkHash, uploadChunk);

                // generate the post headers
                HashMap<String, String> headers = generatePostHeaders(encodedShortFileName, fileSize, chunkNumber, chunkHash, chunkSize);
                // generate the get parameters
                HashMap<String, String> parameters = generateGetParameters();

                printDebugRequestData(headers, parameters);

                response = mediaFire.apiCall().upload.resumableUpload(parameters, null, headers, uploadChunk);

                // set poll upload key if possible
                if (shouldSetPollUploadKey(response)) {
                    uploadItem.setPollUploadKey(response.getDoUpload().getPollUploadKey());
                }

                if (shouldCancelUpload(response)) {
                    notifyListenerCancelled(response);
                    return;
                }
            }

            updateProgressForListener(numChunks);

            // update the response bitmap
            int count = response.getResumableUpload().getBitmap().getCount();
            List<Integer> words = response.getResumableUpload().getBitmap().getWords();
            ResumableBitmap bitmap = new ResumableBitmap(count, words);
            uploadItem.setBitmap(bitmap);
            logger.info("(" + uploadItem.getFileData().getFilePath() + ") upload item bitmap: " + uploadItem.getBitmap().getCount() + " count, (" + uploadItem.getBitmap().getWords().toString() + ") words.");
        } // end loop

        // let the listeners know that upload has attempted to upload all chunks.
        notifyListenerCompleted(response);
    }

    private void printDebugRequestData(HashMap<String, String> headers, HashMap<String, String> parameters) {
        logger.info("headers: " + headers.toString());
        logger.info("parameters: " + parameters.toString());
    }

    private void printDebugCurrentChunk(int chunkNumber, int numChunks, int chunkSize, int unitSize, long fileSize, String chunkHash, byte[] uploadChunk) {
        logger.info("current chunk: " + chunkNumber);
        logger.info("total chunks: " + numChunks);
        logger.info("current chunk size: " + chunkSize);
        logger.info("normal chunk size: " + unitSize);
        logger.info("total file size: " + fileSize);
        logger.info("current chunk hash: " + chunkHash);
        StringBuilder sb = new StringBuilder();
        for (byte b : uploadChunk) {
            sb.append(b);
        }
        logger.info("current upload chunk bytes: " + sb.toString());
    }

    private void updateProgressForListener(int totalChunks) {
        logger.info("updateProgressForListener()");
        // give number of chunks/numChunks for onProgressUpdate
        int numUploaded = 0;
        for (int i = 0; i < totalChunks; i++) {
            if (uploadItem.getBitmap().isUploaded(i)) {
                numUploaded++;
            }
        }
        notifyListenerOnProgressUpdate(numUploaded, totalChunks);
    }

    public boolean shouldCancelUpload(UploadResumableResponse response) {
        logger.info("shouldCancelUpload()");
        // if API response code OR Upload Response Result code have an error then we need to terminate the process
        if (response.hasError()) {
            logger.info(" response has an error # " + response.getError() + ": " + response.getMessage());
            notifyListenerCancelled(response);
            return true;
        }

        if (response.getDoUpload().getResultCode() != ResumableResultCode.NO_ERROR) {
            // let the listeners know we are done with this process (because there was an error in this case)
            if (response.getDoUpload().getResultCode() != ResumableResultCode.SUCCESS_FILE_MOVED_TO_ROOT) {
                // let the listeners know we are done with this process (because there was an error in this case)
                logger.info(" cancelling because result code: " + response.getDoUpload().getResultCode().toString());
                notifyListenerCancelled(response);
                return true;
            }
        }

        return false;
    }

    public ResumableChunkInfo createResumableChunkInfo(int unitSize, int chunkNumber) {
        logger.info("createResumableChunkInfo");
        ResumableChunkInfo resumableChunkInfo;
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
            resumableChunkInfo = new ResumableChunkInfo(chunkHash, uploadChunk);
            fis.close();
            bis.close();
        } catch (FileNotFoundException e) {
            return new ResumableChunkInfo(e);
        } catch (NoSuchAlgorithmException e) {
            return new ResumableChunkInfo(e);
        } catch (IOException e) {
            return new ResumableChunkInfo(e);
        }
        return resumableChunkInfo;
    }

    /**
     * generates a HashMap of the GET parameters.
     *
     * @return The parameters to use for the upload API request.
     */
    private HashMap<String, String> generateGetParameters() {
        logger.info(" generateGetParameters()");
        // get upload options. these will be passed as request parameters
        UploadOptions uploadOptions = uploadItem.getUploadOptions();
        String actionOnDuplicate = uploadOptions.getActionOnDuplicate();
        String versionControl = uploadOptions.getVersionControl();
        String uploadFolderKey = uploadOptions.getUploadFolderKey();
        String uploadPath = uploadOptions.getUploadPath();
        logger.info(" action on duplicate: " + actionOnDuplicate);
        logger.info(" version control: " + versionControl);
        logger.info(" upload folder key: " + uploadFolderKey);

        String actionToken = mediaFire.apiCall().requestUploadActionToken();
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("session_token", actionToken);
        parameters.put("action_on_duplicate", actionOnDuplicate);
        parameters.put("response_format", "json");
        parameters.put("version_control", versionControl);
        if (!uploadPath.isEmpty()) {
            parameters.put("path", uploadPath);
        } else {
            parameters.put("folder_key", uploadFolderKey);
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
        logger.info(" generatePostHeaders()");
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
     * only set the upload key for the upload item if response/doupload/result is 14 or 0.
     *
     * @param response The response from the resumable upload API request.
     * @return Flag indicating if the upload key should be set.
     */
    private boolean shouldSetPollUploadKey(UploadResumableResponse response) {
        logger.info(" shouldSetPollUploadKey()");
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
        logger.info(" getChunkSize()");
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

        return chunkSize;
    }

    /**
     * creates an upload chunk array of bytes based on a position in a file.
     */
    private byte[] createUploadChunk(long unitSize, int chunkNumber, BufferedInputStream fileStream) throws IOException {
        logger.info(" createUploadChunk()");
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
        logger.info(" getSHA256()");
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
        logger.info(" convertHashBytesToString()");
        StringBuilder sb = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String tempString = Integer.toHexString((hashByte & 0xFF) | 0x100).substring(1, 3);
            sb.append(tempString);
        }
        String hash = sb.toString();
        return hash;
    }
}
