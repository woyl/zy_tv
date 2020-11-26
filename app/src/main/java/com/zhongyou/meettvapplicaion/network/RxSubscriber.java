package com.zhongyou.meettvapplicaion.network;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.arch.lifecycle.LifecycleRegistry;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.zhongyou.meettvapplicaion.BaseApplication;
import com.zhongyou.meettvapplicaion.base.MyObserver;

import java.net.ConnectException;
import java.text.ParseException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;


/**
 * Created by lichuanbei on 2016/10/26.
 * 封装Subscriber,使用RxJava+Retrofit封装库
 * 参考博文：http://blog.csdn.net/jdsjlzx/article/details/51882661
 * http://blog.csdn.net/u012551350/article/details/51445357
 */

public abstract class RxSubscriber<T>  implements Observer<T> {
	protected final String TAG = this.getClass().getSimpleName();
	//对应HTTP的状态码
	private static final int UNAUTHORIZED = 401;
	private static final int FORBIDDEN = 403;
	private static final int NOT_FOUND = 404;
	private static final int REQUEST_TIMEOUT = 408;
	private static final int INTERNAL_SERVER_ERROR = 500;
	private static final int BAD_GATEWAY = 502;
	private static final int SERVICE_UNAVAILABLE = 503;
	private static final int GATEWAY_TIMEOUT = 504;

	public boolean isShowBaseError = true;

	private boolean isOnCompleted = false;


	/**
	 * 复写该方法，不使用super.onStart()视为不使用进度框
	 */
	@Override
	public void onSubscribe(Disposable d) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					sleep(0);
					handler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				if (!isOnCompleted) {//2s后还未获取数据则等待
					Activity activity = AppManager.getInstance().getCurrentActivity();

					if (activity != null) {
						LoadingDialog.showDialog(activity);
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};


	@Override
	public void onComplete() {
		isOnCompleted = true;
		LoadingDialog.closeDialog();
	}


	@Override
	public void onError(Throwable e) {
		e.printStackTrace();
		ApiException ex;
		if (!NetworkUtil.isConnected(BaseApplication.getInstance())) {
			ex = new ApiException(e, ApiConstants.NETWORD_ERROR);
			ex.msg = "网络不可用!请检查网络设置";
			onErrorBase(ex);
			return;
		}
		if (e instanceof NetworkErrorException) {
//            NetworkErrorException networkErrorException = (NetworkErrorException) e;
			ex = new ApiException(e, ApiConstants.HTTP_ERROR);
			ex.msg = "网络错误!请检查网络是否正常";  //均视为网络错误
			onErrorBase(ex);
			return;
		}
		if (e instanceof ConnectException) {
			ConnectException connectException = (ConnectException) e;
			ex = new ApiException(e, ApiConstants.HTTP_ERROR);
			ex.msg = "服务器连接超时!";
			L.i(TAG, connectException.getCause() + "");

			onErrorBase(ex);
			return;
		}
		if (e instanceof HttpException) {
			HttpException httpException = (HttpException) e;
			ex = new ApiException(e, ApiConstants.HTTP_ERROR);
			int code = httpException.code();
			switch (code) {
				case 0:
				case UNAUTHORIZED:
				case FORBIDDEN:
				case NOT_FOUND:
				case REQUEST_TIMEOUT:
				case GATEWAY_TIMEOUT:
				case INTERNAL_SERVER_ERROR:
				case BAD_GATEWAY:
				case SERVICE_UNAVAILABLE:
				default:
					ex.msg = "网络错误，连接服务器失败";  //均视为网络错误
					break;
			}
			onErrorBase(ex);
		} else if (e instanceof ServerException) {    //服务器返回的错误
			ServerException resultException = (ServerException) e;
			ex = new ApiException(resultException, ApiConstants.PARSE_ERROR);
			ex.msg = resultException.msg;
			onErrorBase(ex);
		} else if (e instanceof JSONException
				|| e instanceof ParseException) {
			ex = new ApiException(e, ApiConstants.PARSE_ERROR);
			ex.msg = "数据解析错误，请与我们联系";            //均视为解析错误
			onErrorBase(ex);

		} else {
			ex = new ApiException(e, ApiConstants.UNKNOWN);
			ex.msg = "";          //未知错误
			onErrorBase(ex);
		}
	}

	@Override
	public void onNext(T t) {
		Log.e("请求返回原始数据", "=" + t.toString());
		if (t instanceof JSONObject) {
			Log.e("====", " 是JSON");
		}
		_onNext(t);


	}

	public void onErrorBase(ApiException ex) {
		int code = ex.code;
		String msg = ex.msg;
		L.i(TAG, "元数据错误：code:" + code + "\nmsg" + msg);
		if (isShowBaseError) {
			if (!msg.equals(""))
				Toast.makeText(BaseApplication.getInstance(), msg, Toast.LENGTH_SHORT).show();
		}
		_onError(code, msg);
	}

	public abstract void _onNext(T t);



	public void _onError(int code, String msg) {
		isOnCompleted = true;
		LoadingDialog.closeDialog();
	}
}