package com.arkhive.components.api.filesystem.folder;

import java.util.List;

import com.arkhive.components.api.filesystem.FileSystemItem;

/**
 * listener for periodic updates on folder/get_content.php calls.
 *
 * @author Chris Najar
 */
public interface GetContentsUpdateListener {
    public void contentsReceived(List<FileSystemItem> items);

    public void finishedReceivingContents();
}
