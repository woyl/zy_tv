package com.zhongyou.meettvapplicaion.network;

/**
 * Created by lichuanbei on 2016/10/26.
 *
 * 自定义异常类：参考博文：http://blog.csdn.net/jdsjlzx/article/details/51882661
 */

public class ApiException extends Exception {
    public int code;
    public String msg;

    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;

    }
}