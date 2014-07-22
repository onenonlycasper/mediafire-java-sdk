package com.mediafire.sdk.api_responses.upload;

import com.mediafire.sdk.api_responses.ApiResponse;

import java.util.ArrayList;
import java.util.List;

public class ResumableResponse extends ApiResponse {
    private DoUpload doupload;
    private ResumableUpload resumable_upload;

    public class DoUpload {
        private String result;
        private String key;

        public Result getResultCode() {
            if (result == null || result.isEmpty()) {
                return Result.fromInt(0);
            }
            return Result.fromInt(Integer.parseInt(result));
        }

        public int getResult() {
            if (result == null || result.isEmpty()) {
                return -1;
            }
            return Integer.parseInt(result);
        }

        public String getPollUploadKey() {
            if (key == null) {
                return "";
            }
            return key;
        }
    }

    public class ResumableUpload {
        private String all_units_ready;
        private String number_of_units;
        private String unit_size;
        private Bitmap bitmap;

        public boolean areAllUnitsReady() {
            return "yes".equals(all_units_ready);
        }

        public int getNumberOfUnits() {
            if (number_of_units == null || number_of_units.isEmpty()) {
                return 0;
            }
            return Integer.parseInt(number_of_units);
        }

        public int getUnitSize() {
            if (unit_size == null || unit_size.isEmpty()) {
                return 0;
            }
            return Integer.parseInt(unit_size);
        }

        public Bitmap getBitmap() {
            if (bitmap == null) {
                return new Bitmap();
            }
            return bitmap;
        }

        public class Bitmap {
            private String count;
            private String[] words;

            public int getCount() {
                if (count == null || count.isEmpty()) {
                    return 0;
                }
                return Integer.parseInt(count);
            }

            public List<Integer> getWords() {
                if (words == null || words.length == 0) {
                    return new ArrayList<Integer>();
                }
                return convert(words);
            }

            private List<Integer> convert(String[] words) {
                List<Integer> ret = new ArrayList<Integer>();
                for (String str : words) {
                    ret.add(Integer.parseInt(str));
                }

                if (ret.size() == words.length) {
                    return ret;
                } else {
                    return new ArrayList<Integer>();
                }
            }
        }
    }

    public DoUpload getDoUpload() {
        if (doupload == null) {
            return new DoUpload();
        }
        return doupload;
    }

    public ResumableUpload getResumableUpload() {
        if (resumable_upload == null) {
            return new ResumableUpload();
        }
        return resumable_upload;
    }

    public enum Result {
        NO_ERROR(0),
        SUCCESS_FILE_MOVED_TO_ROOT(14),
        DROPBOX_KEY_INVALID_1(-1),
        DROPBOX_KEY_INVALID_2(-8),
        DROPBOX_KEY_INVALID_3(-11),
        INVALID_DROPBOX_CONFIG_1(-21),
        INVALID_DROPBOX_CONFIG_2(-22),
        UNKOWN_UPLOAD_ERROR_1(-31),
        UNKOWN_UPLOAD_ERROR_2(-40),
        MISSING_FILE_DATA(-32),
        UPLOAD_EXCEEDS_UPLOAD_MAX_FILESIZE(-41),
        UPLOAD_EXCEEDS_MAX_FILE_SIZE_SPECIFIED_IN_HTML_FORM(-42),
        UPLOAD_FILE_ONLY_PARTIALLY_UPLOADED(-43),
        NO_FILE_UPLOADED(-44),
        MISSING_TEMPORARY_FOLDER(-45),
        FAILED_TO_WRITE_FILE_TO_DISK(-46),
        PHP_EXTENSION_STOPPED_UPLOAD(-47),
        INVALID_FILE_SIZE(-48),
        MISSING_FILE_NAME(-49),
        FILE_SIZE_DOES_NOT_MATCH_SIZE_ON_DISK(-51),
        HASH_SENT_MISMATCH_ACTUAL_FILE_HASH(-90),
        MISSING_OR_INVALID_SESSION_TOKEN(-99),
        INVALID_QUICKKEY_OR_FILE_DOES_NOT_BELONG_TO_SESSION_USER(-203),
        USER_DOES_NOT_HAVE_WRITE_PERMISSIONS_FOR_THIS_FILE(-204),
        USER_DOES_NOT_HAVE_WRITE_PERMISSION_FOR_DESTINATION_FOLDER(-205),
        ATTEMPTING_RESUMABLE_UPLOAD_UNIT_UPLOAD_BEFORE_CALLING_PRE_UPLOAD(-302),
        INVALID_UNIT_SIZE(-303),
        INVALID_UNIT_HASH(-304),
        MAXIMUM_FILE_SIZE_FOR_FREE_USERS_EXCEEDED_1(-701),
        MAXIMUM_FILE_SIZE_FOR_FREE_USERS_EXCEEDED_2(-881),
        MAXIMUM_FILE_SIZE_EXCEEDED_1(-700),
        MAXIMUM_FILE_SIZE_EXCEEDED_2(-882),
        INTERNAL_SERVER_ERROR_1(-10),
        INTERNAL_SERVER_ERROR_2(-12),
        INTERNAL_SERVER_ERROR_3(-26),
        INTERNAL_SERVER_ERROR_4(-50),
        INTERNAL_SERVER_ERROR_5(-52),
        INTERNAL_SERVER_ERROR_6(-53),
        INTERNAL_SERVER_ERROR_7(-54),
        INTERNAL_SERVER_ERROR_8(-70),
        INTERNAL_SERVER_ERROR_9(-71),
        INTERNAL_SERVER_ERROR_10(-80),
        INTERNAL_SERVER_ERROR_11(-120),
        INTERNAL_SERVER_ERROR_12(-122),
        INTERNAL_SERVER_ERROR_13(-124),
        INTERNAL_SERVER_ERROR_14(-140),
        INTERNAL_SERVER_ERROR_15(-200),
        INTERNAL_SERVER_ERROR_16(-301),;

        private final int value;

        private Result(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Result fromInt(int value) {
            for (final Result e : values()) {
                if (e.getValue() == value) {
                    return e;
                }
            }
            throw new IllegalArgumentException("Return code out of range : " + value);
        }

        @Override
        public String toString() {
            String returnMessage;
            switch (this.value) {
                case 0:
                    returnMessage = "Success";
                    break;
                case 14:
                    returnMessage = "Upload succeeded but the folder specified does not exist, "
                            + "so the file was placed in the root folder";
                    break;
                case -1:
                case -8:
                case -11:
                    returnMessage = "Dropbox key is invalid";
                    break;
                case -21:
                case -22:
                    returnMessage = "Invalid Dropbox Configuration";
                    break;
                case -31:
                case -40:
                    returnMessage = "Unknown upload error";
                    break;
                case -32:
                    returnMessage = "Missing file Data";
                    break;
                case -41:
                    returnMessage = "The uploaded file exceeds the upload_max_filesize";
                    break;
                case -42:
                    returnMessage = "The uploaded file exceeds the MAX_FILE_SIZE directive "
                            + "that was specified in the HTML form";
                    break;
                case -43:
                    returnMessage = "The uploaded file was only partially uploaded";
                    break;
                case -44:
                    returnMessage = "No file was uploaded";
                    break;
                case -45:
                    returnMessage = "Missing a temporary folder";
                    break;
                case -46:
                    returnMessage = "Failed to write file to disk";
                    break;
                case -47:
                    returnMessage = "A PHP extension stopped the file upload";
                    break;
                case -48:
                    returnMessage = "Invalid file size";
                    break;
                case -49:
                    returnMessage = "Missing file name";
                    break;
                case -51:
                    returnMessage = "File size does not match size on disk";
                    break;
                case -90:
                    returnMessage = "The Hash sent mismatch the actual file hash";
                    break;
                case -99:
                    returnMessage = "Missing or invalid session token";
                    break;
                case -203:
                    returnMessage = "Invalid Quickkey or File does not belong to session user";
                    break;
                case -204:
                    returnMessage = "User does not have write permissions to this file";
                    break;
                case -205:
                    returnMessage = "User does not have write permissions to the destination folder";
                    break;
                case -302:
                    returnMessage = "Attempting a resumable upload unit upload before "
                            + "calling upload/pre_upload API";
                    break;
                case -303:
                    returnMessage = "Invalid unit size";
                    break;
                case -304:
                    returnMessage = "Invalid unit hash";
                    break;
                case -701:
                case -881:
                    returnMessage = "Maximum file size for free users exceeded";
                    break;
                case -700:
                case -882:
                    returnMessage = "Maximum file size exceeded";
                    break;
                case -10:
                case -12:
                case -26:
                case -50:
                case -52:
                case -53:
                case -54:
                case -70:
                case -71:
                case -80:
                case -120:
                case -122:
                case -124:
                case -140:
                case -200:
                case -301:
                    returnMessage = "Internal Server Errors";
                    break;
                default:
                    throw new IllegalArgumentException("Return code out of range : " + this.value);
            }
            return returnMessage;
        }
    }

}

