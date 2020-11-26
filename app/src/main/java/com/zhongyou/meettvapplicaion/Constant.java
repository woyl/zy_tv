package com.zhongyou.meettvapplicaion;

import android.os.Build;

import com.orhanobut.logger.Logger;

/**
 * Created by wufan on 2017/8/2.
 */

public class Constant {

	public static String APIHOSTURL = "";
	public static String WEBSOCKETURL = "";
	public static String DOWNLOADURL = "";
	static boolean debug = BuildConfig.DEBUG;
	//获取是否是平板设备(触摸屏设备)
	public static boolean isPadApplicaion = getIsPadApplication();


	public static final String VIDEO = "video";
	public static final String PLAYVIDEO = "playVideo";
	public static final String PAUSEVIDEO = "pauseVideo";
	public static final String STOPVIDEO = "stopVideo";
	public static String IMTOKEN = "";
	public static final String KEY_ClOSE_MIC = "ClOSE_MIC";

	public static final String KEY_ONMessageArrived = "ONMessageArrived";
	public static final String KEY_IMMESSAGE_ARRIVE = "KEY_IMMESSAGE_ARRIVE";

	public static final String KEY_IMMESSAGE_RECALL = "KEY_IMMESSAGE_RECALL";
	public static final String KEY_IMMESSAGE_RECALL_MESSAGE = "KEY_IMMESSAGE_RECALL_MESSAGE";

	public static String currentGroupId = "";

	//判断是否是前置摄像头
	public static boolean isFrontCamrea = false;

	//0 为主持人 1 为参会人  2为观众 默认为观众
	public static int videoType = 2;

	public static boolean isNeedRecord = false;
	public static int resolvingPower = 1;

	public static boolean isAdminCount = false;


	public final static int delayTime = 5000;


	/*
	 * {
	 *   "data":{
	 *   "staticRes":{
	 *       "apiDownloadUrl":"http://api.zhongyouie.cn",
	 *       "domain":"http://api.zhongyouie.com",
	 *       "websocket":"http://ws.zhongyouie.com/sales"
	 *       }
	 *    },
	 *   "errcode":0,
	 *   "errmsg":"处理成功"
	 * }
	 * */

	public static String getAPIHOSTURL() {

		if (debug) {
//			APIHOSTURL = "http://osg.apitest.zhongyouie.cn";
			APIHOSTURL = "http://api.zhongyouie.com";
		} else {
			APIHOSTURL = "http://api.zhongyouie.com";
		}
		return APIHOSTURL;
	}


	public static String getWeChatEditProfileHost() {
		if (debug) {
			return "http://osg.motest.zhongyouie.cn";
		} else {
			return "http://mo.zhongyouie.com";
		}
	}

	public static String getWEBSOCKETURL() {
		if (debug) {
			WEBSOCKETURL = "http://wstest.zhongyouie.cn/sales";
		} else {
			WEBSOCKETURL = "http://ws.zhongyouie.com/sales";
		}
		return WEBSOCKETURL;
	}

	public static String getDOWNLOADURL() {
		if (debug) {
			DOWNLOADURL = "http://tapi.zhongyouie.cn";
		} else {
			DOWNLOADURL = "http://api.zhongyouie.cn";
		}
		return DOWNLOADURL;
	}

	/**
	 * 获取是否是平板设配
	 */
	public static boolean getIsPadApplication() {
		Logger.e(Build.MANUFACTURER + "---" + Build.MODEL);

           /* if (Build.MANUFACTURER.startsWith("rockchip")){
               return true;
            }else {
                return false;
            }*/

		isPadApplicaion = BaseApplication.isPadApplication(BaseApplication.getInstance());
		return isPadApplicaion;
	}


	/**
	 * 检查更新地址
	 */
	public static final String VERSION_UPDATE_URL = BuildConfig.API_DOMAIN_NAME_YOYOTU + "/dz/app/version/"
			+ BuildConfig.APPLICATION_ID + "/android/GA/latest?versionCode=" + BuildConfig.VERSION_CODE;

	public static final String EVENT_LISTEN_SOCKET_ID = "LISTEN_SOCKET_ID";
	public static final String EVENT_ON_CALL = "ON_CALL";
	public static final String EVENT_LISTEN_SALES_SOCKET_ID = "LISTEN_SALES_SOCKET_ID";
	public static final String EVENT_SALES_ONLINE_WITH_STATUS_RETURN = "SALES_ONLINE_WITH_STATUS_RETURN";
	public static final String EVENT_LISTEN_TV_LEAVE_CHANNEL = "LISTEN_TV_LEAVE_CHANNEL";
	public static final String EVENT_TIMEOUT_WITHOUT_REPLY = "TIMEOUT_WITHOUT_REPLY";
	public static final String EVENT_OLD_DISCONNECT = "OLD_DISCONNECT";
	public static final String EVENT_LISTEN_EXPOSITOR_UPDATE_PROFILE = "LISTEN_EXPOSITOR_UPDATE_PROFILE";
	public static final String EVENT_LISTEN_SALES_UPDATE_ONLINE_STATUS = "LISTEN_SALES_UPDATE_ONLINE_STATUS";
	public static final String EVENT_LISTEN_EXPOSITOR_UPDATE_RATING = "LISTEN_EXPOSITOR_UPDATE_RATING";

	public static final String EVENT_SALES_ONLINE_WITH_STATUS = "SALES_ONLINE_WITH_STATUS";
	public static final String EVENT_CHANGE_RESOLUTION = "CHANGE_RESOLUTION";
	public static final String EVENT_RE_CHECK_SOCKET_ID = "RE_CHECK_SOCKET_ID";
	public static final String EVENT_UPDATE_ONLINE_STATUS = "UPDATE_ONLINE_STATUS";
	public static final String EVENT_REPLY_TV = "REPLY_TV";
	public static final String EVENT_END_CALL = "END_CALL";

	public static final String EVENT_UPLOAD_SPACE_COMPLETE = "UPLOAD_SPACE_COMPLETE";

	public static final String FORUM_SEND_CONTENT = "FORUM_SEND_CONTENT";
	public static final String FORUM_REVOKE = "FORUM_REVOKE";

	public static final String KEY_MUTE_AUDI = "mute_audio";
	public static final String KEY_REMOVE_USER = "remove_user";

}
