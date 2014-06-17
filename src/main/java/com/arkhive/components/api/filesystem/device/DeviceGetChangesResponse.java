package com.arkhive.components.api.filesystem.device;

import java.util.LinkedList;
import java.util.List;

import com.arkhive.components.api.ApiResponse;
import com.arkhive.components.api.filesystem.FileSystemItem;

/**
 * Class representing api/device/get_changes.php response.
 *
 * @author Chris Najar
 *         {"response":{"action":"device\/get_changes","updated":{"files":[],"folders":[]},"deleted":{"files":[],"folders":[]},"device_revision":"15260","changes_list_block":"500","result":"Success","new_key":"yes","current_api_version":"2.14"}}
 */
public class DeviceGetChangesResponse extends ApiResponse {
    private ChangedItems updated;
    private ChangedItems deleted;
    //CHECKSTYLE:OFF
    private String device_revision;
    private String changes_list_block;
    //CHECKSTYLE:ON

    public int getDeviceRevision() {
        if (device_revision == null) {
            device_revision = "0";
        }
        return Integer.valueOf(device_revision);
    }

    public int getChangesListBlock() {
        if (changes_list_block == null) {
            changes_list_block = "0";
        }
        return Integer.valueOf(changes_list_block);
    }

    public ChangedItems getUpdatedItems() {
        if (updated == null) {
            updated = new ChangedItems();
        }

        return updated;
    }

    public ChangedItems getDeletedItems() {
        if (deleted == null) {
            deleted = new ChangedItems();
        }

        return deleted;
    }

    /**
     * class representing a collection of files and folders which have been changed.
     *
     * @author Chris Najar
     */
    public class ChangedItems {
        private List<File> files;
        private List<Folder> folders;

        public List<File> getFiles() {
            if (files == null) {
                files = new LinkedList<File>();
            }
            return files;
        }

        public List<Folder> getFolders() {
            if (folders == null) {
                folders = new LinkedList<Folder>();
            }
            return folders;
        }

        public List<FileSystemItem> getAllItems() {
            List<FileSystemItem> combined = new LinkedList<FileSystemItem>();

            for (File file : getFiles()) {
                FileSystemItem.Builder builder = new FileSystemItem.Builder();
                builder.isFolder(false);
                builder.name(file.getFileName());
                builder.key(file.getQuickKey());
                FileSystemItem item = builder.build();
                combined.add(item);
            }

            for (Folder folder : getFolders()) {
                FileSystemItem.Builder builder = new FileSystemItem.Builder();
                builder.isFolder(true);
                builder.key(folder.getFolderKey());
                builder.revision(folder.getRevision());
                FileSystemItem item = builder.build();
                combined.add(item);
            }

            return combined;
        }
    }

    /**
     * class representing a file that has been changed.
     *
     * @author Chris Najar
     */
    public class File {
        private String quickkey;
        private String revision;
        private String created;
        private String filename;
        private String mimetype;
        private String filetype;
        //CHECKSTYLE:OFF
        private String parent_folderkey;
        private String parent_foldername;
        //CHECKSTYLE:ON

        public String getFileType() {
            if (filetype == null) {
                this.filetype = "";
            }
            return filetype;
        }

        public String getMimeType() {
            if (mimetype == null) {
                this.mimetype = "";
            }
            return mimetype;
        }

        public String getParentFolderName() {
            if (parent_foldername == null) {
                this.parent_foldername = "";
            }
            return parent_foldername;
        }

        public String getFileName() {
            if (filename == null) {
                this.filename = "";
            }
            return this.filename;
        }

        public String getQuickKey() {
            if (quickkey == null) {
                this.quickkey = "";
            }
            return quickkey;
        }

        public int getRevision() {
            if (revision == null) {
                this.revision = "0";
            }
            return Integer.valueOf(revision);
        }

        public String getCreated() {
            if (created == null) {
                this.created = "";
            }
            return created;
        }

        public String getParentFolderKey() {
            if (parent_folderkey == null) {
                this.parent_folderkey = "";
            }
            return parent_folderkey;
        }
    }

    /**
     * class representing a folder that has been changed.
     *
     * @author Chris Najar
     */
    public class Folder {
        private String folderkey;
        private String revision;
        //CHECKSTYLE:OFF
        private String parent_folderkey;
        //CHECKSTYLE:ON

        public String getFolderKey() {
            if (folderkey == null) {
                this.folderkey = "";
            }
            return folderkey;
        }

        public int getRevision() {
            if (revision == null) {
                this.revision = "0";
            }
            return Integer.valueOf(revision);
        }

        public String getParentFolderKey() {
            if (parent_folderkey == null) {
                this.parent_folderkey = "";
            }
            return parent_folderkey;
        }
    }
}
