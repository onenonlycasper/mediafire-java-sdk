package com.mediafire.uploader.uploaditem;

import com.mediafire.sdk.config.MFConfiguration;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * file information for an upload item.
 */
public class FileData {
    private static final String TAG = FileData.class.getCanonicalName();
    private final String filePath;
    private long fileSize;
    private String fileHash;

    public FileData(String filePath) {
        MFConfiguration.getStaticMFLogger().logMessage(TAG, "FileData object created");
        if (filePath == null) {
            throw new IllegalArgumentException("invalid filePath (cannot be null)");
        }
        this.filePath = filePath;
        setFileSize();
        setFileHash();
    }

    /**
     * Gets the filename.
     *
     * @return
     */
    public String getFilePath() {
        MFConfiguration.getStaticMFLogger().logMessage(TAG, "getFilePath()");
        return filePath;
    }

    /**
     * gets the file size.
     *
     * @return
     */
    public long getFileSize() {
        MFConfiguration.getStaticMFLogger().logMessage(TAG, "getFilePath()");
        return fileSize;
    }

    /**
     * gets the file hash.
     *
     * @return
     */
    public String getFileHash() {
        MFConfiguration.getStaticMFLogger().logMessage(TAG, "getFilePath()");
        return fileHash;
    }

    public void setFileSize() {
        MFConfiguration.getStaticMFLogger().logMessage(TAG, "setFileSize()");
        File file = new File(getFilePath());
        fileSize = file.length();

        MFConfiguration.getStaticMFLogger().logMessage(TAG, "file size set to " + fileSize);
    }

    public void setFileHash() {
        MFConfiguration.getStaticMFLogger().logMessage(TAG, "setFileHash()");
        File file = new File(filePath);
        FileInputStream fileInputStream;
        BufferedInputStream fileUri;
        BufferedInputStream in;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            MFConfiguration.getStaticMFLogger().logMessage(TAG, "Exception: " + e);
            fileHash = "";
            return;
        }

        fileUri = new BufferedInputStream(fileInputStream);

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = new byte[8192];
            in = new BufferedInputStream(fileUri);
            int byteCount;
            while ((byteCount = in.read(bytes)) > 0) {
                digest.update(bytes, 0, byteCount);
            }

            byte[] digestBytes = digest.digest();
            StringBuilder sb = new StringBuilder();

            for (byte digestByte : digestBytes) {
                String tempString = Integer.toHexString((digestByte & 0xFF) | 0x100).substring(1, 3);
                sb.append(tempString);
            }

            fileHash = sb.toString();
            fileInputStream.close();
            fileUri.close();
            in.close();
        } catch (NoSuchAlgorithmException e) {
            MFConfiguration.getStaticMFLogger().logMessage(TAG, "Exception: " + e);
            fileHash = "";
        } catch (IOException e) {
            MFConfiguration.getStaticMFLogger().logMessage(TAG, "Exception: " + e);
            fileHash = "";
        } finally {
            fileInputStream = null;
            fileUri = null;
            in = null;
        }
        MFConfiguration.getStaticMFLogger().logMessage(TAG, "FILE HASH IS SET TO: " + fileHash);
    }
}
