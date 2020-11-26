package com.zhongyou.meettvapplicaion;

import android.net.Uri;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.orhanobut.logger.Logger;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.OkHttpCallback;
import com.zhongyou.meettvapplicaion.utils.OkHttpUtil;

import java.util.Map;

/**
 * Created by whatisjava on 16/4/12.
 */
public class ApiClient {

    private OkHttpUtil okHttpUtil;
    public static final String PAGE_NO = "pageNo";
    public static final String PAGE_SIZE = "pageSize";

    private static class SingletonHolder {
        private static ApiClient instance = new ApiClient();
    }

    public static ApiClient getInstance() {
        return SingletonHolder.instance;
    }

    private ApiClient() {
        okHttpUtil = OkHttpUtil.getInstance();
    }

    private String jointBaseUrl(String apiName) {
        return Constant.getAPIHOSTURL() + "/osg/app" + apiName;
    }

    /**
     * 通用接口域名，例如检查版本更新，声网秘钥获取之类
     *
     * @param apiName
     * @return
     */
    private String jointCommonBaseUrl(String apiName) {
        return  Constant.getDOWNLOADURL()+ "/dz" + apiName;

    }

    private String jointParamsToUrl(String url, Map<String, String> params) {
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

    /**
     * 版本更新检测
     *
     * @param tag
     * @param pkgName
     * @param versionCode
     * @param callback
     */
    public void versionCheck(Object tag, String pkgName, String versionCode, OkHttpCallback callback) {
        okHttpUtil.get(tag, jointCommonBaseUrl("/app/version/" + pkgName + "/android/GA/latest?versionCode=" + versionCode), callback);
    }

    /**
     * log信息记录
     *
     * @param tag
     * @param callback
     * @param params
     */
    public void errorlog(Object tag, OkHttpCallback callback, Map<String, Object> params) {
        okHttpUtil.post(tag, jointBaseUrl("/log"), callback, params);
    }

    public void getMeetingByCategory(Object tag, int type, OkHttpCallback callback) {
        okHttpUtil.get(tag, jointBaseUrl("/meeting/list?type=" + type), callback);
    }




    /**
     * 点击导购员卡片时记录呼叫开始的时间
     *
     * @param expostorId
     * @param responseCallback
     */
    public void startCallExpostor(Object tag, String expostorId, OkHttpCallback responseCallback) {
        okHttpUtil.post(tag, jointBaseUrl("/call/expostor/" + expostorId), responseCallback);
    }

    /**
     * 收到导购员退出通知或者主动退出房间时请求
     *
     * @param recordId
     * @param responseCallback
     * @return
     */
    public void endCallExpostor(Object tag, String recordId, OkHttpCallback responseCallback, Map<String, Object> params) {
        okHttpUtil.put(tag, jointBaseUrl("/call/record/" + recordId), responseCallback, params);
    }

    /**
     * 收到导购员退出通知或者主动退出房间时请求
     *
     * @param recordId
     * @param responseCallback
     * @return
     */
    public void endCallExpostor(Object tag, String recordId, OkHttpCallback responseCallback, int state) {
        okHttpUtil.put(tag, jointBaseUrl("/call/record/" + recordId + "?state=" + state), responseCallback);
    }

    /**
     * 收到导购员退出通知或者主动退出房间时请求
     *
     * @param recordId
     * @param responseCallback
     * @return
     */
    public void stopCallExpostor(Object tag, String recordId, OkHttpCallback responseCallback) {
        okHttpUtil.delete(tag, jointBaseUrl("/call/record/" + recordId), responseCallback);
    }

    /**
     * 更新设备信息，目前只有socketid的更新
     *
     * @param tag
     * @param deviceId
     * @param responseCallback
     * @param updateValues
     */
    public void updateDeviceInfo(Object tag, String deviceId, OkHttpCallback responseCallback, Map<String, Object> updateValues) {
        okHttpUtil.put(tag, jointBaseUrl("/device/" + deviceId), responseCallback, updateValues);
    }

    /**
     * 获取声网参数
     *
     * @param params
     * @param responseCallback
     */
    public void getAgoraKey(Object tag, Map<String, String> params, OkHttpCallback responseCallback) {
        okHttpUtil.get(tag, jointParamsToUrl(Constant.getAPIHOSTURL() + "/osg/agora/key/osgV2", params), responseCallback);
    }

    /**
     * 注册设备信息
     *
     * @param tag
     * @param responseCallback
     * @param params
     */
    public void deviceRegister(Object tag, OkHttpCallback responseCallback, Map<String, Object> params) {
        okHttpUtil.post(tag, jointBaseUrl("/device"), responseCallback, params);
    }

    /**
     * 获取微信登录二维码
     *
     * @param tag
     * @param callback
     */
    public void getWeChatLoginQRCode(Object tag, OkHttpCallback callback) {
        okHttpUtil.get(tag, jointBaseUrl("/wechat/login/qrcode"), callback);
    }

    /**
     * 登出账号
     *
     * @param tag
     * @param responseCallback
     */
    public void logout(Object tag, OkHttpCallback responseCallback) {
        okHttpUtil.delete(tag, jointBaseUrl("/user/logout"), responseCallback);
    }

    /**
     * 获取当前用户信息
     *
     * @param tag
     * @param callback
     */
    public void getCurrentUser(Object tag, OkHttpCallback callback) {
        okHttpUtil.get(tag, jointBaseUrl("/user"), callback);
    }

    /**
     * 获取选中导购员的服务次数
     *
     * @param tag
     * @param userId
     * @param responseCallback
     */
    public void getSalesServiceCount(Object tag, String userId, OkHttpCallback responseCallback) {
        okHttpUtil.get(tag, jointBaseUrl("/call/rankInfo/" + userId), responseCallback);
    }

    /**
     * 提交评价结果
     *
     * @param tag
     * @param callback
     * @param values
     */
    public void submitEvaluate(Object tag, OkHttpCallback callback, Map<String, Object> values) {
        okHttpUtil.post(tag, jointBaseUrl("/call/ratingStar"), callback, values);
    }

    /**
     * 登录事件监听
     *
     * @param tag
     * @param callback
     */
    public void loginState(Object tag, OkHttpCallback callback) {
        okHttpUtil.post(tag, jointBaseUrl("/user/login/listen"), callback);
    }

    /**
     * 获取讲解视频列表
     *
     * @param tag
     * @param callback
     */
    public void excellentVideos(Object tag, OkHttpCallback callback, Map<String, String> queryParams) {
        okHttpUtil.get(tag, jointParamsToUrl(jointBaseUrl("/excellentVideos/retrieve"), queryParams), callback);
    }

    /**
     * 视频观看记录
     *
     * @param tag
     * @param callback
     * @param values
     */
    public void videoWatchRecord(Object tag, OkHttpCallback callback, Map<String, Object> values) {
        okHttpUtil.post(tag, jointBaseUrl("/excellentVideos/watchRecord"), callback, values);
    }

    public void getAllMeeting(Object tag, OkHttpCallback callback) {
        okHttpUtil.get(tag, jointBaseUrl("/meeting/list"), callback);
    }


    public void verifyRole(Object tag, OkHttpCallback callback, Map<String, Object> values) {
        okHttpUtil.post(tag, jointBaseUrl("/meeting/verify"), callback, values);
    }

    public void joinMeeting(Object tag, OkHttpCallback callback, Map<String, Object> values) {
        okHttpUtil.post(tag, jointBaseUrl("/meeting/join"), callback, values);
    }

    public void getMeeting(Object tag, String meetingId, OkHttpCallback callback) {
        okHttpUtil.get(tag, jointBaseUrl("/meeting/" + meetingId), callback);
    }

    public void getMeetingHost(Object tag, String meetingId,String clientUid, OkHttpCallback callback) {
        Logger.e(jointBaseUrl("/meeting/" + meetingId + "/host"));
        okHttpUtil.get(tag, jointBaseUrl("/meeting/" + meetingId + "/host?clientUid="+ clientUid), callback);
    }

    public void finishMeeting(Object tag, String meetingId, int attendance, OkHttpCallback callback) {
        okHttpUtil.post(tag, jointBaseUrl("/meeting/" + meetingId + "/end?attendance=" + attendance), callback);
    }

    public void expostorOnlineStats(Object tag, OkHttpCallback callback, Map<String, Object> values) {
        okHttpUtil.post(tag, jointBaseUrl("/user/expostor/online/stats"), callback, values);
    }

    public void meetingJoinStats(Object tag, OkHttpCallback callback, Map<String, Object> values) {
        okHttpUtil.post(tag, jointBaseUrl("/meeting/join/stats"), callback, values);
    }

    public void meetingHostStats(Object tag, OkHttpCallback callback, Map<String, Object> values) {
        okHttpUtil.post(tag, jointBaseUrl("/meeting/host/stats"), callback, values);
    }

    /**
     * 获取空间状态列表
     *
     * @param tag
     * @param callback
     */
    public void statusTypes(Object tag, OkHttpCallback callback) {
        okHttpUtil.get(tag, jointBaseUrl("/space/status/type"), callback);
    }

    /**
     * 获取空间状态
     *
     * @param tag
     * @param callback
     */
    public void status(Object tag, OkHttpCallback callback, Map<String, String> queryParams) {
        okHttpUtil.get(tag, jointParamsToUrl(jointBaseUrl("/space/status"), queryParams), callback);
    }

    /**
     * 获取空间状态列表
     *
     * @param tag
     * @param callback
     */
    public void statusIsLiked(Object tag, OkHttpCallback callback, String statusId) {
        okHttpUtil.get(tag, jointBaseUrl("/space/status/" + statusId + "/like"), callback);
    }

    /**
     * 视频观看记录
     *
     * @param tag
     * @param callback
     */
    public void statusLike(Object tag, OkHttpCallback callback, String statusId) {
        okHttpUtil.post(tag, jointBaseUrl("/space/status/" + statusId + "/like"), callback);
    }

    /**
     * 视频观看记录
     *
     * @param tag
     * @param callback
     */
    public void statusView(Object tag, OkHttpCallback callback, String statusId) {
        okHttpUtil.post(tag, jointBaseUrl("/space/status/" + statusId + "/view"), callback);
    }

    public void statusUploadUrl(Object tag, OkHttpCallback callback) {
        okHttpUtil.get(tag, jointBaseUrl("/space/status/upload_foces/url"), callback);
    }

    public void meetingMaterials(Object tag, OkHttpCallback callback, String meetingId) {
        okHttpUtil.get(tag, jointBaseUrl("/meeting/materials?meetingId=" + meetingId), callback);
    }

    public void meetingMaterial(Object tag, OkHttpCallback callback, String materialsId) {
        okHttpUtil.get(tag, jointBaseUrl("/meeting/materials/" + materialsId), callback);
    }

    public void meetingMaterialUpload(Object tag, OkHttpCallback callback, Map<String, Object> values) {
        okHttpUtil.post(tag, jointBaseUrl("/meeting/materials"), callback, values);
    }

    public void meetingSetMaterial(Object tag, OkHttpCallback callback, String meetingId, String materialId) {
        okHttpUtil.post(tag, jointBaseUrl("/meeting/" + meetingId + "/materials/" + materialId), callback);
    }

    public void meetingLeaveTemp(Object tag, OkHttpCallback callback, String meetingId, Map<String, Object> values) {
        okHttpUtil.post(tag, jointBaseUrl("/meeting/" + meetingId + "/leave/temp"), callback, values);
    }

    public void channelCount(Object tag, OkHttpCallback callback, String meetingId, Map<String, Object> values){
        okHttpUtil.post(tag, jointBaseUrl("/meeting/" + meetingId + "/user/count"), callback, values);
    }

    /**
     * 取消请求
     *
     * @param tag
     */
    public void cancelRequestCall(Object tag) {
        okHttpUtil.cancelTag(tag);
    }

    /**
     * 获取讨论区信息
     *
     * @param tag
     * @param params
     * @param callback
     */
    public void getForumContent(Object tag, Map<String, String> params, com.zhongyou.meettvapplicaion.utils.OkHttpCallback callback) {
        String meetingId = (String) params.get("meetingId");
        params.remove("meetingId");
        String url = jointBaseUrl("/forum/" + meetingId + "/content");
        okHttpUtil.get(tag, com.zhongyou.meettvapplicaion.net.OkHttpUtil.jointUrl(url, params), callback);
    }

    /**
     * 讨论区所有消息标记已读
     * /osg/app/forum/{meetingId}/view/log
     *
     * @param tag
     * @param meetingId
     * @param callback
     */
    public void sendViewLog(Object tag, String meetingId, OkHttpCallback callback) {
        okHttpUtil.post(tag, jointBaseUrl("/forum/" + meetingId + "/view/log"), callback);
    }

    /**
     * 讨论区提交内容
     * /osg/app/forum
     *
     * @param tag
     * @param values
     * @param callback
     */
    public void sendForumContext(Object tag, Map<String, Object> values, OkHttpCallback callback) {
        okHttpUtil.post(tag, jointBaseUrl("/forum"), callback, values);
    }

    /**
     * 会议参会人摄像头抓拍
     * /osg/app/meeting/screenshot
     *
     * @param tag
     * @param values
     * @param callback
     */
    public void meetingScreenshot(Object tag, Map<String, Object> values, OkHttpCallback callback) {
        okHttpUtil.post(tag, jointBaseUrl("/meeting/screenshot"), callback, values);
    }


    /**
     * 获取参会人图像信息
     * /osg/app/meeting/{meetingId}/screenshot
     *
     * @param tag
     * @param values
     * @param callback
     */
    public void meetingGetScreenshot(Object tag, Map<String, String> values, OkHttpCallback callback) {
        String meetingId = (String) values.get("meetingId");
        values.remove("meetingId");

        String url = jointBaseUrl("/meeting/" + meetingId + "/screenshot");
        okHttpUtil.get(tag, com.zhongyou.meettvapplicaion.net.OkHttpUtil.jointUrl(url, values), callback);
    }

    /**
     * 获取服务器地址
     * */

    public void getHttpBaseUrl(Object tag, OkHttpCallback callback){
        String env="test";
        if (!BuildConfig.DEBUG){
            env="formal";
        }
        okHttpUtil.get(tag,BuildConfig.API_DOMAIN_NAME+"/osg/"+env+"/android/config",callback);
    }
/**
 * 开始录制
 * *//*
    public void startRecordVideo(Object tag, Map<String, Object> values, OkHttpCallback callback){
        okHttpUtil.post(BuildConfig.API_DOMAIN_NAME+"/osg/app/meeting/startRecordVideo",getCommonHead(), JSON.toJSONString(values),callback,tag);
    }


    *//**
     * 结束录制
     * *//*
    public void stopRecordVideo(Object tag, Map<String, Object> values, OkHttpCallback callback){
        okHttpUtil.post(tag,jointBaseUrl("/meeting/stopRecordVideo"),callback,values);
    }*/


}
