package com.arkhive.components.api.upload.responses;

import java.util.ArrayList;
import java.util.List;

import com.arkhive.components.api.ApiResponse;

/**
 * This class represents the data structure received (response) by a call to /api/upload/check.php.
 * @author Chris Najar
 *
 */
public class CheckResponse extends ApiResponse {
  //CHECKSTYLE:OFF field name must match field from API.
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
  //CHECKSTYLE:ON
  
  /**This class represents part of the data structure for the pre-upload response.
   * @author Chris Najar
   */
  public class ResumableUpload {
    //CHECKSTYLE:OFF
    private String all_units_ready;
    private String number_of_units;
    private String unit_size;
    //CHECKSTYLE:ON
    private Bitmap bitmap;

    public boolean getAllUnitsReady() {
      if (all_units_ready == null) { return false; }
      if (all_units_ready.equals("yes")) {
        return true;
      } else {
        return false;
      }
    }
    public int getNumberOfUnits() {
      if (number_of_units == null || number_of_units.equals("")) { return 0; }
      return Integer.parseInt(number_of_units);
    }
    public int getUnitSize() {
      if (unit_size == null || unit_size.equals("")) { return 0; }
      return Integer.parseInt(unit_size);
    }
    public Bitmap getBitmap() {
      if (bitmap == null) { return new Bitmap(); }
      return bitmap;
    }
  }

  /**This class represents part of the data structure received from upload/check.php response.
   * @author Chris Najar
   */
  public class Bitmap {
    private String count;
    private String[] words;

    public int getCount() {
      if (count == null || count.equals("")) { return 0; }
      return Integer.parseInt(count);
    }

    public List<Integer> getWords() {
      if (words == null || words.length == 0) { return new ArrayList<Integer>(); }
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
        return ret = new ArrayList<Integer>();
      }
    }
  }
  
  public long getUsedStorageSize() {
    if (used_storage_size == null || used_storage_size.equals("")) { return 0; }
    return Long.parseLong(used_storage_size);
  }
  public long getStorageLimit() {
    if (storage_limit == null || storage_limit.equals("")) { return 0; }
    return Long.parseLong(storage_limit);
  }
  public boolean getStorageLimitExceeded() {
    if (storage_limit_exceeded == null) { return false; }
    if (storage_limit_exceeded.equals("yes")) {
      return true;
    } else {
      return false;
    }
  }
  public ResumableUpload getResumableUpload() {
    if (resumable_upload == null) { return new ResumableUpload(); }
    return resumable_upload;
  }

  public boolean getHashExists() {
    if (hash_exists == null) { return false; }
    if (hash_exists.equals("yes")) {
      return true;
    } else {
      return false;
    }
  }
  public boolean getInAccount() {    
    if (in_account == null) { return false; }
    if (in_account.equals("yes")) {
      return true;
    } else {
      return false;
    }
  }
  public boolean getInFolder() {
    if (in_folder == null) { return false; }
    if (in_folder.equals("yes")) {
      return true;
    } else {
      return false;
    }
  }
  public boolean getFileExists() {
    if (file_exists == null) { return false; }
    if (file_exists.equals("yes")) {
      return true;
    } else {
      return false;
    }
  }
  public boolean getDifferentHash() {
    if (different_hash == null) { return false; }
    if (different_hash.equals("yes")) {
      return true;
    } else {
      return false;
    }
  }
  public String getDuplicateQuickkey() {
    if (duplicate_quickkey == null) { return  ""; }
    return this.duplicate_quickkey;
  }
  public long getAvailableSpace() {
    if (available_space == null) { return 0; }
    return Long.parseLong(this.available_space);
  }
}
