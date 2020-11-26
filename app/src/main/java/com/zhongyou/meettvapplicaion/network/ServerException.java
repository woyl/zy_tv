package com.zhongyou.meettvapplicaion.network;

/**
 * Created by lichuanbei on 2016/10/26.
 */

public class ServerException extends RuntimeException {
    public int code;
    public String msg;

    public ServerException(int code, String msg){
        this.code=code;
        this.msg=msg;
    }
}