package com.mediafire.uploader.process;

import com.mediafire.sdk.api_responses.upload.ResumableResponse;
import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.http.*;
import com.mediafire.sdk.token.MFTokenFarm;
import com.mediafire.uploader.manager.UploadManagerWorker;
import com.mediafire.uploader.uploaditem.*;

import java.io.*;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

public class ResumableProcess extends UploadProcess {

    private static final String TAG = ResumableProcess.class.getCanonicalName();

    public ResumableProcess(MFTokenFarm mfTokenFarm, UploadManagerWorker uploadManagerWorker, UploadItem uploadItem) {
        super(mfTokenFarm, uploadItem, uploadManagerWorker);
    }

    @Override
    protected void doUploadProcess() {
        MFConfiguration.getStaticMFLogger().v(TAG, "doUploadProcess()");

        //get file size. this will be used for chunks.
        FileData fileData = uploadItem.getFileData();
        long fileSize = fileData.getFileSize();

        String encodedShortFileName;
        try {
            encodedShortFileName = URLEncoder.encode(uploadItem.getFileName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            MFConfiguration.getStaticMFLogger().v(TAG, "Exception while encoding file name: " + e);
            notifyListenerException(e);
            return;
        }

        // get chunk. these will be used for chunks.
        ChunkData chunkData = uploadItem.getChunkData();
        int numChunks = chunkData.getNumberOfUnits();
        int unitSize = chunkData.getUnitSize();

        // loop through our chunks and create http post with header data and send after we are done looping,
        // let the listener know we are completed
        ResumableResponse response = null;
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

                MFRequest.MFRequestBuilder mfRequestBuilder = new MFRequest.MFRequestBuilder(MFHost.LIVE_HTTP, MFApi.UPLOAD_RESUMABLE);
                mfRequestBuilder.requestParameters(parameters);
                mfRequestBuilder.headers(headers);
                mfRequestBuilder.payload(uploadChunk);
                MFRequest mfRequest = mfRequestBuilder.build();

                MFResponse mfResponse = mfTokenFarm.getMFHttpRunner().doRequest(mfRequest);
                response = mfResponse.getResponseObject(ResumableResponse.class);

                // set poll upload key if possible
                if (shouldSetPollUploadKey(response)) {
                    uploadItem.setPollUploadKey(response.getDoUpload().getPollUploadKey());
                }

                if (shouldCancelUpload(response)) {
                    notifyListenerCancelled(response);
                    return;
                }

                // update the response bitmap
                int count = response.getResumableUpload().getBitmap().getCount();
                List<Integer> words = response.getResumableUpload().getBitmap().getWords();
                ResumableBitmap bitmap = new ResumableBitmap(count, words);
                uploadItem.setBitmap(bitmap);
                MFConfiguration.getStaticMFLogger().v(TAG, "(" + uploadItem.getFileData().getFilePath() + ") upload item bitmap: " + uploadItem.getBitmap().getCount() + " count, (" + uploadItem.getBitmap().getWords().toString() + ") words.");

                clearReferences(chunkSize, chunkHash, uploadChunk, headers, parameters);
            }
            updateProgressForListener(numChunks);

        } // end loop

        // let the listeners know that upload has attempted to upload all chunks.
        notifyListenerCompleted(response);
    }

    private void clearReferences(int chunkSize, String chunkHash, byte[] uploadChunk, HashMap<String, String> headers, HashMap<String, String> parameters) {
        chunkSize = 0;
        chunkHash = null;
        uploadChunk = null;
        headers = null;
        parameters = null;
    }

    private void printDebugRequestData(HashMap<String, String> headers, HashMap<String, String> parameters) {
        MFConfiguration.getStaticMFLogger().v(TAG, "headers: " + headers.toString());
        MFConfiguration.getStaticMFLogger().v(TAG, "parameters: " + parameters.toString());
    }

    private void printDebugCurrentChunk(int chunkNumber, int numChunks, int chunkSize, int unitSize, long fileSize, String chunkHash, byte[] uploadChunk) {
        MFConfiguration.getStaticMFLogger().v(TAG, "current thread: " + Thread.currentThread().getName());
        MFConfiguration.getStaticMFLogger().v(TAG, "current chunk: " + chunkNumber);
        MFConfiguration.getStaticMFLogger().v(TAG, "total chunks: " + numChunks);
        MFConfiguration.getStaticMFLogger().v(TAG, "current chunk size: " + chunkSize);
        MFConfiguration.getStaticMFLogger().v(TAG, "normal chunk size: " + unitSize);
        MFConfiguration.getStaticMFLogger().v(TAG, "total file size: " + fileSize);
        MFConfiguration.getStaticMFLogger().v(TAG, "current chunk hash: " + chunkHash);
        MFConfiguration.getStaticMFLogger().v(TAG, "upload chunk ");
    }

    private void updateProgressForListener(int totalChunks) {
        MFConfiguration.getStaticMFLogger().v(TAG, "updateProgressForListener()");
        // give number of chunks/numChunks for onProgressUpdate
        int numUploaded = 0;
        for (int i = 0; i < totalChunks; i++) {
            if (uploadItem.getBitmap().isUploaded(i)) {
                numUploaded++;
            }
        }
        MFConfiguration.getStaticMFLogger().v(TAG, numUploaded + "/" + totalChunks + " chunks uploaded");
        notifyListenerOnProgressUpdate(numUploaded, totalChunks);
    }

    public boolean shouldCancelUpload(ResumableResponse response) {
        MFConfiguration.getStaticMFLogger().v(TAG, "shouldCancelUpload()");
        // if API response code OR Upload Response Result code have an error then we need to terminate the process
        if (response.hasError()) {
            notifyListenerCancelled(response);
            return true;
        }

        if (response.getDoUpload().getResultCode() != ResumableResponse.Result.NO_ERROR) {
            // let the listeners know we are done with this process (because there was an error in this case)
            if (response.getDoUpload().getResultCode() != ResumableResponse.Result.SUCCESS_FILE_MOVED_TO_ROOT) {
                // let the listeners know we are done with this process (because there was an error in this case)
                notifyListenerCancelled(response);
                return true;
            }
        }

        return false;
    }

    public ResumableChunkInfo createResumableChunkInfo(int unitSize, int chunkNumber) {
        MFConfiguration.getStaticMFLogger().v(TAG, "createResumableChunkInfo");
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
        MFConfiguration.getStaticMFLogger().v(TAG, "generateGetParameters()");
        // get upload options. these will be passed as request parameters
        UploadOptions uploadOptions = uploadItem.getUploadOptions();
        String actionOnDuplicate = uploadOptions.getActionOnDuplicate();
        String versionControl = uploadOptions.getVersionControl();
        String uploadFolderKey = uploadOptions.getUploadFolderKey();
        String uploadPath = uploadOptions.getUploadPath();

        String actionToken = mfTokenFarm.borrowMFUploadActionToken().getTokenString();
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

    private HashMap<String, String> generatePostHeaders(String encodedShortFileName, long fileSize, int chunkNumber, String chunkHash, int chunkSize) {
        MFConfiguration.getStaticMFLogger().v(TAG, "generatePostHeaders()");
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

    private boolean shouldSetPollUploadKey(ResumableResponse response) {
        switch (response.getDoUpload().getResultCode()) {
            case NO_ERROR:
            case SUCCESS_FILE_MOVED_TO_ROOT:
                return true;
            default:
                return false;
        }
    }

    private int getChunkSize(int chunkNumber, int numChunks, long fileSize, int unitSize) {
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

    private byte[] createUploadChunk(long unitSize, int chunkNumber, BufferedInputStream fileStream) throws IOException {
        MFConfiguration.getStaticMFLogger().v(TAG, "createUploadChunk()");
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

    private String getSHA256(byte[] chunkData) throws NoSuchAlgorithmException, IOException {
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

    private String convertHashBytesToString(byte[] hashBytes) {
        MFConfiguration.getStaticMFLogger().v(TAG, "convertHashBytesToString()");
        StringBuilder sb = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String tempString = Integer.toHexString((hashByte & 0xFF) | 0x100).substring(1, 3);
            sb.append(tempString);
        }

        return sb.toString();
    }
}
