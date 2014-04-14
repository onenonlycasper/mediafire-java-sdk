package com.arkhive.components.api.contacts;

import java.util.HashMap;
import java.util.Map;

/** Value object for holding a contact information. */
public class Contact {

    private ContactVO contactVO;

    private Contact(ContactVO contactVO) { this.contactVO = contactVO; }

    public Map<String, String> getParameterList() {
        Map<String, String> parameters = new HashMap<String, String>();
        if (contactVO.contactType != null) { parameters.put("contact_type", contactVO.contactType); }
        if (contactVO.contactKey != null) { parameters.put("contact_key", contactVO.contactKey); }
        if (contactVO.displayName != null) { parameters.put("display_name", contactVO.displayName); }
        if (contactVO.firstName != null) { parameters.put("first_name", contactVO.firstName); }
        if (contactVO.lastName != null) { parameters.put("last_name", contactVO.lastName); }
        if (contactVO.avatar != null) { parameters.put("avatar", contactVO.avatar); }
        if (contactVO.sourceId != null) { parameters.put("source_id", contactVO.sourceId); }
        if (contactVO.email != null) { parameters.put("email", contactVO.email); }
        if (contactVO.phone != null) { parameters.put("phone", contactVO.phone); }
        if (contactVO.birthdate != null) { parameters.put("birthdate", contactVO.birthdate); }
        if (contactVO.location != null) { parameters.put("location", contactVO.location); }
        if (contactVO.gender != null) { parameters.put("gender", contactVO.gender); }
        if (contactVO.website != null) { parameters.put("website", contactVO.website); }
        if (contactVO.groupId != null) { parameters.put("group_id", contactVO.groupId); }
        return parameters;
    }

    private static class ContactVO {
        String contactType;
        String contactKey;
        String displayName;
        String firstName;
        String lastName;
        String avatar;
        String sourceId;
        String email;
        String phone;
        String birthdate;
        String location;
        String website;
        String gender;
        String groupId;
    }

    /** Builder for a contact object. */
    public static class Builder {
        private final ContactVO contact = new ContactVO();

        public Builder contactType(String value) {
            if (value != null) { contact.contactType = value; }
            return this;
        }

        public Builder contactKey(String value) {
            if (value != null) { contact.contactKey = value; }
            return this;
        }

        public Builder displayName(String value) {
            if (value != null) { contact.displayName = value; }
            return this;
        }

        public Builder firstName(String value) {
            if (value != null) { contact.firstName = value; }
            return this;
        }

        public Builder lastName(String value) {
            if (value != null) { contact.lastName = value; }
            return this;
        }

        public Builder avatar(String value) {
            if (value != null) { contact.avatar = value; }
            return this;
        }

        public Builder sourceId(String value) {
            if (value != null) { contact.sourceId = value; }
            return this;
        }

        public Builder email(String value) {
            if (value != null) { contact.email = value; }
            return this;
        }

        public Builder phone(String value) {
            if (value != null) { contact.phone = value; }
            return this;
        }

        public Builder birthdate(String value) {
            if (value != null) { contact.birthdate = value; }
            return this;
        }

        public Builder location(String value) {
            if (value != null) { contact.location = value; }
            return this;
        }

        public Builder gender(String value) {
            if (value != null) { contact.gender = value; }
            return this;
        }

        public Builder website(String value) {
            if (value != null) { contact.website = value; }
            return this;
        }

        public Builder groupId(String value) {
            if (value != null) { contact.groupId = value; }
            return this;
        }

        public Contact build() {
            return new Contact(contact);
        }
    }
}
