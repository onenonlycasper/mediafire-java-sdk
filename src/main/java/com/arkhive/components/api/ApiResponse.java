package com.arkhive.components.api;

/**
 * class to be extended by all api response classes. This class contains the common response fields.
 *
 * @author
 */
public class ApiResponse {
    private String action;
    private String message;
    private String result;
    private int error;
    //CHECKSTYLE:OFF
    private String current_api_version;
    //CHECKSTYLE:ON

    public String getAction() {
        if (action == null) {
            action = "";
        }
        return action;
    }

    public String getResult() {
        if (result == null) {
            result = "";
        }
        return result;
    }

    public String getMessage() {
        if (message == null) {
            message = "";
        }
        return message;
    }

    public String getCurrentApiVersion() {
        if (current_api_version == null) {
            current_api_version = "";
        }
        return current_api_version;
    }

    public ApiResponseCode getErrorCode() {
        return ApiResponseCode.fromInt(error);
    }

    public int getErrorNumber() {
        return error;
    }

    public boolean hasError() {
        return error != 0;
    }
}
