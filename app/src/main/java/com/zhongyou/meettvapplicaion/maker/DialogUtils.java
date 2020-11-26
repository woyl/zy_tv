package com.zhongyou.meettvapplicaion.maker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiniu.android.common.Constants;
import com.serenegiant.usb.USBMonitor;
import com.zhongyou.meettvapplicaion.ApiClient;
import com.zhongyou.meettvapplicaion.BaseException;
import com.zhongyou.meettvapplicaion.Constant;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.business.MeetingsActivityLaunch;
import com.zhongyou.meettvapplicaion.business.SearchActivity;
import com.zhongyou.meettvapplicaion.entities.Agora;
import com.zhongyou.meettvapplicaion.entities.Bucket;
import com.zhongyou.meettvapplicaion.entities.HostUser;
import com.zhongyou.meettvapplicaion.entities.Meeting;
import com.zhongyou.meettvapplicaion.entities.MeetingJoin;
import com.zhongyou.meettvapplicaion.entity.NewMeetingJoin;
import com.zhongyou.meettvapplicaion.network.HttpsRequest;
import com.zhongyou.meettvapplicaion.network.RxSchedulersHelper;
import com.zhongyou.meettvapplicaion.network.RxSubscriber;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.Logger;
import com.zhongyou.meettvapplicaion.utils.OkHttpCallback;
import com.zhongyou.meettvapplicaion.utils.SpUtil;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;
import com.zhongyou.meettvapplicaion.utils.UIDUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agora.openlive.ui.MeetingInitActivity;

/**
 * @author golangdorid@gmail.com
 * @date 2020/6/9 2:18 PM.
 * @
 */
public class DialogUtils {
	Dialog dialog;
	int mJoinRole;
	private Context mContext;
	private static DialogUtils mDialogUtils;


	USBMonitor usbMonitor;

	private DialogUtils(Context context) {
		mContext = context;
		usbMonitor = new USBMonitor(mContext, new USBMonitor.OnDeviceConnectListener() {
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

	public static DialogUtils getInstance(Context context) {
		if (mDialogUtils == null) {
			mDialogUtils = new DialogUtils(context);
		}
		return mDialogUtils;
	}


	public void showQuickJoinDialog(Activity activity) {

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


		View view;

		if (Constant.isPadApplicaion) {
			view = View.inflate(activity, R.layout.dialog_meeting_code_pad, null);
		} else {
			view = View.inflate(activity, R.layout.dialog_meeting_code, null);
		}

		((TextView) view.findViewById(R.id.title)).setText("请输入快速码加入");
		view.findViewById(R.id.noCodeJoin).setVisibility(View.GONE);

		EditText code = view.findViewById(R.id.code);
		view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(code.getText().toString().trim())) {
					JSONObject params = new JSONObject();
					params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
					params.put("meetingId", "");
					params.put("token", code.getText().toString().trim());
					quickJoinMeeting(params);
				} else {
					code.setError("会议加入码不能为空");
				}

			}
		});
		view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog = new Dialog(activity, R.style.CustomDialog);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		dialog.show();
	}

	public void quickJoinMeeting(JSONObject params) {
		HttpsRequest.provideClientApi().quickJoin(params)
				.compose(RxSchedulersHelper.io_main())
				.subscribe(new RxSubscriber<JSONObject>() {
					@Override
					public void _onNext(JSONObject jsonObject) {
						if (jsonObject.getInteger("errcode") == 0) {
							NewMeetingJoin meetingJoin = JSON.parseObject(jsonObject.toJSONString(), NewMeetingJoin.class);
							mJoinRole = meetingJoin.getData().getRole();
							if (meetingJoin.getData().getMeeting().getIsRecord() == 1) {
								Constant.isNeedRecord = true;
							} else {
								Constant.isNeedRecord = false;
							}
							MeetingJoin oldmeeting = new MeetingJoin();
							HostUser hostUser = new HostUser();
							hostUser.setClientUid(meetingJoin.getData().getHostUser().getClientUid());
							hostUser.setHostUserName(meetingJoin.getData().getHostUser().getHostUserName());

							oldmeeting.setHostUser(hostUser);
							oldmeeting.setRole(mJoinRole);
							Meeting meeting = JSON.parseObject(jsonObject.getJSONObject("data").getJSONObject("meeting").toJSONString(), Meeting.class);
							oldmeeting.setMeeting(meeting);


							Map<String, String> params = new HashMap<String, String>();
							params.put("channel", oldmeeting.getMeeting().getId());
							params.put("account", UIDUtil.generatorUID(Preferences.getUserId()));
							mJoinRole = oldmeeting.getRole();
							if (mJoinRole == 0) {
								params.put("role", "Publisher");
							} else if (mJoinRole == 1) {
								params.put("role", "Publisher");
							} else if (mJoinRole == 2) {
								params.put("role", "Subscriber");
							}

							ApiClient.getInstance().getAgoraKey(this, params, getAgoraCallback(oldmeeting, meetingJoin.getData().getMeeting().getTitle()));

						} else {
							ToastUtils.showToast(jsonObject.getString("errmsg"));
						}
					}
				});
	}

	private OkHttpCallback getAgoraCallback(final MeetingJoin meetingJoin, String meetingTitle) {
		return new OkHttpCallback<Bucket<Agora>>() {

			@Override
			public void onSuccess(Bucket<Agora> agoraBucket) {

				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				Intent intent = new Intent(mContext, MeetingInitActivity.class);
				intent.putExtra("agora", agoraBucket.getData());
				intent.putExtra("meeting", meetingJoin);
				intent.putExtra("role", mJoinRole);
				intent.putExtra("meetingName", meetingTitle);
				mContext.startActivity(intent);
			}

			@Override
			public void onFailure(int errorCode, BaseException exception) {
				Toast.makeText(mContext, "网络异常，请稍后重试！", Toast.LENGTH_SHORT).show();
			}

		};
	}


	public void showNormalJoinMeetingDialog(Activity activity, String meetingID, int isToken) {
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

		View view;
		Log.e("DialogUtils", "showNormalJoinMeetingDialog: " + meetingID);
		if (Constant.isPadApplicaion) {
			view = View.inflate(activity, R.layout.dialog_meeting_code_pad, null);
		} else {
			view = View.inflate(activity, R.layout.dialog_meeting_code, null);
		}

		if (isToken == 1) {
			view.findViewById(R.id.noCodeJoin).setVisibility(View.GONE);
		} else {
			view.findViewById(R.id.noCodeJoin).setVisibility(View.VISIBLE);
		}

		final EditText codeEdit = view.findViewById(R.id.code);
		if (TextUtils.isEmpty(SpUtil.getString("meetingID", ""))) {
			codeEdit.setText("");
		} else {
			codeEdit.setText(SpUtil.getString("meetingID", ""));
		}
		codeEdit.setSelection(codeEdit.getText().toString().trim().length());

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


		view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Camera.getNumberOfCameras() <= 0) {
					Toast.makeText(activity, "未检测到摄像头，请插入摄像头再试！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (!TextUtils.isEmpty(codeEdit.getText())) {
					ArrayMap<String, Object> params = new ArrayMap<String, Object>();
					params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
					params.put("meetingId", meetingID);
					params.put("token", codeEdit.getText().toString());

					ApiClient.getInstance().verifyRole(this, verifyRoleCallback(activity, meetingID, codeEdit.getText().toString()), params);
				} else {
					codeEdit.setError("加入码不能为空");
				}
			}
		});
		view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		view.findViewById(R.id.noCodeJoin).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayMap<String, Object> params = new ArrayMap<String, Object>();
				params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
				params.put("meetingId", meetingID);
				params.put("token", "");
				ApiClient.getInstance().verifyRole(this, verifyRoleCallback(activity, meetingID,""), params);
			}
		});
		dialog = new Dialog(activity, R.style.CustomDialog);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		dialog.show();
	}

	private OkHttpCallback verifyRoleCallback(Activity activity, String meetingID, String token) {
		return new OkHttpCallback<Bucket<MeetingJoin>>() {

			@Override
			public void onSuccess(Bucket<MeetingJoin> meetingJoinBucket) {
				ArrayMap<String, Object> params = new ArrayMap<String, Object>();
				params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
				params.put("meetingId", meetingID);
				params.put("token", token);

				SpUtil.put(meetingID, token.trim());
				ApiClient.getInstance().joinMeeting(this, joinMeetingCallback(activity), params);
			}

			@Override
			public void onFailure(int errorCode, BaseException exception) {
				super.onFailure(errorCode, exception);
				Toast.makeText(activity, exception.getMessage(), Toast.LENGTH_SHORT).show();
			}
		};
	}

	private OkHttpCallback joinMeetingCallback(Activity activity) {

		return new OkHttpCallback<Bucket<MeetingJoin>>() {

			@Override
			public void onSuccess(Bucket<MeetingJoin> meetingJoinBucket) {
				MeetingJoin meetingJoin = meetingJoinBucket.getData();
				int isRecord = meetingJoin.getMeeting().getIsRecord();

				if (meetingJoin.getMeeting().getIsRecord() == 1) {
					Constant.isNeedRecord = true;
				} else {
					Constant.isNeedRecord = false;
				}
				Constant.resolvingPower = meetingJoin.getMeeting().getResolvingPower();

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

				ApiClient.getInstance().getAgoraKey(activity, params, getAgoraCallback(meetingJoin, meetingJoin.getMeeting().getTitle()));
			}

			@Override
			public void onFailure(int errorCode, BaseException exception) {
				super.onFailure(errorCode, exception);
				Toast.makeText(activity, exception.getMessage(), Toast.LENGTH_SHORT).show();
			}
		};
	}

	public void showSearchDialog(Activity activity) {
		if (activity.isFinishing()) {
			return;
		}
		if (activity.isDestroyed()) {
			return;
		}
		View view = View.inflate(activity, R.layout.dialog_search, null);
		EditText search = view.findViewById(R.id.edit_text);
		ImageButton confirm = view.findViewById(R.id.confirm);
		ImageButton cancel = view.findViewById(R.id.cancel);
		confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (search.getText().toString().trim().length() <= 0) {
					ToastUtils.showToast("请输入关键字搜索");
				} else {
					InputMethodManager inputmanger = (InputMethodManager) activity
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputmanger.hideSoftInputFromWindow(search.getWindowToken(), 0);
					Intent intent = new Intent(activity, SearchActivity.class);
					intent.putExtra("keyWords", search.getText().toString().trim());
					activity.startActivity(intent);
					if (dialog != null) {
						dialog.dismiss();
					}
				}
			}
		});


		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});

		dialog = new Dialog(activity, R.style.CustomDialog);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		dialog.show();
	}

}
