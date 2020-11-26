package com.zhongyou.meettvapplicaion.network;

/**
 * Created by lichuanbei on 2016/10/26.
 *
 * 网络常量定义
 */

public class ApiConstants {
    /**
     * 成功
     */
    public static final int SUCCESS = 1000;


    /**
     * 成功，有数据
     */
    public static final int SUCCESS_DATA = 1001;

    /**
     * 挤下线——被踢出
     */
    public static final int SIGN_OUT = 9999;



    /**
     * 未知错误
     */
    public static final int UNKNOWN = -1;
    /**
     * 解析错误
     */
    public static final int PARSE_ERROR = -2;
    /**
     * 网络错误
     */
    public static final int NETWORD_ERROR = -3;
    /**
     * 协议出错
     */
    public static final int HTTP_ERROR = -4;

}
