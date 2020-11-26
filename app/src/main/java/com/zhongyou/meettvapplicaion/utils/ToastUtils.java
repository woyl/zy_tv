package com.zhongyou.meettvapplicaion.utils;

import android.content.Context;
import android.widget.Toast;

import com.zhongyou.meettvapplicaion.BaseApplication;


/**
 * Created by wufan on 2016/12/17.
 */

public class ToastUtils {
    public static Toast mToast;

    /**
     * 显示吐司
     * @param context
     * @param message
     */
    public static void showToast(final Context context, final String message){
        if (mToast == null){
            mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        }else{
            mToast.setText(message);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }


    public static void showToast(String message){
        Context context=BaseApplication.getInstance();
        if(context!=null){
            showToast(context,message);
        }

    }

    public static void showToast(int messageResId){
        Context context= BaseApplication.getInstance();
        if(context!=null){
            showToast(context,messageResId);
        }

    }

    /**
     * 显示吐司
     * @param context
     * @param messageResId
     */
    public static void showToast(final Context context, final int messageResId){
        if (mToast == null){
            mToast = Toast.makeText(context, messageResId, Toast.LENGTH_SHORT);
        }else{
            mToast.setText(messageResId);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }
}