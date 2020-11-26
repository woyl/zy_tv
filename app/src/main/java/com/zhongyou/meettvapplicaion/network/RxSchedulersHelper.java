package com.zhongyou.meettvapplicaion.network;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by lichuanbei on 2016/10/26.
 * 处理Rx线程:参看博文：http://www.jianshu.com/p/f3f0eccbcd6f
 */

public class RxSchedulersHelper {


	public static <T> ObservableTransformer<T, T> io_main() {
		return upstream ->
				upstream.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread());
	}
}
