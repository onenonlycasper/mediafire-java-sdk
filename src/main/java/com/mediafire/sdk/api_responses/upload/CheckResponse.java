package com.mediafire.sdk.api_responses.upload;

import com.mediafire.sdk.api_responses.ApiResponse;

import java.util.ArrayList;
import java.util.List;

public class CheckResponse extends ApiResponse {
    private String hash_exists;
    private String in_account;
    private String in_folder;
    private String file_exists;
    private String different_hash;
    private String duplicate_quickkey;
    private String available_space;
    private String used_storage_size;
    private String storage_limit;
    private String storage_limit_exceeded;
    ResumableUpload resumable_upload;

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

    public long getUsedStorageSize() {
        if ("".equals(used_storage_size)) {
            return 0;
        }
        return Long.parseLong(used_storage_size);
    }

    public long getStorageLimit() {
        if ("".equals(storage_limit)) {
            return 0;
        }
        return Long.parseLong(storage_limit);
    }

    public boolean getStorageLimitExceeded() {
        return "yes".equals(storage_limit_exceeded);
    }

    public ResumableUpload getResumableUpload() {
        if (resumable_upload == null) {
            return new ResumableUpload();
        }
        return resumable_upload;
    }

    public boolean doesHashExists() {
        return "yes".equals(hash_exists);
    }

    public boolean isInAccount() {
        return "yes".equals(in_account);
    }

    public boolean isInFolder() {
        return "yes".equals(in_folder);
    }

    public boolean doesFileExist() {
        return "yes".equals(file_exists);
    }

    public boolean isDifferentHash() {
        return "yes".equals(different_hash);
    }

    public String getDuplicateQuickkey() {
        if (duplicate_quickkey == null) {
            return "";
        }
        return this.duplicate_quickkey;
    }

    public long getAvailableSpace() {
        if (available_space == null) {
            return 0;
        }
        return Long.parseLong(this.available_space);
    }

    public enum Error {
        NON_OWNER_UPLOAD_WITHOUT_WRITE_PERMISSIONS_TO_FOLDER(114),
        NO_ERROR(0),;

        private final int value;

        private Error(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Error fromInt(int value) {
            for (final Error e : values()) {
                if (e.getValue() == value) {
                    return e;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            String response;
            switch (this.value) {
                case 114:
                    response = "Success";
                    break;
                default:
                    response = "No error code associated with: " + this.value;
                    break;
            }
            return response;
        }
    }
}
