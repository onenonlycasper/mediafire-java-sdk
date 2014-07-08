package com.arkhive.components.core.module_api.responses;

import java.util.ArrayList;
import java.util.List;

public class UploadCheckResponse extends ApiResponse {
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
}
