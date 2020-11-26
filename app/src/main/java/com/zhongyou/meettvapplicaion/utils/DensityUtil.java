package com.zhongyou.meettvapplicaion.utils;

import android.content.Context;

/**
 * Created by whatisjava on 17-1-4.
 */

public class DensityUtil {

    public static int dip2px(Context context, float dpValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dipId2px(Context context, int dp_id){
        int dp = (int) context.getResources().getDimension(dp_id);
        int px= DensityUtil.dip2px(context,dp);
        return px;
    }
}
