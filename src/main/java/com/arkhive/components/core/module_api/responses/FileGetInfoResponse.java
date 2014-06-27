package com.arkhive.components.core.module_api.responses;

import java.util.LinkedList;
import java.util.List;

/**
 * @author
 */
public class FileGetInfoResponse extends ApiResponse {
    private FileInfo file_info;
    private List<FileInfos> file_infos;

    public FileInfo getFileInfo() {
        if (file_info == null) {
            this.file_info = new FileInfo();
        }
        return this.file_info;
    }

    public List<FileInfos> getFileInfos() {
        if (file_infos == null) {
            file_infos = new LinkedList<FileInfos>();
        }

        return file_infos;
    }

    public class FileInfos {
        private String quickkey;
        private String filename;
        private String created;
        private String downloads;
        private String description;
        private String size;
        private String privacy;
        private String password_protected;
        private String hash;
        private String filetype;
        private String mimetype;
        private String owner_name;
        private String flag;
        private String parent_folderkey;
        private String revision;
        private String view;
        private String edit;
        private List<Links> links;

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

        public String getCreated() {
            if (this.created == null) {
                this.created = "";
            }
            return this.created;
        }

        public int getDownloads() {
            if (this.downloads == null) {
                this.downloads = "0";
            }
            return Integer.valueOf(this.downloads);
        }

        public String getDescription() {
            if (this.description == null) {
                this.description = "";
            }
            return this.description;
        }

        public int getSize() {
            if (this.size == null) {
                this.size = "0";
            }
            return Integer.valueOf(this.size);
        }

        public boolean isPrivate() {
            if (this.privacy == null) {
                this.privacy = "public";
            }

            return "private".equalsIgnoreCase(this.privacy);
        }

        public boolean isPasswordProtected() {
            if (this.password_protected == null) {
                this.password_protected = "no";
            }

            return "yes".equalsIgnoreCase(this.password_protected);
        }

        public String getHash() {
            if (this.hash == null) {
                this.hash = "";
            }
            return this.hash;
        }

        public String getFileType() {
            if (this.filetype == null) {
                this.filetype = "";
            }
            return this.filetype;
        }

        public String getMimeType() {
            if (this.mimetype == null) {
                this.mimetype = "";
            }
            return this.mimetype;
        }

        public String getOwnerName() {
            if (this.owner_name == null) {
                this.owner_name = "";
            }
            return this.owner_name;
        }

        public int getFlag() {
            if (this.flag == null) {
                this.flag = "-1";
            }
            return Integer.valueOf(this.flag);
        }

        public String getParentFolderKey() {
            if (this.parent_folderkey == null) {
                this.parent_folderkey = "";
            }
            return this.parent_folderkey;
        }

        public int getRevision() {
            if (this.revision == null) {
                this.revision = "0";
            }
            return Integer.valueOf(this.revision);
        }

        public String getView() {
            return view;
        }

        public String getEdit() {
            return edit;
        }

        public List<Links> getLinks() {
            return links;
        }

        private class Links {
            private String view;
            private String normal_download;

            private String getViewLink() {
                return view;
            }

            private String getNormalDownloadLink() {
                return normal_download;
            }
        }
    }

    /**
     * class representing contents of "file_info" jsonobject.
     *
     * @author
     */
    public class FileInfo {
        private String password_protected;
        private String owner_name;
        private String shared_by_user;
        private String parent_folderkey;
        private String quickkey;
        private String filename;
        private String created;
        private String downloads;
        private String description;
        private String size;
        private String privacy;
        private String hash;
        private String filetype;
        private String mimetype;
        private String flag;
        private String permissions;
        private String revision;

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

        public String getCreated() {
            if (this.created == null) {
                this.created = "";
            }
            return this.created;
        }

        public int getDownloads() {
            if (this.downloads == null) {
                this.downloads = "0";
            }
            return Integer.valueOf(this.downloads);
        }

        public String getDescription() {
            if (this.description == null) {
                this.description = "";
            }
            return this.description;
        }

        public int getSize() {
            if (this.size == null) {
                this.size = "0";
            }
            return Integer.valueOf(this.size);
        }

        public boolean isPrivate() {
            if (this.privacy == null) {
                this.privacy = "public";
            }

            return "private".equalsIgnoreCase(this.privacy);
        }

        public boolean isPasswordProtected() {
            if (this.password_protected == null) {
                this.password_protected = "no";
            }

            return "yes".equalsIgnoreCase(this.password_protected);
        }

        public String getHash() {
            if (this.hash == null) {
                this.hash = "";
            }
            return this.hash;
        }

        public String getFileType() {
            if (this.filetype == null) {
                this.filetype = "";
            }
            return this.filetype;
        }

        public String getMimeType() {
            if (this.mimetype == null) {
                this.mimetype = "";
            }
            return this.mimetype;
        }

        public String getOwnerName() {
            if (this.owner_name == null) {
                this.owner_name = "";
            }
            return this.owner_name;
        }

        public int getFlag() {
            if (this.flag == null) {
                this.flag = "-1";
            }
            return Integer.valueOf(this.flag);
        }

        public boolean isSharedByUser() {
            if (this.shared_by_user == null) {
                this.shared_by_user = "yes";
            }

            return !"no".equalsIgnoreCase(this.shared_by_user);
        }

        public int getPermissions() {
            if (this.permissions == null) {
                this.permissions = "-1";
            }

            return Integer.valueOf(this.permissions);
        }

        public String getParentFolderKey() {
            if (this.parent_folderkey == null) {
                this.parent_folderkey = "";
            }
            return this.parent_folderkey;
        }

        public int getRevision() {
            if (this.revision == null) {
                this.revision = "0";
            }
            return Integer.valueOf(this.revision);
        }
    }
}
