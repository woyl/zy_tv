package io.agora.openlive.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.opengl.EGLSurface;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.alibaba.android.vlayout.layout.MyGridLayoutHelper;
import com.alibaba.android.vlayout.layout.SpliteGridLayoutHelper;
import com.alibaba.android.vlayout.layout.StaggeredGridLayoutHelper;
import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.dou361.ijkplayer.widget.PlayerView;
import com.herewhite.sdk.Room;
import com.herewhite.sdk.RoomParams;
import com.herewhite.sdk.WhiteSdk;
import com.herewhite.sdk.WhiteSdkConfiguration;
import com.herewhite.sdk.WhiteboardView;
import com.herewhite.sdk.domain.AnimationMode;
import com.herewhite.sdk.domain.DeviceType;
import com.herewhite.sdk.domain.GlobalState;
import com.herewhite.sdk.domain.MemberState;
import com.herewhite.sdk.domain.PptPage;
import com.herewhite.sdk.domain.Promise;
import com.herewhite.sdk.domain.RoomPhase;
import com.herewhite.sdk.domain.SDKError;
import com.herewhite.sdk.domain.Scene;
import com.herewhite.sdk.domain.SceneState;
import com.herewhite.sdk.domain.UrlInterrupter;
import com.herewhite.sdk.domain.WhiteDisplayerState;
import com.serenegiant.usb.USBMonitor;
import com.squareup.picasso.Picasso;
import com.tendcloud.tenddata.TCAgent;
import com.yunos.tv.alitvasrsdk.ASRCommandReturn;
import com.yunos.tv.alitvasrsdk.AliTVASRManager;
import com.yunos.tv.alitvasrsdk.OnASRCommandListener;
import com.zhongyou.meettvapplicaion.ApiClient;
import com.zhongyou.meettvapplicaion.BaseApplication;
import com.zhongyou.meettvapplicaion.BaseException;
import com.zhongyou.meettvapplicaion.BuildConfig;
import com.zhongyou.meettvapplicaion.Constant;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.business.ForumActivity;
import com.zhongyou.meettvapplicaion.business.IMTVChatActivity;
import com.zhongyou.meettvapplicaion.business.adapter.AudienceAdapter;
import com.zhongyou.meettvapplicaion.business.adapter.MaterialAdapter;
import com.zhongyou.meettvapplicaion.business.adapter.MeetingCameraImageAdapter;
import com.zhongyou.meettvapplicaion.business.adapter.NewAudienceVideoAdapter;
import com.zhongyou.meettvapplicaion.entities.Agora;
import com.zhongyou.meettvapplicaion.entities.AudienceVideo;
import com.zhongyou.meettvapplicaion.entities.Bucket;
import com.zhongyou.meettvapplicaion.entities.ConvertFileList;
import com.zhongyou.meettvapplicaion.entities.ForumContent;
import com.zhongyou.meettvapplicaion.entities.ForumRevokeContent;
import com.zhongyou.meettvapplicaion.entities.GetMeetingScreenshot;
import com.zhongyou.meettvapplicaion.entities.HostUser;
import com.zhongyou.meettvapplicaion.entities.Material;
import com.zhongyou.meettvapplicaion.entities.Materials;
import com.zhongyou.meettvapplicaion.entities.Meeting;
import com.zhongyou.meettvapplicaion.entities.MeetingJoin;
import com.zhongyou.meettvapplicaion.entities.MeetingJoinStats;
import com.zhongyou.meettvapplicaion.entities.MeetingMaterialsPublish;
import com.zhongyou.meettvapplicaion.entities.PageData;
import com.zhongyou.meettvapplicaion.entities.PaginationData;
import com.zhongyou.meettvapplicaion.event.ForumActivityCloseEvent;
import com.zhongyou.meettvapplicaion.event.IMMessgeEvent;
import com.zhongyou.meettvapplicaion.im.IMChatMessage;
import com.zhongyou.meettvapplicaion.im.NoPluginTVConversationFragment;
import com.zhongyou.meettvapplicaion.interfaces.CallbackListener;
import com.zhongyou.meettvapplicaion.interfaces.CallbackTwoListener;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.CameraHelper;
import com.zhongyou.meettvapplicaion.utils.DisplayUtil;

import com.zhongyou.meettvapplicaion.utils.HintDialog;
import com.zhongyou.meettvapplicaion.utils.MyDialog;
import com.zhongyou.meettvapplicaion.utils.OkHttpCallback;
import com.zhongyou.meettvapplicaion.utils.RxBus;
import com.zhongyou.meettvapplicaion.utils.SizeUtils;
import com.zhongyou.meettvapplicaion.utils.StringUtils;
import com.zhongyou.meettvapplicaion.utils.TimeUtil;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;
import com.zhongyou.meettvapplicaion.utils.UIDUtil;
import com.zhongyou.meettvapplicaion.utils.helper.ImageHelper;
import com.zhongyou.meettvapplicaion.utils.listener.RecyclerViewBottomScrollListener;
import com.zhongyou.meettvapplicaion.utils.statistics.ZYAgent;
import com.zhongyou.meettvapplicaion.view.FocusFixedLinearLayoutManager;
import com.zhongyou.meettvapplicaion.view.GridSpaceItemDecoration;
import com.zhongyou.meettvapplicaion.view.MyTvListview;
import com.zhongyou.meettvapplicaion.view.PreviewPlayer;
import com.zhongyou.meettvapplicaion.view.RecyclerViewTV;
import com.zhongyou.meettvapplicaion.view.SpaceItemDecoration;
import com.zhongyou.meettvapplicaion.view.manager.UniformRollLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.jzvd.JzvdStd;
import io.agora.AgoraAPI;
import io.agora.AgoraAPIOnlySignal;
import io.agora.openlive.model.AGEventHandler;
import io.agora.openlive.model.EglCore;
import io.agora.openlive.model.ExternalVideoInputManager;

import com.zhongyou.meettvapplicaion.whiteboard.BoardEventListener;
import com.zhongyou.meettvapplicaion.whiteboard.BoardManager;

import io.agora.openlive.model.GlUtil;
import io.agora.openlive.model.ProgramTextureOES;
import io.agora.openlive.videosourse.PrivateTextureHelper;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.mediaio.AgoraSurfaceView;
import io.agora.rtc.mediaio.AgoraTextureCamera;
import io.agora.rtc.mediaio.IVideoSink;
import io.agora.rtc.mediaio.IVideoSource;
import io.agora.rtc.video.AgoraVideoFrame;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.TextMessage;
import me.jessyan.autosize.utils.AutoSizeUtils;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;
import rx.Subscription;
import wendu.dsbridge.DWebView;

import static io.agora.rtc.Constants.REMOTE_AUDIO_REASON_INTERNAL;
import static io.agora.rtc.Constants.REMOTE_AUDIO_REASON_LOCAL_MUTED;
import static io.agora.rtc.Constants.REMOTE_AUDIO_REASON_LOCAL_UNMUTED;
import static io.agora.rtc.Constants.REMOTE_AUDIO_REASON_NETWORK_CONGESTION;
import static io.agora.rtc.Constants.REMOTE_AUDIO_REASON_NETWORK_RECOVERY;
import static io.agora.rtc.Constants.REMOTE_AUDIO_REASON_REMOTE_MUTED;
import static io.agora.rtc.Constants.REMOTE_AUDIO_REASON_REMOTE_OFFLINE;
import static io.agora.rtc.Constants.REMOTE_AUDIO_REASON_REMOTE_UNMUTED;
import static io.agora.rtc.mediaio.MediaIO.BufferType.BYTE_ARRAY;
import static io.agora.rtc.mediaio.MediaIO.BufferType.TEXTURE;
import static io.agora.rtc.mediaio.MediaIO.PixelFormat.I420;
import static io.agora.rtc.mediaio.MediaIO.PixelFormat.TEXTURE_OES;
import static io.agora.rtc.video.VideoCanvas.RENDER_MODE_FILL;
import static io.agora.rtc.video.VideoCanvas.RENDER_MODE_HIDDEN;

public class MeetingBroadcastActivity extends BaseActivity implements AGEventHandler, RecyclerViewTV.OnItemListener, BoardEventListener, SurfaceTexture.OnFrameAvailableListener {

    private final static Logger LOG = LoggerFactory.getLogger(MeetingBroadcastActivity.class);

    private final String TAG = MeetingBroadcastActivity.class.getSimpleName();

    private static final int DEFAULT_VIDEO_TYPE = ExternalVideoInputManager.TYPE_LOCAL_VIDEO;

    private MeetingJoin meetingJoin;
    private Agora agora;
    private Material currentMaterial;
    private int position;
    private HashMap<Integer, AudienceVideo> audienceHashMap = new HashMap<Integer, AudienceVideo>();
    private ArrayList<AudienceVideo> audiences = new ArrayList<AudienceVideo>();

    private String channelName;
    private int memberCount;

    private FrameLayout broadcasterLayout, broadcasterSmailLayout;
    private TextView broadcastNameText, broadcastTipsText;
    private static TextView forum_item_more_broadcast_msg;
    private Button waiterButton, exitButton, stopButton, pptButton, previewButton, nextButton, exitDocButton, muteAudioButton, full_screen, switchCamera;
    private ImageView docImage, forum_item_more_broadcast_msg_img;
    private ImageButton monitorVideoButton;
    private TextView pageText;
//    private SurfaceView localBroadcasterSurfaceView,remoteAudienceSurfaceView;

    /**
     * TextureView
     */
    private TextureView localBroadcasterSurfaceView, remoteAudienceSurfaceView;

    private AudienceVideo currentAudience, newAudience;
    private int currentAiducenceId;

    private List<Button> mButtonList = new ArrayList<>();

    private static final String DOC_INFO = "doc_info";
    private static final String CALLING_AUDIENCE = "calling_audience";
    private static final String MODEL_CHANGE = "model_change";
    private static final String EQUALLY = "equally";
    private static final String BIGSCREEN = "bigScreen";

    /**
     * 本地视图显示模式
     */
    private static final int LOCAL_RENDER_MODE = RENDER_MODE_FILL;


    private boolean isConnecting = false;
    private boolean isFullScreen = false;
    private SpaceItemDecoration mDecor;

    private Subscription subscription;
    private ConstraintLayout constraintLayout_forum_broadcast_newmsg;
    private LinearLayout forum_item_broadcast_more;
    private TextSwitcher textswitcher_forum_item_broadcast_notification;
    private RecyclerView broadcastMeetingCameraRecyclerview;
    private MeetingCameraImageAdapter meetingCameraImageAdapter;
    private UniformRollLayoutManager broadcastMeetingCameraLayoutManager;

    private int meetingCameraImagePlayOutDelay;
    private int lastCompletelyVisibleItemPosition;
    private Timer broadcastMeetingCameraTimer;
    private TimerTask broadcastMeetingCameraTimerTask;
    private PaginationData<GetMeetingScreenshot> paginDataBucketGetMeetingScreenshotData;
    //分页信息
    private final int PAGE_SIZE = 10;
    private final int PAGE_NO = 1;
    private int pageNo = PAGE_NO;
    private RecyclerView mAudienceRecyclerView;
    private VirtualLayoutManager mVirtualLayoutManager;
    private DelegateAdapter mDelegateAdapter;
    private NewAudienceVideoAdapter mVideoAdapter;
    private boolean isSplitMode = false;
    private AudienceVideo mCurrentAudienceVideo;
//    private SurfaceView mAudienceVideoSurfaceView;

    /**
     * TextureView
     */
    private TextureView mAudienceVideoSurfaceView;

    private FrameLayout mBroadCastContainer;
    private Button mSplitView;
    private RelativeLayout mMsgContent;
    private TextView mMsgText;
    private Button mDiscussButton;
    private String mResourceId;
    private String mSid;
    private String mRecordUid;
    private String mMeetingName;
    private View mVideoContainer;
    private Button mPlayButton;
    private GridSpaceItemDecoration mGridSpaceItemDecoration;
    private PlayerView mVideoPreviewPlayler;
    private TextView mCurrentNetSpeed;
    private PreviewPlayer mPreviewPlayer;
    private NoPluginTVConversationFragment mChatFragment;
    private HintDialog mDialog;

    public enum LoadingStatus {LoadingNewMsg, LoadingFirstMsg}

    private long meetingCameraTS = 0;
    private static final int HANDLER_UPDATE_FORUM_TEXT = 1;
    private static final int HANDLER_UPDATE_FORUM_TEXT_NO = 1;
    private static final int HANDLER_UPDATE_FORUM_TEXT_MORE = 2;
    private SizeUtils mSizeUtils;
    private final String TAG_POLLING_REQUEST_NEW_MEETING_CAMERA_IMAGE = "轮询参会人图像：";
    private TextView mNetWorkNumber;
    private TextView mNetworkIcon;


    /**
     * 白板
     */
    private WhiteboardView whiteboardView;
    private String uuid = "";
    private String roomToken = "";
    private final String ROOM_INFO = "room info";
    private final String ROOM_ACTION = "room action";
    private String AppIdentifier = "";
    private Room whiteRoom;
    private WhiteSdk whiteSdk;
    private BoardManager boardManager;

    public final static String SCENE_DIR = "/zhongyouonlie";

    /**
     * 远程视频是否被关闭
     */
    private boolean remoteIsOpen;
    private int onUserMuteVideoUid;

    /**
     * 是否切换过
     */
    private boolean isSwitch;

    /**
     * 0 主持人  1 参会人 2 观众
     */
    private Context context;

    /**
     * 连麦状态
     */
    private ImageView img_line_state;
    private TextView tv_time;
    private Timer timer;
    private TimerTask timerTask;

    /**
     * 和 iOS 名字一致
     */
    private final String EVENT_NAME = "WhiteCommandCustomEvent";

    /**
     * 远程显示
     *
     */

    private RelativeLayout recycleviewContainer;

    private final ForumItemMoreTextHandler forumItemMoreTextHandler = new ForumItemMoreTextHandler(this);

    private void initPageNo() {
        pageNo = PAGE_NO;
    }

    private boolean nextPage() {
        if (pageNo >= paginDataBucketGetMeetingScreenshotData.getTotalPage()) {//没有更多数据了！
            return false;
        }
        pageNo += PAGE_NO;
        return true;
    }


    private View addLocalPreview() {
        // Currently only local video sharing needs
        // a local preview.
        TextureView textureView = BaseApplication.getInstance().localPreview();
        broadcasterLayout.removeAllViews();
        broadcasterLayout.addView(textureView,
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        return textureView;
    }

    @SuppressLint("HandlerLeak")
    private Handler connectingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isConnecting) {
                Toast.makeText(MeetingBroadcastActivity.this, "连麦超时，请稍后再试!", Toast.LENGTH_SHORT).show();

                isConnecting = false;

                agoraAPI.channelDelAttr(channelName, CALLING_AUDIENCE);

                if (currentAudience != null) {
                    mLogger.e("连麦超时……");
                    currentAudience.setCallStatus(0);
                    audienceHashMap.put(currentAudience.getUid(), currentAudience);
                    updateAudienceList();

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("finish", true);
                        agoraAPI.messageInstantSend("" + currentAudience.getUid(), 0, jsonObject.toString(), "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (newAudience != null) {
                    mLogger.e("连麦超时……");
                    newAudience.setCallStatus(0);
                    audienceHashMap.put(newAudience.getUid(), newAudience);
                    updateAudienceList();
                    newAudience = null;
                } else {
                    currentAudience = null;
                }
            }
        }
    };

    private Button mFocusedButton = waiterButton;
    @SuppressLint("HandlerLeak")
    private Handler showToolBarsHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:

                    for (Button focusedButton : mButtonList) {
                        if (focusedButton.isFocused()) {
                            mFocusedButton = focusedButton;
                            break;
                        }

                    }
                    findViewById(R.id.linearLayout2).setVisibility(View.GONE);

                    break;
                case 1:
                    findViewById(R.id.linearLayout2).setVisibility(View.VISIBLE);
                    if (mFocusedButton != null) {
                        mFocusedButton.requestFocus();
                    } else {
                        waiterButton.requestFocus();
                    }
                    showToolBarsHandler.sendEmptyMessageDelayed(0, Constant.delayTime);
                    break;
                case 2:
                    if (mPreviewPlayer != null) {
                        if (mPreviewPlayer.state == JzvdStd.STATE_PLAYING) {
                            mPlayButton.setText("暂停");
                            setTextViewDrawableTop(mPlayButton, R.drawable.bg_meeting_video_pause_selector);
                        } else {
                            mPlayButton.setText("播放");
                            setTextViewDrawableTop(mPlayButton, R.drawable.bg_meeting_video_play_selector);
                        }
                    }
                    showToolBarsHandler.sendEmptyMessageDelayed(2, 1000);
                    break;
                case 3:
                    HintDialog.Builder builder = new HintDialog.Builder(MeetingBroadcastActivity.this);
                    switch (msg.arg1) {
                        case 0:
                            builder.setMessage("本地视频状态正常");
                            break;
                        case 1:
                            builder.setMessage("出错原因不明");
                            break;
                        case 3:
                            builder.setMessage("没有权限启动本地视频采集设备");
                            break;
                        case 4:
                            builder.setMessage("本地视频采集设备正在使用中");
                            break;
                        case 5:
                            builder.setMessage("本地视频编码失败");
                            break;
                    }
                    builder.setTitle("视频启动失败");
                    builder.setPositiveButtonMsg("确定");
                    builder.setOnClickListener(new HintDialog.ClickListener() {
                        @Override
                        public void onClick(int tags) {
                            switch (tags) {
                                case MyDialog.BUTTON_POSITIVE:
                                    mDialog.dismiss();
                                    finish();
                                    break;
                            }
                        }
                    });
                    if (isFinishing()) {
                        return;
                    }
                    mDialog = builder.create();
                    mDialog.show();
                    break;
                case 4:
                    if (MeetingBroadcastActivity.this.isFinishing()) {
                        return;
                    }
                    try {
                        HintDialog.Builder dialogBuilder = new HintDialog.Builder(MeetingBroadcastActivity.this);
                        dialogBuilder
                                .setTitle("网络连接失败")
                                .setMessage("与声网服务器连接断开  请检查网络连接？")
                                .setPositiveButtonMsg("退出")
                                .setOnClickListener(new HintDialog.ClickListener() {
                                    @Override
                                    public void onClick(int tags) {
                                        switch (tags) {
                                            case MyDialog.BUTTON_POSITIVE:
                                                mDialog.dismiss();
                                                doLeaveChannel();
                                                finish();
                                                break;
                                        }
                                    }
                                })
                                .setCancelable(false);
                        mDialog = dialogBuilder.create();
                        mDialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;


            }
        }
    };

    private void setButtonrawableTop(Button view, int drawable) {
        Drawable top = getResources().getDrawable(drawable);
        view.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
    }

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
            String deviceName = "";
            if (usbDevice != null) {
                deviceName = usbDevice.getDeviceName();
            }
            mLogger.e("--- 接收到广播， action: " + action);
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                mLogger.e("USB device is Attached: " + deviceName);
                new Handler().postDelayed(() -> {
                    if (CameraHelper.getNumberOfCameras() > 0) {
                        broadcastTipsText.setVisibility(View.GONE);
                        initUIandEvent();

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
                mLogger.e("USB device is Detached: " + deviceName);
                new Handler().postDelayed(() -> {
                    if (CameraHelper.getNumberOfCameras() <= 0) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(getApplicationContext(), "检测到摄像头被拔出", Toast.LENGTH_SHORT).show();
                        }
                        broadcasterLayout.removeAllViews();
                        broadcastTipsText.setText("未连接摄像头，请重新通话");
                        broadcastTipsText.setVisibility(View.VISIBLE);
                        worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
                    } else {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(getApplicationContext(), "检测到还有" + CameraHelper.getNumberOfCameras() + "个摄像头", Toast.LENGTH_SHORT).show();
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
    private int pptModel = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_broadcast);
        context = this;
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(Constant.KEY_ONMessageArrived);
        filter.addAction(Constant.KEY_IMMESSAGE_RECALL);
        mCurrentNetSpeed = findViewById(R.id.currentSpeed);
        if (BuildConfig.DEBUG) {
            mCurrentNetSpeed.setVisibility(View.GONE);
        } else {
            mCurrentNetSpeed.setVisibility(View.GONE);
        }

        registerReceiver(mUsbReceiver, filter);
        mSizeUtils = new SizeUtils(this);
        registerReceiver(homeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        mDecor = new SpaceItemDecoration(0, 5, 0, 0);
        mGridSpaceItemDecoration = new GridSpaceItemDecoration(DisplayUtil.getHeight(this) / 4);
        TCAgent.onEvent(this, "进入会议直播界面");

		/*try {
			// 注册语音搜索组件
			mAliTVASRManager.init(getBaseContext(), false);
			mAliTVASRManager.setOnASRCommandListener(mASRCommandListener);
		} catch (Exception e) {
			e.printStackTrace();
		}*/

        int i = rtcEngine().enableAudioVolumeIndication(1000, 3, true);
        if (i >= 0) {
            com.orhanobut.logger.Logger.e("启用说话者音量提示：成功" + i);
        } else {
            com.orhanobut.logger.Logger.e("启用说话者音量提示：失败" + i);
        }
        mAudienceRecyclerView = findViewById(R.id.audience_recyclerView);
		/*mGridLayoutHelper = new MyGridLayoutHelper(2);
		mGridLayoutHelper.setHGap(10);
		mGridLayoutHelper.setVGap(10);
		mGridLayoutHelper.setItemCount(8);
		mGridLayoutHelper.setAutoExpand(false);*/



        mAudienceRecyclerView.removeItemDecoration(mDecor);
        mAudienceRecyclerView.removeItemDecoration(mGridSpaceItemDecoration);
        mAudienceRecyclerView.addItemDecoration(mDecor);

        mVirtualLayoutManager = new VirtualLayoutManager(this);
        mDelegateAdapter = new DelegateAdapter(mVirtualLayoutManager, false);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        mAudienceRecyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);
        mAudienceRecyclerView.setLayoutManager(mVirtualLayoutManager);


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, DisplayUtil.dip2px(this, 20), DisplayUtil.dip2px(this, 30));
        mAudienceRecyclerView.setLayoutParams(layoutParams);

        full_screen = findViewById(R.id.full_screen);

        LinearLayoutHelper helper = new LinearLayoutHelper();
        helper.setItemCount(16);
        mVideoAdapter = new NewAudienceVideoAdapter(this, helper);
        mDelegateAdapter.addAdapter(mVideoAdapter);
        mAudienceRecyclerView.setAdapter(mDelegateAdapter);


        mSplitView = findViewById(R.id.splitView);


        mDiscussButton = findViewById(R.id.discuss);


        mMsgContent = findViewById(R.id.msgContent);
        mMsgText = findViewById(R.id.msg);


        showToolBarsHandler.sendEmptyMessageDelayed(0, 5000);

        /**
         * 白板
         */
        whiteboardView = findViewById(R.id.white);
        DWebView.setWebContentsDebuggingEnabled(true);
        whiteboardView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        whiteboardView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            boardManager.refreshViewSize();
            boardManager.scalePptToFit(AnimationMode.Immediately);
//            boardManager.setWritable(true);
//            RectangleConfig rectangleConfig = new RectangleConfig(0d, 0d, 0d, 0d);
//            boardManager.moveCameraToContainer(rectangleConfig);
        });
        boardManager = new BoardManager();
        boardManager.setListener(this);

        /**连麦状态*/
        img_line_state = findViewById(R.id.img_line_state);
        tv_time = findViewById(R.id.tv_time);
        timer = new Timer();


        broadcastTipsText = findViewById(R.id.broadcast_tips);
        broadcastNameText = findViewById(R.id.broadcaster);

        broadcasterLayout = findViewById(R.id.broadcaster_view);
        forum_item_more_broadcast_msg = findViewById(R.id.forum_item_more_broadcast_msg);
        mNetWorkNumber = findViewById(R.id.netWorkNumber);
        mNetworkIcon = findViewById(R.id.networkIcon);
        broadcasterSmailLayout = findViewById(R.id.broadcaster_smail_view);
        mBroadCastContainer = findViewById(R.id.broadcaster_small_view);
        View mRlayout = findViewById(R.id.parentContainer);

        mPlayButton = findViewById(R.id.play);
        docImage = findViewById(R.id.doc_image);
        pageText = findViewById(R.id.page);

        recycleviewContainer = findViewById(R.id.recycleviewContainer);

        setClickListener(mRlayout);

        /**声网配置显示模式*/
        setVideoConfig(DisplayUtil.getWidth(this), DisplayUtil.getHeight(this));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setClickListener(View mRlayout) {
        full_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isUsePPTModel) {

                    if (isSplitMode && currentMaterial == null && !isFullScreen) {
                        ToastUtils.showToast(MeetingBroadcastActivity.this, "均分模式下不能全屏");
                    } else if (currentMaterial != null && !isFullScreen) {//当前在展示ppt
                        mLogger.e("当前在展示ppt  " + !isFullScreen);
                        isFullScreen = true;
                        full_screen.setText("恢复");
                        localBroadcasterSurfaceView.setVisibility(View.GONE);
                        mBroadCastContainer.setVisibility(View.GONE);
                        waiterButton.setVisibility(View.INVISIBLE);
                        stopButton.setVisibility(View.INVISIBLE);
                        mDiscussButton.setVisibility(View.INVISIBLE);
                        pptButton.setVisibility(View.INVISIBLE);
                        muteAudioButton.setVisibility(View.INVISIBLE);
                        mSplitView.setVisibility(View.INVISIBLE);

                        if (Constant.isPadApplicaion) {
                            switchCamera.setVisibility(View.INVISIBLE);
                        } else {
                            switchCamera.setVisibility(View.GONE);
                        }


                        if (badge != null) {
                            badge.setBadgeNumber(0);
                        }


                    } else if (currentMaterial != null && isFullScreen) {
                        mLogger.e("当前在展示ppt  " + isFullScreen);
                        isFullScreen = false;
                        full_screen.setText("全屏");
                        localBroadcasterSurfaceView.setVisibility(View.VISIBLE);
                        mBroadCastContainer.setVisibility(View.VISIBLE);
                        waiterButton.setVisibility(View.VISIBLE);

                        if (currentAudience != null && currentAiducenceId != -1) {
                            stopButton.setVisibility(View.VISIBLE);
                        } else {
                            stopButton.setVisibility(View.INVISIBLE);
                        }

                        mDiscussButton.setVisibility(View.VISIBLE);
                        pptButton.setVisibility(View.VISIBLE);
                        muteAudioButton.setVisibility(View.VISIBLE);
                        if (Constant.isPadApplicaion) {
                            switchCamera.setVisibility(View.VISIBLE);
                        } else {
                            switchCamera.setVisibility(View.GONE);
                        }

                        mSplitView.setVisibility(View.GONE);
                        if (badge != null) {
                            badge.setBadgeNumber(audienceHashMap.size());
                        }
                    } else if (currentMaterial == null && !isFullScreen) {
                        mLogger.e("当前没有在展示ppt  " + !isFullScreen);
                        isFullScreen = true;
                        full_screen.setText("恢复");
                        findViewById(R.id.close_all_audio).setVisibility(View.GONE);
                        mVideoAdapter.setVisibility(View.GONE);
                        mAudienceRecyclerView.setVisibility(View.GONE);
                        waiterButton.setVisibility(View.INVISIBLE);
                        stopButton.setVisibility(View.INVISIBLE);
                        mDiscussButton.setVisibility(View.INVISIBLE);

                        pptButton.setVisibility(View.INVISIBLE);
                        muteAudioButton.setVisibility(View.INVISIBLE);
                        mSplitView.setVisibility(View.INVISIBLE);
                        if (Constant.isPadApplicaion) {
                            switchCamera.setVisibility(View.INVISIBLE);
                        } else {
                            switchCamera.setVisibility(View.GONE);
                        }
                        if (badge != null) {
                            badge.setBadgeNumber(0);
                        }
                    } else if (currentMaterial == null && isFullScreen) {
                        mLogger.e("当前没有在展示ppt  " + isFullScreen);
                        isFullScreen = false;
                        findViewById(R.id.close_all_audio).setVisibility(View.VISIBLE);
                        full_screen.setText("全屏");
                        mVideoAdapter.setVisibility(View.VISIBLE);
                        mAudienceRecyclerView.setVisibility(View.VISIBLE);

                        waiterButton.setVisibility(View.VISIBLE);
                        if (currentAudience != null && currentAiducenceId != -1) {
                            stopButton.setVisibility(View.VISIBLE);
                        } else {
                            stopButton.setVisibility(View.INVISIBLE);
                        }
                        mDiscussButton.setVisibility(View.VISIBLE);
                        pptButton.setVisibility(View.VISIBLE);
                        muteAudioButton.setVisibility(View.VISIBLE);
                        mSplitView.setVisibility(View.VISIBLE);

                        if (Constant.isPadApplicaion) {
                            switchCamera.setVisibility(View.VISIBLE);
                        } else {
                            switchCamera.setVisibility(View.GONE);
                        }

                        if (badge != null) {
                            badge.setBadgeNumber(audienceHashMap.size());
                        }
                    }
                } else {
                    //此时是在使用ppt
                    pptModel++;
                    if (pptModel >= 3) {
                        pptModel = 0;
                    }
                    if (pptModel == 0) {
                        full_screen.setText("非全屏");
                        notFullScreenState();
                    } else if (pptModel == 1) {
                        full_screen.setText("全屏");
                        FullScreenState();
                    } else if (pptModel == 2) {
                        full_screen.setText("隐藏浮窗");
                        clearAllState();
                    }

                }
            }
        });

        mSplitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mVideoAdapter.getDataSize() <= 0) {
                    ToastUtils.showToast("当前还没有参会人 不支持模式切换");
                    return;
                }
                if (isSplitMode) {
                    isSplitMode = false;
                    mSplitView.setText("均分模式");
//					exitSpliteMode();
                    mVideoAdapter.setCanFocusable(true);

                    agoraAPI.channelSetAttr(channelName, MODEL_CHANGE, BIGSCREEN);
                } else {
                    isSplitMode = true;
                    mSplitView.setText("退出均分");
//					SpliteViews();
                    agoraAPI.channelSetAttr(channelName, MODEL_CHANGE, EQUALLY);
                    mVideoAdapter.setCanFocusable(false);


                }
            }
        });

        /**
         *
         * 点击适配器切换视频
         * */
        mVideoAdapter.setOnItemClickListener(new NewAudienceVideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                isSwitch = true;
                int currentIndex = position;
                if (position >= mVideoAdapter.getAudienceVideoLists().size()) {
                    mVideoAdapter.notifyDataSetChanged();
                    return;
                }

                if (remoteIsOpen && !mVideoAdapter.isHaveChairMan()) {
                    ToastUtils.showToast("对方视频已关闭，不能切换哦!");
                    return;
                }

                if (mVideoAdapter.isHaveChairMan()) {
                    //点击的如果是主持人
                    if (mVideoAdapter.getAudienceVideoLists().get(position).isBroadcaster()) {
                        if (mCurrentAudienceVideo != null) {

                            //参会人关闭摄像
                            mCurrentAudienceVideo.setOpenVideo(remoteIsOpen);
                            broadcasterLayout.setBackgroundColor(ContextCompat.getColor(MeetingBroadcastActivity.this, R.color.transparent));
                            broadcasterLayout.setForeground(null);


                            mVideoAdapter.removeItem(position);
                            mVideoAdapter.insertItem(position, mCurrentAudienceVideo);
                            broadcasterLayout.removeAllViews();

                            stripSurfaceView(localBroadcasterSurfaceView);
                            broadcasterLayout.addView(localBroadcasterSurfaceView);
                            /**TextureView*/
//                            localBroadcasterSurfaceView.setZOrderMediaOverlay(true);
                        }
                        mCurrentAudienceVideo = null;
                        mAudienceVideoSurfaceView = null;
                        mVideoAdapter.notifyDataSetChanged();
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
                            /**TextureView*/
                            mAudienceVideoSurfaceView = mCurrentAudienceVideo.getTextureView();

                            mAudienceVideoSurfaceView.postDelayed(() -> {
                                View viewByPosition = mAudienceRecyclerView.getLayoutManager().findViewByPosition(currentIndex);
                                if (viewByPosition != null) {
                                    viewByPosition.requestFocus();
                                }
                            }, 300);
                        }
                    }

                    if (isUsePPTModel) {
                        try {
                            if (!mVideoAdapter.getAudienceVideoLists().get(position).isShowSurface()) {
                                mVideoAdapter.getAudienceVideoLists().get(position).setShowSurface(true);
                                mVideoAdapter.getAudienceVideoLists().get(1).setShowSurface(false);
                                Collections.swap(mVideoAdapter.getAudienceVideoLists(), 1, position);
                                mVideoAdapter.notifyItemRangeChanged(1, position);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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

                if (isSplitMode || isUsePPTModel) {
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


                stripSurfaceView(localBroadcasterSurfaceView);
                //主持人画面 加入到列表中
                AudienceVideo audienceVideo = new AudienceVideo();
                audienceVideo.setUid(config().mUid);
                audienceVideo.setName("主持人" + meetingJoin.getHostUser().getHostUserName());
                audienceVideo.setBroadcaster(true);
                /**TextureView*/
//                audienceVideo.setSurfaceView(localBroadcasterSurfaceView);
                audienceVideo.setTextureView(localBroadcasterSurfaceView);
                mVideoAdapter.insetChairMan(0, audienceVideo);
                View viewByPosition = mAudienceRecyclerView.getLayoutManager().findViewByPosition(position);
                if (viewByPosition != null) {
                    viewByPosition.requestFocus();
                }
            }
        });

        mDiscussButton.setOnClickListener(v -> {
//			Intent forumIntent = new Intent(MeetingBroadcastActivity.this, ForumActivity.class);
            Intent forumIntent = new Intent(MeetingBroadcastActivity.this, IMTVChatActivity.class);
            forumIntent.putExtra("groupId", channelName);
            startActivity(forumIntent);
//			RongIM.getInstance().startConversation(MeetingBroadcastActivity.this, Conversation.ConversationType.GROUP, channelName,meetingJoin.getMeeting().getTitle());
        });

        mDiscussButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (isSplitMode) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                        return true;
                    }
                }
                return false;
            }
        });

        mBroadCastContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (count != 0) {
                            lastX = (int) event.getRawX();
                            lastY = (int) event.getRawY();
                        }
                        count++;
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
            }
        });

    }


    @Override
    public void onRoomPhaseChanged(RoomPhase phase) {

    }

    @Override
    public void onSceneStateChanged(SceneState state) {

    }

    @Override
    public void onMemberStateChanged(MemberState state) {

    }

    @Override
    public void onRoom(Room room) {
        whiteRoom = room;
    }

    boolean isCloseAll = false;

    private void exitSpliteMode() {

        //将主持人拿出来


        recycleviewContainer.setBackground(null);

        int chairManPosition = mVideoAdapter.getChairManPosition();

        //如果主持人 在的话
        if (chairManPosition != -1) {
            //将主持人添加到大的画面上
            broadcasterLayout.setVisibility(View.VISIBLE);
            /**TextureView*/
            localBroadcasterSurfaceView.setVisibility(View.VISIBLE);
//            localBroadcasterSurfaceView.setZOrderOnTop(false);
//            localBroadcasterSurfaceView.setZOrderMediaOverlay(false);
            broadcasterLayout.removeAllViews();
            stripSurfaceView(localBroadcasterSurfaceView);
            broadcasterLayout.addView(localBroadcasterSurfaceView);

            mVideoAdapter.getAudienceVideoLists().remove(chairManPosition);
            mVideoAdapter.notifyDataSetChanged();

        } else {
            // TODO: 2019-11-26 主持人不在
        }

	/*	if (currentMaterial!=null){

			mAudienceRecyclerView.removeItemDecoration(mDecor);
			mAudienceRecyclerView.addItemDecoration(mDecor);
		}else {
			mAudienceRecyclerView.removeItemDecoration(new Span);
			mAudienceRecyclerView.addItemDecoration(mDecor);
		}*/
        if (Constant.isPadApplicaion) {
            mVideoAdapter.setItemBackground(R.drawable.shap_rantage_blue);
        } else {
            mVideoAdapter.setItemBackground(R.drawable.bg_audience_border);
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(AutoSizeUtils.pt2px(this, 300), RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, DisplayUtil.dip2px(this, 20), DisplayUtil.dip2px(this, 30));
        mAudienceRecyclerView.setLayoutParams(layoutParams);

        mAudienceRecyclerView.removeItemDecoration(mDecor);
        mAudienceRecyclerView.removeItemDecoration(mGridSpaceItemDecoration);
        mAudienceRecyclerView.addItemDecoration(mDecor);


        mDelegateAdapter.clear();
        LinearLayoutHelper helper = new LinearLayoutHelper();

        helper.setItemCount(16);
        mVideoAdapter.setLayoutHelper(helper);
        mVideoAdapter.notifyDataSetChanged();
        mDelegateAdapter.addAdapter(mVideoAdapter);

        if (isUsePPTModel) {
            docImage.setVisibility(View.VISIBLE);
            whiteboardView.setVisibility(View.VISIBLE);
            broadcasterLayout.setVisibility(View.GONE);
        } else {
            broadcasterLayout.setVisibility(View.VISIBLE);
            docImage.setVisibility(View.GONE);
            whiteboardView.setVisibility(View.GONE);
        }

        if (mVideoAdapter.getDataSize() >= 1) {
            mAudienceRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mAudienceRecyclerView.setVisibility(View.GONE);
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

    private void SpliteViews() {

        //主持人在列表中 则将大的broadcasterView的视频加入到receclerview中去  将主持人移动到集合第一个去
        mVideoAdapter.setItemBackground(R.drawable.shap_rantage_blue);
        if (mVideoAdapter.isHaveChairMan()) {
            /**TextureView*/
            mVideoAdapter.getAudienceVideoLists().get(mVideoAdapter.getChairManPosition()).setTextureView(localBroadcasterSurfaceView);
            mVideoAdapter.notifyDataSetChanged();
            if (mCurrentAudienceVideo != null) {
                broadcasterLayout.removeAllViews();
                /**TextureView*/
                stripSurfaceView(mCurrentAudienceVideo.getTextureView());
                mCurrentAudienceVideo.setShowSurface(true);
                mVideoAdapter.insertItem(mCurrentAudienceVideo);
                //将主持人移动到集合第一个
							/*int chairManPosition = audienceVideoAdapter.getChairManPosition();
							if (chairManPosition!=-1){
								audienceVideoAdapter.insertItem(0,audienceVideoAdapter.getAudienceVideoLists().get(chairManPosition));
								audienceVideoAdapter.removeItem(chairManPosition+1);
							}*/
                mCurrentAudienceVideo = null;
            }
        } else {
            if (!isUsePPTModel) {
                //将主持人加入到recyclerView中去
                stripSurfaceView(localBroadcasterSurfaceView);
                AudienceVideo audienceVideo = new AudienceVideo();
                audienceVideo.setUid(config().mUid);
                audienceVideo.setName("主持人" + meetingJoin.getHostUser().getHostUserName());
                audienceVideo.setBroadcaster(true);
                /**TextureView*/
                audienceVideo.setTextureView(localBroadcasterSurfaceView);
                mVideoAdapter.insertItem(0, audienceVideo);
                broadcasterLayout.removeAllViews();
            }
        }
        if (!isUsePPTModel) {
            //将recyclerview变成全屏布局
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mAudienceRecyclerView.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            mAudienceRecyclerView.setLayoutParams(layoutParams);
            mSizeUtils.setViewMatchParent(mAudienceRecyclerView);
            mAudienceRecyclerView.removeItemDecoration(mDecor);
            mAudienceRecyclerView.removeItemDecoration(mGridSpaceItemDecoration);
        } else {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(AutoSizeUtils.pt2px(this, 300), RelativeLayout.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, DisplayUtil.dip2px(this, 20), DisplayUtil.dip2px(this, 30));
            mAudienceRecyclerView.setLayoutParams(layoutParams);
        }

        changeViewLayout();
    }

    /**
     * TextureView
     */
    private void stripSurfaceView(TextureView view) {
        if (view == null) {
            com.orhanobut.logger.Logger.e("view==null");
            return;
        }
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((FrameLayout) parent).removeView(view);
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
    private boolean isVideoMonitor;

    private boolean isMirrorView = false;

    private int lastX, lastY;
    int count = 0;

    //medialO
    private IVideoSource mVideoSource;
    private IVideoSink mRender;
    //push 1280 720  640 480
    private static final int DEFAULT_CAPTURE_WIDTH = 1280;
    private static final int DEFAULT_CAPTURE_HEIGHT = 720;

    private int mPreviewTexture;
    private SurfaceTexture mPreviewSurfaceTexture;
    private EglCore mEglCore;
    private EGLSurface mDummySurface;
    private EGLSurface mDrawSurface;
    private ProgramTextureOES mProgram;
    private float[] mTransform = new float[16];
    private float[] mMVPMatrix = new float[16];
    private boolean mMVPMatrixInit = false;

    private Camera mCamera;
    private int mFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private boolean mPreviewing = false;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private boolean mTextureDestroyed;

    private volatile boolean mChannelJoined;
    private int mRemoteWidth;
    private int mRemoteHeight;
    private int mRemoteTop;
    private int mRemoteEnd;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void initUIandEvent() {
        event().addEventHandler(this);

        Intent intent = getIntent();
        agora = intent.getParcelableExtra("agora");
        meetingJoin = intent.getParcelableExtra("meeting");
        ZYAgent.onEvent(getApplicationContext(), "meetingId=" + meetingJoin.getMeeting().getId());

        /**主要获取白板的信息*/
        uuid = meetingJoin.getMeeting().getRoomUUID();
        roomToken = meetingJoin.getMeeting().getRoomToken();

        startTime();

        joinRoom(uuid, roomToken);

        channelName = meetingJoin.getMeeting().getId();
        config().mUid = Integer.parseInt(UIDUtil.generatorUID(Preferences.getUserId()));

//        setVideoSource();
        setVideoPush();

        if ("true".equals(agora.getIsTest())) {
            worker().joinChannel(null, channelName, config().mUid);
        } else {
            worker().joinChannel(agora.getToken(), channelName, config().mUid);
        }

        broadcastNameText.setText("主持人：" + meetingJoin.getHostUser().getHostUserName());


        /**
         * 上一页
         * */
        previewButton = findViewById(R.id.preview);
        previewButton.setOnClickListener(view -> {
            if (currentMaterial != null) {
                if (position > 0) {
                    position--;
//                    stopPlayVideo();

                    switch (currentMaterial.getType()) {
                        case "1":
                            PlayVideo();
                            setTextViewDrawableTop(mPlayButton, R.drawable.bg_meeting_video_play_selector);
                            break;
                        case "0":
                            MeetingMaterialsPublish currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(position);
                            mVideoContainer.setVisibility(View.GONE);
                            mPlayButton.setVisibility(View.GONE);
                            docImage.setVisibility(View.VISIBLE);
                            whiteboardView.setVisibility(View.GONE);
                            String imageUrl = ImageHelper.getThumb(currentMaterialPublish.getUrl());
                            Picasso.with(MeetingBroadcastActivity.this).load(imageUrl).into(docImage);
                            pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");
                            break;
                        case "2":
                            mVideoContainer.setVisibility(View.GONE);
                            mPlayButton.setVisibility(View.GONE);
                            docImage.setVisibility(View.GONE);
                            whiteboardView.setVisibility(View.VISIBLE);
                            if (whiteRoom != null) {
                                int dexIn = whiteRoom.getSceneState().getIndex() - 1;
                                boardManager.setSceneIndex(dexIn);
                            }
                            if (currentMaterial.getConvertFileLists() != null && currentMaterial.getConvertFileLists().size() > 0) {
                                pageText.setText("第" + (position + 1) + "/" + currentMaterial.getConvertFileLists().size() + "页");
                            }
                            break;
                    }
                    pageText.setVisibility(View.VISIBLE);


                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("material_id", currentMaterial.getId());
                        jsonObject.put("doc_index", position);
                        agoraAPI.channelSetAttr(channelName, DOC_INFO, jsonObject.toString());
                        agoraAPI.messageChannelSend(channelName, jsonObject.toString(), "");
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

        previewButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (isSplitMode) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        return true;
                    }
                } else {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        setAudienceVideoRequestFocus();
                        return true;
                    }
                }
                return false;
            }
        });

        switchCamera = findViewById(R.id.switch_camera);
        if (Constant.isPadApplicaion) {
            switchCamera.setVisibility(View.VISIBLE);
        } else {
            switchCamera.setVisibility(View.GONE);
        }

        switchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rtcEngine().switchCamera();
            }
        });

        switchCamera.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (isSplitMode) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                        return true;
                    }
                }
                return false;
            }
        });

        /**下一页*/
        nextButton = findViewById(R.id.next);
        nextButton.setOnClickListener(view -> {
            if (currentMaterial != null) {

//                stopPlayVideo();
                switch (currentMaterial.getType()) {
                    case "1":
                        PlayVideo();
                        setTextViewDrawableTop(mPlayButton, R.drawable.bg_meeting_video_play_selector);
                        break;
                    case "0":
                        if (position < (currentMaterial.getMeetingMaterialsPublishList().size() - 1)) {
                            position++;
                            MeetingMaterialsPublish currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(position);
                            mVideoContainer.setVisibility(View.GONE);
                            mPlayButton.setVisibility(View.GONE);
                            docImage.setVisibility(View.VISIBLE);
                            whiteboardView.setVisibility(View.GONE);
                            String imageUrl = ImageHelper.getThumb(currentMaterialPublish.getUrl());
                            Picasso.with(MeetingBroadcastActivity.this).load(imageUrl).into(docImage);
                            pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");
                        } else {
                            Toast.makeText(this, "当前是最后一张了", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "2":
                        if (currentMaterial.getConvertFileLists() != null && currentMaterial.getConvertFileLists().size() > 0) {
                            if (position < (currentMaterial.getConvertFileLists().size() - 1)) {
                                position++;
                                mVideoContainer.setVisibility(View.GONE);
                                mPlayButton.setVisibility(View.GONE);
                                docImage.setVisibility(View.GONE);
                                whiteboardView.setVisibility(View.VISIBLE);
                                if (whiteRoom != null) {
                                    int dexIn = whiteRoom.getSceneState().getIndex() + 1;
                                    boardManager.setSceneIndex(dexIn);
                                }
                                pageText.setText("第" + (position + 1) + "/" + currentMaterial.getConvertFileLists().size() + "页");
                            } else {
                                Toast.makeText(this, "当前是最后一张了", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                }

                pageText.setVisibility(View.VISIBLE);

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("material_id", currentMaterial.getId());
                    jsonObject.put("doc_index", position);
                    agoraAPI.channelSetAttr(channelName, DOC_INFO, jsonObject.toString());
                    agoraAPI.messageChannelSend(channelName, jsonObject.toString(), "");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(this, "没找到ppt", Toast.LENGTH_SHORT).show();
            }

        });
        nextButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (isSplitMode) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        return true;
                    }
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    setAudienceVideoRequestFocus();
                    return true;
                }
                return false;
            }
        });
        exitDocButton = findViewById(R.id.exit_ppt);
        exitDocButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (isSplitMode) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        return true;
                    }
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    setAudienceVideoRequestFocus();
                    return true;
                }
                return false;
            }
        });
        exitDocButton.setOnClickListener(view -> {

			/*docImage.setVisibility(View.GONE);
			pageText.setVisibility(View.GONE);
			previewButton.setVisibility(View.GONE);
			nextButton.setVisibility(View.GONE);
			exitDocButton.setVisibility(View.GONE);
			mBroadCastContainer.setVisibility(View.GONE);
			broadcasterSmailLayout.removeView(localBroadcasterSurfaceView);
			broadcasterSmailLayout.setVisibility(View.GONE);
			broadcasterLayout.setVisibility(View.VISIBLE);
			broadcasterLayout.removeAllViews();
			stripSurfaceView(localBroadcasterSurfaceView);
			broadcasterLayout.addView(localBroadcasterSurfaceView);
			currentMaterial = null;*/
            exitDoc();

        });

        monitorVideoButton = findViewById(R.id.monitor_video);
        if (meetingJoin.getMeeting().getScreenshotFrequency() == Meeting.SCREENSHOTFREQUENCY_INVALID) {
            monitorVideoButton.setVisibility(View.GONE);
        } else {
            monitorVideoButton.setVisibility(View.GONE);
        }

        //设置默认状态
        monitorVideoButton.setImageResource(R.drawable.ic_monitor_video_off_unfocus);//静音
        monitorVideoButton.setBackground(getResources().getDrawable(R.drawable.bg_meeting_mute_unfocus_selector));

        monitorVideoButton.setOnClickListener(v -> {
            if (isVideoMonitor) {
                isVideoMonitor = false;
                monitorVideoButton.setImageResource(R.drawable.ic_monitor_video_off);
                hideMeetingCameraImageLayout();
            } else {
                isVideoMonitor = true;
                broadcastMeetingCameraRecyclerview.setAdapter(meetingCameraImageAdapter);
                monitorVideoButton.setImageResource(R.drawable.ic_monitor_video_on);
                showMeetingCameraImageLayout();
                requestMeetingCameraImage();
            }
        });
        monitorVideoButton.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (!isVideoMonitor) {
                    monitorVideoButton.setImageResource(R.drawable.ic_monitor_video_off);//静音
                    monitorVideoButton.setBackground(getResources().getDrawable(R.drawable.bg_meeting_mute_selector));
                }
            } else {
                if (!isVideoMonitor) {
                    monitorVideoButton.setImageResource(R.drawable.ic_monitor_video_off_unfocus);//静音
                    monitorVideoButton.setBackground(getResources().getDrawable(R.drawable.bg_meeting_mute_unfocus_selector));
                }
            }
        });

        muteAudioButton = findViewById(R.id.mute_audio);
        muteAudioButton.setOnClickListener(v -> {
            com.orhanobut.logger.Logger.e("点击了音频按钮");
            if (!isMuted) {
                isMuted = true;
                setButtonrawableTop(muteAudioButton, R.drawable.icon_speek_no);
                muteAudioButton.setText("话筒关闭");
                Glide.with(this).load(R.drawable.icon_speek_no_select).into(img_line_state);
            } else {
                isMuted = false;
                setButtonrawableTop(muteAudioButton, R.drawable.icon_speek_select);
                muteAudioButton.setText("话筒打开");
                Glide.with(this).load(R.drawable.icon_speek_select).into(img_line_state);
            }
            rtcEngine().muteLocalAudioStream(isMuted);
        });


        muteAudioButton.setOnFocusChangeListener((v, hasFocus) -> {
//			Log.e(TAG, "音频按钮: " + isMuted);
            if (hasFocus) {
                if (isMuted) {
                    setButtonrawableTop(muteAudioButton, R.drawable.icon_speek_no_select);
                    muteAudioButton.setText("话筒关闭");
//					muteAudioButton.setImageResource(R.drawable.ic_muted);//静音
//					muteAudioButton.setBackground(getResources().getDrawable(R.drawable.bg_meeting_mute_selector));
                } else {
                    setButtonrawableTop(muteAudioButton, R.drawable.icon_speek_select);
                    muteAudioButton.setText("话筒打开");
                }
            } else {
                if (isMuted) {
                    setButtonrawableTop(muteAudioButton, R.drawable.icon_speek_no_new);
                    muteAudioButton.setText("话筒关闭");
//					muteAudioButton.setBackground(getResources().getDrawable(R.drawable.bg_meeting_mute_unfocus_selector));
                } else {
                    setButtonrawableTop(muteAudioButton, R.drawable.icon_speek);
                    muteAudioButton.setText("话筒打开");
                }
            }
        });

        /**
         * 参会人
         * */
        waiterButton = findViewById(R.id.waiter);
        waiterButton.setOnClickListener(view -> {
            if (audiences.size() > 0) {
                showAlertDialog();
            }

        });


        exitButton = findViewById(R.id.exit);
        exitButton.setOnClickListener(view -> {
//            showDialog(1, "确定结束会议吗？", "暂时离开", "结束会议", null);
            showExitDialog();
        });


        stopButton = findViewById(R.id.stop_audience);
        stopButton.setOnClickListener(view -> {
            if (currentAudience != null) {
                showDialog(3, "结束" + currentAudience.getName() + "的发言？", "取消", "确定", currentAudience);
            } else {
                agoraAPI.channelClearAttr(channelName);
                if (currentAiducenceId != 0) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("finish", true);
                        agoraAPI.messageInstantSend("" + currentAiducenceId, 0, jsonObject.toString(), "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        findViewById(R.id.close_all_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCloseAll = true;
                setButtonrawableTop(findViewById(R.id.close_all_audio), R.drawable.icon_close_all);
                agoraAPI.channelSetAttr(channelName, Constant.KEY_ClOSE_MIC, System.currentTimeMillis() + "");
            }
        });

        findViewById(R.id.close_all_audio).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setButtonrawableTop(findViewById(R.id.close_all_audio), R.drawable.icon_close_all_focued);
                } else {
                    if (isCloseAll) {
                        setButtonrawableTop(findViewById(R.id.close_all_audio), R.drawable.icon_close_all);
                    } else {
                        setButtonrawableTop(findViewById(R.id.close_all_audio), R.drawable.icon_close_all_default);
                    }
                }
            }
        });

        findViewById(R.id.close_all_audio).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((isSplitMode) && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.getAction() == KeyEvent.ACTION_DOWN) {

                    setAudienceVideoRequestFocus();
                    return true;
                }
                return false;
            }
        });

        /**
         * 资料按钮
         * */
        pptButton = findViewById(R.id.ppt);
        pptButton.setOnClickListener(view -> {
            ApiClient.getInstance().meetingMaterials(TAG, new OkHttpCallback<Bucket<Materials>>() {
                @Override
                public void onSuccess(Bucket<Materials> materialsBucket) {
                    showPPTListDialog(materialsBucket.getData().getPageData());
                }

                @Override
                public void onFailure(int errorCode, BaseException exception) {
                    super.onFailure(errorCode, exception);
                    Toast.makeText(MeetingBroadcastActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }, meetingJoin.getMeeting().getId());
        });

        doConfigEngine(Constants.CLIENT_ROLE_BROADCASTER);

        constraintLayout_forum_broadcast_newmsg = findViewById(R.id.constraintLayout_forum_broadcast_newmsg);
        textswitcher_forum_item_broadcast_notification = findViewById(R.id.textswitcher_forum_item_broadcast_notification);
        constraintLayout_forum_broadcast_newmsg.setVisibility(View.GONE);

        forum_item_more_broadcast_msg_img = findViewById(R.id.forum_item_more_broadcast_msg_img);
        forum_item_broadcast_more = findViewById(R.id.forum_item_broadcast_more);
        forum_item_broadcast_more.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    forum_item_more_broadcast_msg.setTextColor(Color.WHITE);
                    forum_item_more_broadcast_msg_img.setImageResource(R.drawable.ic_forum_more_msg);
                } else {
                    forum_item_more_broadcast_msg.setTextColor(getResources().getColor(R.color.c_FF909090));
                    forum_item_more_broadcast_msg_img.setImageResource(R.drawable.ic_forum_more_msg_unfocus);
                }
            }
        });
        forum_item_broadcast_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forumIntent = new Intent(MeetingBroadcastActivity.this, ForumActivity.class);
                forumIntent.putExtra(ForumActivity.INTENT_KEY_FORUM, meetingJoin.getMeeting().getId());
                startActivity(forumIntent);
            }
        });

        broadcastMeetingCameraRecyclerview = findViewById(R.id.broadcast_meeting_camera_recyclerview);
        broadcastMeetingCameraLayoutManager = new UniformRollLayoutManager(this);
        broadcastMeetingCameraLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        broadcastMeetingCameraRecyclerview.setLayoutManager(broadcastMeetingCameraLayoutManager);
        broadcastMeetingCameraRecyclerview.setItemAnimator(new DefaultItemAnimator());
        broadcastMeetingCameraRecyclerview.setHasFixedSize(true);
        meetingCameraImageAdapter = new MeetingCameraImageAdapter(getApplicationContext());
        broadcastMeetingCameraRecyclerview.addOnScrollListener(recyclerViewBottomScrollListener);


        // TODO: 2019-12-20 根据usb类型 设置是否翻转画面 rtcEngine().setLocalVideoMirrorMode(Constants.VIDEO_MIRROR_MODE_DISABLED);
        //  另一种思路 判断是否是后置摄像头 或者前置摄像头 前置的话就设置关闭镜像

        mVideoContainer = findViewById(R.id.videoContainer);



        /*设置当为均分模式时  点击向上按钮 不能让列表获取到焦点*/
        waiterButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (isSplitMode) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                        return true;
                    }
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP && (!isSplitMode)) {

                    setAudienceVideoRequestFocus();
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mChatFragment != null && mChatFragment.getView().getVisibility() != View.VISIBLE) {
                        findViewById(R.id.close_discuss).requestFocus();
                    }
                }

                return false;
            }
        });
        stopButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (isSplitMode) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                        return true;
                    }
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP && (!isSplitMode)) {
                    setAudienceVideoRequestFocus();
                    return true;
                }
                return false;
            }
        });

        pptButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (isSplitMode) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                        return true;
                    }
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP && (!isSplitMode)) {
                    setAudienceVideoRequestFocus();
                    return true;
                }
                return false;
            }
        });

        mSplitView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (isSplitMode) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                        return true;
                    }
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                    setAudienceVideoRequestFocus();
                    return true;
                }
                return false;
            }
        });

        full_screen.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (isSplitMode) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                        return true;
                    }
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                    setAudienceVideoRequestFocus();
                    return true;
                }
                return false;
            }
        });

        exitButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (isSplitMode) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        return true;
                    } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                        return true;
                    }
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                    if (isSplitMode) {
                        return true;
                    }
                    setAudienceVideoRequestFocus();
                    return true;
                }
                return false;
            }
        });


        muteAudioButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (isSplitMode) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                        return true;
                    }
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP && (!isSplitMode)) {
                    setAudienceVideoRequestFocus();
                    return true;
                }

                return false;
            }
        });

        mButtonList.add(waiterButton);
        mButtonList.add(exitButton);
        mButtonList.add(stopButton);
        mButtonList.add(pptButton);
        mButtonList.add(previewButton);
        mButtonList.add(nextButton);
        mButtonList.add(exitDocButton);
        mButtonList.add(muteAudioButton);
        mButtonList.add(full_screen);
        mButtonList.add(switchCamera);
        mButtonList.add(mDiscussButton);
        mButtonList.add(mPlayButton);

        /*设置分屏模式 向上按钮被屏蔽 结束*/

        agoraAPI = AgoraAPIOnlySignal.getInstance(this, agora.getAppID());

        agoraAPI.callbackSet(new AgoraAPI.CallBack() {

            @Override
            public void onLoginSuccess(int uid, int fd) {
                super.onLoginSuccess(uid, fd);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        runOnUiThread(() -> Toast.makeText(MeetingBroadcastActivity.this, "信令系统登陆成功", Toast.LENGTH_SHORT).show());
                    }
                    agoraAPI.channelJoin(channelName);

                    //录制视频
                    mLogger.e("是否需要录制：  " + Constant.isNeedRecord);
                    if (Constant.isNeedRecord) {
                        Map<String, Object> map = new HashMap<>();
                        mMeetingName = getIntent().getStringExtra("meetingName");
                        map.put("cname", meetingJoin.getMeeting().getId());
                        map.put("uid", meetingJoin.getHostUser().getClientUid());
                        map.put("clientRequest", "{}");
                        com.zhongyou.meettvapplicaion.net.ApiClient.getInstance().startRecordVideo(this, JSON.toJSONString(map), new com.zhongyou.meettvapplicaion.net.OkHttpCallback<com.alibaba.fastjson.JSONObject>() {
                            @Override
                            public void onSuccess(com.alibaba.fastjson.JSONObject json) {

                                mLogger.e(JSON.toJSONString(json));
                                if (json.getInteger("errcode") != 0) {
                                    ToastUtils.showToast(json.getString("errmsg"));

                                } else {
                                    mResourceId = json.getJSONObject("data").getJSONObject("result").getString("resourceId");
                                    mSid = json.getJSONObject("data").getJSONObject("result").getString("sid");
                                    mRecordUid = json.getJSONObject("data").getJSONObject("result").getString("uid");
                                }
                            }

                            @Override
                            public void onFailure(int errorCode, BaseException exception) {
                                super.onFailure(errorCode, exception);
                                mLogger.e("startRecordVideo", exception.getMessage());
                            }
                        });
                    }

                });

            }

            @Override
            public void onLoginFailed(final int ecode) {
                super.onLoginFailed(ecode);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(MeetingBroadcastActivity.this, "信令系统登陆失败" + ecode, Toast.LENGTH_SHORT).show();
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
                    runOnUiThread(() -> Toast.makeText(MeetingBroadcastActivity.this, "信令重连失败第" + nretry + "次", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onReconnected(int fd) {
                super.onReconnected(fd);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> Toast.makeText(MeetingBroadcastActivity.this, "信令系统重连成功", Toast.LENGTH_SHORT).show());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        agoraAPI.channelJoin(channelName);
                    }
                });
            }

            @Override
            public void onChannelJoined(String channelID) {
                super.onChannelJoined(channelID);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(MeetingBroadcastActivity.this, "加入信令频道成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onChannelJoinFailed(String channelID, int ecode) {
                super.onChannelJoinFailed(channelID, ecode);
                runOnUiThread(() -> {
					/*if (BuildConfig.DEBUG) {
						Toast.makeText(MeetingBroadcastActivity.this, "加入信令频道失败", Toast.LENGTH_SHORT).show();
					}*/
//					showToolBarsHandler.sendEmptyMessage(5);
                });
            }

            @Override
            public void onLogout(int ecode) {
                super.onLogout(ecode);
                runOnUiThread(() -> {
                    showToolBarsHandler.sendEmptyMessage(4);
                });
            }

            @Override
            public void onChannelQueryUserNumResult(String channelID, int ecode, final int num) {
                super.onChannelQueryUserNumResult(channelID, ecode, num);
                runOnUiThread(() -> {
                    memberCount = num;
                    updateAudienceList();
                });
            }

            @Override
            public void onUserAttrResult(String account, String name, String value) {
                mLogger.e("onUserAttrResult------->account==%s,name==%s,value==%s", account, name, value);
                super.onUserAttrResult(account, name, value);
                com.orhanobut.logger.Logger.e("获取到用户  account:" + account + "的属性 name: " + name + "的值为 value:" + value);
                runOnUiThread(() -> {
                    int key = Integer.parseInt(account);
                    if (name.equals("userName") || name.equals("uname")) {
                        if (TextUtils.isEmpty(value)) {
                            return;
                        }

                        if (audienceHashMap.containsKey(key) && !TextUtils.isEmpty(value) && audienceHashMap.get(key) != null) {
                            audienceHashMap.get(key).setUname(value);
                            audienceHashMap.get(key).setName(value);
                        }
                        try {
                            int positionById = mVideoAdapter.getPositionById(Integer.parseInt(account));

                            if (positionById != -1) {
                                AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(positionById);
                                if (audienceVideo != null) {
                                    mVideoAdapter.getAudienceVideoLists().get(positionById).setName(value);
                                    mVideoAdapter.notifyDataSetChanged();
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                    if (BuildConfig.DEBUG) {
                        Toast.makeText(MeetingBroadcastActivity.this, "获取到用户" + account + "的属性" + name + "的值为" + value, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onChannelUserJoined(String account, int uid) {
                super.onChannelUserJoined(account, uid);
                com.orhanobut.logger.Logger.e("onChannelUserJoined   account:" + account + "   uid:" + uid);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(MeetingBroadcastActivity.this, "参会人" + account + "进入信令频道", Toast.LENGTH_SHORT).show();
                    }
                    agoraAPI.getUserAttr(account, "userName");
                    agoraAPI.getUserAttr(account, "uname");
                    if (currentAudience != null) { // 正在连麦
                        agoraAPI.channelSetAttr(channelName, CALLING_AUDIENCE, "" + currentAudience.getUid());
                    } else { // 没有正在连麦
                        agoraAPI.channelDelAttr(channelName, CALLING_AUDIENCE);
                    }
                    if (currentMaterial != null) { //正在演示ppt
                        try {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(MeetingBroadcastActivity.this, "向参会人" + account + "发送ppt信息", Toast.LENGTH_SHORT).show();
                            }
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("material_id", currentMaterial.getId());
                            jsonObject.put("doc_index", position);
                            agoraAPI.channelSetAttr(channelName, DOC_INFO, jsonObject.toString());
                            agoraAPI.messageChannelSend(channelName, jsonObject.toString(), "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else { // 没有在演示ppt
                        agoraAPI.channelDelAttr(channelName, DOC_INFO);
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MeetingBroadcastActivity.this, "参会人" + account + "上来时主持人端没有ppt信息", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onChannelUserLeaved(String account, int uid) {
                super.onChannelUserLeaved(account, uid);
                mLogger.e("用户" + account + "退出信令频道");
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(MeetingBroadcastActivity.this, "参会人" + account + "退出信令频道", Toast.LENGTH_SHORT).show();
                    }
                    AudienceVideo audience = audienceHashMap.remove(Integer.parseInt(account));
                    updateAudienceList();
                    if (audience != null) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(MeetingBroadcastActivity.this, audience.getUname() + "退出信令频道", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }

            @Override
            public void onMessageSendSuccess(String messageID) {
                super.onMessageSendSuccess(messageID);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> Toast.makeText(MeetingBroadcastActivity.this, messageID + "发送成功", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onMessageSendError(String messageID, int ecode) {
                super.onMessageSendError(messageID, ecode);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> Toast.makeText(MeetingBroadcastActivity.this, messageID + "发送失败", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onMessageInstantReceive(final String account, final int uid, final String msg) {
                mLogger.e("account==%s,uid==%d,msg==%s", account, uid, msg);
                super.onMessageInstantReceive(account, uid, msg);
                runOnUiThread(() -> {
                    if (BuildConfig.DEBUG) {
                        runOnUiThread(() -> Toast.makeText(MeetingBroadcastActivity.this, "收到参会人" + account + "发来的消息" + msg, Toast.LENGTH_SHORT).show());
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(msg);
                        if (jsonObject.has("handsUp")) {
                            AudienceVideo audience = JSON.parseObject(jsonObject.toString(), AudienceVideo.class);
                            audience.setUname(TextUtils.isEmpty(audience.getName()) ? (TextUtils.isEmpty(audience.getUname()) ? "" : audience.getUname()) : audience.getName());
                            audience.setName(TextUtils.isEmpty(audience.getName()) ? (TextUtils.isEmpty(audience.getUname()) ? "" : audience.getUname()) : audience.getName());
                            if (jsonObject.has("isAudience") && jsonObject.getBoolean("isAudience") && audience.getCallStatus() == 2) {
                                currentAudience = audience;
                                stopButton.setVisibility(View.VISIBLE);
                            }
                            mLogger.e("收到消息 包含handsUp 放进audienceHashMap里面 ");
                            audienceHashMap.put(audience.getUid(), audience);
                            if (audienceAdapter != null) {
                                audienceAdapter.notifyDataSetChanged();
                            }
                            updateAudienceList();
                            try {
                                int positionById = mVideoAdapter.getPositionById(Integer.parseInt(account));

                                if (positionById != -1) {
                                    AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(positionById);
                                    if (audienceVideo != null) {
                                        mVideoAdapter.getAudienceVideoLists().get(positionById).setName(audienceHashMap.get(audience.getUid()).getName());
                                        mVideoAdapter.notifyDataSetChanged();
                                    }

                                } else {
                                    audienceHashMap.put(audience.getUid(), audience);
                                    updateAudienceList();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            updateAudienceList();
                            if (audience.isHandsUp()) {
                                Toast.makeText(MeetingBroadcastActivity.this, audience.getUname() + "请求发言", Toast.LENGTH_SHORT).show();
                            }
                        }
                        // 终止连麦
                        if (jsonObject.has("finish")) {
                            boolean finish = jsonObject.getBoolean("finish");
                            if (finish) {
                                if (currentAudience != null && account.equals("" + currentAudience.getUid())) {
                                    stopButton.setVisibility(View.INVISIBLE);

                                    agoraAPI.channelDelAttr(channelName, CALLING_AUDIENCE);
                                    audienceHashMap.get(currentAiducenceId).setCallStatus(0);
                                    audienceAdapter.notifyDataSetChanged();

                                    currentAudience = null;
                                    currentAiducenceId = 0;

                                    if (isSplitMode && mVideoAdapter.getAudienceVideoLists().size() <= 1) {
                                        exitSpliteMode();
                                        isSplitMode = false;
                                    }
                                }
                            }
                        }
                        mLogger.e("account:=%s,currentAiducenceId:==%d", account, currentAiducenceId);

						/*if (jsonObject.has("returnInformation")) {
							AudienceVideo audience = JSON.parseObject(jsonObject.toString(), AudienceVideo.class);
							if (Integer.parseInt(account) == currentAiducenceId) {
								mLogger.e("参会人的视频流进入了  更新列表信息" + audience.toString());
								currentAudience = audience;
							}
							mLogger.e("收到消息 包含returnInformation  放进audienceHashMap中" + JSON.toJSONString(audience));
							audienceHashMap.put(audience.getUid(), audience);
							updateAudienceList();
						}*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onChannelAttrUpdated(String channelID, String name, String value, String type) {
                super.onChannelAttrUpdated(channelID, name, value, type);
                mLogger.e("onChannelAttrUpdated:   " + "channelID:" + channelID + "---" + "name:" + name + "---" + "value:" + value + "---" + "type:" + type);
                runOnUiThread(() -> {
                    if (CALLING_AUDIENCE.equals(name)) {
                        if (TextUtils.isEmpty(value)) {
                            if (currentAudience != null) {
                                currentAudience.setCallStatus(0);
                                currentAudience.setHandsUp(false);
                                mLogger.e("收到消息 连麦中断 ");
                                audienceHashMap.put(currentAudience.getUid(), currentAudience);
                                updateAudienceList();

                            }

                            stopButton.setVisibility(View.INVISIBLE);
                            waiterButton.requestFocus();
							/*audienceLayout.removeAllViews();
							audienceNameText.setText("");
							audienceTipsText.setVisibility(View.VISIBLE);*/

                            if (isSplitMode && mVideoAdapter.getAudienceVideoLists().size() <= 1) {
                                exitSpliteMode();
                                isSplitMode = false;
                            }

                        } else {
                            if (currentAiducenceId == Integer.parseInt(value)) {
                                mLogger.e("currentAiducenceId:" + mCurrentAudienceVideo);

                            } else {
                                mLogger.e("value:" + Integer.parseInt(value));
                                mVideoAdapter.deleteItem(currentAiducenceId);
                                currentAiducenceId = Integer.parseInt(value);
                            }


                        }

                        if (isSplitMode) {
                            changeViewLayout();
                        }
                    }

                    if (DOC_INFO.equals(name) && type.equals("update")) {
                        isUsePPTModel = true;
                        nextButton.setVisibility(View.VISIBLE);
                        previewButton.setVisibility(View.VISIBLE);
                        exitDocButton.setVisibility(View.VISIBLE);
                        mSplitView.setVisibility(View.GONE);
                        full_screen.setVisibility(View.VISIBLE);

// channelID:d851a165a68f434fa2578f3f26e93303---name:doc_info---value:{"material_id":"1faedc33b38442f9898d48923ae268fb","doc_index":0}---type:update
                        /**原来需要调用接口展示白板*/
//                        if (currentMaterial == null) {
//                            position = JSON.parseObject(value).getInteger("doc_index");
//                            ApiClient.getInstance().meetingMaterial(TAG, meetingMaterialCallback, JSON.parseObject(value).getString("material_id"));
//                        }

                        changeViewsByPPTModel(null);

						/*if (isSplitMode && !isUsePPTModel) {
							full_screen.setVisibility(View.GONE);
						} else {
							full_screen.setVisibility(View.VISIBLE);
						}*/
                    }

                    if (MODEL_CHANGE.equals(name)) {
                        if (value.equals(BIGSCREEN)) {
                            isSplitMode = false;
                            if (isFullScreen) {
                                mVideoAdapter.setVisibility(View.GONE);
                                mAudienceRecyclerView.setVisibility(View.GONE);
                            } else {
                                mVideoAdapter.setVisibility(View.VISIBLE);
                                mAudienceRecyclerView.setVisibility(View.VISIBLE);
                            }
                            if (!isUsePPTModel) {
                                exitSpliteMode();
                            }
                            pptButton.setVisibility(View.VISIBLE);
                            full_screen.setVisibility(View.VISIBLE);

                        } else if (value.equals(EQUALLY)) {
                            if (mVideoAdapter.getDataSize() < 1) {
                                isSplitMode = false;
                                exitSpliteMode();
                                return;
                            }
                            isSplitMode = true;
                            if (mVideoAdapter.getDataSize() >= 1) {
                                if (!isUsePPTModel) {
                                    SpliteViews();
                                }
                                if (currentMaterial != null) {
                                    full_screen.setVisibility(View.VISIBLE);
                                } else {
                                    full_screen.setVisibility(View.GONE);
                                }
                                pptButton.setVisibility(View.GONE);
                            }
                        }
                    }
                    //关闭ppt演示  保留连麦等
                    if (DOC_INFO.equals(name) && type.equals("del")) {
                        isUsePPTModel = false;
                        docImage.setVisibility(View.GONE);
                        whiteboardView.setVisibility(View.GONE);
                        pageText.setVisibility(View.GONE);
                        previewButton.setVisibility(View.GONE);
                        nextButton.setVisibility(View.GONE);
                        exitDocButton.setVisibility(View.GONE);
                        pptButton.requestFocus();

                        /**
                         * 此前 主播在最右边 清除掉主播父元素，然后在添加到最中间显示
                         * */
						/*mBroadCastContainer.setVisibility(View.GONE);
						broadcasterSmailLayout.removeView(localBroadcasterSurfaceView);
						broadcasterSmailLayout.setVisibility(View.INVISIBLE);
						broadcasterLayout.setVisibility(View.VISIBLE);
						stripSurfaceView(localBroadcasterSurfaceView);
						broadcasterLayout.removeAllViews();
						broadcasterLayout.addView(localBroadcasterSurfaceView);*/
                        currentMaterial = null;
                        exitPPT();

                    }
                    /**
                     * 关闭所有的
                     * */
                    if (TextUtils.isEmpty(name) && TextUtils.isEmpty(value) && type.equals("clear")) {
                        mAudienceRecyclerView.setVisibility(View.VISIBLE);
                        docImage.setVisibility(View.GONE);
                        whiteboardView.setVisibility(View.GONE);
                        pageText.setVisibility(View.GONE);
                        previewButton.setVisibility(View.GONE);
                        nextButton.setVisibility(View.GONE);
                        exitDocButton.setVisibility(View.GONE);
                        mBroadCastContainer.setVisibility(View.GONE);
                        stopButton.setVisibility(View.INVISIBLE);
						/*broadcasterSmailLayout.removeView(localBroadcasterSurfaceView);
						broadcasterSmailLayout.setVisibility(View.INVISIBLE);
						stripSurfaceView(localBroadcasterSurfaceView);
						broadcasterLayout.setVisibility(View.VISIBLE);
						broadcasterLayout.removeAllViews();
						broadcasterLayout.addView(localBroadcasterSurfaceView);*/
                        currentMaterial = null;
                        currentAudience = null;
                        //将连麦的用户删除
                        int positionById = mVideoAdapter.getPositionById(currentAiducenceId);
                        if (positionById != -1) {
                            AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(positionById);
                            audienceVideo.setCallStatus(0);
                            mVideoAdapter.deleteItemById(currentAiducenceId);
                            mVideoAdapter.notifyDataSetChanged();
                            audienceHashMap.get(currentAiducenceId).setCallStatus(0);
                            updateAudienceList();
                        }


                        exitPPT();
                        currentAiducenceId = -1;

                    }

                    if (Constant.KEY_ClOSE_MIC.equals(name)) {
                        isCloseAll = true;
                        setButtonrawableTop(findViewById(R.id.close_all_audio), R.drawable.icon_close_all);
                    }
                });
            }

            @Override
            public void onError(final String name, final int ecode, final String desc) {
                super.onError(name, ecode, desc);
                com.orhanobut.logger.Logger.e("name:" + name + "---" + "ecode:" + ecode + "---" + "desc:" + desc);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(() -> {
                        if (ecode != 208)
                            Toast.makeText(MeetingBroadcastActivity.this, "收到错误信息\nname: " + name + "\necode: " + ecode + "\ndesc: " + desc, Toast.LENGTH_SHORT).show();
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
        ApiClient.getInstance().meetingJoinStats(TAG, meetingJoinStatsCallback(false), params);

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


        try {
            FragmentManager fragmentManage = getSupportFragmentManager();
            mChatFragment = (NoPluginTVConversationFragment) fragmentManage.findFragmentById(R.id.chatFragment);
            mChatFragment.setGroupID(channelName);
            waiterButton.setNextFocusLeftId(mChatFragment.getEditTextID());
        } catch (Exception e) {
            e.printStackTrace();
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

        findViewById(R.id.close_discuss).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mChatFragment != null) {
                        mChatFragment.getConversationAdaterEx().setCanFocused(true);
                    }
                }

                return false;
            }
        });

    }

    private void setVideoPush() {
        mRemoteWidth = getResources().getDimensionPixelSize(R.dimen.remote_preview_width);
        mRemoteHeight = getResources().getDimensionPixelSize(R.dimen.remote_preview_height);
        mRemoteTop = getResources().getDimensionPixelSize(R.dimen.remote_preview_margin_top);
        mRemoteEnd = getResources().getDimensionPixelSize(R.dimen.remote_preview_margin_end);
        rtcEngine().setExternalVideoSource(true, true, true);
    }

    private void setVideoSource() {
        mVideoSource = new AgoraTextureCamera(this, 1280, 720);
        localBroadcasterSurfaceView = new TextureView(this);
        broadcasterLayout.addView(localBroadcasterSurfaceView);
        mRender = new PrivateTextureHelper(this, localBroadcasterSurfaceView);
        ((PrivateTextureHelper) mRender).init(((AgoraTextureCamera) mVideoSource).getEglContext());
        ((PrivateTextureHelper) mRender).setBufferType(TEXTURE);
        ((PrivateTextureHelper) mRender).setPixelFormat(TEXTURE_OES);

        worker().getRtcEngine().setVideoSource(mVideoSource);
        worker().getRtcEngine().setLocalVideoRenderer(mRender);
        worker().preview2(true, null, config().mUid);
    }

    private void exitDoc() {
        mPlayButton.setVisibility(View.GONE);

        position = 0;

        if (mPreviewPlayer != null) {
            JzvdStd.releaseAllVideos();
        }

        mPlayButton.setText("播放");
        setTextViewDrawableTop(mPlayButton, R.drawable.bg_meeting_video_play_selector);

        findViewById(R.id.app_video_box).setVisibility(View.GONE);

//        agoraAPI.channelSetAttr(channelName, Constant.VIDEO, Constant.PAUSEVIDEO);
//        agoraAPI.channelSetAttr(channelName, Constant.VIDEO, Constant.STOPVIDEO);


        localBroadcasterSurfaceView.setVisibility(View.VISIBLE);

        mVideoContainer.setVisibility(View.GONE);

        agoraAPI.channelDelAttr(channelName, DOC_INFO);
    }

    private void startTime() {
        long updateTime = System.currentTimeMillis();
        long startTime = TimeUtil.getStringToDate(meetingJoin.getMeeting().getCreateDate(), "yyyy-MM-dd HH:mm:ss");
        long pastTime = updateTime - startTime;
        timerTask = new TimerTask() {
            long count = 0;

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_time.setText(getStringTime(pastTime + count++));
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 1000);

    }

    private String getStringTime(long cnt) {
        if (cnt >= 360000000) {
            return "99:00:00";
        }
        long hour = cnt / 3600 / 1000;
        long min = cnt % 3600 / 60;
        long second = cnt % 60;
        return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, min, second);
    }

    private void stopTime() {
        if (timerTask != null && !timerTask.cancel()) {
            timerTask.cancel();
            timer.cancel();
        }
    }


    public void setVideoConfig(int width, int height) {

        VideoEncoderConfiguration.ORIENTATION_MODE mode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_LANDSCAPE;
        VideoEncoderConfiguration videoEncoderConfiguration = new VideoEncoderConfiguration(
                new VideoEncoderConfiguration.VideoDimensions(width, height),
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE, mode
        );
        videoEncoderConfiguration.mirrorMode = Constants.VIDEO_MIRROR_MODE_ENABLED;
        rtcEngine().setVideoEncoderConfiguration(videoEncoderConfiguration);
    }


    private Dialog exitDialog;

    private void showExitDialog() {
        View contentView = View.inflate(this, R.layout.dialog_exit_meeting, null);
        TextView finishTips = contentView.findViewById(R.id.finish_meeting_tips);
        Button tempLeaveButton = contentView.findViewById(R.id.left);
        tempLeaveButton.setOnClickListener(view -> {
            exitDialog.cancel();
            if (currentAudience != null) {
                agoraAPI.channelDelAttr(channelName, CALLING_AUDIENCE);

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("finish", true);
                    agoraAPI.messageInstantSend("" + currentAudience.getUid(), 0, jsonObject.toString(), "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(this, "当前没有连麦人", Toast.LENGTH_SHORT).show();
                }
                if (currentAiducenceId != 0) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("finish", true);
                        agoraAPI.messageInstantSend("" + currentAiducenceId, 0, jsonObject.toString(), "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("clientUid", "" + config().mUid);
            params.put("hostUserId", Preferences.getUserId());
            params.put("hostUserName", meetingJoin.getHostUser().getHostUserName());
            params.put("status", 2);
            stopRecordVideo();
            ApiClient.getInstance().meetingLeaveTemp(TAG, meetingTempLeaveCallback, meetingJoin.getMeeting().getId(), params);
            agoraAPI.channelDelAttr(channelName, DOC_INFO);
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
            agoraAPI.channelDelAttr(channelName, DOC_INFO);
        });

        exitDialog = new Dialog(this, R.style.MyDialog);
        exitDialog.setContentView(contentView);
        exitDialog.show();

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
                            forum_item_more_broadcast_msg.setText("添加评论");
                        } else if (msg.arg1 == HANDLER_UPDATE_FORUM_TEXT_MORE) {
                            forum_item_more_broadcast_msg.setText("展开评论");
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
        return pageData.getMeetingId().equals(meetingJoin.getMeeting().getId());
    }

    private void requestRecord() {
        this.requestRecord(null);
    }

    /**
     * 请求讨论区信息
     */
    private void requestRecord(PageData pageData) {
        if (pageData != null) {
            //非第一次进入直播室，走讨论区的socket请求
            if (isCurrentMeeting(pageData)) {
                showTextSwitcherNormalContent(pageData);
            }
        } else {
            Map<String, String> params = new HashMap<>();
            params.put(ApiClient.PAGE_NO, "1");
            params.put(ApiClient.PAGE_SIZE, "10");
            params.put("meetingId", meetingJoin.getMeeting().getId());
            ApiClient.getInstance().getForumContent(this, params, new OkHttpCallback<Bucket<ForumContent>>() {
                @Override
                public void onSuccess(Bucket<ForumContent> forumContentBucket) {
                    ZYAgent.onEvent(getApplicationContext(), "主持人界面获得到讨论区信息历史记录");
                    //OSG-445，根据需求再次修改，默认入口变回展示状态，并添加默认文字，作为讨论区入口
                    ForumContent forumContent = forumContentBucket.getData();
                    Message message = new Message();
                    message.what = HANDLER_UPDATE_FORUM_TEXT;

                    if (forumContent.getTotalCount() <= 0) {
                        message.arg1 = HANDLER_UPDATE_FORUM_TEXT_NO;
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
     * 显示会议图片功能
     */
    private void requestMeetingCameraImage() {
        Map<String, String> params = new HashMap<>();
        params.put("meetingId", meetingJoin.getMeeting().getId());
        params.put("ts", String.valueOf(Preferences.getMeetingCameraLastTs()));
        params.put(ApiClient.PAGE_NO, String.valueOf(pageNo));
        params.put(ApiClient.PAGE_SIZE, String.valueOf(PAGE_SIZE));

        ApiClient.getInstance().meetingGetScreenshot(getApplicationContext(), params, meetingGetFirstScreenshotCallback);
    }

    /**
     * 会议图片展示分页数据回调函数
     */
    private OkHttpCallback<Bucket<PaginationData<GetMeetingScreenshot>>> meetingGetFirstScreenshotCallback = new OkHttpCallback<Bucket<PaginationData<GetMeetingScreenshot>>>() {

        @Override
        public void onSuccess(Bucket<PaginationData<GetMeetingScreenshot>> paginDataBucket) {
            paginDataBucketGetMeetingScreenshotData = paginDataBucket.getData();
            if (paginDataBucketGetMeetingScreenshotData.getTotalCount() <= 0) {
                ZYAgent.onEvent(getApplicationContext(), TAG_POLLING_REQUEST_NEW_MEETING_CAMERA_IMAGE + "暂无参会人图像信息");
                ToastUtils.showToast("暂无参会人图像信息");
                startPollingRequestNewMeetingCameraImage();
                return;
            }
            ArrayList<GetMeetingScreenshot> pageData = paginDataBucketGetMeetingScreenshotData.getPageData();
            ZYAgent.onEvent(getApplicationContext(), TAG_POLLING_REQUEST_NEW_MEETING_CAMERA_IMAGE + "接收到最新会议图像");
            getLastMeetingCameraImageTS(pageData);
            meetingCameraImageAdapter.addData(pageData);
            startRollMeetingImageTimer();
            startPollingRequestNewMeetingCameraImage();
        }
    };

    private boolean pollingRequestNewMeetingCameraImageTimerRunning;
    private Timer pollingRequestNewMeetingCameraImageTimer;
    private TimerTask pollingRequestNewMeetingCameraImageTimerTask;

    private boolean rollMeetingImageTimerRunning;

    /**
     * 轮询新会议图像任务
     */
    private void startPollingRequestNewMeetingCameraImage() {
        if (pollingRequestNewMeetingCameraImageTimerRunning) {//正在启动中，无需再次启动timer轮询
            return;
        }
//        //MockData，时间间隔缩短，5s一次请求
//        final int requestPeriod = 5 * 1000;
        final int requestPeriod = 10 * 1000;//根据新需求，轮询时间由60s，更改为10s
        ZYAgent.onEvent(getApplicationContext(), TAG_POLLING_REQUEST_NEW_MEETING_CAMERA_IMAGE + "开始轮询会议图像");

        pollingRequestNewMeetingCameraImageTimer = new Timer();
        pollingRequestNewMeetingCameraImageTimerTask = new TimerTask() {
            @Override
            public void run() {
                requestMeetingCameraImage();
            }
        };
        pollingRequestNewMeetingCameraImageTimer.schedule(pollingRequestNewMeetingCameraImageTimerTask, 0, requestPeriod);
        pollingRequestNewMeetingCameraImageTimerRunning = true;//轮询服务正在进行
    }

    /**
     * 停止轮询新会议图像任务
     */
    private void stopPollingRequestNewMeetingCameraImage() {
        ZYAgent.onEvent(getApplicationContext(), TAG_POLLING_REQUEST_NEW_MEETING_CAMERA_IMAGE + "停止轮询新会议图像任务");
        pollingRequestNewMeetingCameraImageTimerRunning = false;//停止轮询新图像
        if (pollingRequestNewMeetingCameraImageTimer != null) {
            pollingRequestNewMeetingCameraImageTimer.cancel();
        }
        if (pollingRequestNewMeetingCameraImageTimerTask != null) {
            pollingRequestNewMeetingCameraImageTimerTask.cancel();
        }
        pollingRequestNewMeetingCameraImageTimer = null;
        pollingRequestNewMeetingCameraImageTimerTask = null;
    }

    /**
     * 获取最后图片的TS时间
     *
     * @param pageData
     */
    private void getLastMeetingCameraImageTS(ArrayList<GetMeetingScreenshot> pageData) {
        GetMeetingScreenshot getMeetingScreenshot = pageData.get(pageData.size() - 1);
        meetingCameraTS = getMeetingScreenshot.getTs();
        //存储收到的最后图像的时间戳
        Preferences.setMeetingCameraLastTs(meetingCameraTS);
        ZYAgent.onEvent(getApplicationContext(), TAG_POLLING_REQUEST_NEW_MEETING_CAMERA_IMAGE + "最后会议图像的时间戳：" + meetingCameraTS);
    }

    /**
     * 隐藏参会人会议图片视图
     */
    private void hideMeetingCameraImageLayout() {
        broadcastMeetingCameraRecyclerview.setVisibility(View.GONE);
        meetingCameraImageAdapter.clearData();

        lastCompletelyVisibleItemPosition = 0;
        meetingCameraImagePlayOutDelay = 0;
        initPageNo();

        stopRollMeetingImageTimer();
        stopPollingRequestNewMeetingCameraImage();
    }

    /**
     * 显示会议图像视图布局
     */
    private void showMeetingCameraImageLayout() {

        broadcastMeetingCameraRecyclerview.setVisibility(View.VISIBLE);
//        //MockData, 测试数据，将时间设置为2s
//        meetingCameraImagePlayOutDelay = 2000;
    }

    /**
     * 停止滚动会议图像
     */
    private void stopRollMeetingImageTimer() {
        rollMeetingImageTimerRunning = false;
        if (broadcastMeetingCameraTimer != null) {
            broadcastMeetingCameraTimer.cancel();
        }
        if (broadcastMeetingCameraTimerTask != null) {
            broadcastMeetingCameraTimerTask.cancel();
        }
        broadcastMeetingCameraTimer = null;
        broadcastMeetingCameraTimerTask = null;
        ZYAgent.onEvent(getApplicationContext(), TAG_POLLING_REQUEST_NEW_MEETING_CAMERA_IMAGE + "停止会议图像滚动播放");
    }

    /**
     * 开始滚动会议图像
     */
    private void startRollMeetingImageTimer() {
        if (rollMeetingImageTimerRunning) {
            return;
        }
        broadcastMeetingCameraTimer = new Timer();
        broadcastMeetingCameraTimerTask = new TimerTask() {
            @Override
            public void run() {
                broadcastMeetingCameraImageHandler.sendEmptyMessage(1);
            }
        };
        //如果播放时间间隔是0毫秒，则默认设置播放间隔，为3000毫秒
        meetingCameraImagePlayOutDelay = meetingJoin.getMeeting().getScreenshotScrollFrequency() == 0 ? UniformRollLayoutManager.NORMAL_TIME : meetingJoin.getMeeting().getScreenshotScrollFrequency();
        broadcastMeetingCameraLayoutManager.setSmoothTime(meetingCameraImagePlayOutDelay);

        broadcastMeetingCameraTimer.schedule(broadcastMeetingCameraTimerTask, 0, meetingCameraImagePlayOutDelay);
        ZYAgent.onEvent(getApplicationContext(), TAG_POLLING_REQUEST_NEW_MEETING_CAMERA_IMAGE + "开启会议图像滚动播放");
        rollMeetingImageTimerRunning = true;
    }


    /**
     * 更新会议滚动条目
     */
    private Handler broadcastMeetingCameraImageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (lastCompletelyVisibleItemPosition == 0) {
                lastCompletelyVisibleItemPosition = broadcastMeetingCameraLayoutManager.findLastCompletelyVisibleItemPosition();
            }
            broadcastMeetingCameraRecyclerview.smoothScrollToPosition(lastCompletelyVisibleItemPosition);
            ZYAgent.onEvent(getApplicationContext(), TAG_POLLING_REQUEST_NEW_MEETING_CAMERA_IMAGE + "当前可视区域会议图像下角标：" + lastCompletelyVisibleItemPosition);
            lastCompletelyVisibleItemPosition++;
        }
    };

    /**
     * 当滚动会议图像到底部时回调监听
     */
    private RecyclerViewBottomScrollListener recyclerViewBottomScrollListener = new RecyclerViewBottomScrollListener() {
        @Override
        public void onScrollToBottom() {
            ZYAgent.onEvent(getApplicationContext(), TAG_POLLING_REQUEST_NEW_MEETING_CAMERA_IMAGE + "会议图像滚动到底部");
            stopRollMeetingImageTimer();
            startPollingRequestNewMeetingCameraImage();//轮询新会议图像任务

            //经与需求再次讨论，当会议图像没有更多，停止请求服务器数据，停止滚动播放
            if (nextPage()) {
                ZYAgent.onEvent(getApplicationContext(), TAG_POLLING_REQUEST_NEW_MEETING_CAMERA_IMAGE + "再次分页请求会议图像的最新数据");
                requestMeetingCameraImage();
            }
        }
    };

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
        textswitcher_forum_item_broadcast_notification.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_bottom));
        textswitcher_forum_item_broadcast_notification.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_top));
        textswitcher_forum_item_broadcast_notification.removeAllViews();
        textswitcher_forum_item_broadcast_notification.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(getApplicationContext());
                textView.setSingleLine();
                textView.setTextSize(24);//字号
                textView.setTextColor(getResources().getColor(R.color.white));
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setSingleLine();
                textView.setGravity(Gravity.CENTER_VERTICAL);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
//
//
//			FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mMsgContent.getLayoutParams();
//			layoutParams.setMargins(mDiscussButton.getLeft() + mDiscussButton.getWidth() / 4, 0, 0, DisplayUtil.dip2px(this, 60));
//			mMsgContent.setLayoutParams(layoutParams);
//
//			if (msgHandler.hasMessages(1)) {
//				msgHandler.removeMessages(1);
//			}
//
//			msgHandler.sendEmptyMessageDelayed(1, 3000);
//
//			textswitcher_forum_item_broadcast_notification.setText(msg);
//			textswitcher_forum_item_broadcast_notification.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_bottom));
//			textswitcher_forum_item_broadcast_notification.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_top));
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


    private Dialog dialog;

    private void showDialog(final int type, final String title, final String leftText, final String rightText, final AudienceVideo audience) {
        View view = View.inflate(this, R.layout.dialog_selector, null);
        TextView titleText = view.findViewById(R.id.title);
        titleText.setText(title);

        Button leftButton = view.findViewById(R.id.left);
        leftButton.setText(leftText);
        leftButton.setOnClickListener((View) -> {
            dialog.cancel();
            if (type == 1) {

            } else if (type == 2 || type == 5) {
                isConnecting = true;
                connectingHandler.sendEmptyMessageDelayed(0, 10000);
                currentAudience = audience;
                currentAudience.setCallStatus(1);
                currentAudience.setHandsUp(false);
                mLogger.e("设置连麦属性为正在连麦");
                audienceHashMap.put(currentAudience.getUid(), currentAudience);
                updateAudienceList();
//				audienceNameText.setText(currentAudience.getUname());

                agoraAPI.channelSetAttr(channelName, CALLING_AUDIENCE, "" + currentAudience.getUid());
            }
        });

        Button rightButton = view.findViewById(R.id.right);
        rightButton.setText(rightText);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                if (type == 1) {

                } else if (type == 2) {
                    audience.setCallStatus(0);
                    audience.setHandsUp(false);
                    mLogger.e("设置连麦属性 为关闭连麦");
                    audienceHashMap.put(audience.getUid(), audience);
                    updateAudienceList();
                    stopButton.setVisibility(View.INVISIBLE);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("response", false);
                        agoraAPI.messageInstantSend("" + audience.getUid(), 0, jsonObject.toString(), "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (type == 3) {
                    audience.setCallStatus(0);
                    audience.setHandsUp(false);
                    mLogger.e("设置连麦属性 为关闭连麦");
                    audienceHashMap.put(audience.getUid(), audience);
                    updateAudienceList();

                    stopButton.setVisibility(View.INVISIBLE);

					/*audienceLayout.removeAllViews();
					audienceTipsText.setVisibility(View.VISIBLE);
					audienceNameText.setText("");*/

                    currentAudience = null;
                    agoraAPI.channelDelAttr(channelName, CALLING_AUDIENCE);

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("finish", true);
                        agoraAPI.messageInstantSend("" + audience.getUid(), 0, jsonObject.toString(), "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (type == 4) {


                    newAudience = audience;

                    isConnecting = true;
                    connectingHandler.sendEmptyMessageDelayed(0, 10000);

                    if (currentAudience != null) {
                        currentAudience.setCallStatus(0);
                        currentAudience.setHandsUp(false);
                        mLogger.e("设置连麦属性 为关闭连麦" + currentAudience.getUname());
                        audienceHashMap.put(currentAudience.getUid(), currentAudience);

                        newAudience.setCallStatus(1);
                        newAudience.setHandsUp(false);
                        mLogger.e("设置连麦属性 为正在连麦" + newAudience.getUname());
                        audienceHashMap.put(newAudience.getUid(), newAudience);

                        updateAudienceList();
/*
						audienceLayout.removeAllViews();
						audienceNameText.setText("");
						audienceTipsText.setVisibility(View.VISIBLE);*/

                        stopButton.setVisibility(View.INVISIBLE);

                        agoraAPI.channelSetAttr(channelName, CALLING_AUDIENCE, "" + newAudience.getUid());
                    }
                }
            }
        });

        dialog = new Dialog(this, R.style.MyDialog);
        dialog.setContentView(view);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = DisplayUtil.dip2px(this, 740);
        lp.height = DisplayUtil.dip2px(this, 480);
        dialogWindow.setAttributes(lp);

        dialog.show();
    }

    private OkHttpCallback meetingTempLeaveCallback = new OkHttpCallback<Bucket>() {

        @Override
        public void onSuccess(Bucket meetingTempLeaveBucket) {
            Log.v("meetingTempLeave", meetingTempLeaveBucket.toString());
        }
    };

    AlertDialog alertDialog, pptAlertDialog, pptDetailDialog;
    AudienceAdapter audienceAdapter;
    private TextView audienceCountText;
    EditText searchEdit;
    Button searchButton;

    /**
     * 参会人
     */
    @SuppressLint("SetTextI18n")
    private void showAlertDialog() {
        View view;
        if (Constant.isPadApplicaion) {
            view = View.inflate(this, R.layout.dialog_audience_list_pad, null);
        } else {
            view = View.inflate(this, R.layout.dialog_audience_list, null);
        }

        audienceCountText = view.findViewById(R.id.audience_count);
        audienceCountText.setText("所有参会人 (" + audiences.size() + ")");


        searchEdit = view.findViewById(R.id.search_edit);

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Constant.isPadApplicaion) {
                    if (!TextUtils.isEmpty(s)) {
                        searchButton.setFocusable(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchButton = view.findViewById(R.id.search_button);
        searchButton.setOnClickListener((view1) -> {
            if (TextUtils.isEmpty(searchEdit.getText())) {
                Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show();
            } else {
                if (audienceAdapter != null) {

                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                    audienceAdapter.setData(searchAudiences(audiences, searchEdit.getText().toString()));
                }
            }
        });

        MyTvListview listView = view.findViewById(R.id.list_view);
        if (audienceAdapter == null) {
            audienceAdapter = new AudienceAdapter(this, audiences);
        } else {
            audienceAdapter.setData(audiences);
        }
        listView.setAdapter(audienceAdapter);
        listView.setItemsCanFocus(true);
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                TextView stop_speak = view.findViewById(R.id.stop_speak);
                stop_speak.setTextColor(getResources().getColor(R.color.red));
                stop_speak.setFocusable(true);
                stop_speak.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                listView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
            }
        });


        handler.sendEmptyMessageDelayed(101, 500);

        audienceAdapter.setCallbackListener(new CallbackListener<Boolean>() {
            @Override
            public void callback(Boolean aBoolean) {
                if (aBoolean) {
                    searchEdit.setFocusable(true);
                    searchEdit.requestFocus();
                }
            }
        });

        audienceAdapter.setRespontListences(new CallbackTwoListener<Integer, Integer>() {
            @Override
            public void callback(Integer position, Integer object) {
                if (isConnecting) {
                    Toast.makeText(context, "暂时无法切换连麦，请10秒后尝试", Toast.LENGTH_SHORT).show();
                } else {
                    AudienceVideo audience = (AudienceVideo) audienceAdapter.getItem(position);
                    if (currentAudience != null) {
                        if (currentAudience.getCallStatus() == 2 && currentAudience.getUid() != audience.getUid()) {
                            showDialog(4, "中断当前" + currentAudience.getName() + "的连麦，连接" + audience.getName() + "的连麦", "取消", "确定", audience);
                        } else {
                            Toast.makeText(context, "正在与当前参会人连麦中", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (audience.getCallStatus() == 0) {
                            if (audience.isHandsUp()) {
                                showDialog(2, audience.getName() + "请求连麦", "接受", "拒绝", audience);
                            } else {
                                showDialog(5, "确定与" + audience.getName() + "连麦", "确定", "取消", audience);
                            }
                        } else {
                            Toast.makeText(context, "正在与当前参会人连麦中", Toast.LENGTH_SHORT).show();
                        }
                    }
                    alertDialog.cancel();
                }
            }
        });

        /**
         *
         * 禁言
         * */
        audienceAdapter.setRespontListencesStopSpeak(new CallbackTwoListener<Integer, Integer>() {
            @Override
            public void callback(Integer position, Integer object) {
                ArrayList<AudienceVideo> audienceVideoArrayList = audienceAdapter.getData();
                JSONObject jsonObject = new JSONObject();
                try {
                    if (audienceVideoArrayList.get(position).getStopSpeak() == 1) {
                        jsonObject.put(Constant.KEY_MUTE_AUDI, false);
                        ToastUtils.showToast("已打开");
                        audienceVideoArrayList.get(position).setStopSpeak(0);
                    } else if (audienceVideoArrayList.get(position).getStopSpeak() == 0) {
                        ToastUtils.showToast("禁言成功");
                        audienceVideoArrayList.get(position).setStopSpeak(1);
                        jsonObject.put(Constant.KEY_MUTE_AUDI, true);
                    } else {
                        ToastUtils.showToast("禁言失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                searchEdit.setFocusable(false);
                agoraAPI.messageInstantSend(String.valueOf(object), 0, jsonObject.toString(), "");
                audienceAdapter.notifyDataSetChanged();
            }
        });

        audienceAdapter.setRespontListencesRemovePerson(new CallbackTwoListener<Integer, Integer>() {
            @Override
            public void callback(Integer position, Integer object) {
                ArrayList<AudienceVideo> audienceVideoArrayList = audienceAdapter.getData();

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(Constant.KEY_REMOVE_USER, true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                agoraAPI.messageInstantSend(String.valueOf(object), 0, jsonObject.toString(), "");
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();

        Window window = alertDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = DisplayUtil.getWidth(this);
            params.height = DisplayUtil.getHeight(this);
            window.setAttributes(params);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            searchEdit.setFocusable(true);
        }
    };

    private ArrayList<AudienceVideo> searchAudiences(ArrayList<AudienceVideo> audiences, String keyword) {
        ArrayList<AudienceVideo> audienceArrayList = new ArrayList<>();
        for (AudienceVideo audience : audiences) {
            if (audience.getUname().contains(keyword)) {
                audienceArrayList.add(audience);
            }
        }
        return audienceArrayList;
    }

    private void showPPTListDialog(ArrayList<Material> materials) {
        View view = View.inflate(this, R.layout.dialog_ppt_list, null);
        RecyclerViewTV recyclerViewTV = view.findViewById(R.id.meeting_doc_list);
        FocusFixedLinearLayoutManager gridlayoutManager = new FocusFixedLinearLayoutManager(this); // 解决快速长按焦点丢失问题.
        gridlayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        recyclerViewTV.setLayoutManager(gridlayoutManager);
        if (Constant.isPadApplicaion) {
            recyclerViewTV.setFocusable(true);
        } else {
            recyclerViewTV.setFocusable(false);
        }
        recyclerViewTV.addItemDecoration(new SpaceItemDecoration((int) (getResources().getDimension(R.dimen.my_px_20)), 0, (int) (getResources().getDimension(R.dimen.my_px_20)), 0));
        recyclerViewTV.setOnItemListener(this);
        recyclerViewTV.setSelectedItemAtCentered(true);
        MaterialAdapter materialAdapter = new MaterialAdapter(this, materials);
        materialAdapter.setRoom(whiteRoom);
        recyclerViewTV.setAdapter(materialAdapter);
        materialAdapter.setOnClickListener(new MaterialAdapter.OnClickListener() {
            /**预览*/
            @Override
            public void onPreviewButtonClick(View v, Material material, int position) {
                showPPTDetailDialog(material);
            }

            /**使用*/
            @Override
            public void onUseButtonClick(View v, Material material, int position) {
                currentMaterial = material;
                MeetingBroadcastActivity.this.position = 0;
                /**发送了一个视频*/
//                agoraAPI.channelSetAttr(channelName, Constant.VIDEO, Constant.STOPVIDEO);
                Collections.sort(currentMaterial.getMeetingMaterialsPublishList(), (o1, o2) -> (o1.getPriority() < o2.getPriority()) ? -1 : 1);
                ApiClient.getInstance().meetingSetMaterial(TAG, setMaterialCallback, meetingJoin.getMeeting().getId(), currentMaterial.getId());
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(MeetingBroadcastActivity.this, R.style.MyDialog);
        builder.setView(view);
        pptAlertDialog = builder.create();
        if (!pptAlertDialog.isShowing()) {
            pptAlertDialog.show();
        }
    }

    PreviewPlayer jzvdStd;
    long currentTimes;


    /**
     * 资料 视频
     */
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


                RelativeLayout videoContainer = view.findViewById(R.id.videoContainer);
                ImageView imageView = view.findViewById(R.id.image_view);
                if (material.getMeetingMaterialsPublishList().get(position).getType().equals("1")) {
                    imageView.setVisibility(View.GONE);
                    videoContainer.setVisibility(View.VISIBLE);

                    view.setFocusable(true);
                    view.requestFocus();
                    view.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            mLogger.e(keyCode);
                            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {

                                if (System.currentTimeMillis() - currentTimes >= 1000) {
                                    if (jzvdStd != null) {
                                        jzvdStd.startButton.performClick();
                                    }
                                }
                                currentTimes = System.currentTimeMillis();
                            }
                            return false;
                        }
                    });
                    jzvdStd = view.findViewById(R.id.jz_video);
                    if (jzvdStd == null) {
                        mLogger.e("jzvdStd==null");
                        return view;
                    }
                    String imageUrl = ImageHelper.videoImageFromUrl(material.getMeetingMaterialsPublishList().get(0).getUrl()
                            , AutoSizeUtils.dp2px(MeetingBroadcastActivity.this, 300)
                            , AutoSizeUtils.dp2px(MeetingBroadcastActivity.this, 400));
                    Picasso.with(MeetingBroadcastActivity.this).load(imageUrl)
                            .error(R.drawable.item_forum_img_error)
                            .placeholder(R.drawable.item_forum_img_loading)
                            .into(jzvdStd.thumbImageView);
                    jzvdStd.setUp(material.getMeetingMaterialsPublishList().get(0).getUrl(), "", JzvdStd.SCREEN_NORMAL);

                } else {
                    String imageUrl = ImageHelper.getThumb(material.getMeetingMaterialsPublishList().get(position).getUrl());
                    Picasso.with(MeetingBroadcastActivity.this).load(imageUrl).into(imageView);

                }
                container.addView(view);
                return view;

            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                if (jzvdStd != null) {
                    JzvdStd.releaseAllVideos();
                }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MeetingBroadcastActivity.this, R.style.MyDialog);
        builder.setView(view);
        pptDetailDialog = builder.create();
        if (!pptDetailDialog.isShowing()) {
            pptDetailDialog.show();
        }
        pptDetailDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (jzvdStd != null) {
                    JzvdStd.releaseAllVideos();
                }
            }
        });
    }

    private boolean isUsePPTModel = false;

    /**
     * 第一次进入的时候请求
     */
    private OkHttpCallback setMaterialCallback = new OkHttpCallback<Bucket>() {
        @Override
        public void onSuccess(Bucket bucket) {

            if (pptAlertDialog != null && pptAlertDialog.isShowing()) {
                pptAlertDialog.dismiss();
            }
			/*isUsePPTModel = true;

			//如果当前列表里面有主持人 则需要将主持人拿出来放在右下角  然后将大的参会人放在列表中去
			if (mVideoAdapter.isHaveChairMan()) {
				int chairManPosition = mVideoAdapter.getChairManPosition();
				if (chairManPosition != -1) {
					mVideoAdapter.removeItem(chairManPosition);
				}
				if (mCurrentAudienceVideo != null) {
					mVideoAdapter.insertItem(mCurrentAudienceVideo);
				}
			}


			broadcasterLayout.removeAllViews();
			broadcasterLayout.setVisibility(View.GONE);
			mBroadCastContainer.setVisibility(View.VISIBLE);
			broadcasterSmailLayout.setVisibility(View.VISIBLE);
			if (Constant.isPadApplicaion) {
				mBroadCastContainer.setFocusable(true);
			}
			broadcasterSmailLayout.removeAllViews();
			stripSurfaceView(localBroadcasterSurfaceView);
//			localBroadcasterSurfaceView.setZOrderOnTop(true);
			localBroadcasterSurfaceView.setZOrderMediaOverlay(true);
			broadcasterSmailLayout.addView(localBroadcasterSurfaceView);

			mVideoAdapter.setSurfaceViewVisibility(false);
			previewButton.setVisibility(View.VISIBLE);
			nextButton.setVisibility(View.VISIBLE);
			exitDocButton.setVisibility(View.VISIBLE);

			mAudienceRecyclerView.setVisibility(View.GONE);
			docImage.setVisibility(View.GONE);


			MeetingMaterialsPublish currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(position);

			pageText.setVisibility(View.VISIBLE);
			pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");

			String imageUrl = ImageHelper.getThumb(currentMaterialPublish.getUrl());
			Picasso.with(MeetingBroadcastActivity.this).load(imageUrl).into(docImage);

			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("material_id", currentMaterial.getId());
				jsonObject.put("doc_index", position);
				agoraAPI.channelSetAttr(channelName, DOC_INFO, jsonObject.toString());
				agoraAPI.messageChannelSend(channelName, jsonObject.toString(), "");
			} catch (Exception e) {
				e.printStackTrace();
			}*/
            changeViewsByPPTModel(null);
        }

        @Override
        public void onFailure(int errorCode, BaseException exception) {
            super.onFailure(errorCode, exception);
            Toast.makeText(MeetingBroadcastActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };


    /**
     * 使用
     */
    @SuppressLint("SetTextI18n")
    private void changeViewsByPPTModel(Material material) {
        isUsePPTModel = true;
        pptModel = 0;
        mAudienceRecyclerView.setVisibility(View.VISIBLE);
        mVideoAdapter.setVisibility(View.VISIBLE);

        if (currentMaterial == null && material != null) {
            currentMaterial = material;

        }


        recycleviewContainer.setBackground(null);

	/*	MeetingMaterialsPublish e1 = new MeetingMaterialsPublish();
		e1.setCreateDate(System.currentTimeMillis() + "");
		e1.setId(System.currentTimeMillis() + "");
		e1.setType("1");
		e1.setPriority(4);
		e1.setUrl("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4");
		currentMaterial.getMeetingMaterialsPublishList().add(e1);*/

        //如果当前列表里面有主持人 则需要将主持人拿出来放在右下角  然后将大的参会人放在列表中去
        if (mVideoAdapter.isHaveChairMan()) {
			/*int chairManPosition = mVideoAdapter.getChairManPosition();
			if (chairManPosition != -1) {
				mVideoAdapter.removeItem(chairManPosition);
			}*/

            if (mCurrentAudienceVideo != null) {
                mCurrentAudienceVideo.setShowSurface(true);
                mVideoAdapter.insertItem(1, mCurrentAudienceVideo);
                mCurrentAudienceVideo = null;
            }

        } else {
            //列表中没有主持人 添加主持人到列表中
            AudienceVideo audienceVideo = new AudienceVideo();
            audienceVideo.setUid(config().mUid);
            audienceVideo.setName("主持人" + meetingJoin.getHostUser().getHostUserName());
            audienceVideo.setBroadcaster(true);
            /**TextureView*/
            audienceVideo.setTextureView(localBroadcasterSurfaceView);
            mVideoAdapter.insertItem(0, audienceVideo);
            mVideoAdapter.notifyDataSetChanged();
        }

        mLogger.e("此时集合的大小是:  " + mVideoAdapter.getDataSize() + "--- pptModel: " + pptModel);

        if (pptModel == 0) {
            notFullScreenState();
        } else if (pptModel == 1) {
            FullScreenState();
        } else if (pptModel == 2) {
            clearAllState();
        }

        broadcasterLayout.removeAllViews();
        broadcasterLayout.setVisibility(View.GONE);
		/*mBroadCastContainer.setVisibility(View.VISIBLE);
		broadcasterSmailLayout.setVisibility(View.VISIBLE);*/
        if (Constant.isPadApplicaion) {
            mBroadCastContainer.setFocusable(true);
        }
		/*broadcasterSmailLayout.removeAllViews();
		stripSurfaceView(localBroadcasterSurfaceView);
//			localBroadcasterSurfaceView.setZOrderOnTop(true);
		localBroadcasterSurfaceView.setZOrderMediaOverlay(true);
		broadcasterSmailLayout.addView(localBroadcasterSurfaceView);*/

//		mVideoAdapter.setSurfaceViewVisibility(false);
        previewButton.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        exitDocButton.setVisibility(View.VISIBLE);
/*
		mAudienceRecyclerView.setVisibility(View.GONE);
		mVideoAdapter.setVisibility(View.GONE);*/


        /**
         * 有视频显示视频，没有显示图片 ,,以前是显示视频和图片，现在修改为白板
         *
         *  type = 0 是图片，，1视频 2 ppt
         * */
        if (currentMaterial != null) {
            switch (currentMaterial.getType()) {
                case "1":
                    PlayVideo();
                    break;
                case "0":
                    if (currentMaterial.getMeetingMaterialsPublishList().size() > 0) {
                        MeetingMaterialsPublish currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(position);
                        mVideoContainer.setVisibility(View.GONE);
                        findViewById(R.id.app_video_box).setVisibility(View.GONE);

                        findViewById(R.id.play).setVisibility(View.GONE);
                        mPlayButton.setVisibility(View.GONE);

                        docImage.setVisibility(View.VISIBLE);
                        whiteboardView.setVisibility(View.GONE);
                        pageText.setVisibility(View.VISIBLE);
                        pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");

                        String imageUrl = ImageHelper.getThumb(currentMaterialPublish.getUrl());
                        Picasso.with(MeetingBroadcastActivity.this).load(imageUrl).into(docImage);
                    }
                    break;
                case "2":
                    setWhiteBoard(currentMaterial.getConvertFileLists(), position, currentMaterial.getName());
                    break;
            }

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("material_id", currentMaterial.getId());
                jsonObject.put("doc_index", position);
                agoraAPI.channelSetAttr(channelName, DOC_INFO, jsonObject.toString());
                agoraAPI.messageChannelSend(channelName, jsonObject.toString(), "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 使用白板
     */
    @SuppressLint("SetTextI18n")
    private void setWhiteBoard(ArrayList<ConvertFileList> convertFileList, int position, String name) {
        /**白板设置*/
        if (convertFileList != null && convertFileList.size() > 0 && convertFileList.get(position) != null) {
            pageText.setText("第" + (position + 1) + "/" + convertFileList.size() + "页");
            Scene[] scenes = new Scene[convertFileList.size()];
            for (int i = 0; i < convertFileList.size(); i++) {
                scenes[i] = new Scene(name + (i + 1), new PptPage(convertFileList.get(i).getPpt().getSrc(),
                        Double.parseDouble(convertFileList.get(i).getPpt().getWidth()), Double.parseDouble(convertFileList.get(i).getPpt().getHeight())));
            }
            if (whiteRoom != null) {
                whiteRoom.putScenes(SCENE_DIR, scenes, position);
                whiteRoom.setScenePath(SCENE_DIR + "/" + name + (position + 1));
            }
        }
        mVideoContainer.setVisibility(View.GONE);
        findViewById(R.id.app_video_box).setVisibility(View.GONE);

        findViewById(R.id.play).setVisibility(View.GONE);
        mPlayButton.setVisibility(View.GONE);

        docImage.setVisibility(View.GONE);
        whiteboardView.setVisibility(View.VISIBLE);
        pageText.setVisibility(View.VISIBLE);

    }

    private void joinRoom(String uuid, String roomToken) {
        logRoomInfo("room uuid: " + uuid + "\nroomToken: " + roomToken);

//        WhiteSdkConfiguration sdkConfiguration = new WhiteSdkConfiguration(DeviceType.desktop, 1, 1);

        /*显示用户头像*/
//        sdkConfiguration.setUserCursor(true);

        //动态 ppt 需要的自定义字体，如果没有使用，无需调用
//        HashMap<String, String> map = new HashMap<>();
//        map.put("宋体", "https://your-cdn.com/Songti.ttf");
//        sdkConfiguration.setFonts(map);

        //图片替换 API，需要在 whiteSDKConfig 中先行调用 setHasUrlInterrupterAPI，进行设置，否则不会被回调。
//        whiteSdk = new WhiteSdk(whiteboardView, this, sdkConfiguration,
//                new UrlInterrupter() {
//                    @Override
//                    public String urlInterrupter(String sourceUrl) {
//                        return sourceUrl;
//                    }
//                });

        /** 设置自定义全局状态，在后续回调中 GlobalState 直接进行类型转换即可 */
//        WhiteDisplayerState.setCustomGlobalStateClass(MyGlobalState.class);

        //如需支持用户头像，请在设置 WhiteSdkConfiguration 后，再调用 setUserPayload 方法，传入符合用户信息

        final Date joinDate = new Date();
        logRoomInfo("native join " + joinDate);
        initBoardWithRoomToken(uuid, roomToken);
    }


    public void initBoardWithRoomToken(String uuid, String roomToken) {
        if (TextUtils.isEmpty(uuid) || TextUtils.isEmpty(roomToken)) return;
        boardManager.getRoomPhase(new Promise<RoomPhase>() {
            @Override
            public void then(RoomPhase phase) {
                if (phase != RoomPhase.connected) {
                    RoomParams params = new RoomParams(uuid, roomToken);
                    boardManager.init(whiteSdk, params);
                }
            }

            @Override
            public void catchEx(SDKError t) {
                ToastUtils.showToast(t.getMessage());
            }
        });
    }

    /**
     * 自定义 GlobalState 示例
     * 继承自 GlobalState 的子类，然后调用 {@link WhiteDisplayerState#setCustomGlobalStateClass(Class)}
     */
    class MyGlobalState extends GlobalState {
        public String getOne() {
            return one;
        }

        public void setOne(String one) {
            this.one = one;
        }

        String one;
    }

    void logRoomInfo(String str) {
        Log.e(ROOM_INFO, Thread.currentThread().getStackTrace()[3].getMethodName() + " " + str);
    }

    void logAction() {
        Log.e(ROOM_ACTION, Thread.currentThread().getStackTrace()[3].getMethodName());
    }

    private OkHttpCallback meetingMaterialCallback = new OkHttpCallback<Bucket<Material>>() {

        @Override
        public void onSuccess(Bucket<Material> materialBucket) {
            changeViewsByPPTModel(materialBucket.getData());
        }

        @Override
        public void onFailure(int errorCode, BaseException exception) {
            super.onFailure(errorCode, exception);
            Toast.makeText(MeetingBroadcastActivity.this, errorCode + "---" + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void stopRecordVideo() {
        if (Constant.isNeedRecord) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("sid", mSid);
            map.put("resourceId", mResourceId);
            map.put("cname", meetingJoin.getMeeting().getId());
            map.put("uid", mRecordUid);

            com.zhongyou.meettvapplicaion.net.ApiClient.getInstance().stopRecordVideo(this, JSON.toJSONString(map), new com.zhongyou.meettvapplicaion.net.OkHttpCallback<com.alibaba.fastjson.JSONObject>() {
                @Override
                public void onSuccess(com.alibaba.fastjson.JSONObject json) {
                    if (json.getInteger("errcode") != 0) {
                        ToastUtils.showToast(MeetingBroadcastActivity.this, json.getString("errmsg"));
                    }
                }

                @Override
                public void onFailure(int errorCode, BaseException exception) {
                    super.onFailure(errorCode, exception);
                    mLogger.e(exception.getMessage());
                }
            });
        }


    }

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

            stopRecordVideo();
        }

        @Override
        public void onFailure(int errorCode, BaseException exception) {
            super.onFailure(errorCode, exception);
            Toast.makeText(MeetingBroadcastActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void doConfigEngine(int cRole) {
        int quilty = Constant.resolvingPower;
        if (quilty == 1.0) {
            worker().configEngine(cRole, VideoEncoderConfiguration.VD_640x360);//主持人是640*480
        } else if (quilty == 2.0) {
            worker().configEngine(cRole, VideoEncoderConfiguration.VD_640x360);//主持人是960*720
        } else if (quilty == 3.0) {
            worker().configEngine(cRole, VideoEncoderConfiguration.VD_1280x720);//主持人是1920 × 1080
        } else {
            worker().configEngine(cRole, VideoEncoderConfiguration.VD_640x360);//主持人是640*480
        }

	/*	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		int prefIndex = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, ConstantApp.DEFAULT_PROFILE_IDX);
		int vProfile = ConstantApp.VIDEO_PROFILES[prefIndex - 2];*/
    }

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
            ApiClient.getInstance().meetingJoinStats(TAG, meetingJoinStatsCallback(false), params);
        }
        broadcasterLayout.removeAllViews();
    }

    @Override
    public void onJoinChannelSuccess(final String channel, final int uid, final int elapsed) {
        runOnUiThread(() -> {
            if (isFinishing()) {
                return;
            }
            com.orhanobut.logger.Logger.e("onJoinChannelSuccess   加入频道成功");
            config().mUid = uid;
            channelName = channel;

            if ("true".equals(agora.getIsTest())) {
                agoraAPI.login2(agora.getAppID(), "" + uid, "noneed_token", 0, "", 20, 30);
            } else {
                agoraAPI.login2(agora.getAppID(), "" + uid, agora.getSignalingKey(), 0, "", 20, 30);
            }
            if (localBroadcasterSurfaceView == null) {
                /**TextureView*/
                localBroadcasterSurfaceView = RtcEngine.CreateTextureView(getApplicationContext());
//                localBroadcasterSurfaceView.setZOrderOnTop(false);
//                localBroadcasterSurfaceView.setZOrderMediaOverlay(false);
                broadcasterLayout.addView(localBroadcasterSurfaceView);

                mChannelJoined = true;
                setSurfaceTextureListener(localBroadcasterSurfaceView);


                if (BuildConfig.DEBUG && Build.DEVICE.equals("we30c")) {
                    rtcEngine().muteLocalAudioStream(true);
                    setButtonrawableTop(muteAudioButton, R.drawable.icon_speek_no);
                    muteAudioButton.setText("话筒关闭");
                }
                if (mUSBMonitor == null) {
                    mUSBMonitor = new USBMonitor(this, mConnectListener);
                }
                if (!mUSBMonitor.isRegistered()) {
                    mUSBMonitor.register();
                }
                mLogger.e(JSON.toJSONString(mUSBMonitor.getDeviceList()));
                for (UsbDevice usbDevice : mUSBMonitor.getDeviceList()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (usbDevice.getProductName() != null) {
                            //TC-UB60pro Video
                            if (usbDevice.getProductName().toLowerCase().contains("ub")
                                    || usbDevice.getProductName().toLowerCase().contains("all in")) {
                                mLogger.e("onJoinChannelSuccess: 不需要镜像展示");
//                                int i = rtcEngine().setLocalVideoMirrorMode(Constants.VIDEO_MIRROR_MODE_DISABLED);
//                                mLogger.e("镜像视频： -->" + i);
//                                worker().preview2(true, localBroadcasterSurfaceView, config().mUid, true);
                                mLogger.e("onJoinChannelSuccess: 需要镜像展示");
                                worker().preview2(true, localBroadcasterSurfaceView, config().mUid, false);
                                break;
                            }
                        } else {
                            mLogger.e("onJoinChannelSuccess: 需要镜像展示");
                            rtcEngine().setLocalVideoMirrorMode(Constants.VIDEO_MIRROR_MODE_ENABLED);
                            worker().preview2(true, localBroadcasterSurfaceView, config().mUid, false);
                        }
                    } else {
                        worker().preview2(true, localBroadcasterSurfaceView, config().mUid, true);
                    }
                }
            }
        });
    }

    /**push方式*/
    private void setSurfaceTextureListener(TextureView localBroadcasterSurfaceView) {
        localBroadcasterSurfaceView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Log.i(TAG, "onSurfaceTextureAvailable:" + width + "x" + height);
                initOpenGL(surface, width, height);
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                Log.i(TAG, "onSurfaceTextureDestroyed");
                mTextureDestroyed = true;
                closeCamera();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    private void openCamera() {
        if (mCamera != null || mPreviewing) {
            Log.i(TAG, "Camera preview has been started");
            return;
        }

        try {
            mCamera = Camera.open(0);

            // It is assumed to capture images of resolution 640x480.
            // During development, it should be the most suitable
            // supported resolution that best fits the scenario.
            Camera.Parameters parameters = mCamera.getParameters();
            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
            for (Camera.Size size : sizes) {
                Log.e("mlt_cj","............."+size.height);
                Log.e("mlt_cj","............."+size.width);
            }
            parameters.setPreviewSize(DEFAULT_CAPTURE_WIDTH, DEFAULT_CAPTURE_HEIGHT);
            mCamera.setParameters(parameters);
            mCamera.setPreviewTexture(mPreviewSurfaceTexture);

            // The display orientation is 90 for both front and back
            // facing cameras using a surface texture for the preview
            // when the screen is in portrait mode.
            mCamera.setDisplayOrientation(0);
            startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startPreview() {
        if (mCamera != null && !mPreviewing) {
            mCamera.startPreview();
            mPreviewing = true;
        }
    }

    private void stopPreview() {
        if (mCamera != null && mPreviewing) {
            mCamera.stopPreview();
            mPreviewing = false;
        }
    }

    private void closeCamera() {
        if (mCamera != null) {
            stopPreview();
            mCamera.release();
            mCamera = null;
            mProgram.release();
            mEglCore.releaseSurface(mDummySurface);
            mEglCore.releaseSurface(mDrawSurface);
            mEglCore.release();
        }
    }

    private void initOpenGL(SurfaceTexture surface, int width, int height) {
        mTextureDestroyed = false;
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        mEglCore = new EglCore();
        mDummySurface = mEglCore.createOffscreenSurface(1, 1);
        mEglCore.makeCurrent(mDummySurface);
        mPreviewTexture = GlUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mPreviewSurfaceTexture = new SurfaceTexture(mPreviewTexture);
        mPreviewSurfaceTexture.setOnFrameAvailableListener(this);

        mDrawSurface = mEglCore.createWindowSurface(surface);
        mProgram = new ProgramTextureOES();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if (mTextureDestroyed) return;

        if (!mEglCore.isCurrent(mDrawSurface)) {
            mEglCore.makeCurrent(mDrawSurface);
        }

        try {
            mPreviewSurfaceTexture.updateTexImage();
            mPreviewSurfaceTexture.getTransformMatrix(mTransform);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // The rectangle ratio of frames and the screen surface
        // may be different, so cropping may happen when display
        // frames to the screen.
        calculateDisplayTransform();
        GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);
        mProgram.drawFrame(mPreviewTexture, mTransform, mMVPMatrix);
        mEglCore.swapBuffers(mDrawSurface);

        if (mChannelJoined) {
            float[] floats = {1,0,0,0, 0,-1,0,0, 0,0,-1,0, 0,1,0,1};
            mTransform = floats;
            boolean isPush = rtcEngine().pushExternalVideoFrame(
                    buildVideoFrame(mPreviewTexture, mTransform));
        }
    }

    private void calculateDisplayTransform() {
        // The display transformation matrix does not change
        // for the same camera when the screen orientation
        // remains the same.
        if (mMVPMatrixInit) return;

        // For simplicity, we only consider the activity as
        // portrait mode. In this case, the captured images
        // should be rotated 90 degrees (left or right).
        // Thus the frame width and height should be swapped.
        float frameRatio = DEFAULT_CAPTURE_HEIGHT / (float) DEFAULT_CAPTURE_WIDTH;
        float surfaceRatio = mSurfaceWidth / (float) mSurfaceHeight;
        Matrix.setIdentityM(mMVPMatrix, 0);

        if (frameRatio >= surfaceRatio) {
            float w = DEFAULT_CAPTURE_WIDTH * surfaceRatio;
            float scaleW = DEFAULT_CAPTURE_HEIGHT / w;
            Matrix.scaleM(mMVPMatrix, 0, 1, 1, 1);
        } else {
            float h = DEFAULT_CAPTURE_HEIGHT / surfaceRatio;
            float scaleH = DEFAULT_CAPTURE_WIDTH / h;
            Matrix.scaleM(mMVPMatrix, 0, -1, 1, 1);
        }

        mMVPMatrixInit = true;
    }

    private AgoraVideoFrame buildVideoFrame(int textureId, float[] transform) {
        AgoraVideoFrame frame = new AgoraVideoFrame();
        frame.textureID = textureId;
        frame.format = AgoraVideoFrame.FORMAT_TEXTURE_OES;
        frame.transform = transform;
//        frame.stride = DEFAULT_CAPTURE_HEIGHT;
//        frame.height = DEFAULT_CAPTURE_WIDTH;
        frame.stride = 1920;
        frame.height = 1080;
        frame.eglContext14 = mEglCore.getEGLContext();
        frame.timeStamp = System.currentTimeMillis();
        return frame;
    }


    private String meetingJoinTraceId;

    private OkHttpCallback meetingJoinStatsCallback(boolean isRestart) {
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
    public void onLocalVideoStateChanged(int localVideoState, int error) {
        runOnUiThread(() -> {
            Log.e(TAG, "onLocalVideoStateChanged: localVideoState:" + localVideoState + "  error:" + error);
            if (localVideoState == 3) {//本地视频启动失败
                Message message = new Message();
                message.what = 3;
                message.arg1 = error;
                showToolBarsHandler.sendMessageDelayed(message, 1000);

            }
        });


    }

    /**
     * 开启远端画面
     */
    private IVideoSink mRemoteRender;

    @Override
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
        com.orhanobut.logger.Logger.e("uid:" + uid + "  state:" + state + "  reason:" + reason + "   elapsed:" + elapsed);
        runOnUiThread(() -> {
            if (state == 2) {//远端用户的视频正在解码
                int positionById = mVideoAdapter.getPositionById(uid);
                com.orhanobut.logger.Logger.e("onRemoteVideoStateChanged /参会人的视频正在解码 positionById:" + positionById);
                if (isFinishing()) {
                    return;
                }
                mLogger.e("参会人的视频进入……");

                if (uid == currentAiducenceId) {
                    isConnecting = false;
                    com.orhanobut.logger.Logger.e("连麦观众的视频进入了会议");
                }

                //只能以参会人的身份进入视频 如果为第8个  则应该是观众
                if (currentMaterial == null) {
                    mAudienceRecyclerView.setVisibility(View.VISIBLE);
                    mVideoAdapter.setVisibility(View.VISIBLE);
                } else {
                    if (pptModel == 1 || pptModel == 2) {
                        mAudienceRecyclerView.setVisibility(View.GONE);
                        mVideoAdapter.setVisibility(View.GONE);
                    }

                }
                /**TextureView*/

                // remoteAudienceSurfaceView.setZOrderOnTop(true);
                // remoteAudienceSurfaceView.setZOrderMediaOverlay(true);

                com.orhanobut.logger.Logger.e("参会人进入会议的positionById：--->" + positionById);

                remoteAudienceSurfaceView = RtcEngine.CreateTextureView(getApplicationContext());
                rtcEngine().setupRemoteVideo(new VideoCanvas(remoteAudienceSurfaceView, LOCAL_RENDER_MODE, uid));
                if (positionById == -1) {
                    if (mCurrentAudienceVideo != null && mCurrentAudienceVideo.getUid() == uid) { //主持人
                        //当前视频传输的是放大的视频
                        /**TextureView*/
                        mCurrentAudienceVideo.setTextureView(remoteAudienceSurfaceView);
                        stripSurfaceView(remoteAudienceSurfaceView);
                        mCurrentAudienceVideo.setVideoStatus(0);
                        broadcasterLayout.removeAllViews();
                        broadcasterLayout.addView(remoteAudienceSurfaceView);
                        broadcasterLayout.setVisibility(View.VISIBLE);
                    } else {
                        AudienceVideo audienceVideo = new AudienceVideo();
                        audienceVideo.setUid(uid);
                        audienceVideo.setBroadcaster(false);
                        audienceVideo.setCallStatus(2);
                        /**TextureView*/
                        audienceVideo.setTextureView(remoteAudienceSurfaceView);
                        audienceVideo.setVideoStatus(0);
                        mVideoAdapter.insertItem(audienceVideo);

                        if (uid != 0 && uid == currentAiducenceId) {
                            mLogger.e("观众视频进入了……" + uid);
                            stopButton.setVisibility(View.VISIBLE);
                            currentAudience = audienceVideo;
                            isConnecting = false;
                        } else {
                            if (currentAiducenceId == 0 || currentAudience == null) {
                                stopButton.setVisibility(View.INVISIBLE);
                            } else {

                                if (isFullScreen) {
                                    stopButton.setVisibility(View.INVISIBLE);
                                } else {
                                    stopButton.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }

                } else {
                    rtcEngine().setupRemoteVideo(new VideoCanvas(remoteAudienceSurfaceView, LOCAL_RENDER_MODE, uid));
                    AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(positionById);
                    if (audienceVideo != null) {
                        /**TextureView*/
                        audienceVideo.setTextureView(remoteAudienceSurfaceView);
                        mVideoAdapter.notifyItemChanged(positionById);
                    }
                }

                agoraAPI.getUserAttr(String.valueOf(uid), "userName");
                //调用这个方法  会得到取某个特定用户的用户属性  调用成功会回调 onUserAttrResult
                agoraAPI.getUserAttr(String.valueOf(uid), "uname");


                if (connectingHandler.hasMessages(0)) {
                    connectingHandler.removeMessages(0);
                }

                    /*	JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("uid", uid);
					jsonObject.put("getInformation", true);
					agoraAPI.messageInstantSend(uid + "", 0, jsonObject.toString(), "");
				} catch (JSONException e) {
					e.printStackTrace();
				}*/

                //分屏模式下 改变布局
                if (isSplitMode && mVideoAdapter.getDataSize() <= 7) {
                    SpliteViews();
                    mSplitView.setText("退出均分");
                    if (mSplitView.getVisibility() == View.VISIBLE) {
                        mSplitView.requestFocus();
                    } else {
                        exitButton.requestFocus();
                    }
                }

                if (isFullScreen || pptModel == 1 || pptModel == 2) {
                    mAudienceRecyclerView.setVisibility(View.GONE);
                    mVideoAdapter.setVisibility(View.GONE);
                } else {
                    if (pptModel == 0) {
                        notFullScreenState();
                    }
                    mAudienceRecyclerView.setVisibility(View.VISIBLE);
                    mVideoAdapter.setVisibility(View.VISIBLE);
                }  /*else if (positionById == -1) {
					com.orhanobut.logger.Logger.e("参会人的视频正在解码:positionById-->"+positionById);
					AudienceVideo audienceVideo = new AudienceVideo();
					SurfaceView userSurfaceView = RtcEngine.CreateRendererView(getApplicationContext());
					userSurfaceView.setZOrderOnTop(true);
					userSurfaceView.setZOrderMediaOverlay(true);
					rtcEngine().setupRemoteVideo(new VideoCanvas(userSurfaceView, VideoCanvas.LOCAL_RENDER_MODE, uid));
					audienceVideo.setSurfaceView(userSurfaceView);
					audienceVideo.setVideoStatus(0);
					audienceVideo.setUid(uid);

					mVideoAdapter.notifyItemInserted(mVideoAdapter.getItemCount());
					agoraAPI.getUserAttr(String.valueOf(uid), "userName");
					//调用这个方法  会得到取某个特定用户的用户属性  调用成功会回调 onUserAttrResult
					agoraAPI.getUserAttr(String.valueOf(uid), "uname");
					audienceHashMap.put(uid, audienceVideo);
				}*/


            } else if (state == 4) {//远端视频流播放失败
                int positionById = mVideoAdapter.getPositionById(uid);
                if (positionById != -1) {
                    mVideoAdapter.getAudienceVideoLists().get(positionById).setVideoStatus(2);
                    mVideoAdapter.getAudienceVideoLists().get(positionById).setSurfaceView(null);
                    mVideoAdapter.notifyItemChanged(positionById);
                }

            } else if (state == 3) {//远端视频流卡顿
                int positionById = mVideoAdapter.getPositionById(uid);
                if (positionById != -1) {
                    mVideoAdapter.getAudienceVideoLists().get(positionById).setVideoStatus(1);
                    mVideoAdapter.notifyItemChanged(positionById);
                }
            } else if (state == Constants.REMOTE_VIDEO_STATE_STOPPED) {
                Log.i(TAG, "remote video stopped:" + (uid & 0xFFFFFFFFL));
                removeRemotePreview(uid);
            }
        });
    }

    private void removeRemotePreview(int uid) {
        rtcEngine().setupRemoteVideo(new VideoCanvas(
                null, VideoCanvas.RENDER_MODE_HIDDEN, uid));
    }


    /**
     * 远端音频流状态:state
     * REMOTE_AUDIO_STATE_STOPPED(0)：远端音频流默认初始状态。 在 REMOTE_AUDIO_REASON_LOCAL_MUTED(3)、REMOTE_AUDIO_REASON_REMOTE_MUTED(5) 或 REMOTE_AUDIO_REASON_REMOTE_OFFLINE(7) 的情况下，会报告该状态。
     * REMOTE_AUDIO_STATE_STARTING(1)：本地用户已接收远端音频首包
     * REMOTE_AUDIO_STATE_DECODING(2)：远端音频流正在解码，正常播放。在 REMOTE_AUDIO_REASON_NETWORK_RECOVERY(2)、 REMOTE_AUDIO_REASON_LOCAL_UNMUTED(4) 或 REMOTE_AUDIO_REASON_REMOTE_UNMUTED(6) 的情况下，会报告该状态
     * REMOTE_AUDIO_STATE_FROZEN(3)：远端音频流卡顿。在 REMOTE_AUDIO_REASON_NETWORK_CONGESTION(1) 的情况下，会报告该状态
     * REMOTE_AUDIO_STATE_FAILED(4)：远端音频流播放失败。在 REMOTE_AUDIO_REASON_INTERNAL(0) 的情况下，会报告该状态
     * <p>
     * 远端音频流状态改变的具体原因：reason
     * REMOTE_AUDIO_REASON_INTERNAL(0)：内部原因
     * REMOTE_AUDIO_REASON_NETWORK_CONGESTION(1)：网络阻塞
     * REMOTE_AUDIO_REASON_NETWORK_RECOVERY(2)：网络恢复正常
     * REMOTE_AUDIO_REASON_LOCAL_MUTED(3)：本地用户停止接收远端音频流或本地用户禁用音频模块
     * REMOTE_AUDIO_REASON_LOCAL_UNMUTED(4)：本地用户恢复接收远端音频流或本地用户启用音频模块
     * REMOTE_AUDIO_REASON_REMOTE_MUTED(5)：远端用户停止发送音频流或远端用户禁用音频模块
     * REMOTE_AUDIO_REASON_REMOTE_UNMUTED(6)：远端用户恢复发送音频流或远端用户启用音频模块
     * REMOTE_AUDIO_REASON_REMOTE_OFFLINE(7)：远端用户离开频道
     */
    @Override
    public void onRemoteAudioStateChanged(int uid, int state, int reason, int elapsed) {
        runOnUiThread(() -> {
            switch (reason) {
                case REMOTE_AUDIO_REASON_INTERNAL:
                    if (BuildConfig.DEBUG) {
                        ToastUtils.showToast("音频发生错误");
                    }
                    break;
                case REMOTE_AUDIO_REASON_NETWORK_CONGESTION:
                    if (BuildConfig.DEBUG) {
                        ToastUtils.showToast("网络错误");
                    }
                    break;
                case REMOTE_AUDIO_REASON_NETWORK_RECOVERY:
                    if (BuildConfig.DEBUG) {
                        ToastUtils.showToast("网络恢复正常");
                    }
                    break;
                case REMOTE_AUDIO_REASON_LOCAL_MUTED:
                    if (BuildConfig.DEBUG) {
                        ToastUtils.showToast("本地用户停止接收远端音频流或本地用户禁用音频模块");
                    }
                    audienceAdapter.setUid(uid, 1);
                    break;
                case REMOTE_AUDIO_REASON_LOCAL_UNMUTED:
                    if (BuildConfig.DEBUG) {
                        ToastUtils.showToast("本地用户恢复接收远端音频流或本地用户启用音频模块");
                    }
                    audienceAdapter.setUid(uid, 0);
                    break;
                case REMOTE_AUDIO_REASON_REMOTE_MUTED:
                    if (BuildConfig.DEBUG) {
                        ToastUtils.showToast("远端用户停止发送音频流或远端用户禁用音频模块");
                    }
                    break;
                case REMOTE_AUDIO_REASON_REMOTE_UNMUTED:
                    if (BuildConfig.DEBUG) {
                        ToastUtils.showToast("远端用户恢复发送音频流或远端用户启用音频模块");
                    }
                    break;
                case REMOTE_AUDIO_REASON_REMOTE_OFFLINE:
                    if (BuildConfig.DEBUG) {
                        ToastUtils.showToast("远端用户离开频道");
                    }
                    break;
            }
        });
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
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        runOnUiThread(() -> {
            if (uid == currentAiducenceId) {
                mLogger.e("观众视频进入了……" + uid);
                stopButton.setVisibility(View.VISIBLE);
                isConnecting = false;
            }
        });
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        runOnUiThread(() -> {
            if (isFinishing()) {
                return;
            }

            ApiClient.getInstance().getMeetingHost(TAG, meetingJoin.getMeeting().getId(), String.valueOf(uid), joinMeetingCallback(uid));
        });
    }

    private OkHttpCallback joinMeetingCallback(int uid) {
        return new OkHttpCallback<Bucket<HostUser>>() {
            @Override
            public void onSuccess(Bucket<HostUser> hostUserBucket) {
                String role = hostUserBucket.getData().getRole();
                if (role != null) {
                    switch (role) {
                        case "0"://主持人
                            break;
                        case "1"://参会人
                            break;
                        case "2"://观众
                            break;
                        default:
                            break;
                    }
                }
            }
        };
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        LOG.debug("onUserOffline " + (uid & 0xFFFFFFFFL) + " " + reason);
        mLogger.e("onUserOffline " + (uid) + " " + reason);
        if (isFinishing()) {
            return;
        }

        runOnUiThread(() -> {

            if (mCurrentAudienceVideo != null && mCurrentAudienceVideo.getUid() == uid) {
                broadcasterLayout.removeAllViews();
                if (mVideoAdapter.isHaveChairMan()) {
                    int chairManPosition = mVideoAdapter.getChairManPosition();
                    if (chairManPosition != -1) {
                        AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(chairManPosition);
                        if (audienceVideo != null && audienceVideo.getSurfaceView() != null) {
                            mVideoAdapter.removeItem(chairManPosition);
                        }

                    }
                }
//                localBroadcasterSurfaceView.setZOrderOnTop(true);
//                localBroadcasterSurfaceView.setZOrderMediaOverlay(false);
                stripSurfaceView(localBroadcasterSurfaceView);
                broadcasterLayout.addView(localBroadcasterSurfaceView);
                mCurrentAudienceVideo = null;
                if (audienceHashMap.containsKey(uid)) {
                    audienceHashMap.get(uid).setCallStatus(0);
                    audienceHashMap.get(uid).setHandsUp(false);
                    updateAudienceList();
                }
            }

            if (reason == Constants.USER_OFFLINE_BECOME_AUDIENCE) {
                mLogger.e("%d的用户变成了观众模式", uid);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (currentAudience != null && currentAiducenceId == uid) {
                            if (audienceHashMap.containsKey(uid)) {
                                audienceHashMap.get(uid).setCallStatus(0);
                                audienceHashMap.get(uid).setHandsUp(false);
                                updateAudienceList();

                            }
                            currentAudience = null;
                            currentAiducenceId = -1;
                            stopButton.setVisibility(View.GONE);
                        }
                        int positionById = mVideoAdapter.getPositionById(uid);
                        if (positionById != -1) {
                            AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(positionById);
                            if (audienceVideo != null && audienceVideo.getSurfaceView() != null) {
                                audienceVideo.getSurfaceView().setZOrderMediaOverlay(false);
                                audienceVideo.getSurfaceView().setZOrderOnTop(false);
                            }
                        }
                        mVideoAdapter.deleteItem(uid);

                    }
                });
            } else if (reason == Constants.USER_OFFLINE_QUIT) {
                mLogger.e("%d的用户退出了频道", uid);
                if (mCurrentAudienceVideo != null && mCurrentAudienceVideo.getUid() == uid) {
                    broadcasterLayout.removeAllViews();
                    if (mVideoAdapter.isHaveChairMan()) {
                        int chairManPosition = mVideoAdapter.getChairManPosition();
                        if (chairManPosition != -1) {
                            AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(chairManPosition);
                            if (audienceVideo != null && audienceVideo.getSurfaceView() != null) {
                                mVideoAdapter.removeItem(chairManPosition);
                            }

                        }
                    }
//                    localBroadcasterSurfaceView.setZOrderOnTop(true);
//                    localBroadcasterSurfaceView.setZOrderMediaOverlay(false);
                    stripSurfaceView(localBroadcasterSurfaceView);
                    broadcasterLayout.addView(localBroadcasterSurfaceView);
                    mCurrentAudienceVideo = null;

                }

                if (audienceHashMap.containsKey(uid)) {
                    audienceHashMap.remove(uid);
                    currentAudience = null;
                    updateAudienceList();
                }
                int positionById = mVideoAdapter.getPositionById(uid);
                if (positionById != -1) {
                    AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(positionById);
                    if (audienceVideo != null && audienceVideo.getSurfaceView() != null) {
                        audienceVideo.getSurfaceView().setZOrderMediaOverlay(false);
                        audienceVideo.getSurfaceView().setZOrderOnTop(false);
                    }
                }
                mVideoAdapter.deleteItem(uid);
                if (uid == currentAiducenceId) {
                    stopButton.setVisibility(View.GONE);
                    currentAiducenceId = -1;
                }

            } else if (reason == Constants.USER_OFFLINE_DROPPED) {//因过长时间收不到对方数据包，超时掉线
                if (currentAiducenceId == uid) {//当前连麦的观众掉线了
                    stopButton.setVisibility(View.INVISIBLE);
                    if (audienceHashMap.containsKey(uid)) {
                        audienceHashMap.remove(uid);
                    }
                    stopButton.setVisibility(View.GONE);
                }
                int positionById = mVideoAdapter.getPositionById(uid);
                if (positionById != -1) {
                    AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(positionById);
                    if (audienceVideo != null && audienceVideo.getSurfaceView() != null) {
                        audienceVideo.getSurfaceView().setZOrderMediaOverlay(false);
                        audienceVideo.getSurfaceView().setZOrderOnTop(false);
                    }
                }
                mVideoAdapter.deleteItem(uid);
            }

            //需要将主持人移动到大的view上  在删除这个参会人
            //		changeView(broadcasterLayout, localBroadcasterSurfaceView);
            //如果是分屏模式 先判断是否大于2
            if (isSplitMode) {
                mLogger.e("当前集合大小是：" + mVideoAdapter.getDataSize());
                //只有参会人和主持人时  没有使用ppt 集合退出为2
                // 只有参会人和主持人时  使用了ppt 集合退出为1
                //只有主持人和观众时  没有使用ppt 集合退出为2
                //只有主持人和观众时  使用了ppt   集合退出为1
                //使用了ppt 集合大小为1时 需要特别处理 此时应该只有主持人
                //没有使用ppt时 集合大小为2 需要特别处理 此时也应该只有主持人

                if (!isUsePPTModel && mVideoAdapter.getDataSize() == 1 || isUsePPTModel && mVideoAdapter.getDataSize() == 1) {

                    if (mVideoAdapter.isHaveChairMan() && !isUsePPTModel) {
                        mLogger.e("此时没有在使用ppt   主持人在列表中");
                        int chairManPosition = mVideoAdapter.getChairManPosition();
                        if (chairManPosition != -1) {
                            AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(chairManPosition);
                            if (audienceVideo != null && audienceVideo.getSurfaceView() != null) {
                                mVideoAdapter.removeItem(chairManPosition);
                            }
                        }

//                        localBroadcasterSurfaceView.setZOrderOnTop(true);
//                        localBroadcasterSurfaceView.setZOrderMediaOverlay(false);
                        broadcasterLayout.removeAllViews();
                        stripSurfaceView(localBroadcasterSurfaceView);
                        broadcasterLayout.addView(localBroadcasterSurfaceView);

                        int positionById = mVideoAdapter.getPositionById(uid);
                        if (positionById != -1) {
                            AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(positionById);
                            if (audienceVideo != null) {
                                audienceVideo.getSurfaceView().setZOrderMediaOverlay(false);
                                audienceVideo.getSurfaceView().setZOrderOnTop(false);
                            }
                        }
                        mSplitView.setText("均分模式");
                        mVideoAdapter.deleteItem(uid);

                    } else if (isUsePPTModel) {
                        //在使用ppt的时候 主持人是不再列表中 此时有人退出 就直接移除此人就行
                        mLogger.e("此时在使用ppt 主持人不再列表中");
                        int positionById = mVideoAdapter.getPositionById(uid);
                        if (positionById != -1) {
                            AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(positionById);
                            if (audienceVideo != null && audienceVideo.getSurfaceView() != null) {
                                audienceVideo.getSurfaceView().setZOrderOnTop(false);
                                audienceVideo.getSurfaceView().setZOrderMediaOverlay(false);
                            }
                            mVideoAdapter.deleteItem(uid);
                        }
                    }
                    if (!isUsePPTModel) {
//                        localBroadcasterSurfaceView.setZOrderOnTop(false);
//                        localBroadcasterSurfaceView.setZOrderMediaOverlay(false);
                        stripSurfaceView(localBroadcasterSurfaceView);
                        broadcasterLayout.removeAllViews();
                        broadcasterLayout.addView(localBroadcasterSurfaceView);
                    }

                    mSplitView.setText("均分模式");
                    if (pptModel == 2) {
                        exitSpliteMode();
                    } else if (pptModel == 1) {
                        FullScreenState();
                    } else if (pptModel == 0) {
                        notFullScreenState();
                    }


                } else {
                    //如果大2的话 直接移除此人就行
                    int positionById = mVideoAdapter.getPositionById(uid);
                    if (positionById != -1) {
                        AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(positionById);
                        if (audienceVideo != null) {
                            audienceVideo.getSurfaceView().setZOrderMediaOverlay(false);
                            audienceVideo.getSurfaceView().setZOrderOnTop(false);
                        }
                    }
                    mVideoAdapter.deleteItem(uid);


                }
                mLogger.e(isSplitMode + "----" + isUsePPTModel);
                if (isSplitMode) {
                    if (mVideoAdapter.getAudienceVideoLists().size() > 1) {
                        SpliteViews();
                    } else {
                        exitSpliteMode();
                        isSplitMode = false;
                    }
                }
            } else {
                //不是分屏模式 如果此人在大的视图 直接移除大视图 将主持人拿出来放到大的视图
                if (mCurrentAudienceVideo != null && mCurrentAudienceVideo.getUid() == uid) {
                    broadcasterLayout.removeAllViews();
                    if (mVideoAdapter.isHaveChairMan()) {
                        int chairManPosition = mVideoAdapter.getChairManPosition();
                        if (chairManPosition != -1) {
                            AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(chairManPosition);
                            if (audienceVideo != null && audienceVideo.getSurfaceView() != null) {
                                mVideoAdapter.removeItem(chairManPosition);
                            }

                        }
                    }
//                    localBroadcasterSurfaceView.setZOrderOnTop(true);
//                    localBroadcasterSurfaceView.setZOrderMediaOverlay(false);
                    stripSurfaceView(localBroadcasterSurfaceView);
                    broadcasterLayout.addView(localBroadcasterSurfaceView);
                } else {
                    //如果此人不再大的视图里面 直接删除此人
                    int positionById = mVideoAdapter.getPositionById(uid);
                    if (positionById != -1) {
                        AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(positionById);
                        if (audienceVideo != null) {
                            audienceVideo.getSurfaceView().setZOrderMediaOverlay(false);
                            audienceVideo.getSurfaceView().setZOrderOnTop(false);
                        }
                    }
                    mVideoAdapter.deleteItem(uid);
                }

            }

            if (isUsePPTModel) {
                broadcasterLayout.setVisibility(View.GONE);
            } else {
                broadcasterLayout.setVisibility(View.VISIBLE);
            }
        });

    }

    Badge badge;

    private void updateAudienceList() {
        Iterator iter = audienceHashMap.entrySet().iterator();
        audiences.clear();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            audiences.add((AudienceVideo) entry.getValue());
        }
        if (audienceAdapter != null) {
            audienceAdapter.setData(audiences);
        }
        if (badge == null) {
            badge = new QBadgeView(this)
                    .bindTarget(waiterButton)
                    .setBadgeBackgroundColor(getResources().getColor(R.color.red))
                    .setBadgeTextColor(getResources().getColor(R.color.white))
                    .setBadgeGravity(Gravity.END | Gravity.TOP)
                    .setGravityOffset(60, -2, true)
                    .setBadgeNumber(audiences.size());
        } else {
            badge.bindTarget(waiterButton).setBadgeNumber(audiences.size());
        }

        if (audienceCountText != null) {
            audienceCountText.setText("所有参会人 (" + audiences.size() + ")");
        }
    }

    @Override
    public void onConnectionLost() {
        showToolBarsHandler.sendEmptyMessage(4);
    }

    @Override
    public void onConnectionInterrupted() {
        /*runOnUiThread(() -> Toast.makeText(MeetingBroadcastActivity.this, "网络连接不佳，视频将会有卡顿，可尝试降低分辨率", Toast.LENGTH_SHORT).show());*/
    }

    @Override
    public void onUserMuteVideo(final int uid, final boolean muted) {
		/*if (BuildConfig.DEBUG) {
			runOnUiThread(() -> Toast.makeText(MeetingBroadcastActivity.this, uid + " 的视频被暂停了 " + muted, Toast.LENGTH_SHORT).show());
		}*/
        runOnUiThread(() -> {
            remoteIsOpen = muted;
            onUserMuteVideoUid = uid;
            for (AudienceVideo audienceVideo : mVideoAdapter.getAudienceVideoLists()) {
                if (audienceVideo.getUid() == uid && !TextUtils.equals(meetingJoin.getHostUser().getClientUid(), String.valueOf(uid))) {
                    audienceVideo.setOpenVideo(muted);
                    mVideoAdapter.notifyDataSetChanged();
                    return;
                }
            }

            if (muted && isSwitch) {
                broadcasterLayout.removeAllViews();
                broadcasterLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_color));
                broadcasterLayout.setForeground(ContextCompat.getDrawable(this, R.drawable.tx));
            } else {
                broadcasterLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
                broadcasterLayout.setForeground(null);
            }

        });
    }

    @Override
    public void onUserMuteAudio(int uid, boolean muted) {
        runOnUiThread(() -> {
            mVideoAdapter.setMutedStatusByUid(uid, muted);
        });
    }

    @Override
    public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {
        if (BuildConfig.DEBUG) {
            for (int i = 0; i < speakers.length; i++) {
                int finalI = i;
				/*runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ToastUtils.showToast("当前说话的人有" + speakers[finalI].uid + "----" + speakers[finalI].volume);
					}
				});*/
            }
        }

//        runOnUiThread(() -> {
//            for (IRtcEngineEventHandler.AudioVolumeInfo audioVolumeInfo : speakers) {
//                mVideoAdapter.setVolumeByUid(audioVolumeInfo.uid, audioVolumeInfo.volume);
//            }
//        });

    }

    @Override
    public void onLastmileQuality(final int quality) {

    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        if (BuildConfig.DEBUG) {
            runOnUiThread(() -> {
                showNetQuality(rxQuality);
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
    public void onError(final int err) {
        if (BuildConfig.DEBUG) {
            runOnUiThread(() -> {
//				Toast.makeText(MeetingBroadcastActivity.this, "错误码：" + err, Toast.LENGTH_SHORT).show();
                mLogger.e("错误码：  " + err);
            });

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        worker().preview2(false, null, config().mUid);
		/*doLeaveChannel();

		currentMaterial = null;

		if (agoraAPI.getStatus() == 2) {
			agoraAPI.channelDelAttr(channelName, DOC_INFO);
			if (currentAudience != null) {
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("finish", true);
					agoraAPI.messageInstantSend("" + currentAudience.getUid(), 0, jsonObject.toString(), "");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			agoraAPI.logout();
		}

		finish();*/
    }

    @Override
    public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {

    }

    @Override
    public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {

    }

    @Override
    public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {

    }

    @Override
    public void onBackPressed() {
//        showDialog(1, "确定结束会议吗？", "暂时离开", "结束会议", null);
        showExitDialog();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        event().removeEventHandler(this);
        if (mUSBMonitor != null) {
            mUSBMonitor.unregister();
        }


        TCAgent.onPageEnd(this, TAG);

        unregisterReceiver(mUsbReceiver);
        unregisterReceiver(homeKeyEventReceiver);

        currentMaterial = null;

        if (agoraAPI.getStatus() == 2) {
            agoraAPI.channelClearAttr(channelName);
            if (currentAudience != null) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("finish", true);
                    agoraAPI.messageInstantSend("" + currentAudience.getUid(), 0, jsonObject.toString(), "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                currentAudience = null;
            }
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
        rtcEngine().stopPreview();
        if (subscription != null) {
            subscription.unsubscribe();
        }

        rtcEngine().setupLocalVideo(new VideoCanvas(null, LOCAL_RENDER_MODE, config().mUid));

        if (whiteboardView != null) {
            whiteboardView.removeAllViews();
            whiteboardView.destroy();
        }
        stopTime();

        worker().preview2(false, null, config().mUid);
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

                    doLeaveChannel();
                    if (agoraAPI.getStatus() == 2) {
                        agoraAPI.channelDelAttr(channelName, DOC_INFO);
                        if (currentAudience != null) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("finish", true);
                                agoraAPI.messageInstantSend("" + currentAudience.getUid(), 0, jsonObject.toString(), "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        currentAudience = null;
                        agoraAPI.logout();
                    }
                    agoraAPI.destroy();
                } else if (TextUtils.equals(reason, RECENTAPPS)) {
                    // 点击 菜单键
                    if (BuildConfig.DEBUG)
                        Toast.makeText(getApplicationContext(), "您点击了菜单键", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public void finish() {
        hideMeetingCameraImageLayout();
        RxBus.sendMessage(new ForumActivityCloseEvent());
        super.finish();
    }

    private void changeViewLayout() {

        int dataSize = mVideoAdapter.getDataSize();
        mLogger.e("集合大小：%d", dataSize);
        mLogger.e(currentMaterial == null);

        if (dataSize == 1) {
            exitSpliteMode();
            return;
        }

        recycleviewContainer.setBackground(getResources().getDrawable(R.drawable.bj));

        mDelegateAdapter.clear();
        if (currentMaterial == null) {
            mAudienceRecyclerView.removeItemDecoration(mDecor);
            SpaceItemDecoration decor1 = new SpaceItemDecoration(0, 0, 0, 0);
            mAudienceRecyclerView.removeItemDecoration(decor1);
            mAudienceRecyclerView.addItemDecoration(decor1);
            if (mVideoAdapter.getDataSize() == 3) {
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
            MyGridLayoutHelper mGridLayoutHelper = new MyGridLayoutHelper(2);
            mGridLayoutHelper.setItemCount(16);
            mGridLayoutHelper.setAutoExpand(false);
            mAudienceRecyclerView.removeItemDecoration(mDecor);
            mAudienceRecyclerView.removeItemDecoration(mGridSpaceItemDecoration);
            mAudienceRecyclerView.addItemDecoration(mDecor);
            mVideoAdapter.setLayoutHelper(mGridLayoutHelper);
        }

        mVideoAdapter.notifyDataSetChanged();
        mDelegateAdapter.addAdapter(mVideoAdapter);
    }


    private void exitPPT() {


        findViewById(R.id.app_video_box).setVisibility(View.GONE);

        if (mVideoAdapter.getDataSize() > 0) {
            mAudienceRecyclerView.setVisibility(View.VISIBLE);
            mVideoAdapter.setVisibility(View.VISIBLE);
        }
        mVideoAdapter.setSurfaceViewVisibility(true);
        isUsePPTModel = false;
        if (mVideoAdapter.isHaveChairMan() && !isSplitMode) {

            mLogger.e("mCurrentAudienceVideo==null" + mCurrentAudienceVideo);
            if (mCurrentAudienceVideo != null) {
                /**TextureView*/
                TextureView surfaceView = mCurrentAudienceVideo.getTextureView();
                stripSurfaceView(surfaceView);
                broadcasterLayout.setVisibility(View.VISIBLE);
                broadcasterLayout.removeAllViews();
                broadcasterLayout.addView(surfaceView);
            } else {

                int chairManPosition = mVideoAdapter.getChairManPosition();
                if (chairManPosition != -1) {
                    mVideoAdapter.removeItem(chairManPosition);
                }

                localBroadcasterSurfaceView.setVisibility(View.VISIBLE);
//                localBroadcasterSurfaceView.setZOrderOnTop(false);
//                localBroadcasterSurfaceView.setZOrderMediaOverlay(false);
                broadcasterLayout.removeAllViews();
                stripSurfaceView(localBroadcasterSurfaceView);
                broadcasterLayout.addView(localBroadcasterSurfaceView);
                broadcasterLayout.setVisibility(View.VISIBLE);


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
        } else {
            //如果是分屏模式 清除掉ppt的时候 需要重新分屏
            if (isSplitMode) {
                // pptModel:  0    mVideoAdapter.getDataSize():  1 只有主持人在列表中
                // pptModel: 1    mVideoAdapter.getDataSize():  0  参会人观众主持人 都不在列表中
                //pptModel:2    mVideoAdapter.getDataSize():  0  //观众  参会人 主持人 都不在列表中

                mLogger.e("isSplitMode:   " + isSplitMode + "-----  pptModel:  " + pptModel + "--------   mVideoAdapter.getDataSize():  " + mVideoAdapter.getDataSize());

                if (pptModel == 0 && mVideoAdapter.getDataSize() == 1) {
                    if (mVideoAdapter.isHaveChairMan()) {
                        mVideoAdapter.removeItem(mVideoAdapter.getChairManPosition());
                    }
                    stripSurfaceView(localBroadcasterSurfaceView);
                    broadcasterLayout.removeAllViews();
                    localBroadcasterSurfaceView.setVisibility(View.VISIBLE);
                    broadcasterLayout.addView(localBroadcasterSurfaceView);
                    broadcasterLayout.setVisibility(View.VISIBLE);
                    mBroadCastContainer.setVisibility(View.GONE);
                } else if (pptModel == 2 || pptModel == 1) {
                    if (mVideoAdapter.getDataSize() == 0) {
                        if (mVideoAdapter.isHaveChairMan()) {
                            mVideoAdapter.removeItem(mVideoAdapter.getChairManPosition());
                        }
                        stripSurfaceView(localBroadcasterSurfaceView);
                        broadcasterLayout.removeAllViews();
                        localBroadcasterSurfaceView.setVisibility(View.VISIBLE);
                        broadcasterLayout.addView(localBroadcasterSurfaceView);
                        broadcasterLayout.setVisibility(View.VISIBLE);
                        mBroadCastContainer.setVisibility(View.GONE);
                    } else {
                        SpliteViews();
                        mSplitView.setText("退出均分");
                    }
                } else {
                    SpliteViews();
                    mSplitView.setText("退出均分");
                }
            } else {
                if (!mVideoAdapter.isHaveChairMan()) {
                    if (mCurrentAudienceVideo != null) {
                        /**TextureView*/
                        TextureView surfaceView = mCurrentAudienceVideo.getTextureView();
                        stripSurfaceView(surfaceView);
                        broadcasterLayout.setVisibility(View.VISIBLE);
                        broadcasterLayout.removeAllViews();
                        broadcasterLayout.addView(surfaceView);
                    } else {

                        int chairManPosition = mVideoAdapter.getChairManPosition();
                        if (chairManPosition != -1) {
                            mVideoAdapter.removeItem(chairManPosition);
                        }

                        /**TextureView*/
                        localBroadcasterSurfaceView.setVisibility(View.VISIBLE);
//                        localBroadcasterSurfaceView.setZOrderOnTop(false);
//                        localBroadcasterSurfaceView.setZOrderMediaOverlay(false);
                        broadcasterLayout.removeAllViews();
                        stripSurfaceView(localBroadcasterSurfaceView);
                        broadcasterLayout.addView(localBroadcasterSurfaceView);
                        broadcasterLayout.setVisibility(View.VISIBLE);

                    }
                }

            }
        }

        mBroadCastContainer.setVisibility(View.GONE);
        docImage.setVisibility(View.GONE);
        whiteboardView.setVisibility(View.GONE);
        pageText.setVisibility(View.GONE);
        previewButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
        exitDocButton.setVisibility(View.GONE);
        broadcasterSmailLayout.removeView(localBroadcasterSurfaceView);


        if (isSplitMode && !isUsePPTModel) {
            full_screen.setVisibility(View.GONE);
        } else {
            full_screen.setVisibility(View.VISIBLE);
        }

        full_screen.setText(isFullScreen ? "恢复" : "全屏");
        //此时退出ppt模式 重置pptmodel
        pptModel = -1;

        if (isFullScreen) {
            stopButton.setVisibility(View.INVISIBLE);
            mAudienceRecyclerView.setVisibility(View.GONE);
            mVideoAdapter.setVisibility(View.GONE);
        } else {
            if (currentAiducenceId != -1 && currentAudience != null) {
                stopButton.setVisibility(View.VISIBLE);
            } else {
                stopButton.setVisibility(View.INVISIBLE);
            }
            mAudienceRecyclerView.setVisibility(View.VISIBLE);
            mVideoAdapter.setVisibility(View.VISIBLE);
        }
        currentMaterial = null;

        if (isSplitMode) {
            pptButton.requestFocus();
            pptButton.requestFocusFromTouch();
        }
        mSplitView.setVisibility(View.VISIBLE);
    }

    /**
     * 当用户退出 或者变成观众的时候
     * 需要判断主持人是否在列表中
     * 如果主持人在列表中 就需要将主持人放大
     * 然后列表中移除参会人
     */
    public void changeView(FrameLayout broadcasterLayout, TextureView broadCastView) {

        if (mVideoAdapter.isHaveChairMan()) {
            int chairManPosition = mVideoAdapter.getChairManPosition();

            if (chairManPosition != -1) {
                AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(chairManPosition);
                if (audienceVideo != null && audienceVideo.getSurfaceView() != null) {
                    audienceVideo.getSurfaceView().setZOrderMediaOverlay(false);
                    audienceVideo.getSurfaceView().setZOrderOnTop(false);
                }
                mVideoAdapter.deleteItem(chairManPosition);
                stripSurfaceView(broadCastView);
                broadcasterLayout.removeAllViews();
                broadcasterLayout.addView(broadCastView);
            }
        }
    }

    private USBMonitor mUSBMonitor;
    private boolean needMirrorMode = false;
    private final USBMonitor.OnDeviceConnectListener mConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(UsbDevice device) {
		/*	mLogger.e("USBMonitor.OnDeviceConnectListener    onAttach" + JSON.toJSONString(device));

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				if (device.getProductName().equals("TCUB60")) {
					needMirrorMode = true;
					mLogger.e("needMirrorMode:11111   " + needMirrorMode);
					rtcEngine().setLocalVideoMirrorMode(Constants.VIDEO_MIRROR_MODE_ENABLED);
				} else {
					mLogger.e("needMirrorMode:2222  " + needMirrorMode);
					if (!needMirrorMode) {
						rtcEngine().setLocalVideoMirrorMode(Constants.VIDEO_MIRROR_MODE_DISABLED);
					}
				}
			} else {
				if (!needMirrorMode) {
					rtcEngine().setLocalVideoMirrorMode(Constants.VIDEO_MIRROR_MODE_DISABLED);
				}

			}*/


        }

        @Override
        public void onDettach(UsbDevice device) {
            mLogger.e("USBMonitor.OnDeviceConnectListener    onDettach");
        }

        @Override
        public void onConnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock, boolean createNew) {
            mLogger.e("USBMonitor.OnDeviceConnectListener    onConnect");
        }

        @Override
        public void onDisconnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock) {
            mLogger.e("USBMonitor.OnDeviceConnectListener    onDisconnect");
        }

        @Override
        public void onCancel(UsbDevice device) {
            mLogger.e("USBMonitor.OnDeviceConnectListener    device");
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getAction()) {
            case KeyEvent.ACTION_DOWN:
                if (showToolBarsHandler.hasMessages(0)) {
                    showToolBarsHandler.removeMessages(0);
                }
                if (findViewById(R.id.linearLayout2).getVisibility() == View.VISIBLE) {
                    showToolBarsHandler.sendEmptyMessageDelayed(0, Constant.delayTime);
                } else if (findViewById(R.id.linearLayout2).getVisibility() == View.GONE) {
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
                if (findViewById(R.id.linearLayout2).getVisibility() == View.VISIBLE) {
                    showToolBarsHandler.sendEmptyMessageDelayed(0, Constant.delayTime);
                } else if (findViewById(R.id.linearLayout2).getVisibility() == View.GONE) {
                    showToolBarsHandler.sendEmptyMessage(1);
                }
                break;

        }
        return super.dispatchTouchEvent(ev);
    }


    /**
     * 非全屏状态，画面背景为ppt，主持人 参会人悬浮在ppt内容上
     * <p>
     * **需要判断集合大小，先讲主持人加入到集合中再判断  如果大于8人  就不显示自己  如果小于8人，所以的都显示
     */
    private void notFullScreenState() {
        full_screen.setText("非全屏");
        pptModel = 0;
        if (!mVideoAdapter.isHaveChairMan()) {
            AudienceVideo audienceVideo = new AudienceVideo();
            audienceVideo.setUid(config().mUid);
            audienceVideo.setName("主持人" + meetingJoin.getHostUser().getHostUserName());
            audienceVideo.setBroadcaster(true);
            /**TextureView*/
            audienceVideo.setTextureView(localBroadcasterSurfaceView);
            mVideoAdapter.insertItem(0, audienceVideo);
            mVideoAdapter.notifyDataSetChanged();
        } else {
            if (mCurrentAudienceVideo != null) {
                mVideoAdapter.insertItem(mCurrentAudienceVideo);
            }
            mCurrentAudienceVideo = null;
        }

        if (mVideoAdapter.getDataSize() > 8) {
            int chairManPosition = mVideoAdapter.getChairManPosition();
            if (mVideoAdapter.getChairManPosition() != -1) {
                mVideoAdapter.getAudienceVideoLists().get(chairManPosition).getSurfaceView().setVisibility(View.GONE);
                mVideoAdapter.removeItem(mVideoAdapter.getChairManPosition());
            }
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(AutoSizeUtils.pt2px(this, 300), RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, DisplayUtil.dip2px(this, 20), 0);
        mAudienceRecyclerView.setLayoutParams(layoutParams);

        mDelegateAdapter.clear();
        mAudienceRecyclerView.removeItemDecoration(mDecor);
        mAudienceRecyclerView.addItemDecoration(mDecor);
        mAudienceRecyclerView.removeItemDecoration(mGridSpaceItemDecoration);
        LinearLayoutHelper helper = new LinearLayoutHelper();
        helper.setItemCount(16);
        mVideoAdapter.setLayoutHelper(helper);

        mDelegateAdapter.addAdapter(mVideoAdapter);
        mVideoAdapter.notifyDataSetChanged();


        mVideoAdapter.setVisibility(View.VISIBLE);
        mAudienceRecyclerView.setVisibility(View.VISIBLE);

        broadcasterSmailLayout.setVisibility(View.GONE);
        mBroadCastContainer.setVisibility(View.GONE);

    }

    /**
     * 全屏状态：画面背景为PPT内容，右下角悬浮自己的画面 悬浮画面可以拖动
     */
    private void FullScreenState() {
        full_screen.setText("全屏");
        pptModel = 1;
        //如果当前列表里面有主持人 则需要将主持人拿出来放在右下角  然后将大的参会人放在列表中去
        if (mVideoAdapter.isHaveChairMan()) {
            int chairManPosition = mVideoAdapter.getChairManPosition();
            if (chairManPosition != -1) {
                mVideoAdapter.getAudienceVideoLists().remove(chairManPosition);
                mVideoAdapter.notifyDataSetChanged();
            }
            if (mCurrentAudienceVideo != null) {
                mVideoAdapter.insertItem(mCurrentAudienceVideo);
                mCurrentAudienceVideo = null;
            }
        }


        mAudienceRecyclerView.setVisibility(View.GONE);
        mVideoAdapter.setVisibility(View.GONE);

        broadcasterSmailLayout.setVisibility(View.VISIBLE);
        mBroadCastContainer.setVisibility(View.VISIBLE);

        broadcasterSmailLayout.removeAllViews();
        localBroadcasterSurfaceView.setVisibility(View.VISIBLE);
        /**TextureView*/
//        localBroadcasterSurfaceView.setZOrderOnTop(true);
//        localBroadcasterSurfaceView.setZOrderMediaOverlay(true);
        stripSurfaceView(localBroadcasterSurfaceView);

        broadcasterSmailLayout.addView(localBroadcasterSurfaceView);
    }

    /**
     * 隐藏浮窗状态：画面只有PPT内容；
     */
    private void clearAllState() {
        full_screen.setText("隐藏浮窗");
        pptModel = 2;
        mVideoAdapter.setVisibility(View.GONE);
        mAudienceRecyclerView.setVisibility(View.GONE);

        broadcasterSmailLayout.setVisibility(View.GONE);
        mBroadCastContainer.setVisibility(View.GONE);
    }

    private void setTextViewDrawableTop(TextView view, int drawable) {
        Drawable top = getResources().getDrawable(drawable);
        view.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
    }


    /**
     * 播放视频
     */
    public void PlayVideo() {
        mPreviewPlayer = findViewById(R.id.jz_video);

        mVideoContainer.setVisibility(View.VISIBLE);
        docImage.setVisibility(View.GONE);
        whiteboardView.setVisibility(View.GONE);
        mPlayButton.setVisibility(View.VISIBLE);
        findViewById(R.id.app_video_box).setVisibility(View.VISIBLE);


        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPreviewPlayer != null) {
                    mPreviewPlayer.startButton.performClick();
                    showToolBarsHandler.sendEmptyMessage(2);
					/*if (mPreviewPlayer.getCurrentState() != JzvdStd.STATE_PLAYING) {
						agoraAPI.channelSetAttr(channelName, Constant.VIDEO, Constant.STOPVIDEO);

					} else {
						agoraAPI.channelSetAttr(channelName, Constant.VIDEO, Constant.STOPVIDEO);

					}*/
                }
            }
        });

        mPreviewPlayer.setOnVideoPlayChangeListener(new PreviewPlayer.OnVideoPlayChangeListener() {
            @Override
            public void play() {
                agoraAPI.channelSetAttr(channelName, Constant.VIDEO, Constant.PLAYVIDEO);
                mPlayButton.setText("播放");
                setTextViewDrawableTop(mPlayButton, R.drawable.bg_meeting_video_play_selector);
            }

            @Override
            public void pause() {
                agoraAPI.channelSetAttr(channelName, Constant.VIDEO, Constant.PAUSEVIDEO);
                mPlayButton.setText("暂停");
                setTextViewDrawableTop(mPlayButton, R.drawable.bg_meeting_video_pause_selector);
            }

            @Override
            public void complete() {
                agoraAPI.channelSetAttr(channelName, Constant.VIDEO, Constant.STOPVIDEO);
            }
        });

        mPreviewPlayer.setUp(currentMaterial.getMeetingMaterialsPublishList().get(0).getUrl(), "", JzvdStd.SCREEN_NORMAL);
        mPreviewPlayer.getSeekBar().setVisibility(View.GONE);
        mPreviewPlayer.getCurrentTimeView().setVisibility(View.GONE);
        mPreviewPlayer.getTotalTimeView().setVisibility(View.GONE);
        mPreviewPlayer.getFullScreenView().setVisibility(View.GONE);

        String imageUrl = ImageHelper.videoImageFromUrl(currentMaterial.getMeetingMaterialsPublishList().get(0).getUrl()
                , AutoSizeUtils.dp2px(MeetingBroadcastActivity.this, 300)
                , AutoSizeUtils.dp2px(MeetingBroadcastActivity.this, 400));
        Picasso.with(MeetingBroadcastActivity.this).load(imageUrl)
                .error(R.drawable.item_forum_img_error)
                .placeholder(R.drawable.item_forum_img_loading)
                .into(mPreviewPlayer.thumbImageView);

    }

    private void stopPlayVideo() {
        JzvdStd.releaseAllVideos();
        agoraAPI.channelSetAttr(channelName, Constant.VIDEO, Constant.STOPVIDEO);
        mPlayButton.setText("播放");
        setTextViewDrawableTop(mPlayButton, R.drawable.icon_play);

    }

    private void setAudienceVideoRequestFocus() {
        if (mVideoAdapter != null && mVideoAdapter.getItemCount() > 0) {
            mAudienceRecyclerView.requestFocus();
        }
    }

}
