package com.liaochente.lessdfs.protocol;

import com.liaochente.lessdfs.protocol.body.data.BaseBO;

import java.io.Serializable;

public class LessMessageBody implements Serializable {
    private BaseBO bo;

    public BaseBO getBo() {
        return bo;
    }

    public void setBo(BaseBO bo) {
        this.bo = bo;
    }

    @Override
    public String toString() {
        return bo.toString();
    }
}
