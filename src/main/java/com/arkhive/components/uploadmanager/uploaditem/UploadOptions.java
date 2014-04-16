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
    /**
     * enum for the upload option "action_on_duplicate"
     * in the GET request to be sent via upload/pre_upload.php.
     * @author Chris Najar
     *
     */
    public enum ActionOnDuplicate { KEEP, SKIP, REPLACE };
    /**
     * enum for the upload option "version_control"
     * in the POST request to be sent via upload/upload.php.
     * @author Chris Najar
     *
     */
    public enum VersionControl { CREATE_PATCHES, KEEP_REVISION, NONE };
    private int currentUploadAttempts;
    private int maximumUploadAttempts;
    private String resumable;
    private String uploadFolderKey;
    private String actionOnDuplicate;
    private String versionControl;
    private String uploadPath;

    /**
     * Constructor for the upload options which takes parameters
     * set by implementor.
     * @param maxUploadAttempts - maximum times to attempt to upload
     * @param curUploadAttempts - current number of upload attempts
     * @param resumable - upload is resumable or not
     * TODO(jmoore): please advise if we should make ALL uploads resumable,
     * or do we want to implement whether or not the upload is resumable on a per UploadItem basis?
     */
    public UploadOptions(int maxUploadAttempts, int curUploadAttempts, boolean resumable) {
        this.currentUploadAttempts = curUploadAttempts;
        this.maximumUploadAttempts = maxUploadAttempts;
        if (resumable) {
            this.resumable = "yes";
        } else {
            this.resumable = "no";
        }
    }

    public UploadOptions() {
        this(5, 0, true);
    }

    /*============================
     * public getters
     *============================*/
    /**
     * Gets the current upload attempts.
     * @return number of current attempts for this UploadItem
     */
    public int getCurrentUploadAttempts() { return currentUploadAttempts; }

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
     * protected getters
     *============================*/
    /**
     * Returns the maximum number of Upload Attempts for this UploadItem.
     * @return max number of attempts (int)
     */
    protected int getMaximumUploadAttempts() { return maximumUploadAttempts; }

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


    /*============================
     * protected methods
     *============================*/
    /**
     * indicates an upload attempt has been made so increases the current upload attempts by 1.
     */
    protected void uploadAttempt() { currentUploadAttempts++; }
}
