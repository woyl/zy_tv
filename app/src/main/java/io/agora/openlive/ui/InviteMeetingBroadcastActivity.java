package io.agora.openlive.ui;

import android.app.AlertDialog;
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
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zhongyou.meettvapplicaion.ApiClient;
import com.zhongyou.meettvapplicaion.BaseException;
import com.zhongyou.meettvapplicaion.BuildConfig;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.business.adapter.MaterialAdapter;
import com.zhongyou.meettvapplicaion.entities.Agora;
import com.zhongyou.meettvapplicaion.entities.AudienceVideo;
import com.zhongyou.meettvapplicaion.entities.Bucket;
import com.zhongyou.meettvapplicaion.entities.Material;
import com.zhongyou.meettvapplicaion.entities.Materials;
import com.zhongyou.meettvapplicaion.entities.Meeting;
import com.zhongyou.meettvapplicaion.entities.MeetingJoin;
import com.zhongyou.meettvapplicaion.entities.MeetingJoinStats;
import com.zhongyou.meettvapplicaion.entities.MeetingMaterialsPublish;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.CameraHelper;
import com.zhongyou.meettvapplicaion.utils.OkHttpCallback;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;
import com.zhongyou.meettvapplicaion.utils.UIDUtil;
import com.zhongyou.meettvapplicaion.utils.helper.ImageHelper;
import com.zhongyou.meettvapplicaion.utils.statistics.ZYAgent;
import com.zhongyou.meettvapplicaion.view.FocusFixedLinearLayoutManager;
import com.zhongyou.meettvapplicaion.view.RecyclerViewTV;
import com.zhongyou.meettvapplicaion.view.SpaceItemDecoration;
import com.squareup.picasso.Picasso;
import com.tendcloud.tenddata.TCAgent;
import com.yunos.tv.alitvasrsdk.ASRCommandReturn;
import com.yunos.tv.alitvasrsdk.AliTVASRManager;
import com.yunos.tv.alitvasrsdk.OnASRCommandListener;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
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

public class InviteMeetingBroadcastActivity extends BaseActivity implements AGEventHandler {

    private final static Logger LOG = LoggerFactory.getLogger(InviteMeetingBroadcastActivity.class);

    private final String TAG = InviteMeetingBroadcastActivity.class.getSimpleName();

    private MeetingJoin meetingJoin;
    private Agora agora;
    private Material currentMaterial;
    private int position;

    private String channelName;
    private int memberCount;

    private FrameLayout broadcasterView, broadcasterSmallView;
    private TextView broadcasterNameText, broadcasterTipsText;
    private Button finishMeetingButton, pptButton, previewButton, nextButton, exitDocButton;
    private ImageButton muteAudioButton;
    private ImageView docImage;
    private TextView pageText;
    /**TextureView*/
    private TextureView localBroadcasterSurfaceView;
    private RecyclerView audienceRecyclerView;
    private AudienceVideoAdapter audienceVideoAdapter;


    private static final String DOC_INFO = "doc_info";

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
            UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            String deviceName = usbDevice.getDeviceName();
            Log.e(TAG, "--- 接收到广播， action: " + action);
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Log.e(TAG, "USB device is Attached: " + deviceName);

                new Handler().postDelayed(() -> {
                    if (CameraHelper.getNumberOfCameras() > 0) {

                        worker().configEngine(Constants.CLIENT_ROLE_BROADCASTER, VideoEncoderConfiguration.VD_320x180);

                        SurfaceView localSurfaceView = RtcEngine.CreateRendererView(getApplicationContext());
                        rtcEngine().setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, config().mUid));
                        localSurfaceView.setZOrderOnTop(true);
                        localSurfaceView.setZOrderMediaOverlay(true);

                        broadcasterView.addView(localSurfaceView);

                        worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);

                        if (broadcasterTipsText!=null){
                            broadcasterTipsText.setVisibility(View.GONE);
                        }

                        if (BuildConfig.DEBUG) {
                            Toast.makeText(getApplicationContext(), "检测到有" + CameraHelper.getNumberOfCameras() + "个摄像头插入", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(getApplicationContext(), "没有检测到摄像头", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 1000);
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Log.e(TAG, "USB device is Detached: " + deviceName);

                new Handler().postDelayed(() -> {
                    if (CameraHelper.getNumberOfCameras() <= 0) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(getApplicationContext(), "检测到摄像头被拔出", Toast.LENGTH_SHORT).show();
                        }
                        broadcasterView.removeAllViews();


                        if (broadcasterTipsText!=null){
                            broadcasterTipsText.setText("未连接摄像头，请重新通话");
                            broadcasterTipsText.setVisibility(View.VISIBLE);
                        }else {
                            ToastUtils.showToast("未连接摄像头，请重新通话");
                        }

                        worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
                    } else {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(getApplicationContext(), "检测到还有" + CameraHelper.getNumberOfCameras() + "个摄像头", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_invite_meeting_broadcast);



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

    private boolean isMuted = false;

    @Override
    protected void initUIandEvent() {
        event().addEventHandler(this);

        Intent intent = getIntent();
        agora = intent.getParcelableExtra("agora");
        meetingJoin = intent.getParcelableExtra("meeting");
        ZYAgent.onEvent(getApplicationContext(), "meetingId=" + meetingJoin.getMeeting().getId());

        channelName = meetingJoin.getMeeting().getId();
        config().mUid = Integer.parseInt(UIDUtil.generatorUID(Preferences.getUserId()));

        audienceRecyclerView = findViewById(R.id.audience_list);
        audienceRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2, RecyclerView.VERTICAL, false));
        audienceRecyclerView.addItemDecoration(new SpaceItemDecoration(8, 0, 0, 8));
        audienceVideoAdapter = new AudienceVideoAdapter(this);
        audienceRecyclerView.setAdapter(audienceVideoAdapter);
        audienceRecyclerView.setFocusable(false);

        broadcasterTipsText = findViewById(R.id.broadcast_tips);
        broadcasterNameText = findViewById(R.id.broadcaster_name);
        broadcasterNameText.setText("主持人：" + meetingJoin.getHostUser().getHostUserName());
        broadcasterView = findViewById(R.id.broadcaster_view);
        broadcasterSmallView = findViewById(R.id.broadcaster_small_view);
        docImage = findViewById(R.id.doc_image);
        pageText = findViewById(R.id.page);

        previewButton = findViewById(R.id.preview);
        previewButton.setOnClickListener(view -> {
            if (currentMaterial != null) {
                if (position > 0) {
                    position--;
                    MeetingMaterialsPublish currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(position);
                    docImage.setVisibility(View.VISIBLE);
                    String imageUrl = ImageHelper.getThumb(currentMaterialPublish.getUrl());
                    Picasso.with(InviteMeetingBroadcastActivity.this).load(imageUrl).into(docImage);
                    pageText.setVisibility(View.VISIBLE);
                    pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("material_id", currentMaterial.getId());
                        jsonObject.put("doc_index", position);
                        agoraAPI.channelSetAttr(channelName, DOC_INFO, jsonObject.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "当前是第一张了", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "没找到ppt", Toast.LENGTH_SHORT).show();
            }
        });

        nextButton = findViewById(R.id.next);
        nextButton.setOnClickListener(view -> {
            if (currentMaterial != null) {
                if (position < (currentMaterial.getMeetingMaterialsPublishList().size() - 1)) {
                    position++;
                    MeetingMaterialsPublish currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(position);
                    docImage.setVisibility(View.VISIBLE);
                    String imageUrl = ImageHelper.getThumb(currentMaterialPublish.getUrl());
                    Glide.with(InviteMeetingBroadcastActivity.this) .asBitmap()

                            .load(imageUrl)

                            .error(R.drawable.item_forum_img_error)
                            .placeholder(R.drawable.item_forum_img_loading)
                            .into(docImage);
//                    Picasso.with(InviteMeetingBroadcastActivity.this).load(imageUrl).into(docImage);
                    pageText.setVisibility(View.VISIBLE);
                    pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("material_id", currentMaterial.getId());
                        jsonObject.put("doc_index", position);
                        agoraAPI.channelSetAttr(channelName, DOC_INFO, jsonObject.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "当前是最后一张了", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "没找到ppt", Toast.LENGTH_SHORT).show();
            }

        });

        exitDocButton = findViewById(R.id.exit_ppt);
        exitDocButton.setOnClickListener(view -> {
            currentMaterial = null;
            docImage.setVisibility(View.GONE);
            pageText.setVisibility(View.GONE);

            previewButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            exitDocButton.setVisibility(View.GONE);

            broadcasterSmallView.removeView(localBroadcasterSurfaceView);
            broadcasterSmallView.setVisibility(View.GONE);
            broadcasterView.setVisibility(View.VISIBLE);
            broadcasterView.removeAllViews();
            broadcasterView.addView(localBroadcasterSurfaceView);

            agoraAPI.channelDelAttr(channelName, DOC_INFO);
        });

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

        finishMeetingButton = findViewById(R.id.finish_meeting);
        finishMeetingButton.setOnClickListener(view -> {
            showExitDialog();
        });

        pptButton = findViewById(R.id.meeting_doc);
        pptButton.setOnClickListener(view -> {
            ApiClient.getInstance().meetingMaterials(TAG, new OkHttpCallback<Bucket<Materials>>() {
                @Override
                public void onSuccess(Bucket<Materials> materialsBucket) {
                    showPPTListDialog(materialsBucket.getData().getPageData());
                }

                @Override
                public void onFailure(int errorCode, BaseException exception) {
                    super.onFailure(errorCode, exception);
                    Toast.makeText(InviteMeetingBroadcastActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }, meetingJoin.getMeeting().getId());
        });

        worker().configEngine(Constants.CLIENT_ROLE_BROADCASTER, VideoEncoderConfiguration.VD_320x180);
        rtcEngine().enableAudioVolumeIndication(400, 3,false);

        /**TextureView*/
        localBroadcasterSurfaceView = RtcEngine.CreateTextureView(getApplicationContext());
        rtcEngine().setupLocalVideo(new VideoCanvas(localBroadcasterSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, config().mUid));
//        localBroadcasterSurfaceView.setZOrderOnTop(true);
//        localBroadcasterSurfaceView.setZOrderMediaOverlay(true);
        localBroadcasterSurfaceView.setFocusable(false);
        broadcasterView.addView(localBroadcasterSurfaceView);

        worker().preview2(true, localBroadcasterSurfaceView, config().mUid);

        if ("true".equals(agora.getIsTest())) {
            worker().joinChannel(null, channelName, config().mUid);
        } else {
            worker().joinChannel(agora.getToken(), channelName, config().mUid);
        }



        agoraAPI = AgoraAPIOnlySignal.getInstance(this, agora.getAppID());
        agoraAPI.callbackSet(new AgoraAPI.CallBack() {

            @Override
            public void onLoginSuccess(int uid, int fd) {
                super.onLoginSuccess(uid, fd);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, "信令系统登陆成功", Toast.LENGTH_SHORT).show());
                    }
                    agoraAPI.channelJoin(channelName);
                });

            }

            @Override
            public void onLoginFailed(final int ecode) {
                super.onLoginFailed(ecode);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(InviteMeetingBroadcastActivity.this, "信令系统登陆失败" + ecode, Toast.LENGTH_SHORT).show();
                    }
                    if ("true".equals(agora.getIsTest())) {
                        agoraAPI.login2(agora.getAppID(), "" + config().mUid, "noneed_token", 0, "", 20, 30);
                    } else {
                        agoraAPI.login2(agora.getAppID(), "" + config().mUid, agora.getSignalingKey(), 0, "", 20, 30);
                    }

                });

            }

            @Override
            public void onReconnecting(int nretry) {
                super.onReconnecting(nretry);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, "信令重连失败第" + nretry + "次", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onReconnected(int fd) {
                super.onReconnected(fd);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, "信令系统重连成功", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onChannelJoined(String channelID) {
                super.onChannelJoined(channelID);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(InviteMeetingBroadcastActivity.this, "加入信令频道成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onChannelJoinFailed(String channelID, int ecode) {
                super.onChannelJoinFailed(channelID, ecode);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(InviteMeetingBroadcastActivity.this, "加入信令频道失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onLogout(int ecode) {
                super.onLogout(ecode);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(InviteMeetingBroadcastActivity.this, "退出信令频道成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onChannelQueryUserNumResult(String channelID, int ecode, final int num) {
                super.onChannelQueryUserNumResult(channelID, ecode, num);
                runOnUiThread(() -> {
                    memberCount = num;
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("count", num);
                    ApiClient.getInstance().channelCount(TAG, channelCountCallback(), meetingJoin.getMeeting().getId(), params);
                });
            }

            @Override
            public void onUserAttrResult(String account, String name, String value) {
                super.onUserAttrResult(account, name, value);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(InviteMeetingBroadcastActivity.this, "获取到用户" + account + "的属性" + name + "的值为" + value, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onChannelUserJoined(String account, int uid) {
                super.onChannelUserJoined(account, uid);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(InviteMeetingBroadcastActivity.this, "参会人" + account + "进入信令频道", Toast.LENGTH_SHORT).show();
                    }

                    agoraAPI.channelQueryUserNum(channelName);

                    if (currentMaterial != null) { //正在演示ppt
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(InviteMeetingBroadcastActivity.this, "向刚进入的参会人" + account + "发送ppt信息", Toast.LENGTH_SHORT).show();
                        }
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("material_id", currentMaterial.getId());
                            jsonObject.put("doc_index", position);
                            agoraAPI.channelSetAttr(channelName, DOC_INFO, jsonObject.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else { // 没有在演示ppt
                        agoraAPI.channelDelAttr(channelName, DOC_INFO);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(InviteMeetingBroadcastActivity.this, "参会人" + account + "上来时主持人端没有ppt信息", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onChannelUserLeaved(String account, int uid) {
                super.onChannelUserLeaved(account, uid);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(InviteMeetingBroadcastActivity.this, "参会人" + account + "退出信令频道", Toast.LENGTH_SHORT).show();
                    }
                    agoraAPI.channelQueryUserNum(channelName);
                });
            }

            @Override
            public void onMessageSendSuccess(String messageID) {
                super.onMessageSendSuccess(messageID);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, messageID + "发送成功", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onMessageSendError(String messageID, int ecode) {
                super.onMessageSendError(messageID, ecode);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, messageID + "发送失败", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onMessageInstantReceive(final String account, final int uid, final String msg) {
                super.onMessageInstantReceive(account, uid, msg);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, "收到参会人" + account + "发来的消息" + msg, Toast.LENGTH_SHORT).show());
                    }
                });
            }

            @Override
            public void onChannelAttrUpdated(String channelID, String name, String value, String type) {
                super.onChannelAttrUpdated(channelID, name, value, type);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, "" + channelID + "" + name + "" + value + "" + type, Toast.LENGTH_SHORT).show());
                    }
                });
            }

            @Override
            public void onError(final String name, final int ecode, final String desc) {
                super.onError(name, ecode, desc);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> {
                        if (ecode != 208)
                            Toast.makeText(InviteMeetingBroadcastActivity.this, "收到错误信息\nname: " + name + "\necode: " + ecode + "\ndesc: " + desc, Toast.LENGTH_SHORT).show();
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

            @Override
            public void onLog(String txt) {
                super.onLog(txt);
                Log.v("信令--->", txt);
            }
        });

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("meetingId", meetingJoin.getMeeting().getId());
        params.put("status", 1);
        params.put("type", 1);
        ApiClient.getInstance().meetingJoinStats(TAG, meetingJoinStatsCallback(), params);

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);

    }

    private OkHttpCallback channelCountCallback() {
        return new OkHttpCallback<Bucket>() {
            @Override
            public void onSuccess(Bucket bucket) {
                Log.v("channel_count", bucket.toString());
            }
        };
    }

    private Dialog exitDialog;

    private void showExitDialog() {
        View contentView = View.inflate(this, R.layout.dialog_exit_meeting, null);
        TextView finishTips = contentView.findViewById(R.id.finish_meeting_tips);
        Button tempLeaveButton = contentView.findViewById(R.id.left);
        tempLeaveButton.setOnClickListener(view -> {
            exitDialog.cancel();
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("clientUid", "" + config().mUid);
            params.put("hostUserId", Preferences.getUserId());
            params.put("hostUserName", meetingJoin.getHostUser().getHostUserName());
            params.put("status", 2);
            ApiClient.getInstance().meetingLeaveTemp(TAG, meetingTempLeaveCallback, meetingJoin.getMeeting().getId(), params);

            doLeaveChannel();
            if (agoraAPI.getStatus() == 2) {
                agoraAPI.logout();
            }
            finish();
        });
        Button finishMeetingButton = contentView.findViewById(R.id.right);
        finishMeetingButton.setOnClickListener(view -> {
            if (finishTips.getVisibility() == View.VISIBLE) {
                ApiClient.getInstance().finishMeeting(TAG, meetingJoin.getMeeting().getId(), memberCount, finishMeetingCallback);
                exitDialog.cancel();
            } else {
                finishTips.setVisibility(View.VISIBLE);
            }
        });
        exitDialog = new Dialog(this, R.style.MyDialog);
        exitDialog.setContentView(contentView);
        exitDialog.show();
    }

    private OkHttpCallback meetingTempLeaveCallback = new OkHttpCallback<Bucket>() {

        @Override
        public void onSuccess(Bucket meetingTempLeaveBucket) {
            Log.v("meetingTempLeave", meetingTempLeaveBucket.toString());
        }
    };

    private AlertDialog pptAlertDialog, pptDetailDialog;

    private void showPPTListDialog(ArrayList<Material> materials) {
        View view = View.inflate(this, R.layout.dialog_ppt_list, null);
        RecyclerViewTV recyclerViewTV = view.findViewById(R.id.meeting_doc_list);
        FocusFixedLinearLayoutManager gridlayoutManager = new FocusFixedLinearLayoutManager(this); // 解决快速长按焦点丢失问题.
        gridlayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        recyclerViewTV.setLayoutManager(gridlayoutManager);
        recyclerViewTV.setFocusable(false);
        recyclerViewTV.addItemDecoration(new SpaceItemDecoration((int) (getResources().getDimension(R.dimen.my_px_20)), 0, (int) (getResources().getDimension(R.dimen.my_px_20)), 0));
        recyclerViewTV.setSelectedItemAtCentered(true);
        MaterialAdapter materialAdapter = new MaterialAdapter(this, materials);
        recyclerViewTV.setAdapter(materialAdapter);
        materialAdapter.setOnClickListener(new MaterialAdapter.OnClickListener() {
            @Override
            public void onPreviewButtonClick(View v, Material material, int position) {
                showPPTDetailDialog(material);
            }

            @Override
            public void onUseButtonClick(View v, Material material, int position) {
                currentMaterial = material;
                Collections.sort(currentMaterial.getMeetingMaterialsPublishList(), (o1, o2) -> (o1.getPriority() < o2.getPriority()) ? -1 : 1);
                ApiClient.getInstance().meetingSetMaterial(TAG, setMaterialCallback, meetingJoin.getMeeting().getId(), currentMaterial.getId());
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(InviteMeetingBroadcastActivity.this, R.style.MyDialog);
        builder.setView(view);
        pptAlertDialog = builder.create();
        if (!pptAlertDialog.isShowing()) {
            pptAlertDialog.show();
        }
    }

    private void showPPTDetailDialog(Material material) {
        View view = View.inflate(this, R.layout.dialog_ppt_detail, null);
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        TextView pageText = view.findViewById(R.id.page);
        pageText.setText("第1/" + material.getMeetingMaterialsPublishList().size() + "页");
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return material.getMeetingMaterialsPublishList().size();
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View view = View.inflate(container.getContext(), R.layout.item_doc_detail, null);
                ImageView imageView = view.findViewById(R.id.image_view);
                String imageUrl = ImageHelper.getThumb(material.getMeetingMaterialsPublishList().get(position).getUrl());
                Picasso.with(InviteMeetingBroadcastActivity.this).load(imageUrl).into(imageView);
                container.addView(view);
                return view;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pageText.setText("第" + (position + 1) + "/" + material.getMeetingMaterialsPublishList().size() + "页");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        TextView timeText = view.findViewById(R.id.time);
        timeText.setText(material.getCreateDate() + "创建");
        AlertDialog.Builder builder = new AlertDialog.Builder(InviteMeetingBroadcastActivity.this, R.style.MyDialog);
        builder.setView(view);
        pptDetailDialog = builder.create();
        if (!pptDetailDialog.isShowing()) {
            pptDetailDialog.show();
        }
    }

    private OkHttpCallback setMaterialCallback = new OkHttpCallback<Bucket>() {
        @Override
        public void onSuccess(Bucket bucket) {
            Log.v("material_set", bucket.toString());
            if (pptAlertDialog.isShowing()) {
                pptAlertDialog.dismiss();
            }

            broadcasterView.removeAllViews();
            broadcasterView.setVisibility(View.GONE);
            broadcasterSmallView.setVisibility(View.VISIBLE);
            broadcasterSmallView.removeAllViews();
            broadcasterSmallView.addView(localBroadcasterSurfaceView);

            previewButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
            exitDocButton.setVisibility(View.VISIBLE);

            docImage.setVisibility(View.VISIBLE);

            position = 0;
            MeetingMaterialsPublish currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(position);

            pageText.setVisibility(View.VISIBLE);
            pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");

            String imageUrl = ImageHelper.getThumb(currentMaterialPublish.getUrl());
            Picasso.with(InviteMeetingBroadcastActivity.this).load(imageUrl).into(docImage);

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("material_id", currentMaterial.getId());
                jsonObject.put("doc_index", position);
                agoraAPI.channelSetAttr(channelName, DOC_INFO, jsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int errorCode, BaseException exception) {
            super.onFailure(errorCode, exception);
            Toast.makeText(InviteMeetingBroadcastActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private OkHttpCallback finishMeetingCallback = new OkHttpCallback<Bucket<Meeting>>() {
        @Override
        public void onSuccess(Bucket<Meeting> meetingBucket) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("finish_meeting", true);
                agoraAPI.messageChannelSend(channelName, jsonObject.toString(), "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            doLeaveChannel();
            if (agoraAPI.getStatus() == 2) {
                agoraAPI.logout();
            }
            finish();
        }

        @Override
        public void onFailure(int errorCode, BaseException exception) {
            super.onFailure(errorCode, exception);
            Toast.makeText(InviteMeetingBroadcastActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("finish_meeting", true);
                agoraAPI.messageChannelSend(channelName, jsonObject.toString(), "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            doLeaveChannel();
            if (agoraAPI.getStatus() == 2) {
                agoraAPI.logout();
            }
            finish();
        }
    };

    @Override
    protected void deInitUIandEvent() {
        doLeaveChannel();
        event().removeEventHandler(this);
    }

    private void doLeaveChannel() {
        currentMaterial = null;
        agoraAPI.channelDelAttr(channelName, DOC_INFO);

        worker().leaveChannel(config().mChannel);
        worker().preview2(false, null, 0);
        if (!TextUtils.isEmpty(meetingJoinTraceId)) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("meetingJoinTraceId", meetingJoinTraceId);
            params.put("meetingId", meetingJoin.getMeeting().getId());
            params.put("status", 2);
            params.put("type", 1);
            params.put("leaveType", 1);
            ApiClient.getInstance().meetingJoinStats(TAG, meetingJoinStatsCallback(), params);
        }
    }

    @Override
    public void onJoinChannelSuccess(final String channel, final int uid, final int elapsed) {
        runOnUiThread(() -> {
            if (isFinishing()) {
                return;
            }

            config().mUid = uid;
            channelName = channel;

            if ("true".equals(agora.getIsTest())) {
                agoraAPI.login2(agora.getAppID(), "" + uid, "noneed_token", 0, "", 20, 30);
            } else {
                agoraAPI.login2(agora.getAppID(), "" + uid, agora.getSignalingKey(), 0, "", 20, 30);
            }

        });
    }

    private String meetingJoinTraceId;

    private OkHttpCallback meetingJoinStatsCallback() {
        return new OkHttpCallback<Bucket<MeetingJoinStats>>() {

            @Override
            public void onSuccess(Bucket<MeetingJoinStats> meetingJoinStatsBucket) {
                if (TextUtils.isEmpty(meetingJoinTraceId)) {
                    meetingJoinTraceId = meetingJoinStatsBucket.getData().getId();
                } else {
                    meetingJoinTraceId = null;
                }
            }

            @Override
            public void onFailure(int errorCode, BaseException exception) {
                super.onFailure(errorCode, exception);
            }
        };
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        runOnUiThread(() -> {
            if (isFinishing()) {
                return;
            }
            if (BuildConfig.DEBUG) {
                Toast.makeText(InviteMeetingBroadcastActivity.this, "参会人" + uid + "的视频流进入", Toast.LENGTH_SHORT).show();
            }

            SurfaceView remoteAudienceSurfaceView = RtcEngine.CreateRendererView(getApplicationContext());
            remoteAudienceSurfaceView.setZOrderOnTop(true);
            remoteAudienceSurfaceView.setZOrderMediaOverlay(true);
            rtcEngine().setupRemoteVideo(new VideoCanvas(remoteAudienceSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));

            AudienceVideo audienceVideo = new AudienceVideo();
            audienceVideo.setUid(uid);
            audienceVideo.setName("参会人" + uid);
            audienceVideo.setBroadcaster(false);
            audienceVideo.setSurfaceView(remoteAudienceSurfaceView);
            audienceVideoAdapter.insertItem(audienceVideo);
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

            audienceVideoAdapter.deleteItem(uid);

            if (BuildConfig.DEBUG)
                Toast.makeText(InviteMeetingBroadcastActivity.this, "参会人" + uid + "退出了", Toast.LENGTH_SHORT).show();

        });
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
    public void onConnectionLost() {
        runOnUiThread(() -> {
            Toast.makeText(InviteMeetingBroadcastActivity.this, "与声网服务器连接断开，请检查网络连接", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public void onConnectionInterrupted() {
        runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, "网络连接不佳，视频将会有卡顿，可尝试降低分辨率", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onUserMuteVideo(final int uid, final boolean muted) {
        if (BuildConfig.DEBUG) {
            runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, uid + " 的视频被暂停了 " + muted, Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onLastmileQuality(final int quality) {
        if (BuildConfig.DEBUG) {
            runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, "本地网络质量报告：" + showNetQuality(quality), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        if (BuildConfig.DEBUG) {
            runOnUiThread(() -> {
//                    Toast.makeText(MeetingBroadcastActivity.this, "用户" + uid + "的\n上行网络质量：" + showNetQuality(txQuality) + "\n下行网络质量：" + showNetQuality(rxQuality), Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public void onWarning(int warn) {
        if (BuildConfig.DEBUG) {
//            runOnUiThread(() -> Toast.makeText(MeetingBroadcastActivity.this, "警告码：" + warn, Toast.LENGTH_SHORT).show());
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
    public void onError(final int err) {
        if (BuildConfig.DEBUG) {
            runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, "错误码：" + err, Toast.LENGTH_SHORT).show());
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

    @Override
    protected void onStop() {
        super.onStop();
        doLeaveChannel();

        currentMaterial = null;

        if (agoraAPI.getStatus() == 2) {
            agoraAPI.channelDelAttr(channelName, DOC_INFO);
            agoraAPI.logout();
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        releaseMicOccupy();

        TCAgent.onPageEnd(this, TAG);

        unregisterReceiver(mUsbReceiver);
        unregisterReceiver(homeKeyEventReceiver);

        currentMaterial = null;

        if (agoraAPI.getStatus() == 2) {
            agoraAPI.channelClearAttr(channelName);
            agoraAPI.logout();
        }
        agoraAPI.destroy();

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

                    currentMaterial = null;

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("clientUid", "" + config().mUid);
                    params.put("hostUserId", Preferences.getUserId());
                    params.put("hostUserName", meetingJoin.getHostUser().getHostUserName());
                    params.put("status", 2);
                    ApiClient.getInstance().meetingLeaveTemp(TAG, meetingTempLeaveCallback, meetingJoin.getMeeting().getId(), params);

                    doLeaveChannel();
                    if (agoraAPI.getStatus() == 2) {
                        agoraAPI.channelDelAttr(channelName, DOC_INFO);
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
