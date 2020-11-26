package com.zhongyou.meettvapplicaion.event;

public class PublishEvent {

    private String msg;

    public PublishEvent(String msg){
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
