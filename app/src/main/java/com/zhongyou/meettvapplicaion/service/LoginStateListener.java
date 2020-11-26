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
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.zhongyou.meettvapplicaion.BuildConfig;
import com.zhongyou.meettvapplicaion.business.SignInActivity;
import com.zhongyou.meettvapplicaion.entities.LoginListenBean;
import com.zhongyou.meettvapplicaion.entities.base.BaseBean;
import com.zhongyou.meettvapplicaion.net.ApiClient;
import com.zhongyou.meettvapplicaion.net.OkHttpBaseCallback;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yuna on 2017/7/24.
 */

public class LoginStateListener extends Service {

	private Runnable runnable;
	private Handler handler = new Handler();
	private static int KEEP_TIME = 1000;
	/**
	 * 服务是否已经创建
	 */
	public static boolean serviceIsCreate = false;

	@Override
	public void onCreate() {
		super.onCreate();

		serviceIsCreate = true;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("requestlogin", "监听登录");
		requestLoginListen();
		runnable = new Runnable() {
			@Override
			public void run() {
				requestLoginListen();
				Log.v("requestlogin", "监听登录");
			}
		};

		return super.onStartCommand(intent, flags, startId);
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		serviceIsCreate = false;
	}




	private OkHttpBaseCallback requestLoginListenCallback = new OkHttpBaseCallback<BaseBean<LoginListenBean>>() {

		@Override
		public void onSuccess(BaseBean<LoginListenBean> result) {
			if (serviceIsCreate) {
				LoginListenBean bean = result.getData();
				if (bean != null && bean.isLogin() == true && !TextUtils.isEmpty(bean.getToken())) {
					Preferences.setToken(bean.getToken());
					//监听用户登录成功后初始化用户信息
					Log.v("requestlogin", "bean.getToken()==" + bean.getToken());

					Logger.e("LoginStateListener"+ bean.getToken());
					Intent intent = new Intent(getApplication(), UserInfoListener.class);
					startService(intent);
					stopService(LoginStateListener.this);
				} else if (bean != null && bean.isLogin() == false) {
					poll();
				}
			}

		}


		@Override
		public void onErrorAll(Exception e) {
			super.onErrorAll(e);
			ToastUtils.showToast(e.getMessage());
			poll();
		}
	};

	private void requestLoginListen() {
		ApiClient.getInstance().requestLoginListen(this, requestLoginListenCallback);
	}

	public static void stopService(Context context) {
		if (serviceIsCreate) {
			Intent intent = new Intent(context, LoginStateListener.class);
			context.stopService(intent);
		}

	}

	public static void actionStart(Context context) {
		if (!serviceIsCreate) {
			Intent intent = new Intent(context, LoginStateListener.class);
			context.startService(intent);
		}

	}

	private void poll() {
		handler.removeCallbacks(runnable);
		if (serviceIsCreate) {
			handler.postDelayed(runnable, KEEP_TIME);
		}

	}


}
