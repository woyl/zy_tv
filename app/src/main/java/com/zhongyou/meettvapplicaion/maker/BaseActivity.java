package com.zhongyou.meettvapplicaion.maker;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;

import com.zhongyou.meettvapplicaion.business.BasisActivity;

/**
 * @author golangdorid@gmail.com
 * @date 2020/6/4 9:28 AM.
 * @
 */
abstract class BaseActivity<X extends ViewDataBinding> extends BasisActivity {
	protected X mBinding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = DataBindingUtil.setContentView(this, getLayoutId());
		initListener();
		initData();
		mBinding.setLifecycleOwner(this);
	}

	private void initListener() {
	}

	private void initData() {
	}

	abstract int getLayoutId();

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBinding.unbind();
	}
}
