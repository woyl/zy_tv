package com.zhongyou.meettvapplicaion.business;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhongyou.meettvapplicaion.ApiClient;
import com.zhongyou.meettvapplicaion.BaseException;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.entities.Bucket;
import com.zhongyou.meettvapplicaion.entities.Liked;
import com.zhongyou.meettvapplicaion.entities.SpaceStatusPublish;
import com.zhongyou.meettvapplicaion.entities.Status;
import com.zhongyou.meettvapplicaion.entities.Statuses;
import com.zhongyou.meettvapplicaion.utils.ActivityUtil;
import com.zhongyou.meettvapplicaion.utils.CircleTransform;
import com.zhongyou.meettvapplicaion.utils.OkHttpCallback;
import com.zhongyou.meettvapplicaion.view.VideoPlayer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fm.jiecao.jcvideoplayer_lib.JCUserAction;
import fm.jiecao.jcvideoplayer_lib.JCUserActionStandard;

public class StatusActivity extends BasisActivity {

	private ArrayList<Status> statuses;
	private Status status;
	private int pageNo, currentTag, totalNo, position;
	private String currentStatusTypeId;

	private VideoPlayer videoPlayer;
	private ViewPager viewPager;
	private TextView statusText, viewText, likeText, likeStatusText;
	private FrameLayout likeLayout;
	private ImageView avatarImage;
	private TextView nameText;

	private boolean backRefresh = false;

	@Override
	public String getStatisticsTag() {
		return "金牌学院列表页";
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);

		currentStatusTypeId = getIntent().getStringExtra("statusTypeId");
		statuses = getIntent().getParcelableArrayListExtra("statuses");
		status = getIntent().getParcelableExtra("status");
		pageNo = getIntent().getIntExtra("pageNo", 1);
		Log.v("pageNo", "" + pageNo);
		totalNo = getIntent().getIntExtra("totalNo", 1);
		Log.v("totalNo", "" + totalNo);
		currentTag = getIntent().getIntExtra("currentTag", 1);
		Log.v("currentTag", "" + currentTag);
		position = statuses.indexOf(status);
		Log.v("position", position + "");

		initView();

	}

	private void initView() {
		videoPlayer = findViewById(R.id.video_player);
		viewPager = findViewById(R.id.view_pager);
		avatarImage = findViewById(R.id.avatar);
		nameText = findViewById(R.id.uploader_name);
		statusText = findViewById(R.id.status);
		viewText = findViewById(R.id.view_num);
		likeText = findViewById(R.id.like_num);
		likeLayout = findViewById(R.id.like_layout);
		likeLayout.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
					return true;
				}
				return false;
			}
		});
		likeStatusText = findViewById(R.id.like_text);
		if (null!=videoPlayer.getBackView()){
			videoPlayer.getBackView().setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
		assign();
	}

	private void assign() {
		if (!ActivityUtil.isLive(this)) {
			return;
		}
		if (!TextUtils.isEmpty(status.getUserLogo())) {
			Picasso.with(this).load(status.getUserLogo()).transform(new CircleTransform()).into(avatarImage);
		}
		nameText.setText(status.getUserName());
		statusText.setText(status.getStatus());
		viewText.setText(String.valueOf(status.getViewingNum()));
		likeText.setText(String.valueOf(status.getLikeNum()));

		if (status.getType() == 1) {
			viewPager.setVisibility(View.GONE);
			videoPlayer.setVisibility(View.VISIBLE);
			VideoPlayer.setJcUserAction(new UserActionListener());
			videoPlayer.bottomContainer.setVisibility(View.VISIBLE);
			videoPlayer.startButton.postDelayed(() -> {
				String url = status.getSpaceStatusPublishList().get(0).getUrl();
//                String url = "http://oy5rcvj8v.bkt.clouddn.com/osg/space/video/00751d46eae651497b97da9fb3843feb55.mp4";
				videoPlayer.setUp(url, VideoPlayer.SCREEN_LAYOUT_NORMAL, status.getStatus());
				videoPlayer.startButton.performClick();
			}, 100);
			videoPlayer.requestFocus();
		} else {
			videoPlayer.setVisibility(View.GONE);
			viewPager.setVisibility(View.VISIBLE);
			viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
				@Override
				public Fragment getItem(int position) {
					SpaceStatusPublish spaceStatusPublish = status.getSpaceStatusPublishList().get(position);
					return ImageFragment.newInstance(spaceStatusPublish);
				}

				@Override
				public int getCount() {
					return status.getSpaceStatusPublishList().size();
				}

			});
			viewPager.setOnPageChangeListener(new OnPageChangeListener() {
				@Override
				public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                    Log.v("onPageScrolled", "position:" + position + "---positionOffset:" + positionOffset + "---positionOffsetPixels:" + positionOffsetPixels);
				}

				@Override
				public void onPageSelected(int position) {
					Log.v("onPageSelected", "" + position);
					if (position == 0) {

					} else if (position == (status.getSpaceStatusPublishList().size() - 1)) {

					}
				}

				@Override
				public void onPageScrollStateChanged(int state) {
//                    Log.v("onPageScrollStateChang", "" + state);
				}
			});
		}

		ApiClient.getInstance().statusIsLiked(TAG, isLikedCallback, status.getId());

		ApiClient.getInstance().statusView(TAG, viewCallback, status.getId());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e("keyCode", "----->" + keyCode);
		switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
               /* if (videoPlayer.currentState == VideoPlayer.CURRENT_STATE_PLAYING) {
                    videoPlayer.startButton.performClick();
                }
                if (position > 0) {
                    position--;
                    status = statuses.get(position);
                    assign();
                }
                break;*/
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				/*if (videoPlayer.currentState == VideoPlayer.CURRENT_STATE_PLAYING) {
					videoPlayer.release();
				}
				if (position < statuses.size() - 1) {
					position++;
					status = statuses.get(position);
					assign();
				} else {
					if (pageNo < totalNo) {
						pageNo++;
						loadStatus();
					}
				}*/
				videoPlayer.bottomContainer.requestFocus();
				videoPlayer.bottomContainer.setVisibility(View.VISIBLE);
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
               /* if (status.getType() == 0) {
                    viewPager.requestFocus();
                } else if (status.getType() == 1) {
                    videoPlayer.requestFocus();
                }*/
				videoPlayer.bottomContainer.requestFocus();
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				if (likeLayout.isFocusable()) {
					likeLayout.requestFocus();
				}
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
				if (videoPlayer.currentState == VideoPlayer.CURRENT_STATE_PAUSE || videoPlayer.currentState == VideoPlayer.CURRENT_STATE_PLAYING) {
					videoPlayer.startButton.performClick();
				}
				break;
		}
		return super.onKeyDown(keyCode, event);
	}


	private class UserActionListener implements JCUserActionStandard {
		@Override
		public void onEvent(int type, String url, int screen, Object... objects) {
			switch (type) {
				case JCUserAction.ON_CLICK_PAUSE:
					Log.i("USER_EVENT", "ON_CLICK_PAUSE" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
					break;
				case JCUserAction.ON_CLICK_RESUME:
					Log.i("USER_EVENT", "ON_CLICK_RESUME" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
					break;
				case JCUserAction.ON_AUTO_COMPLETE:
					Log.i("USER_EVENT", "ON_AUTO_COMPLETE" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
					if (position < statuses.size() - 1) {
						position++;
						status = statuses.get(position);
						assign();
					}
					break;
				default:
					Log.i("USER_EVENT", "unknow");
					break;
			}
		}
	}

	private OkHttpCallback isLikedCallback = new OkHttpCallback<Bucket<Liked>>() {
		@Override
		public void onSuccess(Bucket<Liked> likedBucket) {
			if (likedBucket.getData().getId() != null) {
				likeStatusText.setText("已点赞");
				likeLayout.setBackgroundResource(R.drawable.bg_liked);
				likeLayout.setFocusable(false);
				likeLayout.setOnClickListener(null);
			} else {
				likeStatusText.setText("点赞");
				likeLayout.setBackgroundResource(R.drawable.bg_upload_selector);
				likeLayout.setFocusable(true);
				viewPager.requestFocus();
				likeLayout.setOnClickListener(v -> {
					ApiClient.getInstance().statusLike(TAG, likeCallback, status.getId());
				});
			}
		}

		@Override
		public void onFailure(int errorCode, BaseException exception) {
			super.onFailure(errorCode, exception);
			Toast.makeText(getApplicationContext(), errorCode + ":" + exception.getMessage(), Toast.LENGTH_SHORT).show();
		}
	};

	private OkHttpCallback likeCallback = new OkHttpCallback<Bucket>() {
		@Override
		public void onSuccess(Bucket bucket) {
			Log.v(TAG, bucket.toString());
			if (bucket.getErrcode() == 0) {
				backRefresh = true;
				likeStatusText.setText("已点赞");
				likeLayout.setOnClickListener(null);
				likeLayout.setFocusable(false);
				likeLayout.setBackgroundResource(R.drawable.bg_liked);
				status.setLikeNum(status.getLikeNum() + 1);
				likeText.setText(String.valueOf(status.getLikeNum()));
			}
		}

		@Override
		public void onFailure(int errorCode, BaseException exception) {
			super.onFailure(errorCode, exception);
			Toast.makeText(getApplicationContext(), errorCode + ":" + exception.getMessage(), Toast.LENGTH_SHORT).show();
		}
	};

	private OkHttpCallback viewCallback = new OkHttpCallback<Bucket>() {
		@Override
		public void onSuccess(Bucket bucket) {
			backRefresh = true;
			Log.v(TAG, bucket.toString());
			status.setViewingNum(status.getViewingNum() + 1);
			viewText.setText(String.valueOf(status.getViewingNum()));
		}

		@Override
		public void onFailure(int errorCode, BaseException exception) {
			super.onFailure(errorCode, exception);
			Toast.makeText(getApplicationContext(), errorCode + ":" + exception.getMessage(), Toast.LENGTH_SHORT).show();
		}
	};

	private void loadStatus() {
		ArrayMap<String, String> params = new ArrayMap<String, String>();
		params.put("typeId", currentStatusTypeId);
		params.put("type", String.valueOf(currentTag + 1));
		params.put("pageSize", "20");
		params.put("pageNo", String.valueOf(pageNo));
		ApiClient.getInstance().status(TAG, statusCallback(), params);
	}

	private OkHttpCallback statusCallback() {
		return new OkHttpCallback<Bucket<Statuses>>() {
			@Override
			public void onSuccess(Bucket<Statuses> statusesBucket) {
				if (ActivityUtil.isLive(StatusActivity.this)) {
					totalNo = statusesBucket.getData().getTotalPage();
					statuses.addAll(statusesBucket.getData().getPageData());
					status = statusesBucket.getData().getPageData().get(0);
					position++;
					assign();
				}
			}

			@Override
			public void onFailure(int errorCode, BaseException exception) {
				super.onFailure(errorCode, exception);
				Toast.makeText(StatusActivity.this, errorCode + ":" + exception.getMessage(), Toast.LENGTH_SHORT).show();
			}
		};

	}

	@Override
	public void onBackPressed() {
		if (backRefresh) {
			setResult(RESULT_OK);
		}
		super.onBackPressed();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (videoPlayer != null) {
			if (videoPlayer.currentState == VideoPlayer.CURRENT_STATE_PLAYING) {
				videoPlayer.release();
			}
		}
	}
}
