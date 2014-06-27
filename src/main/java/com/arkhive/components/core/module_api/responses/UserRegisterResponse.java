package com.arkhive.components.core.module_api.responses;



/**
 * response class for user registration.
 *
 * @author
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
