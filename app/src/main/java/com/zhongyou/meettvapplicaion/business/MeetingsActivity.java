package com.zhongyou.meettvapplicaion.business;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.zhongyou.meettvapplicaion.ApiClient;
import com.zhongyou.meettvapplicaion.BaseException;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.business.adapter.MeetingAdapter;
import com.zhongyou.meettvapplicaion.entities.Agora;
import com.zhongyou.meettvapplicaion.entities.Bucket;
import com.zhongyou.meettvapplicaion.entities.Meeting;
import com.zhongyou.meettvapplicaion.entities.MeetingJoin;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.CameraHelper;
import com.zhongyou.meettvapplicaion.utils.Logger;
import com.zhongyou.meettvapplicaion.utils.OkHttpCallback;
import com.zhongyou.meettvapplicaion.utils.SpUtil;
import com.zhongyou.meettvapplicaion.utils.UIDUtil;
import com.zhongyou.meettvapplicaion.view.FocusFixedLinearLayoutManager;
import com.zhongyou.meettvapplicaion.view.GeneralAdapter;
import com.zhongyou.meettvapplicaion.view.RecyclerViewTV;
import com.zhongyou.meettvapplicaion.view.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.agora.openlive.model.ConstantApp;
import io.agora.openlive.ui.MeetingInitActivity;

/**
 * Created by whatisjava on 17-11-15.
 */

public class MeetingsActivity extends BasisActivity implements RecyclerViewTV.OnItemListener {

	private static final String TAG = MeetingsActivity.class.getSimpleName();

	private ImageView tipsImage;
	private RecyclerViewTV recyclerViewTV;
	private MeetingAdapter meetingAdapter;

	@Override
	public String getStatisticsTag() {
		return "列表页";
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meetings);

		init();

		ApiClient.getInstance().getAllMeeting(TAG, meetingsCallback);
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		ApiClient.getInstance().getAllMeeting(TAG, meetingsCallback);
	}

	private void init() {
		tipsImage = findViewById(R.id.tips);
		recyclerViewTV = findViewById(R.id.meeting_list);
		FocusFixedLinearLayoutManager gridlayoutManager = new FocusFixedLinearLayoutManager(this); // 解决快速长按焦点丢失问题.
		gridlayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
		recyclerViewTV.setLayoutManager(gridlayoutManager);
		recyclerViewTV.setFocusable(false);
		recyclerViewTV.addItemDecoration(new SpaceItemDecoration((int) (getResources().getDimension(R.dimen.my_px_20)), 0, (int) (getResources().getDimension(R.dimen.my_px_20)), 0));
		recyclerViewTV.setOnItemListener(this);
		recyclerViewTV.setSelectedItemAtCentered(true);
	}

	private Dialog dialog;

	private void initDialog(final Meeting meeting) {
		View view = View.inflate(this, R.layout.dialog_meeting_code, null);
		final EditText codeEdit = (EditText) view.findViewById(R.id.code);

		if (!SpUtil.getString(meeting.getId(), "").equals("")) {
			codeEdit.setText(SpUtil.getString(meeting.getId(), ""));
		} else {
			codeEdit.setText("");
		}

		view.findViewById(R.id.confirm).setOnClickListener(v -> {
			if (!TextUtils.isEmpty(codeEdit.getText())) {
				ArrayMap<String, Object> params = new ArrayMap<String, Object>();
				params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
				params.put("meetingId", meeting.getId());
				params.put("token", codeEdit.getText().toString());
				ApiClient.getInstance().verifyRole(TAG, verifyRoleCallback(meeting, codeEdit.getText().toString()), params);
			} else {
				codeEdit.setError("加入码不能为空");
			}
		});
		view.findViewById(R.id.cancel).setOnClickListener(v -> dialog.dismiss());
		dialog = new Dialog(this, R.style.CustomDialog);
		dialog.setContentView(view);
		dialog.show();
	}

	private OkHttpCallback verifyRoleCallback(final Meeting meeting, final String token) {
		return new OkHttpCallback<Bucket<MeetingJoin>>() {

			@Override
			public void onSuccess(Bucket<MeetingJoin> meetingJoinBucket) {
				if (CameraHelper.getNumberOfCameras() <= 0) {
					Toast.makeText(MeetingsActivity.this, "未检测到摄像头，请插入摄像头再试！", Toast.LENGTH_SHORT).show();
					return;
				}

				int profileIndex = 0;
				if (meeting.getResolvingPower() == 1.0) {
					profileIndex = 0;
				} else if (meeting.getResolvingPower() == 2.0) {
					profileIndex = 1;
				} else if (meeting.getResolvingPower() == 3.0) {
					profileIndex = 2;
				}

				mLogger.e("profileIndex:  " + profileIndex);

				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MeetingsActivity.this);
				SharedPreferences.Editor editor = pref.edit();
				editor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, profileIndex);
				editor.apply();

				SpUtil.put(meeting.getId(), token.trim());

				ArrayMap<String, Object> params = new ArrayMap<String, Object>();
				params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
				params.put("meetingId", meeting.getId());
				params.put("token", token);
				ApiClient.getInstance().joinMeeting(TAG, joinMeetingCallback, params);
			}

			@Override
			public void onFailure(int errorCode, BaseException exception) {
				super.onFailure(errorCode, exception);
				Toast.makeText(MeetingsActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
			}
		};
	}

	private OkHttpCallback joinMeetingCallback = new OkHttpCallback<Bucket<MeetingJoin>>() {

		@Override
		public void onSuccess(Bucket<MeetingJoin> meetingJoinBucket) {
			MeetingJoin meetingJoin = meetingJoinBucket.getData();
			Map<String, String> params = new HashMap<String, String>();
			params.put("channel", meetingJoin.getMeeting().getId());
			params.put("account", UIDUtil.generatorUID(Preferences.getUserId()));
			params.put("role", meetingJoin.getRole() == 0 ? "Publisher" : "Subscriber");
			ApiClient.getInstance().getAgoraKey(MeetingsActivity.this, params, getAgoraCallback(meetingJoin));
		}

		@Override
		public void onFailure(int errorCode, BaseException exception) {
			super.onFailure(errorCode, exception);
			Toast.makeText(MeetingsActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
		}
	};

	private OkHttpCallback getAgoraCallback(final MeetingJoin meetingJoin) {
		return new OkHttpCallback<Bucket<Agora>>() {

			@Override
			public void onSuccess(Bucket<Agora> agoraBucket) {
				Logger.info("agora", agoraBucket.getData().toString());
				dialog.dismiss();
				Intent intent = new Intent(getApplication(), MeetingInitActivity.class);
				intent.putExtra("agora", agoraBucket.getData());
				intent.putExtra("meeting", meetingJoin);
				startActivity(intent);
			}

			@Override
			public void onFailure(int errorCode, BaseException exception) {
				Toast.makeText(getApplication(), "网络异常，请稍后重试！", Toast.LENGTH_SHORT).show();
			}

		};
	}

	private OkHttpCallback meetingsCallback = new OkHttpCallback<Bucket<ArrayList<Meeting>>>() {

		@Override
		public void onSuccess(final Bucket<ArrayList<Meeting>> meetingBucket) {
			if (meetingBucket.getData().size() > 0) {
				meetingAdapter = new MeetingAdapter(getApplicationContext(), meetingBucket.getData());
				recyclerViewTV.setAdapter(new GeneralAdapter(meetingAdapter));

				recyclerViewTV.setOnItemClickListener((parent, itemView, position) -> {

					if (Build.VERSION.SDK_INT >= 23) {
						//视频会议拍照功能
						int REQUEST_CODE_CONTACT = 101;
						String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
								Manifest.permission.CAMERA};
						//验证是否许可权限
						for (String str : permissions) {
							if (MeetingsActivity.this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
								//申请权限
								MeetingsActivity.this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
								return;
							}
						}
					}

					Meeting meeting = meetingBucket.getData().get(position);
					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
						CameraManager cameraManager = (CameraManager) MeetingsActivity.this.getSystemService(Context.CAMERA_SERVICE);
						try {
							if (cameraManager != null) {
								int length = cameraManager.getCameraIdList().length;
								com.orhanobut.logger.Logger.e("cameraLength:---->" + length);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					if (CameraHelper.getNumberOfCameras() <= 0) {
						Toast.makeText(MeetingsActivity.this, "未检测到摄像头，请插入摄像头再试！", Toast.LENGTH_SHORT).show();
						return;
					}
					initDialog(meeting);
				});
				recyclerViewTV.setVisibility(View.VISIBLE);
				tipsImage.setVisibility(View.GONE);
			} else {
				recyclerViewTV.setVisibility(View.GONE);
				tipsImage.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onFailure(int errorCode, BaseException exception) {
			super.onFailure(errorCode, exception);
			Toast.makeText(MeetingsActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {

	}

	@Override
	public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {

	}

	@Override
	public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {

	}
}
