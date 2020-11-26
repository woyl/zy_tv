package com.zhongyou.meettvapplicaion.entities;

/**
 * Created by wufan on 2017/7/20.
 */

public class LoginWechat {


    /**
     * wechat : {"id":"706d61593ca14623b3aad55d446759c6"}
     * user : {"id":"e414d731b8284431a0692e39dd11c95d"}
     */

    private Wechat wechat;
    private User user;

    public Wechat getWechat() {
        return wechat;
    }

    public void setWechat(Wechat wechat) {
        this.wechat = wechat;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

   
}
