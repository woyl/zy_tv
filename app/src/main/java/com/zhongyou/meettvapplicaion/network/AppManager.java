package com.zhongyou.meettvapplicaion.network;

import android.app.Activity;

import java.lang.ref.WeakReference;

/**
 * Created by jie on 2016/12/28.
 */

public class AppManager {

    private static AppManager sInstance = new AppManager();
    private WeakReference<Activity> sCurrentActivityWeakRef;


    private AppManager() {

    }

    public static AppManager getInstance() {
        return sInstance;
    }

    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (sCurrentActivityWeakRef != null) {
            currentActivity = sCurrentActivityWeakRef.get();
        }
        return currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
        sCurrentActivityWeakRef = new WeakReference<Activity>(activity);
    }

}
