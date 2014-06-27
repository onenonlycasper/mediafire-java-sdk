package com.arkhive.components.core.module_api.responses;


import java.util.LinkedList;
import java.util.List;

/**
 * Class representing api/device/get_changes.php response.
 *
 * @author
 *         {"response":{"action":"device\/get_changes","updated":{"files":[],"folders":[]},"deleted":{"files":[],"folders":[]},"device_revision":"15260","changes_list_block":"500","result":"Success","new_key":"yes","current_api_version":"2.14"}}
 */
public class DeviceGetChangesResponse extends ApiResponse {
    private ChangedItems updated;
    private ChangedItems deleted;

    private String device_revision;
    private String changes_list_block;


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
     * @author
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
    }

    /**
     * class representing a file that has been changed.
     *
     * @author
     */
    public class File {
        private String quickkey;
        private String revision;
        private String created;
        private String filename;
        private String mimetype;
        private String filetype;

        private String parent_folderkey;
        private String parent_foldername;


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
     * @author
     */
    public class Folder {
        private String folderkey;
        private String revision;

        private String parent_folderkey;


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
