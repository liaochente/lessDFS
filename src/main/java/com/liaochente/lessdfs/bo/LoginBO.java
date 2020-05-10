package com.liaochente.lessdfs.bo;

import java.io.Serializable;

/**
 * 登录认证数据实体
 */
public class LoginBO extends BaseBO {
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
