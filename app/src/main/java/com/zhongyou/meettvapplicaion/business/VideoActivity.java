package com.zhongyou.meettvapplicaion.business;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zhongyou.meettvapplicaion.ApiClient;
import com.zhongyou.meettvapplicaion.BaseException;
import com.zhongyou.meettvapplicaion.Constant;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.business.adapter.BaseRecyclerAdapter;
import com.zhongyou.meettvapplicaion.business.adapter.BaseRecyclerHolder;
import com.zhongyou.meettvapplicaion.business.adapter.QRCodeActivity;
import com.zhongyou.meettvapplicaion.entities.Bucket;
import com.zhongyou.meettvapplicaion.entities.QRCodeUrl;
import com.zhongyou.meettvapplicaion.entities.Status;
import com.zhongyou.meettvapplicaion.entities.StatusType;
import com.zhongyou.meettvapplicaion.entities.Statuses;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.ActivityUtil;
import com.zhongyou.meettvapplicaion.utils.DensityUtil;
import com.zhongyou.meettvapplicaion.utils.OkHttpCallback;
import com.zhongyou.meettvapplicaion.utils.QRCodeUtil;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;
import com.zhongyou.meettvapplicaion.utils.helper.ImageHelper;
import com.zhongyou.meettvapplicaion.view.CircleImageView;
import com.zhongyou.meettvapplicaion.view.MyRecyclerView;
import com.zhongyou.meettvapplicaion.view.SpaceItemDecoration;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoActivity extends BasisActivity {

	@BindView(R.id.img)
	ImageView img;
	@BindView(R.id.name)
	TextView name;
	@BindView(R.id.user_face)
	CircleImageView userFace;
	@BindView(R.id.upload)
	TextView upload;
	@BindView(R.id.titleLinear)
	LinearLayout titleLinear;
	@BindView(R.id.labelRecyclerView)
	MyRecyclerView labelRecyclerView;
	@BindView(R.id.newest)
	TextView mNewestTextView;
	@BindView(R.id.hotest)
	Button mHotestTextView;
	@BindView(R.id.likest)
	Button mLikestTextView;
	@BindView(R.id.bt_left)
	ImageButton btLeft;
	@BindView(R.id.videoRecyclerView)
	MyRecyclerView videoRecyclerView;
	@BindView(R.id.bt_right)
	ImageButton btRight;
	@BindView(R.id.relative)
	RelativeLayout relative;

	private ImageView mBack;


	private int pageNo = 1;
	private int mLabelLastSelectedPosition = 0;

	private View mLastSelectedView;

	private StatusType currentLabeType;

	private List<StatusType> mLableLists = new ArrayList<>();

	private BaseRecyclerAdapter<StatusType> mLableAdapter;

	private ArrayList<Status> mVideoLists = new ArrayList<>();

	private BaseRecyclerAdapter<Status> mVideoAdapter;
	private int totalNo;

	private int mVideoType = 0;
	private int currentTag = 1;

	@Override
	public String getStatisticsTag() {
		return "金牌商学院列表页";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		ButterKnife.bind(this);
		//获取分类的数据
		initTabData();
		initListener();
		initViews();
		initUsers();

	}

	private void initUsers() {
		name.setText(Preferences.getUserName());
		String headImgeUser;
		if (TextUtils.isEmpty(Preferences.getUserPhoto())) {
			headImgeUser = Preferences.getWeiXinHead();
		} else {
			headImgeUser = Preferences.getUserPhoto();
		}
		Glide.with(this).asBitmap().load(headImgeUser)

				.placeholder(R.drawable.ico_face)
				.error(R.drawable.ico_face).
				into(userFace);
	}

	private void initViews() {
		mNewestTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, R.drawable.ic_selected_tab);
		mNewestTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
		videoRecyclerView.addItemDecoration(new SpaceItemDecoration(0, 0, 24, 24));
	}


	public void resetVideoType() {
		mHotestTextView.setTextColor(getResources().getColor(R.color.white));
		mLikestTextView.setTextColor(getResources().getColor(R.color.white));
		mNewestTextView.setTextColor(getResources().getColor(R.color.white));

		mNewestTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, R.drawable.ic_selected_tab);
		mLikestTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
		mHotestTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);

		mNewestTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
		mHotestTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		mLikestTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

	}


	/**
	 * @param selectText 被点击的TextView
	 * @param second     第二个TextView
	 * @param third      第三个TextView
	 *                   <p>
	 *                   改变被选中的文字样式 设置第二个 第三个文字没选中的样式
	 */
	private void setItemSelectd(TextView selectText, TextView second, TextView third) {
		selectText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, R.drawable.ic_selected_tab);
		second.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
		third.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);

		selectText.setTextColor(getResources().getColor(R.color.blue));
		second.setTextColor(getResources().getColor(R.color.white));
		third.setTextColor(getResources().getColor(R.color.white));

		selectText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
		second.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		third.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
	}

	private void initListener() {

		mBack = findViewById(R.id.back);
		if (Constant.isPadApplicaion){
			mBack.setVisibility(View.VISIBLE);
		}else {
			mBack.setVisibility(View.GONE);
		}
		findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		labelRecyclerView.setOnRightFoucsLinstener(new MyRecyclerView.OnRightFoucsListener() {
			@Override
			public boolean onRightFoucs() {
				if (labelRecyclerView.getSelectedPosition() == mLableLists.size() - 1) {
					return true;
				}
				return false;
			}

			@Override
			public boolean onLeftFouces() {
				if (labelRecyclerView.getSelectedPosition() == 0) {
					return true;
				}
				return false;
			}
		});


		mNewestTextView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
					return true;
				} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
					labelRecyclerView.setItemSelected(mLabelLastSelectedPosition);
					labelRecyclerView.setFocusable(true);
					mNewestTextView.setNextFocusUpId(R.id.labelRecyclerView);
				}
				return false;
			}
		});

		if (Constant.isPadApplicaion) {
			mNewestTextView.setOnClickListener(v -> {
				mLogger.e("最新按钮被点击");
				mNewestTextView.setFocusable(true);
				mNewestTextView.setFocusableInTouchMode(true);
				mNewestTextView.requestFocus();
				setItemSelectd(mNewestTextView, mHotestTextView, mLikestTextView);

			});

			mHotestTextView.setOnClickListener(v -> {
				mLogger.e("最热按钮被点击");
				mHotestTextView.setFocusable(true);
				mHotestTextView.setFocusableInTouchMode(true);
				mHotestTextView.requestFocus();
				setItemSelectd(mHotestTextView, mNewestTextView, mLikestTextView);
			});
			mLikestTextView.setOnClickListener(v -> {
				mLogger.e("点赞最多按钮被点击");
				mLikestTextView.setFocusable(true);
				mLikestTextView.setFocusableInTouchMode(true);
				mLikestTextView.requestFocus();
				setItemSelectd(mLikestTextView, mHotestTextView, mNewestTextView);
			});

		}

		mHotestTextView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
					Logger.e("keyCode" + keyCode);
					labelRecyclerView.setItemSelected(mLabelLastSelectedPosition);
					labelRecyclerView.setFocusable(true);
					labelRecyclerView.requestFocus();
					mHotestTextView.setNextFocusUpId(R.id.labelRecyclerView);
					return true;
				}
				return false;
			}
		});
		mLikestTextView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
					labelRecyclerView.setItemSelected(mLabelLastSelectedPosition);
					labelRecyclerView.setFocusable(true);
					labelRecyclerView.requestFocus();
					mLikestTextView.setNextFocusUpId(R.id.labelRecyclerView);
					return true;
				}
				return false;
			}
		});


		mNewestTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mVideoType = 1;
					currentTag = 1;
					pageNo = 1;
					mHotestTextView.setFocusable(true);
					mLikestTextView.setFocusable(true);
					mNewestTextView.setNextFocusRightId(R.id.hotest);
					mHotestTextView.setNextFocusRightId(R.id.likest);

					setLastSelectedView(mLastSelectedView, mLabelLastSelectedPosition);

					mNewestTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, R.drawable.ic_selected_tab);
					mHotestTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
					mLikestTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);

					mNewestTextView.setTextColor(getResources().getColor(R.color.blue));
					mHotestTextView.setTextColor(getResources().getColor(R.color.white));
					mLikestTextView.setTextColor(getResources().getColor(R.color.white));

					mNewestTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
					mHotestTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
					mLikestTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
					getVideoByType(mLableLists.get(mLabelLastSelectedPosition), 1);
				} else {
					mNewestTextView.setTextColor(getResources().getColor(R.color.white));
				}
			}
		});


		mHotestTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					currentTag = 0;
					setLastSelectedView(mLastSelectedView, mLabelLastSelectedPosition);
					getVideoByType(mLableLists.get(mLabelLastSelectedPosition), 0);
					mVideoType = 0;
					pageNo = 1;
					mHotestTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, R.drawable.ic_selected_tab);
					mNewestTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
					mLikestTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
					mHotestTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);

					mHotestTextView.setTextColor(getResources().getColor(R.color.blue));
					mLikestTextView.setTextColor(getResources().getColor(R.color.white));
					mNewestTextView.setTextColor(getResources().getColor(R.color.white));


					mNewestTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
					mLikestTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
				} else {
					mHotestTextView.setTextColor(getResources().getColor(R.color.white));
				}

			}
		});

		mLikestTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					currentTag = 2;
					getVideoByType(mLableLists.get(mLabelLastSelectedPosition), 2);
					mVideoType = 2;
					pageNo = 1;
					setLastSelectedView(mLastSelectedView, mLabelLastSelectedPosition);
					mLikestTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, R.drawable.ic_selected_tab);
					mHotestTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
					mNewestTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);

					mLikestTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);

					mLikestTextView.setTextColor(getResources().getColor(R.color.blue));
					mNewestTextView.setTextColor(getResources().getColor(R.color.white));
					mHotestTextView.setTextColor(getResources().getColor(R.color.white));

					mNewestTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
					mHotestTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
				} else {
					mLikestTextView.setTextColor(getResources().getColor(R.color.white));
				}

			}
		});


		upload.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
					labelRecyclerView.setItemSelected(mLabelLastSelectedPosition);
					labelRecyclerView.setFocusable(true);
					upload.setNextFocusDownId(R.id.labelRecyclerView);

				}
				return false;
			}
		});


		upload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLogger.e("upload  onClick ");
				ApiClient.getInstance().statusUploadUrl(TAG, urlCallback);
			}
		});

		videoRecyclerView.setOnLoadMoreListener(new MyRecyclerView.OnLoadMoreListener() {
			@Override
			public void onLoadMore() {


				if (pageNo >= totalNo) {
					videoRecyclerView.setLoadMoreComplete();
					Toast.makeText(VideoActivity.this, "没有了", Toast.LENGTH_SHORT).show();
					return;
				}
				Logger.e("onLoadMore:继续加载数据");
				pageNo++;

				getVideoByType(mLableLists.get(mLabelLastSelectedPosition), mVideoType);
			}
		});


		videoRecyclerView.setOnRightFoucsLinstener(new MyRecyclerView.OnRightFoucsListener() {
			@Override
			public boolean onRightFoucs() {
				Logger.e("onRightFoucs   " + videoRecyclerView.getSelectedPosition() + "-----" + videoRecyclerView.getSelectedPosition() % 4);
				if (videoRecyclerView.getSelectedPosition() > 0 && (videoRecyclerView.getSelectedPosition() + 1) % 4 == 0) {
					return true;
				}
				return false;
			}

			@Override
			public boolean onLeftFouces() {
				Logger.e("onLeftFouces   " + videoRecyclerView.getSelectedPosition());
				return false;
			}
		});


	}


	private OkHttpCallback urlCallback = new OkHttpCallback<Bucket<QRCodeUrl>>() {

		@Override
		public void onSuccess(Bucket<QRCodeUrl> stringBucket) {
			Log.v("url", "" + stringBucket.getData());
			new Thread(() -> {
				final String filePath = getFileRoot(VideoActivity.this) + File.separator + "qr_" + System.currentTimeMillis() + ".jpg";
				boolean success = QRCodeUtil.createQRImage(stringBucket.getData().getUrl(), 350, 350, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher), filePath);
				if (success) {
					startActivityForResult(new Intent(VideoActivity.this, QRCodeActivity.class).putExtra("file_path", filePath), 0x100);
				}
			}).start();
		}

		@Override
		public void onFailure(int errorCode, BaseException exception) {
			ToastUtils.showToast(exception.getMessage());
		}


	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == 0x100) {
				getVideoByType(mLableLists.get(mLabelLastSelectedPosition), mVideoType);
			}
		}
	}

	//文件存储根目录
	private String getFileRoot(Context context) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File external = context.getExternalFilesDir(null);
			if (external != null) {
				return external.getAbsolutePath();
			}
		}
		return context.getFilesDir().getAbsolutePath();
	}

	private void initTabData() {
		ApiClient.getInstance().statusTypes(TAG, lableCallBack);
	}

	private OkHttpCallback lableCallBack = new OkHttpCallback<Bucket<ArrayList<StatusType>>>() {

		@Override
		public void onSuccess(Bucket<ArrayList<StatusType>> arrayListBucket) {
			mLableLists = arrayListBucket.getData();
			if (mLableAdapter == null) {
				mLableAdapter = new BaseRecyclerAdapter<StatusType>(VideoActivity.this, mLableLists, R.layout.item_status_type_label) {
					@Override
					public void convert(BaseRecyclerHolder holder, StatusType item, int position, boolean isScrolling) {
						holder.setText(R.id.label, item.getName());
						if (position == 0) {
							((TextView) holder.getView(R.id.label)).setTextColor(getResources().getColor(R.color.blue));
							(holder.getView(R.id.sideView)).setVisibility(View.VISIBLE);
							upload.setFocusable(true);
						}

						holder.itemView.setNextFocusUpId(R.id.newest);
						holder.itemView.setNextFocusRightId(R.id.upload);


						holder.getView(R.id.label).setOnKeyListener(new View.OnKeyListener() {
							@Override
							public boolean onKey(View v, int keyCode, KeyEvent event) {
								Logger.e("keyCode:" + keyCode);
								if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
									mNewestTextView.setFocusable(true);
									mNewestTextView.requestFocus();
									mNewestTextView.setFocusableInTouchMode(true);
									mNewestTextView.requestFocusFromTouch();
								}

								return false;
							}
						});

					}
				};

				mLableAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
					@Override
					public void onItemClick(RecyclerView parent, View view, int position) {
						labelRecyclerView.setItemSelected(position);
					}
				});

				labelRecyclerView.setLayoutManager(new LinearLayoutManager(VideoActivity.this, LinearLayoutManager.HORIZONTAL, false));
				labelRecyclerView.setAdapter(mLableAdapter);

				labelRecyclerView.setOnItemStateListener(new MyRecyclerView.OnItemStateListener() {
					@Override
					public void onItemViewClick(View view, int position) {
						mLogger.e("onItemViewClick   %d", position);

					}

					@Override
					public void onItemViewFocusChanged(boolean gainFocus, View view, int position) {
						if (gainFocus) {
							//恢复视屏最新最热等显示状态
							resetVideoType();
							pageNo = 1;
							currentLabeType = mLableLists.get(position);
							mLastSelectedView = view;
							mLabelLastSelectedPosition = position;
							getVideoByType(mLableLists.get(position), 1);
							TextView lableView = ((BaseRecyclerHolder) labelRecyclerView.getChildViewHolder(view)).getView(R.id.label);
							lableView.setTextColor(getResources().getColor(R.color.blue));
							((BaseRecyclerHolder) labelRecyclerView.getChildViewHolder(view)).getView(R.id.sideView).setVisibility(View.VISIBLE);
							((BaseRecyclerHolder) labelRecyclerView.getChildViewHolder(view)).getView(R.id.sideView).setBackgroundColor(getResources().getColor(R.color.blue));
						} else {
							TextView lableView = ((BaseRecyclerHolder) labelRecyclerView.getChildViewHolder(view)).getView(R.id.label);
							lableView.setTextColor(getResources().getColor(R.color.white));
							((BaseRecyclerHolder) labelRecyclerView.getChildViewHolder(view)).getView(R.id.sideView).setVisibility(View.INVISIBLE);
						}
					}
				});
			} else {
				mLableAdapter.notifyDataSetChanged();
			}
			labelRecyclerView.setItemSelected(0);
			getVideoByType(mLableLists.get(0), 1);
			currentLabeType = mLableLists.get(0);

		}
	};


	public void setLastSelectedView(View view, int position) {
		if (view == null) {
			mLastSelectedView = labelRecyclerView.getChildAt(0);
			view = mLastSelectedView;
		}
		TextView lableView = ((BaseRecyclerHolder) labelRecyclerView.getChildViewHolder(view)).getView(R.id.label);
		lableView.setTextColor(getResources().getColor(R.color.blue));
		((BaseRecyclerHolder) labelRecyclerView.getChildViewHolder(view)).getView(R.id.sideView).setVisibility(View.VISIBLE);
		((BaseRecyclerHolder) labelRecyclerView.getChildViewHolder(view)).getView(R.id.sideView).setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_selected_tab));
	}


	public void getVideoByType(StatusType statusType, int type) {
		mLogger.e("当前视频类型是：%d", type + 1);
		ArrayMap<String, String> params = new ArrayMap<String, String>();
		params.put("typeId", statusType.getId());
		// 1 最热 2:最新 3:点赞最多
		params.put("type", String.valueOf(type + 1));
		params.put("pageSize", "20");
		params.put("pageNo", String.valueOf(pageNo));
		ApiClient.getInstance().status(TAG, videoCallBack(), params);
	}

	private OkHttpCallback videoCallBack() {
		return new OkHttpCallback<Bucket<Statuses>>() {
			@Override
			public void onSuccess(Bucket<Statuses> statusesBucket) {
				if (ActivityUtil.isLive(VideoActivity.this)) {
					totalNo = statusesBucket.getData().getTotalPage();
					pageNo = statusesBucket.getData().getPageNo();
					videoRecyclerView.setLoadMoreComplete();
					if (pageNo == 1) {
						mVideoLists.clear();
					}
					mVideoLists.addAll(statusesBucket.getData().getPageData());
					if (mVideoAdapter == null) {
						mVideoAdapter = new BaseRecyclerAdapter<Status>(VideoActivity.this, mVideoLists, R.layout.item_status) {
							@Override
							public void convert(BaseRecyclerHolder holder, Status item, int position, boolean isScrolling) {
								if (item.getType() == 1) {
									String imageUrl = ImageHelper.videoFrameUrl(item.getSpaceStatusPublishList().get(0).getUrl(), DensityUtil.dip2px(mContext, 235), DensityUtil.dip2px(mContext, 139));
									Picasso.with(mContext)
											.load(imageUrl)
											.into((ImageView) holder.getView(R.id.image));
								} else if (item.getType() == 0) {
									String imageUrl = ImageHelper.videoFrameUrl(item.getSpaceStatusPublishList().get(0).getUrl(), DensityUtil.dip2px(mContext, 235), DensityUtil.dip2px(mContext, 139));
									Picasso.with(mContext)
											.load(ImageHelper.getThumAndCrop(imageUrl, DensityUtil.px2dip(mContext, 235), DensityUtil.px2dip(mContext, 139)))
											.into((ImageView) holder.getView(R.id.image));
								}
								if (item.getType() == 0) {
									holder.getView(R.id.video_tag).setVisibility(View.GONE);
								} else if (item.getType() == 1) {
									holder.getView(R.id.video_tag).setVisibility(View.VISIBLE);
								}
								((TextView) holder.getView(R.id.title)).setText(item.getStatus());
								((TextView) holder.getView(R.id.view_num)).setText(item.getViewingNum() + "");
								((TextView) holder.getView(R.id.like_num)).setText(item.getLikeNum() + "");
							}
						};

						videoRecyclerView.setOnItemStateListener(new MyRecyclerView.OnItemStateListener() {
							@Override
							public void onItemViewClick(View view, int position) {
								Status status = mVideoLists.get(position);
								startActivityForResult(new Intent(VideoActivity.this, StatusActivity.class)
										.putParcelableArrayListExtra("statuses", mVideoLists)
										.putExtra("status", status)
										.putExtra("statusTypeId", currentLabeType.getId())
										.putExtra("currentTag", currentTag)
										.putExtra("totalNo", totalNo)
										.putExtra("pageNo", pageNo), 0x100);
							}

							@Override
							public void onItemViewFocusChanged(boolean gainFocus, View view, int position) {
//								Logger.e("当前为"+position+"的视屏被选中了");
							}
						});
						GridLayoutManager gridLayoutManager = new GridLayoutManager(VideoActivity.this, 4);
						gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
						videoRecyclerView.setLayoutManager(gridLayoutManager);
						videoRecyclerView.setAdapter(mVideoAdapter);

						//平板下 增加方法
						mVideoAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
							@Override
							public void onItemClick(RecyclerView parent, View view, int position) {
								Status status = mVideoLists.get(position);
								startActivityForResult(new Intent(VideoActivity.this, StatusActivity.class)
										.putParcelableArrayListExtra("statuses", mVideoLists)
										.putExtra("status", status)
										.putExtra("statusTypeId", currentLabeType.getId())
										.putExtra("currentTag", currentTag)
										.putExtra("totalNo", totalNo)
										.putExtra("pageNo", pageNo), 0x100);
							}
						});

					} else {
						mVideoAdapter.setNotifyDataChange(mVideoLists);
					}


				}
			}
		};

	}

}
