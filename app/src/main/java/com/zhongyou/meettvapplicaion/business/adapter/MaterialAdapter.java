package com.zhongyou.meettvapplicaion.business.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.text.UFormat;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.herewhite.sdk.Room;
import com.herewhite.sdk.domain.Promise;
import com.herewhite.sdk.domain.SDKError;
import com.herewhite.sdk.domain.Scene;
import com.herewhite.sdk.domain.SceneState;
import com.zhongyou.meettvapplicaion.Constant;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.entities.Material;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;
import com.zhongyou.meettvapplicaion.utils.helper.ImageHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import io.agora.openlive.ui.MeetingBroadcastActivity;
import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * Created by whatisjava on 17-10-18.
 */

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.ViewHolder> {

	private Context mContext;
	private ArrayList<Material> materials;
	private OnClickListener onClickListener;
	private Room room;

	public MaterialAdapter(Context context, ArrayList<Material> materials) {
		this.mContext = context;
		this.materials = materials;
	}

	public void setRoom (Room room) {
		this.room = room;
	}

	public void addData(ArrayList<Material> materials) {
		this.materials.addAll(materials);
		notifyItemRangeInserted(this.materials.size(), materials.size());
	}

	public void cleanData() {
		this.materials.clear();
		notifyDataSetChanged();
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view;
		if (Constant.isPadApplicaion) {
			view = LayoutInflater.from(mContext).inflate(R.layout.item_material_pad, parent, false);
		} else {
			view = LayoutInflater.from(mContext).inflate(R.layout.item_material, parent, false);
		}
		return new ViewHolder(view);
	}

	@SuppressLint("DefaultLocale")
	@Override
	public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
		Material material = materials.get(position);

		switch (material.getType()) {
			case "1": //视频
				viewHolder.videoIndicator.setVisibility(View.VISIBLE);
				String imageFristUrl = ImageHelper.videoImageFromUrl(material.getMeetingMaterialsPublishList().get(0).getUrl(), AutoSizeUtils.dp2px(mContext, 266), AutoSizeUtils.dp2px(mContext, 300));
				Picasso.with(mContext).load(imageFristUrl).error(R.drawable.item_forum_img_error).placeholder(R.drawable.item_forum_img_loading).into(viewHolder.imageView);
				break;
			case "0":
				viewHolder.videoIndicator.setVisibility(View.GONE);
				if (material.getMeetingMaterialsPublishList() != null && material.getMeetingMaterialsPublishList().size() > 0) {
					String imageUrl = ImageHelper.getThumb(material.getMeetingMaterialsPublishList().get(0).getUrl());
					Picasso.with(mContext).load(imageUrl).into(viewHolder.imageView);
				}
				break;
			case "2":
				viewHolder.videoIndicator.setVisibility(View.GONE);

				if (material.getConvertFileLists() != null && material.getConvertFileLists().size() > 0) {
					if (room != null) {
						room.getSceneSnapshotImage(MeetingBroadcastActivity.SCENE_DIR+"/"+material.getName()+1, new Promise<Bitmap>() {
							@Override
							public void then(Bitmap bitmap) {
								Glide.with(mContext).load(bitmap).into(viewHolder.imageView);
							}

							@Override
							public void catchEx(SDKError t) {
								ToastUtils.showToast(t.getMessage());
							}
						});
					}

//					String imageUrl = ImageHelper.getThumb(material.getConvertFileLists().get(0).getPpt().getSrc());
//					Picasso.with(mContext).load(imageUrl).into(viewHolder.imageView);
				}
				break;
		}
		if (material.getType().equals("2")) {
			viewHolder.countText.setText(String.format("%d张", material.getConvertFileLists().size()));
		} else {
			viewHolder.countText.setText(String.format("%d张", material.getMeetingMaterialsPublishList().size()));
		}

		viewHolder.uploadTimeText.setText(String.format("%s上传", material.getCreateDate()));
		if (material.getName() == null || material.getName().isEmpty()) {
			viewHolder.name.setVisibility(View.INVISIBLE);
		} else {
			viewHolder.name.setText(material.getName());
		}
		if (onClickListener != null) {
			viewHolder.previewButton.setOnClickListener(v -> {
				Log.e("onBindViewHolder", "previewButton: onClick()");

				int layoutPos = viewHolder.getLayoutPosition();
				onClickListener.onPreviewButtonClick(v, material, layoutPos);
			});
			viewHolder.useButton.setOnClickListener(v -> {
				Log.e("onBindViewHolder", "useButton: onClick()");

				int layoutPos = viewHolder.getLayoutPosition();
				onClickListener.onUseButtonClick(v, material, layoutPos);
			});
		}

		if (Constant.isPadApplicaion) {
			viewHolder.previewButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						viewHolder.previewButton.callOnClick();
					}
				}
			});

			viewHolder.useButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						viewHolder.useButton.callOnClick();
					}
				}
			});

			viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					viewHolder.itemView.requestFocus();
				}
			});

		}


		viewHolder.coverLayout.setOnFocusChangeListener((v, hasFocus) -> {
			if (hasFocus) {
				viewHolder.previewButton.setVisibility(View.VISIBLE);
				viewHolder.useButton.setVisibility(View.VISIBLE);
				viewHolder.uploadTimeText.setBackgroundResource(R.drawable.bg_upload_time_focused);
				viewHolder.uploadTimeText.setTextColor(Color.WHITE);

				viewHolder.name.setBackgroundResource(R.drawable.bg_upload_time_focused);
				viewHolder.name.setTextColor(Color.WHITE);


			} else {
				if (viewHolder.previewButton.hasFocus() || viewHolder.useButton.hasFocus()) {
					viewHolder.previewButton.setVisibility(View.VISIBLE);
					viewHolder.useButton.setVisibility(View.VISIBLE);
				} else {
					viewHolder.previewButton.setVisibility(View.GONE);
					viewHolder.useButton.setVisibility(View.GONE);

					viewHolder.uploadTimeText.setBackgroundResource(R.drawable.bg_upload_time_default);
					viewHolder.uploadTimeText.setTextColor(Color.BLACK);
					viewHolder.name.setBackgroundResource(R.drawable.bg_upload_time_default);
					viewHolder.name.setTextColor(Color.BLACK);
				}
			}
		});
		viewHolder.previewButton.setOnKeyListener((v, keyCode, event) -> {
			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				return true;
			}
			return false;
		});
		viewHolder.useButton.setOnKeyListener((v, keyCode, event) -> {
			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				return true;
			}
			return false;
		});

		if (material.getType().equals("2")) {
			viewHolder.previewButton.setVisibility(View.GONE);
		}
	}

	@Override
	public int getItemCount() {
		return materials != null ? materials.size() : 0;
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		ImageView imageView;
		TextView countText, uploadTimeText, name;
		Button previewButton, useButton;
		FrameLayout coverLayout;
		ImageView videoIndicator;


		public ViewHolder(View itemView) {
			super(itemView);
			name = itemView.findViewById(R.id.name);
			videoIndicator = itemView.findViewById(R.id.videoIndicator);
			coverLayout = itemView.findViewById(R.id.cover_layout);
			imageView = itemView.findViewById(R.id.image);
			countText = itemView.findViewById(R.id.count);
			uploadTimeText = itemView.findViewById(R.id.upload_time);
			previewButton = itemView.findViewById(R.id.preview);
			useButton = itemView.findViewById(R.id.use);
		}
	}

	public interface OnClickListener {

		void onPreviewButtonClick(View v, Material material, int position);

		void onUseButtonClick(View v, Material material, int position);

	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public interface OnItemClickListener {
		void onItemClick(int position);
	}

	private OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(OnItemClickListener itemClickListener) {
		this.mOnItemClickListener = itemClickListener;
	}
}
