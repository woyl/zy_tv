package com.zhongyou.meettvapplicaion;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhongyou.meettvapplicaion.network.ApiService;
import com.zhongyou.meettvapplicaion.network.HttpsRequest;
import com.zhongyou.meettvapplicaion.network.RxSchedulersHelper;
import com.zhongyou.meettvapplicaion.network.RxSubscriber;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;

import io.reactivex.disposables.Disposable;

/**
 * @author golangdorid@gmail.com
 * @date 2020/6/3 2:44 PM.
 * @
 */
public class NetWorkUtils {

	private static ApiService mApiService = HttpsRequest.provideClientApi();
	private static NetWorkUtils mNetWorkUtils = new NetWorkUtils();

	private NetWorkUtils() {
	}

	public static NetWorkUtils getInstance() {
		if (mNetWorkUtils == null) {
			mNetWorkUtils = new NetWorkUtils();
		}
		return mNetWorkUtils;
	}

	public void getBannerData(OnResultCallBack callBack) {
		mApiService.getPromotionPageId(1).compose(RxSchedulersHelper.io_main()).subscribe(new RxSubscriber<JSONObject>() {
			@Override
			public void onSubscribe(Disposable d) {

			}

			@Override
			public void _onNext(JSONObject jsonObject) {
				if (jsonObject.getInteger("errcode") == 0) {
					callBack.onDataSuccess(jsonObject);
				} else {
					callBack.onDataFailure();
					ToastUtils.showToast(jsonObject.getString("errmsg"));
				}
			}
		});
	}


	public interface OnResultCallBack {
		void onDataSuccess(JSONObject jsonObject);

		void onDataFailure();
	}
}
