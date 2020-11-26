package com.zhongyou.meettvapplicaion.business;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhongyou.meettvapplicaion.BaseApplication;
import com.zhongyou.meettvapplicaion.BuildConfig;
import com.zhongyou.meettvapplicaion.Constant;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.entities.base.BaseErrorBean;
import com.zhongyou.meettvapplicaion.event.ResolutionChangeEvent;
import com.zhongyou.meettvapplicaion.net.ApiClient;
import com.zhongyou.meettvapplicaion.net.OkHttpBaseCallback;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.AppUtil;
import com.zhongyou.meettvapplicaion.utils.Login.LoginHelper;
import com.zhongyou.meettvapplicaion.utils.RxBus;
import com.zhongyou.meettvapplicaion.utils.statistics.ZYAgent;
import com.orhanobut.logger.Logger;

import io.agora.openlive.model.ConstantApp;

/**
 * Created by whatisjava on 17-9-4.
 */

public class SettingActivity extends BasisActivity {

	private SharedPreferences.Editor mEditor;
	private int mPrefIndex;
	private ImageView mBack;

	@Override
	public String getStatisticsTag() {
		return "设置";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		mEditor = pref.edit();

		pref = PreferenceManager.getDefaultSharedPreferences(this);
		mPrefIndex = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, ConstantApp.DEFAULT_PROFILE_IDX);
		Logger.v("当前选中的是："+mPrefIndex);
		findViewById(R.id.resolution).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showSelectDialog();
			}
		});

		findViewById(R.id.edit_profile).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(SettingActivity.this, QRCodeActivity.class), 0x101);
			}
		});

		findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, DocumentActivity.class));
			}
		});

		findViewById(R.id.exit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});

		mBack = findViewById(R.id.back);
		if (Constant.isPadApplicaion){
			mBack.setVisibility(View.VISIBLE);
		}else {
			mBack.setVisibility(View.GONE);
		}

		TextView version = findViewById(R.id.versionInfo);
		version.setText("V "+ AppUtil.getCurrentAppVersionName(this));
		findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * 分辨率的选择
	 */
	Dialog myDialog;
	private SharedPreferences pref;
	private void showSelectDialog() {

		final String[] Fruit = new String[]{"流畅(480X320)", "标准(640*480)", "高清(1280*720)"};
		 myDialog = new AlertDialog.Builder(SettingActivity.this)

				.setSingleChoiceItems(Fruit, mPrefIndex-2, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int position) {
						if (myDialog!=null){
							Logger.e("选中的分辨率为:"+(position+2));
							mEditor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX,position+2);
							mEditor.apply();
							mPrefIndex=position+2;
							ResolutionChangeEvent resolutionChangeEvent = new ResolutionChangeEvent();
							resolutionChangeEvent.setResolution(pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, ConstantApp.DEFAULT_PROFILE_IDX));
							RxBus.sendMessage(resolutionChangeEvent);
							myDialog.dismiss();
						}

					}
				})
				.create();
		myDialog.show();

	}

	private void showDialog() {
		ZYAgent.onEvent(BaseApplication.getInstance(), "点击退出登录");
		View view = View.inflate(this, R.layout.dialog_cancle_delete, null);
		TextView textView3 = (TextView) view.findViewById(R.id.textView3);
		textView3.setText("确定退出当前帐号？");

		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		view.findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
				ApiClient.getInstance().logout(new OkHttpBaseCallback<BaseErrorBean>() {
					@Override
					public void onSuccess(BaseErrorBean entity) {
						ZYAgent.onEvent(BaseApplication.getInstance(), "退出请求 成功");
						Preferences.clear();
					}

					@Override
					public void onErrorAll(Exception e) {
						super.onErrorAll(e);
						ZYAgent.onEvent(BaseApplication.getInstance(), "退出请求 失败");
						if (BuildConfig.DEBUG) {
							showToast("debug 退出登录请求失败");
						}
					}

					@Override
					public void onFinish() {
						LoginHelper.logout(SettingActivity.this, true);
					}
				});

			}
		});
		view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
				ZYAgent.onEvent(BaseApplication.getInstance(), "取消退出登录");
			}
		});
		dialog.setContentView(view);
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 0x100 || requestCode == 0x101) {
				finish();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
