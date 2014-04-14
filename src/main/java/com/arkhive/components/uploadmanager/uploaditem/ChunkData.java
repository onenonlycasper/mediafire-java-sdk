package com.arkhive.components.uploadmanager.uploaditem;

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

    /**
     * Sole constructor for this data structure.
     * @param unitSize - unit size as received from pre upload
     * response
     * @param numberOfUnits - number of units as received from
     * pre upload response
     */
    public ChunkData(int unitSize, int numberOfUnits) {
        this.unitSize = unitSize;
        this.numberOfUnits = numberOfUnits;
    }

    /*============================
     * public getters
     *============================*/
    /**
     * Get the unit size for each chunk.
     * @return
     */
    public int getUnitSize() { return unitSize; }
    
    /**
     * Get the number of chunks.
     * @return
     */
    public int getNumberOfUnits() { return numberOfUnits; }
    
    /*============================
     * public setters
     *============================*/
    /**
     * sets the unit size to the passed value.
     * @param unitSize
     */
    public void setUnitSize(int unitSize) {
      this.unitSize = unitSize;
    }
    
    /**
     * sets the number of units to the passed value.
     * @param numberOfUnits
     */
    public void setNumberOfUnits(int numberOfUnits) {
      this.numberOfUnits = numberOfUnits;
    }
    
}
