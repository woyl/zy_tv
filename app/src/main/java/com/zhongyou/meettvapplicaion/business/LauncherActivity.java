package com.zhongyou.meettvapplicaion.business;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhongyou.meettvapplicaion.BaseApplication;
import com.zhongyou.meettvapplicaion.BaseException;
import com.zhongyou.meettvapplicaion.BuildConfig;
import com.zhongyou.meettvapplicaion.Constant;
import com.zhongyou.meettvapplicaion.DialogContract;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.TVDialogActivity;
import com.zhongyou.meettvapplicaion.entities.User;
import com.zhongyou.meettvapplicaion.entities.UserData;
import com.zhongyou.meettvapplicaion.entities.Version;
import com.zhongyou.meettvapplicaion.entities.Wechat;
import com.zhongyou.meettvapplicaion.entities.base.BaseBean;
import com.zhongyou.meettvapplicaion.event.UserUpdateEvent;
import com.zhongyou.meettvapplicaion.net.ApiClient;
import com.zhongyou.meettvapplicaion.net.OkHttpBaseCallback;
import com.zhongyou.meettvapplicaion.net.OkHttpCallback;
import com.zhongyou.meettvapplicaion.network.ApiService;
import com.zhongyou.meettvapplicaion.network.HttpsRequest;
import com.zhongyou.meettvapplicaion.network.RxSchedulersHelper;
import com.zhongyou.meettvapplicaion.network.RxSubscriber;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.ApkUtil;
import com.zhongyou.meettvapplicaion.utils.DeviceUtil;
import com.zhongyou.meettvapplicaion.utils.Installation;
import com.zhongyou.meettvapplicaion.utils.Logger;
import com.zhongyou.meettvapplicaion.utils.Login.LoginHelper;
import com.zhongyou.meettvapplicaion.utils.RxBus;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;
import com.zhongyou.meettvapplicaion.utils.UUIDUtils;
import com.zhongyou.meettvapplicaion.utils.statistics.ZYAgent;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tendcloud.tenddata.TCAgent;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by whatisjava on 17-7-11.
 */

public class LauncherActivity extends BasisActivity implements EasyPermissions.PermissionCallbacks {

	private int mIntentType;
	private static final int DIALOG_REQUEST_CODE = 23;
	private Version mVersion;
	private ApiService mApiService;


	@Override
	public String getStatisticsTag() {
		return "启动页";
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApiService = HttpsRequest.provideClientApi();
		if (EasyPermissions.hasPermissions(LauncherActivity.this, perms)) {
			registerDevice();
		} else {
			EasyPermissions.requestPermissions(LauncherActivity.this, "请授予必要的权限", 0, perms);
		}

	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}

	@Override
	public void onPermissionsGranted(int i, @NonNull List<String> list) {
		registerDevice();
	}

	@Override
	public void onPermissionsDenied(int i, @NonNull List<String> list) {
		mLogger.e(list.toString());
		if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
			new AppSettingsDialog.Builder(this)
					.setTitle("权限不足")
					.setRationale("请授予必须的权限，否则应用无法正常运行")
					.build().show();
		}
	}

	private String[] perms = {
			Manifest.permission.CAMERA,
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.RECORD_AUDIO,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE
	};

	@Override
	protected void onResume() {
		super.onResume();
		//onNewIntent 后onResume 请求回调token错误又调用退出登录方法,死循环.
		//退出登录case,直接返回,不再执行任何请求,因为下一步跳转到登录界面
		if (mIntentType == LoginHelper.LOGIN_TYPE_LOGOUT || mIntentType == LoginHelper.LOGIN_TYPE_EXIT) {
			return;
		}
		//复位标志
		mIntentType = 0;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
//        setIntent(intent);//must store the new intent unless getIntent() will return the old one
		//如果没销毁,注销只调用onNewIntent,需要在这里处理注销逻辑
		processExtraData(intent);


	}

	private void processExtraData(Intent intent) {
		mIntentType = intent.getIntExtra(LoginHelper.LOGIN_TYPE, 0);
		Logger.d(TAG, "mIntentType " + mIntentType);
		if (mIntentType == LoginHelper.LOGIN_TYPE_EXIT) {
			//退出应用
			Logger.d(TAG, "退出应用");
			quit();
		}
		//复位标志
		mIntentType = 0;
	}

	private void quit() {
		ZYAgent.onEvent(getApplicationContext(), "退出应用_退出强制升级");
		finish();
	}


	private void versionCheck() {
		//先检查升级,再进入主页或者登录
		ApiClient.getInstance().versionCheck(this, new OkHttpBaseCallback<BaseBean<Version>>() {
			@Override
			public void onSuccess(BaseBean<Version> entity) {
				mVersion = entity.getData();
				com.orhanobut.logger.Logger.e(JSON.toJSONString(mVersion));
				if (mVersion == null) {
					goHomeOrLogin();
					return;
				}

				if (mVersion.getImportance() == 1) {
					goHomeOrLogin();
					return;
				}

				//1:最新版，不用更新 2：小改动，可以不更新 3：建议更新 4 强制更新
				if (mVersion.getImportance() == 4) {
					try {
						mLogger.e(ApkUtil.compareVersion(mVersion.getVersionDesc(), BuildConfig.VERSION_NAME) > 0);
						if (ApkUtil.compareVersion(mVersion.getVersionDesc(), BuildConfig.VERSION_NAME) > 0) {
							startActivity(new Intent(mContext, UpdateActivity.class).putExtra("version", mVersion).putExtra("isForceUpDate", true));
							finish();
						}
					} catch (Exception e) {
						goHomeOrLogin();
					}

				} else if (mVersion.getImportance() == 2 || mVersion.getImportance() == 3) {
					//推荐更新
					Intent intent = new Intent(LauncherActivity.this, TVDialogActivity.class);
					intent.putExtra(TVDialogActivity.ARG_TITLE_RES, R.string.dialog_title);
					intent.putExtra(TVDialogActivity.ARG_ICON_RES, R.mipmap.ic_launcher);
					intent.putExtra(TVDialogActivity.ARG_NEGATIVE_RES, R.string.dialog_no);
					intent.putExtra(TVDialogActivity.ARG_POSITIVE_RES, R.string.dialog_yes);
//					intent.putExtra(TVDialogActivity.ARG_DESC_RES, "最新版本:"+ mVersion.getVersionDesc());
					intent.putExtra(TVDialogActivity.ARG_DESC_RES, getResources().getString(R.string.dialog_desc) + mVersion.getVersionDesc());
					startActivityForResult(intent, DIALOG_REQUEST_CODE);
				} else {
					goHomeOrLogin();
				}

			}


			@Override
			public void onErrorAll(Exception e) {
				super.onErrorAll(e);
				String str = e.getMessage();
				showToast(str);
				ZYAgent.onEvent(mContext, str);
				BuglyLog.e(TAG, str);
				goHomeOrLogin();
			}

		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == DIALOG_REQUEST_CODE) {
			if (resultCode == RESULT_OK) { //retry
				startActivityForResult(new Intent(mContext, UpdateActivity.class).putExtra("version", mVersion).putExtra("isForceUpDate", false), DIALOG_REQUEST_CODE);
			} else if (resultCode == RESULT_CANCELED) {
				goHomeOrLogin();
			} else if (resultCode == 0x002) {
				goHomeOrLogin();
			}
		}
	}

	@SuppressLint("MissingPermission")
	private void registerDevice() {
		ZYAgent.onEvent(BaseApplication.getInstance(), "注册设备");
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;
		int height = metric.heightPixels;
		float density = metric.density;
		int densityDpi = metric.densityDpi;
		String uuid = UUIDUtils.getUUID(getApplication());
		if (TextUtils.isEmpty(uuid)) {
			TCAgent.onEvent(LauncherActivity.this, "注册设备时获取DeviceUUID失败，请联系系统管理员");
			Toast.makeText(this, "注册设备时获取DeviceUUID失败，请联系系统管理员!", Toast.LENGTH_SHORT).show();
			return;
		} else {
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("uuid", uuid);
				jsonObject.put("aliDeviceUUID", UUIDUtils.getALIDeviceUUID(getApplication()));
				jsonObject.put("androidId", TextUtils.isEmpty(Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID)) ? "" : Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
				jsonObject.put("manufacturer", Build.MANUFACTURER);
				jsonObject.put("name", Build.BRAND);
				jsonObject.put("model", Build.MODEL);
				jsonObject.put("version", "");
				jsonObject.put("sdkVersion", "" + Build.VERSION.SDK_INT);
				jsonObject.put("screenDensity", "width:" + width + ",height:" + height + ",density:" + density + ",densityDpi:" + densityDpi);
				jsonObject.put("display", Build.DISPLAY);
				jsonObject.put("finger", Build.FINGERPRINT);
				jsonObject.put("appVersion", BuildConfig.FLAVOR + "_" + BuildConfig.VERSION_NAME + "_" + BuildConfig.VERSION_CODE);
				jsonObject.put("source", 1);
				jsonObject.put("cpuSerial", Installation.getCPUSerial());
				try {
					TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					jsonObject.put("androidDeviceId", tm != null ? tm.getDeviceId() : "");
				} catch (SecurityException e) {
					e.printStackTrace();
				}
				jsonObject.put("buildSerial", Build.SERIAL);
				jsonObject.put("internalSpace", DeviceUtil.getDeviceTotalMemory(this));
				jsonObject.put("internalFreeSpace", DeviceUtil.getDeviceAvailMemory(this));
				jsonObject.put("sdSpace", DeviceUtil.getDeviceTotalInternalStorage());
				jsonObject.put("sdFreeSpace", DeviceUtil.getDeviceAvailInternalStorage());
				client.deviceRegister(this, jsonObject.toString(), respStatusCallback);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private OkHttpBaseCallback respStatusCallback = new OkHttpBaseCallback<BaseBean>() {
		@Override
		public void onSuccess(BaseBean entity) {
			ZYAgent.onEvent(BaseApplication.getInstance(), "注册成功");
		}

		@Override
		public void onErrorAll(Exception e) {
			if (e.getMessage() != null) {
				ZYAgent.onEvent(BaseApplication.getInstance(), "注册失败" + e.getMessage());
			}

		}

		@Override
		public void onFinish() {
			versionCheck();
		}
	};

	private void goHomeOrLogin() {
		if (Preferences.isLogin()) {
			requestUser();
		} else {
			startActivity(new Intent(this, SignInActivity.class));
			finish();
		}
	}


	public void requestUser() {
		client.requestUser(this, new OkHttpBaseCallback<BaseBean<UserData>>() {
			@Override
			public void onSuccess(BaseBean<UserData> entity) {
				if (entity == null || entity.getData() == null || entity.getData().getUser() == null) {
					showToast("数据为空");
					return;
				}
				User user = entity.getData().getUser();
				Wechat wechat = entity.getData().getWechat();
				LoginHelper.savaUser(user);
				if (wechat != null) {
					LoginHelper.savaWeChat(wechat);
				}

				getImToken();

			}

			@Override
			public void onFinish() {
			}

			@Override
			public void onFailure(int errorCode, BaseException exception) {
				super.onFailure(errorCode, exception);
				RxBus.sendMessage(new UserUpdateEvent());
//                HomeTvActivity.actionStart(LauncherActivity.this);
				startActivity(new Intent(getApplication(), MeetingsActivityLaunch.class));
				finish();
			}
		});
	}

	private void getImToken() {
		com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
		object.put("nickname", Preferences.getUserName());
		object.put("userId", Preferences.getUserId());
		object.put("avatar", Preferences.getUserPhoto());
		mApiService
				.getToken(object).compose(RxSchedulersHelper.io_main()).subscribe(new RxSubscriber<com.alibaba.fastjson.JSONObject>() {

			@Override
			public void onSubscribe(Disposable d) {
				super.onSubscribe(d);
			}

			@Override
			public void _onNext(com.alibaba.fastjson.JSONObject jsonObject) {
				if (jsonObject.getInteger("errcode") == 0) {
					if (jsonObject.getJSONObject("data").getInteger("code") == 200) {
						Constant.IMTOKEN = jsonObject.getJSONObject("data").getString("token");
						Preferences.setImToken(Constant.IMTOKEN);
						IMConnect(Constant.IMTOKEN);
					} else {
						ToastUtils.showToast("登陆聊天系统失败");
						Preferences.clear();
						startActivity(new Intent(getApplication(), SignInActivity.class));
						finish();
					}
				} else {
					ToastUtils.showToast("登陆聊天系统失败 请重新登陆");
					Preferences.clear();
					startActivity(new Intent(getApplication(), SignInActivity.class));
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
				ToastUtils.showToast("聊天系统登陆失败 请重新登陆");
				Preferences.clear();
				startActivity(new Intent(LauncherActivity.this, SignInActivity.class));
			}

			@Override
			public void onSuccess(String s) {
				com.orhanobut.logger.Logger.e("登陆IM成功：" + s);
				if (TextUtils.isEmpty(Preferences.getUserId())) {
					ToastUtils.showToast("登陆信息已失效 请重新登陆");
					Preferences.clear();
					startActivity(new Intent(LauncherActivity.this, SignInActivity.class));
					return;
				}
				if (TextUtils.isEmpty(Preferences.getUserName())) {
					ToastUtils.showToast("登陆信息已失效 请重新登陆");
					Preferences.clear();
					startActivity(new Intent(LauncherActivity.this, SignInActivity.class));
					return;
				}
				if (TextUtils.isEmpty(Preferences.getUserPhoto())) {

					ToastUtils.showToast("登陆信息已失效 请重新登陆");
					Preferences.clear();
					startActivity(new Intent(LauncherActivity.this, SignInActivity.class));
					return;
				}

				UserInfo userInfo = new UserInfo(Preferences.getUserId(), Preferences.getUserName(), Uri.parse(Preferences.getUserPhoto()));
				RongIM.getInstance().refreshUserInfoCache(userInfo);
				RongIM.getInstance().setCurrentUserInfo(userInfo);

				startActivity(new Intent(getApplication(), MeetingsActivityLaunch.class));
				finish();
			}

			@Override
			public void onError(RongIMClient.ErrorCode errorCode) {
				ToastUtils.showToast("聊天系统登陆失败 请重新登陆");
				Preferences.clear();
				startActivity(new Intent(LauncherActivity.this, SignInActivity.class));
			}
		});
	}


}
