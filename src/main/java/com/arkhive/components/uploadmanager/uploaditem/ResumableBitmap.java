package com.arkhive.components.uploadmanager.uploaditem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * This data structure represents the bitmap
 * which is received from the pre upload response
 * which is made by calling api/upload/pre_upload.php.
 *
 * @author
 */
public class ResumableBitmap {
    private final int count;
    private List<Integer> words;
    private List<Boolean> uploadUnits;
    private final Logger logger = LoggerFactory.getLogger(ResumableBitmap.class);

    /**
     * Constructor given an int count and Collection of words.
     *
     * @param count
     * @param words
     */
    public ResumableBitmap(int count, List<Integer> words) {
        this.count = count;
        this.words = words;
        uploadUnits = decodeBitmap(count, words);
    }

    private List<Boolean> decodeBitmap(int count, List<Integer> words) {
        List<Boolean> uploadUnits = new LinkedList<Boolean>();

        //loop count times
        for (int i = 0; i < count; i++) {
            //convert words to binary string
            String word = Integer.toBinaryString(words.get(i));

            //ensure number is 16 bit by adding 0 until there are 16 bits
            while (word.length() < 16) {
                word = "0" + word;
            }

            //add boolean to collection depending on bit value
            for (int j = 0; j < word.length(); j++) {
                uploadUnits.add(i * 16 + j, word.charAt(15 - j) == '1');
            }
        }

        return uploadUnits;
    }

    public boolean isUploaded(int chunkId) {
        if (uploadUnits.isEmpty()) {
            return false;
        }
        return uploadUnits.get(chunkId);
    }

    public List<Boolean> getUploadUnits() {
        return uploadUnits;
    }

    public int getCount() {
        return count;
    }

    public List<Integer> getWords() {
        return words;
    }
}
