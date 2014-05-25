package com.arkhive.components.uploadmanager.uploaditem;

/**
 * Class which represents the data structure
 * that holds user options when they create an
 * Upload Item. The user does not have to use this
 * data structure and if they do not then a default
 * constructor is used with 5 max upload attempts,
 * resumable, and mandatory upload.
 * @author Chris Najar
 *
 */
public class UploadOptions {
    private String resumable;
    private String uploadFolderKey;
    private String actionOnDuplicate;
    private String versionControl;
    private String uploadPath;
    private ActionOnInAccount actionOnInAccount;

    /**
     * Constructor for the upload options which takes parameters
     * set by implementor.

     * @param resumable - upload is resumable or not
     */
    public UploadOptions(boolean resumable) {
        if (resumable) {
            this.resumable = "yes";
        } else {
            this.resumable = "no";
        }
    }

    public UploadOptions() {
        this(true);
    }

    /*============================
     * public getters
     *============================*/

    /**
     * Gets the action on what to do if a file already exists in a users account.
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
     * @return the upload folder key.
     */
    public String getUploadFolderKey() {
      if (uploadFolderKey == null) {
        uploadFolderKey = "myfiles";
      } else if (uploadFolderKey.isEmpty()) {
        uploadFolderKey = "myfiles";
      }
      return uploadFolderKey;
    }

    /**
     * Gets the ActionOnDuplicate option.
     * @return 'keep', 'skip', 'replace'
     */
    public String getActionOnDuplicate() {
        if (actionOnDuplicate == null) { setActionOnDuplicate(ActionOnDuplicate.KEEP); }
        return actionOnDuplicate;
    }

    /**
     * Gets the version control option.
     * @return create_patches, keep_revision, or none
     */
    public String getVersionControl() {
        if (versionControl == null) { setVersionControl(VersionControl.CREATE_PATCHES); }
        return versionControl;
    }

    /**
     * Returns whether the resumable option is "yes" or "no".
     * @return "yes" if resumable, "no" otherwise
     */
    public String isResumable() { return resumable; }

    /**
     * Returns the path relative to myfiles root that will serve as the upload path.
     * @return  The upload path.
     */
    public String getUploadPath() {
        if (this.uploadPath == null) { return ""; }
        return this.uploadPath;
    }

    /*============================
     * public setters
     *============================*/
    /**
     * sets the upload folder key for this Upload Item. this is the destination folder key.
     * @param uploadFolderKey
     */
    public void setUploadFolderKey(String uploadFolderKey) {
      if (uploadFolderKey == null) {
        uploadFolderKey = "myfiles";
      } else if (uploadFolderKey.isEmpty()) {
        uploadFolderKey = "myfiles";
      }
      this.uploadFolderKey = uploadFolderKey;
    }

    public void setUploadPath(String path) {
        if (path != null) {
            this.uploadPath = path;
        }
    }

    /*============================
     * private setters
     *============================*/
    /**
     * sets the version control option for this upload item.
     * @param control - which version control.
     */
    private void setVersionControl(VersionControl control) {
        switch(control) {
            case CREATE_PATCHES: this.versionControl = "create_patches"; break;
            case KEEP_REVISION: this.versionControl = "keep_revision"; break;
            case NONE: this.versionControl = "none"; break;
            default: this.versionControl = "create_patches"; break;
        }
    }

    /**
     * sets the action on duplicate option for this upload item.
     * @param actionOnDuplicate
     */
    public void setActionOnDuplicate(ActionOnDuplicate actionOnDuplicate) {
        switch(actionOnDuplicate) {
            case KEEP:      this.actionOnDuplicate = "keep";    break;
            case REPLACE:   this.actionOnDuplicate = "replace"; break;
            case SKIP:      this.actionOnDuplicate = "skip";    break;
            default:        this.actionOnDuplicate = "keep";    break;
        }
    }

    /**
     * enum for the upload option "action_on_duplicate"
     * in the GET request to be sent via upload/pre_upload.php.
     * @author Chris Najar
     *
     */
    public enum ActionOnDuplicate { KEEP, SKIP, REPLACE };

    /**
     * enum for how to handle when an upload is already in a users account
     */
    public enum ActionOnInAccount { UPLOAD_ALWAYS, UPLOAD_IF_NOT_IN_FOLDER, DO_NOT_UPLOAD}
    /**
     * enum for the upload option "version_control"
     * in the POST request to be sent via upload/upload.php.
     * @author Chris Najar
     *
     */
    public enum VersionControl { CREATE_PATCHES, KEEP_REVISION, NONE };
}
