package com.mediafire.uploader.uploaditem;

import com.mediafire.sdk.config.MFConfiguration;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * This data structure represents an item to be uploaded.
 * The only mandatory parameter that needs to be passed to
 * the constructor is a path, but the implementer of this
 * data structure can also pass specific upload options.
 *
 * @author
 */
public class UploadItem {
    private static final String TAG = UploadItem.class.getCanonicalName();
    private int uploadAttemptCount;
    private boolean cancelled;
    private String fileName;
    private UploadOptions options;
    private final FileData fileData;
    private ChunkData chunkData;
    private ResumableBitmap bitmap;
    private String pollUploadKey;

    /**
     * Constructor which takes a path and upload attempts.
     * Use this method when you want to customize the upload options for this UploadItem.
     *
     * @param path          - file path on the device
     * @param uploadOptions - upload options to use for the upload item.
     *                      Should use the single or dual argument constructor for the most part.
     */
    public UploadItem(String path, UploadOptions uploadOptions) {
        MFConfiguration.getErrorTracker().i(TAG, "UploadItem created");
        if (path == null) {
            throw new IllegalArgumentException("path must not be null");
        }

        if (uploadOptions == null) {
            options = new UploadOptions();
        } else {
            this.options = uploadOptions;
        }

        if (options.getCustomFileName().isEmpty()) {
            setFileName(path);
        } else {
            fileName = options.getCustomFileName();
        }

        //set Object fields so they won't be null
        fileData = new FileData(path);
        pollUploadKey = "";
        chunkData = new ChunkData();
        bitmap = new ResumableBitmap(0, new ArrayList<Integer>());
        uploadAttemptCount = 0;
    }

    /**
     * Constructor which takes a path and an image id
     * Use this method when you want to use default upload options for this UploadItem.
     *
     * @param path - path to data
     */
    public UploadItem(String path) {
        this(path, null);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancelUpload() {
        cancelled = true;
    }

    public int getUploadAttemptCount() {
        MFConfiguration.getErrorTracker().i(TAG, "getUploadAttemptCount(" + uploadAttemptCount + ")");
        uploadAttemptCount++;
        return uploadAttemptCount;
    }

    /**
     * Called to get the Short file name.
     *
     * @return the filename.
     */
    public String getFileName() {
        MFConfiguration.getErrorTracker().i(TAG, "getFileName()");
        if (!options.getCustomFileName().isEmpty()) {
            fileName = options.getCustomFileName();
        }
        return fileName;
    }

    /**
     * Called to get the UploadItemFileData.
     *
     * @return the file data struct.
     */
    public FileData getFileData() {
        MFConfiguration.getErrorTracker().i(TAG, "getFileData()");
        return fileData;
    }

    /**
     * Called to get the poll upload key.
     *
     * @return - the poll upload key.
     */
    public String getPollUploadKey() {
        MFConfiguration.getErrorTracker().i(TAG, "getPollUploadKey()");
        return pollUploadKey;
    }

    /**
     * Called to get the UploadItemFileUploadOptions.
     *
     * @return - the upload options struct.
     */
    public UploadOptions getUploadOptions() {
        MFConfiguration.getErrorTracker().i(TAG, "getUploadOptions()");
        if (options == null) {
            options = new UploadOptions();
        }
        return options;
    }

    /**
     * Called to get the UploadItemChunkData.
     *
     * @return - the chunkdata struct.
     */
    public ChunkData getChunkData() {
        MFConfiguration.getErrorTracker().i(TAG, "getChunkData()");
        if (chunkData == null) {
            chunkData = new ChunkData();
        }
        return chunkData;
    }

    /**
     * Called to get the ResumableUploadBitmap.
     *
     * @return - the resumablebitmap struct.
     */
    public ResumableBitmap getBitmap() {
        MFConfiguration.getErrorTracker().i(TAG, "getBitmap()");
        if (bitmap == null) {
            MFConfiguration.getErrorTracker().i(TAG, "  resumable bitmap reference lost");
            bitmap = new ResumableBitmap(0, new ArrayList<Integer>());
        }
        return bitmap;
    }

    /**
     * Sets the ResumableUploadBitmap.
     *
     * @param bitmap - the resumablebitmap to set.
     */
    public void setBitmap(ResumableBitmap bitmap) {
        MFConfiguration.getErrorTracker().i(TAG, "setBitmap()");
        this.bitmap = bitmap;
    }

    /**
     * Sets the poll upload key.
     *
     * @param pollUploadKey - the polluploadkey to set.
     */
    public void setPollUploadKey(String pollUploadKey) {
        MFConfiguration.getErrorTracker().i(TAG, "setPollUploadKey()");
        this.pollUploadKey = pollUploadKey;
    }

    /**
     * sets the short file name given the path.
     *
     * @param path path of the file.
     */
    private void setFileName(String path) {
        MFConfiguration.getErrorTracker().i(TAG, "setFileName()");
        String[] splitName = path.split("/");
        //just throwing the unsupportedcoding exception to whoever creates the upload item
        try {
            this.fileName = URLEncoder.encode(splitName[splitName.length - 1], "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported");
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (this.getClass() != object.getClass()) {
            return false;
        }

        if (!fileData.getFilePath().equals(((UploadItem) object).fileData.getFilePath())) {
            return false;
        }

        if (fileData.getFileHash() != null && ((UploadItem) object).fileData.getFileHash() != null) {
            if (!fileData.getFileHash().equals(((UploadItem) object).fileData.getFileHash())) {
                return false;
            }
        }

        return true;
    }
}
