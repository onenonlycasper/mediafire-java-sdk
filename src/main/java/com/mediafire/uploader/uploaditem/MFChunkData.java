package com.mediafire.uploader.uploaditem;

public class MFChunkData {
    private int unitSize;
    private int numberOfUnits;

    public MFChunkData() {
        unitSize = 0;
        numberOfUnits = 0;
    }

    public int getUnitSize() {
        return unitSize;
    }

    public int getNumberOfUnits() {
        return numberOfUnits;
    }

    public void setUnitSize(int unitSize) {
        this.unitSize = unitSize;
    }

    public void setNumberOfUnits(int numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }

}
