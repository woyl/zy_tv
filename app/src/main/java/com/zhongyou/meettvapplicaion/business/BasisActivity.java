package com.zhongyou.meettvapplicaion.business;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;

import com.elvishew.xlog.XLog;
import com.zhongyou.meettvapplicaion.BaseApplication;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.event.KickOffEvent;
import com.zhongyou.meettvapplicaion.net.ApiClient;
import com.zhongyou.meettvapplicaion.utils.FontCache;
import com.zhongyou.meettvapplicaion.utils.HintDialog;
import com.zhongyou.meettvapplicaion.utils.Logger;
import com.zhongyou.meettvapplicaion.utils.MyDialog;
import com.zhongyou.meettvapplicaion.utils.NetUtils;
import com.zhongyou.meettvapplicaion.utils.RxBus;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;
import com.zhongyou.meettvapplicaion.utils.statistics.ZYAgent;

import java.util.Calendar;

import io.agora.openlive.ui.BaseActivity;
import rx.Subscription;
import rx.functions.Action1;

public abstract class BasisActivity extends AppCompatActivity implements View.OnClickListener {

	public final String TAG = getClass().getSimpleName();

	public final String FTAG = Logger.lifecycle;

	protected Context mContext;
	private BaseApplication mMyApp;

	protected ApiClient client;
	protected String userId;
	protected String token;

	protected ProgressDialog progressDialog;
	public Typeface typeface;
	protected com.elvishew.xlog.Logger mLogger;
	private Subscription mSubscription;
	private HintDialog mDialog;

	public abstract String getStatisticsTag();


	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Logger.d(FTAG + TAG, "onNewIntent");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.d(FTAG + TAG, "onCreate");
		mContext = this;
		mMyApp = (BaseApplication) this.getApplicationContext();
//        userId = Preferences.getUserId();
//        token = Preferences.getToken();

		client = ApiClient.getInstance();
		typeface = FontCache.getTypeface("fonts/words.ttf", this);

		registerReceiver(mHomeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));


		mLogger = XLog.tag(TAG)
				.t()
				.st(2)
				.b()
				.build();

		mSubscription = RxBus.handleMessage(new Action1() {
			@Override
			public void call(Object o) {
				if (o instanceof KickOffEvent) {
					KickOffEvent kickOffEvent = (KickOffEvent) o;
					if (kickOffEvent.isKickOff()) {
						HintDialog.Builder builder = new HintDialog.Builder(BasisActivity.this);
						builder.setMessage("您的账号在别的地方进行了登陆 请重新登陆");
						builder.setTitle("账号异常");
						builder.setPositiveButtonMsg("确定");
						builder.setOnClickListener(new HintDialog.ClickListener() {
							@Override
							public void onClick(int tags) {
								switch (tags) {
									case MyDialog.BUTTON_POSITIVE:
										mDialog.dismiss();
										startActivity(new Intent(BasisActivity.this, SignInActivity.class));
										finish();
										break;
								}
							}
						});
						mDialog = builder.create();
						mDialog.show();
					} else {
						HintDialog.Builder builder = new HintDialog.Builder(BasisActivity.this);
						builder.setMessage("账号异常");
						builder.setTitle("账号异常");
						builder.setPositiveButtonMsg("确定");
						builder.setTitle("当前登陆信息已失效 请重新登陆");
						builder.setOnClickListener(new HintDialog.ClickListener() {
							@Override
							public void onClick(int tags) {
								switch (tags) {
									case MyDialog.BUTTON_POSITIVE:
										mDialog.dismiss();
										startActivity(new Intent(BasisActivity.this, SignInActivity.class));
										finish();
										break;
								}
							}
						});
						mDialog = builder.create();
						mDialog.show();
					}
				}
			}
		});
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Logger.d(FTAG + TAG, "onRestart");
	}

	@Override
	protected void onStart() {
		super.onStart();
		Logger.d(FTAG + TAG, "onStart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		ZYAgent.onPageStart(this, getStatisticsTag());
		mMyApp.setCurrentActivity(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		ZYAgent.onPageEnd(this, getStatisticsTag());
	}


	@Override
	protected void onStop() {
		super.onStop();
		Logger.d(FTAG + TAG, "onStop");
	}

	@Override
	protected void onDestroy() {
		Logger.d(FTAG + TAG, "onDestroy");
//        OkHttpUtil.getInstance().cancelTag(this);
		unregisterReceiver(mHomeKeyEventReceiver);
		cancelDialog();
		clearReferences();
		if (mSubscription != null) {
			mSubscription.unsubscribe();
		}
		if (mDialog != null) {
			mDialog.dismiss();
		}
		super.onDestroy();
	}

	private void clearReferences() {
		Activity currActivity = mMyApp.getCurrentActivity();
		if (this.equals(currActivity))
			mMyApp.setCurrentActivity(null);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Logger.d(FTAG + TAG, "onSaveInstanceState");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Logger.d(FTAG + TAG, "onRestoreInstanceState");
	}

	public void showToast(int resId) {
		ToastUtils.showToast(resId);
	}

	public void showToast(String str) {
		ToastUtils.showToast(str);
	}


	protected void showDialog(String message) {
//        if(progressDialog == null){
//            progressDialog = new ProgressDialog(this, R.style.MyDialog);
//            progressDialog.setMessage(message);
//            progressDialog.setCancelable(false);
//        }
//        progressDialog.show();
	}

	protected void cancelDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.cancel();
		}
	}

	protected BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
		String SYSTEM_REASON = "reason";
		String SYSTEM_HOME_KEY = "homekey";
		String SYSTEM_HOME_KEY_LONG = "recentapps";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
					//表示按了home键,程序到了后台
					Log.i(TAG, "mHomeKeyEventReceiver SYSTEM_HOME_KEY");
					onHomeKey();
				} else if (TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)) {
					//表示长按home键,显示最近使用的程序列表
					Log.i(TAG, "mHomeKeyEventReceiver SYSTEM_HOME_KEY_LONG");
					onHomeKeyLong();
				}
			}
		}
	};

	protected void onHomeKey() {

	}

	protected void onHomeKeyLong() {

	}

	public static final int MIN_CLICK_DELAY_TIME = 1000;
	private long lastClickTime = 0;

	/**
	 * 点击事件
	 *
	 * @param
	 */
	@Override
	public void onClick(View v) {
		long currentTime = Calendar.getInstance().getTimeInMillis();
		//防止重复提交订单，最小点击为1秒
		if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
			lastClickTime = currentTime;

			normalOnClick(v);
			if (!NetUtils.isNetworkConnected(this)) {//not network
				Toast.makeText(this, getResources().getString(R.string.network_err_toast), Toast.LENGTH_SHORT).show();
			} else {//have network
				checkNetWorkOnClick(v);
			}
		}

	}


	/**
	 * 检查网络，如果没有网络的话，就不能点击
	 *
	 * @param v
	 */
	protected void checkNetWorkOnClick(View v) {

	}

	/**
	 * 不用检查网络，可以直接触发的点击事件
	 *
	 * @param v
	 */
	protected void normalOnClick(View v) {

	}

}
