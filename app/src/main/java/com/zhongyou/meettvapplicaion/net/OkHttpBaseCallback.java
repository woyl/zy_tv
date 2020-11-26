package com.zhongyou.meettvapplicaion.net;

import android.util.Log;

import com.zhongyou.meettvapplicaion.BaseException;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;


/**
 * @author
 * @desc okHttp回调类
 * @date 2016/12/1
 */
public abstract class OkHttpBaseCallback<T> extends OkHttpCallback<T> {
    public final String TAG = getClass().getSimpleName();


    /**
     * 构造的时候获得type的class
     */
    public OkHttpBaseCallback() {
        super();
        Log.i(TAG, mType.toString());
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onFailure(int errorCode, BaseException exception) {
        onErrorAll(exception);
    }

    /**
     * 通用错误调用,不管是onFail还是onErr都调用,用来网络不成功时的响应,
     * 重要:一般情况下实现这个方法就行并且super.onErrorAll
     */
    public void onErrorAll(Exception e) {
        if (e.getMessage() != null) {
            Log.e(TAG, "网络异常,请检查网络配置" + mType.toString() + e.getMessage());
            //错误一概提示网络异常
            ToastUtils.showToast(e.getMessage());
//            if (e instanceof BaseErrorBeanException) {
//                //提示服务器的错误信息
//                ToastUtils.showToast(e.getMessage());
//            } else {
//                ToastUtils.showToast("网络异常,请检查网络配置");
//                Log.e(TAG, "网络异常,请检查网络配置 toast" + mType.toString() + e.getMessage());
//            }
        } else {
            Log.e(TAG, "e.getMessage()==null" + mType.toString());
        }
    }

    /**
     * 不想Toast错误,就覆盖onErrorAll方法,并调用这个方法.
     *
     * @param e
     */
    public void onErrorAllNoToast(Exception e) {
        if (e.getMessage() != null) {
            Log.e(TAG, "网络异常,请检查网络配置" + mType.toString() + e.getMessage());
        } else {
            Log.e(TAG, "e.getMessage()==null" + mType.toString());
        }
    }


}