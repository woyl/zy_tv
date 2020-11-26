package io.agora.openlive.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.zhongyou.meettvapplicaion.Constant;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.entities.Agora;
import com.zhongyou.meettvapplicaion.entities.MeetingJoin;
import com.zhongyou.meettvapplicaion.event.ResolutionChangeEvent;
import com.zhongyou.meettvapplicaion.im.ImUserInfo;
import com.zhongyou.meettvapplicaion.network.RxSchedulersHelper;
import com.zhongyou.meettvapplicaion.network.RxSubscriber;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.Logger;
import com.tendcloud.tenddata.TCAgent;
import com.zhongyou.meettvapplicaion.utils.RxBus;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.GroupNotificationMessage;

public class MeetingInitActivity extends BaseActivity {

	private static final String TAG = MeetingInitActivity.class.getSimpleName();
	public final String FTAG = Logger.lifecycle;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.e(TAG, "角色为:" + meetingJoin.getRole());
			int role = getIntent().getIntExtra("role", 0);
			String meetingName = getIntent().getStringExtra("meetingName");
			Log.e(TAG, "角色为role:=============" + role + "======================");
			if (role == 0) {
				forwardToLiveRoom(0, meetingName);
			} else if (role == 1) {
				forwardToLiveRoom(1, meetingName);
			} else if (role == 2) {
				forwardToLiveRoom(2, meetingName);
			}
		}
	};


	private Agora agora;
	private MeetingJoin meetingJoin;

	private AudioManager mAudioManager;

	@Override
	protected void onResume() {
		super.onResume();
		Logger.d(FTAG + TAG, "onResume");
		TCAgent.onPageStart(this, "视频通话");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Logger.d(FTAG + TAG, "onResume");
		TCAgent.onPageEnd(this, "视频通话");
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_openlive);

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
	}


	private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
		@Override
		public void onAudioFocusChange(int focusChange) {
			switch (focusChange) {
				case AudioManager.AUDIOFOCUS_GAIN:
					Log.d(TAG, "AUDIOFOCUS_GAIN [" + this.hashCode() + "]");

					break;
				case AudioManager.AUDIOFOCUS_LOSS:

					Log.d(TAG, "AUDIOFOCUS_LOSS [" + this.hashCode() + "]");
					break;
				case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:

					Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT [" + this.hashCode() + "]");
					break;
				case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:

					Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK [" + this.hashCode() + "]");
					break;
			}
		}
	};

	@Override
	protected void initUIandEvent() {
		meetingJoin = getIntent().getParcelableExtra("meeting");
		agora = getIntent().getParcelableExtra("agora");
		setAppId(agora.getAppID());

		handler.sendEmptyMessageDelayed(0, 600);
	}

	@Override
	protected void deInitUIandEvent() {
	}

	Intent intent;

	public void forwardToLiveRoom(int cRole, String meetingName) {
		mLogger.e("cRole=" + cRole + "-----" + "meetingJoin.getMeeting().getType()=" + meetingJoin.getMeeting().getType());
		ResolutionChangeEvent resolutionChangeEvent = new ResolutionChangeEvent();
		resolutionChangeEvent.setResolution(meetingJoin.getMeeting().getResolvingPower() + 1);
		RxBus.sendMessage(resolutionChangeEvent);
		if ((Constant.isAdminCount && Constant.isNeedRecord)) {
			// TODO: 2019-12-30 录制视频
			Map<String, String> map = new HashMap<>();
			map.put("appId", "47e3fe7ab51f499c8d0fbc2d6bfc6c5b");
			map.put("cname", meetingName);
			map.put("uid", Preferences.getUserId());

			OkGo.<String>post(Constant.getAPIHOSTURL() + "/osg/app/meeting/startRecordVideo")
					.params("appId", "47e3fe7ab51f499c8d0fbc2d6bfc6c5b")
					.params("cname", meetingName)
					.params("uid", Preferences.getUserId())
					.execute(new StringCallback() {
						@Override
						public void onError(Response<String> response) {
							mLogger.e(response.body());
						}

						@Override
						public void onSuccess(Response<String> response) {
							mLogger.e(response.body());
						}
					});
		}
		if (cRole == 0) {//主持人进入
			Constant.videoType = 0;
			//.putExtra("meeting",meetingJoin.getMeeting()
			intent = new Intent(MeetingInitActivity.this, MeetingBroadcastActivity.class);
//            intent = new Intent(MeetingInitActivity.this, InviteMeetingBroadcastActivity.class);
//			if (meetingJoin.getMeeting().getType() == 0) {
//				intent = new Intent(MeetingInitActivity.this, MeetingBroadcastActivity.class);
//			} else {
//				intent = new Intent(MeetingInitActivity.this, MeetingBroadcastActivity.class);
//			}
		} else if (cRole == 1) {//参会人进入
			Constant.videoType = 1;
			intent = new Intent(MeetingInitActivity.this, MeetingAudienceActivity.class);
//            intent = new Intent(MeetingInitActivity.this, InviteMeetingBroadcastActivity.class);
//            intent = new Intent(MeetingInitActivity.this, InviteMeetingAudienceActivity.class);
//			if (meetingJoin.getMeeting().getType() == 0) {
//				intent = new Intent(MeetingInitActivity.this, MeetingAudienceActivity.class);
//			} else {
//				intent = new Intent(MeetingInitActivity.this, MeetingAudienceActivity.class);
//			}
		} else if (cRole == 2) {
			Constant.videoType = 2;
			intent = new Intent(MeetingInitActivity.this, MeetingAudienceActivity.class);
		}


		intent.putExtra("meeting", meetingJoin);
		intent.putExtra("agora", agora);
		Constant.currentGroupId = meetingJoin.getMeeting().getId();
		intent.putExtra("meetingName", meetingName);
		groupCreateAndJoin(intent);


	}


	private void verifyIsInGroup(Intent intent) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("groupId", meetingJoin.getMeeting().getId());
		mApiService.queryGroupUser(jsonObject).compose(RxSchedulersHelper.io_main()).subscribe(new RxSubscriber<JSONObject>() {

			@Override
			public void onSubscribe(Disposable d) {

			}

			@Override
			public void _onNext(JSONObject jsonObject) {

				ImUserInfo imUserInfo = jsonObject.toJavaObject(ImUserInfo.class);

				try {
					if (imUserInfo.getErrcode() == 0) {
						if (imUserInfo.getData().getCode().equals("200")) {
							for (int i = 0; i < imUserInfo.getData().getUsers().size(); i++) {
								if (imUserInfo.getData().getUsers().get(i).getId().equals(Preferences.getUserId())) {
									startActivity(intent);
									finish();
									return;
								}
							}
							groupCreateAndJoin(intent);
						}
					}
				} catch (Exception e) {
					groupCreateAndJoin(intent);
				}
			}

			@Override
			public void _onError(int code, String msg) {
				ToastUtils.showToast(msg);
			}
		});
	}

	private void groupCreateAndJoin(Intent intent) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("groupId", meetingJoin.getMeeting().getId());
		jsonObject.put("userId", Preferences.getUserId());
		jsonObject.put("groupName", meetingJoin.getMeeting().getTitle());
		mApiService.groupCreateAndJoin(jsonObject).compose(RxSchedulersHelper.io_main()).subscribe(new RxSubscriber<JSONObject>() {
			@Override
			public void onSubscribe(Disposable d) {

			}

			@Override
			public void _onNext(JSONObject jsonObject) {
				if (jsonObject.getInteger("errcode") == 0) {
					if (jsonObject.getJSONObject("data").getInteger("code") == 200) {

						//向本地会话中插入一条消息。这条消息只是插入本地会话，
						// 不会实际发送给服务器和对方。该消息不一定插入本地数据库，
						// 是否入库由消息的属性决定
						JSONObject object = new JSONObject();
						object.put("operatorNickname", Preferences.getUserName());
						object.put("targetUserIds", new String[]{Preferences.getUserId()});
						object.put("targetUserDisplayNames", new String[]{Preferences.getUserName()});
						GroupNotificationMessage message = GroupNotificationMessage.obtain(
								Preferences.getUserId(),
								"Add",
								object.toString(),
								"加入群组");

						RongIM.getInstance().sendMessage(Conversation.ConversationType.GROUP, meetingJoin.getMeeting().getId(), message, "", "", new RongIMClient.SendMessageCallback() {
							@Override
							public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {

							}

							@Override
							public void onSuccess(Integer integer) {

							}
						});
						startActivity(intent);
						finish();

					} else {
						ToastUtils.showToast("进入聊天室失败");
					}
				}
			}
		});
	}

}
