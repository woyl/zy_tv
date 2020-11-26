package com.zhongyou.meettvapplicaion.network;


import com.alibaba.fastjson.JSONObject;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by jie on 2016/12/8.
 */

public interface ApiService {

	//获取Token
	@POST("/osg/app/rongcloud/getToken")
	Observable<JSONObject> getToken(@Body JSONObject jsonObject);

	//创建并加入群组
	@POST("/osg/app/rongcloud/groupCreateAndJoin")
	Observable<JSONObject> groupCreateAndJoin(@Body JSONObject jsonObject);


	//查询群组成员
	@POST("/osg/app/rongcloud/queryGroupUser")
	Observable<JSONObject> queryGroupUser(@Body JSONObject jsonObject);

	//查询用户信息
	@POST("/osg/app/rongcloud/queryUserInfo")
	Observable<JSONObject> queryUserInfo(@Body JSONObject jsonObject);


	/**
	 * 快速加入会议
	 */
	@POST("/osg/app/meeting/quickJoin")
	Observable<JSONObject> quickJoin(@Body JSONObject json);

	/**
	 * 版本检测
	 *
	 * @param appCode    zyou_online_android
	 * @param clientType 1
	 */
	@POST("osg/app/version/checkVersion")
	Observable<JSONObject> VersionCheck(@Body JSONObject jsonObject);


	/**
	 * 获取所有的ppt资料
	 */
	@GET("osg/app/meeting/materials")
	Observable<JSONObject> getAllPPT(@Query("meetingId") String meetingId);

	/**
	 * 获取推广页面数据
	 *
	 * @param type 1 会议 2 创客
	 */

	@GET("/osg/app/promotion/getPromotionPageId")
	Observable<JSONObject> getPromotionPageId(@Query("type") int type);

	/**
	 * 日更课程更多
	 */
	@GET("/osg/app/changeLessonByDay/getChangeLessonByDay")
	Observable<JSONObject> getMoreAudioCourse(@Query("pageNo") int pageNo);

	/**
	 * 创客栏目及所有系列
	 */
	@GET("/osg/app/column")
	Observable<JSONObject> getMakerColumn();

	/**
	 * 创客栏目返回系列数据
	 */
	@GET("/osg/app/series/getSeriesPageByType/{typeId}")
	Observable<JSONObject> getSeriesPageByType(@Path("typeId") String typeId, @Query("pageNo") int pageNo, @Query("pageSIze") int pageSIze);

	/**
	 * 根据系列返回页面数据
	 */
	@GET("/osg/app/ckPage")
	Observable<JSONObject> getMoreCourse(@Query("pageId") String pageId, @Query("seriesId") String id);

	/**
	 * 获取通知类型数据
	 */
	@GET("/osg/app/noticeMessage/getNoticeMessage")
	Observable<JSONObject> getNoticeMessageTypeByUserId();

	/**
	 * 获取消息详情
	 */
	@GET("/osg/app/noticeMessage/getNoticeMessageByType")
	Observable<JSONObject> getMessageDetailByUserId(@Query("userId") String userId, @Query("type") String type, @Query("pageNo") int pageNo);

	/**
	 * 获取未读消息
	 */
	@GET("/osg/app/noticeMessage/getIsExistUnread")
	Observable<JSONObject> getIsExistUnread();

}
