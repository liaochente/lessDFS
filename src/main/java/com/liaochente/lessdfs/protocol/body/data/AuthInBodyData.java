package com.liaochente.lessdfs.protocol.body.data;

public class AuthInBodyData extends BaseBO {
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "[password = " + password + "]";
    }
}
