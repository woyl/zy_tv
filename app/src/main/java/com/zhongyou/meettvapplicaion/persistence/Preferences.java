package com.zhongyou.meettvapplicaion.persistence;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.zhongyou.meettvapplicaion.BaseApplication;


public class Preferences {
	private static final String tag = Preferences.class.getSimpleName();

	private static final String PREFERENCE_TOKEN = "token";

	private static final String PREFERENCE_USER_ID = "u_id";

	private static final String PREFERENCE_USER_NAME = "u_name";
	private static final String PREFERENCE_USER_MOBILE = "u_mobile";
	private static final String PREFERENCE_USER_ADDRESS = "u_address";
	private static final String PREFERENCE_USER_PHOTO = "u_photo";
	private static final String PREFERENCE_USER_SIGNATURE = "u_signature";
	private static final String PREFERENCE_USER_RANK = "u_rank";

	private static final String PREFERENCE_USER_AUDIT_STATUS = "u_audit_status";
	private static final String PREFERENCE_USER_POST_TYPE_NAME = "u_post_type_name";

	private static final String PREFERENCE_USER_AREA_INFO = "u_area_info";
	private static final String PREFERENCE_USER_AREA_NAME = "u_area_name";

	private static final String PREFERENCE_USER_CUSTOM_NAME = "u_custom_name";

	private static final String PREFERENCE_CLASS_ID = "c_id";

	private static final String PREFERENCE_STUDENT_ID = "s_id";
	private static final String PREFERENCE_WEIXIN_HEAD = "weixin_head";

	private static final String PREFERENCE_UUID = "uuid";

	private static final String PREFERENCE_MEETING_ID = "meeting_id";
	private static final String PREFERENCE_MEETING_JOIN_TRACE_ID = "meetingJoinTraceId";
	private static final String PREFERENCE_IMTOKEN = "imToken";

	private static final String PREFERENCE_ISROTATION = "isrotation";

	/**
	 * url前缀要持久化保存
	 **/
	private static String PREFERENCE_IMG_URL = "imgUrl";
	private static String PREFERENCE_VIDEO_URL = "videoUrl";
	private static String PREFERENCE_DOWNLOAD_URL = "downloadUrl";
	private static String PREFERENCE_COOPERATION_URL = "cooperationUrl";

	private static String imgUrl;
	private static String videoUrl;
	private static String downloadUrl;
	private static String cooperationUrl;

	//会议图像最后展示时间
	private static final String PREFERENCE_MEETINGCAMERALASTTS = "meetingCameraLastTs";


	public static void setStudentId(String studentId) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(PREFERENCE_STUDENT_ID, studentId);
		if (!editor.commit()) {
			Log.d(tag, "student id save failure");
		} else {
			Log.d(tag, "student id save success");
		}
	}

	public static String getUserId() {
		return getPreferences().getString(PREFERENCE_USER_ID, null);
	}

	public static void setUserId(String userId) {
		Logger.e("--------------setUserId-------------");
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(PREFERENCE_USER_ID, userId);
		if (!editor.commit()) {
			Log.d(tag, "User id save failure");
		} else {
			Log.d(tag, "User id save success");
		}
	}

	public static String getMeetingId() {
		return getPreferences().getString(PREFERENCE_MEETING_ID, null);
	}

	public static void setMeetingId(String meetingId) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(PREFERENCE_MEETING_ID, meetingId);
		if (!editor.commit()) {
			Log.d(tag, "meetingId save failure");
		} else {
			Log.d(tag, "meetingId save success");
		}
	}

	public static Boolean getRotation() {
		return getPreferences().getBoolean(PREFERENCE_ISROTATION, false);
	}

	public static void setRotation(boolean meetingId) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putBoolean(PREFERENCE_ISROTATION, meetingId);
		if (!editor.commit()) {
			Log.d(tag, "meetingId save failure");
		} else {
			Log.d(tag, "meetingId save success");
		}
	}

	public static String getMeetingJoinTraceId() {
		return getPreferences().getString(PREFERENCE_MEETING_JOIN_TRACE_ID, null);
	}

	public static void setMeetingJoinTraceId(String meetingTraceId) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(PREFERENCE_MEETING_JOIN_TRACE_ID, meetingTraceId);
		if (!editor.commit()) {
			Log.d(tag, "User id save failure");
		} else {
			Log.d(tag, "User id save success");
		}
	}

	public static int getUserAuditStatus() {
		return getPreferences().getInt(PREFERENCE_USER_AUDIT_STATUS, 0);
	}

	public static void setUserAuditStatus(int rank) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putInt(PREFERENCE_USER_AUDIT_STATUS, rank);
		if (!editor.commit()) {
			Log.d(tag, "User AuditStatus save failure");
		} else {
			Log.d(tag, "User AuditStatus save success");
		}
	}

	public static int getUserRank() {
		return getPreferences().getInt(PREFERENCE_USER_RANK, 0);
	}

	public static void setUserRank(int rank) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putInt(PREFERENCE_USER_RANK, rank);
		if (!editor.commit()) {
			Log.d(tag, "User rank save failure");
		} else {
			Log.d(tag, "User rank save success");
		}
	}

	public static String getUserMobile() {
		return getPreferences().getString(PREFERENCE_USER_MOBILE, "");
	}

	public static void setUserMobile(String userMobile) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(PREFERENCE_USER_MOBILE, userMobile);
		if (!editor.commit()) {
			Log.d(tag, "User mobile save failure");
		} else {
			Log.d(tag, "User mobile save success");
		}
	}

	public static String getUserPostTypeName() {
		return getPreferences().getString(PREFERENCE_USER_POST_TYPE_NAME, "");
	}

	public static void setUserPostTypeName(String postTypeName) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(PREFERENCE_USER_POST_TYPE_NAME, postTypeName);
		if (!editor.commit()) {
			Log.d(tag, "User postTypeName save failure");
		} else {
			Log.d(tag, "User postTypeName save success");
		}
	}

	public static String getUserName() {
		return getPreferences().getString(PREFERENCE_USER_NAME, "");
	}

	public static void setUserName(String userName) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(PREFERENCE_USER_NAME, userName);
		if (!editor.commit()) {
			Log.d(tag, "User name save failure");
		} else {
			Log.d(tag, "User name save success");
		}
	}

	public static String getUserAddress() {
		return getPreferences().getString(PREFERENCE_USER_ADDRESS, "");
	}

	public static void setUserAddress(String userName) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(PREFERENCE_USER_ADDRESS, userName);
		if (!editor.commit()) {
			Log.d(tag, "User address save failure");
		} else {
			Log.d(tag, "User address save success");
		}
	}

	public static void setCustomName(String customName) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(PREFERENCE_USER_CUSTOM_NAME, customName);
		if (!editor.commit()) {
			Log.d(tag, "User Address save failure");
		} else {
			Log.d(tag, "User Address save success");
		}
	}

	public static String getCustomName() {
		return getPreferences().getString(PREFERENCE_USER_CUSTOM_NAME, null);
	}

	public static void setAreaName(String areaName) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(PREFERENCE_USER_AREA_NAME, areaName);
		if (!editor.commit()) {
			Log.d(tag, "User Address save failure");
		} else {
			Log.d(tag, "User Address save success");
		}
	}

	public static String getAreaName() {
		return getPreferences().getString(PREFERENCE_USER_AREA_NAME, null);
	}

	public static void setAreaInfo(String areaInfo) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(PREFERENCE_USER_AREA_INFO, areaInfo);
		if (!editor.commit()) {
			Log.d(tag, "User Address save failure");
		} else {
			Log.d(tag, "User Address save success");
		}
	}

	public static String getAreaInfo() {
		return getPreferences().getString(PREFERENCE_USER_AREA_INFO, null);
	}

	public static String getUserPhoto() {
		return getPreferences().getString(PREFERENCE_USER_PHOTO, "");
	}

	public static void setUserPhoto(String userName) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(PREFERENCE_USER_PHOTO, userName);
		if (!editor.commit()) {
			Log.d(tag, "User photo save failure");
		} else {
			Log.d(tag, "User photo save success");
		}
	}

	public static void setUserSignature(String userName) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(PREFERENCE_USER_SIGNATURE, userName);
		if (!editor.commit()) {
			Log.d(tag, "User Signature save failure");
		} else {
			Log.d(tag, "User Signature save success");
		}
	}

	public static String getToken() {
		return getPreferences().getString(PREFERENCE_TOKEN, null);
	}

	public static void setToken(String token) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(PREFERENCE_TOKEN, token);
		if (!editor.commit()) {
			Log.d(tag, "Token save failure");
		} else {
			Log.d(tag, "Token save success");
		}
	}

	public static void setHostUrl(String hostUrl) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString("hostUrl", hostUrl);
		if (!editor.commit()) {
			Log.d(tag, "User photo save failure");
		} else {
			Log.d(tag, "User photo save success");
		}
	}

	public static String getHostUrl() {
		return getPreferences().getString("hostUrl", "");
	}

	public static SharedPreferences getPreferences() {
//        return PreferenceManager.getDefaultSharedPreferences(BaseApplication.getInstance());
		return BaseApplication.getInstance().getSharedPreferences("remote", Context.MODE_MULTI_PROCESS);
	}

	public static void clear() {
		setToken(null);
		setUserId(null);
		setStudentId(null);
		setImToken("");
		getPreferences().edit().clear().apply();
	}

	public static boolean isLogin() {
//        return (!TextUtils.isEmpty(getUserId())) && (!TextUtils.isEmpty(getToken()));
		return (!TextUtils.isEmpty(getToken()));
	}

	public static String getImgUrl() {
		if (!TextUtils.isEmpty(imgUrl)) {
			return imgUrl;
		} else {
			return getPreferences().getString(PREFERENCE_IMG_URL, null);
		}
	}

	public static void setImgUrl(String str) {
		imgUrl = str;
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(PREFERENCE_IMG_URL, str);
		if (!editor.commit()) {
			Log.d(tag, "setImgUrl save failure");
		} else {
			Log.d(tag, "setImgUrl save success");
		}
	}


	public static void setImToken(String imToken) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(PREFERENCE_IMTOKEN, imToken);
		if (!editor.commit()) {
			Log.d(tag, "setImgUrl save failure");
		} else {
			Log.d(tag, "setImgUrl save success");
		}
	}

	public static String getImToken() {
		return getPreferences().getString(PREFERENCE_IMTOKEN, "");
	}

	public static String getVideoUrl() {
		if (!TextUtils.isEmpty(imgUrl)) {
			return videoUrl;
		} else {
			return getPreferences().getString(PREFERENCE_VIDEO_URL, null);
		}
	}

	public static void setVideoUrl(String str) {
		videoUrl = str;
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(PREFERENCE_VIDEO_URL, str);
		if (!editor.commit()) {
			Log.d(tag, "setVideoUrl save failure");
		} else {
			Log.d(tag, "setVideoUrl save success");
		}
	}

	public static String getDownloadUrl() {
		if (!TextUtils.isEmpty(downloadUrl)) {
			return downloadUrl;

		} else {
			return getPreferences().getString(PREFERENCE_DOWNLOAD_URL, null);
		}

	}

	public static void setDownloadUrl(String str) {
		downloadUrl = str;
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(PREFERENCE_DOWNLOAD_URL, str);
		if (!editor.commit()) {
			Log.d(tag, "setDownloadUrl save failure");
		} else {
			Log.d(tag, "setDownloadUrl save success");
		}
	}

	public static String getCooperationUrl() {
		if (!TextUtils.isEmpty(cooperationUrl)) {
			return cooperationUrl;
		} else {
			return getPreferences().getString(PREFERENCE_COOPERATION_URL, null);
		}

	}

	public static void setCooperationUrl(String str) {
		cooperationUrl = str;
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(PREFERENCE_COOPERATION_URL, str);
		if (!editor.commit()) {
			Log.d(tag, "setCooperationUrl save failure");
		} else {
			Log.d(tag, "setCooperationUrl save success");
		}
	}

	public static String getUUID() {
		return getPreferences().getString(PREFERENCE_UUID, null);
	}

	public static void setUUID(String uuid) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(PREFERENCE_UUID, uuid);
		if (!editor.commit()) {
			Log.d(tag, "User name save failure");
		} else {
			Log.d(tag, "User name save success");
		}
	}

	public static void setWeiXinHead(String weiXinHead) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(PREFERENCE_WEIXIN_HEAD, weiXinHead);
		if (!editor.commit()) {
			Log.d(tag, "User mobile save failure");
		} else {
			Log.d(tag, "User mobile save success");
		}
	}

	public static String getWeiXinHead() {
		return getPreferences().getString(PREFERENCE_WEIXIN_HEAD, "");
	}

	public static void setMeetingCameraLastTs(long ts) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putLong(PREFERENCE_MEETINGCAMERALASTTS, ts);
		if (!editor.commit()) {
			Log.d(tag, "meeting camera last ts save failure");
		} else {
			Log.d(tag, "meeting camera last ts save success");
		}
	}

	public static long getMeetingCameraLastTs() {
		return getPreferences().getLong(PREFERENCE_MEETINGCAMERALASTTS, 0L);
	}

    public static String getAgroUid() {
        return getPreferences().getString("AgroUid", "");
    }

    public static void saveAgroUid(String uid) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString("AgroUid", uid);
		if (!editor.commit()) {
			Log.d(tag, "User mobile save failure");
		} else {
			Log.d(tag, "User mobile save success");
		}
	}
}
