package com.arkhive.components.uploadmanager.uploaditem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * file information for an upload item.
 */
public class FileData {
    private static final String TAG = FileData.class.getSimpleName();
    private final String filePath;
    private long fileSize;
    private String fileHash;
    private final Logger logger = LoggerFactory.getLogger(FileData.class);

    public FileData(String filePath) {
        logger.info("FileData object created");
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
        logger.info("getFilePath()");
        return filePath;
    }

    /**
     * gets the file size.
     *
     * @return
     */
    public long getFileSize() {
        logger.info("getFilePath()");
        return fileSize;
    }

    /**
     * gets the file hash.
     *
     * @return
     */
    public String getFileHash() {
        logger.info("getFilePath()");
        return fileHash;
    }

    public void setFileSize() {
        logger.info(" setFileSize()");
        File file = new File(getFilePath());
        fileSize = file.length();

        logger.info("file size set to " + fileSize);
    }

    public void setFileHash() {
        logger.info(" setFileHash()");
        File file = new File(filePath);
        FileInputStream fileInputStream;
        BufferedInputStream fileUri;
        BufferedInputStream in;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.info("Exception: " + e);
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
            logger.info("Exception: " + e);
            fileHash = "";
        } catch (IOException e) {
            logger.info("Exception: " + e);
            fileHash = "";
        } finally {
            fileInputStream = null;
            fileUri = null;
            in = null;
        }
        logger.info(" FILE HASH IS SET TO: " + fileHash);
    }
}
