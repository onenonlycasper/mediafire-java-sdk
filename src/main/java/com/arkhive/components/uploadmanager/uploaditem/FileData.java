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
      logger.info(TAG + "FileData() created");
      if (filePath == null) { throw new IllegalArgumentException("invalid filePath (cannot be null)"); }
      this.filePath = filePath;
      this.setFileSize();
      this.setFileHash();
    }

    /*============================
     * public getters
     *============================*/
    /**
     * Gets the filename.
     * @return
     */
    public String getFilePath() { return this.filePath; }
    
    /**
     * gets the file size.
     * @return
     */
    public long getFileSize() { return this.fileSize; }
    
    /**
     * gets the file hash.
     * @return
     */
    public String getFileHash() { return this.fileHash; }

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
          this.fileSize = channel.size();

          fileInputStream.close();
          channel.close();
      } catch (FileNotFoundException e) {
          this.fileSize = 0;
          e.printStackTrace();
      } catch (IOException e) {
          this.fileSize = 0;
          e.printStackTrace();
      }
        logger.info(TAG + "--file size set to: " + this.fileSize);
    }

    private void setFileHash() {
        logger.info(TAG + "setFileHash()");
      File file = new File(filePath);
      FileInputStream fileInputStream;

      try {
          fileInputStream = new FileInputStream(file);
      } catch (FileNotFoundException e1) {
          this.fileHash = "";
          return;
      }

      BufferedInputStream fileURI = new BufferedInputStream(fileInputStream);

      MessageDigest digest;
      try {
          digest = MessageDigest.getInstance("SHA-256");
          byte[] bytes = new byte[8192];
          BufferedInputStream in = new BufferedInputStream(fileURI);
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
          fileURI.close();
          in.close();
      } catch (NoSuchAlgorithmException e) {
          e.printStackTrace();
          this.fileHash = "";
      } catch (IOException e) {
          e.printStackTrace();
          this.fileHash = "";
      }
        logger.info(TAG + "--file hash set to: " + fileHash);
    }
}
