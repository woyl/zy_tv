package com.zhongyou.meettvapplicaion.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by LuoPan on 2017/3/21 20:37.
 */
public class CommonUtils {

    private static SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.CHINA);
    private static Date date = new Date();

    public static String getFormattedTime(int timeMillis) {
        // 设置时间
        date.setTime(timeMillis);
        // 返回格式化后的结果
        return sdf.format(date);
    }



}
