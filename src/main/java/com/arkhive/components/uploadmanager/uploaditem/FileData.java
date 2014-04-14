package com.arkhive.components.uploadmanager.uploaditem;

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
  private String fileName;
  private long fileSize;
  private String fileHash;

    public FileData(String fileName) {
      if (fileName == null) { throw new IllegalArgumentException("invalid fileName (cannot be null)"); }
      this.fileName = fileName;
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
    public String getFileName() { return this.fileName; }
    
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
      File file = new File(getFileName());
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
    }

    private void setFileHash() {
      File file = new File(fileName);
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
    }
}
