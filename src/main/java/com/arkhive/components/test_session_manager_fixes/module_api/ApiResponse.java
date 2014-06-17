package com.arkhive.components.test_session_manager_fixes.module_api;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public class ApiResponse {
    private String action;
    private String message;
    private String result;
    private String error;
    private String current_api_version;
    private String new_key;
    private String time;

    public final String getAction() {
        return action;
    }

    public final String getMessage() {
        return message;
    }

    public final int getError() {
        int intValueOfError;
        if (error == null) {
            intValueOfError = 0;
        } else {
            intValueOfError = Integer.valueOf(error);
        }
        return intValueOfError;
    }

    public final String getResult() {
        return result;
    }

    public final String getCurrentApiVersion() {
        return current_api_version;
    }

    public final boolean hasError() {
        return error == null;
    }

    public final String getTime() {
        return time;
    }

    public boolean needNewKey() {
        return new_key != null && "yes".equals(new_key);
    }
}
