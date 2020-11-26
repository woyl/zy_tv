package io.agora.openlive.ui;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhongyou.meettvapplicaion.BaseApplication;
import com.zhongyou.meettvapplicaion.BuildConfig;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.entities.Agora;
import com.zhongyou.meettvapplicaion.entities.base.BaseErrorBean;
import com.zhongyou.meettvapplicaion.event.ExitChatEvent;
import com.zhongyou.meettvapplicaion.event.HangOnEvent;
import com.zhongyou.meettvapplicaion.net.ApiClient;
import com.zhongyou.meettvapplicaion.net.OkHttpCallback;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.CameraHelper;
import com.zhongyou.meettvapplicaion.utils.CircleTransform;
import com.zhongyou.meettvapplicaion.utils.RxBus;
import com.zhongyou.meettvapplicaion.utils.UIDUtil;
import com.zhongyou.meettvapplicaion.utils.statistics.ZYAgent;
import com.squareup.picasso.Picasso;
import com.tendcloud.tenddata.TCAgent;
import com.yunos.tv.alitvasrsdk.ASRCommandReturn;
import com.yunos.tv.alitvasrsdk.AliTVASRManager;
import com.yunos.tv.alitvasrsdk.OnASRCommandListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import io.agora.AgoraAPI;
import io.agora.AgoraAPIOnlySignal;
import io.agora.openlive.model.AGEventHandler;
import io.agora.openlive.model.ConstantApp;
import io.agora.openlive.model.VideoStatusData;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import rx.Subscription;
import rx.functions.Action1;

public class LiveRoomActivity extends BaseActivity implements AGEventHandler {

    private final String TAG = LiveRoomActivity.class.getSimpleName();
    private final static Logger log = LoggerFactory.getLogger(LiveRoomActivity.class);

    private GridVideoViewContainer mGridVideoViewContainer;

    private RelativeLayout mSmallVideoViewDock;

    /**TextureView*/
    private final HashMap<Integer, TextureView> mUidsList = new HashMap<>(); // uid = 0 || uid == EngineConfig.mUid

    private AudioManager mAudioManager;

    private String deviceInfo;

    private String channelName, callInfo, photo, name, shopPhoto;
    private Agora agora;

    private TextView saleNameText, storeNameText, deviceInfoText, countText, netTipsText;

    private TextView myNameText, myAddressText, cameraTipsText;
    private ImageView myAvatarImage;

    private ImageView bgImage, storeImage;
    private int shopOwnerResolution;

    private int remoteUid;
    private Subscription hangonScription;

    private AgoraAPIOnlySignal agoraAPI;

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
            Log.e(TAG,"--- 接收到广播， action: " + action);
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Log.e(TAG, "USB device is Attached: " + deviceName);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (CameraHelper.getNumberOfCameras() > 0) {
                            Toast.makeText(getApplicationContext(), "检测到有" + CameraHelper.getNumberOfCameras() + "个摄像头插入", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "没有检测到摄像头", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 1000);
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Log.e(TAG, "USB device is Detached: " + deviceName);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (CameraHelper.getNumberOfCameras() <= 0) {
                            Toast.makeText(getApplicationContext(), "检测到摄像头被拔出", Toast.LENGTH_SHORT).show();
                            cameraTipsText.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(getApplicationContext(), "检测到还有" + CameraHelper.getNumberOfCameras() + "个摄像头", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shopOwnerResolution = getIntent().getIntExtra("shopOwnerResolution", 0);
        if (shopOwnerResolution == 4) {
            setContentView(R.layout.activity_chat);
        } else {
            setContentView(R.layout.activity_chat_small);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);
        TCAgent.onEvent(this, "进入电视端导购直播界面");

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

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
        ZYAgent.onPageStart(this, "视频通话");
    }

    @Override
    protected void onPause() {
        super.onPause();
        ZYAgent.onPageEnd(this, "视频通话");
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
        event().addEventHandler(this);

        Intent i = getIntent();
        int cRole = i.getIntExtra(ConstantApp.ACTION_KEY_CROLE, 0);

        if (cRole == 0) {
            throw new RuntimeException("Should not reach here");
        }

        channelName = i.getStringExtra(ConstantApp.ACTION_KEY_ROOM_NAME);
        callInfo = i.getStringExtra("callInfo");

        deviceInfo = i.getStringExtra("deviceInfo");
        deviceInfoText = (TextView) findViewById(R.id.device_info);
        deviceInfoText.setText("设备型号　" + deviceInfo);

        agora = i.getParcelableExtra("agora");

        photo = i.getStringExtra("photo");
        name = i.getStringExtra("name");
        shopPhoto = i.getStringExtra("shopPhoto");

        config().mUid = Integer.parseInt(UIDUtil.generatorUID(Preferences.getUserId()));

        doConfigEngine(cRole);

        mGridVideoViewContainer = (GridVideoViewContainer) findViewById(R.id.grid_video_view_container);

        mGridVideoViewContainer.setItemEventHandler(new VideoViewEventListener() {
            @Override
            public void onItemDoubleClick(View v, Object item) {
                log.debug("onItemDoubleClick " + v + " " + item);

                if (mUidsList.size() < 2) {
                    return;
                }

                if (mViewType == VIEW_TYPE_DEFAULT)
                    switchToSmallVideoView(((VideoStatusData) item).mUid);
                else
                    switchToDefaultVideoView();
            }
        });

        /**TextureView*/
        TextureView surfaceV = RtcEngine.CreateTextureView(getApplicationContext());
        rtcEngine().setupLocalVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, config().mUid));
//        surfaceV.setZOrderOnTop(true);
//        surfaceV.setZOrderMediaOverlay(true);

        mUidsList.put(config().mUid, surfaceV); // get first surface view

        mGridVideoViewContainer.initViewContainer(getApplicationContext(), config().mUid, mUidsList, shopOwnerResolution == 4); // first is now full view
        worker().preview2(true, surfaceV, config().mUid);

        if ("true".equals(agora.getIsTest())) {
            worker().joinChannel(null, channelName, config().mUid);
        } else {
            worker().joinChannel(agora.getToken(), channelName, config().mUid);
        }


        if (shopOwnerResolution != 4) {
            bgImage = (ImageView) findViewById(R.id.avatar);
            Picasso.with(LiveRoomActivity.this).load(photo).transform(new CircleTransform()).into(bgImage);
        }

        countText = (TextView) findViewById(R.id.online_count);

        cameraTipsText = (TextView) findViewById(R.id.camera_tips);
        netTipsText = (TextView) findViewById(R.id.net_tips);

        agoraAPI = AgoraAPIOnlySignal.getInstance(this, agora.getAppID());
        agoraAPI.callbackSet(new AgoraAPI.CallBack() {

            @Override
            public void onLoginSuccess(int uid, int fd) {
                super.onLoginSuccess(uid, fd);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LiveRoomActivity.this, "login success", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                agoraAPI.channelJoin(channelName);
            }

            @Override
            public void onLoginFailed(final int ecode) {
                super.onLoginFailed(ecode);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LiveRoomActivity.this, "login failed " + ecode, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onLogout(final int ecode) {
                super.onLogout(ecode);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LiveRoomActivity.this, "logout " + ecode, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onChannelLeaved(final String channelID, final int ecode) {
                super.onChannelLeaved(channelID, ecode);

                if (BuildConfig.DEBUG) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LiveRoomActivity.this, channelID + " leaved becourse " + ecode, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                agoraAPI.channelJoin(channelName);
            }

            @Override
            public void onChannelJoined(final String channelID) {
                super.onChannelJoined(channelID);

                if (BuildConfig.DEBUG) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LiveRoomActivity.this, "channel joined " + channelID, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                agoraAPI.channelQueryUserNum(channelName);
            }

            @Override
            public void onChannelJoinFailed(String channelID, int ecode) {
                super.onChannelJoinFailed(channelID, ecode);

            }

            @Override
            public void onChannelQueryUserNumResult(String channelID, int ecode, final int num) {
                super.onChannelQueryUserNumResult(channelID, ecode, num);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        countText.setText("当前在线人数：" + num);
                    }
                });
            }

            @Override
            public void onChannelUserJoined(String account, int uid) {
                super.onChannelUserJoined(account, uid);
                agoraAPI.channelQueryUserNum(channelName);
            }

            @Override
            public void onChannelUserLeaved(String account, int uid) {
                super.onChannelUserLeaved(account, uid);
                agoraAPI.channelQueryUserNum(channelName);
            }

            @Override
            public void onLog(final String txt) {
                super.onLog(txt);
                Log.d("onLog--->", txt);
            }
        });
        agoraAPI.login(agora.getAppID(), UIDUtil.generatorUID(Preferences.getUserId()), agora.getSignalingKey(), 0, "");

        myNameText = (TextView) findViewById(R.id.expostor_name);
        if (myNameText != null) {
            myNameText.setText(Preferences.getUserName());
        }
        myAddressText = (TextView) findViewById(R.id.expostor_address);
        if (myAddressText != null) {
            myAddressText.setText(Preferences.getUserAddress());
        }
        myAvatarImage = (ImageView) findViewById(R.id.expostor_avatar);
        if (myAvatarImage != null) {
            Picasso.with(this).load(Preferences.getUserPhoto()).into(myAvatarImage);
        }

        saleNameText = (TextView) findViewById(R.id.sale_name);
        if (saleNameText != null) {
            saleNameText.setText(callInfo.substring(callInfo.indexOf(" ")));
        }
        storeNameText = (TextView) findViewById(R.id.store_name);
        if (storeNameText != null) {
            storeNameText.setText(callInfo.substring(0, callInfo.indexOf(" ")));
        }

        storeImage = (ImageView) findViewById(R.id.store_image);
        if (storeImage != null && !TextUtils.isEmpty(shopPhoto)) {
            Picasso.with(this).load(shopPhoto).into(storeImage);
        }

        findViewById(R.id.end_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopCallRecord();
            }
        });

        hangonScription = RxBus.handleMessage(new Action1() {
            @Override
            public void call(Object o) {
                if (o instanceof HangOnEvent) {
                    hangonScription.unsubscribe();
                    ApiClient.getInstance().startOrStopOrRejectCallExpostor(channelName, "7", new ExpostorCallback());
                }
            }
        });
    }

    class ExpostorCallback extends OkHttpCallback<BaseErrorBean> {

        @Override
        public void onSuccess(BaseErrorBean entity) {
            Log.d("stop when calling", entity.toString());
            Toast.makeText(LiveRoomActivity.this, "通话被其他应用打断", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFinish() {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_LEFT:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void doConfigEngine(int cRole) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int prefIndex = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, ConstantApp.DEFAULT_PROFILE_IDX);
//        if (prefIndex > ConstantApp.VIDEO_PROFILES.length - 1) {
//            prefIndex = ConstantApp.DEFAULT_PROFILE_IDX;
//        }
        int vProfile = ConstantApp.VIDEO_PROFILES[prefIndex - 2];

        worker().configEngine(cRole, VideoEncoderConfiguration.VD_320x180);
    }

    @Override
    protected void deInitUIandEvent() {
        doLeaveChannel();
        event().removeEventHandler(this);

        mUidsList.clear();
    }

    private void doLeaveChannel() {
        worker().leaveChannel(config().mChannel);
        worker().preview2(false, null, 0);
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        doRenderRemoteUi(uid);
    }

    private void doRenderRemoteUi(final int uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                if (mUidsList.containsKey(uid)) {
                    return;
                }

                /**TextureView*/
                TextureView surfaceV = RtcEngine.CreateTextureView(getApplicationContext());
                boolean useDefaultLayout = mViewType == VIEW_TYPE_DEFAULT && mUidsList.size() != 2;
//                surfaceV.setZOrderOnTop(!useDefaultLayout);
//                surfaceV.setZOrderMediaOverlay(!useDefaultLayout);
                mUidsList.put(uid, surfaceV);
                rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));

                remoteUid = uid;

                if (!useDefaultLayout) {
                    log.debug("doRenderRemoteUi LAYOUT_TYPE_DEFAULT " + (uid & 0xFFFFFFFFL));
                    switchToDefaultVideoView();
                } else {
//                    int bigBgUid = mSmallVideoViewAdapter == null ? uid : mSmallVideoViewAdapter.getExceptedUid();
//                    log.debug("doRenderRemoteUi LAYOUT_TYPE_SMALL " + (uid & 0xFFFFFFFFL) + " " + (bigBgUid & 0xFFFFFFFFL));
                    switchToSmallVideoView(remoteUid);
                }


                findViewById(R.id.end_call).setVisibility(View.VISIBLE);
                findViewById(R.id.end_call).requestFocus();
            }
        });
    }

    @Override
    public void onJoinChannelSuccess(final String channel, final int uid, final int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                if (mUidsList.containsKey(uid)) {
                    log.debug("already added to UI, ignore it " + (uid & 0xFFFFFFFFL) + " " + mUidsList.get(uid));
                    return;
                }

                worker().getEngineConfig().mUid = uid;

                TextureView surfaceV = mUidsList.remove(0);
                if (surfaceV != null) {
                    mUidsList.put(uid, surfaceV);
                }

            }
        });
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {

    }

    @Override
    public void onUserOffline(int uid, int reason) {
        log.debug("onUserOffline " + (uid & 0xFFFFFFFFL) + " " + reason);
        doRemoveRemoteUi(uid);
    }


    @Override
    public void onLastmileQuality(final int quality) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LiveRoomActivity.this, "本地网络质量报告：" + showNetQuality(quality), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String showNetQuality(int quality){
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
    public void onConnectionLost() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LiveRoomActivity.this, "网络连接断开，请检查网络连接", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public void onConnectionInterrupted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LiveRoomActivity.this, "网络连接不佳，视频将会有卡顿，可尝试降低分辨率", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUserMuteVideo(final int uid, final boolean muted) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LiveRoomActivity.this, uid + " 的视频被暂停了 " + muted, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUserMuteAudio(int uid, boolean muted) {

    }

    @Override
    public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {

    }

    @Override
    public void onNetworkQuality(final int uid, final int txQuality, final int rxQuality) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (uid == 0) {
                    if (txQuality > 2 || rxQuality > 2) {
                        netTipsText.setVisibility(View.VISIBLE);
                    } else {
                        netTipsText.setVisibility(View.GONE);
                    }
                } else {

                }
//                Toast.makeText(LiveRoomActivity.this, "用户" + uid + "的\n上行网络质量：" + showNetQuality(txQuality) + "\n下行网络质量：" + showNetQuality(rxQuality), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onWarning(int warn) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LiveRoomActivity.this, "警告码：" + warn, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onError(final int err) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LiveRoomActivity.this, "错误码：" + err, Toast.LENGTH_SHORT).show();
            }
        });
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

    /**TextureView*/
    private void requestRemoteStreamType(final int currentHostCount) {
        log.debug("requestRemoteStreamType " + currentHostCount);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HashMap.Entry<Integer, TextureView> highest = null;
                    for (HashMap.Entry<Integer, TextureView> pair : mUidsList.entrySet()) {
                    log.debug("requestRemoteStreamType " + currentHostCount + " local " + (config().mUid & 0xFFFFFFFFL) + " " + (pair.getKey() & 0xFFFFFFFFL) + " " + pair.getValue().getHeight() + " " + pair.getValue().getWidth());
                    if (pair.getKey() != config().mUid && (highest == null || highest.getValue().getHeight() < pair.getValue().getHeight())) {
                        if (highest != null) {
                            rtcEngine().setRemoteVideoStreamType(highest.getKey(), Constants.VIDEO_STREAM_LOW);
                            log.debug("setRemoteVideoStreamType switch highest VIDEO_STREAM_LOW " + currentHostCount + " " + (highest.getKey() & 0xFFFFFFFFL) + " " + highest.getValue().getWidth() + " " + highest.getValue().getHeight());
                        }
                        highest = pair;
                    } else if (pair.getKey() != config().mUid && (highest != null && highest.getValue().getHeight() >= pair.getValue().getHeight())) {
                        rtcEngine().setRemoteVideoStreamType(pair.getKey(), Constants.VIDEO_STREAM_LOW);
                        log.debug("setRemoteVideoStreamType VIDEO_STREAM_LOW " + currentHostCount + " " + (pair.getKey() & 0xFFFFFFFFL) + " " + pair.getValue().getWidth() + " " + pair.getValue().getHeight());
                    }
                }
                if (highest != null && highest.getKey() != 0) {
                    rtcEngine().setRemoteVideoStreamType(highest.getKey(), Constants.VIDEO_STREAM_HIGH);
                    log.debug("setRemoteVideoStreamType VIDEO_STREAM_HIGH " + currentHostCount + " " + (highest.getKey() & 0xFFFFFFFFL) + " " + highest.getValue().getWidth() + " " + highest.getValue().getHeight());
                }
            }
        }, 500);
    }

    private void doRemoveRemoteUi(final int uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                mUidsList.remove(uid);

                int bigBgUid = -1;
                if (mSmallVideoViewAdapter != null) {
                    bigBgUid = mSmallVideoViewAdapter.getExceptedUid();
                }

                log.debug("doRemoveRemoteUi " + (uid & 0xFFFFFFFFL) + " " + (bigBgUid & 0xFFFFFFFFL));

                if (mViewType == VIEW_TYPE_DEFAULT || uid == bigBgUid) {
                    switchToDefaultVideoView();
                } else {
                    switchToSmallVideoView(bigBgUid);
                }
                stopCallRecord();
            }
        });
    }

    private SmallVideoViewAdapter mSmallVideoViewAdapter;

    private void switchToDefaultVideoView() {
        if (mSmallVideoViewDock != null)
            mSmallVideoViewDock.setVisibility(View.GONE);
        mGridVideoViewContainer.initViewContainer(getApplicationContext(), config().mUid, mUidsList, shopOwnerResolution == 4);

        mViewType = VIEW_TYPE_DEFAULT;

        int sizeLimit = mUidsList.size();
        if (sizeLimit > ConstantApp.MAX_PEER_COUNT + 1) {
            sizeLimit = ConstantApp.MAX_PEER_COUNT + 1;
        }
        for (int i = 0; i < sizeLimit; i++) {
            int uid = mGridVideoViewContainer.getItem(i).mUid;
            if (config().mUid != uid) {
                rtcEngine().setRemoteVideoStreamType(uid, Constants.VIDEO_STREAM_HIGH);
                log.debug("setRemoteVideoStreamType VIDEO_STREAM_HIGH " + mUidsList.size() + " " + (uid & 0xFFFFFFFFL));
            }
        }
    }

    private void switchToSmallVideoView(int uid) {
        HashMap<Integer, TextureView> slice = new HashMap<>(1);
        slice.put(uid, mUidsList.get(uid));
        mGridVideoViewContainer.initViewContainer(getApplicationContext(), uid, slice, shopOwnerResolution == 4);

        bindToSmallVideoView(uid);

        mViewType = VIEW_TYPE_SMALL;

        requestRemoteStreamType(mUidsList.size());
    }

    public int mViewType = VIEW_TYPE_DEFAULT;

    public static final int VIEW_TYPE_DEFAULT = 0;

    public static final int VIEW_TYPE_SMALL = 1;

    private void bindToSmallVideoView(int exceptUid) {
        if (mSmallVideoViewDock == null) {
            ViewStub stub = (ViewStub) findViewById(R.id.small_video_view_dock);
            mSmallVideoViewDock = (RelativeLayout) stub.inflate();
        }

        RecyclerView recycler = (RecyclerView) findViewById(R.id.small_video_view_container);

        boolean create = false;

        if (mSmallVideoViewAdapter == null) {
            create = true;
            mSmallVideoViewAdapter = new SmallVideoViewAdapter(this, exceptUid, mUidsList, new VideoViewEventListener() {
                @Override
                public void onItemDoubleClick(View v, Object item) {
                    switchToDefaultVideoView();
                }
            });
            mSmallVideoViewAdapter.setHasStableIds(true);
        }
        recycler.setHasFixedSize(true);

        recycler.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        recycler.setAdapter(mSmallVideoViewAdapter);

        recycler.setDrawingCacheEnabled(true);
        recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

        if (!create) {
            mSmallVideoViewAdapter.notifyUiChanged(mUidsList, exceptUid, null, null);
        }
        recycler.setVisibility(View.VISIBLE);
        mSmallVideoViewDock.setVisibility(View.VISIBLE);
    }


    private Dialog dialog;

    @Override
    public void onBackPressed() {
        View view = View.inflate(this, R.layout.dialog_cancle_delete, null);
        TextView textView3 = (TextView) view.findViewById(R.id.textView3);
        textView3.setText("您要退出当前通话吗？");

        view.findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                stopCallRecord();
            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog = new Dialog(this, R.style.MyDialog);
        dialog.setContentView(view);
        dialog.show();
    }

    public void stopCallRecord(){
        ApiClient.getInstance().startOrStopOrRejectCallExpostor(channelName, "9", new OkHttpCallback<BaseErrorBean>() {
            @Override
            public void onSuccess(BaseErrorBean entity) {
                Log.d("stop receive", entity.toString());
                TCAgent.onEvent(LiveRoomActivity.this, "停止直播更新callrecord状态为9成功");
            }

            @Override
            public void onFinish() {
                RxBus.sendMessage(new ExitChatEvent());
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        hangonScription.unsubscribe();

        unregisterReceiver(mUsbReceiver);

        agoraAPI.logout();

        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);

        //打开语音搜索
        try {
            mAliTVASRManager.setAliTVASREnable(true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mAliTVASRManager.release();

        BaseApplication.getInstance().deInitWorkerThread();
    }
}
