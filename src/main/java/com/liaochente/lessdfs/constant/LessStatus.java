package com.liaochente.lessdfs.constant;

import com.liaochente.lessdfs.protocol.LessMessageType;

public enum LessStatus {

    FAIL(200, "请求失败"),
    OK(0, "请求成功");

    private LessStatus(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public final static LessStatus convert(int status) {
        LessStatus[] lessStatuses = LessStatus.values();
        for (LessStatus lessStatus : lessStatuses) {
            if(lessStatus.status == status) {
                return lessStatus;
            }
        }
        return null;
    }

    private int status;

    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
