package com.arkhive.components.api.filesystem.folder;

import java.util.LinkedList;
import java.util.List;

import com.arkhive.components.api.ApiResponse;

/**
 * folder search response class.
 * @author John Moore
 *
 */
public class FolderSearchResponse extends ApiResponse {
  //CHECKSTYLE:OFF
  private String results_count;
  //CHECKSTYLE:ON
  private List<Result> results;

  public int getResultsCount() {
    if (this.results_count == null) {
      this.results_count = "0";
    }
    return Integer.valueOf(results_count);
  }

  public List<Result> getResults() {
    if (this.results == null) {
      this.results = new LinkedList<Result>();
    }
    return results;
  }

    /**
     * result from folder search.
     * @author John Moore
     *
     */
  public class Result {
    private String type;
    private String quickkey;
    private String filename;
    private String pass;
    private String created;
    private String size;
    private String mimetype;
    private String filetype;
    private String privacy;
    private String flag;
    private String relevancy;
    private String hash;

    private String folderkey;
    private String name;
    //CHECKSTYLE:OFF
    private String parent_folderkey;
    private String parent_name;
    private String password_protected;
    private String byte_count;
    private String total_folders;
    private String total_files;
    private String total_size;
    private String delete_date;
    //CHECKSTYLE:ON

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
    public String getByteCount() {
      if (this.byte_count == null) {
          this.byte_count = "";
      }
      return this.byte_count;
    }
    public int getTotalFolders() {
      if (this.total_folders == null) {
          this.total_folders = "0";
      }
      return Integer.valueOf(this.total_folders);
    }
    public int getTotalFiles() {
      if (this.total_files == null) {
          this.total_files = "0";
      }
      return Integer.valueOf(this.total_files);
    }
    public long getTotalSize() {
      if (this.total_size == null) {
          this.total_size = "0";
      }
      return Long.valueOf(this.total_size);
    }
    public String getDeleteDate() {
      if (this.delete_date == null) {
          this.delete_date = "";
      }
      return this.delete_date;
    }
    public String getType() {
      if (this.type == null) {
          this.type = "";
      }
      return this.type;
    }
    public String getQuickKey() {
      if (this.quickkey == null) {
          this.quickkey = "";
      }
      return this.quickkey;
    }
    public String getFileName() {
      if (this.filename == null) {
          this.filename = "";
      }
      return this.filename;
    }
    public String getParentFolderKey() {
      if (this.parent_folderkey == null) {
          this.parent_folderkey = "";
      }
      return this.parent_folderkey;
    }
    public String getParentName() {
      if (this.parent_name == null) {
          this.parent_name = "";
      }
      return this.parent_name;
    }
    public String getPass() {
      if (this.pass == null) {
          this.pass = "";
      }
      return this.pass;
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
      return Long.valueOf(this.size);
    }
    public String getMimeType() {
      if (this.mimetype == null) {
          this.mimetype = "";
      }
      return this.mimetype;
    }
    public String getFileType() {
      if (this.filetype == null) {
          this.filetype = "";
      }
      return this.filetype;
    }
    public boolean isPublic() {
      if (this.privacy == null) {
          this.privacy = "private";
      }
        return "public".equalsIgnoreCase(this.privacy);
    }
    public String getPasswordProtected() {
      if (this.password_protected == null) {
          this.password_protected = "";
      }
      return this.password_protected;
    }
    public int getFlag() {
      if (this.flag == null) {
          this.flag = "0";
      }
      return Integer.valueOf(this.flag);
    }
    public int getRelevancy() {
      if (this.relevancy == null) {
        this.relevancy = "0";
      }
      return Integer.valueOf(this.relevancy);
    }

    public String getHash() {
        if (this.hash == null) {
            this.hash = "";
        }
        return this.hash;
    }
  }
}
