package com.arkhive.components.api.filesystem;

import java.util.List;

/** Methods to operate on a collection of FileSystemItems. */
public interface FileItemCollection {

    /** Gets a list of all FileSystemItems under a given key.
     *
     * @param  key  The key of the folder to query.
     *
     * @return A list containing all of the FileSystemItems under the key.
     */
    public List<FileSystemItem> getContentByFolderKey(String key);

    /** Get a FileSystemItem from a key.
     *
     * @param  key  The key of the item to fetch.
     *
     * @return  The FileSystemItem matching the key.
     */
    public FileSystemItem getInformationByKey(String key);

    /** Adds a new item to the file system.
     *
     * @param  newItem  The new FileSystemItem to add.
     * @param  key  The parent of the new FileSystemItem.
     *
     * @return  The status of the operation.
     */
    public boolean addFileSystemItem(FileSystemItem newItem, String key);

    /** Remove an item from the file system.
     *
     * @param  itemKey  The key of the item to remove.
     * @param  folderKey  The key of the folder to remove the item from.
     *
     * @return  The status of the removal operation.
     */
    public boolean removeFileSystemItem(String itemKey, String folderKey);

    /** Updates an item on the file system.
     *
     * @param  newItem  The item containing the new information.
     * @param  key  The key of the item to update.
     *
     * @return  The status of the update operation.
     */
    public boolean updateFileSystemItem(FileSystemItem newItem, String key);
}
