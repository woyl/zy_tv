package com.zhongyou.meettvapplicaion.business;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;
import com.zhongyou.meettvapplicaion.BaseApplication;
import com.zhongyou.meettvapplicaion.BaseException;
import com.zhongyou.meettvapplicaion.BuildConfig;
import com.zhongyou.meettvapplicaion.Constant;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.entities.Bucket;
import com.zhongyou.meettvapplicaion.entities.WechatLogin;
import com.zhongyou.meettvapplicaion.event.InitUserInfoSucceedEvent;
import com.zhongyou.meettvapplicaion.net.ApiClient;
import com.zhongyou.meettvapplicaion.net.OkHttpBaseCallback;
import com.zhongyou.meettvapplicaion.network.ApiService;
import com.zhongyou.meettvapplicaion.network.HttpsRequest;
import com.zhongyou.meettvapplicaion.network.RxSchedulersHelper;
import com.zhongyou.meettvapplicaion.network.RxSubscriber;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.service.LoginStateListener;
import com.zhongyou.meettvapplicaion.utils.MethodUtils;
import com.zhongyou.meettvapplicaion.utils.OkHttpCallback;
import com.zhongyou.meettvapplicaion.utils.RxBus;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;
import com.zhongyou.meettvapplicaion.utils.statistics.ZYAgent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by wufan on 2017/8/23.
 */

public class SignInActivity extends BasisActivity {
	private ImageView qrCode;
	Subscription subscription;
	private Dialog dialog;
	private ApiClient apiClient;
	private ApiService mApiService;


	public static void actionStart(Context context) {
		Intent intent = new Intent(context, SignInActivity.class);
		context.startActivity(intent);
	}

	private boolean isForceLogout;

	public static void actionStart(Context context, boolean isForceLogout) {
		Intent intent = new Intent(context, SignInActivity.class);
		intent.putExtra("isForceLogout", isForceLogout);
		context.startActivity(intent);
	}

	protected void initExtraIntent() {
		isForceLogout = getIntent().getBooleanExtra("isForceLogougitt", false);
		apiClient = ApiClient.getInstance();
	}

	@Override
	public String getStatisticsTag() {
		return "登录界面";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);
		initExtraIntent();
		mApiService = HttpsRequest.provideClientApi();
		if (isForceLogout) {
			Toast.makeText(mContext, "is force logout true", Toast.LENGTH_SHORT).show();
			showLogoutForceDialog();
		}
		//注册设备信息,才能监听登录成功
		qrCode = (ImageView) this.findViewById(R.id.signin_qrcode);
		subscription = RxBus.handleMessage(new Action1() {
			@Override
			public void call(Object o) {
				if (o instanceof InitUserInfoSucceedEvent) {


					ZYAgent.onEvent(BaseApplication.getInstance(), "扫描登录成功");
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							getImToken();
							ToastUtils.showToast("扫描登录成功");
						}
					});

					//                    HomeTvActivity.actionStart(mContext);
					/*startActivity(new Intent(getApplication(), MeetingsActivityLaunch.class));
					finish();*/
				}
			}
		});
		//createQRCodeImage();
		requestQRCodeImageAndLogin();


	}

	private int retryCount = 0;

	private void getImToken() {
		retryCount++;
		com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
		object.put("nickname", Preferences.getUserName());
		object.put("userId", Preferences.getUserId());
		object.put("avatar", Preferences.getUserPhoto());
		mApiService
				.getToken(object).compose(RxSchedulersHelper.io_main()).subscribe(new RxSubscriber<JSONObject>() {

			@Override
			public void onSubscribe(Disposable d) {
				super.onSubscribe(d);
			}

			@Override
			public void _onNext(JSONObject jsonObject) {
				if (jsonObject.getInteger("errcode") == 0) {
					if (jsonObject.getJSONObject("data").getInteger("code") == 200) {
						Constant.IMTOKEN = jsonObject.getJSONObject("data").getString("token");
						Preferences.setImToken(Constant.IMTOKEN);
						IMConnect(Constant.IMTOKEN);
					} else {
						getImToken();
						ToastUtils.showToast("登陆聊天系统失败 正在重试第" + retryCount + "次");
						startActivity(new Intent(getApplication(), MeetingsActivityLaunch.class));
						finish();
					}
				} else {
					getImToken();
					ToastUtils.showToast("登陆聊天系统失败 正在重试第" + retryCount + "次");
					startActivity(new Intent(getApplication(), MeetingsActivityLaunch.class));
					finish();
				}
			}
		});
	}

	private void IMConnect(String imtoken) {
		RongIM.connect(imtoken, new RongIMClient.ConnectCallbackEx() {
			@Override
			public void OnDatabaseOpened(RongIMClient.DatabaseOpenStatus databaseOpenStatus) {


			}

			@Override
			public void onTokenIncorrect() {
//				Preferences.clear();
				/*runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ToastUtils.showToast("聊天系统登陆失败 请重新登陆");
					}
				});*/
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getImToken();
						ToastUtils.showToast("登陆聊天系统失败 正在重试第" + retryCount + "次");
					}
				});

			}

			@Override
			public void onSuccess(String s) {
				UserInfo userInfo = new UserInfo(Preferences.getUserId(), Preferences.getUserName(), Uri.parse(Preferences.getUserPhoto()));
				RongIM.getInstance().refreshUserInfoCache(userInfo);
				RongIM.getInstance().setCurrentUserInfo(userInfo);

				startActivity(new Intent(getApplication(), MeetingsActivityLaunch.class));
				finish();
			}

			@Override
			public void onError(RongIMClient.ErrorCode errorCode) {
//				Preferences.clear();
				/*runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ToastUtils.showToast("聊天系统登陆失败 请重新登陆");
					}
				});*/
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getImToken();
						ToastUtils.showToast("登陆聊天系统失败 正在重试第" + retryCount + "次");
					}
				});

			}
		});
	}


	/**
	 * 强制退出dialog
	 */
	private void showLogoutForceDialog() {
		if (dialog == null) {
			View view = View.inflate(getApplicationContext(), R.layout.dialog_common_ok, null);
			TextView title = (TextView) view.findViewById(R.id.title);
			title.setText(getString(R.string.login_invalid));
			view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.cancel();
				}
			});
			dialog = new Dialog(this, R.style.MyDialog);
			dialog.setContentView(view);
		}
		dialog.show();

	}


	//TODO 准备废弃
	public void createQRCodeImage() {
		String url = ApiClient.getLoginUrl();
		Log.d("qr code url", url);
		int height = (int) getResources().getDimension(R.dimen.my_px_386);
		Bitmap bp = MethodUtils.createQRImage(url, height, height);
		qrCode.setImageBitmap(bp);

		LoginStateListener.actionStart(getApplicationContext());
	}


	private Request.Builder addHeader(Map<String, String> headers, Request.Builder builder) {
		if (headers != null && headers.size() > 0) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				builder.addHeader(entry.getKey(), entry.getValue());
			}
			return builder;
		}
		return builder;
	}

	public void requestQRCodeImageAndLogin() {

		OkHttpClient okHttpClient = new OkHttpClient();

		Request.Builder requestBuilder = new Request.Builder()
				.url(Constant.getAPIHOSTURL() + "/osg/app/wechat/login/qrcode");

		mLogger.e(Constant.getAPIHOSTURL() + "/osg/app/wechat/login/qrcode");
		addHeader(ApiClient.getCommonHead(), requestBuilder);

		Logger.e("requestQRCodeImageAndLogin");
		Logger.e(JSON.toJSONString(ApiClient.getCommonHead()));
		Call call = okHttpClient.newCall(requestBuilder.build());
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				Logger.e(e.getMessage());
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String res = response.body().string();
				Logger.e(res);
				if (response.isSuccessful()) {
					try {
						JSONObject jsonObject = JSON.parseObject(res);

						if (jsonObject.getInteger("errcode") == 0) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									String imageUrl = jsonObject.getJSONObject("data").getString("url");
									Glide.with(SignInActivity.this).load(imageUrl).into(qrCode);

                                    /*// TODO: 2019-10-17 测试
                                    RxBus.sendMessage(new InitUserInfoSucceedEvent());*/

									//正确的
									LoginStateListener.actionStart(getApplicationContext());
								}
							});

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		});


//        apiClient.getWechatLoginQRImageURL(okHttpBaseCallback);
	}

	private OkHttpBaseCallback<String> okHttpBaseCallback = new OkHttpBaseCallback<String>() {
		@Override
		public void onSuccess(String entity) {
			JSONObject jsonObject = JSON.parseObject(entity);
			Logger.e("onSuccess111: " + entity);

			if (jsonObject.getInteger("errcode") == 0) {
				String imageUrl = jsonObject.getJSONObject("data").getString("url");
				Glide.with(SignInActivity.this).load(imageUrl).into(qrCode);
			}
		}

	};

	private OkHttpBaseCallback<InputStream> callback = new OkHttpBaseCallback<InputStream>() {
		@Override
		public void onSuccess(InputStream inputStream) {
			try {
				//将输入流数据转化为Bitmap位图数据
				Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

				qrCode.setImageBitmap(bitmap);

				LoginStateListener.actionStart(getApplicationContext());
			} catch (Exception e) {
				BuglyLog.e("onSuccess2", "inputStream");
				CrashReport.postCatchedException(e);
				e.printStackTrace();
			}
		}
	};

	private long mExitTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {//
				// 如果两次按键时间间隔大于2000毫秒，则不退出
				Toast.makeText(this, "再按一次退出中幼在线", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();// 更新mExitTime
			} else {
				quit(); // 否则退出程序
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void quit() {
		LoginStateListener.stopService(this);
		finish();
		System.exit(0);
	}


	@Override
	public void onDestroy() {
		if (subscription != null) {
			subscription.unsubscribe();
		}
		if (dialog != null) {
			dialog.cancel();
		}
		super.onDestroy();
	}


}
