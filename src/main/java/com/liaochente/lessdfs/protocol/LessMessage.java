package com.liaochente.lessdfs.protocol;

import java.io.Serializable;

/**
 * 协议实体
 */
public class LessMessage implements Serializable {
    private LessMessageHeader header;

    private LessMessageBody body;

    public LessMessage() {
    }

    public LessMessage(Integer magic, Integer length, Long sessionId, Integer type, Byte priority, byte[] bodyData) {
        header = new LessMessageHeader(magic, length, sessionId, LessMessageType.convert(type), priority);
    }

    public LessMessageHeader getHeader() {
        return header;
    }

    public void setHeader(LessMessageHeader header) {
        this.header = header;
    }

    public LessMessageBody getBody() {
        return body;
    }

    public void setBody(LessMessageBody body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "header = " + header + " | body = " + body;
    }
}
