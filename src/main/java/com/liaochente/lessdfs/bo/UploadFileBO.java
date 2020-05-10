package com.liaochente.lessdfs.bo;

/**
 * 上传文件数据实体
 */
public class UploadFileBO extends BaseBO {
    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
