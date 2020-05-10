package com.liaochente.lessdfs.bo;

import java.io.Serializable;

/**
 * 登录认证数据实体
 */
public class LoginBO extends BaseBO {
    private String username;

    private String pwd;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
