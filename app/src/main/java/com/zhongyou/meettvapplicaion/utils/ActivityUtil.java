package com.zhongyou.meettvapplicaion.utils;

import android.app.Activity;

/**
 * Created by whatisjava on 17-9-27.
 */

public class ActivityUtil {

    public static boolean isLive(Activity activity){
        if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
            return false;
        } else {
            return true;
        }
    }
}
