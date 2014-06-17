package com.arkhive.components.uploadmanager.uploaditem;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This data structure represents an item to be uploaded.
 * The only mandatory parameter that needs to be passed to
 * the constructor is a path, but the implementer of this
 * data structure can also pass specific upload options.
 *
 * @author Chris Najar
 */
public class UploadItem {
    private static final String TAG = UploadItem.class.getSimpleName();
    private int count;
    private String fileName;
    private UploadOptions options;
    private final FileData fileData;
    private ChunkData chunkData;
    private ResumableBitmap bitmap;
    private String pollUploadKey;
    private final Logger logger = LoggerFactory.getLogger(UploadItem.class);

    /**
     * Constructor which takes a path and upload attempts.
     * Use this method when you want to customize the upload options for this UploadItem.
     *
     * @param path          - file path on the device
     * @param uploadOptions - upload options to use for the upload item.
     *                      Should use the single or dual argument constructor for the most part.
     */
    public UploadItem(String path, UploadOptions uploadOptions) {
        System.out.println(TAG + " UploadItem created");
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
        count = 0;
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

    public int getCheckCount() {
        System.out.println(TAG + " getCheckCount()");
        count++;
        return count;
    }


    /**
     * Called to get the Short file name.
     *
     * @return the filename.
     */
    public String getFileName() {
        System.out.println(TAG + " getFileName()");
        if (!options.getCustomFileName().isEmpty()) {
            fileName = options.getCustomFileName();
        }
        return fileName;
    }

    /**
     * CAlled to get the UploadItemFileData.
     *
     * @return the file data struct.
     */
    public FileData getFileData() {
        System.out.println(TAG + " getFileData()");
        return fileData;
    }

    /**
     * Called to get the poll upload key.
     *
     * @return - the poll upload key.
     */
    public String getPollUploadKey() {
        System.out.println(TAG + " getPollUploadKey()");
        return pollUploadKey;
    }

    /**
     * Called to get the UploadItemFileUploadOptions.
     *
     * @return - the upload options struct.
     */
    public UploadOptions getUploadOptions() {
        System.out.println(TAG + " getUploadOptions()");
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
        System.out.println(TAG + " getChunkData()");
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
        System.out.println(TAG + " getBitmap()");
        if (bitmap == null) {
            System.out.println(TAG + "   resumable bitmap reference lost");
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
        System.out.println(TAG + " setBitmap()");
        this.bitmap = bitmap;
    }

    /**
     * Sets the poll upload key.
     *
     * @param pollUploadKey - the polluploadkey to set.
     */
    public void setPollUploadKey(String pollUploadKey) {
        System.out.println(TAG + " setPollUploadKey()");
        this.pollUploadKey = pollUploadKey;
    }

    /**
     * sets the short file name given the path.
     *
     * @param path path of the file.
     */
    private void setFileName(String path) {
        System.out.println(TAG + " setFileName()");
        String[] splitName = path.split("/");
        //just throwing the unsupportedcoding exception to whoever creates the upload item
        try {
            this.fileName = URLEncoder.encode(splitName[splitName.length - 1], "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported");
        }
    }
}
