package com.liaochente.lessdfs.cache;

public class FileCacheItem {

    private String fileExt;

    private byte[] fileBytes;

    public FileCacheItem(byte[] fileBytes, String fileExt) {
        this.fileBytes = fileBytes;
        this.fileExt = fileExt;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public long getFileSize() {
        return this.fileBytes.length;
    }

    public String getFileExt() {
        return fileExt;
    }
}
