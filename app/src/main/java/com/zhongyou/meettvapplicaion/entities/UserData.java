package com.zhongyou.meettvapplicaion.entities;

/**token获取用户信息
 * Created by wufan on 2017/7/20.
 */

public class UserData {


    /**
     * wechat : {"id":"706d61593ca14623b3aad55d446759c6"}
     * user : {"id":"e414d731b8284431a0692e39dd11c95d"}
     */

    private Wechat wechat;
    private User user;
    private Device device;

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

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
