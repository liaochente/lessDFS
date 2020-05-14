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

    private byte[] placeholder;

    public LessMessageHeader() {
    }

    public LessMessageHeader(Integer magicCode, Long sessionId, LessMessageType type, Byte priority, LessStatus status, byte[] placeholder) {
        this.magicCode = magicCode;
        this.sessionId = sessionId;
        this.type = type;
        this.priority = priority;
        this.placeholder = placeholder;
        this.status = status;
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

    public byte[] getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(byte[] placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public String toString() {
        return "[magicCode:" + magicCode + ", sessionId:" + sessionId + ", type:" + type + ", priority:" + priority + ", placeholder:" + placeholder + "|" + (placeholder == null ? "0" : placeholder.length) + "]";
    }
}
