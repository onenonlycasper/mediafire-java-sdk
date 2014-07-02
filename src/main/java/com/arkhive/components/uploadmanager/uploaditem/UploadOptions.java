package com.arkhive.components.uploadmanager.uploaditem;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class which represents the data structure
 * that holds user options when they create an
 * Upload Item. The user does not have to use this
 * data structure and if they do not then a default
 * constructor is used with 5 max upload attempts,
 * resumable, and mandatory upload.
 *
 * @author
 */
public class UploadOptions {
    private final boolean resumable;
    private String uploadFolderKey;
    private String actionOnDuplicate;
    private String versionControl;
    private String uploadPath;
    private String customFileName;
    private String quickKey;
    private String modificationTime;
    private ActionOnInAccount actionOnInAccount;

    /**
     * Constructor for the upload options which takes parameters
     * set by implementor.
     *
     * @param resumable - upload is resumable or not
     */
    public UploadOptions(boolean resumable) {
        this.resumable = resumable;
    }

    public UploadOptions() {
        this(true);
    }

    /*============================
     * public methods
     *============================*/

    /**
     * get the custom file name for an upload item.
     *
     * @return - the custom file name. if the stored value is null then an empty string will be returned.
     */
    public String getCustomFileName() {
        if (customFileName == null) {
            customFileName = "";
        }
        return customFileName;
    }

    /**
     * set a custom file name for an UploadItem.
     *
     * @param customFileName - the file name desired. if null is passed, then an empty string will be set.
     */
    public void setCustomFileName(String customFileName) {
        if (customFileName == null) {
            customFileName = "";
        }
        this.customFileName = customFileName;
    }

    /**
     * Gets the action on what to do if a file already exists in a users account.
     *
     * @return the ActionOnInAccount representing how to handle a file already in a user account.
     */
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

    /**
     * Gets the upload folder key option.
     *
     * @return the upload folder key.
     */
    public String getUploadFolderKey() {
        if (uploadFolderKey == null) {
            uploadFolderKey = "";
        }
        return uploadFolderKey;
    }

    /**
     * Gets the ActionOnDuplicate option.
     *
     * @return 'keep', 'skip', 'replace'
     */
    public String getActionOnDuplicate() {
        if (actionOnDuplicate == null) {
            setActionOnDuplicate(ActionOnDuplicate.KEEP);
        }
        return actionOnDuplicate;
    }

    /**
     * Gets the version control option.
     *
     * @return create_patches, keep_revision, or none
     */
    public String getVersionControl() {
        if (versionControl == null) {
            setVersionControl(VersionControl.KEEP_REVISION);
        }
        return versionControl;
    }

    /**
     * Returns whether the resumable option is "yes" or "no".
     *
     * @return "yes" if resumable, "no" otherwise
     */
    public String getResumable() {
        if (resumable) {
            return "yes";
        } else {
            return "no";
        }
    }

    /**
     * Returns the path relative to myfiles root that will serve as the upload path.
     *
     * @return The upload path.
     */
    public String getUploadPath() {
        if (this.uploadPath == null) {
            uploadPath = "";
        }
        return this.uploadPath;
    }

    /**
     * sets the upload folder key for this Upload Item. this is the destination folder key.
     *
     * @param uploadFolderKey - the target folder key where the item will be uploaded.
     */
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

    /*============================
     * private methods
     *============================*/

    /**
     * sets the version control option for this upload item.
     *
     * @param control - which version control.
     */
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

    /**
     * Called to get the Modification Time.
     *
     * @return - the modification time.
     */
    public String getModificationTime() {
        return modificationTime;
    }

    /**
     * Sets the quick key.
     *
     * @param quickKey - the quickkey to set.
     */
    public void setQuickKey(String quickKey) {
        this.quickKey = quickKey;
    }

    /**
     * Called to get the quick key.
     *
     * @return the quick key.
     */
    public String getQuickKey() {
        return quickKey;
    }

    /**
     * Sets the modification time. A valid format must be entered.
     *
     * @param modificationTime - the modification time to set.
     */
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

    /**
     * sets the action on duplicate option for this upload item.
     *
     * @param actionOnDuplicate - the action to take.
     */
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

    /**
     * enum for the upload option "action_on_duplicate"
     * in the GET request to be sent via upload/pre_upload.php.
     *
     * @author
     */
     public enum ActionOnDuplicate {
        KEEP, SKIP, REPLACE
    }

    /**
     * enum for how to handle when an upload is already in a users account
     */
    public enum ActionOnInAccount {
        UPLOAD_ALWAYS, UPLOAD_IF_NOT_IN_FOLDER, DO_NOT_UPLOAD
    }

    /**
     * enum for the upload option "version_control"
     * in the POST request to be sent via upload/upload.php.
     *
     * @author
     */
    public enum VersionControl {
        CREATE_PATCHES, KEEP_REVISION, NONE
    }
}
