package com.arkhive.components.api.user;

import com.arkhive.components.api.ApiResponse;

/**
 * response class for user registration.
 * @author Chris Najar
 *
 */
public class UserRegisterResponse extends ApiResponse {
  String email;
  String created;
  
  public String getEmail() {
    if (email == null) {
      email = "";
    }
    return email;
  }
  
  public String getCreated() {
    if (created == null) {
      created = "";
    }
    return created;
  }
}
