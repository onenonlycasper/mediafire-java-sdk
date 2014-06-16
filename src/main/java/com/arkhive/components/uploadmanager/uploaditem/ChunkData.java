package com.arkhive.components.uploadmanager.uploaditem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data structure used within UploadItem.
 * This data structure stores the unit size
 * and number of units. These values should
 * only be received after getting a response
 * from calling pre upload.
 * @author Chris Najar
 *
 */
public class ChunkData {
    private int unitSize;
    private int numberOfUnits;
    private final Logger logger = LoggerFactory.getLogger(ChunkData.class);

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
     * @return
     */
    public int getUnitSize() {
        System.out.println("getUnitSize()");
        System.out.println("RETURNING UNIT SIZE: " + unitSize);
        return unitSize;
    }

    /**
     * Get the number of chunks.
     * @return
     */
    public int getNumberOfUnits() {
        System.out.println("getNumberOfUnits()");
        System.out.println("RETURNING NUMBER OF UNITS: " + numberOfUnits);
        return numberOfUnits;
    }
    
    /*============================
     * public setters
     *============================*/
    /**
     * sets the unit size to the passed value.
     * @param unitSize
     */
    public void setUnitSize(int unitSize) {
        System.out.println("setUnitSize()");
        this.unitSize = unitSize;
        System.out.println("UNIT SIZE SET TO: " + this.unitSize);
    }

    /**
     * sets the number of units to the passed value.
     * @param numberOfUnits
     */
    public void setNumberOfUnits(int numberOfUnits) {
        System.out.println("setNumberOfUnits()");
        this.numberOfUnits = numberOfUnits;
        System.out.println("NUMBER OF UNITS SET TO: " + this.numberOfUnits);
    }

}
