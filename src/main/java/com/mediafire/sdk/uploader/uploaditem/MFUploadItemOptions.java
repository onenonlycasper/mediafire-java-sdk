package com.mediafire.sdk.uploader.uploaditem;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MFUploadItemOptions {
    private final boolean resumable;
    private String uploadFolderKey;
    private String actionOnDuplicate;
    private String versionControl;
    private String uploadPath;
    private String customFileName;
    private String quickKey;
    private String modificationTime;
    private ActionOnInAccount actionOnInAccount;

    public MFUploadItemOptions(boolean resumable) {
        this.resumable = resumable;
    }

    public MFUploadItemOptions() {
        this(true);
    }

    public String getCustomFileName() {
        if (customFileName == null) {
            customFileName = "";
        }
        return customFileName;
    }

    public void setCustomFileName(String customFileName) {
        if (customFileName == null) {
            customFileName = "";
        }
        this.customFileName = customFileName;
    }

    public ActionOnInAccount getActionOnInAccount() {
        if (actionOnInAccount == null) {
            actionOnInAccount = ActionOnInAccount.DO_NOT_UPLOAD;
        }
        return actionOnInAccount;
    }

    public void setActionOnInAccount(ActionOnInAccount actionOnInAccount) {
        if (actionOnInAccount == null) {
            actionOnInAccount = ActionOnInAccount.DO_NOT_UPLOAD;
        }
        this.actionOnInAccount = actionOnInAccount;
    }

    public String getUploadFolderKey() {
        if (uploadFolderKey == null) {
            uploadFolderKey = "";
        }
        return uploadFolderKey;
    }

    public String getActionOnDuplicate() {
        if (actionOnDuplicate == null) {
            setActionOnDuplicate(ActionOnDuplicate.KEEP);
        }
        return actionOnDuplicate;
    }

    public String getVersionControl() {
        if (versionControl == null) {
            setVersionControl(VersionControl.KEEP_REVISION);
        }
        return versionControl;
    }

    public String getResumable() {
        if (resumable) {
            return "yes";
        } else {
            return "no";
        }
    }

    public String getUploadPath() {
        if (this.uploadPath == null) {
            uploadPath = "";
        }
        return this.uploadPath;
    }

    public void setUploadFolderKey(String uploadFolderKey) {
        if (uploadFolderKey == null) {
            uploadFolderKey = "";
        }
        this.uploadFolderKey = uploadFolderKey;
    }

    public void setUploadPath(String path) {
        if (path != null) {
            this.uploadPath = path;
        }
    }

    private void setVersionControl(VersionControl control) {
        switch (control) {
            case CREATE_PATCHES:
                versionControl = "create_patches";
                break;
            case KEEP_REVISION:
                versionControl = "keep_revision";
                break;
            case NONE:
                versionControl = "none";
                break;
            default:
                versionControl = "create_patches";
                break;
        }
    }

    public String getModificationTime() {
        return modificationTime;
    }

    public void setQuickKey(String quickKey) {
        this.quickKey = quickKey;
    }

    public String getQuickKey() {
        return quickKey;
    }

    public void setModificationTime(String modificationTime) {
        String timeString;
        if (modificationTime == null || modificationTime.isEmpty()) {
            timeString = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US).format(new Date());
        } else {
            timeString = modificationTime;
        }

        try {
            this.modificationTime = URLEncoder.encode(timeString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new IllegalStateException("UTF-8 encoding not available");
        }
    }

    public void setActionOnDuplicate(ActionOnDuplicate actionOnDuplicate) {
        switch (actionOnDuplicate) {
            case KEEP:
                this.actionOnDuplicate = "keep";
                break;
            case REPLACE:
                this.actionOnDuplicate = "replace";
                break;
            case SKIP:
                this.actionOnDuplicate = "skip";
                break;
            default:
                this.actionOnDuplicate = "keep";
                break;
        }
    }

     public enum ActionOnDuplicate {
        KEEP, SKIP, REPLACE
    }

    public enum ActionOnInAccount {
        UPLOAD_ALWAYS, UPLOAD_IF_NOT_IN_FOLDER, DO_NOT_UPLOAD
    }

    public enum VersionControl {
        CREATE_PATCHES, KEEP_REVISION, NONE
    }
}
