package com.arkhive.components.api.filesystem.folder;

import com.arkhive.components.api.ApiResponse;

/**
 *
 * @author Chris Najar
 *
 */
public class FolderGetInfoResponse extends ApiResponse {
  //CHECKSTYLE:OFF
  private FolderInfo folder_info;
  //CHECKSTYLE:ON

  public FolderInfo getFolderInfo() {
    if (this.folder_info == null) {
      this.folder_info = new FolderInfo();
    }
    return this.folder_info;
  }

  /**
   *
   * @author Chris Najar
   *
   */
  public class FolderInfo {
    private String folderkey;
    private String name;
    private String desc;
    private String tags;
    private String created;
    private String revision;
    private String epoch;
    private String size;
    private String flag;
    private String permissions;
    private String avatar;
    //CHECKSTYLE:OFF
    private String parent_folderkey;
    private String custom_url;
    private String dbx_enabled;
    private String file_count;
    private String folder_count;
    private String shared_by_user;
    private String owner_name;
    //CHECKSTYLE:ON

    public String getDescription() {
      if (this.desc == null) {
        this.desc = "";
      }
      return this.desc;
    }

    public String getTags() {
      if (this.tags == null) {
        this.tags = "";
      }
      return this.desc;
    }

    public long getEpoch() {
      if (this.epoch == null) {
        this.epoch = "0";
      }
      return Long.valueOf(epoch);
    }

    public String getCustomUrl() {
      if (this.custom_url == null) {
        this.custom_url = "";
      }
      return this.custom_url;
    }

    public String getDbxEnabled() {
      if (this.dbx_enabled == null) {
        this.dbx_enabled = "";
      }

      return this.dbx_enabled;
    }

    public int getFileCount() {
      if (this.file_count == null) {
        this.file_count = "0";
      }
      return Integer.valueOf(this.file_count);
    }

    public int getFolderCount() {
      if (this.folder_count == null) {
        this.folder_count = "0";
      }
      return Integer.valueOf(this.folder_count);
    }

    public int getPermissions() {
      if (this.permissions == null) {
        this.permissions = "0";
      }
      return Integer.valueOf(this.permissions);
    }

    public String getAvatar() {
      if (this.avatar == null) {
        this.avatar = "";
      }
      return this.avatar;
    }

    public String getParentFolderKey() {
      if (this.parent_folderkey == null) {
        this.parent_folderkey = "";
      }
      return this.parent_folderkey;
    }

    public boolean isSharedByUser() {
      if (this.shared_by_user == null) {
        this.shared_by_user = "0";
      }
        return !"0".equalsIgnoreCase(this.shared_by_user);
    }

    public int getRevision() {
      if (this.revision == null) {
        this.revision = "0";
      }
      return Integer.valueOf(this.revision);
    }

    public String getOwnerName() {
      if (this.owner_name == null) {
        this.owner_name = "";
      }
      return this.owner_name;
    }

    public String getFolderKey() {
      if (this.folderkey == null) {
        this.folderkey = "";
      }
      return this.folderkey;
    }

    public String getFolderName() {
      if (this.name == null) {
        this.name = "";
      }
      return this.name;
    }

    public String getCreated() {
      if (this.created == null) {
        this.created = "";
      }
      return this.created;
    }

    public long getSize() {
      if (this.size == null) {
        this.size = "0";
      }
      return Integer.valueOf(this.size);
    }

    public int getFlag() {
      if (this.flag == null) {
        this.flag = "0";
      }
      return Integer.valueOf(this.flag);
    }

    public int getTotalItems() {
      return this.getFileCount() + this.getFolderCount();
    }

  }
}
