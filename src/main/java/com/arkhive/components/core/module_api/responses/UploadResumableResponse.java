package com.arkhive.components.core.module_api.responses;

import com.arkhive.components.core.module_api.codes.ResumableResultCode;

import java.util.ArrayList;
import java.util.List;

/**
 * This data structure represents response received by the response received from api call /api/upload/resumable.php.
 *
 * @author
 */
public class UploadResumableResponse extends ApiResponse {
    private String server;
    private DoUpload doupload;
    private ResumableUpload resumable_upload;

    public class DoUpload {
        private String result;
        private String key;

        public ResumableResultCode getResultCode() {
            if (result == null || result.isEmpty()) {
                return ResumableResultCode.fromInt(0);
            }
            return ResumableResultCode.fromInt(Integer.parseInt(result));
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

    public String getServer() {
        if (server == null) {
            return "";
        }
        return server;
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
}

