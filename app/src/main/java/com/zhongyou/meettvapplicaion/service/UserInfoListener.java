package com.zhongyou.meettvapplicaion.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.orhanobut.logger.Logger;
import com.zhongyou.meettvapplicaion.entities.AuthInfo;
import com.zhongyou.meettvapplicaion.entities.User;
import com.zhongyou.meettvapplicaion.entities.Wechat;
import com.zhongyou.meettvapplicaion.entities.base.BaseBean;
import com.zhongyou.meettvapplicaion.event.InitUserInfoSucceedEvent;
import com.zhongyou.meettvapplicaion.net.ApiClient;
import com.zhongyou.meettvapplicaion.net.OkHttpBaseCallback;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.Login.LoginHelper;
import com.zhongyou.meettvapplicaion.utils.RxBus;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;

/**
 * Created by yuna on 2017/7/25.
 */

public class UserInfoListener extends Service {

	private Runnable runnable;
	private Handler handler = new Handler();

	//服务是否已经创建
	public static boolean serviceIsCreate = false;
	//事件请求间隔
	private static int KEEP_TIME = 1000;

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		serviceIsCreate = true;
		Log.v("requestloginservic", "onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("requestloginservic", "请求用户");
		ApiClient.getInstance().requestUser(UserInfoListener.this, userCallback);

		runnable = new Runnable() {
			@Override
			public void run() {
				ApiClient.getInstance().requestUser(UserInfoListener.this, userCallback);
			}
		};
		return super.onStartCommand(intent, flags, startId);
	}

	private void poll() {
		handler.removeCallbacks(runnable);
		if (serviceIsCreate) {
			handler.postDelayed(runnable, KEEP_TIME);
		}
	}


	private OkHttpBaseCallback userCallback = new OkHttpBaseCallback<BaseBean<AuthInfo>>() {
		@Override
		public void onSuccess(BaseBean<AuthInfo> result) {
			AuthInfo authInfo = result.getData();
			Logger.e("login "+ JSON.toJSONString(result));
			if (serviceIsCreate) {
				Log.v("authinfoinfo", "authInfo.getWechat()==" + authInfo.getWechat());
				User user = result.getData().getUser();
				Wechat wechat = result.getData().getWechat();
				LoginHelper.savaUser(user);
				if (wechat != null) {
					LoginHelper.savaWeChat(wechat);
					if (TextUtils.isEmpty(Preferences.getUserPhoto())) {
						Preferences.setUserPhoto(wechat.getHeadimgurl());
					}
				}

				//发送登录初始化成功事件
				RxBus.sendMessage(new InitUserInfoSucceedEvent());

				stopService(UserInfoListener.this);
			}

		}

		@Override
		public void onErrorAll(Exception e) {
			super.onErrorAll(e);
			ToastUtils.showToast(e.getMessage());
			poll();
		}


	};

	public static void stopService(Context context) {
		if (serviceIsCreate) {
			serviceIsCreate = false;
			Intent intent = new Intent(context, UserInfoListener.class);
			context.stopService(intent);
		}
	}

	@Override
	public void onDestroy() {
		serviceIsCreate = false;
		super.onDestroy();

	}
}
