package com.arkhive.components.uploadmanager.process;

/**
* Created by  on 7/8/2014.
*/
class ResumableChunkInfo {
    private Exception exception;
    private String chunkHash;
    private byte[] uploadChunk;

    public ResumableChunkInfo(String chunkHash, byte[] uploadChunk) {
        this.chunkHash = chunkHash;
        this.uploadChunk = uploadChunk;
    }

    public ResumableChunkInfo(Exception exception) {
        this.exception = exception;
    }

    public boolean hasException() {
        return exception != null;
    }

    public Exception getException() {
        return exception;
    }

    public String getChunkHash() {
        return chunkHash;
    }

    public byte[] getUploadChunk() {
        return uploadChunk;
    }
}
