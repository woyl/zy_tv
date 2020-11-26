package com.zhongyou.meettvapplicaion.business;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.serenegiant.usb.USBMonitor;
import com.zhongyou.meettvapplicaion.ApiClient;
import com.zhongyou.meettvapplicaion.BaseException;
import com.zhongyou.meettvapplicaion.Constant;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.business.adapter.NewMeetingLaunchAdapter;
import com.zhongyou.meettvapplicaion.entities.Agora;
import com.zhongyou.meettvapplicaion.entities.Bucket;
import com.zhongyou.meettvapplicaion.entities.Meeting;
import com.zhongyou.meettvapplicaion.entities.MeetingJoin;
import com.zhongyou.meettvapplicaion.entities.base.BaseArrayBean;
import com.zhongyou.meettvapplicaion.net.OkHttpBaseCallback;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.CameraHelper;
import com.zhongyou.meettvapplicaion.utils.Logger;
import com.zhongyou.meettvapplicaion.utils.OkHttpCallback;
import com.zhongyou.meettvapplicaion.utils.SpUtil;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;
import com.zhongyou.meettvapplicaion.utils.UIDUtil;
import com.zhongyou.meettvapplicaion.view.GeneralAdapter;
import com.zhongyou.meettvapplicaion.view.RecyclerViewTV;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agora.openlive.ui.MeetingInitActivity;

public class SearchActivity extends BasisActivity {

	private String mKeyWords;
	private ImageView mBack;
	private EditText mSearch;
	private RecyclerViewTV mSearchRecyclerView;
	private ImageView mEmptyView;
	private Button mConfirm;
	private NewMeetingLaunchAdapter meetingAdapter;
	private ArrayList<Meeting> mOldDataLists;
	private String mMeetingName;
	USBMonitor usbMonitor;

	@Override
	public String getStatisticsTag() {
		return "搜索";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		initViews();
		initData();
		usbMonitor = new USBMonitor(this, new USBMonitor.OnDeviceConnectListener() {
			@Override
			public void onAttach(UsbDevice device) {

			}

			@Override
			public void onDettach(UsbDevice device) {

			}

			@Override
			public void onConnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock, boolean createNew) {

			}

			@Override
			public void onDisconnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock) {

			}

			@Override
			public void onCancel(UsbDevice device) {

			}
		});
	}

	private void initData() {
		mKeyWords = getIntent().getStringExtra("keyWords");
		if (mKeyWords != null && mKeyWords.length() > 0) {
			mSearch.setText(mKeyWords);
			mSearch.setSelection(mKeyWords.length());
			com.zhongyou.meettvapplicaion.net.ApiClient.getInstance().getAllMeeting(TAG, mKeyWords, searchMeetingCallBack);
		}

	}

	private void initViews() {
		mBack = findViewById(R.id.back);
		mSearch = findViewById(R.id.searchEditText);
		mSearchRecyclerView = findViewById(R.id.searchRecyclerView);
		mEmptyView = findViewById(R.id.emptyView);
		mConfirm = findViewById(R.id.confirm);

		if (Constant.isPadApplicaion) {
			mBack.setVisibility(View.VISIBLE);
			mBack.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		} else {
			mBack.setVisibility(View.GONE);
		}

		mSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().trim().length() <= 0) {
					mConfirm.setVisibility(View.GONE);
				} else {
					mConfirm.setVisibility(View.VISIBLE);
				}
			}
		});

		mSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					InputMethodManager inputmanger = (InputMethodManager) SearchActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputmanger.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
					com.zhongyou.meettvapplicaion.net.ApiClient.getInstance().getAllMeeting(TAG, mSearch.getText().toString().trim(), searchMeetingCallBack);
					return true;
				}
				return false;

			}
		});


		mConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSearch.getText().toString().trim().length() <= 0) {
					ToastUtils.showToast("请输入关键字");
				} else {
					InputMethodManager inputmanger = (InputMethodManager) SearchActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputmanger.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
					com.zhongyou.meettvapplicaion.net.ApiClient.getInstance().getAllMeeting(TAG, mSearch.getText().toString().trim(), searchMeetingCallBack);
				}
			}
		});

	}

	private com.zhongyou.meettvapplicaion.net.OkHttpCallback searchMeetingCallBack = new OkHttpBaseCallback<BaseArrayBean<Meeting>>() {

		@Override
		public void onSuccess(BaseArrayBean<Meeting> meetingBucket) {
			mLogger.e(JSON.toJSONString(meetingBucket));
			if (meetingBucket.getData().size() > 0) {
				oldMeetings = meetingBucket.getData();

				InputMethodManager inputmanger = (InputMethodManager) SearchActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputmanger.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);

				meetingAdapter = new NewMeetingLaunchAdapter(SearchActivity.this, meetingBucket.getData());
				LinearLayoutManager layout = new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.HORIZONTAL, false);
				mSearchRecyclerView.setLayoutManager(layout);

				mSearchRecyclerView.setAdapter(new GeneralAdapter(meetingAdapter));

				mSearchRecyclerView.setVisibility(View.VISIBLE);
				mEmptyView.setVisibility(View.GONE);
				mSearchRecyclerView.setFocusable(true);
				mSearchRecyclerView.requestFocus();
			} else {
				mEmptyView.setVisibility(View.VISIBLE);
				mSearchRecyclerView.setVisibility(View.GONE);
				mSearch.setFocusable(true);
				mSearch.setSelection(mSearch.getText().toString().trim().length());
			}
		}

	};

	ArrayList<Meeting> oldMeetings = new ArrayList<>();

	private void updateMeetingStatus(ArrayList<Meeting> meetings) {

		for (int i = 0; i < meetings.size(); i++) {
			boolean mExist = false;
			for (int j = 0; j < oldMeetings.size(); j++) {
				if (meetings.get(i).getId().equals(oldMeetings.get(j).getId())) {
					mExist = true;
					if (meetings.get(i).getMeetingProcess() != oldMeetings.get(j).getMeetingProcess()) {
						meetingAdapter.UpdateMeeting(meetings.get(i));
					}

				}
			}
			if (!mExist) {
				meetingAdapter.CreateMeeting(meetings.get(i));
			}
		}

		for (int i = 0; i < oldMeetings.size(); i++) {
			boolean mExist = false;
			for (int j = 0; j < meetings.size(); j++) {
				if (meetings.get(j).getId().equals(oldMeetings.get(i).getId())) {
//                    Log.v("oldmeetings9090")
					mExist = true;
				}
			}
			if (!mExist) {
				meetingAdapter.DeleteMeeting(oldMeetings.get(i));
			}
		}

	}

	public void goToMeeting(Meeting meeting) {
		boolean isInsertCamera = false;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			List<UsbDevice> deviceList = usbMonitor.getDeviceList();
			for (UsbDevice usbDevice : deviceList) {
				com.orhanobut.logger.Logger.e(JSON.toJSONString(usbDevice));
				if (usbDevice.getProductName() != null){
					if (  usbDevice.getProductName().toLowerCase().contains("cam")
							||usbDevice.getProductName().toLowerCase().contains("ub")
							|| usbDevice.getProductName().toLowerCase().contains("all in")) {
						isInsertCamera = true;
						break;
					}
				}
			}
			if (!isInsertCamera) {
				Toast.makeText(mContext, "未检测到摄像头，请插入摄像头再试！", Toast.LENGTH_SHORT).show();
				return;
			}

		} else {
			if (Camera.getNumberOfCameras() <= 0) {
				Toast.makeText(mContext, "未检测到摄像头，请插入摄像头再试！", Toast.LENGTH_SHORT).show();
				return;
			}
		}

		if (Build.VERSION.SDK_INT >= 23) {
			//视频会议拍照功能
			int REQUEST_CODE_CONTACT = 101;
			String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.CAMERA};
			//验证是否许可权限
			for (String str : permissions) {
				if (SearchActivity.this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
					//申请权限
					SearchActivity.this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
					return;
				}
			}
		}
		initDialog(meeting);
	}

	private Dialog dialog;

	private void initDialog(final Meeting meeting) {
		View view;
		if (Constant.isPadApplicaion) {
			mLogger.e("当前是平板设备");
			view = View.inflate(this, R.layout.dialog_meeting_code_pad, null);
		} else {
			view = View.inflate(this, R.layout.dialog_meeting_code, null);
		}

		//需要录制
		if (meeting.getIsRecord() == 1) {
			Constant.isNeedRecord = true;
		} else {
			Constant.isNeedRecord = false;
		}

		if (meeting.getScreenshotFrequency() == Meeting.SCREENSHOTFREQUENCY_INVALID) {
			view.findViewById(R.id.dialog_meetingcamera_layout).setVisibility(View.GONE);
		} else {
			view.findViewById(R.id.dialog_meetingcamera_layout).setVisibility(View.VISIBLE);
		}
		final EditText codeEdit = view.findViewById(R.id.code);

		if (!SpUtil.getString(meeting.getId(), "").equals("")) {
			codeEdit.setText(SpUtil.getString(meeting.getId(), ""));
			codeEdit.setSelection(SpUtil.getString(meeting.getId(), "").length());
		} else {
			codeEdit.setText("");
		}


		view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(codeEdit.getText())) {
					ArrayMap<String, Object> params = new ArrayMap<String, Object>();
					params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
					params.put("meetingId", meeting.getId());
					params.put("token", codeEdit.getText().toString());
					ApiClient.getInstance().verifyRole(TAG, verifyRoleCallback(meeting, codeEdit.getText().toString()), params);
				} else {
					codeEdit.setError("加入码不能为空");
				}
			}
		});
		view.findViewById(R.id.confirm).setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (view.findViewById(R.id.noCodeJoin).getVisibility() == View.GONE) {
					view.findViewById(R.id.cancel).setFocusable(true);
					view.findViewById(R.id.cancel).setFocusableInTouchMode(true);
				}
				return false;
			}
		});
		view.findViewById(R.id.noCodeJoin).setVisibility(meeting.getIsToken() == 1 ? View.GONE : View.VISIBLE);
		view.findViewById(R.id.noCodeJoin).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Camera.getNumberOfCameras() <= 0) {
					Toast.makeText(SearchActivity.this, "未检测到摄像头，请插入摄像头再试！", Toast.LENGTH_SHORT).show();
					return;
				}
				ArrayMap<String, Object> params = new ArrayMap<String, Object>();
				params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
				params.put("meetingId", meeting.getId());
				params.put("token", "");
				mMeetingName = meeting.getTitle();
				ApiClient.getInstance().verifyRole(TAG, verifyRoleCallback(meeting, ""), params);

			}
		});
		view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog = new Dialog(this, R.style.CustomDialog);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		dialog.show();
	}

	private OkHttpCallback verifyRoleCallback(final Meeting meeting, String token) {
		return new OkHttpCallback<Bucket<MeetingJoin>>() {

			@Override
			public void onSuccess(Bucket<MeetingJoin> meetingJoinBucket) {
				ArrayMap<String, Object> params = new ArrayMap<String, Object>();
				params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
				params.put("meetingId", meeting.getId());
				params.put("token", token);
				ApiClient.getInstance().joinMeeting(TAG, joinMeetingCallback, params);
				SpUtil.put(meeting.getId(), token.trim());
			}

			@Override
			public void onFailure(int errorCode, BaseException exception) {
				super.onFailure(errorCode, exception);
				Toast.makeText(SearchActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
			}
		};
	}

	private int mJoinRole;
	private OkHttpCallback joinMeetingCallback = new OkHttpCallback<Bucket<MeetingJoin>>() {

		@Override
		public void onSuccess(Bucket<MeetingJoin> meetingJoinBucket) {
			MeetingJoin meetingJoin = meetingJoinBucket.getData();
			Map<String, String> params = new HashMap<String, String>();
			params.put("channel", meetingJoin.getMeeting().getId());
			params.put("account", UIDUtil.generatorUID(Preferences.getUserId()));
			mJoinRole = meetingJoin.getRole();
			if (mJoinRole == 0) {
				params.put("role", "Publisher");
			} else if (mJoinRole == 1) {
				params.put("role", "Publisher");
			} else if (mJoinRole == 2) {
				params.put("role", "Subscriber");
			}
			Constant.resolvingPower = meetingJoin.getMeeting().getResolvingPower();
			ApiClient.getInstance().getAgoraKey(SearchActivity.this, params, getAgoraCallback(meetingJoin));
		}

		@Override
		public void onFailure(int errorCode, BaseException exception) {
			super.onFailure(errorCode, exception);
			Toast.makeText(SearchActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
		}
	};

	private OkHttpCallback getAgoraCallback(final MeetingJoin meetingJoin) {
		return new OkHttpCallback<Bucket<Agora>>() {

			@Override
			public void onSuccess(Bucket<Agora> agoraBucket) {
				Logger.info("agora", agoraBucket.getData().toString());
				dialog.dismiss();
				Intent intent = new Intent(mContext, MeetingInitActivity.class);
				intent.putExtra("agora", agoraBucket.getData());
				intent.putExtra("meeting", meetingJoin);
				intent.putExtra("role", mJoinRole);
				intent.putExtra("meetingName", mMeetingName);
				startActivity(intent);
			}

			@Override
			public void onFailure(int errorCode, BaseException exception) {
				Toast.makeText(getApplication(), "网络异常，请稍后重试！", Toast.LENGTH_SHORT).show();
			}

		};
	}


}
