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
 * @author Chris Najar
 *
 */
public class FileData {
    private static final String TAG = FileData.class.getSimpleName();
    private final String filePath;
    private long fileSize;
    private String fileHash;
    private final Logger logger = LoggerFactory.getLogger(FileData.class);

    public FileData(String filePath) {
        System.out.println("FileData object created");
        if (filePath == null) {
            throw new IllegalArgumentException("invalid filePath (cannot be null)");
        }
        this.filePath = filePath;
        setFileSize();
        setFileHash();
    }

    /**
     * Gets the filename.
     * @return
     */
    public String getFilePath() {
        System.out.println("getFilePath()");
        return filePath;
    }

    /**
     * gets the file size.
     * @return
     */
    public long getFileSize() {
        System.out.println("getFilePath()");
        return fileSize;
    }

    /**
     * gets the file hash.
     * @return
     */
    public String getFileHash() {
        System.out.println("getFilePath()");
        return fileHash;
    }

    public void setFileSize() {
        System.out.println("setFileSize()");
        File file = new File(getFilePath());
//        FileInputStream fileInputStream;
//        FileChannel channel;
//        try {
//            fileInputStream = new FileInputStream(file);
//            channel = fileInputStream.getChannel();
//            fileSize = channel.size();
//
//            fileInputStream.close();
//            channel.close();
//            fileInputStream.close();
//        } catch (FileNotFoundException e) {
//            System.out.println(TAG + "Exception: " + e);
//            fileSize = 0;
//            filePath = "";
//        } catch (IOException e) {
//            System.out.println(TAG + "Exception: " + e);
//            fileSize = 0;
//        } finally {
//            channel = null;
//            fileInputStream = null;
//        }
        fileSize = file.length();

        System.out.println("FILE SIZE IS SET TO: " + fileSize);
    }

    public void setFileHash() {
        System.out.println("setFileHash()");
        File file = new File(filePath);
        FileInputStream fileInputStream;
        BufferedInputStream fileUri;
        BufferedInputStream in;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println(TAG + "Exception: " + e);
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
            System.out.println(TAG + "Exception: " + e);
            fileHash = "";
        } catch (IOException e) {
            System.out.println(TAG + "Exception: " + e);
            fileHash = "";
        } finally {
            fileInputStream = null;
            fileUri = null;
            in = null;
        }
        System.out.println("FILE HASH IS SET TO: " + fileHash);
    }
}
