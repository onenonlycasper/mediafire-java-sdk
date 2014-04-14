package com.arkhive.components.api.filesystem.folder;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import com.arkhive.components.api.ApiResponse;
import com.arkhive.components.api.filesystem.FileSystemItem;

/**
 * Response class for api/folder/get_revision.php.
 * @author Chris Najar
 *
 */
public class FolderGetRevisionResponse extends ApiResponse {
  private Changes changes;
  private String revision;

  public Changes getChanges() {
    if (this.changes == null) {
      this.changes = new Changes();
    }
    return changes;
  }

  public int getRevision() {
    if (this.revision == null) {
      this.revision = "0";
    }

    return Integer.valueOf(revision);
  }

  /**
   * represents changes json object in response.
   * @author Chris Najar
   *
   */
  public class Changes {
    private String add;
    private String remove;
    private String update;

    public List<FileSystemItem> getAdded() {
      if (this.add == null) {
        this.add = "";
      }
      return convertStringListToCollection(add);
    }

    public List<FileSystemItem> getRemoved() {
      if (this.remove == null) {
        this.remove = "";
      }
      return convertStringListToCollection(remove);
    }

    public List<FileSystemItem> getUpdated() {
      if (this.update == null) {
        this.update = "";
      }
      return convertStringListToCollection(update);
    }

    private List<FileSystemItem> convertStringListToCollection(String str) {
      if (str.length() == 0) {
        return new LinkedList<FileSystemItem>();
      }
      //replace extra comma at end of string
      String fixedStr = str;
      if (fixedStr.charAt(fixedStr.length() - 1) == ',') {
        fixedStr = fixedStr.substring(0, fixedStr.length() - 1);
      }

      // create tokenizer between commas
      StringTokenizer st = new StringTokenizer(str, ",");
      List<FileSystemItem> items = new LinkedList<FileSystemItem>();

      while (st.hasMoreElements()) {
        String nextElement = (String) st.nextElement();
        FileSystemItem item = convertStringToItem(nextElement);
        items.add(item);
      }

      return items;
    }

    private FileSystemItem convertStringToItem(String str) {
      String[] split = str.split("-");
      String key = split[1];
      boolean isFolder;
      isFolder = "folder".equalsIgnoreCase(split[0]);
      return new FileSystemItem.Builder().isFolder(isFolder).key(key).build();
    }
  }
}
