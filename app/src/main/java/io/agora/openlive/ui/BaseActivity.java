package io.agora.openlive.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.elvishew.xlog.XLog;
import com.serenegiant.usb.USBMonitor;
import com.zhongyou.meettvapplicaion.BaseApplication;
import com.zhongyou.meettvapplicaion.Constant;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.business.SignInActivity;
import com.zhongyou.meettvapplicaion.event.KickOffEvent;
import com.zhongyou.meettvapplicaion.network.ApiService;
import com.zhongyou.meettvapplicaion.network.HttpsRequest;
import com.zhongyou.meettvapplicaion.utils.HintDialog;
import com.zhongyou.meettvapplicaion.utils.MyDialog;
import com.zhongyou.meettvapplicaion.utils.RxBus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import io.agora.openlive.model.ConstantApp;
import io.agora.openlive.model.EngineConfig;
import io.agora.openlive.model.MyEngineEventHandler;
import io.agora.openlive.model.WorkerThread;
import io.agora.rtc.RtcEngine;
import rx.Subscription;
import rx.functions.Action1;

public abstract class BaseActivity extends AppCompatActivity {

	protected String TAG = this.getClass().getSimpleName();
	private final static Logger log = LoggerFactory.getLogger(BaseActivity.class);

	private String appId;
	protected com.elvishew.xlog.Logger mLogger;
	protected ApiService mApiService;
	protected Subscription mSubscription;
	private HintDialog mDialog;

	public void setAppId(String appId) {
		this.appId = appId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final View layout = findViewById(Window.ID_ANDROID_CONTENT);
		ViewTreeObserver vto = layout.getViewTreeObserver();
		mApiService = HttpsRequest.provideClientApi();
		if (vto!=null){
			vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					} else {
						layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}
					initUIandEvent();
				}
			});

		}

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
						HintDialog.Builder builder = new HintDialog.Builder(BaseActivity.this);
						builder.setTitle("账号异常");
						builder.setMessage("您的账号在别的地方进行了登陆 请重新登陆");
						builder.setPositiveButtonMsg("确定");

						builder.setOnClickListener(new HintDialog.ClickListener() {
							@Override
							public void onClick(int tags) {
								switch (tags) {
									case MyDialog.BUTTON_POSITIVE:
										mDialog.dismiss();
										startActivity(new Intent(BaseActivity.this, SignInActivity.class));
										finish();
										break;
								}
							}
						});

						mDialog = builder.create();
						mDialog.show();
					}else{
						HintDialog.Builder builder = new HintDialog.Builder(BaseActivity.this);
						builder.setMessage("当前登陆信息已失效 请重新登陆");
						builder .setTitle("账号异常");
						builder.setPositiveButtonMsg("确定");
						builder.setOnClickListener(new HintDialog.ClickListener() {
							@Override
							public void onClick(int tags) {
								switch (tags) {
									case MyDialog.BUTTON_POSITIVE:
										mDialog.dismiss();
										startActivity(new Intent(BaseActivity.this, SignInActivity.class));
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

	protected abstract void initUIandEvent();

	protected abstract void deInitUIandEvent();

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (isFinishing()) {
					return;
				}

				boolean checkPermissionResult = checkSelfPermissions();

				if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M)) {
					// so far we do not use OnRequestPermissionsResultCallback
				}
			}
		}, 500);
	}

	private boolean checkSelfPermissions() {
		return checkSelfPermission(Manifest.permission.RECORD_AUDIO, ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO) &&
				checkSelfPermission(Manifest.permission.CAMERA, ConstantApp.PERMISSION_REQ_ID_CAMERA) &&
				checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE);
	}


	public final void closeIME(View v) {
		InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(v.getWindowToken(), 0); // 0 force close IME
		v.clearFocus();
	}

	public final void closeIMEWithoutFocus(View v) {
		InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(v.getWindowToken(), 0); // 0 force close IME
	}

	public void openIME(final EditText v) {
		final boolean focus = v.requestFocus();
		if (v.hasFocus()) {
			final Handler handler = new Handler(Looper.getMainLooper());
			handler.post(new Runnable() {
				@Override
				public void run() {
					InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					boolean result = mgr.showSoftInput(v, InputMethodManager.SHOW_FORCED);
					log.debug("openIME " + focus + " " + result);
				}
			});
		}
	}

	public boolean checkSelfPermission(String permission, int requestCode) {
		log.debug("checkSelfPermission " + permission + " " + requestCode);
		if (PermissionChecker.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
			return false;
		}

		if (Manifest.permission.CAMERA.equals(permission)) {
			((BaseApplication) getApplication()).initWorkerThread(appId);
		}
		return true;
	}

	protected RtcEngine rtcEngine() {
		return ((BaseApplication) getApplication()).getWorkerThread().getRtcEngine();
	}

	protected final WorkerThread worker() {
		return ((BaseApplication) getApplication()).getWorkerThread();
	}

	protected final EngineConfig config() {
		return ((BaseApplication) getApplication()).getWorkerThread().getEngineConfig();
	}

	protected final MyEngineEventHandler event() {
		return ((BaseApplication) getApplication()).getWorkerThread().eventHandler();
	}

	public final void showLongToast(final String msg) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
	                                       @NonNull String permissions[], @NonNull int[] grantResults) {
		log.debug("onRequestPermissionsResult " + requestCode + " " + Arrays.toString(permissions) + " " + Arrays.toString(grantResults));
		switch (requestCode) {
			case ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					checkSelfPermission(Manifest.permission.CAMERA, ConstantApp.PERMISSION_REQ_ID_CAMERA);
				} else {
					finish();
				}
				break;
			}
			case ConstantApp.PERMISSION_REQ_ID_CAMERA: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE);
					((BaseApplication) getApplication()).initWorkerThread(appId);
				} else {
					finish();
				}
				break;
			}
			case ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				} else {
					finish();
				}
				break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mDialog!=null){
			mDialog.dismiss();
		}
		if (mSubscription!=null){
			mSubscription.unsubscribe();
		}
	}
}
