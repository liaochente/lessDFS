package com.liaochente.lessdfs.protocol;

import com.liaochente.lessdfs.constant.LessConfig;
import com.liaochente.lessdfs.constant.LessStatus;

import java.io.Serializable;

/**
 * 协议头部实体
 */
public class LessMessageHeader implements Serializable {

    /**
     * 文件头，固定字符
     */
    private int magicCode;

    private long sessionId;

    private LessMessageType type;

    private byte priority;

    private LessStatus status;

    private String password;

    public LessMessageHeader() {
    }

    public LessMessageHeader(Integer magicCode, Long sessionId, LessMessageType type, Byte priority, LessStatus status, String password) {
        this.magicCode = magicCode;
        this.sessionId = sessionId;
        this.type = type;
        this.priority = priority;
        this.password = password;
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LessStatus getStatus() {
        return status;
    }

    public void setStatus(LessStatus status) {
        this.status = status;
    }

    public int getMagicCode() {
        return magicCode;
    }

    public void setMagicCode(int magicCode) {
        this.magicCode = magicCode;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public LessMessageType getType() {
        return type;
    }

    public void setType(LessMessageType type) {
        this.type = type;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "[magicCode:" + magicCode + ", sessionId:" + sessionId + ", type:" + type + ", priority:" + priority + ", password:" + password + "]";
    }
}
