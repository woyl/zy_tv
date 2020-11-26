package io.agora.openlive.ui;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhongyou.meettvapplicaion.ApiClient;
import com.zhongyou.meettvapplicaion.BaseException;
import com.zhongyou.meettvapplicaion.BuildConfig;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.entities.Agora;
import com.zhongyou.meettvapplicaion.entities.Audience;
import com.zhongyou.meettvapplicaion.entities.AudienceVideo;
import com.zhongyou.meettvapplicaion.entities.Bucket;
import com.zhongyou.meettvapplicaion.entities.HostUser;
import com.zhongyou.meettvapplicaion.entities.Material;
import com.zhongyou.meettvapplicaion.entities.Meeting;
import com.zhongyou.meettvapplicaion.entities.MeetingHostingStats;
import com.zhongyou.meettvapplicaion.entities.MeetingJoin;
import com.zhongyou.meettvapplicaion.entities.MeetingJoinStats;
import com.zhongyou.meettvapplicaion.entities.MeetingMaterialsPublish;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.CameraHelper;
import com.zhongyou.meettvapplicaion.utils.OkHttpCallback;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;
import com.zhongyou.meettvapplicaion.utils.UIDUtil;
import com.zhongyou.meettvapplicaion.utils.statistics.ZYAgent;
import com.zhongyou.meettvapplicaion.view.SpaceItemDecoration;
import com.squareup.picasso.Picasso;
import com.tendcloud.tenddata.TCAgent;
import com.yunos.tv.alitvasrsdk.ASRCommandReturn;
import com.yunos.tv.alitvasrsdk.AliTVASRManager;
import com.yunos.tv.alitvasrsdk.OnASRCommandListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;

import io.agora.AgoraAPI;
import io.agora.AgoraAPIOnlySignal;
import io.agora.openlive.model.AGEventHandler;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static java.lang.System.setProperty;

public class InviteMeetingAudienceActivity extends BaseActivity implements AGEventHandler {

    private final static Logger LOG = LoggerFactory.getLogger(InviteMeetingAudienceActivity.class);

    private final String TAG = InviteMeetingAudienceActivity.class.getSimpleName();

    private MeetingJoin meetingJoin;
    private Meeting meeting;
    private Agora agora;
    private String broadcasterId;
    private Material currentMaterial;
    private int doc_index = 0;

    private RecyclerView audienceRecyclerView;
    private AudienceVideoAdapter audienceVideoAdapter;

    private FrameLayout broadcasterView, broadcasterSmallView;
    private TextView broadcasterNameText, broadcasterTipsText;
    private ImageButton muteAudioButton;
    private Button exitMeetingButton;
    private ImageView docImage;
    private TextView pageText;

    private String channelName;

    private boolean isMuted = false;

    private static final String DOC_INFO = "doc_info";

    /**TextureView*/
    private TextureView remoteBroadcasterSurfaceView;

    //禁用语音搜索相关代码
    private AliTVASRManager mAliTVASRManager = new AliTVASRManager();
    private OnASRCommandListener mASRCommandListener = new OnASRCommandListener() {
        @Override
        public void onASRServiceStatusUpdated(ASRServiceStatus arg0) {
            if (arg0 == ASRServiceStatus.ASR_SERVICE_STATUS_CONNECTED) {
                //禁用语音
                try {
                    mAliTVASRManager.setAliTVASREnable(false);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onASRStatusUpdated(ASRStatus arg0, Bundle arg1) {
        }

        @Override
        public ASRCommandReturn onASRResult(String arg0, boolean arg1) {
            return new ASRCommandReturn();
        }

        @Override
        public ASRCommandReturn onNLUResult(String arg0, String arg1,
                                            String arg2, Bundle arg3) {
            return new ASRCommandReturn();
        }
    };

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            String deviceName = usbDevice.getDeviceName();
            Log.e(TAG, "--- 接收到广播， action: " + action);
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Log.e(TAG, "USB device is Attached: " + deviceName);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (CameraHelper.getNumberOfCameras() > 0) {
//                            Audience audience = (Audience) audienceNameText.getTag();
//                            if (audience != null) {
//                                if (config().mUid == Integer.parseInt(audience.getUid())) {
//                                    worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER, "");
//
//                                    SurfaceView localSurfaceView = RtcEngine.CreateRendererView(getApplicationContext());
//                                    localSurfaceView.setZOrderOnTop(true);
//                                    localSurfaceView.setZOrderMediaOverlay(true);
//                                    rtcEngine().setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, config().mUid));
//
//                                    audienceLayout.addView(localSurfaceView);
//                                    finishButton.setVisibility(View.VISIBLE);
//                                    micButton.setVisibility(View.GONE);
//
//                                    agoraAPI.setAttr("uname", audience.getUname());
//                                }
//                            }
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(getApplicationContext(), "检测到有" + CameraHelper.getNumberOfCameras() + "个摄像头插入", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(getApplicationContext(), "没有检测到摄像头", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, 1000);
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Log.e(TAG, "USB device is Detached: " + deviceName);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (CameraHelper.getNumberOfCameras() <= 0) {
//                            Audience audience = (Audience) audienceNameText.getTag();
//                            if (audience != null) {
//                                if (BuildConfig.DEBUG) {
//                                    Toast.makeText(getApplicationContext(), "检测到摄像头被拔出" + audience.toString(), Toast.LENGTH_SHORT).show();
//                                }
//                                if (config().mUid == audience.getUid()) {
//                                    audienceLayout.removeAllViews();
//
//                                    try {
//                                        JSONObject jsonObject = new JSONObject();
//                                        jsonObject.put("finish", true);
//                                        agoraAPI.messageInstantSend(broadcasterId, 0, jsonObject.toString(), "");
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
//
//                                    Toast.makeText(getApplicationContext(), "摄像头异常，请重新进入会议" + audience.toString(), Toast.LENGTH_SHORT).show();
//
//                                    if (!TextUtils.isEmpty(meetingHostJoinTraceId)) {
//                                        HashMap<String, Object> params = new HashMap<String, Object>();
//                                        params.put("meetingHostJoinTraceId", meetingHostJoinTraceId);
//                                        params.put("status", 2);
//                                        params.put("meetingId", meetingJoin.getMeeting().getId());
//                                        params.put("type", 2);
//                                        params.put("leaveType", 1);
//                                        ApiClient.getInstance().meetingHostStats(TAG, meetingHostJoinTraceCallback, params);
//                                    }
//
////                                    agoraAPI.channelLeave(channelName);
//                                    if (agoraAPI.getStatus() == 2) {
//                                        agoraAPI.logout();
//                                    }
//                                    finish();
//                                }
//                            }
//                            if (handsUp) {
//                                handsUp = false;
//                                micButton.setText("我要发言");
//                                micButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_meeting_signup, 0, 0, 0);
//                                try {
//                                    JSONObject jsonObject = new JSONObject();
//                                    jsonObject.put("auditStatus", Preferences.getUserAuditStatus());
//                                    jsonObject.put("postTypeName", Preferences.getUserPostTypeName());
//                                    jsonObject.put("handsUp", false);
//                                    jsonObject.put("uid", config().mUid);
//                                    jsonObject.put("uname", TextUtils.isEmpty(Preferences.getAreaInfo()) ? "讲解员-" + Preferences.getUserName() : "讲解员-" + Preferences.getAreaInfo() + "-" + Preferences.getUserName());
//                                    agoraAPI.messageInstantSend(broadcasterId, 0, jsonObject.toString(), "");
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
                        } else {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(getApplicationContext(), "检测到还有" + CameraHelper.getNumberOfCameras() + "个摄像头", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, 1000);
            }
        }
    };

    private AgoraAPIOnlySignal agoraAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_meeting_audience);

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);
        requestMicOccupy();
        registerReceiver(homeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        TCAgent.onEvent(this, "进入会议直播界面");

        try {
            // 注册语音搜索组件
            mAliTVASRManager.init(getBaseContext(), false);
            mAliTVASRManager.setOnASRCommandListener(mASRCommandListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "视频通话");
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "视频通话");
    }

    @Override
    protected void initUIandEvent() {
        event().addEventHandler(this);

        Intent intent = getIntent();
        agora = intent.getParcelableExtra("agora");
        meetingJoin = intent.getParcelableExtra("meeting");
        ZYAgent.onEvent(getApplicationContext(), "meetingId=" + meetingJoin.getMeeting().getId());
        meeting = meetingJoin.getMeeting();

        if (meetingJoin==null||meetingJoin.getHostUser()==null||meetingJoin.getHostUser().getClientUid()==null){
            ToastUtils.showToast("主持人还未加入 请稍后再试");
            finish();
            return;

        }

        broadcasterId = meetingJoin.getHostUser().getClientUid();

        channelName = meeting.getId();

        config().mUid = Integer.parseInt(UIDUtil.generatorUID(Preferences.getUserId()));

        audienceRecyclerView = findViewById(R.id.audience_list);
        audienceRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2, RecyclerView.VERTICAL, false));
        audienceRecyclerView.addItemDecoration(new SpaceItemDecoration(8, 0, 0, 8));
        audienceVideoAdapter = new AudienceVideoAdapter(this);
        audienceRecyclerView.setAdapter(audienceVideoAdapter);
        audienceRecyclerView.setFocusable(false);

        broadcasterView = findViewById(R.id.broadcaster_view);
        broadcasterTipsText = findViewById(R.id.broadcaster_tips);
        broadcasterNameText = findViewById(R.id.broadcaster_name);
        broadcasterNameText.setText("主持人：" + meetingJoin.getHostUser().getHostUserName());
        broadcasterSmallView = findViewById(R.id.broadcaster_small_view);
        docImage = findViewById(R.id.doc_image);
        pageText = findViewById(R.id.page);

        muteAudioButton = findViewById(R.id.mute_audio);
        muteAudioButton.setOnClickListener(v -> {
            if (!isMuted) {
                isMuted = true;
                muteAudioButton.setImageResource(R.drawable.ic_muted);
            } else {
                isMuted = false;
                muteAudioButton.setImageResource(R.drawable.ic_unmuted);
            }
            rtcEngine().muteLocalAudioStream(isMuted);
        });
        muteAudioButton.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (isMuted) {
                    muteAudioButton.setImageResource(R.drawable.ic_muted);//静音
                    muteAudioButton.setBackground(getResources().getDrawable(R.drawable.bg_meeting_mute_selector));
                }
            } else {
                if (isMuted) {
                    muteAudioButton.setImageResource(R.drawable.ic_muted_unfocus);//静音
                    muteAudioButton.setBackground(getResources().getDrawable(R.drawable.bg_meeting_mute_unfocus_selector));
                }
            }
        });

        exitMeetingButton = findViewById(R.id.finish_meeting);
        exitMeetingButton.setOnClickListener(view -> {
            showDialog(1, "确定退出吗？", "取消", "确定", null);
        });

        worker().configEngine(Constants.CLIENT_ROLE_BROADCASTER, VideoEncoderConfiguration.VD_320x180);
        rtcEngine().enableAudioVolumeIndication(400, 3,false);

        agoraAPI = AgoraAPIOnlySignal.getInstance(this, agora.getAppID());
        agoraAPI.callbackSet(new AgoraAPI.CallBack() {

            @Override
            public void onLoginSuccess(int uid, int fd) {
                super.onLoginSuccess(uid, fd);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> Toast.makeText(InviteMeetingAudienceActivity.this, "观众登陆信令系统成功", Toast.LENGTH_SHORT).show());
                }
                agoraAPI.channelJoin(channelName);
            }

            @Override
            public void onLoginFailed(final int ecode) {
                super.onLoginFailed(ecode);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> Toast.makeText(InviteMeetingAudienceActivity.this, "观众登陆信令系统失败" + ecode, Toast.LENGTH_SHORT).show());
                }
                // 重新登录信令系统
                if ("true".equals(agora.getIsTest())) {
                    agoraAPI.login2(agora.getAppID(), "" + config().mUid, "noneed_token", 0, "", 20, 30);
                } else {
                    agoraAPI.login2(agora.getAppID(), "" + config().mUid, agora.getSignalingKey(), 0, "", 20, 30);
                }
            }

            @Override
            public void onLogout(int ecode) {
                super.onLogout(ecode);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(InviteMeetingAudienceActivity.this, "退出信令频道成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onChannelJoined(String channelID) {
                super.onChannelJoined(channelID);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(InviteMeetingAudienceActivity.this, "观众登陆信令频道成功", Toast.LENGTH_SHORT).show();
                    }
                    if (agoraAPI.getStatus() == 2) {
                        agoraAPI.queryUserStatus(broadcasterId);
                    }
                });
            }

            @Override
            public void onReconnecting(int nretry) {
                super.onReconnecting(nretry);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> Toast.makeText(InviteMeetingAudienceActivity.this, "信令重连失败第" + nretry + "次", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onReconnected(int fd) {
                super.onReconnected(fd);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> Toast.makeText(InviteMeetingAudienceActivity.this, "信令系统重连成功", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onChannelJoinFailed(String channelID, int ecode) {
                super.onChannelJoinFailed(channelID, ecode);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> Toast.makeText(InviteMeetingAudienceActivity.this, "观众登陆信令频道失败" + ecode, Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onChannelQueryUserNumResult(String channelID, int ecode, final int num) {
                super.onChannelQueryUserNumResult(channelID, ecode, num);
                runOnUiThread(() -> {
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("count", num);
                    ApiClient.getInstance().channelCount(TAG, channelCountCallback(), meetingJoin.getMeeting().getId(), params);
                });
            }

            @Override
            public void onChannelUserJoined(String account, int uid) {
                super.onChannelUserJoined(account, uid);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(InviteMeetingAudienceActivity.this, "用户" + account + "进入信令频道", Toast.LENGTH_SHORT).show();
                    }

                    agoraAPI.channelQueryUserNum(channelName);

                });

            }

            @Override
            public void onChannelUserLeaved(String account, int uid) {
                super.onChannelUserLeaved(account, uid);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(InviteMeetingAudienceActivity.this, "用户" + account + "退出信令频道", Toast.LENGTH_SHORT).show();
                    }

                    agoraAPI.channelQueryUserNum(channelName);

                });
            }

            @Override
            public void onUserAttrResult(final String account, final String name, final String value) {
                super.onUserAttrResult(account, name, value);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(InviteMeetingAudienceActivity.this, "onUserAttrResult 获取正在连麦用户" + account + "的属性" + name + "的值为" + value, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onMessageSendSuccess(String messageID) {
                super.onMessageSendSuccess(messageID);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> Toast.makeText(InviteMeetingAudienceActivity.this, messageID + "发送成功", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onMessageSendError(String messageID, int ecode) {
                super.onMessageSendError(messageID, ecode);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> Toast.makeText(InviteMeetingAudienceActivity.this, messageID + "发送失败", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onMessageInstantReceive(final String account, final int uid, final String msg) {
                super.onMessageInstantReceive(account, uid, msg);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(InviteMeetingAudienceActivity.this, "onMessageInstantReceive 收到主持人" + account + "发来的消息" + msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onMessageChannelReceive(String channelID, String account, int uid, final String msg) {
                super.onMessageChannelReceive(channelID, account, uid, msg);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(InviteMeetingAudienceActivity.this, "onMessageChannelReceive 收到" + account + "发的频道消息" + msg, Toast.LENGTH_SHORT).show();
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(msg);
                        if (jsonObject.has("finish_meeting")) {
                            boolean finishMeeting = jsonObject.getBoolean("finish_meeting");
                            if (finishMeeting) {
                                if (BuildConfig.DEBUG) {
                                    Toast.makeText(InviteMeetingAudienceActivity.this, "主持人离开了", Toast.LENGTH_SHORT).show();
                                }
                                doLeaveChannel();
                                if (agoraAPI.getStatus() == 2) {
                                    agoraAPI.logout();
                                }
                                finish();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            }

            @Override
            public void onChannelAttrUpdated(String channelID, String name, String value, String type) {
                super.onChannelAttrUpdated(channelID, name, value, type);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(InviteMeetingAudienceActivity.this, "onChannelAttrUpdated:" + "\nname:" + name + ", \nvalue:" + value + ", \ntype:" + type, Toast.LENGTH_SHORT).show();
                    }
                    if (DOC_INFO.equals(name)) {
                        if (!TextUtils.isEmpty(value)) {
                            try {
                                JSONObject jsonObject = new JSONObject(value);
                                if (jsonObject.has("material_id") && jsonObject.has("doc_index")) {
                                    doc_index = jsonObject.getInt("doc_index");
                                    String materialId = jsonObject.getString("material_id");

                                    if (currentMaterial != null) {
                                        if (!materialId.equals(currentMaterial.getId())) {
                                            ApiClient.getInstance().meetingMaterial(TAG, meetingMaterialCallback, materialId);
                                        } else {
//                                            if (remoteBroadcasterSurfaceView != null) {
//                                                broadcasterView.removeView(remoteBroadcasterSurfaceView);
//                                                broadcasterView.removeAllViews();
//                                                broadcasterView.setVisibility(View.GONE);
//
//                                                broadcasterSmallView.setVisibility(View.VISIBLE);
//                                                broadcasterSmallView.removeAllViews();
//                                                broadcasterSmallView.addView(remoteBroadcasterSurfaceView);
//                                            }
                                            pageText.setVisibility(View.VISIBLE);
                                            MeetingMaterialsPublish currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(doc_index);
                                            pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");
                                            docImage.setVisibility(View.VISIBLE);
                                            Picasso.with(InviteMeetingAudienceActivity.this).load(currentMaterialPublish.getUrl()).into(docImage);
                                        }
                                    } else {
                                        ApiClient.getInstance().meetingMaterial(TAG, meetingMaterialCallback, materialId);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            pageText.setVisibility(View.GONE);
                            docImage.setVisibility(View.GONE);
                            currentMaterial = null;

                            if (remoteBroadcasterSurfaceView != null) {
                                broadcasterSmallView.removeView(remoteBroadcasterSurfaceView);
                            }
                            broadcasterSmallView.removeAllViews();
                            broadcasterSmallView.setVisibility(View.GONE);

                            broadcasterView.setVisibility(View.VISIBLE);
                            broadcasterView.removeAllViews();
                            if (remoteBroadcasterSurfaceView != null) {
                                broadcasterView.addView(remoteBroadcasterSurfaceView);
                            }
                        }
                    }
                    if (type.equals("clear")){
                        pageText.setVisibility(View.GONE);
                        docImage.setVisibility(View.GONE);
                        currentMaterial = null;

                        if (remoteBroadcasterSurfaceView != null) {
                            broadcasterSmallView.removeView(remoteBroadcasterSurfaceView);
                        }
                        broadcasterSmallView.removeAllViews();
                        broadcasterSmallView.setVisibility(View.GONE);

                        broadcasterView.setVisibility(View.VISIBLE);
                        broadcasterView.removeAllViews();
                        if (remoteBroadcasterSurfaceView != null) {
                            broadcasterView.addView(remoteBroadcasterSurfaceView);
                        }
                    }
                });
            }

            @Override
            public void onError(final String name, final int ecode, final String desc) {
                super.onError(name, ecode, desc);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> {
                        if (ecode != 208)
                            Toast.makeText(InviteMeetingAudienceActivity.this, "收到错误信息\nname: " + name + "\necode: " + ecode + "\ndesc: " + desc, Toast.LENGTH_SHORT).show();
                    });
                }
//                if (agoraAPI.getStatus() != 1 && agoraAPI.getStatus() != 2 && agoraAPI.getStatus() != 3) {
//                    if ("true".equals(agora.getIsTest())) {
//                        agoraAPI.login2(agora.getAppID(), "" + config().mUid, "noneed_token", 0, "", 20, 30);
//                    } else {
//                        agoraAPI.login2(agora.getAppID(), "" + config().mUid, agora.getSignalingKey(), 0, "", 20, 30);
//                    }
//                }
            }
        });

        ApiClient.getInstance().getMeetingHost(TAG, meeting.getId(), "0",joinMeetingCallback(0));

    }

    private OkHttpCallback channelCountCallback() {
        return new OkHttpCallback<Bucket>() {
            @Override
            public void onSuccess(Bucket bucket) {
                Log.v("channel_count", bucket.toString());
            }
        };
    }

    private OkHttpCallback joinMeetingCallback(int uid) {
        return new OkHttpCallback<Bucket<HostUser>>() {

            @Override
            public void onSuccess(Bucket<HostUser> meetingJoinBucket) {
                com.orhanobut.logger.Logger.e(meetingJoinBucket.getData().getHostUserName()+"登陆成功");
                meetingJoin.setHostUser(meetingJoinBucket.getData());
                broadcasterId = meetingJoinBucket.getData().getClientUid();
                broadcasterNameText.setText("主持人：" + meetingJoinBucket.getData().getHostUserName());
                if (uid != 0 && broadcasterId != null) {
                    if (uid == Integer.parseInt(broadcasterId)) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(InviteMeetingAudienceActivity.this, "主持人" + uid + "回来了", Toast.LENGTH_SHORT).show();
                        }

                        /**TextureView*/
                        remoteBroadcasterSurfaceView = RtcEngine.CreateTextureView(getApplicationContext());
//                        remoteBroadcasterSurfaceView.setZOrderOnTop(true);
//                        remoteBroadcasterSurfaceView.setZOrderMediaOverlay(true);
                        rtcEngine().setupRemoteVideo(new VideoCanvas(remoteBroadcasterSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));

                        broadcasterTipsText.setVisibility(View.GONE);

                        if (currentMaterial != null) {
                            broadcasterSmallView.setVisibility(View.VISIBLE);
                            broadcasterSmallView.removeAllViews();
                            broadcasterSmallView.addView(remoteBroadcasterSurfaceView);

                            broadcasterView.setVisibility(View.GONE);
                            pageText.setVisibility(View.VISIBLE);
                            docImage.setVisibility(View.VISIBLE);
                            MeetingMaterialsPublish currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(doc_index);
                            pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");
                            Picasso.with(InviteMeetingAudienceActivity.this).load(currentMaterialPublish.getUrl()).into(docImage);
                        } else {
                            docImage.setVisibility(View.GONE);
                            pageText.setVisibility(View.GONE);
                            broadcasterSmallView.removeAllViews();
                            broadcasterSmallView.setVisibility(View.GONE);
                            broadcasterView.setVisibility(View.VISIBLE);
                            broadcasterView.removeAllViews();
                            broadcasterView.addView(remoteBroadcasterSurfaceView);
                        }
                    } else {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(InviteMeetingAudienceActivity.this, "参会人" + uid + "加入", Toast.LENGTH_SHORT).show();
                        }

                        SurfaceView remoteAudienceSurfaceView = RtcEngine.CreateRendererView(getApplicationContext());
                        remoteAudienceSurfaceView.setZOrderOnTop(true);
                        remoteAudienceSurfaceView.setZOrderMediaOverlay(true);
                        rtcEngine().setupRemoteVideo(new VideoCanvas(remoteAudienceSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));

                        audienceRecyclerView.setVisibility(View.VISIBLE);

                        AudienceVideo audienceVideo = new AudienceVideo();
                        audienceVideo.setUid(uid);
                        audienceVideo.setName("参会人" + uid);
                        audienceVideo.setBroadcaster(false);
                        audienceVideo.setSurfaceView(remoteAudienceSurfaceView);
                        audienceVideoAdapter.insertItem(audienceVideo);
                    }
                } else {
                    /**TextureView*/
                    TextureView localAudienceSurfaceView = RtcEngine.CreateTextureView(getApplicationContext());
//                    localAudienceSurfaceView.setZOrderOnTop(true);
//                    localAudienceSurfaceView.setZOrderMediaOverlay(true);
                    rtcEngine().setupLocalVideo(new VideoCanvas(localAudienceSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, config().mUid));
                    worker().preview2(true, localAudienceSurfaceView, config().mUid);

                    audienceRecyclerView.setVisibility(View.VISIBLE);

                    /**TextureView*/
                    AudienceVideo audienceVideo = new AudienceVideo();
                    audienceVideo.setUid(config().mUid);
                    audienceVideo.setName("参会人" + config().mUid);
                    audienceVideo.setBroadcaster(false);
                    audienceVideo.setTextureView(localAudienceSurfaceView);
                    audienceVideoAdapter.insertItem(audienceVideo);

                    if ("true".equals(agora.getIsTest())) {
                        worker().joinChannel(null, channelName, config().mUid);
                    } else {
                        worker().joinChannel(agora.getToken(), channelName, config().mUid);
                    }
                }
            }

            @Override
            public void onFailure(int errorCode, BaseException exception) {
                super.onFailure(errorCode, exception);
                com.orhanobut.logger.Logger.e(exception.getMessage());
                Toast.makeText(InviteMeetingAudienceActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }

        };
    }

    private OkHttpCallback meetingMaterialCallback = new OkHttpCallback<Bucket<Material>>() {

        @Override
        public void onSuccess(Bucket<Material> materialBucket) {
            Log.v("material", materialBucket.toString());
            currentMaterial = materialBucket.getData();
            Collections.sort(currentMaterial.getMeetingMaterialsPublishList(), (o1, o2) -> (o1.getPriority() < o2.getPriority()) ? -1 : 1);

            MeetingMaterialsPublish currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(doc_index);

            if (remoteBroadcasterSurfaceView != null) {
                broadcasterView.removeView(remoteBroadcasterSurfaceView);
                broadcasterView.setVisibility(View.GONE);
                broadcasterTipsText.setVisibility(View.GONE);

                if (broadcasterSmallView.getChildCount() == 0) {
                    broadcasterSmallView.setVisibility(View.VISIBLE);
                    broadcasterSmallView.removeAllViews();
                    broadcasterSmallView.addView(remoteBroadcasterSurfaceView);
                }
            }

            pageText.setVisibility(View.VISIBLE);
            docImage.setVisibility(View.VISIBLE);
            pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");
            docImage.setVisibility(View.VISIBLE);
            Picasso.with(InviteMeetingAudienceActivity.this).load(currentMaterialPublish.getUrl()).into(docImage);

        }

        @Override
        public void onFailure(int errorCode, BaseException exception) {
            super.onFailure(errorCode, exception);
            Toast.makeText(InviteMeetingAudienceActivity.this, errorCode + "---" + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private String meetingHostJoinTraceId;

    private OkHttpCallback meetingHostJoinTraceCallback = new OkHttpCallback<Bucket<MeetingHostingStats>>() {

        @Override
        public void onSuccess(Bucket<MeetingHostingStats> meetingHostingStatsBucket) {
            if (TextUtils.isEmpty(meetingHostJoinTraceId)) {
                meetingHostJoinTraceId = meetingHostingStatsBucket.getData().getId();
            } else {
                meetingHostJoinTraceId = null;
            }
        }

        @Override
        public void onFailure(int errorCode, BaseException exception) {
            super.onFailure(errorCode, exception);
            Toast.makeText(InviteMeetingAudienceActivity.this, errorCode + "---" + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private Dialog dialog;

    private void showDialog(final int type, final String title, final String leftText, final String rightText, final Audience audience) {
        View view = View.inflate(this, R.layout.dialog_selector, null);
        TextView titleText = view.findViewById(R.id.title);
        titleText.setText(title);

        Button leftButton = view.findViewById(R.id.left);
        leftButton.setText(leftText);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        Button rightButton = view.findViewById(R.id.right);
        rightButton.setText(rightText);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                if (type == 1) {
                    agoraAPI.setAttr("uname", null);

                    if (!TextUtils.isEmpty(meetingHostJoinTraceId)) {
                        HashMap<String, Object> params = new HashMap<String, Object>();
                        params.put("meetingHostJoinTraceId", meetingHostJoinTraceId);
                        params.put("status", 2);
                        params.put("meetingId", meetingJoin.getMeeting().getId());
                        params.put("type", 2);
                        params.put("leaveType", 1);
                        ApiClient.getInstance().meetingJoinStats(TAG, meetingHostJoinTraceCallback, params);
                    }
                    doLeaveChannel();
                    if (agoraAPI.getStatus() == 2) {
                        agoraAPI.logout();
                    }
                    finish();
                }
            }
        });

        dialog = new Dialog(this, R.style.MyDialog);
        dialog.setContentView(view);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = 740;
        lp.height = 480;
        dialogWindow.setAttributes(lp);

        dialog.show();
    }

    @Override
    protected void deInitUIandEvent() {
        doLeaveChannel();
        event().removeEventHandler(this);

    }

    private void doLeaveChannel() {
        worker().leaveChannel(config().mChannel);
        worker().preview2(false, null, 0);

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("meetingJoinTraceId", meetingJoinTraceId);
        params.put("meetingId", meetingJoin.getMeeting().getId());
        params.put("status", 2);
        params.put("type", 2);
        ApiClient.getInstance().meetingJoinStats(TAG, meetingJoinStatsCallback, params);
    }

    @Override
    public void onJoinChannelSuccess(final String channel, final int uid, final int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                config().mUid = uid;
                channelName = channel;

                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("meetingId", meeting.getId());
                params.put("status", 1);
                params.put("type", 2);
                ApiClient.getInstance().meetingJoinStats(TAG, meetingJoinStatsCallback, params);

                if ("true".equals(agora.getIsTest())) {
                    agoraAPI.login2(agora.getAppID(), "" + uid, "noneed_token", 0, "", 20, 30);
                } else {
                    agoraAPI.login2(agora.getAppID(), "" + uid, agora.getSignalingKey(), 0, "", 20, 30);
                }

            }
        });
    }

    private String meetingJoinTraceId;

    private OkHttpCallback meetingJoinStatsCallback = new OkHttpCallback<Bucket<MeetingJoinStats>>() {

        @Override
        public void onSuccess(Bucket<MeetingJoinStats> meetingJoinStatsBucket) {
            if (TextUtils.isEmpty(meetingJoinTraceId)) {
                meetingJoinTraceId = meetingJoinStatsBucket.getData().getId();
            } else {
                meetingJoinTraceId = null;
            }
        }
    };

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        runOnUiThread(() -> {
            if (isFinishing()) {
                return;
            }
            ApiClient.getInstance().getMeetingHost(TAG, meeting.getId(),String.valueOf(uid), joinMeetingCallback(uid));
        });
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {

    }

    @Override
    public void onUserOffline(int uid, int reason) {
        LOG.debug("onUserOffline " + (uid & 0xFFFFFFFFL) + " " + reason);
        runOnUiThread(() -> {
            if (isFinishing()) {
                return;
            }
            if (uid == Integer.parseInt(meetingJoin.getHostUser().getClientUid())) {
                broadcasterTipsText.setVisibility(View.VISIBLE);
            } else {

                audienceVideoAdapter.deleteItem(uid);

                if (audienceVideoAdapter.getItemCount() == 0) {
                    audienceRecyclerView.setVisibility(View.INVISIBLE);
                }

            }
        });
    }

    @Override
    public void onConnectionLost() {
        runOnUiThread(() -> {
            Toast.makeText(InviteMeetingAudienceActivity.this, "网络连接断开，请检查网络连接", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public void onConnectionInterrupted() {
        runOnUiThread(() -> Toast.makeText(InviteMeetingAudienceActivity.this, "网络连接不佳，视频将会有卡顿，可尝试降低分辨率", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onUserMuteVideo(final int uid, final boolean muted) {
        if (BuildConfig.DEBUG) {
            runOnUiThread(() -> Toast.makeText(InviteMeetingAudienceActivity.this, uid + " 的视频被暂停了 " + muted, Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onUserMuteAudio(int uid, boolean muted) {
        runOnUiThread(() -> {
            audienceVideoAdapter.setMutedStatusByUid(uid, muted);
        });
    }

    @Override
    public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {
        runOnUiThread(() -> {
            for (IRtcEngineEventHandler.AudioVolumeInfo audioVolumeInfo : speakers) {
                audienceVideoAdapter.setVolumeByUid(audioVolumeInfo.uid, audioVolumeInfo.volume);
            }
        });
    }

    @Override
    public void onLastmileQuality(final int quality) {
        if (BuildConfig.DEBUG) {
            runOnUiThread(() -> Toast.makeText(InviteMeetingAudienceActivity.this, "本地网络质量报告：" + showNetQuality(quality), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        if (BuildConfig.DEBUG) {
            runOnUiThread(() -> {
//                    Toast.makeText(MeetingAudienceActivity.this, "用户" + uid + "的\n上行网络质量：" + showNetQuality(txQuality) + "\n下行网络质量：" + showNetQuality(rxQuality), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private String showNetQuality(int quality) {
        String lastmileQuality;
        switch (quality) {
            case 0:
                lastmileQuality = "UNKNOWN0";
                break;
            case 1:
                lastmileQuality = "EXCELLENT";
                break;
            case 2:
                lastmileQuality = "GOOD";
                break;
            case 3:
                lastmileQuality = "POOR";
                break;
            case 4:
                lastmileQuality = "BAD";
                break;
            case 5:
                lastmileQuality = "VBAD";
                break;
            case 6:
                lastmileQuality = "DOWN";
                break;
            default:
                lastmileQuality = "UNKNOWN";
        }
        return lastmileQuality;
    }

    @Override
    public void onWarning(int warn) {
        if (BuildConfig.DEBUG) {
//            runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, "警告码：" + warn, Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onError(final int err) {
        if (BuildConfig.DEBUG) {
            runOnUiThread(() -> Toast.makeText(InviteMeetingAudienceActivity.this, "错误码：" + err, Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onLocalVideoStateChanged(int localVideoState, int error) {

    }

    @Override
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {

    }

    @Override
    public void onRemoteAudioStateChanged(int uid, int state, int reason, int elapsed) {

    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {

    }

    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

    @Override
    public void onAttachedToWindow() {
        this.getWindow().addFlags(FLAG_HOMEKEY_DISPATCHED);
        super.onAttachedToWindow();
    }

    @Override
    public void onBackPressed() {
        if (dialog == null || !dialog.isShowing()) {
            showDialog(1, "确定退出吗？", "取消", "确定", null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        TCAgent.onPageEnd(this, "MeetingAudienceActivity");

        releaseMicOccupy();
        unregisterReceiver(mUsbReceiver);
        unregisterReceiver(homeKeyEventReceiver);

        //打开语音搜索
        try {
            mAliTVASRManager.setAliTVASREnable(true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mAliTVASRManager.release();

    }

    private BroadcastReceiver homeKeyEventReceiver = new BroadcastReceiver() {
        String REASON = "reason";
        String HOMEKEY = "homekey";
        String RECENTAPPS = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action) || Intent.ACTION_SHUTDOWN.equals(action)) {
                String reason = intent.getStringExtra(REASON);
                if (TextUtils.equals(reason, HOMEKEY)) {
                    // 点击 Home键
                    if (BuildConfig.DEBUG)
                        Toast.makeText(getApplicationContext(), "您点击了Home键", Toast.LENGTH_SHORT).show();

                    doLeaveChannel();
                    if (agoraAPI.getStatus() == 2) {
                        agoraAPI.logout();
                    }
                    agoraAPI.destroy();
                    finish();
                } else if (TextUtils.equals(reason, RECENTAPPS)) {
                    // 点击 菜单键
                    if (BuildConfig.DEBUG)
                        Toast.makeText(getApplicationContext(), "您点击了菜单键", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private boolean mUseAudioFocus = false, mHasRequestMic = false, mUseBroadcast = true, mHasReleaseMic = false;
    private AudioManager.OnAudioFocusChangeListener mListener;
    private AudioManager am;

    public boolean requestMicOccupy() {
        Log.v(TAG, "requestMic");
        if (mUseAudioFocus) {
            if (mListener != null) {
                releaseMicOccupy();
            }
            am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            mListener = focusChange -> {
                Log.v(TAG, "focusChange =" + focusChange);
                switch (focusChange) {
                    case AUDIOFOCUS_LOSS_TRANSIENT: {
                        Log.v(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                        break;
                    }
                    case AudioManager.AUDIOFOCUS_GAIN: {
                        Log.v(TAG, "AUDIOFOCUS_GAIN");
                        break;
                    }
                    case AudioManager.AUDIOFOCUS_LOSS: {
                        Log.v(TAG, "AUDIOFOCUS_LOSS");
                        break;
                    }
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: {
                        Log.v(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                        break;
                    }
                }
            };

            try {
                setProperty("audio.tv.app.need.remote.mic", "true");
                Field usbMicField = AudioManager.class.getField("STREAM_USB_MIC");
                int streamType = usbMicField.getInt(AudioManager.class);
                int result = am.requestAudioFocus(mListener, streamType, AudioManager.AUDIOFOCUS_GAIN);
                Log.v(TAG, "requestAudioFocus result is " + result);
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                    return true;
                else
                    return false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else if (mUseBroadcast) {
            //if (!mHasRequestMic)
            {
                setProperty("audio.tv.app.need.remote.mic", "true");
                Intent intent = new Intent();
                intent.setAction
                        ("com.yunos.tv.asr.video_call");
                Bundle bundle = new Bundle();
                bundle.putInt("video_call_type", 1);
                Log.v(TAG, "bundle.putint video_call_type 1");
                intent.putExtras(bundle);
                sendBroadcast(intent);
                mHasRequestMic = true;
                mHasReleaseMic = false;
                return true;
            }
            //return true;
        } else {
            return false;
        }
    }

    public void releaseMicOccupy() {
        Log.v(TAG, "releaseMic");
        if (mUseAudioFocus) {
            if (mListener != null) {
                setProperty("audio.tv.app.need.remote.mic", "false");
                am.abandonAudioFocus(mListener);
                mListener = null;
            }
        } else if (mUseBroadcast) {
//            if (!mHasReleaseMic)
            {
                setProperty("audio.tv.app.need.remote.mic", "false");
                Intent intent = new Intent();
                intent.setAction("com.yunos.tv.asr.video_call");
                Bundle bundle = new Bundle();
                bundle.putInt("video_call_type", 0);
                Log.v(TAG, "bundle.putint video_call_type 0");
                intent.putExtras(bundle);
                sendBroadcast(intent);
                mHasReleaseMic = true;
                mHasRequestMic = false;
            }
        }
    }

}
