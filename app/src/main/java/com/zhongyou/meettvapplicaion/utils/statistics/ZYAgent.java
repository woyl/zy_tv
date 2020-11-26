package com.zhongyou.meettvapplicaion.utils.statistics;

import android.content.Context;
import android.util.Log;

import com.tendcloud.tenddata.TCAgent;

/**
 * Created by wufan on 2017/7/18.
 */

public class ZYAgent {
    public static final String TAG = "ZYAgent";

    /**
     * 页面统计-页面打开,因为要写中文页面名,所以直接在具体业务类中调用
     * @param var0
     * @param var1
     */
    public static void onPageStart(Context var0, String var1)
    {
        TCAgent.onPageStart(var0, var1);
        Log.i(TAG,"onPageStart"+var1);
    }

    public static void onPageEnd(Context var0, String var1)
    {
        TCAgent.onPageEnd(var0, var1);
        Log.i(TAG,"onPageEnd"+var1);
    }

    public static void onEvent(Context var0, String var1)
    {
        TCAgent.onEvent(var0, var1);
        Log.i(TAG,"onEvent "+var1);
    }
}
