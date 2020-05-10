package com.liaochente.lessdfs.protocol;

import com.liaochente.lessdfs.bo.BaseBO;
import com.liaochente.lessdfs.bo.LoginBO;

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
        String string = null;
        if (bo instanceof LoginBO) {
            string = ((LoginBO) bo).getPassword();
        } else {
            string = bo.toString();
        }
        return string;
    }
}
