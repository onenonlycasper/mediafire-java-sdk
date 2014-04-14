package com.arkhive.components.api.contacts.fetch;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**Contact representing a contact in the data string portion of the RawTrie data structure.
 * @author Chris Najar
 */
public class Contact {
  private String data;
  private String email;
  private String id;
  private String name;
  private String avatar;
  private String type;
  
  public Contact(String data) {
    this.data = data;
    parseContact();
  }
  
  /**
   * returns the raw data for this contact.
   * @return
   */
  public String getData() {
    if (this.data == null) {
      this.data = "";
    }
    return this.data;
  }

  /**
   * returns the contact email.
   * @return
   */
  public String getEmail() {
    if (this.email == null) {
      this.email = "";
    }
    return this.email;
  }

  /**
   * returns the contact id.
   * @return
   */
  public String getId() {
    if (this.id == null) {
      this.id = "";
    }
    return this.id;
  }

  /**
   * returns the contact name (or email if no name provided).
   * @return
   */
  public String getName() {
    if (this.name == null) {
      this.name = "";
    }
    return this.name;
  }

  /**
   * returns the avatar url.
   * @return
   */
  public String getAvatar() {
    if (this.avatar == null) {
      this.avatar = "";
    }
    return this.avatar;
  }

  /**
   * returns the contact type.
   * @return
   */
  public String getType() {
    if (this.type == null) {
      this.type = "";
    }
    return this.type;
  }

  /**
   * populates the fields for a contact using raw trie format.
   */
  private void parseContact() {
    StringTokenizer st = new StringTokenizer(data, "/");
    List<String> fields = new LinkedList<String>();
    while (st.hasMoreElements()) {
      String field = (String) st.nextElement();
//      System.out.println("field: " + field);
      fields.add(field);
    }
    
    if (fields.size() != 5) {
//      System.out.println("something went wrong, field size incorrect");
      return;
    }
    
    email = fields.get(0);
    id = fields.get(1);
    name = fields.get(2);
    avatar = fields.get(3);
    type = fields.get(4);
  }
}
