package io.agora.openlive.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.alibaba.android.vlayout.layout.MyGridLayoutHelper;
import com.alibaba.android.vlayout.layout.SpliteGridLayoutHelper;
import com.alibaba.android.vlayout.layout.StaggeredGridLayoutHelper;
import com.bumptech.glide.Glide;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.zhongyou.meettvapplicaion.ApiClient;
import com.zhongyou.meettvapplicaion.BaseException;
import com.zhongyou.meettvapplicaion.BuildConfig;
import com.zhongyou.meettvapplicaion.Constant;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.guide.phone.meetingcamera.activity.Camera1ByServiceActivity;
import com.zhongyou.meettvapplicaion.business.ForumActivity;
import com.zhongyou.meettvapplicaion.business.IMTVChatActivity;
import com.zhongyou.meettvapplicaion.business.adapter.NewAudienceVideoAdapter;
import com.zhongyou.meettvapplicaion.entities.Agora;
import com.zhongyou.meettvapplicaion.entities.Audience;
import com.zhongyou.meettvapplicaion.entities.AudienceVideo;
import com.zhongyou.meettvapplicaion.entities.Bucket;
import com.zhongyou.meettvapplicaion.entities.ForumContent;
import com.zhongyou.meettvapplicaion.entities.ForumRevokeContent;
import com.zhongyou.meettvapplicaion.entities.HostUser;
import com.zhongyou.meettvapplicaion.entities.Material;
import com.zhongyou.meettvapplicaion.entities.Meeting;
import com.zhongyou.meettvapplicaion.entities.MeetingHostingStats;
import com.zhongyou.meettvapplicaion.entities.MeetingJoin;
import com.zhongyou.meettvapplicaion.entities.MeetingJoinStats;
import com.zhongyou.meettvapplicaion.entities.MeetingMaterialsPublish;
import com.zhongyou.meettvapplicaion.entities.MeetingScreenShot;
import com.zhongyou.meettvapplicaion.entities.PageData;
import com.zhongyou.meettvapplicaion.entities.QiniuToken;
import com.zhongyou.meettvapplicaion.entities.base.BaseBean;
import com.zhongyou.meettvapplicaion.event.ForumActivityCloseEvent;
import com.zhongyou.meettvapplicaion.event.IMMessgeEvent;
import com.zhongyou.meettvapplicaion.im.IMChatMessage;
import com.zhongyou.meettvapplicaion.im.NoPluginTVConversationFragment;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.guide.phone.meetingcamera.camera.CameraHelper;
import com.zhongyou.meettvapplicaion.utils.DisplayUtil;
import com.zhongyou.meettvapplicaion.utils.OkHttpCallback;
import com.zhongyou.meettvapplicaion.utils.RxBus;
import com.zhongyou.meettvapplicaion.utils.SizeUtils;
import com.zhongyou.meettvapplicaion.utils.StringUtils;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;
import com.zhongyou.meettvapplicaion.utils.UIDUtil;
import com.zhongyou.meettvapplicaion.utils.helper.ImageHelper;
import com.zhongyou.meettvapplicaion.utils.statistics.ZYAgent;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.squareup.picasso.Picasso;
import com.tendcloud.tenddata.TCAgent;
import com.yunos.tv.alitvasrsdk.ASRCommandReturn;
import com.yunos.tv.alitvasrsdk.AliTVASRManager;
import com.yunos.tv.alitvasrsdk.OnASRCommandListener;
import com.zhongyou.meettvapplicaion.view.GridSpaceItemDecoration;
import com.zhongyou.meettvapplicaion.view.PreviewPlayer;
import com.zhongyou.meettvapplicaion.view.SpaceItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import io.agora.AgoraAPI;
import io.agora.AgoraAPIOnlySignal;
import io.agora.openlive.model.AGEventHandler;
import io.agora.openlive.model.ConstantApp;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.TextMessage;
import me.jessyan.autosize.utils.AutoSizeUtils;
import rx.Subscription;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static com.zhongyou.meettvapplicaion.utils.ToastUtils.showToast;
import static io.agora.rtc.Constants.REMOTE_AUDIO_REASON_INTERNAL;
import static io.agora.rtc.Constants.REMOTE_AUDIO_REASON_LOCAL_MUTED;
import static io.agora.rtc.Constants.REMOTE_AUDIO_REASON_LOCAL_UNMUTED;
import static io.agora.rtc.Constants.REMOTE_AUDIO_REASON_NETWORK_CONGESTION;
import static io.agora.rtc.Constants.REMOTE_AUDIO_REASON_NETWORK_RECOVERY;
import static io.agora.rtc.Constants.REMOTE_AUDIO_REASON_REMOTE_MUTED;
import static io.agora.rtc.Constants.REMOTE_AUDIO_REASON_REMOTE_OFFLINE;
import static io.agora.rtc.Constants.REMOTE_AUDIO_REASON_REMOTE_UNMUTED;
import static java.lang.System.setProperty;

public class MeetingAudienceActivity extends BaseActivity implements AGEventHandler {

    private final static Logger LOG = LoggerFactory.getLogger(MeetingAudienceActivity.class);

    private final String TAG = MeetingAudienceActivity.class.getSimpleName();
    private Button mMuteAudio;
    private boolean isMuted;
    private TextView mSwtichCamera;
    private MeetingJoin meetingJoin;
    private Meeting meeting;
    private Agora agora;
    private String broadcastId;
    private Material currentMaterial;
    private int doc_index = 0;
    private SpaceItemDecoration mDecor;

    private FrameLayout broadcasterLayout, audienceLayout, broadcastSmailLayout, localFrameLayout;
    private TextView broadcastNameText, broadcastTipsText, audienceNameText, audienceTipsText;
    private Button micButton, finishButton, exitButton, full_screen;
    private ImageView docImage, forum_item_more_audience_msg_img;
    private TextView pageText;
    private TextView onlineCountText;
    private TextView roleTagText;
    private static TextView forum_item_more_audience_msg;

    private String channelName;

    private String audienceName;

    private int currentAudienceId;
    private int count = 0;//当意外退出时  再重新进入会议 会重复收到几条一样的消息 导致视频画面可能不可见 使用次数来判断
    private int pptMode = -1;
    private static final String CALLING_AUDIENCE = "calling_audience";
    private static final String DOC_INFO = "doc_info";
    private static final String MODEL_CHANGE = "model_change";
    private static final String EQUALLY = "equally";
    private static final String BIGSCREEN = "bigScreen";

//    private SurfaceView remoteBroadcastSurfaceView, localAudienceSurfaceView, remoteAudienceSurfaceView;
    /**TextureView*/
    private TextureView remoteBroadcastSurfaceView, localAudienceSurfaceView, remoteAudienceSurfaceView;
    private Subscription subscription;
    private ConstraintLayout constraintLayout_forum_audience_newmsg;
    private LinearLayout forum_item_audience_more;
    private TextSwitcher textswitcher_forum_item_audience_notification;
    private static final int HANDLER_UPDATE_FORUM_TEXT = 1;
    private static final int HANDLER_UPDATE_FORUM_TEXT_NO = 1;
    private static final int HANDLER_UPDATE_FORUM_TEXT_MORE = 2;

    private final ForumItemMoreTextHandler forumItemMoreTextHandler = new ForumItemMoreTextHandler(this);

    private Timer takePhotoTimer;
    private TimerTask takePhotoTimerTask;
    private final int CODE_REQUEST_TAKEPHOTO = 8011;

    private RelativeLayout mMsgContent;
    private TextView mMsgText;
    private TextView mNetWorkNumber;
    private TextView mNetworkIcon;

    /**连麦状态*/
    private ImageView img_line_state;

    /**设置是否被禁言*/
    private boolean isStopSpeak;

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
            String deviceName = "";
            if (usbDevice != null) {
                deviceName = usbDevice.getDeviceName();
            }
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
                            Audience audience = (Audience) audienceNameText.getTag();
                            if (audience != null) {
                                if (BuildConfig.DEBUG) {
                                    Toast.makeText(getApplicationContext(), "检测到摄像头被拔出" + audience.toString(), Toast.LENGTH_SHORT).show();
                                }
                                if (config().mUid == audience.getUid()) {
                                    audienceLayout.removeAllViews();

                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("finish", true);
                                        agoraAPI.messageInstantSend(broadcastId, 0, jsonObject.toString(), "");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);

                                    Toast.makeText(getApplicationContext(), "摄像头异常，请重新进入教室" + audience.toString(), Toast.LENGTH_SHORT).show();

                                    if (!TextUtils.isEmpty(meetingHostJoinTraceId)) {
                                        HashMap<String, Object> params = new HashMap<String, Object>();
                                        params.put("meetingHostJoinTraceId", meetingHostJoinTraceId);
                                        params.put("status", 2);
                                        params.put("meetingId", meetingJoin.getMeeting().getId());
                                        params.put("type", 2);
                                        params.put("leaveType", 1);
                                        ApiClient.getInstance().meetingHostStats(TAG, meetingHostJoinTraceCallback, params);
                                    }

//                                    agoraAPI.channelLeave(channelName);
                                    if (agoraAPI.getStatus() == 2) {
                                        agoraAPI.logout();
                                    }
                                    finish();
                                }
                            }
                            if (handsUp) {
                                handsUp = false;
                                micButton.setText("申请发言");
//								micButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_meeting_signup, 0, 0, 0);
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("auditStatus", Preferences.getUserAuditStatus());
                                    jsonObject.put("postTypeName", Preferences.getUserPostTypeName());
                                    jsonObject.put("handsUp", false);
                                    jsonObject.put("uid", config().mUid);
                                    jsonObject.put("uname", Preferences.getUserName());
                                    agoraAPI.messageInstantSend(broadcastId, 0, jsonObject.toString(), "");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(getApplicationContext(), "检测到还有" + CameraHelper.getNumberOfCameras() + "个摄像头", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, 1000);
            } else if (Constant.KEY_ONMessageArrived.equals(action)) {
                io.rong.imlib.model.Message message = intent.getParcelableExtra(Constant.KEY_IMMESSAGE_ARRIVE);

                if (mChatFragment != null) {
                    mChatFragment.insertData(message);
                }

            } else if (Constant.KEY_IMMESSAGE_RECALL.equals(action)) {
                io.rong.imlib.model.Message message = intent.getParcelableExtra(Constant.KEY_IMMESSAGE_RECALL_MESSAGE);
                if (mChatFragment != null) {
                    List<IMChatMessage> data = mChatFragment.getData();
                    message.setObjectName("RC:GrpNtf");
                    for (int i = 0; i < data.size(); i++) {
                        if (data.get(i).getMsg().getMessageId() == message.getMessageId()) {
                            //Logger.e("i: " + i + "meesgeId:" + datalists.get(i).getMsg().getMessageId() + "----" + message.getMessageId());
                            IMChatMessage msg = data.get(i);
                            msg.setMsg(message);
                            msg.setItemType(IMChatMessage.NOTIFY);
                            msg.setMsg(message);
                            mChatFragment.getConversationAdaterEx().notifyItemRangeChanged(i, 1);
                            break;
                        }
                    }
                }
            }
        }
    };

    private AgoraAPIOnlySignal agoraAPI;
    private RecyclerView mAudienceRecyclerView;
    private VirtualLayoutManager mVirtualLayoutManager;
    private DelegateAdapter mDelegateAdapter;
    private NewAudienceVideoAdapter mVideoAdapter;
    private AudienceVideo mCurrentAudienceVideo;
    /**TextureView*/
//    private SurfaceView mAudienceVideoSurfaceView;
    private TextureView mAudienceVideoSurfaceView;

    private boolean isHostCommeIn, isFullScreen, isSplitView;
    private AudienceVideo mLocalAudienceVideo;
    private Button mDiscussButton;
    private SizeUtils mSizeUtils;
    private boolean isPPTModel;
    private int lastX, lastY;
    int touchCount = 0;
    private View mVideoContainer;
    private GridSpaceItemDecoration mGridSpaceItemDecoration;
    private TextView mCurrentNetSpeed;
    private boolean mCallingAudience;

    /**TextureView*/
    private void stripSurfaceView(TextureView view) {
        if (view == null) {
            mLogger.e("view==null");
            return;
        }
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((FrameLayout) parent).removeView(view);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_audience);

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(Constant.KEY_ONMessageArrived);
        filter.addAction(Constant.KEY_IMMESSAGE_RECALL);

        registerReceiver(mUsbReceiver, filter);
        requestMicOccupy();
        registerReceiver(homeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        localFrameLayout = findViewById(R.id.localFrameLayout);
        View mRlayout = findViewById(R.id.parentContainer);
        mDecor = new SpaceItemDecoration(0, 5, 0, 0);
        mGridSpaceItemDecoration = new GridSpaceItemDecoration(DisplayUtil.getHeight(this) / 4);
        localFrameLayout.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (touchCount != 0) {
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                    }
                    touchCount++;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;

//						mLogger.e("dx="+dx+"   event.getRawX()"+event.getRawX()+"   lastX="+lastX);
//						mLogger.e("dy="+dy+"   event.getRawY()"+event.getRawY()+"   lastY="+lastY);
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) v.getLayoutParams();

                    int l = layoutParams.leftMargin + dx;
                    int t = layoutParams.topMargin + dy;
                    int b = mRlayout.getHeight() - t - v.getHeight();
                    int r = mRlayout.getWidth() - l - v.getWidth();
                    if (l < 0) {//处理按钮被移动到上下左右四个边缘时的情况，决定着按钮不会被移动到屏幕外边去
                        l = 0;
                        r = mRlayout.getWidth() - v.getWidth();
                    }
                    if (t < 0) {
                        t = 0;
                        b = mRlayout.getHeight() - v.getHeight();
                    }

                    if (r < 0) {
                        r = 0;
                        l = mRlayout.getWidth() - v.getWidth();
                    }
                    if (b < 0) {
                        b = 0;
                        t = mRlayout.getHeight() - v.getHeight();
                    }
                    layoutParams.leftMargin = l;
                    layoutParams.topMargin = t;
                    layoutParams.bottomMargin = b;
                    layoutParams.rightMargin = r;
//						mLogger.e("left="+l+"   top="+t+"    right="+r+"   bottom="+b);
                    v.setLayoutParams(layoutParams);

                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    v.postInvalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        });
        TCAgent.onEvent(this, "进入直播界面");
/*
		try {
			// 注册语音搜索组件
			mAliTVASRManager.init(getBaseContext(), false);
			mAliTVASRManager.setOnASRCommandListener(mASRCommandListener);
		} catch (Exception e) {
			e.printStackTrace();
		}*/

        mAudienceRecyclerView = findViewById(R.id.audience_recyclerView);
		/*mGridLayoutHelper = new MyGridLayoutHelper(2);
		mGridLayoutHelper.setHGap(10);
		mGridLayoutHelper.setVGap(10);
		mGridLayoutHelper.setItemCount(8);
		mGridLayoutHelper.setAutoExpand(false);
*/
        mVirtualLayoutManager = new VirtualLayoutManager(this);
        mDelegateAdapter = new DelegateAdapter(mVirtualLayoutManager, false);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        mAudienceRecyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 8);
        mAudienceRecyclerView.setLayoutManager(mVirtualLayoutManager);


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(AutoSizeUtils.pt2px(this, 300), RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        layoutParams.setMargins(0, AutoSizeUtils.pt2px(this, 80), DisplayUtil.dip2px(this, 20), DisplayUtil.dip2px(this, 30));
        mAudienceRecyclerView.setLayoutParams(layoutParams);


        RelativeLayout relative = findViewById(R.id.relative);

        FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) relative.getLayoutParams();
        layoutParams1.setMargins(0, 0, 0, 0);
        relative.setLayoutParams(layoutParams1);

        mAudienceRecyclerView.removeItemDecoration(mDecor);
        mAudienceRecyclerView.addItemDecoration(mDecor);
        mAudienceRecyclerView.removeItemDecoration(mGridSpaceItemDecoration);

        LinearLayoutHelper helper = new LinearLayoutHelper();
        helper.setItemCount(8);

        mVideoAdapter = new NewAudienceVideoAdapter(this, helper);
        mVideoAdapter.setItemSize(DisplayUtil.dip2px(this, 112), DisplayUtil.dip2px(this, 150));
        mDelegateAdapter.addAdapter(mVideoAdapter);
        mAudienceRecyclerView.setAdapter(mDelegateAdapter);


        mVideoContainer = findViewById(R.id.videoContainer);

        mVideoAdapter.setOnItemClickListener(new NewAudienceVideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                if (mVideoAdapter.isHaveChairMan()) {
                    //点击的如果是主持人
                    if (mVideoAdapter.getAudienceVideoLists().get(position).isBroadcaster()) {
                        if (mCurrentAudienceVideo != null) {
                            mVideoAdapter.removeItem(position);
                            mVideoAdapter.insertItem(position, mCurrentAudienceVideo);
                            broadcasterLayout.removeAllViews();
                            /**TextureView*/
//                            remoteBroadcastSurfaceView.setZOrderMediaOverlay(false);
                            stripSurfaceView(remoteBroadcastSurfaceView);
                            broadcasterLayout.addView(remoteBroadcastSurfaceView);

                        }
                        mCurrentAudienceVideo = null;
                        mAudienceVideoSurfaceView = null;
                    } else {
                        //点击的是其他人 则直接进行交换
                        if (mCurrentAudienceVideo != null) {
                            mCurrentAudienceVideo.setShowSurface(false);
                            mVideoAdapter.insertItem(mCurrentAudienceVideo);

                            mCurrentAudienceVideo = mVideoAdapter.getAudienceVideoLists().get(position);
                            mVideoAdapter.removeItem(position);

                            broadcasterLayout.removeAllViews();
                            /**TextureView*/
                            stripSurfaceView(mCurrentAudienceVideo.getTextureView());
                            broadcasterLayout.addView(mCurrentAudienceVideo.getTextureView());
                            broadcasterLayout.setVisibility(View.VISIBLE);
                            mAudienceVideoSurfaceView = mCurrentAudienceVideo.getTextureView();
                            mAudienceVideoSurfaceView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    View viewByPosition = mAudienceRecyclerView.getLayoutManager().findViewByPosition(position);
                                    if (viewByPosition != null) {
                                        viewByPosition.requestFocus();
                                    }
                                }
                            }, 300);

                        }
                    }
                    if (isPPTModel) {
                        try {
                            if (!mVideoAdapter.getAudienceVideoLists().get(position).isShowSurface()) {
                                mVideoAdapter.getAudienceVideoLists().get(position).setShowSurface(true);
                                mVideoAdapter.getAudienceVideoLists().get(1).setShowSurface(false);
                                Collections.swap(mVideoAdapter.getAudienceVideoLists(), 1, position);
                                mVideoAdapter.notifyItemRangeChanged(1, position);
                            }
                        } catch (Exception e) {

                        }
                    }
                    return;
                }
                //如果当前点击的人视频没打开 则打开视频 然后将其他人的打开的视频给关闭掉
                if (!mVideoAdapter.getAudienceVideoLists().get(position).isShowSurface()) {

                    for (int i = 0; i < mVideoAdapter.getAudienceVideoLists().size(); i++) {
                        if (i != position) {
                            if (mVideoAdapter.getAudienceVideoLists().get(i).isShowSurface()) {
                                mVideoAdapter.getAudienceVideoLists().get(i).setShowSurface(false);
                                mVideoAdapter.notifyItemChanged(i);
                            }
                        }
                    }
                    mVideoAdapter.getAudienceVideoLists().get(position).setShowSurface(true);
                    mVideoAdapter.changePositionToFirst(position);
                    mVideoAdapter.notifyItemChanged(position);
                    View viewByPosition = mAudienceRecyclerView.getLayoutManager().findViewByPosition(position);
                    if (viewByPosition != null) {
                        viewByPosition.requestFocus();
                    }
                    return;
                }

                if (isSplitView || isPPTModel) {
                    return;
                }
                //如果视频打开了 就进行交换走下面的步骤
                //将参会人的画面移到主持人界面
                broadcasterLayout.removeAllViews();
                mCurrentAudienceVideo = mVideoAdapter.getAudienceVideoLists().get(position);
                /**TextureView*/
                mAudienceVideoSurfaceView = mCurrentAudienceVideo.getTextureView();
//                mAudienceVideoSurfaceView.setZOrderMediaOverlay(false);
                mAudienceVideoSurfaceView.setTag(position);
                mVideoAdapter.removeItem(position);
                stripSurfaceView(mAudienceVideoSurfaceView);
                broadcasterLayout.addView(mAudienceVideoSurfaceView);


                stripSurfaceView(remoteBroadcastSurfaceView);
                //主持人画面 加入到列表中
                AudienceVideo audienceVideo = new AudienceVideo();
                audienceVideo.setUid(Integer.parseInt(meetingJoin.getHostUser().getClientUid()));
                audienceVideo.setName("主持人" + meetingJoin.getHostUser().getHostUserName());
                audienceVideo.setBroadcaster(true);
                /**TextureView*/
                audienceVideo.setTextureView(remoteBroadcastSurfaceView);
                mVideoAdapter.insetChairMan(0, audienceVideo);
                View viewByPosition = mAudienceRecyclerView.getLayoutManager().findViewByPosition(position);
                if (viewByPosition != null) {
                    viewByPosition.requestFocus();
                }
            }
        });


        mDiscussButton = findViewById(R.id.discuss);
        mDiscussButton.setOnClickListener(v -> {
            Intent forumIntent = new Intent(MeetingAudienceActivity.this, IMTVChatActivity.class);
            forumIntent.putExtra("groupId", channelName);
            startActivity(forumIntent);
		/*	Intent forumIntent = new Intent(MeetingAudienceActivity.this, ForumActivity.class);
			forumIntent.putExtra(ForumActivity.INTENT_KEY_FORUM, meetingJoin.getMeeting().getId());
			startActivity(forumIntent);*/
        });

        mMsgContent = findViewById(R.id.msgContent);
        mMsgText = findViewById(R.id.msg);
        mSizeUtils = new SizeUtils(this);
        full_screen = findViewById(R.id.full_screen);

        full_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPPTModel) {
                    if (isFullScreen) {
                        full_screen.setText("全屏");
                        isFullScreen = false;
                        findViewById(R.id.linearLayout).setVisibility(View.VISIBLE);
                        if (isPPTModel) {

                        } else {
                            mAudienceRecyclerView.setVisibility(View.VISIBLE);
                            mVideoAdapter.setVisibility(View.VISIBLE);
                        }

                    } else {

                        full_screen.setText("恢复");
                        isFullScreen = true;
                        findViewById(R.id.linearLayout).setVisibility(View.GONE);
                        mAudienceRecyclerView.setVisibility(View.GONE);
                        mVideoAdapter.setVisibility(View.GONE);
                    }
                } else {
                    pptMode++;
                    if (pptMode >= 3) {
                        pptMode = 0;
                    }
                    if (pptMode == 0) {
                        full_screen.setText("非全屏");
                        notFullScreenState();
                    } else if (pptMode == 1) {
                        full_screen.setText("全屏");
                        FullScreenState();
                    } else if (pptMode == 2) {
                        full_screen.setText("隐藏浮窗");
                        clearAllState();
                    }
                }

            }
        });
        full_screen.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    if (mVideoAdapter == null || mVideoAdapter.getItemCount() <= 0) {
                        return true;
                    } else {
                        setAudienceVideoRequestFocus();
                    }
                }
                return false;
            }
        });

        showToolBarsHandler.sendEmptyMessageDelayed(0, 5000);
        mCurrentNetSpeed = findViewById(R.id.currentSpeed);
        if (BuildConfig.DEBUG) {
            mCurrentNetSpeed.setVisibility(View.GONE);
        } else {
            mCurrentNetSpeed.setVisibility(View.GONE);
        }

        img_line_state = findViewById(R.id.img_line_state);

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

    private boolean handsUp = false;

    private void setTextViewDrawableTop(TextView view, int drawable) {
        Drawable top = getResources().getDrawable(drawable);
        view.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
    }

    @Override
    protected void initUIandEvent() {
        event().addEventHandler(this);

        Intent intent = getIntent();
        agora = intent.getParcelableExtra("agora");
        meetingJoin = intent.getParcelableExtra("meeting");
        ZYAgent.onEvent(getApplicationContext(), "meetingId=" + meetingJoin.getMeeting().getId());
        meeting = meetingJoin.getMeeting();

        broadcastId = meetingJoin.getHostUser().getClientUid();

        channelName = meeting.getId();

        config().mUid = Integer.parseInt(UIDUtil.generatorUID(Preferences.getUserId()));

        Preferences.saveAgroUid(String.valueOf(config().mUid));
        audienceName = Preferences.getUserName();

        if ("true".equals(agora.getIsTest())) {
            worker().joinChannel(null, channelName, config().mUid);
        } else {
            worker().joinChannel(agora.getToken(), channelName, config().mUid);
        }

        broadcasterLayout = findViewById(R.id.broadcaster_view);
        broadcastTipsText = findViewById(R.id.broadcast_tips);
        broadcastNameText = findViewById(R.id.broadcaster);
        broadcastNameText.setText("主持人：" + meetingJoin.getHostUser().getHostUserName());

        audienceLayout = findViewById(R.id.audience_view);
        audienceTipsText = findViewById(R.id.audience_tips);
        audienceNameText = findViewById(R.id.audience_name);

        broadcastSmailLayout = findViewById(R.id.broadcaster_smail_view);
        docImage = findViewById(R.id.doc_image_view);
        pageText = findViewById(R.id.page);
        onlineCountText = findViewById(R.id.online_count);
        roleTagText = findViewById(R.id.role_tag);
        forum_item_more_audience_msg = findViewById(R.id.forum_item_more_audience_msg);
        micButton = findViewById(R.id.waiter);
        finishButton = findViewById(R.id.finish);
        finishButton.setOnClickListener(view -> {
            showDialog(2, "确定终止发言吗？", "取消", "确定", null);
        });
        finishButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    if (mVideoAdapter == null || mVideoAdapter.getItemCount() <= 0) {
                        return true;
                    } else {
                        setAudienceVideoRequestFocus();
                    }
                }
                return false;
            }
        });

        mNetWorkNumber = findViewById(R.id.netWorkNumber);
        mNetworkIcon = findViewById(R.id.networkIcon);
        mMuteAudio = findViewById(R.id.mute_audio);
        mSwtichCamera = findViewById(R.id.switch_camera);
        if (!Constant.isPadApplicaion) {
            mSwtichCamera.setVisibility(View.GONE);
        }
        mSwtichCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rtcEngine().switchCamera();
            }
        });
        mMuteAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStopSpeak) {
                    ToastUtils.showToast("你已经被主持人禁言");
                    return;
                }
                if (!isMuted) {
                    isMuted = true;
                    setTextViewDrawableTop(mMuteAudio, R.drawable.icon_speek_no_select);
                    mMuteAudio.setText("话筒关闭");
                } else {
                    isMuted = false;
                    setTextViewDrawableTop(mMuteAudio, R.drawable.icon_speek_select);
                    mMuteAudio.setText("话筒打开");
                }
                rtcEngine().muteLocalAudioStream(isMuted);
            }
        });

        mMuteAudio.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    if (mVideoAdapter == null || mVideoAdapter.getItemCount() <= 0) {
                        return true;
                    } else {
                        setAudienceVideoRequestFocus();
                    }
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mChatFragment != null && mChatFragment.getView().getVisibility() != View.VISIBLE) {
                        findViewById(R.id.close_discuss).setFocusable(true);
                        findViewById(R.id.close_discuss).requestFocus();
                    }
                }

                return false;
            }
        });

        findViewById(R.id.close_discuss).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    mChatFragment.getConversationAdaterEx().setCanFocused(true);
//					listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN && mChatFragment.getView().getVisibility() != View.VISIBLE) {
                    com.orhanobut.logger.Logger.e("keyCode--->" + keyCode + "Constant.videoType:" + Constant.videoType + "   mCallingAudience:" + mCallingAudience);
                    if (Constant.videoType == 2 && !mCallingAudience) {
                        micButton.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });


        micButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                com.orhanobut.logger.Logger.e("micButton  hasFocus:" + hasFocus);
            }
        });

        mMuteAudio.setOnFocusChangeListener((v, hasFocus) -> {
            Log.e(TAG, "音频按钮: " + isMuted);
            if (hasFocus) {
                if (isMuted) {
                    setTextViewDrawableTop(mMuteAudio, R.drawable.icon_speek_no_select);
                    mMuteAudio.setText("话筒关闭");
//					muteAudioButton.setImageResource(R.drawable.ic_muted);//静音
//					muteAudioButton.setBackground(getResources().getDrawable(R.drawable.bg_meeting_mute_selector));
                } else {
                    setTextViewDrawableTop(mMuteAudio, R.drawable.icon_speek_select);
                    mMuteAudio.setText("话筒打开");
                }
            } else {
                if (isMuted) {
                    setTextViewDrawableTop(mMuteAudio, R.drawable.icon_speek_no_new);
                    mMuteAudio.setText("话筒关闭");
//					muteAudioButton.setBackground(getResources().getDrawable(R.drawable.bg_meeting_mute_unfocus_selector));
                } else {
                    setTextViewDrawableTop(mMuteAudio, R.drawable.icon_speek);
                    mMuteAudio.setText("话筒打开");
                }
            }
        });


        micButton.setOnClickListener(view -> {
            if (remoteBroadcastSurfaceView != null) {
                if (CameraHelper.getNumberOfCameras() > 0) {
                    if (handsUp) {
                        handsUp = false;
                        micButton.setText("申请发言");
//						micButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_meeting_signup, 0, 0, 0);
                    } else {
                        handsUp = true;
                        micButton.setText("取消申请");
//						micButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_meeting_signup_giveup, 0, 0, 0);
                    }
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("uid", config().mUid);
                        jsonObject.put("uname", audienceName);
                        jsonObject.put("handsUp", handsUp);
                        jsonObject.put("callStatus", 0);
                        jsonObject.put("isAudience", true);
                        jsonObject.put("auditStatus", Preferences.getUserAuditStatus());
                        jsonObject.put("postTypeName", Preferences.getUserPostTypeName());
                        agoraAPI.messageInstantSend(broadcastId, 0, jsonObject.toString(), "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "未检测到摄像头，请先连接摄像头", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MeetingAudienceActivity.this, "请先等待主持人加入", Toast.LENGTH_SHORT).show();
            }
        });

        micButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                com.orhanobut.logger.Logger.e("keyCode----->" + keyCode);
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    if (mVideoAdapter == null || mVideoAdapter.getItemCount() <= 0) {
                        return true;
                    } else {
                        setAudienceVideoRequestFocus();
                    }
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (Constant.videoType == 2 && mChatFragment != null && mChatFragment.getView().getVisibility() != View.VISIBLE) {
                        findViewById(R.id.close_discuss).setFocusable(true);
                        findViewById(R.id.close_discuss).requestFocus();
                    }
                }
                return false;
            }
        });

        exitButton = findViewById(R.id.exit);
        exitButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    if (mVideoAdapter == null || mVideoAdapter.getItemCount() <= 0) {
                        return true;
                    } else {
                        setAudienceVideoRequestFocus();
                    }
                }
                return false;
            }
        });
        exitButton.setOnClickListener(view -> {
            showDialog(1, "确定退出吗？", "取消", "确定", null);
        });

        constraintLayout_forum_audience_newmsg = findViewById(R.id.constraintLayout_forum_audience_newmsg);
        textswitcher_forum_item_audience_notification = findViewById(R.id.textswitcher_forum_item_audience_notification);

        forum_item_more_audience_msg_img = findViewById(R.id.forum_item_more_audience_msg_img);
        forum_item_audience_more = findViewById(R.id.forum_item_audience_more);
        forum_item_audience_more.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    forum_item_more_audience_msg.setTextColor(Color.WHITE);
                    forum_item_more_audience_msg_img.setImageResource(R.drawable.ic_forum_more_msg);
                } else {
                    forum_item_more_audience_msg.setTextColor(getResources().getColor(R.color.c_FF909090));
                    forum_item_more_audience_msg_img.setImageResource(R.drawable.ic_forum_more_msg_unfocus);
                }
            }
        });
        forum_item_audience_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forumIntent = new Intent(MeetingAudienceActivity.this, ForumActivity.class);
                forumIntent.putExtra(ForumActivity.INTENT_KEY_FORUM, meeting.getId());
                startActivity(forumIntent);
            }
        });


        agoraAPI = AgoraAPIOnlySignal.getInstance(this, agora.getAppID());
        agoraAPI.setAttr("userName", Preferences.getUserName());
        agoraAPI.setAttr("uname", Preferences.getUserName());
        agoraAPI.callbackSet(new AgoraAPI.CallBack() {

            @Override
            public void onLoginSuccess(int uid, int fd) {
                super.onLoginSuccess(uid, fd);

                agoraAPI.channelJoin(channelName);
                agoraAPI.setAttr("userName", Preferences.getUserName()); // 设置正在连麦的用户名
                agoraAPI.setAttr("uname", Preferences.getUserName());
                agoraAPI.queryUserStatus(String.valueOf(uid));
            }

            @Override
            public void onLoginFailed(final int ecode) {
                super.onLoginFailed(ecode);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, "观众登陆信令系统失败" + ecode, Toast.LENGTH_SHORT).show());
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
                        Toast.makeText(MeetingAudienceActivity.this, "退出信令频道成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onChannelJoined(String channelID) {
                super.onChannelJoined(channelID);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(MeetingAudienceActivity.this, "观众登陆信令频道成功", Toast.LENGTH_SHORT).show();
                    }
                    if (agoraAPI.getStatus() == 2) {
                        agoraAPI.channelQueryUserNum(channelName);
                        agoraAPI.queryUserStatus(broadcastId);
                    }
                });
            }

            @Override
            public void onReconnecting(int nretry) {
                super.onReconnecting(nretry);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, "信令重连失败第" + nretry + "次", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onReconnected(int fd) {
                super.onReconnected(fd);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, "信令系统重连成功", Toast.LENGTH_SHORT).show());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        agoraAPI.channelJoin(channelName);
                    }
                });
            }

            @Override
            public void onQueryUserStatusResult(String name, String status) {
                super.onQueryUserStatusResult(name, status);
                if (name.equals(broadcastId) && "1".equals(status)) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("uid", config().mUid);
                        jsonObject.put("uname", audienceName);
                        jsonObject.put("handsUp", handsUp);
                        if (Constant.videoType == 1) {
                            jsonObject.put("callStatus", 2);
                            jsonObject.put("isAudience", false);
                        } else {
                            if (currentAudienceId == config().mUid) {
                                jsonObject.put("callStatus", 2);
                                jsonObject.put("isAudience", true);
                            } else {
                                jsonObject.put("callStatus", 0);
                                jsonObject.put("isAudience", true);
                            }

                        }
                        jsonObject.put("auditStatus", Preferences.getUserAuditStatus());
                        jsonObject.put("postTypeName", Preferences.getUserPostTypeName());
                        agoraAPI.messageInstantSend(name, 0, jsonObject.toString(), "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onChannelJoinFailed(String channelID, int ecode) {
                super.onChannelJoinFailed(channelID, ecode);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, "观众登陆信令频道失败" + ecode, Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onChannelQueryUserNumResult(String channelID, int ecode, final int num) {
                super.onChannelQueryUserNumResult(channelID, ecode, num);
                runOnUiThread(() -> onlineCountText.setText("在线人数：" + num));
            }

            @Override
            public void onChannelUserJoined(String account, int uid) {
                super.onChannelUserJoined(account, uid);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(MeetingAudienceActivity.this, "用户" + account + "进入信令频道", Toast.LENGTH_SHORT).show();
                    }
                    if (agoraAPI.getStatus() == 2) {
                        agoraAPI.channelQueryUserNum(channelName);
                    }
                    agoraAPI.getUserAttr(account, "userName");//会走回调onUserAttrResult
                    agoraAPI.getUserAttr(account, "uname");//会走回调onUserAttrResult
                });

            }

            @Override
            public void onChannelUserLeaved(String account, int uid) {
                super.onChannelUserLeaved(account, uid);
                mLogger.e("用户" + account + "退出信令频道");
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(MeetingAudienceActivity.this, "用户" + account + "退出信令频道", Toast.LENGTH_SHORT).show();
                    }
				/*	if (account.equals(broadcastId)) {
						roleTagText.setVisibility(View.GONE);

						pageText.setVisibility(View.GONE);
						docImage.setVisibility(View.GONE);

						currentMaterial = null;
						broadcastSmailLayout.removeAllViews();

						broadcastSmailLayout.setVisibility(View.GONE);

						if (broadcasterLayout.getChildCount() > 0) {

						} else {
							broadcasterLayout.setVisibility(View.VISIBLE);
							stripSurfaceView(remoteBroadcastSurfaceView);
							broadcasterLayout.removeAllViews();
							if (remoteBroadcastSurfaceView != null) {
								broadcasterLayout.addView(remoteBroadcastSurfaceView);
							}
						}
						onUserOffline(Integer.parseInt(account), Constants.USER_OFFLINE_QUIT);

					}
*/
                    if (agoraAPI.getStatus() == 2) {
                        agoraAPI.channelQueryUserNum(channelName);
                    }
                });
            }

            @Override
            public void onUserAttrResult(final String account, final String name, final String value) {
                mLogger.e("account==%s,name==%s,value==%s", account, name, value);
                super.onUserAttrResult(account, name, value);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(MeetingAudienceActivity.this, "onUserAttrResult 获取正在连麦用户" + account + "的属性" + name + "的值为" + value, Toast.LENGTH_SHORT).show();
                    }

				/*	if ("uname".equals(name)) {
						if (TextUtils.isEmpty(value)) {
							audienceNameText.setText("");
							audienceTipsText.setVisibility(View.VISIBLE);
						} else {
							audienceNameText.setText(value);
							audienceTipsText.setVisibility(View.GONE);
						}
					}*/

                    try {
                        if (name.equals("userName") || name.equals("uname")) {
                            if (TextUtils.isEmpty(value)) {
                                return;
                            }
                            int position = mVideoAdapter.getPositionById(Integer.parseInt(account));
                            if (position != -1 && position < mVideoAdapter.getAudienceVideoLists().size()) {
                                AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(position);
                                if (audienceVideo != null) {
                                    audienceVideo.setName(value);
                                    audienceVideo.setUname(value);
                                    mVideoAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                });
            }

            @Override
            public void onMessageSendSuccess(String messageID) {
                super.onMessageSendSuccess(messageID);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, messageID + "发送成功", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onMessageSendError(String messageID, int ecode) {
                super.onMessageSendError(messageID, ecode);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, messageID + "发送失败", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onMessageInstantReceive(final String account, final int uid, final String msg) {
                super.onMessageInstantReceive(account, uid, msg);
                mLogger.e("account:==" + account + "uid:==" + uid + "msg:==" + msg);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, "onMessageInstantReceive 收到主持人" + account + "发来的消息" + msg, Toast.LENGTH_SHORT).show());
                }
                runOnUiThread(() -> {
                    try {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MeetingAudienceActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                        JSONObject jsonObject = new JSONObject(msg);
                        if (jsonObject.has("response")) {
                            boolean result = jsonObject.getBoolean("response");
                            if (result) {
                                if (BuildConfig.DEBUG) {
                                    Toast.makeText(MeetingAudienceActivity.this, "接受连麦", Toast.LENGTH_SHORT).show();
                                }

                                agoraAPI.setAttr("userName", Preferences.getUserName());
                                agoraAPI.setAttr("uname", Preferences.getUserName());

                                /**TextureView*/
                                localAudienceSurfaceView = RtcEngine.CreateTextureView(getApplicationContext());
//                                localAudienceSurfaceView.setZOrderOnTop(true);
//                                localAudienceSurfaceView.setZOrderMediaOverlay(true);
                                rtcEngine().setupLocalVideo(new VideoCanvas(localAudienceSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, config().mUid));

                                audienceNameText.setText(audienceName);
                                audienceTipsText.setVisibility(View.GONE);


                                finishButton.setVisibility(View.VISIBLE);
                                micButton.setVisibility(View.GONE);

                                if (Constant.isPadApplicaion) {
                                    mSwtichCamera.setVisibility(View.VISIBLE);
                                } else {
                                    mSwtichCamera.setVisibility(View.GONE);
                                }
                                mMuteAudio.setVisibility(View.VISIBLE);


                                worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);

                                HashMap<String, Object> params = new HashMap<String, Object>();
                                params.put("status", 1);
                                params.put("meetingId", meetingJoin.getMeeting().getId());
                                ApiClient.getInstance().meetingHostStats(TAG, meetingHostJoinTraceCallback, params);
                            } else {
                                if (BuildConfig.DEBUG) {
                                    Toast.makeText(MeetingAudienceActivity.this, "拒绝连麦", Toast.LENGTH_SHORT).show();
                                }
                                finishButton.setVisibility(View.GONE);
                                micButton.setVisibility(View.VISIBLE);
                                audienceTipsText.setVisibility(View.VISIBLE);
                            }
                            handsUp = false;
                            micButton.setText("申请发言");
//							micButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_meeting_signup, 0, 0, 0);
                        }
                        if (jsonObject.has("finish")) {
                            boolean finish = jsonObject.getBoolean("finish");
                            if (finish) {
                                audienceLayout.removeAllViews();
                                audienceNameText.setText("");
                                audienceTipsText.setVisibility(View.VISIBLE);
                                if (Constant.videoType == 2) {
                                    localAudienceSurfaceView = null;
                                    micButton.setVisibility(View.VISIBLE);
                                } else {
                                    micButton.setVisibility(View.GONE);
                                }

                                finishButton.setVisibility(View.GONE);

                                if (!isHostCommeIn) {
                                    //主持人离开了
                                    if (mVideoAdapter.isHaveChairMan()) {
                                        int chairManPosition = mVideoAdapter.getChairManPosition();
                                        if (chairManPosition != -1) {
                                            mVideoAdapter.removeItem(chairManPosition);
                                        }
                                    } else {
                                        if (currentAudienceId != 0) {
                                            mVideoAdapter.deleteItemById(currentAudienceId);
                                        }

                                    }

                                    broadcasterLayout.removeAllViews();
                                    broadcasterLayout.setVisibility(View.GONE);
                                    broadcastTipsText.setVisibility(View.VISIBLE);

                                    mLogger.e("mVideoAdapter.getDataSize():-->" + mVideoAdapter.getDataSize());
                                    if (mVideoAdapter.getDataSize() > 0) {
                                        mAudienceRecyclerView.setVisibility(View.VISIBLE);
                                    } else {
                                        mAudienceRecyclerView.setVisibility(View.GONE);
                                    }
                                }

                                if (Constant.videoType == 2) {
                                    handsUp = false;
                                    micButton.setText("申请发言");
                                    worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
                                } else {
                                    worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
                                }

//								agoraAPI.setAttr("uname", null);


//								micButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_meeting_signup, 0, 0, 0);


                                if (!TextUtils.isEmpty(meetingHostJoinTraceId)) {
                                    HashMap<String, Object> params = new HashMap<String, Object>();
                                    params.put("meetingHostJoinTraceId", meetingHostJoinTraceId);
                                    params.put("status", 2);
                                    params.put("meetingId", meetingJoin.getMeeting().getId());
                                    params.put("type", 2);
                                    params.put("leaveType", 1);
                                    ApiClient.getInstance().meetingHostStats(TAG, meetingHostJoinTraceCallback, params);
                                }
                            }
                        }

                        if (jsonObject.has(Constant.KEY_MUTE_AUDI)) {
                            boolean result = jsonObject.getBoolean(Constant.KEY_MUTE_AUDI);
                            if (result) {
                                isStopSpeak = true;
                                setTextViewDrawableTop(mMuteAudio, R.drawable.icon_speek_no_select);
                                mMuteAudio.setText("话筒关闭");
                            } else {
                                isStopSpeak = false;
                                setTextViewDrawableTop(mMuteAudio, R.drawable.icon_speek_select);
                                mMuteAudio.setText("话筒打开");
                            }
                            rtcEngine().muteLocalAudioStream(result);
                        }
                        if (jsonObject.has(Constant.KEY_REMOVE_USER)) {
                            boolean result = jsonObject.getBoolean(Constant.KEY_REMOVE_USER);
                            if (result) {
                                exit();
                                ToastUtils.showToast("你已被主持人移除");
                            }
                        }

						/*if (jsonObject.has("getInformation")) {
							JSONObject json = new JSONObject();
							String audienceName = (TextUtils.isEmpty(Preferences.getAreaName()) ? "" : Preferences.getAreaName()) + "-" + (TextUtils.isEmpty(Preferences.getCustomName()) ? "" : Preferences.getCustomName()) + "-" + Preferences.getUserName();
							json.put("uid", config().mUid);
							json.put("uname", audienceName);
							json.put("callStatus", 2);
							jsonObject.put("isAudience", true);
							json.put("returnInformation", 1);
							json.put("auditStatus", Preferences.getUserAuditStatus());
							json.put("postTypeName", Preferences.getUserPostTypeName());
							agoraAPI.messageInstantSend(meetingJoin.getHostUser().getClientUid(), 0, json.toString(), "");
						}*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onMessageChannelReceive(String channelID, String account, int uid, final String msg) {
                mLogger.e("account==%s,uid==%d,msg==%s", account, uid, msg);
                super.onMessageChannelReceive(channelID, account, uid, msg);
                runOnUiThread(() -> {
                    try {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MeetingAudienceActivity.this, "onMessageChannelReceive 收到" + account + "发的频道消息" + msg, Toast.LENGTH_SHORT).show();
                        }
                        JSONObject jsonObject = new JSONObject(msg);
                        if (jsonObject.has("material_id") && jsonObject.has("doc_index")) {
                            doc_index = jsonObject.getInt("doc_index");
                            String materialId = jsonObject.getString("material_id");

                            if (currentMaterial != null) {
                                if (!materialId.equals(currentMaterial.getId())) {
                                    ApiClient.getInstance().meetingMaterial(TAG, meetingMaterialCallback, materialId);
                                } else {
                                    if (remoteBroadcastSurfaceView != null) {
                                        broadcasterLayout.removeView(remoteBroadcastSurfaceView);
                                        broadcasterLayout.setVisibility(View.GONE);
                                    }
                                    MeetingMaterialsPublish currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(doc_index);

                                    if (currentMaterialPublish.getType().equals("1")) {
                                        PlayVideo();
                                    } else {
                                        stopPlayVideo();
                                        findViewById(R.id.app_video_box).setVisibility(View.GONE);
                                        mVideoContainer.setVisibility(View.GONE);
                                        docImage.setVisibility(View.VISIBLE);
                                        Picasso.with(MeetingAudienceActivity.this).load(currentMaterialPublish.getUrl()).into(docImage);
                                    }
                                    pageText.setVisibility(View.VISIBLE);
                                    pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");

                                }
                            } else {
                                ApiClient.getInstance().meetingMaterial(TAG, meetingMaterialCallback, materialId);
                            }
                        }
                        if (jsonObject.has("finish_meeting")) {
                            boolean finishMeeting = jsonObject.getBoolean("finish_meeting");
                            if (finishMeeting) {
                                if (BuildConfig.DEBUG) {
                                    Toast.makeText(MeetingAudienceActivity.this, "主持人离开了", Toast.LENGTH_SHORT).show();
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
                mLogger.e("channelID:" + channelID + ", name:" + name + ", value:" + value + ", type:" + type);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(MeetingAudienceActivity.this, "onChannelAttrUpdated:" + "\nname:" + name + ", \nvalue:" + value + ", \ntype:" + type, Toast.LENGTH_SHORT).show();
                    }
                    if (CALLING_AUDIENCE.equals(name)) {
                        if (TextUtils.isEmpty(value)) { // 取消连麦
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(MeetingAudienceActivity.this, "收到主持人结束连麦", Toast.LENGTH_SHORT).show();
                            }
                            com.orhanobut.logger.Logger.e("收到主持人结束连麦");
                            mCallingAudience = false;
                            if (remoteAudienceSurfaceView != null) {
                                remoteAudienceSurfaceView = null;
                            }
                            if (remoteBroadcastSurfaceView == null) {
                                broadcastTipsText.setVisibility(View.VISIBLE);

                                return;
                            }
                            if (localAudienceSurfaceView != null) {
//								localAudienceSurfaceView = null;


//								agoraAPI.setAttr("uname", null);

                                if (!TextUtils.isEmpty(meetingHostJoinTraceId)) {
                                    HashMap<String, Object> params = new HashMap<String, Object>();
                                    params.put("meetingHostJoinTraceId", meetingHostJoinTraceId);
                                    params.put("status", 2);
                                    params.put("meetingId", meetingJoin.getMeeting().getId());
                                    params.put("type", 2);
                                    params.put("leaveType", 1);
                                    ApiClient.getInstance().meetingHostStats(TAG, meetingHostJoinTraceCallback, params);
                                }
                            }

                            if (Constant.videoType == 2) {
                                handsUp = false;
                                isMuted = true;
                                micButton.setText("申请发言");
                                micButton.setVisibility(View.VISIBLE);
                                worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
                                mMuteAudio.setVisibility(View.GONE);
                                mSwtichCamera.setVisibility(View.GONE);
                            } else {
                                worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
                                mMuteAudio.setVisibility(View.VISIBLE);
                                mSwtichCamera.setVisibility(View.GONE);
                                micButton.setVisibility(View.GONE);
                            }

                            audienceLayout.removeAllViews();
                            audienceNameText.setText("");
                            audienceTipsText.setVisibility(View.VISIBLE);
                            finishButton.setVisibility(View.GONE);

                            changeMyAudienceView();
                            currentAudienceId = -1;


                        } else { // 开始连麦
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(MeetingAudienceActivity.this, "收到主持人设置的连麦人ID：" + value + ", \ntype:" + type, Toast.LENGTH_SHORT).show();
                            }
                            mLogger.e("收到主持人设置的连麦人ID：=" + value);
                            currentAudienceId = Integer.parseInt(value);
                            if (currentAudienceId == config().mUid) { // 连麦人是我
                                mCallingAudience = true;
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("uid", config().mUid);
                                    jsonObject.put("uname", audienceName);
                                    jsonObject.put("handsUp", handsUp);
                                    if (Constant.videoType == 1) {
                                        jsonObject.put("callStatus", 2);
                                        jsonObject.put("isAudience", false);

                                    } else {
                                        if (currentAudienceId == config().mUid) {
                                            jsonObject.put("callStatus", 2);
                                            jsonObject.put("isAudience", true);
                                        } else {
                                            jsonObject.put("callStatus", 0);
                                            jsonObject.put("isAudience", true);
                                        }

                                    }

                                    jsonObject.put("auditStatus", Preferences.getUserAuditStatus());
                                    jsonObject.put("postTypeName", Preferences.getUserPostTypeName());
                                    agoraAPI.messageInstantSend(broadcastId, 0, jsonObject.toString(), "");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                mLogger.e("连麦人是我");


                                mMuteAudio.setVisibility(View.VISIBLE);
                                if (Constant.isPadApplicaion) {
                                    mSwtichCamera.setVisibility(View.VISIBLE);
                                } else {
                                    mSwtichCamera.setVisibility(View.GONE);
                                }
                                audienceNameText.setText(audienceName);
                                audienceTipsText.setVisibility(View.GONE);

                                finishButton.setVisibility(View.VISIBLE);
                                micButton.setVisibility(View.GONE);

                                if (mChatFragment != null) {
                                    mChatFragment.getEditText().setNextFocusRightId(R.id.mute_audio);
                                    mMuteAudio.setNextFocusLeftId(mChatFragment.getEditTextID());
                                }
//								agoraAPI.setAttr("uname", audienceName); // 设置正在连麦的用户名


                                audienceLayout.removeAllViews();
                                remoteAudienceSurfaceView = null;
                                localAudienceSurfaceView = null;
                                /**TextureView*/
                                localAudienceSurfaceView = RtcEngine.CreateTextureView(getApplicationContext());
//                                localAudienceSurfaceView.setZOrderOnTop(true);
//                                localAudienceSurfaceView.setZOrderMediaOverlay(true);
                                rtcEngine().setupLocalVideo(new VideoCanvas(localAudienceSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, config().mUid));


                                mLocalAudienceVideo = new AudienceVideo();
                                mLocalAudienceVideo.setUid(config().mUid);
                                mLocalAudienceVideo.setName("参会人" + config().mUid);
                                mLocalAudienceVideo.setBroadcaster(false);
                                stripSurfaceView(localAudienceSurfaceView);
                                /**TextureView*/
                                mLocalAudienceVideo.setTextureView(localAudienceSurfaceView);
                                mVideoAdapter.insertItem(mLocalAudienceVideo);


                                worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);

                                HashMap<String, Object> params = new HashMap<String, Object>();
                                params.put("status", 1);
                                params.put("meetingId", meetingJoin.getMeeting().getId());
                                ApiClient.getInstance().meetingHostStats(TAG, meetingHostJoinTraceCallback, params);

                                if (!isMuted) {
                                    mMuteAudio.setText("话筒打开");
                                    setTextViewDrawableTop(mMuteAudio, R.drawable.icon_speek);
                                } else {
                                    mMuteAudio.setText("话筒关闭");
                                    setTextViewDrawableTop(mMuteAudio, R.drawable.icon_speek_no);
                                }

                                int i = rtcEngine().muteLocalAudioStream(isMuted);
                                Log.e(TAG, "onChannelAttrUpdated: 话筒关闭:" + i + "isMuted:" + isMuted);

                                if (pptMode == 1) {
                                    FullScreenState();
                                }


                            } else {  // 连麦人不是我

                                mLogger.e("连麦人不是我");
                                if (Constant.videoType == 2) {
                                    micButton.setVisibility(View.VISIBLE);
                                    micButton.setText("申请发言");
                                    mMuteAudio.setVisibility(View.GONE);
                                    mSwtichCamera.setVisibility(View.GONE);
                                    finishButton.setVisibility(View.GONE);
                                    worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
                                    //参会人（我）此时在列表中
                                    int positionById = mVideoAdapter.getPositionById(config().mUid);
                                    if (positionById != -1) {
                                        AudienceVideo video = mVideoAdapter.getAudienceVideoLists().get(positionById);
                                        if (video != null && video.getSurfaceView() != null) {
                                            video.getSurfaceView().setZOrderMediaOverlay(false);
                                            video.getSurfaceView().setZOrderOnTop(false);
                                        }
                                        mVideoAdapter.deleteItemById(config().mUid);
                                    }


                                } else {
                                    micButton.setVisibility(View.GONE);
                                    finishButton.setVisibility(View.GONE);
                                    worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);

                                }

                                if (mVideoAdapter.isHaveChairMan()) {
                                    int chairManPosition = mVideoAdapter.getChairManPosition();
                                    if (chairManPosition != -1) {
                                        mVideoAdapter.removeItem(chairManPosition);
                                        if (remoteBroadcastSurfaceView != null) {
                                            /**TextureView*/
//                                            remoteBroadcastSurfaceView.setZOrderMediaOverlay(false);
//                                            remoteBroadcastSurfaceView.setZOrderOnTop(false);

                                        }
                                        broadcasterLayout.removeAllViews();
                                        stripSurfaceView(remoteBroadcastSurfaceView);
                                        if (remoteBroadcastSurfaceView != null) {
                                            broadcasterLayout.addView(remoteBroadcastSurfaceView);
                                        }
                                        broadcasterLayout.setVisibility(View.VISIBLE);

                                    }
                                }

                                if (mCurrentAudienceVideo != null) {
                                    mVideoAdapter.insertItem(mCurrentAudienceVideo);
                                    mCurrentAudienceVideo = null;
                                }



								/*if (localAudienceSurfaceView != null) {
									localAudienceSurfaceView = null;
								}*/

//								agoraAPI.setAttr("uname", null);


                                finishButton.setVisibility(View.GONE);


                                if (!TextUtils.isEmpty(meetingHostJoinTraceId)) {
                                    HashMap<String, Object> params = new HashMap<String, Object>();
                                    params.put("meetingHostJoinTraceId", meetingHostJoinTraceId);
                                    params.put("status", 2);
                                    params.put("meetingId", meetingJoin.getMeeting().getId());
                                    params.put("type", 2);
                                    params.put("leaveType", 1);
                                    ApiClient.getInstance().meetingHostStats(TAG, meetingHostJoinTraceCallback, params);
                                }
                            }

                            mLogger.e(isSplitView ? "当前是分屏模式" : "当前是大屏模式");
                            if (isSplitView) {
                                SpliteViews(4);
                            }
                            if (pptMode == 1 || pptMode == 2 || isFullScreen) {
                                mVideoAdapter.setVisibility(View.GONE);
                                mAudienceRecyclerView.setVisibility(View.GONE);
                                if (pptMode == 1 & localAudienceSurfaceView != null) {
                                    FullScreenState();
                                }
                            } else {
                                mVideoAdapter.setVisibility(View.VISIBLE);
                                mAudienceRecyclerView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    if (DOC_INFO.equals(name)) {
                        if (!TextUtils.isEmpty(value)) {
                            mLogger.e("当前是ppt模式---");
                            pptMode = 0;
                            isPPTModel = true;
                            exitSpliteMode();
                            try {
                                JSONObject jsonObject = new JSONObject(value);
                                if (jsonObject.has("material_id") && jsonObject.has("doc_index")) {
                                    doc_index = jsonObject.getInt("doc_index");
                                    if (BuildConfig.DEBUG) {
                                        Toast.makeText(MeetingAudienceActivity.this, "收到主持人端index：" + doc_index, Toast.LENGTH_SHORT).show();
                                    }
                                    String materialId = jsonObject.getString("material_id");
                                    if (currentMaterial != null) {
                                        mLogger.e("currentMaterial!=null");
                                        if (!materialId.equals(currentMaterial.getId())) {
                                            mLogger.e("!materialId.equals(currentMaterial.getId())");
                                            ApiClient.getInstance().meetingMaterial(TAG, meetingMaterialCallback, materialId);
                                        } else {
                                            mLogger.e("!materialId.equals(currentMaterial.getId())");
                                            if (remoteBroadcastSurfaceView != null) {
                                                broadcasterLayout.removeView(remoteBroadcastSurfaceView);
                                                broadcasterLayout.setVisibility(View.GONE);
                                            }

                                            MeetingMaterialsPublish currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(doc_index);

                                            if (currentMaterialPublish.getType().equals("1")) {
                                                PlayVideo();
                                            } else {
                                                stopPlayVideo();
                                                findViewById(R.id.app_video_box).setVisibility(View.GONE);
                                                mVideoContainer.setVisibility(View.GONE);
                                                docImage.setVisibility(View.VISIBLE);
                                                Picasso.with(MeetingAudienceActivity.this).load(currentMaterialPublish.getUrl()).into(docImage);
                                            }
                                            pageText.setVisibility(View.VISIBLE);
                                            pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");


                                            mLogger.e("pptMode=   " + pptMode);

                                            if (pptMode == 1 || pptMode == 2) {
                                                mAudienceRecyclerView.setVisibility(View.GONE);
                                                mVideoAdapter.setVisibility(View.GONE);
                                            } else {
                                                mAudienceRecyclerView.setVisibility(View.VISIBLE);
                                                mVideoAdapter.setVisibility(View.VISIBLE);
                                            }
                                            if (pptMode == 0) {
                                                notFullScreenState();
                                            } else if (pptMode == 1) {
                                                FullScreenState();
                                            } else if (pptMode == 2) {
                                                clearAllState();
                                            }


                                        }
                                    } else {
                                        ApiClient.getInstance().meetingMaterial(TAG, meetingMaterialCallback, materialId);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            mLogger.e("当前是取消ppt模式");
                            pptMode = -1;
                            roleTagText.setVisibility(View.GONE);
                            findViewById(R.id.app_video_box).setVisibility(View.GONE);
                            stopPlayVideo();
                            mVideoContainer.setVisibility(View.GONE);


                            pageText.setVisibility(View.GONE);
                            docImage.setVisibility(View.GONE);
                            isPPTModel = false;
                            currentMaterial = null;


                            broadcastSmailLayout.removeAllViews();
                            if (mCurrentAudienceVideo != null) {
                                //把当前放大的view放到列表中
                                mCurrentAudienceVideo.getSurfaceView().setZOrderMediaOverlay(true);
                                mCurrentAudienceVideo.getSurfaceView().setZOrderOnTop(true);
                                mVideoAdapter.insertItem(mVideoAdapter.getDataSize(), mCurrentAudienceVideo);
                                mCurrentAudienceVideo = null;
                                //主持人移动到大的视图
                                if (mVideoAdapter.isHaveChairMan()) {
                                    if (mVideoAdapter.getChairManPosition() != -1) {
                                        int chairManPosition = mVideoAdapter.getChairManPosition();
                                        AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(chairManPosition);
                                        if (audienceVideo != null && audienceVideo.getSurfaceView() != null) {
                                            audienceVideo.getSurfaceView().setVisibility(View.GONE);
                                        }
                                        mVideoAdapter.removeItem(chairManPosition);
                                    }
                                }
                                if (remoteAudienceSurfaceView != null) {
                                    /**TextureView*/
//                                    remoteBroadcastSurfaceView.setZOrderOnTop(false);
//                                    remoteBroadcastSurfaceView.setZOrderMediaOverlay(false);

                                    remoteBroadcastSurfaceView.setVisibility(View.VISIBLE);
                                }

                                broadcasterLayout.setVisibility(View.VISIBLE);
                                stripSurfaceView(remoteBroadcastSurfaceView);
                                if (remoteBroadcastSurfaceView != null) {
                                    broadcasterLayout.addView(remoteBroadcastSurfaceView);
                                }

                            } else {
                                if (isSplitView) {
                                    mLogger.e("当前是分屏模式---");
                                    SpliteViews(1);
                                } else {
                                    mLogger.e("当前不是分屏模式-----");
                                    exitSpliteMode();
                                }

                            }

                            if (isPPTModel) {
                                if (pptMode == 1) {
                                    mAudienceRecyclerView.setVisibility(View.VISIBLE);
                                    mVideoAdapter.setVisibility(View.VISIBLE);
                                } else {
                                    mAudienceRecyclerView.setVisibility(View.GONE);
                                    mVideoAdapter.setVisibility(View.GONE);
                                }
                            } else {
                                mAudienceRecyclerView.setVisibility(View.VISIBLE);
                                mVideoAdapter.setVisibility(View.VISIBLE);
                            }


                            if (isSplitView && !isPPTModel) {
                                full_screen.setVisibility(View.GONE);
                            } else {
                                full_screen.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    if (MODEL_CHANGE.equals(name)) {
                        if (currentMaterial == null) {
                            if (value.equals(EQUALLY)) {//均分

                                if (isHostCommeIn && mVideoAdapter.getDataSize() >= 1) {
                                    SpliteViews(2);
                                } else {
                                    return;
                                }


                                isSplitView = true;

                                mLogger.e("分屏模式");

                                mVideoAdapter.setVisibility(View.VISIBLE);
                                mAudienceRecyclerView.setVisibility(View.VISIBLE);
                                mVideoAdapter.setCanFocusable(false);
                                mVideoAdapter.notifyDataSetChanged();


                                full_screen.setVisibility(View.GONE);

                                if (!Constant.isPadApplicaion) {
                                    exitButton.requestFocus();
                                }


                            } else if (value.equals(BIGSCREEN)) {//大屏
                                mLogger.e("大屏模式");
                                isSplitView = false;
                                mVideoAdapter.setCanFocusable(true);
                                mVideoAdapter.notifyDataSetChanged();
                                if (isFullScreen) {
                                    mVideoAdapter.setVisibility(View.GONE);
                                    mAudienceRecyclerView.setVisibility(View.GONE);
                                } else {
                                    mVideoAdapter.setVisibility(View.VISIBLE);
                                    mAudienceRecyclerView.setVisibility(View.VISIBLE);
                                }
                                full_screen.setVisibility(View.VISIBLE);
                                exitSpliteMode();
                                if (!Constant.isPadApplicaion) {
                                    exitButton.requestFocus();
                                }
                            }
                        } else if (pptMode == 0) {
                            mAudienceRecyclerView.setVisibility(View.VISIBLE);
                            mVideoAdapter.setVisibility(View.VISIBLE);
                        } else {
                            mAudienceRecyclerView.setVisibility(View.GONE);
                            mVideoAdapter.setVisibility(View.GONE);
                        }

                    }

                    if (Constant.VIDEO.equals(name)) {
                        if (value.equals(Constant.PLAYVIDEO)) {
                            if (player != null) {
//								player.onStatePlaying();
                                findViewById(R.id.app_video_box).setVisibility(View.VISIBLE);
                                mVideoContainer.setVisibility(View.VISIBLE);
								/*if (player.state == Jzvd.STATE_PLAYING) {
									return;
								}*/
                                try {
                                    player.showWifiDialog();
                                } catch (Exception e) {
                                    findViewById(R.id.app_video_box).setVisibility(View.GONE);
                                    mVideoContainer.setVisibility(View.GONE);
                                    mLogger.e("出错了" + e.getMessage());
                                }
//								player.startButton.performClick();
                            } else {
                                PlayVideo();
								/*if (player != null) {
									player.onStatePlaying();
								}*/
                            }
                        } else if (value.equals(Constant.PAUSEVIDEO)) {
                            if (player != null) {
                                if (player.state == Jzvd.STATE_PAUSE) {
                                    return;
                                }
                                player.pauseVideo();
                            }
                        }
                    }


                    /**
                     * 取消所有
                     * */
                    if (type.equals("clear")) {
                        mLogger.e("当前是取消所有的");
                        pptMode = -1;

                        roleTagText.setVisibility(View.GONE);
                        pageText.setVisibility(View.GONE);
                        docImage.setVisibility(View.GONE);
                        currentMaterial = null;

                        findViewById(R.id.app_video_box).setVisibility(View.GONE);
                        mVideoContainer.setVisibility(View.GONE);
                        stopPlayVideo();

                        if (Constant.videoType == 2) {
                            mMuteAudio.setVisibility(View.GONE);
                            mSwtichCamera.setVisibility(View.GONE);
                            micButton.setText("申请发言");
                            isMuted = true;
                        } else if (Constant.videoType == 1) {
                            mMuteAudio.setVisibility(View.VISIBLE);
                            if (Constant.isPadApplicaion) {
                                mSwtichCamera.setVisibility(View.VISIBLE);
                            } else {
                                mSwtichCamera.setVisibility(View.GONE);
                            }
                        }


                        broadcastSmailLayout.removeAllViews();
                        broadcastSmailLayout.setVisibility(View.GONE);
                        broadcasterLayout.setVisibility(View.VISIBLE);

                        mLogger.e("currentAudienceId======================:" + currentAudienceId);

                        //判断观众是否在列表中  如果观众在列表中 就移除观众
                        if (mVideoAdapter.getPositionById(currentAudienceId) != -1) {
                            AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(mVideoAdapter.getPositionById(currentAudienceId));
                            if (audienceVideo != null && audienceVideo.getSurfaceView() != null) {
                                audienceVideo.getSurfaceView().setVisibility(View.GONE);
                                audienceVideo.getSurfaceView().setZOrderMediaOverlay(false);
                            }
                            mVideoAdapter.deleteItem(currentAudienceId);
                        } else if (mCurrentAudienceVideo != null && mCurrentAudienceVideo.getUid() == currentAudienceId) {
                            //判断观众是否在大的视图里面 如果在 就将列表中的主持人移动到大的视图
                            if (mVideoAdapter.getChairManPosition() != -1) {
                                AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(mVideoAdapter.getChairManPosition());
                                if (audienceVideo != null && audienceVideo.getSurfaceView() != null) {
                                    audienceVideo.getSurfaceView().setVisibility(View.GONE);
                                    audienceVideo.getSurfaceView().setZOrderMediaOverlay(false);
                                }
                                mVideoAdapter.removeItem(mVideoAdapter.getChairManPosition());
                            }
                        }
//						changeMyAudienceView();

                        if (remoteBroadcastSurfaceView != null) {
                            broadcasterLayout.removeAllViews();
                            stripSurfaceView(remoteBroadcastSurfaceView);
                            remoteBroadcastSurfaceView.setVisibility(View.VISIBLE);
                            broadcasterLayout.addView(remoteBroadcastSurfaceView);
                        } else {
                            broadcasterLayout.setVisibility(View.GONE);
                            broadcastTipsText.setVisibility(View.VISIBLE);
                        }


                        mLogger.e("onChannelAttrUpdated 集合大小是%d", mVideoAdapter.getDataSize());
                        if (mVideoAdapter.getDataSize() <= 0) {
                            mAudienceRecyclerView.setVisibility(View.GONE);
                            mVideoAdapter.setVisibility(View.GONE);
                        } else {
                            mAudienceRecyclerView.setVisibility(View.VISIBLE);
                            mVideoAdapter.setVisibility(View.VISIBLE);
                        }
                        currentAudienceId = -1;
                        if (isSplitView && !isPPTModel) {
                            full_screen.setVisibility(View.GONE);
                        } else {
                            full_screen.setVisibility(View.VISIBLE);
                        }

                    }

                    if (name.equals(Constant.KEY_ClOSE_MIC)) {
                        isMuted = true;
                        rtcEngine().muteLocalAudioStream(isMuted);
                        //新增 待测试
                        setTextViewDrawableTop(mMuteAudio, R.drawable.icon_speek_no_select);
                        mMuteAudio.setText("话筒关闭");
                    }
                });
            }

            @Override
            public void onError(final String name, final int ecode, final String desc) {
                super.onError(name, ecode, desc);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> {
                        if (ecode != 208)
                            Toast.makeText(MeetingAudienceActivity.this, "收到错误信息\nname: " + name + "\necode: " + ecode + "\ndesc: " + desc, Toast.LENGTH_SHORT).show();
                    });
                }
//                if (ecode == 1002) {
//                    return;
//                }
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

        initTextSwitcher();
        requestRecord();
        subscription = RxBus.handleMessage(o -> {
            if (o instanceof IMMessgeEvent) {
                IMMessgeEvent msg = (IMMessgeEvent) o;
                io.rong.imlib.model.Message imMessage = msg.getImMessage();
                StringBuilder stringBuilder = new StringBuilder();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            stringBuilder.append(imMessage.getContent().getUserInfo().getName()).append(":");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (imMessage.getContent() instanceof TextMessage) {
                            TextMessage msg = (TextMessage) imMessage.getContent();
                            String content = msg.getContent();
                            if (content.length() >= 8) {
                                content = content.substring(0, 8) + "...";
                            }
                            stringBuilder.append(content);
                        } else if (imMessage.getContent() instanceof ImageMessage) {
                            ImageMessage msg = (ImageMessage) imMessage.getContent();
                            stringBuilder.append("发送了一张图片");
                        } else if (imMessage.getContent() instanceof LocationMessage) {
                            LocationMessage msg = (LocationMessage) imMessage.getContent();
                            stringBuilder.append("发送了一个位置");
                        } else if (imMessage.getContent() instanceof FileMessage) {
                            LocationMessage msg = (LocationMessage) imMessage.getContent();
                            stringBuilder.append("发送了一个文件");
                        }
                        startTextSwitcherDataAnimation(stringBuilder.toString());
                    }
                });
            }
        });

        startMeetingCamera(meeting.getScreenshotFrequency());

        try {
            FragmentManager fragmentManage = getSupportFragmentManager();
            mChatFragment = (NoPluginTVConversationFragment) fragmentManage.findFragmentById(R.id.chatFragment);
            mChatFragment.setGroupID(channelName);
            if (Constant.videoType == 2) {
                micButton.setNextFocusLeftId(mChatFragment.getEditTextID());
                mChatFragment.getEditText().setNextFocusRightId(R.id.waiter);

            } else if (Constant.videoType == 1) {
                mMuteAudio.setNextFocusLeftId(mChatFragment.getEditTextID());
                mChatFragment.getEditText().setNextFocusRightId(R.id.mute_audio);

            }
            findViewById(R.id.close_discuss).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mChatFragment != null) {
                        if (mChatFragment.getView().getVisibility() == View.VISIBLE) {
                            mChatFragment.getView().setVisibility(View.GONE);
                        } else {
                            mChatFragment.getView().setVisibility(View.VISIBLE);
                        }
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private NoPluginTVConversationFragment mChatFragment;

    private void changeMyAudienceView() {
        if (currentAudienceId == config().mUid) {
            mLogger.e("当前是取消连麦 取消的连麦人是我");
            /*观众此时在列表中*/

            if (mChatFragment != null) {
                mChatFragment.getEditText().setNextFocusRightId(R.id.waiter);
            }

            if (mCurrentAudienceVideo != null && mCurrentAudienceVideo.getUid() == config().mUid) {
                mLogger.e(" 取消的连麦人是我  我的视频是被放大了的");
                mCurrentAudienceVideo = null;
                currentAudienceId = -1;
                if (mVideoAdapter.getChairManPosition() != -1 && !isPPTModel && !isSplitView) {
                    broadcasterLayout.setVisibility(View.VISIBLE);
                    if (remoteBroadcastSurfaceView != null) {
                        remoteBroadcastSurfaceView.setVisibility(View.VISIBLE);
                        /**TextureView*/
//                        remoteBroadcastSurfaceView.setZOrderOnTop(true);
//                        remoteBroadcastSurfaceView.setZOrderMediaOverlay(false);
                        broadcasterLayout.removeAllViews();
                        stripSurfaceView(remoteBroadcastSurfaceView);
                        broadcasterLayout.addView(remoteBroadcastSurfaceView);
                    }
                    mVideoAdapter.getAudienceVideoLists().remove(mVideoAdapter.getChairManPosition());
                    mVideoAdapter.notifyDataSetChanged();
                }
            } else {//放大的视频不是自己 或者没有放大的视频
                if (isPPTModel || isSplitView) {
                    return;
                }
                //自己肯定在列表中
                //判断主持人是否在列表中
                if (mVideoAdapter.getChairManPosition() != -1) {
                    broadcasterLayout.setVisibility(View.VISIBLE);
                    if (remoteBroadcastSurfaceView != null) {
                        remoteBroadcastSurfaceView.setVisibility(View.VISIBLE);
                        /**TextureView*/
//                        remoteBroadcastSurfaceView.setZOrderOnTop(true);
//                        remoteBroadcastSurfaceView.setZOrderMediaOverlay(false);
                        broadcasterLayout.removeAllViews();
                        stripSurfaceView(remoteBroadcastSurfaceView);
                        broadcasterLayout.addView(remoteBroadcastSurfaceView);
                    }
                    mVideoAdapter.getAudienceVideoLists().remove(mVideoAdapter.getChairManPosition());
                    mVideoAdapter.notifyDataSetChanged();
                }
                mVideoAdapter.deleteItemById(config().mUid);
                if (mCurrentAudienceVideo != null) {//此时有放大的视频
                    //将放大的视频加入到列表中
                    mVideoAdapter.insertItem(mCurrentAudienceVideo);
                }
            }

            if (mVideoAdapter.getPositionById(config().mUid) != -1) {
                mLogger.e("当前观众在列表中");
                mVideoAdapter.deleteItemById(currentAudienceId);

            } else {
                /*观众不再列表中 此时主持人在列表中*/
                mLogger.e("当前观众不在列表中");
                mCurrentAudienceVideo = null;
                currentAudienceId = -1;
                if (mVideoAdapter.isHaveChairMan()) {
                    mLogger.e("当前主持人在列表中");
                    if (isPPTModel) {
                        return;
                    }
                    int chairManPosition = mVideoAdapter.getChairManPosition();
                    if (chairManPosition != -1) {
                        stripSurfaceView(remoteBroadcastSurfaceView);
                        broadcasterLayout.removeAllViews();
                        if (remoteBroadcastSurfaceView != null) {
                            /**TextureView*/
//                            remoteBroadcastSurfaceView.setZOrderMediaOverlay(false);
//                            remoteBroadcastSurfaceView.setZOrderOnTop(false);
                            broadcasterLayout.addView(remoteBroadcastSurfaceView);
                        }
                        mVideoAdapter.deleteItem(chairManPosition);
                    }
                }
            }


        } else {
            mLogger.e("当前取消的连麦人不是我");
            if (mCurrentAudienceVideo != null) {
                if (mCurrentAudienceVideo.getUid() == currentAudienceId) {
                    if (mVideoAdapter.getChairManPosition() != -1 && !isPPTModel && !isSplitView) {
                        broadcasterLayout.setVisibility(View.VISIBLE);
                        if (remoteBroadcastSurfaceView != null) {
                            remoteBroadcastSurfaceView.setVisibility(View.VISIBLE);
                            /**TextureView*/
//                            remoteBroadcastSurfaceView.setZOrderOnTop(true);
//                            remoteBroadcastSurfaceView.setZOrderMediaOverlay(false);
                            broadcasterLayout.removeAllViews();
                            stripSurfaceView(remoteBroadcastSurfaceView);
                            broadcasterLayout.addView(remoteBroadcastSurfaceView);
                        }
                        mVideoAdapter.getAudienceVideoLists().remove(mVideoAdapter.getChairManPosition());
                        mVideoAdapter.notifyDataSetChanged();
                    }
                    mCurrentAudienceVideo = null;
                    currentAudienceId = -1;
                }
            }

        }
        currentAudienceId = -1;

        //不管取消谁 需要判断当前集合大小 如果集合小于等于1 就不再分屏
        if (mVideoAdapter.getDataSize() <= 1) {
            exitSpliteMode();

        } else if (isSplitView) {
            SpliteViews(8);
        }

    }

    private void startMeetingCamera(int screenshotFrequency) {
        if (screenshotFrequency == Meeting.SCREENSHOTFREQUENCY_INVALID) {
            //不抓拍
            return;
        }
        takePhotoTimer = new Timer();
        takePhotoTimerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(MeetingAudienceActivity.this, Camera1ByServiceActivity.class);
                intent.putExtra(Camera1ByServiceActivity.KEY_IMAGE_COMPRESSION_RATIO, meeting.getScreenshotCompressionRatio());
                startActivityForResult(intent, CODE_REQUEST_TAKEPHOTO);
            }
        };
        takePhotoTimer.schedule(takePhotoTimerTask, screenshotFrequency * 1000, screenshotFrequency * 1000);
    }

    private static class ForumItemMoreTextHandler extends Handler {
        private final WeakReference<Activity> mActivity;

        public ForumItemMoreTextHandler(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final Activity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case HANDLER_UPDATE_FORUM_TEXT:
                        if (msg.arg1 == HANDLER_UPDATE_FORUM_TEXT_NO) {
                            forum_item_more_audience_msg.setText("添加评论");
                        } else if (msg.arg1 == HANDLER_UPDATE_FORUM_TEXT_MORE) {
                            forum_item_more_audience_msg.setText("展开评论");
                        }
                        break;
                }
            }
        }
    }

    /**
     * socket接收讨论区通知，判断当前会议是否是socket接收的会议
     *
     * @param pageData
     * @return
     */
    private boolean isCurrentMeeting(PageData pageData) {
        return pageData.getMeetingId().equals(meeting.getId());
    }

    private void requestRecord() {
        this.requestRecord(null);
    }

    /**
     * socket接收讨论区新消息
     */
    private void requestRecord(PageData pageData) {
        if (pageData != null) {
            //非第一次进入直播室，走讨论区的socket请求
            showTextSwitcherNormalContent(pageData);
        } else {
            //第一次进入直播室，走讨论区的http请求
            Map<String, String> params = new HashMap<>();
            params.put(ApiClient.PAGE_NO, "1");
            params.put(ApiClient.PAGE_SIZE, "10");
            params.put("meetingId", meetingJoin.getMeeting().getId());
            ApiClient.getInstance().getForumContent(this, params, new OkHttpCallback<Bucket<ForumContent>>() {
                @Override
                public void onSuccess(Bucket<ForumContent> forumContentBucket) {
                    ZYAgent.onEvent(getApplicationContext(), "参会人界面获得到讨论区信息历史记录");
                    //OSG-445，根据需求再次修改，默认入口变回展示状态，并添加默认文字，作为讨论区入口
                    ForumContent forumContent = forumContentBucket.getData();
                    Message message = new Message();
                    message.what = HANDLER_UPDATE_FORUM_TEXT;

                    if (forumContent.getTotalCount() <= 0) {
                        //OSG-445，添加默认的展示文字，作为讨论区的入口
                        message.arg1 = HANDLER_UPDATE_FORUM_TEXT_NO;
                        forumItemMoreTextHandler.sendMessage(message);
                        startTextSwitcherDataAnimation(StringUtils.textColoring(getApplicationContext(), "", getString(R.string.forun_send_default_message), R.color.c_FF909090));
                        return;
                    }
                    message.arg1 = HANDLER_UPDATE_FORUM_TEXT_MORE;
                    forumItemMoreTextHandler.sendMessage(message);

                    LinkedList<PageData> pageDatas = forumContent.getPageData();
                    PageData pageData = pageDatas.get(pageDatas.size() - 1);
                    String lastMessage = pageData.getUserName() + "：";
                    if (pageData.getMsgType() == PageData.MSGTYPE_WITHDRAW) {
                        //消息类型
                        switch (pageData.getMsgType()) {
                            case PageData.MSGTYPE_NORMAL:
                                break;
                            case PageData.MSGTYPE_WITHDRAW:
                                startTextSwitcherDataAnimation(StringUtils.textColoring(getApplicationContext(), lastMessage, getString(R.string.forun_revocation_message), R.color.c_FF7FBAFF));
                                break;
                            case PageData.MSGTYPE_AGGREGATION:
                                break;
                        }
                    } else {
                        showTextSwitcherNormalContent(pageData);
                    }
                }
            });
        }
    }

    /**
     * 显示正常（非撤回）评论内容
     *
     * @param pageData
     */
    private void showTextSwitcherNormalContent(PageData pageData) {
        String lastMessage = pageData.getUserName() + "：";
        switch (pageData.getType()) {
            case PageData.TYPE_TEXT:
                lastMessage += pageData.getContent();
                startTextSwitcherDataAnimation(lastMessage);
                break;
            case PageData.TYPE_IMAGE:
                startTextSwitcherDataAnimation(StringUtils.textColoring(getApplicationContext(), lastMessage, getString(R.string.forun_send_message), R.color.c_FF7FBAFF));
                break;
        }
    }

    /**
     * 显示撤回评论内容
     *
     * @param forumRevokeContent
     */
    private void showTextSwitcherRevokeContent(ForumRevokeContent forumRevokeContent) {
        String lastMessage = forumRevokeContent.getUserName() + "：";
        startTextSwitcherDataAnimation(StringUtils.textColoring(getApplicationContext(), lastMessage, getString(R.string.forun_revocation_message), R.color.c_FF7FBAFF));
    }

    private void initTextSwitcher() {
        textswitcher_forum_item_audience_notification.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_bottom));
        textswitcher_forum_item_audience_notification.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_top));
        textswitcher_forum_item_audience_notification.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(getApplicationContext());
                textView.setSingleLine();
                textView.setTextSize(24);//字号
                textView.setTextColor(getResources().getColor(R.color.white));
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setSingleLine();
                textView.setGravity(Gravity.CENTER_VERTICAL);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER_VERTICAL;
                textView.setLayoutParams(params);
                return textView;
            }
        });
    }

    //设置数据
    private void startTextSwitcherDataAnimation(CharSequence msg) {
        runOnUiThread(() -> {

//			mMsgContent.setVisibility(View.GONE);
//			mMsgText.setText(msg);
//			FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mMsgContent.getLayoutParams();
//			int[] location = DisplayUtil.getLocation(mDiscussButton);
//
//			layoutParams.setMargins(location[0] - 5 + mDiscussButton.getWidth() / 4, 0, 0, DisplayUtil.dip2px(this, 110));
//			mMsgContent.setLayoutParams(layoutParams);
//
//
//			if (msgHandler.hasMessages(1)) {
//				msgHandler.removeMessages(1);
//			}
//
//			msgHandler.sendEmptyMessageDelayed(1, 3000);
//
//
//			textswitcher_forum_item_audience_notification.setText(msg);
//			textswitcher_forum_item_audience_notification.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_bottom));
//			textswitcher_forum_item_audience_notification.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_top));
        });
    }


    @SuppressLint("HandlerLeak")
    private Handler msgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                mMsgContent.setVisibility(View.GONE);
            } else if (msg.what == 2) {
                mMsgContent.setVisibility(View.GONE);
            }

        }
    };

    private OkHttpCallback joinMeetingCallback(int uid) {
        return new OkHttpCallback<Bucket<HostUser>>() {

            @Override
            public void onSuccess(Bucket<HostUser> meetingJoinBucket) {
                meetingJoin.setHostUser(meetingJoinBucket.getData());
                broadcastId = meetingJoinBucket.getData().getClientUid();
                broadcastNameText.setText("主持人：" + meetingJoinBucket.getData().getHostUserName());
                if (uid != 0 && broadcastId != null) {
                    if (uid == Integer.parseInt(broadcastId)) {
                        mLogger.e("主持人" + uid + "进入了   pptMode: " + pptMode + "   currentMaterial: " + currentMaterial);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MeetingAudienceActivity.this, "主持人" + uid + "回来了", Toast.LENGTH_SHORT).show();
                        }
                        isHostCommeIn = true;
                        pptMode = -1;
                        broadcastTipsText.setVisibility(View.GONE);
                        agoraAPI.channelJoin(channelName);
                        agoraAPI.queryUserStatus(broadcastId);
                        /**TextureView*/
                        remoteBroadcastSurfaceView = RtcEngine.CreateTextureView(getApplicationContext());
//                        remoteBroadcastSurfaceView.setZOrderOnTop(false);
//                        remoteBroadcastSurfaceView.setZOrderMediaOverlay(false);
                        rtcEngine().setupRemoteVideo(new VideoCanvas(remoteBroadcastSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));

                        broadcasterLayout.removeAllViews();
                        if (currentMaterial != null) {
                            broadcasterLayout.setVisibility(View.GONE);
                            broadcastTipsText.setVisibility(View.GONE);
                            pageText.setVisibility(View.VISIBLE);
                            docImage.setVisibility(View.VISIBLE);
                            MeetingMaterialsPublish currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(doc_index);
                            pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");
                            Picasso.with(MeetingAudienceActivity.this).load(currentMaterialPublish.getUrl()).into(docImage);

                            if (pptMode == 1) {
                                int chairManPosition = mVideoAdapter.getChairManPosition();
                                //主持人不再列表中
                                if (chairManPosition == -1) {
                                    AudienceVideo audienceVideo = new AudienceVideo();
                                    audienceVideo.setUid(Integer.parseInt(meetingJoin.getHostUser().getClientUid()));
                                    audienceVideo.setName("主持人" + meetingJoin.getHostUser().getHostUserName());
                                    audienceVideo.setBroadcaster(true);
                                    /**TextureView*/
                                    audienceVideo.setTextureView(remoteBroadcastSurfaceView);
                                    mVideoAdapter.insertItem(0, audienceVideo);
                                } else {
                                    /**TextureView*/
                                    mVideoAdapter.getAudienceVideoLists().get(mVideoAdapter.getChairManPosition()).setTextureView(remoteAudienceSurfaceView);

                                    mVideoAdapter.notifyDataSetChanged();
                                }

                            }
                        } else {
                            //主持人进入时 如果此时主持人在列表中 就移除 然后添加放大的参会人
                            if (mVideoAdapter.isHaveChairMan()) {
                                int chairManPosition = mVideoAdapter.getChairManPosition();
                                if (chairManPosition != -1) {
                                    mVideoAdapter.removeItem(chairManPosition);
                                    if (mCurrentAudienceVideo != null) {
                                        mVideoAdapter.insertItem(chairManPosition, mCurrentAudienceVideo);
                                    }
                                }
                            }
                            docImage.setVisibility(View.GONE);
                            pageText.setVisibility(View.GONE);
                            broadcastSmailLayout.setVisibility(View.INVISIBLE);
                            broadcastTipsText.setVisibility(View.GONE);
                            broadcasterLayout.setVisibility(View.VISIBLE);
                            broadcasterLayout.addView(remoteBroadcastSurfaceView);
                        }
                    } else {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MeetingAudienceActivity.this, "参会人" + uid + "正在连麦", Toast.LENGTH_SHORT).show();
                        }

                        audienceTipsText.setVisibility(View.GONE);
                        audienceLayout.removeAllViews();
                        /**TextureView*/
                        remoteAudienceSurfaceView = RtcEngine.CreateTextureView(getApplicationContext());
//                        remoteAudienceSurfaceView.setZOrderOnTop(true);
//                        remoteAudienceSurfaceView.setZOrderMediaOverlay(true);
                        rtcEngine().setupRemoteVideo(new VideoCanvas(remoteAudienceSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
//                        audienceLayout.addView(remoteAudienceSurfaceView);

                        AudienceVideo audienceVideo = new AudienceVideo();
                        audienceVideo.setUid(uid);
                        audienceVideo.setName("参会人" + uid);
                        audienceVideo.setBroadcaster(false);
                        /**TextureView*/
                        audienceVideo.setTextureView(remoteAudienceSurfaceView);
                        mVideoAdapter.insertItem(audienceVideo);

//						agoraAPI.getUserAttr(String.valueOf(uid), "uname");
                        agoraAPI.getUserAttr(String.valueOf(uid), "userName");
                        agoraAPI.getUserAttr(String.valueOf(uid), "uname");

                    }
                }

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("uid", config().mUid);
                    jsonObject.put("uname", audienceName);
                    jsonObject.put("handsUp", handsUp);
                    if (Constant.videoType == 1) {
                        jsonObject.put("callStatus", 2);
                        jsonObject.put("isAudience", false);
                    } else {
                        jsonObject.put("callStatus", 0);
                        jsonObject.put("isAudience", true);
                    }
                    jsonObject.put("auditStatus", Preferences.getUserAuditStatus());
                    jsonObject.put("postTypeName", Preferences.getUserPostTypeName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                agoraAPI.messageInstantSend(broadcastId, 0, jsonObject.toString(), "");


                if (isSplitView && mVideoAdapter.getDataSize() > 1) {
                    SpliteViews(3);
                }


                if (isFullScreen || pptMode == 1 || pptMode == 2) {
                    mVideoAdapter.setVisibility(View.GONE);
                    mAudienceRecyclerView.setVisibility(View.GONE);
                } else {
                    if (pptMode == 0) {
                        notFullScreenState();
                    }
                    mVideoAdapter.setVisibility(View.VISIBLE);
                    mAudienceRecyclerView.setVisibility(View.VISIBLE);

                }

                if (isPPTModel) {
                    mVideoAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onFailure(int errorCode, BaseException exception) {
                super.onFailure(errorCode, exception);
                Toast.makeText(MeetingAudienceActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    private OkHttpCallback meetingMaterialCallback = new OkHttpCallback<Bucket<Material>>() {

        @Override
        public void onSuccess(Bucket<Material> materialBucket) {
            Log.v("material", materialBucket.toString());
            currentMaterial = materialBucket.getData();
/*

			MeetingMaterialsPublish e1 = new MeetingMaterialsPublish();
			e1.setCreateDate(System.currentTimeMillis() + "");
			e1.setId(System.currentTimeMillis() + "");
			e1.setType("1");
			e1.setPriority(4);
			e1.setUrl("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4");

			currentMaterial.getMeetingMaterialsPublishList().add(e1);
*/


            Collections.sort(currentMaterial.getMeetingMaterialsPublishList(), (o1, o2) -> (o1.getPriority() < o2.getPriority()) ? -1 : 1);

            MeetingMaterialsPublish currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(doc_index);

            if (remoteBroadcastSurfaceView != null) {
                broadcasterLayout.removeView(remoteBroadcastSurfaceView);
                broadcasterLayout.setVisibility(View.GONE);

            }

            mLogger.e("pptMode==   " + pptMode);

            if (pptMode == 1 || pptMode == 2) {
                mAudienceRecyclerView.setVisibility(View.GONE);
                mVideoAdapter.setVisibility(View.GONE);
            } else {
                mAudienceRecyclerView.setVisibility(View.VISIBLE);
                mVideoAdapter.setVisibility(View.VISIBLE);
            }


            if (pptMode == 0) {
                notFullScreenState();
            } else if (pptMode == 1) {
                FullScreenState();
            } else if (pptMode == 2) {
                clearAllState();
            }

            if (isSplitView && !isPPTModel) {
                full_screen.setVisibility(View.GONE);
            } else {
                full_screen.setVisibility(View.VISIBLE);
            }

            if (currentMaterialPublish.getType().equals("1")) {
                PlayVideo();
            } else {
                findViewById(R.id.app_video_box).setVisibility(View.GONE);
                mVideoContainer.setVisibility(View.GONE);
                stopPlayVideo();
                docImage.setVisibility(View.VISIBLE);
                docImage.setVisibility(View.VISIBLE);
                Picasso.with(MeetingAudienceActivity.this).load(currentMaterialPublish.getUrl()).into(docImage);
            }

            pageText.setVisibility(View.VISIBLE);

            pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");


        }

        @Override
        public void onFailure(int errorCode, BaseException exception) {
            super.onFailure(errorCode, exception);
            Toast.makeText(MeetingAudienceActivity.this, errorCode + "---" + exception.getMessage(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(MeetingAudienceActivity.this, errorCode + "---" + exception.getMessage(), Toast.LENGTH_SHORT).show();
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
                    exit();
                } else if (type == 2) {
                    worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
                    audienceLayout.removeAllViews();
                    audienceNameText.setText("");
                    finishButton.setVisibility(View.GONE);
                    micButton.setVisibility(View.VISIBLE);
                    audienceTipsText.setVisibility(View.VISIBLE);
                    localAudienceSurfaceView = null;
                    agoraAPI.channelDelAttr(channelName, CALLING_AUDIENCE);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("finish", true);
                        agoraAPI.messageInstantSend(broadcastId, 0, jsonObject.toString(), "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("handsUp", handsUp);
                        jsonObject.put("uid", config().mUid);
                        jsonObject.put("uname", audienceName);
                        jsonObject.put("callStatus", 0);
                        jsonObject.put("auditStatus", Preferences.getUserAuditStatus());
                        jsonObject.put("postTypeName", Preferences.getUserPostTypeName());
                        agoraAPI.messageInstantSend(broadcastId, 0, jsonObject.toString(), "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!TextUtils.isEmpty(meetingHostJoinTraceId)) {
                        HashMap<String, Object> params = new HashMap<String, Object>();
                        params.put("meetingHostJoinTraceId", meetingHostJoinTraceId);
                        params.put("status", 2);
                        params.put("meetingId", meetingJoin.getMeeting().getId());
                        params.put("type", 2);
                        params.put("leaveType", 1);
                        ApiClient.getInstance().meetingHostStats(TAG, meetingHostJoinTraceCallback, params);
                    }
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

    private void exit() {
        if (localAudienceSurfaceView != null && remoteAudienceSurfaceView == null) {
            audienceLayout.removeAllViews();
            audienceNameText.setText("");
            audienceTipsText.setVisibility(View.GONE);

            localAudienceSurfaceView = null;

//						agoraAPI.setAttr("uname", null);
            agoraAPI.channelDelAttr(channelName, CALLING_AUDIENCE);

            micButton.setVisibility(View.VISIBLE);
            finishButton.setVisibility(View.GONE);

            handsUp = false;
            micButton.setText("申请发言");

            worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("handsUp", handsUp);
                jsonObject.put("uid", config().mUid);
                jsonObject.put("uname", audienceName);
                jsonObject.put("callStatus", 0);
                jsonObject.put("auditStatus", Preferences.getUserAuditStatus());
                jsonObject.put("postTypeName", Preferences.getUserPostTypeName());
                agoraAPI.messageInstantSend(broadcastId, 0, jsonObject.toString(), "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!TextUtils.isEmpty(meetingHostJoinTraceId)) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("meetingHostJoinTraceId", meetingHostJoinTraceId);
                params.put("status", 2);
                params.put("meetingId", meetingJoin.getMeeting().getId());
                params.put("type", 2);
                params.put("leaveType", 1);
                ApiClient.getInstance().meetingHostStats(TAG, meetingHostJoinTraceCallback, params);
            }
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("finish", true);
                agoraAPI.messageInstantSend(broadcastId, 0, jsonObject.toString(), "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (localAudienceSurfaceView == null && remoteAudienceSurfaceView != null) {
            audienceLayout.removeAllViews();
            audienceNameText.setText("");
            audienceTipsText.setVisibility(View.GONE);

            remoteAudienceSurfaceView = null;
        }
        doLeaveChannel();
        if (agoraAPI.getStatus() == 2) {
            agoraAPI.logout();
        }
        finish();
    }

    private void doConfigEngine(int cRole) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int prefIndex = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, ConstantApp.DEFAULT_PROFILE_IDX);
        int vProfile = ConstantApp.VIDEO_PROFILES[prefIndex - 2];

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

                com.orhanobut.logger.Logger.e("onJoinChannelSuccess  加入频道成功");

                config().mUid = uid;
                channelName = channel;
                if ("true".equals(agora.getIsTest())) {
                    agoraAPI.login2(agora.getAppID(), "" + uid, "noneed_token", 0, "", 20, 30);
                } else {
                    agoraAPI.login2(agora.getAppID(), "" + uid, agora.getSignalingKey(), 0, "", 20, 30);
                }


                int quilty = Constant.resolvingPower;
                VideoEncoderConfiguration.VideoDimensions vProfile = VideoEncoderConfiguration.VD_320x180;
                if (quilty == 1.0) {
                    vProfile = VideoEncoderConfiguration.VD_320x180;
                    ;//参会人是320*180
                } else if (quilty == 2.0) {
                    vProfile = VideoEncoderConfiguration.VD_640x360;//参会人是320*180
                } else if (quilty == 3.0) {
                    vProfile = VideoEncoderConfiguration.VD_1280x720;//参会人是640 x 480
                } else {
                    vProfile = VideoEncoderConfiguration.VD_640x360;//参会人是320*180
                }

                // 观众的方式进入
                if (Constant.videoType == 2) {
                    worker().configEngine(Constants.CLIENT_ROLE_AUDIENCE, vProfile);//参会人是320*180
                    mMuteAudio.setVisibility(View.GONE);
                    if (Constant.isPadApplicaion) {
                        mSwtichCamera.setVisibility(View.VISIBLE);
                    } else {
                        mSwtichCamera.setVisibility(View.GONE);
                    }


                } else if (Constant.videoType == 1) {
                    //参会人的方式进入
                    worker().configEngine(Constants.CLIENT_ROLE_BROADCASTER, vProfile);
                    /**TextureView*/
                    localAudienceSurfaceView = RtcEngine.CreateTextureView(getApplicationContext());
//                    localAudienceSurfaceView.setZOrderOnTop(true);
//                    localAudienceSurfaceView.setZOrderMediaOverlay(true);
                    rtcEngine().setupLocalVideo(new VideoCanvas(localAudienceSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, config().mUid));
                    worker().preview2(true, localAudienceSurfaceView, config().mUid);

                    mLocalAudienceVideo = new AudienceVideo();
                    mLocalAudienceVideo.setUid(config().mUid);
                    mLocalAudienceVideo.setName("参会人" + config().mUid);
                    mLocalAudienceVideo.setBroadcaster(false);
                    /**TextureView*/
                    mLocalAudienceVideo.setTextureView(localAudienceSurfaceView);
                    mVideoAdapter.insertItem(mLocalAudienceVideo);

                    mAudienceRecyclerView.setVisibility(View.VISIBLE);
                    micButton.setVisibility(View.GONE);
                    mMuteAudio.setVisibility(View.VISIBLE);
                    if (Constant.isPadApplicaion) {
                        mSwtichCamera.setVisibility(View.VISIBLE);
                    } else {
                        mSwtichCamera.setVisibility(View.GONE);
                    }

                }

                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("meetingId", meeting.getId());
                params.put("status", 1);
                params.put("type", 2);
                ApiClient.getInstance().meetingJoinStats(TAG, meetingJoinStatsCallback, params);


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
            agoraAPI.queryUserStatus(String.valueOf(uid));


            ApiClient.getInstance().getMeetingHost(TAG, meeting.getId(), String.valueOf(uid),joinMeetingCallback(uid));
        });
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        mLogger.e("onUserJoined " + (uid) + " " + elapsed);
        runOnUiThread(() -> {
            if (isFinishing()) {
                return;
            }
        });
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        mLogger.e("onUserOffline " + (uid) + " " + reason);
        runOnUiThread(() -> {
            if (isFinishing()) {
                return;
            }


            if (String.valueOf(uid).equals(broadcastId)) {
                mLogger.e("主持人退出了");
                remoteBroadcastSurfaceView = null;

                if (Constant.videoType == 2 && config().mUid == currentAudienceId) {//我是连麦观众
                    if (mVideoAdapter.getPositionById(config().mUid) != -1) {
                        AudienceVideo video = mVideoAdapter.getAudienceVideoLists().get(mVideoAdapter.getPositionById(config().mUid));
                        if (video != null && video.getSurfaceView() != null) {
                            video.getSurfaceView().setVisibility(View.GONE);
                            video.getSurfaceView().setZOrderMediaOverlay(false);
                        }
                        mVideoAdapter.deleteItemById(config().mUid);
                    }
                    mSwtichCamera.setVisibility(View.GONE);
                    micButton.setText("申请发言");
                    finishButton.setVisibility(View.GONE);
                    mMuteAudio.setVisibility(View.GONE);
                }

                if (Constant.videoType == 2) {
                    changeMyAudienceView();//主持人退出 连麦人也消失
                }


                if (currentMaterial != null) {
                    if (currentMaterial.getMeetingMaterialsPublishList().get(doc_index).getType().equals("1")) {
                        if (player != null) {
                            player.startButton.performClick();
                        }
                        player = null;
                        findViewById(R.id.app_video_box).setVisibility(View.GONE);
                        mVideoContainer.setVisibility(View.GONE);
                    }
                }


                if (mVideoAdapter.isHaveChairMan()) {
                    int chairManPosition = mVideoAdapter.getChairManPosition();
                    if (chairManPosition != -1) {
                        mVideoAdapter.removeItem(chairManPosition);
                        if (mCurrentAudienceVideo != null) {
                            mVideoAdapter.insertItem(chairManPosition, mCurrentAudienceVideo);
                        }
                    }


                }
                isHostCommeIn = false;
                broadcasterLayout.removeAllViews();
                if (reason == 0) {
                    broadcastTipsText.setText("等待主持人进入...");
                } else if (reason == 1) {
                    broadcastTipsText.setText("主持人掉线了……");
                }

                broadcastTipsText.setVisibility(View.VISIBLE);

                broadcastNameText.setText("");


                if (localAudienceSurfaceView != null) {
                    if (Constant.videoType == 1) {
                        worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
                        int positionById = mVideoAdapter.getPositionById(config().mUid);
                        if (positionById != -1) {
                            AudienceVideo video = mVideoAdapter.getAudienceVideoLists().get(positionById);
                            rtcEngine().setupLocalVideo(new VideoCanvas(video.getSurfaceView(), VideoCanvas.RENDER_MODE_HIDDEN, config().mUid));
                            /**TextureView*/
                            worker().preview2(true, video.getTextureView(), config().mUid);
                        }

                    } else {
                        worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
                        finishButton.setVisibility(View.GONE);
                        micButton.setVisibility(View.VISIBLE);
                        if (Constant.videoType == 2) {
                            localAudienceSurfaceView = null;
                        }

                    }
                    remoteAudienceSurfaceView = null;

                    /*if (currentMaterial!=null) {
                        fullScreenButton.setVisibility(View.GONE);
                    }*/
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("finish", true);
                        agoraAPI.messageInstantSend(broadcastId, 0, jsonObject.toString(), "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!TextUtils.isEmpty(meetingHostJoinTraceId)) {
                        HashMap<String, Object> params = new HashMap<String, Object>();
                        params.put("meetingHostJoinTraceId", meetingHostJoinTraceId);
                        params.put("status", 2);
                        params.put("meetingId", meetingJoin.getMeeting().getId());
                        params.put("type", 2);
                        params.put("leaveType", 1);
                        ApiClient.getInstance().meetingHostStats(TAG, meetingHostJoinTraceCallback, params);
                    }

                }
            } else {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(MeetingAudienceActivity.this, "连麦观众" + uid + "退出了" + config().mUid, Toast.LENGTH_SHORT).show();
                }
                //如果连麦的观众 在大的视图 那就移除这个人  把主持人放回到大视图
                if (mCurrentAudienceVideo != null && mCurrentAudienceVideo.getUid() == uid) {
                    mLogger.e("连麦的观众 在大的视图   把主持人放回到大视图 ");
                    broadcasterLayout.removeAllViews();
                    if (mVideoAdapter.isHaveChairMan()) {
                        int chairManPosition = mVideoAdapter.getChairManPosition();
                        if (chairManPosition != -1) {
                            mVideoAdapter.removeItem(chairManPosition);
                        }
                        broadcasterLayout.removeAllViews();
                        stripSurfaceView(remoteBroadcastSurfaceView);
                        broadcasterLayout.addView(remoteBroadcastSurfaceView);
                        broadcasterLayout.setVisibility(View.VISIBLE);
                    }
                    mCurrentAudienceVideo = null;
                } else if (mCurrentAudienceVideo != null && mCurrentAudienceVideo.getUid() != uid) {
                    //退出观众不在大视图里面  但是主持人是在列表中
                    if (mVideoAdapter.isHaveChairMan()) {
                        if (mCurrentAudienceVideo.getSurfaceView() != null) {
                            mCurrentAudienceVideo.getSurfaceView().setZOrderOnTop(false);
                            mCurrentAudienceVideo.getSurfaceView().setZOrderMediaOverlay(false);
                        }
                    }
                    mVideoAdapter.deleteItem(uid);

                } else {
                    mLogger.e("连麦观众在列表中");
                    int position = mVideoAdapter.getPositionById(uid);
                    if (position != -1) {
                        AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(position);
                        if (audienceVideo != null && audienceVideo.getSurfaceView() != null) {
                            audienceVideo.getSurfaceView().setZOrderMediaOverlay(false);
                            audienceVideo.getSurfaceView().setZOrderOnTop(false);
                            mVideoAdapter.removeItem(position);
                        }

                    }
                    mVideoAdapter.deleteItemById(uid);
                    if (remoteBroadcastSurfaceView != null) {
                        stripSurfaceView(remoteBroadcastSurfaceView);
                        /**TextureView*/
//                        remoteBroadcastSurfaceView.setZOrderOnTop(false);
//                        remoteBroadcastSurfaceView.setZOrderMediaOverlay(false);
                        broadcasterLayout.removeAllViews();
                        broadcasterLayout.addView(remoteBroadcastSurfaceView);
                    }

                }

                if (null != currentMaterial) {
                    if (Constant.videoType == 1) {
                        mMuteAudio.setVisibility(View.VISIBLE);
                        if (Constant.isPadApplicaion) {
                            mSwtichCamera.setVisibility(View.VISIBLE);
                        } else {
                            mSwtichCamera.setVisibility(View.GONE);
                        }
                        micButton.setVisibility(View.GONE);
                    } else {
                        mMuteAudio.setVisibility(View.GONE);
                        mSwtichCamera.setVisibility(View.GONE);
                        micButton.setVisibility(View.VISIBLE);
                    }
                }
                remoteAudienceSurfaceView = null;


            }

            if (isSplitView) {
                if (mVideoAdapter.getDataSize() <= 1) {
                    mLogger.e("当前集合大小是小于等于1");
                    exitSpliteMode();
                    isSplitView = false;
                } else {
                    mLogger.e("当前集合大小是大于1的");
                    changeViewLayout();
                    isSplitView = true;
                }
            }
        });
    }

    @Override
    public void onConnectionLost() {
        runOnUiThread(() -> {
            try {
                NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);

                dialogBuilder
                        .withTitle("网络连接失败")
                        .withMessage("与声网服务器连接断开  请检查网络连接\n\t\t是否需要退出？")
                        .withButton1Text("退出")                                      //def gone
                        .isCancelableOnTouchOutside(false)
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogBuilder.dismiss();
                                finish();
                            }
                        })
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogBuilder.dismiss();
                            }
                        })
                        .show();
            } catch (Exception e) {
                ToastUtils.showToast("网络连接失败 请检查网络 重新进入会议");
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onConnectionInterrupted() {
        runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, "网络连接不佳，视频将会有卡顿，可尝试降低分辨率", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onUserMuteVideo(final int uid, final boolean muted) {
        if (BuildConfig.DEBUG) {
            runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, uid + " 的视频被暂停了 " + muted, Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onUserMuteAudio(int uid, boolean muted) {
        runOnUiThread(()-> {
            int uidHost = Integer.parseInt(meetingJoin.getHostUser().getClientUid());
            if (uid == uidHost) {
                if (muted) {
                    Glide.with(this).load(R.drawable.icon_speek_no_select).into(img_line_state);
                } else {
                    Glide.with(this).load(R.drawable.icon_speek_select).into(img_line_state);
                }
            }

        });
    }

    @Override
    public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {
//        runOnUiThread(() -> {
//            for (IRtcEngineEventHandler.AudioVolumeInfo audioVolumeInfo : speakers) {
//                mVideoAdapter.setVolumeByUid(audioVolumeInfo.uid, audioVolumeInfo.volume);
//            }
//        });
    }

    @Override
    public void onLastmileQuality(final int quality) {
        if (BuildConfig.DEBUG) {
            runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, "本地网络质量报告：" + showNetQuality(quality), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        if (BuildConfig.DEBUG) {
            runOnUiThread(() -> {
                showNetQuality(rxQuality);
//                    Toast.makeText(MeetingAudienceActivity.this, "用户" + uid + "的\n上行网络质量：" + showNetQuality(txQuality) + "\n下行网络质量：" + showNetQuality(rxQuality), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private String showNetQuality(int quality) {
        String lastmileQuality;

        switch (quality) {
            case 0:
                lastmileQuality = "UNKNOWN0";
                mCurrentNetSpeed.setText("当前网速：质量未知");
                break;
            case 1:
                lastmileQuality = "EXCELLENT";
                mCurrentNetSpeed.setText("当前网速：质量极好");
                break;
            case 2:
                lastmileQuality = "GOOD";
                mCurrentNetSpeed.setText("当前网速：质量好");
                break;
            case 3:
                lastmileQuality = "POOR";
                mCurrentNetSpeed.setText("当前网速：主观感受有瑕疵但不影响沟通");
                break;
            case 4:
                lastmileQuality = "BAD";
                mCurrentNetSpeed.setText("当前网速：勉强能沟通但不顺畅");
                break;
            case 5:
                lastmileQuality = "VBAD";
                mCurrentNetSpeed.setText("网络质量非常差，基本不能沟通");
                break;
            case 6:
                lastmileQuality = "DOWN";
                mCurrentNetSpeed.setText("当前网速：完全无法沟通");
                break;
            default:
                lastmileQuality = "UNKNOWN";
                mCurrentNetSpeed.setText("当前网速： 正在探测网络质量");
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
            runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, "错误码：" + err, Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
        runOnUiThread(() -> {
            Drawable right = getResources().getDrawable(R.drawable.icon_network_a);
            if (mNetworkIcon != null) {
                if (stats.lastmileDelay > 0 && stats.lastmileDelay <= 50) {
                    right = getResources().getDrawable(R.drawable.icon_network_a);
                } else if (stats.lastmileDelay > 50 && stats.lastmileDelay <= 100) {
                    right = getResources().getDrawable(R.drawable.icon_network_b);
                } else if (stats.lastmileDelay > 100 && stats.lastmileDelay <= 200) {
                    right = getResources().getDrawable(R.drawable.icon_network_c);
                } else if (stats.lastmileDelay > 200) {
                    right = getResources().getDrawable(R.drawable.icon_network_d);
                }
                mNetworkIcon.setCompoundDrawablesWithIntrinsicBounds(null, null, right, null);
            }
            if (mNetWorkNumber != null) {
                mNetWorkNumber.setText(stats.lastmileDelay + "ms");
            }
        });
    }

    @Override
    public void onLocalVideoStateChanged(int localVideoState, int error) {

    }

    @Override
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
        com.orhanobut.logger.Logger.e("uid:" + uid + "  state:" + state + "  reason:" + reason + "   elapsed:" + elapsed);
        runOnUiThread(() -> {
            if (state == 2) {//远端视频流正在解码，正常播放
                //如果是主持人正常解码了
                int chairManPosition = mVideoAdapter.getChairManPosition();
                if (chairManPosition == -1) {//说明在列表中没找到主持人 主持人肯定在大视图里面
                    if (remoteBroadcastSurfaceView == null) {
                        /**TextureView*/
                        remoteBroadcastSurfaceView = RtcEngine.CreateTextureView(getApplicationContext());
//                        remoteBroadcastSurfaceView.setZOrderOnTop(false);
//                        remoteBroadcastSurfaceView.setZOrderMediaOverlay(false);
                        rtcEngine().setupRemoteVideo(new VideoCanvas(remoteBroadcastSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
                        broadcasterLayout.removeAllViews();
                        stripSurfaceView(remoteBroadcastSurfaceView);
                        broadcasterLayout.addView(remoteBroadcastSurfaceView);
                        broadcasterLayout.setVisibility(View.VISIBLE);
                        broadcastTipsText.setVisibility(View.GONE);
                        isHostCommeIn = true;
                    }
                } else {
                    //主持人在列表中 或者其他参会人在列表中
                    try {
                        if (uid == Integer.parseInt(meetingJoin.getHostUser().getClientUid())) {
                            isHostCommeIn = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    int positionById = mVideoAdapter.getPositionById(uid);
                    if (positionById != -1) {
                        AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(positionById);
                        if (audienceVideo != null && audienceVideo.getSurfaceView() == null) {
                            SurfaceView surfaceView = RtcEngine.CreateRendererView(getApplicationContext());
                            surfaceView.setZOrderOnTop(true);
                            surfaceView.setZOrderMediaOverlay(true);
                            audienceVideo.setVideoStatus(0);
                            rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
                            audienceVideo.setSurfaceView(surfaceView);
                            mVideoAdapter.notifyItemChanged(positionById);
                        }
                    } else if (mCurrentAudienceVideo != null && mCurrentAudienceVideo.getUid() == uid) {
                        //当前放大的人在大的视图里买呢
                        SurfaceView surfaceView = RtcEngine.CreateRendererView(getApplicationContext());
                        surfaceView.setZOrderOnTop(false);
                        surfaceView.setZOrderMediaOverlay(false);
                        rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
                        mCurrentAudienceVideo.setSurfaceView(surfaceView);
                        mCurrentAudienceVideo.setVideoStatus(0);
                        broadcasterLayout.removeAllViews();
                        stripSurfaceView(remoteBroadcastSurfaceView);
                        broadcasterLayout.addView(remoteBroadcastSurfaceView);
                        broadcasterLayout.setVisibility(View.VISIBLE);
                    }
                }
            } else if (state == 4) {//远端视频流播放失败
                int positionById = mVideoAdapter.getPositionById(uid);
                if (positionById != -1) {
                    mVideoAdapter.getAudienceVideoLists().get(positionById).setVideoStatus(2);
                    mVideoAdapter.notifyItemChanged(positionById);
                } else if (uid == Integer.parseInt(meetingJoin.getHostUser().getClientUid())) {
                    remoteBroadcastSurfaceView = null;
                    broadcasterLayout.removeAllViews();
                    broadcastTipsText.setText("主持人的视频播放失败了");
                    broadcastTipsText.setVisibility(View.VISIBLE);
                }

            } else if (state == 3) {//远端视频流卡顿
                int positionById = mVideoAdapter.getPositionById(uid);
                if (positionById != -1) {
                    mVideoAdapter.getAudienceVideoLists().get(positionById).setVideoStatus(1);
                    mVideoAdapter.notifyItemChanged(positionById);
                } else if (uid == Integer.parseInt(meetingJoin.getHostUser().getClientUid())) {
                    broadcastTipsText.setText("主持人的视频卡顿了");
                    broadcastTipsText.setVisibility(View.VISIBLE);
                }
            }
        });


    }


    /**
     *
     * 远端音频流状态:state
     * REMOTE_AUDIO_STATE_STOPPED(0)：远端音频流默认初始状态。 在 REMOTE_AUDIO_REASON_LOCAL_MUTED(3)、REMOTE_AUDIO_REASON_REMOTE_MUTED(5) 或 REMOTE_AUDIO_REASON_REMOTE_OFFLINE(7) 的情况下，会报告该状态。
     * REMOTE_AUDIO_STATE_STARTING(1)：本地用户已接收远端音频首包
     * REMOTE_AUDIO_STATE_DECODING(2)：远端音频流正在解码，正常播放。在 REMOTE_AUDIO_REASON_NETWORK_RECOVERY(2)、 REMOTE_AUDIO_REASON_LOCAL_UNMUTED(4) 或 REMOTE_AUDIO_REASON_REMOTE_UNMUTED(6) 的情况下，会报告该状态
     * REMOTE_AUDIO_STATE_FROZEN(3)：远端音频流卡顿。在 REMOTE_AUDIO_REASON_NETWORK_CONGESTION(1) 的情况下，会报告该状态
     * REMOTE_AUDIO_STATE_FAILED(4)：远端音频流播放失败。在 REMOTE_AUDIO_REASON_INTERNAL(0) 的情况下，会报告该状态
     *
     * 远端音频流状态改变的具体原因：reason
     * REMOTE_AUDIO_REASON_INTERNAL(0)：内部原因
     * REMOTE_AUDIO_REASON_NETWORK_CONGESTION(1)：网络阻塞
     * REMOTE_AUDIO_REASON_NETWORK_RECOVERY(2)：网络恢复正常
     * REMOTE_AUDIO_REASON_LOCAL_MUTED(3)：本地用户停止接收远端音频流或本地用户禁用音频模块
     * REMOTE_AUDIO_REASON_LOCAL_UNMUTED(4)：本地用户恢复接收远端音频流或本地用户启用音频模块
     * REMOTE_AUDIO_REASON_REMOTE_MUTED(5)：远端用户停止发送音频流或远端用户禁用音频模块
     * REMOTE_AUDIO_REASON_REMOTE_UNMUTED(6)：远端用户恢复发送音频流或远端用户启用音频模块
     * REMOTE_AUDIO_REASON_REMOTE_OFFLINE(7)：远端用户离开频道
     * */
    @Override
    public void onRemoteAudioStateChanged(int uid, int state, int reason, int elapsed) {
        runOnUiThread(() -> {
            switch (reason) {
                case REMOTE_AUDIO_REASON_INTERNAL:
                    ToastUtils.showToast("音频发生错误");
                    break;
                case REMOTE_AUDIO_REASON_NETWORK_CONGESTION:
                    ToastUtils.showToast("网络错误");
                    break;
                case REMOTE_AUDIO_REASON_NETWORK_RECOVERY:
                    ToastUtils.showToast("网络恢复正常");
                    break;
                case REMOTE_AUDIO_REASON_LOCAL_MUTED:
                    ToastUtils.showToast("本地用户停止接收远端音频流或本地用户禁用音频模块");
                    break;
                case REMOTE_AUDIO_REASON_LOCAL_UNMUTED:
                    ToastUtils.showToast("本地用户恢复接收远端音频流或本地用户启用音频模块");
                    break;
                case REMOTE_AUDIO_REASON_REMOTE_MUTED:
                    ToastUtils.showToast("远端用户停止发送音频流或远端用户禁用音频模块");
                    break;
                case REMOTE_AUDIO_REASON_REMOTE_UNMUTED:
                    ToastUtils.showToast("远端用户恢复发送音频流或远端用户启用音频模块");
                    break;
                case REMOTE_AUDIO_REASON_REMOTE_OFFLINE:
                    ToastUtils.showToast("远端用户离开频道");
                    break;
            }
        });
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
        event().removeEventHandler(this);
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

        if (takePhotoTimer != null && takePhotoTimerTask != null) {
            takePhotoTimer.cancel();
            takePhotoTimerTask.cancel();
        }

        if (subscription != null) {
            subscription.unsubscribe();
        }
        if (player != null) {
            JzvdStd.releaseAllVideos();
        }

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
                    worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
                    audienceLayout.removeAllViews();
                    audienceNameText.setText("");
                    finishButton.setVisibility(View.GONE);
                    micButton.setVisibility(View.VISIBLE);
                    audienceTipsText.setVisibility(View.VISIBLE);

                    if (handsUp) {
                        handsUp = false;
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("finish", true);
                            agoraAPI.messageInstantSend(broadcastId, 0, jsonObject.toString(), "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    agoraAPI.channelLeave(channelName);
                    if (agoraAPI.getStatus() == 2) {
                        agoraAPI.logout();
                    }
                    finish();
                } else if (TextUtils.equals(reason, RECENTAPPS)) {
                    // 点击 菜单键
                    if (BuildConfig.DEBUG)
                        Toast.makeText(getApplicationContext(), "您点击了菜单键", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_REQUEST_TAKEPHOTO) {
            try {
                String pictureLocalPath = data.getStringExtra("pictureLocalPath");
                uploadMeetingImageToQiniu(pictureLocalPath);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * 上传参会人参会时图片到七牛服务器
     *
     * @param imagePath
     */
    private void uploadMeetingImageToQiniu(String imagePath) {
        com.zhongyou.meettvapplicaion.net.ApiClient.getInstance().requestQiniuToken(this, new com.zhongyou.meettvapplicaion.net.OkHttpCallback<BaseBean<QiniuToken>>() {

            @Override
            public void onSuccess(BaseBean<QiniuToken> result) {
                String token = result.getData().getToken();
                if (TextUtils.isEmpty(token)) {
                    String errorMsg = "七牛token获取错误";
                    ZYAgent.onEvent(getApplicationContext(), errorMsg);
                    showToast(errorMsg);
                    return;
                }
                Configuration config = new Configuration.Builder().connectTimeout(5).responseTimeout(5).build();
                UploadManager uploadManager = new UploadManager(config);
                ZYAgent.onEvent(getApplicationContext(), "拍照完毕，准备上传本地图片：" + imagePath);

                String uploadKey = BuildConfig.QINIU_IMAGE_UPLOAD_PATH + meeting.getId() + "/" + Preferences.getUserId() + "/" + imagePath.substring(imagePath.lastIndexOf('/') + 1);
                uploadManager.put(new File(imagePath), uploadKey, token, meetingImageUpCompletionHandler, new UploadOptions(null, null, true, new UpProgressHandler() {
                    @Override
                    public void progress(final String key, final double percent) {
                    }
                }, null));
            }
        });
    }

    private UpCompletionHandler meetingImageUpCompletionHandler = new UpCompletionHandler() {
        @Override
        public void complete(String key, ResponseInfo info, JSONObject response) {
            if (info.isNetworkBroken() || info.isServerError()) {
                ZYAgent.onEvent(getApplicationContext(), "参会人直播图像上传七牛云失败");
                return;
            }
            if (info.isOK()) {
                String meetingImageUrl = BuildConfig.QINIU_IMAGE_DOMAIN + key;
                ZYAgent.onEvent(getApplicationContext(), "参会人直播图像上传七牛云成功，地址：" + meetingImageUrl);
                uploadMeetingImageToServer(meeting.getId(), meetingImageUrl);
            } else {
                ZYAgent.onEvent(getApplicationContext(), "参会人直播图像上传七牛云失败");
            }
        }
    };

    /**
     * 上传参会人参会时图片到Server
     *
     * @param meetingId
     * @param qiniuImageUrl
     */
    public void uploadMeetingImageToServer(String meetingId, String qiniuImageUrl) {
        Map<String, Object> params = new HashMap<>();
        params.put("meetingId", meetingId);
        params.put("imgUrl", qiniuImageUrl);
        params.put("ts", String.valueOf(System.currentTimeMillis()));
        ApiClient.getInstance().meetingScreenshot(this, params, uploadMeetingImageToServerCallback);
    }

    private OkHttpCallback<Bucket<MeetingScreenShot>> uploadMeetingImageToServerCallback = new OkHttpCallback<Bucket<MeetingScreenShot>>() {

        @Override
        public void onSuccess(Bucket<MeetingScreenShot> meetingScreenShotBucket) {
            ZYAgent.onEvent(getApplicationContext(), "参会人直播图像上传服务器成功");
        }
    };

    @Override
    public void finish() {
        RxBus.sendMessage(new ForumActivityCloseEvent());
        super.finish();
    }

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


    private Handler showToolBarsHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    findViewById(R.id.linearLayout).setVisibility(View.GONE);
                    break;
                case 1:
                    findViewById(R.id.linearLayout).setVisibility(View.VISIBLE);
                    if (!Constant.isPadApplicaion) {
                        exitButton.requestFocus();
                    }
                    showToolBarsHandler.sendEmptyMessageDelayed(0, Constant.delayTime);
                    break;

            }
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getAction()) {
            case KeyEvent.ACTION_DOWN:
                if (showToolBarsHandler.hasMessages(0)) {
                    showToolBarsHandler.removeMessages(0);
                }
                if (findViewById(R.id.linearLayout).getVisibility() == View.VISIBLE) {
                    showToolBarsHandler.sendEmptyMessageDelayed(0, Constant.delayTime);
                } else if (findViewById(R.id.linearLayout).getVisibility() == View.GONE) {
                    showToolBarsHandler.sendEmptyMessage(1);
                }
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (showToolBarsHandler.hasMessages(0)) {
                    showToolBarsHandler.removeMessages(0);
                }
                if (findViewById(R.id.linearLayout).getVisibility() == View.VISIBLE) {
                    showToolBarsHandler.sendEmptyMessageDelayed(0, Constant.delayTime);
                } else if (findViewById(R.id.linearLayout).getVisibility() == View.GONE) {
                    showToolBarsHandler.sendEmptyMessage(1);
                }
                break;

        }
        return super.dispatchTouchEvent(ev);
    }

    private void SpliteViews(int x) {
        mVideoAdapter.setItemBackground(R.drawable.shap_rantage_blue);
        mVideoAdapter.notifyDataSetChanged();
        //主持人在列表中 则将大的broadcasterView的视频加入到receclerview中去  将主持人移动到集合第一个去
        if (mVideoAdapter.isHaveChairMan()) {
            /**TextureView*/
            mVideoAdapter.getAudienceVideoLists().get(mVideoAdapter.getChairManPosition()).setTextureView(remoteBroadcastSurfaceView);
            mVideoAdapter.notifyDataSetChanged();
            if (mCurrentAudienceVideo != null) {
                broadcasterLayout.removeAllViews();
                /**TextureView*/
                stripSurfaceView(mCurrentAudienceVideo.getTextureView());
                mCurrentAudienceVideo.setShowSurface(true);
                mVideoAdapter.getAudienceVideoLists().add(mCurrentAudienceVideo);
                mVideoAdapter.notifyDataSetChanged();
                mCurrentAudienceVideo = null;

            }
        } else {
            //将主持人加入到recyclerView中去

            mLogger.e("remoteBroadcastSurfaceView==null" + (remoteBroadcastSurfaceView == null));
            stripSurfaceView(remoteBroadcastSurfaceView);
            AudienceVideo audienceVideo = new AudienceVideo();
            audienceVideo.setUid(Integer.parseInt(meetingJoin.getHostUser().getClientUid()));
            audienceVideo.setName("主持人" + meetingJoin.getHostUser().getHostUserName());
            audienceVideo.setBroadcaster(true);
            /**TextureView*/
            audienceVideo.setTextureView(remoteBroadcastSurfaceView);
            mVideoAdapter.insertItem(0, audienceVideo);
            broadcasterLayout.removeAllViews();


        }

        if (mVideoAdapter.getPositionById(config().mUid) == -1) {//自己不在列表中
            mLogger.e("自己不再列表中" + Constant.videoType + "---" + currentAudienceId + "----" + config().mUid);
            if (Constant.videoType == 1 || currentAudienceId == config().mUid) {
                stripSurfaceView(localAudienceSurfaceView);
                AudienceVideo audienceVideo = new AudienceVideo();
                audienceVideo.setUid(config().mUid);
                audienceVideo.setName("参会人" + config().mUid);
                audienceVideo.setBroadcaster(false);
                /**TextureView*/
                audienceVideo.setTextureView(localAudienceSurfaceView);
                mVideoAdapter.getAudienceVideoLists().add(audienceVideo);
                mVideoAdapter.notifyDataSetChanged();
            }
        }
        localFrameLayout.removeAllViews();
        localFrameLayout.setVisibility(View.GONE);

        if (!isPPTModel) {
            //将recyclerview变成全屏布局
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mAudienceRecyclerView.getLayoutParams();
            mAudienceRecyclerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            layoutParams.setMargins(0, 0, 0, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);


            findViewById(R.id.relative).setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) findViewById(R.id.relative).getLayoutParams();
            layoutParams1.setMargins(0, 0, 0, 0);
            layoutParams1.height = FrameLayout.LayoutParams.MATCH_PARENT;
            layoutParams1.width = FrameLayout.LayoutParams.MATCH_PARENT;
            findViewById(R.id.relative).setLayoutParams(layoutParams1);

            layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            mAudienceRecyclerView.setLayoutParams(layoutParams);
            mSizeUtils.setViewMatchParent(mAudienceRecyclerView);
        } else {

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(AutoSizeUtils.pt2px(this, 300), RelativeLayout.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            layoutParams.setMargins(0, AutoSizeUtils.pt2px(this, 80), DisplayUtil.dip2px(this, 20), DisplayUtil.dip2px(this, 30));
            mAudienceRecyclerView.setLayoutParams(layoutParams);
            RelativeLayout relative = findViewById(R.id.relative);

            FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) relative.getLayoutParams();
            layoutParams1.setMargins(0, 0, 0, 0);
            relative.setLayoutParams(layoutParams1);
        }


        changeViewLayout();


    }

    private void changeViewLayout() {
        int dataSize = mVideoAdapter.getDataSize();
        findViewById(R.id.relative).setBackground(getResources().getDrawable(R.drawable.bj));
        mLogger.e("集合大小：%d", dataSize);
        mLogger.e("当前currentMaterial==null   " + (currentMaterial == null));
        if (dataSize == 1) {
            if (!isPPTModel) {
                //如果不是分屏模式 需要判断主持人是否在列表中
                if (mVideoAdapter.isHaveChairMan()) {
                    //主持人在列表中 删除主持人
                    int chairManPosition = mVideoAdapter.getChairManPosition();
                    if (chairManPosition != -1) {
                        mVideoAdapter.removeItem(chairManPosition);
                    }
                }
                //将主持人加入到大的view中
                broadcasterLayout.removeAllViews();
                broadcasterLayout.setVisibility(View.VISIBLE);
                if (remoteBroadcastSurfaceView != null) {
                    stripSurfaceView(remoteBroadcastSurfaceView);
                    /**TextureView*/
//                    remoteBroadcastSurfaceView.setZOrderOnTop(false);
//                    remoteBroadcastSurfaceView.setZOrderMediaOverlay(false);
                    broadcasterLayout.addView(remoteBroadcastSurfaceView);
                }
            }
        } else {
            mDelegateAdapter.clear();
            if (currentMaterial == null) {
                mAudienceRecyclerView.removeItemDecoration(mDecor);
                SpaceItemDecoration decor1 = new SpaceItemDecoration(0, 0, 0, 0);
                mAudienceRecyclerView.addItemDecoration(decor1);
                if (mVideoAdapter.getDataSize() == 3) {
                    mAudienceRecyclerView.removeItemDecoration(decor1);
                    mAudienceRecyclerView.removeItemDecoration(mGridSpaceItemDecoration);
                    mAudienceRecyclerView.addItemDecoration(mGridSpaceItemDecoration);
                } else {
                    mAudienceRecyclerView.removeItemDecoration(mGridSpaceItemDecoration);
                }
                if (dataSize == 4 || dataSize == 6) {
                    SpliteGridLayoutHelper gridLayoutHelper = new SpliteGridLayoutHelper(2);
                    mVideoAdapter.setLayoutHelper(gridLayoutHelper);
                    mVideoAdapter.notifyDataSetChanged();
                } else {
                    StaggeredGridLayoutHelper staggeredGridLayoutHelper = new StaggeredGridLayoutHelper(dataSize < 7 ? 2 : 4, this);
                    mVideoAdapter.setLayoutHelper(staggeredGridLayoutHelper);
                    mVideoAdapter.notifyDataSetChanged();
                }
            } else {
                GridLayoutHelper gridLayoutHelper = new GridLayoutHelper(2);
                gridLayoutHelper.setItemCount(8);
                gridLayoutHelper.setAutoExpand(false);
                mAudienceRecyclerView.removeItemDecoration(mDecor);
                mAudienceRecyclerView.addItemDecoration(mDecor);
                mAudienceRecyclerView.removeItemDecoration(mGridSpaceItemDecoration);
                mVideoAdapter.setLayoutHelper(gridLayoutHelper);
            }
            mVideoAdapter.notifyDataSetChanged();
            mDelegateAdapter.addAdapter(mVideoAdapter);
        }
    }


    private void exitSpliteMode() {


        if (Constant.isPadApplicaion) {
            mVideoAdapter.setItemBackground(R.drawable.bg_audience_focused_border);
        } else {
            mVideoAdapter.setItemBackground(R.drawable.bg_audience_border);
        }

        findViewById(R.id.relative).setBackground(null);


        mVideoAdapter.notifyDataSetChanged();
        //将主持人拿出来
        int chairManPosition = mVideoAdapter.getChairManPosition();
        //如果主持人 在的话
        if (chairManPosition != -1) {
            //将主持人添加到大的画面上
            mLogger.e("主持人在列表中");

            broadcasterLayout.setVisibility(View.VISIBLE);
            if (remoteBroadcastSurfaceView != null) {
                remoteBroadcastSurfaceView.setVisibility(View.VISIBLE);
                /**TextureView*/
//                remoteBroadcastSurfaceView.setZOrderOnTop(true);
//                remoteBroadcastSurfaceView.setZOrderMediaOverlay(false);
                broadcasterLayout.removeAllViews();
                stripSurfaceView(remoteBroadcastSurfaceView);
                broadcasterLayout.addView(remoteBroadcastSurfaceView);
            }

            mVideoAdapter.getAudienceVideoLists().remove(chairManPosition);
            mVideoAdapter.notifyDataSetChanged();

        }
        if (mVideoAdapter.getPositionById(config().mUid) == -1) {//自己不在列表中
            mLogger.e("Constant.videoType:-->" + Constant.videoType + "  currentAudienceId:--> " + currentAudienceId + "  config().mUid:-->" + config().mUid);
            if (Constant.videoType == 1 || currentAudienceId == config().mUid) {
                AudienceVideo audienceVideo = new AudienceVideo();
                audienceVideo.setUid(config().mUid);
                audienceVideo.setName("参会人" + meetingJoin.getHostUser().getHostUserName());
                audienceVideo.setBroadcaster(false);
                /**TextureView*/
                audienceVideo.setTextureView(localAudienceSurfaceView);
                mVideoAdapter.getAudienceVideoLists().add(audienceVideo);
                mVideoAdapter.notifyDataSetChanged();
            }
        }
        localFrameLayout.setVisibility(View.GONE);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(AutoSizeUtils.pt2px(this, 300), RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        layoutParams.setMargins(0, AutoSizeUtils.pt2px(this, 80), DisplayUtil.dip2px(this, 20), DisplayUtil.dip2px(this, 30));
        mAudienceRecyclerView.setLayoutParams(layoutParams);

        mDelegateAdapter.clear();

        mAudienceRecyclerView.removeItemDecoration(mDecor);
        mAudienceRecyclerView.addItemDecoration(mDecor);
        mAudienceRecyclerView.removeItemDecoration(mGridSpaceItemDecoration);

        LinearLayoutHelper helper = new LinearLayoutHelper();
        helper.setItemCount(8);
        mVideoAdapter.setLayoutHelper(helper);
        mVideoAdapter.notifyDataSetChanged();
        mDelegateAdapter.addAdapter(mVideoAdapter);

        if (isPPTModel) {
            docImage.setVisibility(View.VISIBLE);
            broadcasterLayout.setVisibility(View.GONE);
        } else {
            broadcasterLayout.setVisibility(View.VISIBLE);
            docImage.setVisibility(View.GONE);
        }

        for (int i = 0; i < mVideoAdapter.getAudienceVideoLists().size(); i++) {
            if (i != 0) {
                if (mVideoAdapter.getAudienceVideoLists().get(i).isShowSurface()) {
                    mVideoAdapter.getAudienceVideoLists().get(i).setShowSurface(false);
                    mVideoAdapter.notifyItemChanged(i);
                }
            } else {
                mVideoAdapter.getAudienceVideoLists().get(0).setShowSurface(true);
                mVideoAdapter.notifyItemChanged(0);
            }
        }

    }

    /**
     * 非全屏状态，画面背景为ppt，主持人 参会人悬浮在ppt内容上
     * <p>
     * **需要判断集合大小，先讲主持人加入到集合中再判断  如果大于8人  就不显示自己  如果小于8人，所以的都显示
     */
    private void notFullScreenState() {

        pptMode = 0;
        full_screen.setText("非全屏");
        if (!mVideoAdapter.isHaveChairMan()) {
            AudienceVideo audienceVideo = new AudienceVideo();
            audienceVideo.setUid(Integer.parseInt(meetingJoin.getHostUser().getClientUid()));
            audienceVideo.setName("主持人" + meetingJoin.getHostUser().getHostUserName());
            audienceVideo.setBroadcaster(true);
            /**TextureView*/
            audienceVideo.setTextureView(remoteBroadcastSurfaceView);
            audienceVideo.setShowSurface(true);
            mVideoAdapter.insertItem(0, audienceVideo);
            mVideoAdapter.notifyDataSetChanged();
        } else {
            /**TextureView*/
            mVideoAdapter.getAudienceVideoLists().get(mVideoAdapter.getChairManPosition()).setTextureView(remoteBroadcastSurfaceView);
            mVideoAdapter.notifyDataSetChanged();
            if (mCurrentAudienceVideo != null) {
                mCurrentAudienceVideo.setShowSurface(false);
                mVideoAdapter.insertItem(mCurrentAudienceVideo);
            }
            mCurrentAudienceVideo = null;
        }

        //如果自己不再列表中 将自己也加入到列表中

        if (mVideoAdapter.getPositionById(config().mUid) == -1) {
            //判断当前连麦人是否是自己或者是否是参会人
            if (Constant.videoType == 1 || currentAudienceId == config().mUid) {
                if (localAudienceSurfaceView != null) {
                    localAudienceSurfaceView.setVisibility(View.VISIBLE);
                }
                AudienceVideo audienceVideo = new AudienceVideo();
                audienceVideo.setUid(config().mUid);
                audienceVideo.setName("参会人" + config().mUid);
                audienceVideo.setBroadcaster(false);
                /**TextureView*/
                audienceVideo.setTextureView(localAudienceSurfaceView);
                mVideoAdapter.getAudienceVideoLists().add(audienceVideo);
                mVideoAdapter.notifyDataSetChanged();
            }
        }

        if (mVideoAdapter.getDataSize() > 8) {
            int myPosition = mVideoAdapter.getPositionById(config().mUid);
            if (myPosition != -1) {
                mVideoAdapter.getAudienceVideoLists().get(myPosition).getSurfaceView().setVisibility(View.GONE);
                mVideoAdapter.removeItem(myPosition);
            }
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(AutoSizeUtils.pt2px(this, 300), RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        layoutParams.setMargins(0, AutoSizeUtils.pt2px(this, 80), DisplayUtil.dip2px(this, 20), DisplayUtil.dip2px(this, 30));
        mAudienceRecyclerView.setLayoutParams(layoutParams);

        mAudienceRecyclerView.removeItemDecoration(mDecor);
        mAudienceRecyclerView.addItemDecoration(mDecor);
        mAudienceRecyclerView.removeItemDecoration(mGridSpaceItemDecoration);

        mDelegateAdapter.clear();
        mVideoAdapter.notifyDataSetChanged();

        LinearLayoutHelper helper = new LinearLayoutHelper();
        helper.setItemCount(8);

        mVideoAdapter.setLayoutHelper(helper);

        mDelegateAdapter.addAdapter(mVideoAdapter);
        mVideoAdapter.notifyDataSetChanged();

        mVideoAdapter.setVisibility(View.VISIBLE);
        mAudienceRecyclerView.setVisibility(View.VISIBLE);

        localFrameLayout.setVisibility(View.GONE);

    }

    /**
     * 全屏状态：画面背景为PPT内容，右下角悬浮自己的画面 悬浮画面可以拖动
     */
    private void FullScreenState() {
        pptMode = 1;
        full_screen.setText("全屏");
        //将自己的画面从列表中移除 然后悬浮
        mAudienceRecyclerView.setVisibility(View.GONE);
        mVideoAdapter.setVisibility(View.GONE);

        if (currentAudienceId != config().mUid && Constant.videoType != 1) {
            return;
        }

//将自己的画面从列表中移除 然后悬浮
        mAudienceRecyclerView.setVisibility(View.GONE);
        mVideoAdapter.setVisibility(View.GONE);

        if (currentAudienceId != config().mUid && Constant.videoType != 1) {
            return;
        }


        int currentAudiencePosition = mVideoAdapter.getPositionById(config().mUid);
        mLogger.e(currentAudiencePosition + "-----" + config().mUid);
        if (currentAudiencePosition != -1) {
            mVideoAdapter.deleteItem(config().mUid);

        }

        mLogger.e(localAudienceSurfaceView == null ? "localSurfaceView == null" : "localSurfaceView != null");

        if (localAudienceSurfaceView == null) {
            return;
        }
        localFrameLayout.removeAllViews();
        localFrameLayout.setVisibility(View.VISIBLE);
        localAudienceSurfaceView.setVisibility(View.VISIBLE);

        /**TextureView*/
//        localAudienceSurfaceView.setZOrderOnTop(true);
//        localAudienceSurfaceView.setZOrderMediaOverlay(true);
        stripSurfaceView(localAudienceSurfaceView);

        localFrameLayout.addView(localAudienceSurfaceView);

    }

    /**
     * 隐藏浮窗状态：画面只有PPT内容；
     */
    private void clearAllState() {
        pptMode = 2;
        full_screen.setText("隐藏浮窗");
        mVideoAdapter.setVisibility(View.GONE);
        mAudienceRecyclerView.setVisibility(View.GONE);

        localFrameLayout.setVisibility(View.GONE);
        if (localAudienceSurfaceView != null) {
            localAudienceSurfaceView.setVisibility(View.GONE);
        }
    }

    private PreviewPlayer player;

    public void PlayVideo() {
        player = findViewById(R.id.jz_video);
//		mPlayVideoText.setVisibility(View.VISIBLE);
//		mPlayVideoText.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (player != null) {
//					if (player.isPlaying()) {
//						player.pausePlay();
//						agoraAPI.channelSetAttr(channelName, Constant.VIDEO, Constant.PAUSEVIDEO);
////						mPlayVideoText.setText("播放");
//					} else {
//						agoraAPI.channelSetAttr(channelName, Constant.VIDEO, Constant.PLAYVIDEO);
//						player.startPlay();
////						mPlayVideoText.setText("暂停播放");
//					}
//				}
//			}
//		});
        if (currentMaterial == null) {
            mLogger.e("currentMaterial == null ");
            findViewById(R.id.app_video_box).setVisibility(View.GONE);
            mVideoContainer.setVisibility(View.GONE);
            return;
        }

        docImage.setVisibility(View.GONE);

        findViewById(R.id.app_video_box).setVisibility(View.VISIBLE);
        mVideoContainer.setVisibility(View.VISIBLE);

        player.setUp(currentMaterial.getMeetingMaterialsPublishList().get(0).getUrl(), "", JzvdStd.SCREEN_NORMAL);
        player.getSeekBar().setVisibility(View.GONE);
        player.getCurrentTimeView().setVisibility(View.GONE);
        player.getTotalTimeView().setVisibility(View.GONE);
        player.getFullScreenView().setVisibility(View.GONE);

        String imageUrl = ImageHelper.videoImageFromUrl(currentMaterial.getMeetingMaterialsPublishList().get(0).getUrl()
                , AutoSizeUtils.dp2px(MeetingAudienceActivity.this, 300)
                , AutoSizeUtils.dp2px(MeetingAudienceActivity.this, 400));
        Picasso.with(MeetingAudienceActivity.this).load(imageUrl)
                .error(R.drawable.item_forum_img_error)
                .placeholder(R.drawable.item_forum_img_loading)
                .into(player.thumbImageView);


    }

    private void stopPlayVideo() {
        if (player != null) {
            JzvdStd.releaseAllVideos();
        }

//		mPlayVideoText.setText("播放");

    }

    private void setAudienceVideoRequestFocus() {
        if (mVideoAdapter != null && mVideoAdapter.getItemCount() > 0) {
            mAudienceRecyclerView.requestFocus();
        }
    }


}
