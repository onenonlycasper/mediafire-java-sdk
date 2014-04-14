package com.arkhive.components.api.filesystem;

/**
 *
 * @author john moore
 *
 */
public class FileSystemItem {
    private boolean isFolder;
    private String parentFolderKey;
    private String key;
    private String name;
    private String mimeType;
    private String description;
    private String createdDate;
    private String hash;
    private String thumbnail;
    private boolean isShared;
    private boolean isSharedFromOther;
    private long size;
    private int revisionEpoch;
    private long revision;


  public String getThumbnailBase64Encoded() {
    if (thumbnail == null) { thumbnail = ""; }
    return thumbnail;
  }
  /**
   * The hash of the item. only applies to files.
   * @return the hash of the file.
   */
  public String getHash() {
    if (hash == null) { hash = ""; }
    return hash;
  }

  /** Indicated if the item a folder.
   * @return true if the item is a folder.
   */
  public boolean isFolder() { return isFolder; }

  /** The key of the items parent.
   * @return the key of the items parent.
   */
  public String getParentFolderKey() {
    if (parentFolderKey == null) { parentFolderKey = ""; }
    return parentFolderKey;
  }

  /** The key for the item.
   * For a folder this is the folder key, for a file it is the quickey.
   * @return The key of the item.
   */
  public String getKey() {
    if (key == null) { key = ""; }
    return key;
  }

  /** The name of the item.
   * @return The name of the item.
   */
  public String getName() {
    if (name == null) { name = ""; }
    return name;
  }

  /** The mimetype of the item.
   * @return the mimeType
   */
  public String getMimeType() {
    if (mimeType == null) { mimeType = ""; }
    return mimeType;
  }

  /** The description of the item.
   * @return The description of the item.
   */
  public String getDescription() {
    if (description == null) { description = ""; }
    return description;
  }

  /** The date the item was created in the cloud.
   * @return The items creation date.
   */
  public String getCreatedDate() {
    if (createdDate == null) { createdDate = ""; }
    return createdDate;
  }

  /** Indicates if the file is shared with other users.
   * @return The sharing status of the item.
   */
  public boolean isShared() { return isShared; }

  /** Indicates if the file is shared from another user.
   * @return The sharing status of the item.
   */
  public boolean isSharedFromOther() { return isSharedFromOther; }

  /** The items size in bytes.
   * @return The size of the item.
   */
  public long getSize() { return size; }

  /** The epoch of the last revision.
   * @return The epoch time of the last revision.
   */
  public int getRevisionEpoch() { return revisionEpoch; }

  /** The revision number of the last revision.
   * @return The revision number.
   */
  public long getRevision() { return revision; }

  /**
   *
   */
  @Override
  public String toString() {

      return "key: " + getKey() + ", "
              + "parentFolderKey: " + getParentFolderKey() + ", "
              + "isFolder: " + isFolder() + ", "
              + "name: " + getName() + ", "
              + "mimeType: " + getMimeType() + ", "
              + "description: " + getDescription() + ", "
              + "createdDate: " + getCreatedDate() + ", "
              + "isShared: " + isShared() + ", "
              + "isSharedFromOther: " + isSharedFromOther() + ", "
              + "size: " + getSize() + ", "
              + "hash: " + getHash() + ", "
              + "revisionEpoch: " + getRevisionEpoch() + ", "
              + "revision: " + getRevision();
  }
  /**
   *
   * @author john moore
   *
   */
  public static class Builder {
        private FileSystemItem item = new FileSystemItem();

        public Builder isFolder(boolean value) {
            item.isFolder = value;
            return this;
        }

        public Builder parentFolderKey(String value) {
            if (value == null || value.isEmpty()) { value = ""; }
            item.parentFolderKey = value;
            return this;
        }

        public Builder key(String value) {
            if (value == null || value.isEmpty()) { value = ""; }
            item.key = value;
            return this;
        }

        public Builder name(String value) {
            if (value == null || value.isEmpty()) { value = ""; }
            item.name = value;
            return this;
        }

        public Builder mimeType(String value) {
            if (value == null || value.isEmpty()) { value = ""; }
            item.mimeType = value;
            return this;
        }

        public Builder description(String value) {
            if (value == null || value.isEmpty()) { value = ""; }
            item.description = value;
            return this;
        }

        public Builder createdDate(String value) {
            if (value == null) { value = ""; }
            item.createdDate = value;
            return this;
        }

        public Builder isShared(boolean value) {
            item.isShared = value;
            return this;
        }

        public Builder isSharedFromOther(boolean value) {
            item.isSharedFromOther = value;
            return this;
        }

        public Builder size(int value) {
            item.size = value;
            return this;
        }

        public Builder revision(long value) {
            item.revision = value;
            return this;
        }

        public Builder revisionEpoch(int value) {
            item.revisionEpoch = value;
            return this;
        }

        public Builder hash(String value) {
          if (value == null) {
            value = "";
          }
          item.hash = value;
          return this;
        }

        public Builder thumbnailBase64Encoded(String value) {
          if (value == null) {
            value = "";
          }
          item.thumbnail = value;
          return this;
        }

        public FileSystemItem build() {
            return item;
        }
    }


}
