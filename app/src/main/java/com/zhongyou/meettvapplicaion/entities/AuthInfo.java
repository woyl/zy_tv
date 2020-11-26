package com.zhongyou.meettvapplicaion.entities;

/**
 * Created by yuna on 2017/7/27.
 */

public class AuthInfo {

    private Device device;
    private Wechat wechat;
    private User user;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

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
