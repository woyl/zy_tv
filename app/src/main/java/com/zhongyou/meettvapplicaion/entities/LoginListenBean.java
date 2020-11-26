package com.zhongyou.meettvapplicaion.entities;

/**
 * Created by yuna on 2017/7/24.
 */

public class LoginListenBean {

    private  boolean login;
    private String token;

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
