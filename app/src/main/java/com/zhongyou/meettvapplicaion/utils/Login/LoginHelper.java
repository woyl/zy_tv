package com.zhongyou.meettvapplicaion.utils.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.zhongyou.meettvapplicaion.BaseApplication;
import com.zhongyou.meettvapplicaion.business.LauncherActivity;
import com.zhongyou.meettvapplicaion.business.MeetingsActivityLaunch;
import com.zhongyou.meettvapplicaion.entities.User;
import com.zhongyou.meettvapplicaion.entities.Wechat;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.business.SignInActivity;
import com.zhongyou.meettvapplicaion.utils.Logger;
import com.zhongyou.meettvapplicaion.utils.statistics.ZYAgent;


/**
 * 登录,注销,退出帮助类
 * Created by wufan on 2017/1/22.
 */

public class LoginHelper {
    public static final String TAG ="LoginHelper";
    public static final String LOGIN_TYPE="login_type";
    public static final int LOGIN_TYPE_EXIT=1;
    public static final int LOGIN_TYPE_LOGOUT=2;
    public static  boolean mIsLogout=false;
    public static final Class clazz= MeetingsActivityLaunch.class;


    /**
     * 非mainTabActivity页面调用
     * @param activity
     */
    public static void exit(Activity activity) {
        Intent intent;
        intent = new Intent(activity, clazz);
        intent.putExtra(LOGIN_TYPE, LOGIN_TYPE_EXIT);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }


    /**
     * 非mainTabActivity页面调用
     * @param activity
     */
    public static void exit(Activity activity,Class clazz) {
        Intent intent;
        intent = new Intent(activity, clazz);
        intent.putExtra(LOGIN_TYPE, LOGIN_TYPE_EXIT);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }


    /**
     * 退出登录-退出到主页activity,强制退出
     * @param activity
     */
    public static void logout(Activity activity) {
        logout(activity,false);
    }


    /**
     * 退出登录-退出到主页activity
     * @param activity
     * @param isUserLogout 是否用户手动退出
     */
    public static void logout(Activity activity, boolean isUserLogout) {
        Logger.d(TAG,activity.getClass().getSimpleName());
        logoutCustom(activity);

        Intent intent;
        if(activity instanceof SignInActivity || activity instanceof LauncherActivity){
            intent = new Intent(activity, SignInActivity.class);
        } else {
            intent = new Intent(activity, MeetingsActivityLaunch.class);
        }
        intent.putExtra(LOGIN_TYPE, LOGIN_TYPE_LOGOUT);
        intent.putExtra("IS_USER_LOGOUT",isUserLogout);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
//            activity.finish(); //不需要调用finish.clear_top flag会把当前activity finish.

    }




    /**目前只有请求回调中调用
     * 退出登录-全局调用
     */
    public static void logout(){
        //不检查token是否已登录,因为token有值但是已经是错误的了.
//        if (Preferences.isLogin()) {
            Activity currentActivity = BaseApplication.getInstance().getCurrentActivity();
            if (currentActivity != null) {
                com.orhanobut.logger.Logger.e("---------"+currentActivity+"---------");
                LoginHelper.logout(currentActivity);
            } else {
                //应用不再前台,延迟到应用到前台处理
                LoginHelper.mIsLogout = true;
            }

//        }
    }


    /**
     * 退出登录-本应用自己业务相关,本地用户数据,服务
     * @param context
     */
    public static void logoutCustom(Context context) {
        ZYAgent.onEvent(context,"退出登录");
        ZYAgent.onEvent(context,"退出登录 连接服务 请求停止");
        Preferences.clear();
    }

    public static void savaUser(User user){
        Preferences.setToken(user.getToken());
        Preferences.setUserId(user.getId());
        Preferences.setUserName(user.getName());
        Preferences.setUserMobile(user.getMobile());
        Preferences.setUserAddress(user.getAddress());
        Preferences.setUserPhoto(user.getPhoto());
        Preferences.setUserSignature(user.getSignature());
        Preferences.setUserRank(user.getRank());
        Preferences.setUserAuditStatus(user.getAuditStatus());
        Preferences.setUserPostTypeName(user.getPostTypeName());
        Preferences.setAreaInfo(user.getAreaInfo());
        Preferences.setAreaName(user.getAreaName());
        Preferences.setCustomName(user.getCustomName());
    }

    public static void savaWeChat(Wechat wechat){
        Preferences.setWeiXinHead(wechat.getHeadimgurl());
    }

}
