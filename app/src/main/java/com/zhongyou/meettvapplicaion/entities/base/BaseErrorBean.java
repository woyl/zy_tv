package com.zhongyou.meettvapplicaion.entities.base;

import android.text.TextUtils;

import com.zhongyou.meettvapplicaion.utils.ToastUtils;


/**
 * http 返回200以外不解析业务bean,只解析错误数据
 * Created by wufan on 2016/12/20.
 * bean基类
 */

public class BaseErrorBean {
    private int errcode;
    private String errmsg;
    //{"errcode": 40003, "errmsg": "拒绝访问"}
    public static final int ERRCODE_TOKEN_ERROR = 40003;
    //{"errcode": 40001, "errmsg": "无访问权限"}//token格式错误,没token前缀
    public static final int ERRCODE_TOKEN_ERROR_FORMAT_40001 = 40001;

    public boolean isSuccess() {
        return errcode == 0;
    }

    public boolean isTokenError() {
        return errcode == ERRCODE_TOKEN_ERROR || errcode == ERRCODE_TOKEN_ERROR_FORMAT_40001;
    }

    public static boolean isTokenError(int errcode) {
        return errcode == ERRCODE_TOKEN_ERROR;
    }

    public void showMsg() {
        if (!TextUtils.isEmpty(errmsg)) {
            ToastUtils.showToast(errmsg);
        } else {
            ToastUtils.showToast("服务器返回错误Errmsg null");
        }
    }


    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

}
