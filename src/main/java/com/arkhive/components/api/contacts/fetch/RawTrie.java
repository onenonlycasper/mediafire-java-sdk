package com.arkhive.components.api.contacts.fetch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


// CHECKSTYLE:OFF
import com.google.gson.Gson;
// CHECKSTYLE:ON
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import sun.misc.BASE64Decoder;

//{"root":{"a":{"@":{"b":{".":{"c":{"end":[0]}}}}}}} a@b.c

/**
 * raw auto complete trie.
 *
 * @author
 */
public class RawTrie {
    private final String data;
    private List<String> dataPointer;
    private final Node root;
    private final String directory;

    public RawTrie(String directory, String data) {
        if (directory == null || data == null) {
            throw new IllegalArgumentException("Cannot pass null as an argument for RawTrie() constructor");
        }
        this.directory = directory;
        this.data = decode(data);
        this.root = new Node("root");
        build(root, getResponseString(decode(directory)));
        createDataPointer(this.data);
    }

    /**
     * builds the trie recursively.
     *
     * @param node
     * @param responseString
     */
    @SuppressWarnings("unchecked")
    private void build(Node node, String responseString) {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        builder.disableHtmlEscaping();
        // Using Object.class because the response can have a JSON Object with a field name of any char value. Or the
        // response can have a JSON Array. So using Object so response string can be any JSON Object or Array with any
        // field name.
        // This returns a Map, and we only know that the key is a String, but the value could potentially be anything.
        // In the case of this response string, we know it can only be a String or Array.
        Object o = builder.create().fromJson(responseString, Object.class);

        // Map will contain either <String field, Json Object> or <String field, Json Array>
        Map<String, Object> map = (Map<String, Object>) o;

        // Adding nodes regardless of what Object is. Determine what it is later.
        // if Object is a Map, we know it's a JSON object, if it is an ArrayList we know this is the last leaf of this
        // branch in the trie.

        // for each child in json root node
        for (Map.Entry<String, Object> e : map.entrySet()) {
            // add new node to current node of trie if it doesn't exist
            Node newNode = new Node(e.getKey());
            node.addChild(newNode);
            // call this method again with json child and trie node just created as arguments
            if (e.getValue() instanceof Map) {
                String json = new Gson().toJson(e.getValue());
                build(newNode, json);
            } else {
                ArrayList<String> list = (ArrayList<String>) e.getValue();
                Node lastLeaf = new Node(list.toString());
                newNode.addChild(lastLeaf);
            }
        }
    }

    /**
     * creates a data pointer collection.
     *
     * @param str
     */
    private void createDataPointer(String str) {
        dataPointer = new LinkedList<String>();
        StringTokenizer st = new StringTokenizer(str, "|");
        while (st.hasMoreElements()) {
            String nextElement = (String) st.nextElement();
            dataPointer.add(nextElement);
        }
    }

    /**
     * lookup a value within a node starting at some index.
     *
     * @param term    - the string to lookup.
     * @param index   - should always pass 0 unless you have a good reason not to.
     * @param node    - the starting node (i.e. root node of this trie via getRoot()).
     * @param results - if you have a collection of nodes to add to, pass it, if not, pass null or create an empty list.
     * @return
     */
    public List<Node> lookup(String term, int index, Node node, List<Node> results) {
        if (results == null) {
            results = new LinkedList<Node>();
        }

        // all children of Node node must be matching term.
        if (index >= term.length()) {
            results.addAll(getLeaves(node, null));
            return results;
        }

        for (Node child : node.getChildren()) {
            String target = String.valueOf(term.charAt(index));
            if (child.getData().equalsIgnoreCase(target)) {
                lookup(term, index + 1, child, results);
            }
        }

        return results;
    }

    /**
     * converts an end point to a value. In other words, end points are in format "[##.#]" will convert to "##"
     *
     * @param nodeValue
     * @return
     * @throws Exception
     */
    public int convertEndNodeToPointer(String nodeValue) throws Exception {
        if (nodeValue.length() < 3) {
            throw new Exception("Invalid node value");
        } else if (!"[".equalsIgnoreCase(String.valueOf(nodeValue.charAt(0)))) {
            throw new Exception("Invalid node value");
        } else if (!"]".equalsIgnoreCase(String.valueOf(nodeValue.charAt(nodeValue.length() - 1)))) {
            throw new Exception("Invalid node value");
        }

        double rawVal = Double.parseDouble(nodeValue.substring(1, nodeValue.length() - 1));

        return (int) rawVal;
    }

    /**
     * retrieves all the leaves of this node.
     *
     * @param node
     * @param list
     * @return
     */
    public List<Node> getLeaves(Node node, List<Node> list) {
        if (list == null) {
            list = new LinkedList<Node>();
        }

        if (node.getChildren().size() == 0) {
            list.add(node);
            return list;
        }

        for (Node child : node.getChildren()) {
            getLeaves(child, list);
        }

        return list;
    }

    private String getResponseString(String response) {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(response);
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject().get("root").getAsJsonObject();
            return obj.toString();
        } else {
            return "";
        }
    }

    /**
     * retrieves the root node for this trie.
     *
     * @return
     */
    public Node getRoot() {
        return this.root;
    }

    /**
     * retrieves the raw data string for this trie.
     *
     * @return
     */
    public String getData() {
        return this.data;
    }

    /**
     * retrieves the raw directory string for this trie.
     *
     * @return
     */
    public String getDirectory() {
        return this.directory;
    }

    /**
     * retrieves the data pointer (decoded data object) for this trie.
     *
     * @return
     */
    public List<String> getDataPointer() {
        return this.dataPointer;
    }

    /**
     * passing a search term will search the trie for potential matches. Potential matches are returned as a colleciton.
     *
     * @param searchTerm - collection of Contact objects which match the search term.
     * @return
     */
    public List<Contact> getResultsForSearchTerm(String searchTerm) {
        List<Contact> contacts = new LinkedList<Contact>();

        List<Node> nodes = lookup(searchTerm, 0, getRoot(), null);

        List<String> dataPointer = getDataPointer();

        for (Node node : nodes) {
            int indexToPointAt = node.getDataAsInteger();
            Contact contact = new Contact(dataPointer.get(indexToPointAt));
            contacts.add(contact);
        }

        return contacts;
    }

    private String decode(String str) {
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            return new String(decoder.decodeBuffer(str));
        } catch (IOException e) {
            return new String(new byte[0]);
        }
    }
}
