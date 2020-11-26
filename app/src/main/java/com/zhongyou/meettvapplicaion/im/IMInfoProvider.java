package com.zhongyou.meettvapplicaion.im;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.orhanobut.logger.Logger;
import com.zhongyou.meettvapplicaion.network.ApiService;
import com.zhongyou.meettvapplicaion.network.HttpsRequest;
import com.zhongyou.meettvapplicaion.network.RxSchedulersHelper;
import com.zhongyou.meettvapplicaion.network.RxSubscriber;

import io.reactivex.disposables.Disposable;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.UserInfo;

/**
 * @author luopan@centerm.com
 * @date 2020-03-05 08:11.
 */
public class IMInfoProvider {

	private ApiService mApiService;

	public IMInfoProvider() {
		mApiService = HttpsRequest.provideClientApi();
	}

	public void init(Context context) {
		initInfoProvider(context);
	}

	/**
	 * 初始化信息提供者，包括用户信息，群组信息，群主成员信息
	 */
	private void initInfoProvider(Context context) {
		RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
			@Override
			public UserInfo getUserInfo(String uid) {
				IMInfoProvider.this.getUserInfo(uid,"");
				return null;
			}
		}, true);

		RongIM.setGroupUserInfoProvider(new RongIM.GroupUserInfoProvider() {
			@Override
			public GroupUserInfo getGroupUserInfo(String gid, String uid) {
				getUserInfo(uid,gid);
				return null;
			}
		},true);

		RongIM.setGroupInfoProvider(new RongIM.GroupInfoProvider() {
			@Override
			public Group getGroupInfo(String gid) {
				getGroupAllUserInfo(gid);
				return null;
			}
		},true);
	}


	private void getGroupAllUserInfo(String groupId) {
		if (TextUtils.isEmpty(groupId)) {
			return;
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("groupId", groupId);
		mApiService.queryGroupUser(jsonObject).compose(RxSchedulersHelper.io_main()).subscribe(new RxSubscriber<JSONObject>() {
			@Override
			public void onSubscribe(Disposable d) {
			}

			@Override
			public void _onNext(JSONObject jsonObject) {
				Logger.e(jsonObject.toJSONString());
				if (jsonObject.getInteger("errcode") == 0) {
					if (jsonObject.getJSONObject("data").getString("code").equals("200")) {
//						JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("users");
						String groupName=jsonObject.getJSONObject("data").getString("groupName");
						Group group=new Group(groupId,groupName, Uri.parse(""));
						RongIM.getInstance().refreshGroupInfoCache(group);
					}
				}
			}
		});
	}

	private void getUserInfo(String userId,String gropId) {
		if (TextUtils.isEmpty(userId)) {
			return;
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("userId", userId);
		mApiService.queryUserInfo(jsonObject).compose(RxSchedulersHelper.io_main()).subscribe(new RxSubscriber<JSONObject>() {

			@Override
			public void onSubscribe(Disposable d) {
			}

			@Override
			public void _onNext(JSONObject jsonObject) {
				if (jsonObject.getInteger("errcode") == 0) {
					if (jsonObject.getJSONObject("data").getString("code").equals("200")) {
						jsonObject = jsonObject.getJSONObject("data");
						UserInfo userInfo = new UserInfo(userId, jsonObject.getString("userName"), Uri.parse(jsonObject.getString("userPortrait")));
						RongIM.getInstance().refreshUserInfoCache(userInfo);
						if (!TextUtils.isEmpty(gropId)){
							GroupUserInfo groupUserInfo=new GroupUserInfo(gropId,userId,userInfo.getName());
							RongIM.getInstance().refreshGroupUserInfoCache(groupUserInfo);
						}

					}
				}
			}
		});
	}
}
