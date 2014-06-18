package com.arkhive.components.uploadmanager.uploaditem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data structure used within UploadItem.
 * This data structure stores the unit size
 * and number of units. These values should
 * only be received after getting a response
 * from calling pre upload.
 *
 * @author
 */
public class ChunkData {
    private int unitSize;
    private int numberOfUnits;

    /**
     * Sole constructor for this data structure.
     */
    public ChunkData() {
        unitSize = 0;
        numberOfUnits = 0;
    }

    /*============================
     * public getters
     *============================*/

    /**
     * Get the unit size for each chunk.
     *
     * @return
     */
    public int getUnitSize() {
        return unitSize;
    }

    /**
     * Get the number of chunks.
     *
     * @return
     */
    public int getNumberOfUnits() {
        return numberOfUnits;
    }
    
    /*============================
     * public setters
     *============================*/

    /**
     * sets the unit size to the passed value.
     *
     * @param unitSize
     */
    public void setUnitSize(int unitSize) {
        this.unitSize = unitSize;
    }

    /**
     * sets the number of units to the passed value.
     *
     * @param numberOfUnits
     */
    public void setNumberOfUnits(int numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }

}
