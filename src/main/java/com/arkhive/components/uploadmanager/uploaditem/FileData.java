package com.arkhive.components.uploadmanager.uploaditem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * file information for an upload item.
 * @author Chris Najar
 *
 */
public class FileData {
    private static final String TAG = FileData.class.getSimpleName();
    private String filePath;
    private long fileSize;
    private String fileHash;
    private final Logger logger = LoggerFactory.getLogger(FileData.class);

    public FileData(String filePath) {
        logger.info(TAG + "FileData object created");
        if (filePath == null) {
            throw new IllegalArgumentException("invalid filePath (cannot be null)");
        }
        this.filePath = filePath;
        setFileSize();
        setFileHash();
    }

    /*============================
     * public getters
     *============================*/
    /**
     * Gets the filename.
     * @return
     */
    public String getFilePath() {
        logger.info(TAG + "getFilePath()");
        return filePath;
    }

    /**
     * gets the file size.
     * @return
     */
    public long getFileSize() {
        logger.info(TAG + "getFilePath()");
        return fileSize;
    }

    /**
     * gets the file hash.
     * @return
     */
    public String getFileHash() {
        logger.info(TAG + "getFilePath()");
        return fileHash;
    }

    /*============================
     * private setters
     *============================*/
    private void setFileSize() {
        logger.info(TAG + "setFileSize()");
        File file = new File(getFilePath());
        FileInputStream fileInputStream;
        FileChannel channel;
        try {
            fileInputStream = new FileInputStream(file);
            channel = fileInputStream.getChannel();
            fileSize = channel.size();

            fileInputStream.close();
            channel.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            logger.error(TAG + "Exception: " + e);
            fileSize = 0;
            filePath = "";
        } catch (IOException e) {
            logger.error(TAG + "Exception: " + e);
            fileSize = 0;
        } finally {
            channel = null;
            fileInputStream = null;
        }
        logger.info(TAG + "--file size set to: " + this.fileSize);
    }

    private void setFileHash() {
        logger.info(TAG + "setFileHash()");
        File file = new File(filePath);
        FileInputStream fileInputStream;
        BufferedInputStream fileUri;
        BufferedInputStream in;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.error(TAG + "Exception: " + e);
            this.fileHash = "";
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
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < digestBytes.length; ++i) {
                String tempString = Integer.toHexString((digestBytes[i] & 0xFF) | 0x100).substring(1, 3);
                sb.append(tempString);
            }

            this.fileHash = sb.toString();
            fileInputStream.close();
            fileUri.close();
            in.close();
        } catch (NoSuchAlgorithmException e) {
            logger.error(TAG + "Exception: " + e);
            this.fileHash = "";
        } catch (IOException e) {
            logger.error(TAG + "Exception: " + e);
            this.fileHash = "";
        } finally {
            fileInputStream = null;
            fileUri = null;
            in = null;
        }

        logger.info(TAG + "--file hash set to: " + fileHash);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (this.getClass() != object.getClass()) {
            return false;
        }

        if (!(object instanceof FileData)) {
            return false;
        }

        if (!filePath.equals(((FileData) object).filePath)) {
            return false;
        }

        if (fileSize != ((FileData) object).fileSize) {
            return false;
        }

        if (!fileHash.equals(((FileData) object).fileHash)) {
            return false;
        }

        return true;
    }
}
