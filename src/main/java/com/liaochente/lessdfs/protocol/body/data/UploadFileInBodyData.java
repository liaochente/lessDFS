package com.liaochente.lessdfs.protocol.body.data;

public class UploadFileInBodyData extends BaseBO {
    private String fileName;

    private String fileExt;

    private byte[] data;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "[fileName=" + fileName + ", fileExt=" + fileExt + ", data=" + data + "|" + data.length + "]";
    }
}
