package com.zhongyou.meettvapplicaion.business.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v17.leanback.widget.Visibility;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.OnePlusNLayoutHelper;
import com.alibaba.android.vlayout.layout.SpliteGridLayoutHelper;
import com.alibaba.android.vlayout.layout.StaggeredGridLayoutHelper;
import com.alibaba.fastjson.JSON;
import com.orhanobut.logger.Logger;
import com.zhongyou.meettvapplicaion.BuildConfig;
import com.zhongyou.meettvapplicaion.Constant;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.entities.AudienceVideo;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.DisplayUtil;
import com.zhongyou.meettvapplicaion.utils.listener.OnDoubleClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import me.jessyan.autosize.utils.AutoSizeUtils;

public class NewAudienceVideoAdapter extends DelegateAdapter.Adapter<NewAudienceVideoAdapter.AudienceVideoViewHolder> {

	private Context context;
	private ArrayList<AudienceVideo> audienceVideos = new ArrayList<AudienceVideo>();
	private AudienceVideoViewHolder mAudienceVideoViewHolder;
	private int height, width;
	private boolean canFocusable = true;
	private boolean isSplitView = false;

	private final int OPENVIDEO = 0x0000;
	private final int CLOSEVIDEO = 0x0001;

	public NewAudienceVideoAdapter(Context context, LayoutHelper layoutHelper) {
		this.context = context;
		this.mLayoutHelper = layoutHelper;
		height = DisplayUtil.dip2px(context, 150);
		width = DisplayUtil.dip2px(context, 114);

	}

	public void setCanFocusable(boolean canFocusable) {
		this.canFocusable = canFocusable;
		notifyDataSetChanged();
	}


	public void setLayoutHelper(LayoutHelper layoutHelper) {
		this.mLayoutHelper = layoutHelper;
	}

	public synchronized void insertItem(AudienceVideo audienceVideo) {
		for (int i = 0; i < audienceVideos.size(); i++) {
			if (audienceVideos.get(i).getUid() == audienceVideo.getUid()) {
				return;
			}
		}
		if (audienceVideo.isBroadcaster()) {
			this.audienceVideos.add(0, audienceVideo);
		} else {
			this.audienceVideos.add(audienceVideos.size(), audienceVideo);
		}
		notifyDataSetChanged();
	}

	public void changeLists(ArrayList<AudienceVideo> audienceVideos) {
		this.audienceVideos = audienceVideos;
	}

	public synchronized void insertItem(int position, AudienceVideo audienceVideo) {
		if (audienceVideo.isBroadcaster()) {
			this.audienceVideos.add(0, audienceVideo);
			notifyDataSetChanged();
		} else {
			this.audienceVideos.add(position, audienceVideo);
			notifyItemInserted(position);
		}
	}

	public synchronized void insetChairMan(int position, AudienceVideo audienceVideo) {
		this.audienceVideos.add(position, audienceVideo);
		notifyItemInserted(position);
	}

	/*public void changItemHeight(SizeUtils sizeUtils){
		if (mAudienceVideoViewHolder!=null){
		}
	}*/

	public int getDataSize() {
		if (audienceVideos != null) {
			return audienceVideos.size();
		}
		return 0;
	}

	public ArrayList<AudienceVideo> getAudienceVideoLists() {
		return audienceVideos;
	}

	public synchronized void deleteItem(int uid) {
		Iterator<AudienceVideo> iterator = audienceVideos.iterator();
		while (iterator.hasNext()) {
			AudienceVideo audienceVideo = iterator.next();
			if (audienceVideo.getUid() == uid) {
				int position = audienceVideos.indexOf(audienceVideo);
				if (position != -1) {
					stripSurfaceView(audienceVideo.getTextureView());
//					if (audienceVideo != null && audienceVideo.getTextureView() != null) {
//						audienceVideo.getSurfaceView().setZOrderMediaOverlay(false);
//						audienceVideo.getSurfaceView().setZOrderOnTop(false);
//					}
					Log.e("deleteItem", "deleteItem: " + position);
//                Log.v("delete", "1--" + audienceVideos.size() + "--position--" + position);
					iterator.remove();
//                Log.v("delete", "2--" + audienceVideos.size());
					notifyDataSetChanged();
				}
			}
		}
	}


	public synchronized void deleteItemById(int uid) {
		for (int i = 0; i < audienceVideos.size(); i++) {
			if (audienceVideos.get(i).getUid() == uid) {
				audienceVideos.remove(i);
				Log.e("deleteItemById", "deleteItem: i===" + i);
				notifyItemRemoved(i);
				break;
			}
		}
	}

	public boolean isHaveChairMan() {
		for (int i = 0; i < audienceVideos.size(); i++) {
			if (audienceVideos.get(i).isBroadcaster()) {
				return true;
			}
		}
		return false;
	}


	public int getPositionById(int uid) {
		for (int i = 0; i < audienceVideos.size(); i++) {
			if (audienceVideos.get(i).getUid() == uid) {
				return i;

			}
		}
		return -1;
	}

	public void setSurfaceViewVisibility(boolean isZOrderTop) {
		for (int i = 0; i < audienceVideos.size(); i++) {
			if (audienceVideos.get(i) != null && audienceVideos.get(i).getTextureView() != null) {
				audienceVideos.get(i).getTextureView().setVisibility(isZOrderTop ? View.VISIBLE : View.GONE);
			}
			notifyDataSetChanged();
		}

	}

	public int getChairManPosition() {
		for (int i = 0; i < audienceVideos.size(); i++) {
			if (audienceVideos.get(i).isBroadcaster()) {
				return i;
			}
		}
		return -1;
	}

	public synchronized void removeItem(int position) {
		AudienceVideo audienceVideo = audienceVideos.get(position);
		stripSurfaceView(audienceVideo.getTextureView());
//		if (null != audienceVideo.getTextureView()) {
//			audienceVideo.getSurfaceView().setZOrderMediaOverlay(false);
//			audienceVideo.getSurfaceView().setZOrderOnTop(false);
//		}
		audienceVideos.remove(position);
		notifyItemRemoved(position);
	}

	public void setVolumeByUid(int uid, int volume) {
		for (AudienceVideo audienceVideo : audienceVideos) {
			if (audienceVideo.getUid() == uid) {
//                Log.v("update_volume", "1--" + audienceVideo.toString());
				audienceVideo.setVolume(volume);
				int position = audienceVideos.indexOf(audienceVideo);
//                Log.v("update_volume", "2--" + audienceVideo.toString() + "--position--" + position);
				notifyItemChanged(position, 1);
			} else {
//                Log.v("update_volume", "不是更新它");
			}
		}
	}

	public void setVisibility(@Visibility int visibility) {
		for (AudienceVideo audienceVideo : this.audienceVideos) {
			if (visibility == View.VISIBLE) {
				if (audienceVideo.getTextureView() != null) {
					audienceVideo.getTextureView().setVisibility(View.VISIBLE);
				}
			} else {
				if (audienceVideo.getTextureView() != null) {
					audienceVideo.getTextureView().setVisibility(View.GONE);
				}
			}
		}
	}

	public void setMutedStatusByUid(int uid, boolean muted) {
		for (AudienceVideo audienceVideo : audienceVideos) {
			if (audienceVideo.getUid() == uid) {
//                Log.v("update_mute", "1--" + audienceVideo.toString());
				audienceVideo.setMuted(muted);
				int position = audienceVideos.indexOf(audienceVideo);
//                Log.v("update_mute", "2--" + audienceVideo.toString() + "--position--" + position);
				notifyItemChanged(position, 1);
			} else {
//                Log.v("update_mute", "不是更新它");
			}
		}
	}

	@Override
	public AudienceVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view;
		switch (viewType) {
			case OPENVIDEO:
				if (Constant.isPadApplicaion) {
					view = LayoutInflater.from(context).inflate(R.layout.item_meeting_audience_video_pad, parent, false);
				} else {
					view = LayoutInflater.from(context).inflate(R.layout.item_meeting_audience_video, parent, false);
				}
				break;
			case CLOSEVIDEO:
				if (Constant.isPadApplicaion) {
					view = LayoutInflater.from(context).inflate(R.layout.item_meeting_audience_video_pad, parent, false);
				} else {
					view = LayoutInflater.from(context).inflate(R.layout.item_meeting_audience_close_video, parent, false);
				}
				break;
			default:
				view = LayoutInflater.from(context).inflate(R.layout.item_meeting_audience_close_video, parent, false);
				break;
		}

		mAudienceVideoViewHolder = new AudienceVideoViewHolder(view);
		return mAudienceVideoViewHolder;

	}

	@Override
	public int getItemViewType(int position) {
		if (isSplitView) {
			return OPENVIDEO;
		}
		if (position == 0) {
			return OPENVIDEO;
		}
		if (audienceVideos.get(position) != null) {
			if (audienceVideos.get(position).isShowSurface()) {
				return OPENVIDEO;
			} else {
				return CLOSEVIDEO;
			}
		}

		return CLOSEVIDEO;
	}

	public void setItemSize(int height, int width) {
		this.width = width;
		this.height = height;
	}

	int res;

	public void setItemBackground(int res) {
		this.res = res;
	}

	public AudienceVideoViewHolder getHolder() {
		return mAudienceVideoViewHolder;
	}


	@Override
	public void onBindViewHolder(@NonNull final AudienceVideoViewHolder holder, @SuppressLint("RecyclerView") int position) {
		final AudienceVideo audienceVideo = audienceVideos.get(position);
		holder.itemView.setFocusable(canFocusable);

		if (res != 0) {
			holder.itemView.setBackground(context.getResources().getDrawable(res));
		}
		ViewGroup.LayoutParams mLayoutParams = holder.itemView.getLayoutParams();
		if (getItemViewType(position) == OPENVIDEO) {
			mLayoutParams.width = AutoSizeUtils.pt2px(context, 300);
			mLayoutParams.height = AutoSizeUtils.pt2px(context, 150);
		} else {
			mLayoutParams.width = AutoSizeUtils.pt2px(context, 300);
			mLayoutParams.height = AutoSizeUtils.pt2px(context, 30);
		}


		if (mLayoutHelper instanceof StaggeredGridLayoutHelper || mLayoutHelper instanceof SpliteGridLayoutHelper) {
			isSplitView = true;
			switch (getItemCount()) {
				case 0:
					break;
				case 1:
					mLayoutParams.width = getWidth(context);
					mLayoutParams.height = getHeight(context);
					break;
				case 2:
					mLayoutParams.width = getWidth(context) / 2;
					mLayoutParams.height = getHeight(context);
					break;
				case 3:
				case 4:
					mLayoutParams.width = getWidth(context) / 2;
					mLayoutParams.height = getHeight(context) / 2;
					break;
				case 5:
				case 6:
					mLayoutParams.width = getWidth(context) / 2;
					mLayoutParams.height = getHeight(context) / 3;
					break;
				case 7:
				case 8:
					mLayoutParams.width = getWidth(context) / 4;
					mLayoutParams.height = getHeight(context) / 2;
					break;

			}

		} else {
			isSplitView = false;

		}

		holder.itemView.setLayoutParams(mLayoutParams);

		if (position > 16) {
			ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
			layoutParams.width = 0;
			layoutParams.height = 0;
			holder.itemView.setLayoutParams(layoutParams);
		}


		if (position == 0) {
			audienceVideo.setShowSurface(true);
		}
		/**？？*/
		if (audienceVideo.getTextureView() == null && !audienceVideo.isBroadcaster()) {
			holder.itemView.setVisibility(View.VISIBLE);
			holder.nameText.setText(audienceVideo.getName() + "-" + position);
			holder.nameText.setTextColor(context.getResources().getColor(R.color.black));
			holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white));
		} else if (audienceVideo.isBroadcaster() && audienceVideo.getTextureView() == null) {
			holder.itemView.setVisibility(View.VISIBLE);
			holder.chairManHint.setVisibility(View.VISIBLE);
			holder.nameText.setText(audienceVideo.getName());
			holder.labelText.setVisibility(View.VISIBLE);
			holder.itemView.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
				@Override
				public void onDoubleClick(View v) {
					if (mDoubleClickListener != null) {
						mDoubleClickListener.onDoubleClick(recyclerView, v, position);
					}
				}
			}));
		} else {
			holder.chairManHint.setVisibility(View.GONE);
			holder.itemView.setVisibility(View.VISIBLE);

			/*holder.itemView.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
				@Override
				public void onDoubleClick(View v) {
					if (mDoubleClickListener != null) {
						mDoubleClickListener.onDoubleClick(recyclerView, v, position);
					}
				}
			}));*/

			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (listener != null) {
						listener.onItemClick(recyclerView, v, position);
					}
				}
			});
			holder.nameText.setText(TextUtils.isEmpty(audienceVideo.getName()) ? (TextUtils.isEmpty(audienceVideo.getUname()) ? "" : audienceVideo.getUname()) : audienceVideo.getName());
			if (String.valueOf(audienceVideo.getUid()).equals(Preferences.getAgroUid()) && !audienceVideo.isBroadcaster()) {
				holder.nameText.setText(Preferences.getUserName());
			}

			if (audienceVideo.isBroadcaster()) {
				holder.nameText.setVisibility(View.GONE);
			} else {
				holder.nameText.setVisibility(View.VISIBLE);
			}


			if (audienceVideo.isBroadcaster()) {
				holder.labelText.setVisibility(View.VISIBLE);
			} else {
				holder.labelText.setVisibility(View.GONE);
			}

			/**old声音大小输入*/
//			if (!audienceVideo.isMuted() || audienceVideo.getVolume() <= 100) {
//				holder.volumeImage.setVisibility(View.GONE);
//			} else {
//				holder.volumeImage.setVisibility(View.VISIBLE);
//			}

			if (audienceVideo.isMuted()) {
				holder.volumeImage.setVisibility(View.VISIBLE);
			} else {
				holder.volumeImage.setVisibility(View.GONE);
			}

			if (holder.videoLayout.getChildCount() > 0) {
				holder.videoLayout.removeAllViews();
			}


			if (audienceVideo.isShowSurface()||audienceVideo.isBroadcaster()) {//当前参会人展示视频 或者是主持人
				final TextureView surfaceView = audienceVideo.getTextureView();
				if (surfaceView != null) {
//					surfaceView.setZOrderMediaOverlay(true);
					stripSurfaceView(surfaceView);
					holder.videoLayout.removeAllViews();
					holder.videoLayout.addView(surfaceView, new VirtualLayoutManager.LayoutParams(VirtualLayoutManager.LayoutParams.MATCH_PARENT, VirtualLayoutManager.LayoutParams.MATCH_PARENT));
					holder.videoLayout.setVisibility(View.VISIBLE);
				}
				////0 正常 1 卡顿 2 播放失败
				if (audienceVideo.getVideoStatus() == 0) {
					holder.videoStatus.setVisibility(View.GONE);
				} else if (audienceVideo.getVideoStatus() == 1) {
					holder.videoStatus.setVisibility(View.VISIBLE);
					holder.videoStatus.setText("对方网络不好 视频卡顿");
				} else if (audienceVideo.getVideoStatus() == 2) {
					holder.videoStatus.setVisibility(View.VISIBLE);
					holder.videoStatus.setText("对方网络不好 视频播放失败");
				}

			} else {
				holder.videoStatus.setVisibility(View.GONE);
				holder.videoLayout.removeAllViews();
				holder.videoLayout.setVisibility(View.GONE);
				if (getItemViewType(position) == CLOSEVIDEO) {
					holder.headImageView.setVisibility(View.GONE);
				} else {
					holder.headImageView.setVisibility(View.VISIBLE);
				}

			}

		}
		Log.i("lt_cj", "onBindViewHolder: "+ audienceVideo.isOpenVideo());
		if (audienceVideo.isOpenVideo()) {
			holder.headImageView.setVisibility(View.VISIBLE);
			holder.videoLayout.setVisibility(View.GONE);
		} else {
			holder.headImageView.setVisibility(View.GONE);
			holder.videoLayout.setVisibility(View.VISIBLE);
		}


	}

	@Override
	public void onBindViewHolder(@NonNull AudienceVideoViewHolder holder, int position, @NonNull List<Object> payloads) {
		if (payloads.isEmpty()) {
			onBindViewHolder(holder, position);
		} else {
			final AudienceVideo audienceVideo = audienceVideos.get(position);

			/**old声音大小输入*/
			if (audienceVideo.isMuted()) {
				holder.volumeImage.setVisibility(View.VISIBLE);
			} else {
				holder.volumeImage.setVisibility(View.GONE);
			}
//			if (!audienceVideo.isMuted() || audienceVideo.getVolume() <= 100) {
//				holder.volumeImage.setVisibility(View.GONE);
//			} else {
//				holder.volumeImage.setVisibility(View.VISIBLE);
//			}

		}
	}

	private void stripSurfaceView(TextureView view) {
		if (view == null) {
			return;
		}
		ViewParent parent = view.getParent();
		if (parent != null) {
			((FrameLayout) parent).removeView(view);
		}
	}


	@Override
	public int getItemCount() {
		return audienceVideos != null ? audienceVideos.size() : 0;
	}

	private LayoutHelper mLayoutHelper;

	@Override
	public LayoutHelper onCreateLayoutHelper() {
		return mLayoutHelper;
	}

	public class AudienceVideoViewHolder extends RecyclerView.ViewHolder {

		private TextView labelText, nameText, chairManHint, videoStatus;
		private FrameLayout videoLayout;
		private ImageView volumeImage, headImageView;

		public AudienceVideoViewHolder(View itemView) {
			super(itemView);
			videoLayout = itemView.findViewById(R.id.audience_video_layout);
			labelText = itemView.findViewById(R.id.label);
			chairManHint = itemView.findViewById(R.id.chairManHint);
			nameText = itemView.findViewById(R.id.name);
			volumeImage = itemView.findViewById(R.id.volume_status);
			headImageView = itemView.findViewById(R.id.headImageView);
			videoStatus = itemView.findViewById(R.id.videoStatus);
		}
	}


	/**
	 * 定义一个点击事件接口回调
	 */

	public interface onDoubleClickListener {
		void onDoubleClick(View parent, View view, int position);
	}

	public interface OnItemClickListener {
		void onItemClick(RecyclerView parent, View view, int position);
	}

	public interface OnItemLongClickListener {
		boolean onItemLongClick(RecyclerView parent, View view, int position);
	}

	private OnItemClickListener listener;//点击事件监听器
	private OnItemLongClickListener longClickListener;//长按监听器
	private onDoubleClickListener mDoubleClickListener;

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
	}

	public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
		this.longClickListener = longClickListener;
	}

	public void setOnDoucleClickListener(onDoubleClickListener onDoucleClickListener) {
		this.mDoubleClickListener = onDoucleClickListener;
	}

	private RecyclerView recyclerView;

	//在RecyclerView提供数据的时候调用
	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
		this.recyclerView = recyclerView;
	}

	@Override
	public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
		super.onDetachedFromRecyclerView(recyclerView);
		this.recyclerView = null;
	}


	/**
	 * @return 屏幕宽度 in pixel
	 */
	public static int getWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		int width = wm.getDefaultDisplay().getWidth();
		return width;
	}

	/**
	 * @return 屏幕高度 in pixel
	 */
	public static int getHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int height = wm.getDefaultDisplay().getHeight();
		return height;
	}

	public void changePositionToFirst(int position) {
		Collections.swap(audienceVideos, 0, position);
	}
}


