package com.liaochente.lessdfs.protocol;

import java.io.Serializable;

/**
 * 协议头部实体
 */
public class LessMessageHeader implements Serializable {

    /**
     * 文件头，固定字符
     */
    private Integer magicCode = 0x76;

    /**
     * 消息体长度
     */
    private Integer length;

    private Long sessionId;

    private LessMessageType type;

    private Byte priority;

    private String fileExt;

    public LessMessageHeader() {
    }

    public LessMessageHeader(Integer magicCode, Integer length, Long sessionId, LessMessageType type, Byte priority, String fileExt) {
        this.magicCode = magicCode;
        this.length = length;
        this.sessionId = sessionId;
        this.type = type;
        this.priority = priority;
        this.fileExt = fileExt;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public Integer getMagicCode() {
        return magicCode;
    }

    public void setMagicCode(Integer magicCode) {
        this.magicCode = magicCode;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public LessMessageType getType() {
        return type;
    }

    public void setType(LessMessageType type) {
        this.type = type;
    }

    public Byte getPriority() {
        return priority;
    }

    public void setPriority(Byte priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "[magicCode:" + magicCode + ", length:" + length + ", sessionId:" + sessionId + ", type:" + type + ", priority:" + priority + "]";
    }
}
