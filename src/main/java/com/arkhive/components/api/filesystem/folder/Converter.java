package com.arkhive.components.api.filesystem.folder;

import java.util.LinkedList;
import java.util.List;

import com.arkhive.components.api.filesystem.FileSystemItem;
import com.arkhive.components.api.filesystem.FileSystemItem.Builder;
import com.arkhive.components.api.filesystem.folder.FolderGetContentsResponse.File;
import com.arkhive.components.api.filesystem.folder.FolderGetContentsResponse.Folder;

/**
 *
 * @author Chris Najar
 *
 */
public class Converter {

  /** Converts a collection of Folder objects (from FolderGetContentsResponse) into FileSystemItem collection.
   * @param allFolders - collection of Folder objects.
   * @param parentFolderKey - the parent folder key of "allFolders".
   * @return a collection of FileSystemItem objects.
   */
  public static List<FileSystemItem> convertFolders(List<Folder> allFolders, String parentFolderKey) {
    List<FileSystemItem> items = new LinkedList<FileSystemItem>();

    for (Folder f : allFolders) {
      FileSystemItem item = makeBuilder(f, parentFolderKey).build();
      items.add(item);
    }

    return items;
  }

  private static Builder makeBuilder(Folder f, String parentFolderKey) {
    Builder builder = new Builder();
    builder.createdDate(f.getCreated());
    builder.description(f.getDescription());
    builder.isFolder(true);
    builder.isShared(f.isSharedFromOther());
    builder.key(f.getFolderkey());
    builder.name(f.getFolderName());
    builder.parentFolderKey(parentFolderKey);
    builder.isSharedFromOther(f.isSharedFromOther());
    builder.isShared(f.isSharedByUser());
    builder.revision(f.getRevision().getRevision());
    builder.revisionEpoch((int) f.getRevision().getEpoch());
    builder.size((int) f.getSize());

    return builder;
  }

  /** Converts a collection of File objects (from FolderGetContentsResponse) into FileSystemItem collection.
   * @param allFiles - collection of File objects.
   * @param parentFolderKey - the parent folder key of "allFiles".
   * @return a collection of FileSystemItem objects.
   */
  public static List<FileSystemItem> convertFiles(List<File> allFiles, String parentFolderKey) {
    List<FileSystemItem> items = new LinkedList<FileSystemItem>();

    for (File f : allFiles) {
      FileSystemItem item = makeBuilder(f, parentFolderKey).build();
      items.add(item);
    }

    return items;
  }

  private static Builder makeBuilder(File f, String parentFolderKey) {
    Builder builder = new Builder();
    builder.createdDate(f.getCreated());
    builder.description(f.getDescription());
    builder.isFolder(false);
    builder.isShared(f.isSharedByUser());
    builder.key(f.getQuickKey());
    builder.mimeType(f.getMimeType());
    builder.name(f.getFilename());
    builder.parentFolderKey(parentFolderKey);
    builder.size((int) f.getSize());
    builder.hash(f.getHash());
    return builder;

  }

}
