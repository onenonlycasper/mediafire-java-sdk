package com.mediafire.sdk.uploader;

/**
* Created by  on 7/8/2014.
*/
class MFResumableChunkInfo {
    private String chunkHash;
    private byte[] uploadChunk;

    public MFResumableChunkInfo(String chunkHash, byte[] uploadChunk) {
        this.chunkHash = chunkHash;
        this.uploadChunk = uploadChunk;
    }

    public String getChunkHash() {
        return chunkHash;
    }

    public byte[] getUploadChunk() {
        return uploadChunk;
    }
}
