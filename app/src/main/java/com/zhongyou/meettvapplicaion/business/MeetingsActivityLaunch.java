package com.zhongyou.meettvapplicaion.business;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.usb.UsbDevice;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.serenegiant.usb.USBMonitor;
import com.zhongyou.meettvapplicaion.ApiClient;
import com.zhongyou.meettvapplicaion.BaseException;
import com.zhongyou.meettvapplicaion.BuildConfig;
import com.zhongyou.meettvapplicaion.Constant;
import com.zhongyou.meettvapplicaion.NetWorkUtils;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.business.adapter.MeetingLaunchAdapter;
import com.zhongyou.meettvapplicaion.core.PlayService;
import com.zhongyou.meettvapplicaion.entities.Agora;
import com.zhongyou.meettvapplicaion.entities.Bucket;
import com.zhongyou.meettvapplicaion.entities.HostUser;
import com.zhongyou.meettvapplicaion.entities.MeeetingAdmin;
import com.zhongyou.meettvapplicaion.entities.Meeting;
import com.zhongyou.meettvapplicaion.entities.MeetingJoin;
import com.zhongyou.meettvapplicaion.entities.base.BaseArrayBean;
import com.zhongyou.meettvapplicaion.entity.NewMeetingJoin;
import com.zhongyou.meettvapplicaion.entity.RecomandData;
import com.zhongyou.meettvapplicaion.im.IMInfoProvider;
import com.zhongyou.meettvapplicaion.maker.DialogUtils;
import com.zhongyou.meettvapplicaion.maker.MakerDetailActivity;
import com.zhongyou.meettvapplicaion.maker.MakerIndexActivity;
import com.zhongyou.meettvapplicaion.net.OkHttpBaseCallback;
import com.zhongyou.meettvapplicaion.network.HttpsRequest;
import com.zhongyou.meettvapplicaion.network.LoadingDialog;
import com.zhongyou.meettvapplicaion.network.RxSchedulersHelper;
import com.zhongyou.meettvapplicaion.network.RxSubscriber;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.Logger;
import com.zhongyou.meettvapplicaion.utils.Login.LoginHelper;
import com.zhongyou.meettvapplicaion.utils.OkHttpCallback;
import com.zhongyou.meettvapplicaion.utils.SizeUtils;
import com.zhongyou.meettvapplicaion.utils.SpUtil;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;
import com.zhongyou.meettvapplicaion.utils.UIDUtil;
import com.zhongyou.meettvapplicaion.utils.statistics.ZYAgent;
import com.zhongyou.meettvapplicaion.view.CircleImageView;
import com.zhongyou.meettvapplicaion.view.FocusFixedLinearLayoutManager;
import com.zhongyou.meettvapplicaion.view.GeneralAdapter;
import com.zhongyou.meettvapplicaion.view.RecyclerViewTV;
import com.zhongyou.meettvapplicaion.view.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.bgabanner.BGABanner;
import io.agora.openlive.ui.MeetingInitActivity;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class MeetingsActivityLaunch extends BasisActivity implements RecyclerViewTV.OnItemListener {

	private static final String TAG = MeetingsActivity.class.getSimpleName();
	public int mCurrentPosition = 0;
	//    private ImageView tipsImage;
	private RecyclerViewTV recyclerViewTV;
	private TextView tvOpen, tvInvited, tvVersion, tvStart, tvExpostor, tvSchool, tvSetting, tvUser, btSetting;
	private TextView imgOpen, imgInvited;
	private RelativeLayout imgNoMeeting;
	private MeetingLaunchAdapter meetingAdapter;
	private int type = 0;
	ArrayList<Meeting> oldMeetings = new ArrayList<>();
	private boolean mMin = false;
	private int tempNum = 0;
	private static long UPDATE_SPACE = 60000;
	private int mIntentType;
	private SizeUtils mSizeUtils;
	private BGABanner mBGABanner;
	List<String> permissions = new ArrayList<String>();
	private int nextItem = 0;
	private View mLastSelectedLableView;
	USBMonitor usbMonitor;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mMin = true;
			tempNum = tempNum + 1;
//            Log.v("meetingsactivityl","发送handler=="+tempNum);
			ApiClient.getInstance().getMeetingByCategory(TAG, type, meetingsCallback);
			handler.sendEmptyMessageDelayed(10, UPDATE_SPACE);
			Log.v("meetingsactivityl", "发送handler handlermessage  *****************");

		}
	};
	private CircleImageView mUserFace;
	private ImageView mSearchImageView;
	private String mMeetingName;
	private ServiceConnection serviceConn;
	private RecomandData mRecomandData;

	@Override
	public String getStatisticsTag() {
		return null;
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_meeting_launch);
		if (Preferences.getUserId() == null || Preferences.getUserId().length() <= 0) {
			ToastUtils.showToast("登陆信息已过期 请重新登陆");
			startActivity(new Intent(this, SignInActivity.class));
			finish();
		}
		initPlayService();
		IMInfoProvider infoProvider = new IMInfoProvider();
		infoProvider.init(this);


		UserInfo info = new UserInfo(Preferences.getUserId(), Preferences.getUserName(), Uri.parse(Preferences.getUserPhoto()));

		RongIM.getInstance().refreshUserInfoCache(info);
		RongIM.getInstance().setCurrentUserInfo(info);
		RongIM.getInstance().setMessageAttachedUserInfo(true);

		initView();
		init();

		askPermission();

		mSizeUtils = new SizeUtils(this);
		ApiClient.getInstance().getMeetingByCategory(TAG, 0, meetingsCallback);

		mLogger.e(Constant.isPadApplicaion ? "当前设备是平板设备" : "当前设备是电视");

		Log.v("meetingsactivityl", "callback  *****************发送handler==");
//		mLogger.e("SDKVerion:" + RtcEngine.getSdkVersion());

		com.zhongyou.meettvapplicaion.net.ApiClient.getInstance().requestMeetingAdmin(this, new OkHttpBaseCallback<Bucket<MeeetingAdmin>>() {

			@Override
			public void onSuccess(Bucket<MeeetingAdmin> entity) {
				MeeetingAdmin meeetingAdmin = entity.getData();
				if (meeetingAdmin.isMeetingAdmin()) {
					Constant.isAdminCount = true;
				} else {
					Constant.isAdminCount = false;
				}
			}
		});

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

	private void initPlayService() {
		//播放服务
		Intent intent = new Intent(this, PlayService.class);
		serviceConn = new ServiceConnection() {
			//连接异常断开
			public void onServiceDisconnected(ComponentName name) {

			}

			//连接成功
			public void onServiceConnected(ComponentName name, IBinder binder) {
				PlayService.MusicBinder musicBinder = (PlayService.MusicBinder) binder;
				MakerDetailActivity.setMusicBinder(musicBinder);
				MakerIndexActivity.setMusicBinder(musicBinder);
			}
		};

		bindService(intent, serviceConn, Service.BIND_AUTO_CREATE);
	}


	private boolean askPermission() {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			int RECORD_AUDIO = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
			if (RECORD_AUDIO != PackageManager.PERMISSION_GRANTED) {
				permissions.add(Manifest.permission.RECORD_AUDIO);
			}

			if (!permissions.isEmpty()) {
				requestPermissions(permissions.toArray(new String[permissions.size()]), 1);
			} else
				return false;
		} else
			return false;
		return true;

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == 1) {

			boolean result = true;
			for (int i = 0; i < permissions.length; i++) {
				result = result && grantResults[i] == PackageManager.PERMISSION_GRANTED;
			}
			if (!result) {

				Toast.makeText(this, "授权结果（至少有一项没有授权），result=" + result, Toast.LENGTH_LONG).show();
				// askPermission();
			} else {
				//授权成功
			}
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}


//
//    @Override
//    protected String pageName() {
//        return null;
//    }

	@Override
	protected void onRestart() {
		super.onRestart();
		mMin = false;
		ApiClient.getInstance().getMeetingByCategory(TAG, type, meetingsCallback);
	}

	@Override
	protected void onStart() {
		super.onStart();
		handler.sendEmptyMessageDelayed(10, UPDATE_SPACE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (handler.hasMessages(10)) {
			handler.removeMessages(10);
		}
	}

	private int times = 0;

	private void initView() {
		TextView quickJoin = findViewById(R.id.quickJoin);
		TextView quickJoinLine = findViewById(R.id.quickJoinLine);
		mBGABanner = findViewById(R.id.banner);
		imgNoMeeting = (RelativeLayout) this.findViewById(R.id.tips);
		tvStart = (TextView) this.findViewById(R.id.tv_start);
		tvOpen = (TextView) this.findViewById(R.id.tv_open);
		tvInvited = (TextView) this.findViewById(R.id.tv_invited);
		imgInvited = (TextView) this.findViewById(R.id.invited_slide);
		imgOpen = (TextView) this.findViewById(R.id.open_slide);
		tvExpostor = (TextView) this.findViewById(R.id.tv_exp);
		tvSchool = (TextView) this.findViewById(R.id.tv_school);
		tvSetting = (TextView) this.findViewById(R.id.tv_setting);
		tvVersion = (TextView) this.findViewById(R.id.tv_version);
		tvUser = (TextView) this.findViewById(R.id.tv_user);
		mSearchImageView = this.findViewById(R.id.searchImageView);
		mUserFace = findViewById(R.id.user_face);
		String headImgeUser;
		if (TextUtils.isEmpty(Preferences.getUserPhoto())) {
			headImgeUser = Preferences.getWeiXinHead();
		} else {
			headImgeUser = Preferences.getUserPhoto();
		}
		Glide.with(this).asBitmap().load(headImgeUser).into(mUserFace);

		tvVersion.setText("中幼在线 " + "版本" + BuildConfig.VERSION_NAME);
		btSetting = findViewById(R.id.bt_setting);
		tvUser.setText(Preferences.getAreaName() + " " + Preferences.getUserName());
		tvStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
//                startActivity(new Intent(getApplication(),StartMeetingDialog.class));
			}
		});
		//设置最后一个选中的按钮是公开会议
		mLastSelectedLableView = tvOpen;
		findViewById(R.id.img).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				times++;
				if (times == 7) {
					TextView debugVersion = findViewById(R.id.version_debug);
					debugVersion.setVisibility(View.VISIBLE);
					debugVersion.setText("versionName: " + BuildConfig.VERSION_NAME + "\t" + "debug:  " + BuildConfig.DEBUG);
					times = 0;
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							debugVersion.setVisibility(View.GONE);
						}
					}, 3 * 1000);
				}

			}
		});


		tvOpen.setFocusable(true);
		tvInvited.setFocusable(true);
		quickJoin.setFocusable(true);

		if (Constant.isPadApplicaion) {
			tvInvited.setOnClickListener(v -> {
				tvInvited.setFocusable(true);
				tvInvited.setFocusableInTouchMode(true);
				tvInvited.requestFocus();
			});

			tvOpen.setOnClickListener(v -> {
				tvOpen.setFocusable(true);
				tvOpen.setFocusableInTouchMode(true);
				tvOpen.requestFocus();
			});
		}

		/*mBGABanner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mBGABanner.getItemImageView(mBGABanner.getCurrentItem()).requestFocus();
				}
			}
		});*/


		tvOpen.requestFocus();
		tvOpen.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View view, int keycode, KeyEvent keyEvent) {

				if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
					imgOpen.setVisibility(View.VISIBLE);
					imgInvited.setVisibility(View.INVISIBLE);
				}

				if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
					btSetting.setFocusable(true);
				}
				if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
					return true;
				}
				return false;
			}
		});
		tvInvited.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
				Log.e(TAG, "onKey: tvInvited" + keyEvent.getKeyCode());
				if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
					imgOpen.setVisibility(View.INVISIBLE);
					imgInvited.setVisibility(View.VISIBLE);

				}
				if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
					mSearchImageView.setFocusable(true);
					quickJoin.setFocusable(true);
					return false;
				}
				if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
					tvOpen.setFocusable(true);
				}
				if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
					btSetting.setFocusable(true);
				}


				return false;
			}
		});

		mSearchImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				View view = View.inflate(MeetingsActivityLaunch.this, R.layout.dialog_search, null);
				EditText search = view.findViewById(R.id.edit_text);
				ImageButton confirm = view.findViewById(R.id.confirm);
				ImageButton cancel = view.findViewById(R.id.cancel);
				confirm.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (search.getText().toString().trim().length() <= 0) {
							ToastUtils.showToast("请输入关键字搜索");
						} else {
							InputMethodManager inputmanger = (InputMethodManager) MeetingsActivityLaunch.this
									.getSystemService(Context.INPUT_METHOD_SERVICE);
							inputmanger.hideSoftInputFromWindow(search.getWindowToken(), 0);
							Intent intent = new Intent(MeetingsActivityLaunch.this, SearchActivity.class);
							intent.putExtra("keyWords", search.getText().toString().trim());
							startActivity(intent);
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
				if (dialog == null) {
					dialog = new Dialog(MeetingsActivityLaunch.this, R.style.CustomDialog);
				}
				dialog.setContentView(view);
				dialog.setCanceledOnTouchOutside(true);
				dialog.setCancelable(true);
				dialog.show();
			}
		});
		mSearchImageView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.e(TAG, "onKey: " + keyCode);
				if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
					btSetting.setFocusable(true);
					btSetting.setFocusableInTouchMode(true);
				}
				return false;
			}
		});

		btSetting.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.e(TAG, "onKey:btSetting " + event.getKeyCode());
				if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
					return true;
				} else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
					isSettingSelected = true;
					mLastSelectedLableView.setFocusable(true);
					mLastSelectedLableView.requestFocus();


					return true;
				}
				return false;
			}
		});

		tvSchool.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				switch (event.getKeyCode()) {
					case KeyEvent.KEYCODE_DPAD_UP:
						if (recyclerViewTV.getVisibility() == View.VISIBLE) {
							if (recyclerViewTV.getChildAt(mCurrentPosition) == null) {
								if (recyclerViewTV.getChildAt(0) == null){
									return false;
								}
								recyclerViewTV.getChildAt(0).requestFocus();
							} else {
								recyclerViewTV.getChildAt(mCurrentPosition).requestFocus();
							}
							return true;
						} else {
							if (type == 0) {
								tvOpen.setFocusable(true);
							} else if (type == 1) {
								tvInvited.setFocusable(true);
							}
						}
						break;
					default:
						return false;
				}
				return false;
			}
		});


		btSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(getApplication(), SettingActivity.class), 0x101);
			}
		});
		tvSchool.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(getApplication(), MakerIndexActivity.class));
			}
		});
		tvSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivityForResult(new Intent(getApplication(), SettingActivity.class), 0x101);
			}
		});
		tvExpostor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
//				startActivity(new Intent(getApplication(), HomeTvActivity.class));
			}
		});
		tvExpostor.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
				if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {

					if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
						tvSetting.setFocusable(true);
						tvSetting.requestFocus();
						return true;
					}

				}

				return false;
			}
		});
		tvOpen.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (b) {
					mLastSelectedLableView = tvOpen;
					mMin = false;
					mCurrentPosition = 0;
					tvOpen.setTextColor(getResources().getColor(R.color.blue));
					tvInvited.setTextColor(getResources().getColor(R.color.white));
					imgInvited.setVisibility(View.INVISIBLE);
					imgOpen.setVisibility(View.VISIBLE);


					type = 0;
					ApiClient.getInstance().getMeetingByCategory(TAG, type, meetingsCallback);
				} else {
					tvOpen.setTextColor(getResources().getColor(R.color.white));
					/*imgOpen.setVisibility(View.INVISIBLE);*/
				}
			}
		});
		tvInvited.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (b) {
					mLastSelectedLableView = tvInvited;
					mMin = false;
					mCurrentPosition = 0;
					tvInvited.setTextColor(getResources().getColor(R.color.blue));
					tvOpen.setTextColor(getResources().getColor(R.color.white));
					imgOpen.setVisibility(View.INVISIBLE);
					imgInvited.setVisibility(View.VISIBLE);
					type = 1;
					ApiClient.getInstance().getMeetingByCategory(TAG, type, meetingsCallback);
				} else {
					tvInvited.setTextColor(getResources().getColor(R.color.white));
					/*imgInvited.setVisibility(View.INVISIBLE);*/
				}
			}
		});
		quickJoin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mLastSelectedLableView = quickJoin;

					quickJoin.setTextColor(getResources().getColor(R.color.blue));

					tvInvited.setTextColor(getResources().getColor(R.color.white));
					tvOpen.setTextColor(getResources().getColor(R.color.white));

					imgOpen.setVisibility(View.INVISIBLE);
					imgInvited.setVisibility(View.INVISIBLE);
					quickJoinLine.setVisibility(View.VISIBLE);

				} else {
					quickJoin.setTextColor(getResources().getColor(R.color.white));
					quickJoinLine.setVisibility(View.INVISIBLE);
				}
			}
		});
		quickJoin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isInsertCamera = false;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					List<UsbDevice> deviceList = usbMonitor.getDeviceList();
					for (UsbDevice usbDevice : deviceList) {
						com.orhanobut.logger.Logger.e(JSON.toJSONString(usbDevice));
						if (usbDevice.getProductName() != null) {
							if (usbDevice.getProductName().toLowerCase().contains("cam")
									|| usbDevice.getProductName().toLowerCase().contains("ub")
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


				showQuickJoinDialog();
			}
		});

		mBGABanner.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int currentItem = mBGABanner.getCurrentItem();
				List<RecomandData.MeetingListBean> meetingList = mRecomandData.getMeetingList();
				RecomandData.MeetingListBean model = meetingList.get(currentItem);
				Log.e(TAG, "mBGABanner.setOnClickListener: " + JSON.toJSONString(model));
				if (model == null) {
					return;
				}
				if (model.getIsDefaultImg() == 1) {
					return;
				}

				if (TextUtils.isEmpty(model.getUrlId())) {
					return;
				}
				if (model.getLinkType() == 1) {
					int isToken = 1;
					if (!TextUtils.isEmpty(model.getIsToken())) {
						if (model.getIsToken().equals("0")) {
							isToken = 0;
						}
					}
					DialogUtils.getInstance(getOnAttachActivity())
							.showNormalJoinMeetingDialog(getOnAttachActivity(), model.getUrlId(), isToken);
				} else if (model.getLinkType() == 2) {
					Intent intent = new Intent(getOnAttachActivity(), MakerDetailActivity.class);
					intent.putExtra("pageId", model.getUrlId());
					intent.putExtra("seriesId", model.getSeriesId());
					startActivity(intent);
				}

			}
		});


		NetWorkUtils.getInstance().getBannerData(new NetWorkUtils.OnResultCallBack() {
			@Override
			public void onDataSuccess(JSONObject jsonObject) {
				mRecomandData = JSON.parseObject(jsonObject.getJSONObject("data").toJSONString(), RecomandData.class);
				List<String> textLists = new ArrayList<>();
				if (mRecomandData.getMeetingList() == null) {
					return;
				}
				for (RecomandData.MeetingListBean meetingListBean : mRecomandData.getMeetingList()) {
					textLists.add(meetingListBean.getName());
				}
				mBGABanner.setData(R.layout.item_localimage, mRecomandData.getMeetingList(), textLists);
				mBGABanner.setAdapter(new BGABanner.Adapter<AppCompatImageView, RecomandData.MeetingListBean>() {
					@Override
					public void fillBannerItem(BGABanner banner, AppCompatImageView itemView, RecomandData.MeetingListBean model, int position) {
						Glide.with(MeetingsActivityLaunch.this)
								.load(model.getPictureURL())
								.error(R.drawable.defaule_banner)
								.placeholder(R.drawable.defaule_banner)
								.into(itemView);
						itemView.setScaleType(ImageView.ScaleType.FIT_XY);
					}
				});


				mBGABanner.setOnKeyListener(new View.OnKeyListener() {
					@Override
					public boolean onKey(View v, int keyCode, KeyEvent event) {
						if (event.getAction() == KeyEvent.ACTION_DOWN) {
							if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
								nextItem = (nextItem + 1 > mBGABanner.getItemCount() - 1 ? mBGABanner.getItemCount() - 1 : mBGABanner.getCurrentItem() + 1);
								mBGABanner.setCurrentItem(nextItem);
								return true;
							} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
								nextItem = (nextItem - 1 < 0 ? 0 : mBGABanner.getCurrentItem() - 1);
								mBGABanner.setCurrentItem(nextItem);
								return true;
							} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
								if (mLastSelectedLableView == tvInvited) {
									mLogger.e("mLastSelectedLableView==tvInvited");
								} else if (mLastSelectedLableView == tvOpen) {
									mLogger.e("mLastSelectedLableView==tvOpen");
								}
								mLastSelectedLableView.requestFocus();
								return true;
							} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
								if (recyclerViewTV.getVisibility() == View.VISIBLE) {
									if (recyclerViewTV.getChildAt(mCurrentPosition) != null) {
										recyclerViewTV.getChildAt(mCurrentPosition).requestFocus();
									} else if (meetingAdapter != null && meetingAdapter.getItemCount() > 0) {
										recyclerViewTV.getChildAt(0).requestFocus();
									}
								} else if (recyclerViewTV.getVisibility() == View.GONE) {
									tvSchool.requestFocus();
									return false;
								}
								return true;
							} else {
								return false;
							}
						}
						return false;
					}
				});


			}


			@Override
			public void onDataFailure() {

			}
		});

	}

	private void showQuickJoinDialog() {
		View view;
		if (Constant.isPadApplicaion) {
			mLogger.e("当前是平板设备");
			view = View.inflate(this, R.layout.dialog_meeting_code_pad, null);
		} else {
			view = View.inflate(this, R.layout.dialog_meeting_code, null);
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

		dialog = new Dialog(this, R.style.CustomDialog);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		dialog.show();
	}

	private void quickJoinMeeting(JSONObject params) {
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
							Constant.resolvingPower = meetingJoin.getData().getMeeting().getResolvingPower();

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

							ApiClient.getInstance().getAgoraKey(MeetingsActivityLaunch.this, params, getAgoraCallback(oldmeeting));

						} else {
							ToastUtils.showToast(jsonObject.getString("errmsg"));
						}
					}
				});
	}

	private com.zhongyou.meettvapplicaion.net.OkHttpCallback searchMeetingCallBack = new OkHttpBaseCallback<BaseArrayBean<Meeting>>() {

		@Override
		public void onSuccess(BaseArrayBean<Meeting> meetingBucket) {
			if (meetingBucket.getData().size() > 0) {
				imgNoMeeting.setVisibility(View.GONE);
				if (!mMin || recyclerViewTV.getVisibility() == View.GONE) {
					oldMeetings = meetingBucket.getData();
					meetingAdapter = new MeetingLaunchAdapter(MeetingsActivityLaunch.this, meetingBucket.getData());
					recyclerViewTV.setAdapter(new GeneralAdapter(meetingAdapter));

				} else {
					updateMeetingStatus(meetingBucket.getData());
					oldMeetings = meetingBucket.getData();
				}
				recyclerViewTV.setVisibility(View.VISIBLE);
			} else {
				recyclerViewTV.setVisibility(View.GONE);
				imgNoMeeting.setVisibility(View.VISIBLE);
			}
		}
	};

	private boolean isSettingSelected = false;

	private void init() {
//        tipsImage = (ImageView) findViewById(R.id.tips);
		recyclerViewTV = (RecyclerViewTV) findViewById(R.id.meeting_list);
		// 解决快速长按焦点丢失问题.
		FocusFixedLinearLayoutManager gridlayoutManager = new FocusFixedLinearLayoutManager(this);
		gridlayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
		recyclerViewTV.setLayoutManager(gridlayoutManager);
		recyclerViewTV.setFocusable(false);
		recyclerViewTV.addItemDecoration(new SpaceItemDecoration((int) (getResources().getDimension(R.dimen.my_px_15)), 0, (int) (getResources().getDimension(R.dimen.my_px_15)), 0));
		recyclerViewTV.setOnItemListener(this);
		recyclerViewTV.setSelectedItemAtCentered(true);
	}

	private Dialog dialog;

	private void initDialog(int type, final Meeting meeting) {
		View view;
		if (Constant.isPadApplicaion) {
			mLogger.e("当前是平板设备");
			view = View.inflate(this, R.layout.dialog_meeting_code_pad, null);
		} else {
			view = View.inflate(this, R.layout.dialog_meeting_code, null);
		}

		if (meeting.getScreenshotFrequency() == Meeting.SCREENSHOTFREQUENCY_INVALID) {
			view.findViewById(R.id.dialog_meetingcamera_layout).setVisibility(View.GONE);
		} else {
			view.findViewById(R.id.dialog_meetingcamera_layout).setVisibility(View.VISIBLE);
		}
		final EditText codeEdit = view.findViewById(R.id.code);

		//需要录制
		if (meeting.getIsRecord() == 1) {
			Constant.isNeedRecord = true;
		} else {
			Constant.isNeedRecord = false;
		}

		if (!SpUtil.getString(meeting.getId(), "").equals("")) {
			codeEdit.setText(SpUtil.getString(meeting.getId(), ""));
			codeEdit.setSelection(SpUtil.getString(meeting.getId(), "").length());
		} else {
			codeEdit.setText("");
		}

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


//		view.findViewById(R.id.confirm).setNextFocusRightId(view.findViewById(R.id.noCodeJoin).getVisibility() == View.VISIBLE ? R.id.noCodeJoin : R.id.cancel);
//		view.findViewById(R.id.cancel).setNextFocusLeftId(view.findViewById(R.id.noCodeJoin).getVisibility() == View.VISIBLE ? R.id.noCodeJoin : R.id.confirm);

		view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Camera.getNumberOfCameras() <= 0) {
					Toast.makeText(MeetingsActivityLaunch.this, "未检测到摄像头，请插入摄像头再试！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (!TextUtils.isEmpty(codeEdit.getText())) {
					ArrayMap<String, Object> params = new ArrayMap<String, Object>();
					params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
					params.put("meetingId", meeting.getId());
					params.put("token", codeEdit.getText().toString());
					mMeetingName = meeting.getTitle();
					ApiClient.getInstance().verifyRole(TAG, verifyRoleCallback(meeting, codeEdit.getText().toString()), params);
					LoadingDialog.showDialog(MeetingsActivityLaunch.this);
				} else {
					codeEdit.setError("加入码不能为空");
				}
			}
		});

		view.findViewById(R.id.noCodeJoin).setVisibility(meeting.getIsToken() == 1 ? View.GONE : View.VISIBLE);
		view.findViewById(R.id.noCodeJoin).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Camera.getNumberOfCameras() <= 0) {
					Toast.makeText(MeetingsActivityLaunch.this, "未检测到摄像头，请插入摄像头再试！", Toast.LENGTH_SHORT).show();
					return;
				}
				ArrayMap<String, Object> params = new ArrayMap<String, Object>();
				params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
				params.put("meetingId", meeting.getId());
				params.put("token", "");
				mMeetingName = meeting.getTitle();
				ApiClient.getInstance().verifyRole(TAG, verifyRoleCallback(meeting, ""), params);
				LoadingDialog.showDialog(MeetingsActivityLaunch.this);

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

				SpUtil.put(meeting.getId(), token.trim());

				ApiClient.getInstance().joinMeeting(TAG, joinMeetingCallback, params);
			}

			@Override
			public void onFailure(int errorCode, BaseException exception) {
				super.onFailure(errorCode, exception);
				LoadingDialog.closeDialog();
				Toast.makeText(MeetingsActivityLaunch.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
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

			Constant.resolvingPower = meetingJoin.getMeeting().getResolvingPower();

			if (meetingJoin.getMeeting().getIsRecord() == 1) {
				Constant.isNeedRecord = true;
			} else {
				Constant.isNeedRecord = false;
			}

			if (mJoinRole == 0) {
				params.put("role", "Publisher");
			} else if (mJoinRole == 1) {
				params.put("role", "Publisher");
			} else if (mJoinRole == 2) {
				params.put("role", "Subscriber");
			}

			ApiClient.getInstance().getAgoraKey(MeetingsActivityLaunch.this, params, getAgoraCallback(meetingJoin));
		}

		@Override
		public void onFailure(int errorCode, BaseException exception) {
			super.onFailure(errorCode, exception);
			LoadingDialog.closeDialog();
			Toast.makeText(MeetingsActivityLaunch.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
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
				LoadingDialog.closeDialog();
			}

			@Override
			public void onFailure(int errorCode, BaseException exception) {
				Toast.makeText(getApplication(), "网络异常，请稍后重试！", Toast.LENGTH_SHORT).show();
				LoadingDialog.closeDialog();
			}

		};
	}

	private void updateMeetingStatus(ArrayList<Meeting> meetings) {

		for (int i = 0; i < meetings.size(); i++) {
			boolean mExist = false;
			for (int j = 0; j < oldMeetings.size(); j++) {
				Log.v("meetingsactivityl2", "meetings.get(i).getId()==" + meetings.get(i).getId());
				Log.v("meetingsactivityl2", "oldMeetings.get(j).getId()==" + oldMeetings.get(j).getId());
				if (meetings.get(i).getId().equals(oldMeetings.get(j).getId())) {
					Log.v("meetingsactivityl2", "进入update*****************");
					mExist = true;
					if (meetings.get(i).getMeetingProcess() != oldMeetings.get(j).getMeetingProcess()) {
						meetingAdapter.UpdateMeeting(meetings.get(i));
					}

				}
			}
			if (!mExist) {
				Log.v("meetingsactivityl", "加入新的meeting*****************" + meetings.get(i).getId() + "***" + meetings.get(i).getTitle());
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

	public void gotoMeetting(int position) {


		USBMonitor usbMonitor = new USBMonitor(this, new USBMonitor.OnDeviceConnectListener() {
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
		boolean isInsertCamera = false;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			List<UsbDevice> deviceList = usbMonitor.getDeviceList();
			for (UsbDevice usbDevice : deviceList) {
				com.orhanobut.logger.Logger.e(JSON.toJSONString(usbDevice));
				if (usbDevice.getProductName() != null) {
					//USB Tablet
					if (usbDevice.getProductName().toLowerCase().contains("cam")
							|| usbDevice.getProductName().toLowerCase().contains("ub")
							|| usbDevice.getProductName().toLowerCase().contains("all in")) {
						isInsertCamera = true;
						break;
					}
				}

			}
			if (!isInsertCamera) {
				Toast.makeText(MeetingsActivityLaunch.this, "未检测到摄像头，请插入摄像头再试！", Toast.LENGTH_SHORT).show();
				return;
			}

		} else {
			if (Camera.getNumberOfCameras() <= 0) {
				Toast.makeText(MeetingsActivityLaunch.this, "未检测到摄像头，请插入摄像头再试！", Toast.LENGTH_SHORT).show();
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
				if (MeetingsActivityLaunch.this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
					//申请权限
					MeetingsActivityLaunch.this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
					return;
				}
			}
		}

		Meeting meeting = oldMeetings.get(position);
		initDialog(1, meeting);
	}

	private OkHttpCallback meetingsCallback = new OkHttpCallback<BaseArrayBean<Meeting>>() {

		@Override
		public void onSuccess(final BaseArrayBean<Meeting> meetingBucket) {
			if (meetingBucket.getData().size() > 0) {
				imgNoMeeting.setVisibility(View.GONE);
				if (!mMin || recyclerViewTV.getVisibility() == View.GONE) {
					oldMeetings = meetingBucket.getData();
					meetingAdapter = new MeetingLaunchAdapter(MeetingsActivityLaunch.this, meetingBucket.getData());
					recyclerViewTV.setAdapter(new GeneralAdapter(meetingAdapter));
					/*recyclerViewTV.setOnItemClickListener((parent, itemView, position) -> {


					});*/

				} else {
					if (meetingBucket.getData() != null && meetingBucket.getData().size() > 0) {
						updateMeetingStatus(meetingBucket.getData());
						oldMeetings = meetingBucket.getData();
					}

				}
//                handler.sendEmptyMessageDelayed(10,10000);
				Log.v("meetingsactivityl", "callback  *****************");
				recyclerViewTV.setVisibility(View.VISIBLE);
//                tipsImage.setVisibility(View.GONE);
			} else {
				recyclerViewTV.setVisibility(View.GONE);
				imgNoMeeting.setVisibility(View.VISIBLE);
//                tipsImage.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onFailure(int errorCode, BaseException exception) {
			super.onFailure(errorCode, exception);
			Toast.makeText(MeetingsActivityLaunch.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {

	}

	@Override
	public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
		Log.e(TAG, "onItemSelected: " + mCurrentPosition);
		mCurrentPosition = position;
	}

	@Override
	public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {

	}

	private long mExitTime = 0;
	private long mLastKeyDownTime = 0;

	@Override
	public void onBackPressed() {
		if ((System.currentTimeMillis() - mExitTime) > 2000) {//
			// 如果两次按键时间间隔大于2000毫秒，则不退出
			Toast.makeText(this, "再按一次退出中幼在线", Toast.LENGTH_SHORT).show();
			mExitTime = System.currentTimeMillis();// 更新mExitTime
		} else {
			quit(); // 否则退出程序
		}
	}

	private void quit() {
//        HeartService.stopService(this);
		ZYAgent.onEvent(getApplicationContext(), "返回退出应用");
		ZYAgent.onEvent(getApplicationContext(), "返回退出应用 连接服务 请求停止");
		Preferences.setRotation(false);
		finish();
		System.exit(0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (handler.hasMessages(10)) {
			handler.removeMessages(10);
		}
		if (serviceConn != null) {
			unbindService(serviceConn);
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
//        setIntent(intent);//must store the new intent unless getIntent() will return the old one
		//如果没销毁,注销只调用onNewIntent,需要在这里处理注销逻辑
		processExtraData(intent);
	}

	private void processExtraData(Intent intent) {
		mIntentType = intent.getIntExtra(LoginHelper.LOGIN_TYPE, 0);
		Logger.d(TAG, "mIntentType " + mIntentType);
		if (mIntentType == LoginHelper.LOGIN_TYPE_EXIT) {
			//退出应用
			Logger.d(TAG, "退出应用");
			quit();
		} else if (mIntentType == LoginHelper.LOGIN_TYPE_LOGOUT) {
			//退出登录
			Logger.d(TAG, "退出登录");
//            if(!isNewActivity){
//                //非新activity,需要修改登录UI
//                Log.i(TAG, "发送LogoutEvent");
////                RxBus.sendMessage(new LogoutEvent());
//            }
			setResult(RESULT_OK);
			//判断是否是强制退出,强制退出在登录页有弹窗提醒
			boolean isUserLogout = intent.getBooleanExtra("IS_USER_LOGOUT", false);
			SignInActivity.actionStart(mContext, !isUserLogout);
			finish();
		}

	}

	private Activity getOnAttachActivity() {
		return this;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (recyclerViewTV != null && recyclerViewTV.getVisibility() == View.VISIBLE) {
			if (recyclerViewTV.getChildAt(mCurrentPosition) != null) {
				recyclerViewTV.getChildAt(mCurrentPosition).requestFocus();
			} else if (meetingAdapter != null && meetingAdapter.getItemCount() > 0) {
				recyclerViewTV.getChildAt(0).requestFocus();
			}
		}
	}
}

