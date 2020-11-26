package com.zhongyou.meettvapplicaion.event;

/**
 * Created by wufan on 2017/8/3.
 */

public class CallEvent {
    boolean isCall ;
    String tvSocketId;

    public CallEvent(boolean isCall,String tvSocketId) {
        this.isCall = isCall;
        this.tvSocketId = tvSocketId;
    }

    public boolean isCall() {
        return isCall;
    }

    public void setCall(boolean call) {
        isCall = call;
    }

    public String getTvSocketId() {
        return tvSocketId;
    }

    public void setTvSocketId(String tvSocketId) {
        this.tvSocketId = tvSocketId;
    }
}
