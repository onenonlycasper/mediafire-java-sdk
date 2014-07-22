package com.mediafire.uploader.uploaditem;

import com.mediafire.sdk.config.MFConfiguration;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * file information for an upload item.
 */
public class MFFileData {
    private static final String TAG = MFFileData.class.getCanonicalName();
    private final String filePath;
    private long fileSize;
    private String fileHash;

    public MFFileData(String filePath) {
        MFConfiguration.getStaticMFLogger().v(TAG, "MFFileData object created");
        if (filePath == null) {
            throw new IllegalArgumentException("invalid filePath (cannot be null)");
        }
        this.filePath = filePath;
        setFileSize();
        setFileHash();
    }

    public String getFilePath() {
        return filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileSize() {
        File file = new File(getFilePath());
        fileSize = file.length();
    }

    public void setFileHash() {
        File file = new File(filePath);
        FileInputStream fileInputStream;
        BufferedInputStream fileUri;
        BufferedInputStream in;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            MFConfiguration.getStaticMFLogger().v(TAG, "Exception: " + e);
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
            MFConfiguration.getStaticMFLogger().v(TAG, "Exception: " + e);
            fileHash = "";
        } catch (IOException e) {
            MFConfiguration.getStaticMFLogger().v(TAG, "Exception: " + e);
            fileHash = "";
        } finally {
            fileInputStream = null;
            fileUri = null;
            in = null;
        }
        MFConfiguration.getStaticMFLogger().v(TAG, "FILE HASH IS SET TO: " + fileHash);
    }
}
