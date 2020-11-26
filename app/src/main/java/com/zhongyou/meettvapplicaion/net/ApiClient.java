package com.zhongyou.meettvapplicaion.net;

import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.zhongyou.meettvapplicaion.BaseApplication;
import com.zhongyou.meettvapplicaion.BuildConfig;
import com.zhongyou.meettvapplicaion.Constant;
import com.zhongyou.meettvapplicaion.entities.QiniuToken;
import com.zhongyou.meettvapplicaion.entities.RankInfo;
import com.zhongyou.meettvapplicaion.entities.RecordData;
import com.zhongyou.meettvapplicaion.entities.RecordTotal;
import com.zhongyou.meettvapplicaion.entities.UserData;
import com.zhongyou.meettvapplicaion.entities.Version;
import com.zhongyou.meettvapplicaion.entities.base.BaseBean;
import com.zhongyou.meettvapplicaion.entities.base.BaseErrorBean;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.UUIDUtils;
import com.tendcloud.tenddata.TCAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by whatisjava on 16/4/12.
 */
public class ApiClient {
	public final String TAG = getClass().getSimpleName();

	private OkHttpUtil okHttpUtil;

	private static class SingletonHolder {
		private static ApiClient instance = new ApiClient();
	}

	private String jointBaseUrl(String apiName) {
		return Constant.getAPIHOSTURL() + "/osg/app" + apiName;


	}

	public static ApiClient getInstance() {
		return SingletonHolder.instance;
	}

	private ApiClient() {
		okHttpUtil = OkHttpUtil.getInstance();
	}

	public static Map<String, String> getCommonHead() {
		Map<String, String> params = new HashMap<>();
		params.put("Authorization", TextUtils.isEmpty(Preferences.getToken()) ? "" : "Token " + Preferences.getToken());
		params.put("Content-Type", "application/json; charset=UTF-8");
		params.put("DeviceUuid", UUIDUtils.getUUID(BaseApplication.getInstance()));
		params.put("User-Agent", "HRZY_HOME"
				+ "_"
				+ BuildConfig.APPLICATION_ID
				+ "_"
				+ BuildConfig.VERSION_NAME
				+ "_"
				+ TCAgent.getDeviceId(BaseApplication.getInstance()) + "(android_OS_"
				+ Build.VERSION.RELEASE + ";" + Build.MANUFACTURER
				+ "_" + Build.MODEL + ")");

		return params;
	}


	public static Map<String, String> getAgoraHeader() {
		Map<String, String> map = new HashMap<>();
		try {
			String plainCredentials = "d1414626ad1f460a9d2e6a05a263c60c:b89542e6a5f24758ba91c86ee23c78c2";
			String base64Credentials =(Base64.encodeToString(plainCredentials.getBytes("utf-8"),Base64.NO_WRAP));
			Logger.e(base64Credentials.trim().replaceAll("[\\s*\t\n\r]", ""));
			map.put("Authorization","Basic "+ base64Credentials.trim().replaceAll("[\\s*\t\n\r]", ""));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return map;
	}



	public static String jointParamsToUrl(String url, Map<String, String> params) {
		if (params != null && params.size() > 0) {
			Uri uri = Uri.parse(url);
			Uri.Builder b = uri.buildUpon();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				b.appendQueryParameter(entry.getKey(), entry.getValue());
			}
			return b.build().toString();
		}
		return url;
	}


	public static String getLoginUrl() {
		Map<String, String> params = new HashMap<>();
		params.put("deviceUUID", UUIDUtils.getUUID(BaseApplication.getInstance()));
		return jointParamsToUrl(BuildConfig.API_DOMAIN_NAME_MO + "/expositorEdit", params);
	}

	//获取微信登陆图片下载的URL
	public void getWechatLoginQRImageURL(OkHttpBaseCallback callback) {
		okHttpUtil.get(BuildConfig.API_DOMAIN_NAME_MO + "/osg/app/wechat/login/qrcode", getCommonHead(), null, callback);
	}

	//下载微信登陆图片
	public void getWechatLoginImage(String url, OkHttpBaseCallback<InputStream> callback) {
		okHttpUtil.get(url, callback);
	}

	public static String getEditUserinfoUrl(String token) {
		Map<String, String> params = new HashMap<>();
		params.put("deviceUUID", UUIDUtils.getUUID(BaseApplication.getInstance()));
		params.put("isLoginNotice", "false");
		params.put("requestUUID",token);
		return jointParamsToUrl(Constant.getWeChatEditProfileHost()+ "/onlineShopperEdit", params);
	}

	//  软件更新
	public void versionCheck(Object tag, OkHttpBaseCallback<BaseBean<Version>> callback) {
		if (Constant.getIsPadApplication()) {
			OkHttpUtil.getInstance().get(Constant.getAPIHOSTURL()+"/osg/app/version/"
					+ "com.zhongyou.meetpadapplicaion" + "/android/GA/latest?versionCode=" + BuildConfig.VERSION_CODE, tag, callback);
		} else {
			OkHttpUtil.getInstance().get(Constant.getAPIHOSTURL()+"/osg/app/version/"
					+ BuildConfig.APPLICATION_ID + "/android/GA/latest?versionCode=" + BuildConfig.VERSION_CODE, tag, callback);
		}

	}

	public void getAllMeeting(Object tag, String meetingName, OkHttpCallback callback) {
		String url = Constant.getAPIHOSTURL() + "/osg/app/meeting/list";
		if (!TextUtils.isEmpty(meetingName))
			url += "?title=" + meetingName;
		okHttpUtil.get(url, getCommonHead(), null, callback);
	}

	//获取全局配置信息接口
	public static String getGlobalConfigurationInformation() {
		String BabyHomeUrl = Constant.getDOWNLOADURL() + "/dz/app/config";
		return BabyHomeUrl;
	}

	//  注册设备信息
	public void deviceRegister(Object tag, String jsonStr, OkHttpBaseCallback callback) {
		OkHttpUtil.getInstance().postJson(Constant.getAPIHOSTURL() + "/osg/app/device", getCommonHead(), jsonStr, callback, tag);
	}


	public void requestLoginListen(Object tag, OkHttpBaseCallback callback) {
		okHttpUtil.postJson(jointBaseUrl("/user/login/listen"), getCommonHead(), "", callback, tag);
	}

	/**
	 * 收到客户退出通知或者主动退出房间时请求
	 *
	 * @param recordId
	 * @param responseCallback
	 * @return
	 */
	public void startOrStopOrRejectCallExpostor(String recordId, String state, OkHttpCallback responseCallback) {
		okHttpUtil.put(Constant.getAPIHOSTURL() + "/osg/app/call/record/" + recordId + "?state=" + state, getCommonHead(), null, responseCallback);
	}

	/**
	 * 更新设备信息，目前只有socketid的更新
	 *
	 * @param tag
	 * @param deviceId
	 * @param responseCallback
	 * @param jsonStr
	 */
	public void updateDeviceInfo(Object tag, String deviceId, OkHttpCallback responseCallback, String jsonStr) {
		okHttpUtil.putJson(Constant.getAPIHOSTURL() + "/osg/app/device/" + deviceId, getCommonHead(), jsonStr, responseCallback, tag);
	}

	/**
	 * 获取声网参数
	 *
	 * @param params
	 * @param responseCallback
	 */
	public void getAgoraKey(Object tag, Map<String, String> params, OkHttpCallback responseCallback) {
		okHttpUtil.get(jointParamsToUrl(Constant.getAPIHOSTURL() + "/osg/agora/key/osgV2", params), tag, responseCallback);
	}

	/**
	 *
	 */
	public void requestWxToken(Object tag, OkHttpBaseCallback callback) {
		Map<String, String> params = new HashMap<>();
		OkHttpUtil.getInstance().get("https://api.weixin.qq.com/sns/oauth2/access_token", params, tag, callback);
	}

	/**
	 * 通过微信code登录
	 *
	 * @param code
	 * @param state
	 * @param tag
	 * @param callback
	 */
	public void requestWechat(String code, String state, Object tag, OkHttpBaseCallback callback) {
		Map<String, String> params = new HashMap<>();
		params.put("code", code);
		params.put("state", state);
		OkHttpUtil.getInstance().get(Constant.getAPIHOSTURL() + "/osg/app/wechat", params, tag, callback);
	}


	/**
	 * 获取用户信息,每次启动刷新用户数据
	 *
	 * @param tag
	 * @param callback
	 */
	public void requestUser(Object tag, OkHttpBaseCallback<BaseBean<UserData>> callback) {
		OkHttpUtil.getInstance().get(Constant.getAPIHOSTURL() + "/osg/app/user", getCommonHead(), null, callback, tag);
	}


	/**
	 * 设置用户信息
	 *
	 * @param tag
	 * @param callback
	 */
	public void requestUserExpostor(Object tag, Map<String, String> params, OkHttpBaseCallback callback) {
		okHttpUtil.getInstance().putJson(Constant.getAPIHOSTURL() + "/osg/app/user/expostor/" + Preferences.getUserId(), getCommonHead(),
				OkHttpUtil.getGson().toJson(params), callback, tag);
	}

	/**
	 * 心跳加在线状态
	 *
	 * @param tag
	 * @param params
	 * @param callback
	 */
	public void requestUserExpostorState(Object tag, Map<String, String> params, OkHttpBaseCallback callback) {
		okHttpUtil.getInstance().putJson(Constant.getAPIHOSTURL() + "/osg/app/user/expostor/" + Preferences.getUserId() + "/state"
				, getCommonHead(), OkHttpUtil.getGson().toJson(params), callback, tag);
	}

	/**
	 * 获取七牛图片上传token
	 *
	 * @param tag
	 * @param callback
	 */
	public void requestQiniuToken(Object tag, OkHttpCallback<BaseBean<QiniuToken>> callback) {
		//使用唷唷兔地址
		okHttpUtil.getInstance().get(Constant.getAPIHOSTURL() + "/osg/resource/uploadtoken/image", tag, callback);
	}

	/**
	 * 获取导购通话时间
	 *
	 * @param tag
	 * @param callback
	 */
	public void requestRecordTotal(Object tag, OkHttpBaseCallback<BaseBean<RecordTotal>> callback) {
		//使用唷唷兔地址
		String userId = Preferences.getUserId();
		okHttpUtil.getInstance().get(Constant.getAPIHOSTURL() + "/osg/app/call/expostor/" + userId + "/record/total", getCommonHead(), null, callback, tag);
	}

	/**
	 * 获取导购通话记录
	 *
	 * @param tag
	 * @param callback
	 */
	public void requestRecord(Object tag, String starFilter, String pageNo, String pageSize, OkHttpBaseCallback<BaseBean<RecordData>> callback) {
		//使用唷唷兔地址
		String userId = Preferences.getUserId();
		Map<String, String> params = new HashMap<>();
		if (!TextUtils.isEmpty(starFilter)) {
			params.put("starFilter", "1");
		}
		params.put("pageNo", pageNo);
		params.put("pageSize", pageSize);
		okHttpUtil.getInstance().get(Constant.getAPIHOSTURL() + "/osg/app/call/expostor/" + userId + "/record", getCommonHead(), params, callback, tag);
	}

	/**
	 * 获得手机验证码
	 *
	 * @param tag
	 * @param mobile
	 */
	public void requestVerifyCode(Object tag, String mobile, OkHttpBaseCallback<BaseErrorBean> callback) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("mobile", mobile);
			okHttpUtil.getInstance().postJson(Constant.getAPIHOSTURL() + "/osg/app/user/verifyCode", getCommonHead(), jsonObject.toString(), callback, tag);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}

	}


	/**
	 * 获取导购员评分
	 *
	 * @param tag
	 * @param callback
	 */
	public void requestRankInfo(Object tag, OkHttpBaseCallback<BaseBean<RankInfo>> callback) {
		//使用唷唷兔地址
		String userId = Preferences.getUserId();
		okHttpUtil.getInstance().get(Constant.getAPIHOSTURL() + "/osg/app/call/rankInfo/" + userId, getCommonHead(), null, callback, tag);
	}


	public void requestReplayComment(Object tag, String callRecordId, String replyRating, OkHttpBaseCallback<BaseErrorBean> callback) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("callRecordId", callRecordId);
			jsonObject.put("replyRating", replyRating);
			okHttpUtil.getInstance().postJson(Constant.getAPIHOSTURL() + "/osg/app/call/replyComment", getCommonHead(), jsonObject.toString(), callback, tag);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}

	}

	public void logout(OkHttpBaseCallback<BaseErrorBean> callback) {
		okHttpUtil.delete(jointBaseUrl("/user/logout"), getCommonHead(), null, callback);
	}


	public void getHttpBaseUrl(Object tag, OkHttpCallback callback) {
		String env = "test";
		if (!BuildConfig.DEBUG) {
			env = "formal";
		}
		okHttpUtil.get(BuildConfig.API_DOMAIN_NAME + "/osg/" + env + "/android/config", tag, callback);
	}

	/**
	 * 获取录制资源
	 * */
	public void getAgoraResourceID(String jsonStr, OkHttpBaseCallback<com.alibaba.fastjson.JSONObject> callback, Object TAG) {
		okHttpUtil.postJson("http://api.agora.io/v1/apps/47e3fe7ab51f499c8d0fbc2d6bfc6c5b/cloud_recording/acquire", getAgoraHeader(), jsonStr, callback, TAG);
	}




	/**
	 * 获取该用户是否是会议管理员
	 * /osg/app/user/meeting/admin
	 */
	public void requestMeetingAdmin(Object tag, com.zhongyou.meettvapplicaion.net.OkHttpCallback callback){
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/app/user/meeting/admin", getCommonHead(), null, callback);
	}

	/**
	 * 开始录制
	 * */

	public void  startRecordVideo(Object tag,String jsonStr,com.zhongyou.meettvapplicaion.net.OkHttpCallback<com.alibaba.fastjson.JSONObject> callback){
		okHttpUtil.postJson(Constant.getAPIHOSTURL()+"/osg/app/meeting/startRecordVideo",getCommonHead(),jsonStr,callback,tag);



	}

	/**
	 *
	 * 结束录制
	 * */

	public void stopRecordVideo(Object tag, String jsonStr, OkHttpCallback<com.alibaba.fastjson.JSONObject> callback){
		okHttpUtil.postJson(Constant.getAPIHOSTURL()+"/osg/app/meeting/stopRecordVideo",getCommonHead(),jsonStr,callback,tag);
	}




}
