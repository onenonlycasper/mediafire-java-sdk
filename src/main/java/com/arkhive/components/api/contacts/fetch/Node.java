package com.arkhive.components.api.contacts.fetch;

import java.util.LinkedList;
import java.util.List;

/**
 * nodes.
 * @author Chris Najar
 *
 */
public class Node {
  private String data;
  private List<Node> children;

  public Node(String data) {
    this.data = data;
    children = new LinkedList<Node>();
  }

  /**
   * gets the data value stored in this node.
   * @return
   */
  public String getData() {
    return data;
  }

  /**
   * gets the children of this node.
   * @return
   */
  public List<Node> getChildren() {
    return children;
  }

  /**
   * adds a child node to this node.
   * @param node
   * @return
   */
  public boolean addChild(Node node) {
      return !children.contains(node) && children.add(node);
  }

  public String toString() {
    return "[" + data + "] [" + children.size() + "]";
  }

  /**
   * returns this node data as an integer.
   * @return
   */
  public int getDataAsInteger() {
    if (!(data.contains("[") && data.contains("]"))) {
      throw new IllegalStateException("can't call this method if this isn't an end node");
    }
//    System.out.println("getDataAsInteger() called");
    String cleaned = data.replace("[", "");
    cleaned = cleaned.replace("]", "");

    double dbl = Double.valueOf(cleaned);

    return (int) dbl;
  }
}
