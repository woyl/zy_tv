package com.zhongyou.meettvapplicaion.event;

/**用户设置状态改变
 * Created by wufan on 2017/8/1.
 */

public class SetUserStateEvent {
    private boolean isOnline;

    public SetUserStateEvent(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
