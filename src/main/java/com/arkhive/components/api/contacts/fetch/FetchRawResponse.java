package com.arkhive.components.api.contacts.fetch;

import com.arkhive.components.api.contacts.ContactResponse;

/**
 * Fetch response.
 *
 * @author
 */
public class FetchRawResponse extends ContactResponse {
    //CHECKSTYLE:OFF
    private String node_count;
    //CHECKSTYLE:ON
    private String directory;
    private String data;

    public int getNodeCount() {
        if (node_count == null) {
            node_count = "0";
        }
        return Integer.parseInt(node_count);
    }

    public String getDirectory() {
        if (this.directory == null) {
            this.directory = "";
        }
        return this.directory;
    }

    public String getData() {
        if (this.data == null) {
            this.data = "";
        }
        return this.data;
    }

    /**
     * creates a raw trie from this response.
     *
     * @return
     */
    public RawTrie getRawTrie() {
        return new RawTrie(getDirectory(), getData());
    }
}
