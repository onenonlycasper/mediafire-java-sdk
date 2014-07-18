package com.mediafire.uploader.uploaditem;

import com.mediafire.sdk.config.MFConfiguration;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

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


    public UploadItem(String path, UploadOptions uploadOptions) {
        MFConfiguration.getStaticMFLogger().v(TAG, "UploadItem created");
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
        MFConfiguration.getStaticMFLogger().v(TAG, "getUploadAttemptCount(" + uploadAttemptCount + ")");
        uploadAttemptCount++;
        return uploadAttemptCount;
    }

    public String getFileName() {
        MFConfiguration.getStaticMFLogger().v(TAG, "getFileName()");
        if (!options.getCustomFileName().isEmpty()) {
            fileName = options.getCustomFileName();
        }
        return fileName;
    }

    public FileData getFileData() {
        MFConfiguration.getStaticMFLogger().v(TAG, "getFileData()");
        return fileData;
    }

    public String getPollUploadKey() {
        MFConfiguration.getStaticMFLogger().v(TAG, "getPollUploadKey()");
        return pollUploadKey;
    }

    public UploadOptions getUploadOptions() {
        MFConfiguration.getStaticMFLogger().v(TAG, "getUploadOptions()");
        if (options == null) {
            options = new UploadOptions();
        }
        return options;
    }

    public ChunkData getChunkData() {
        MFConfiguration.getStaticMFLogger().v(TAG, "getChunkData()");
        if (chunkData == null) {
            chunkData = new ChunkData();
        }
        return chunkData;
    }

    public ResumableBitmap getBitmap() {
        MFConfiguration.getStaticMFLogger().v(TAG, "getBitmap()");
        if (bitmap == null) {
            MFConfiguration.getStaticMFLogger().v(TAG, "  resumable bitmap reference lost");
            bitmap = new ResumableBitmap(0, new ArrayList<Integer>());
        }
        return bitmap;
    }

    public void setBitmap(ResumableBitmap bitmap) {
        MFConfiguration.getStaticMFLogger().v(TAG, "setBitmap()");
        this.bitmap = bitmap;
    }

    public void setPollUploadKey(String pollUploadKey) {
        MFConfiguration.getStaticMFLogger().v(TAG, "setPollUploadKey()");
        this.pollUploadKey = pollUploadKey;
    }

    private void setFileName(String path) {
        MFConfiguration.getStaticMFLogger().v(TAG, "setFileName()");
        String[] splitName = path.split("/");
        //just throwing the UnsupportedEncodingException exception to whoever creates the upload item
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
