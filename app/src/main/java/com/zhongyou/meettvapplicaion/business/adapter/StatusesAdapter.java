package com.zhongyou.meettvapplicaion.business.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.entities.Status;
import com.zhongyou.meettvapplicaion.event.ListRequestFocusEvent;
import com.zhongyou.meettvapplicaion.utils.DensityUtil;
import com.zhongyou.meettvapplicaion.utils.RxBus;
import com.zhongyou.meettvapplicaion.utils.helper.ImageHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by whatisjava on 17-10-18.
 */

public class StatusesAdapter extends RecyclerView.Adapter<StatusesAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Status> statuses = new ArrayList<Status>();

    private boolean isFinish = false;

    public StatusesAdapter(Context context) {
        this.mContext = context;
    }

    public void addData(ArrayList<Status> statuses) {
        this.statuses.addAll(statuses);
        notifyItemRangeInserted(this.statuses.size(), statuses.size());
    }

    public void cleanData(){
        this.statuses.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_status, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Status status = statuses.get(position);

        String url = status.getSpaceStatusPublishList().get(0).getUrl();
        if (status.getType() == 1) {
            String imageUrl = ImageHelper.videoFrameUrl(url, DensityUtil.dip2px(mContext, 235), DensityUtil.dip2px(mContext, 139));
            Picasso.with(mContext)
                    .load(imageUrl)
                    .into(viewHolder.imageView);
        } else if (status.getType() == 0) {
            Picasso.with(mContext)
                    .load(ImageHelper.getThumAndCrop(url, DensityUtil.px2dip(mContext, 235), DensityUtil.px2dip(mContext, 139)))
                    .into(viewHolder.imageView);
        }

        if (status.getType() == 0) {
            viewHolder.videoTagImage.setVisibility(View.GONE);
        } else if (status.getType() == 1) {
            viewHolder.videoTagImage.setVisibility(View.VISIBLE);
        }

        viewHolder.titleText.setText(status.getStatus());
        viewHolder.viewText.setText(String.valueOf(status.getViewingNum()));
        viewHolder.likeText.setText(String.valueOf(status.getLikeNum()));
        viewHolder.itemView.setOnKeyListener((v, keyCode, event) -> {
            if ((position % 4) == 0) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    if (isFinish) {
                        isFinish = false;
                    } else {
                        RxBus.sendMessage(new ListRequestFocusEvent(0));
                    }
                    return true;
                }
            }
            if ((position % 4) == 1) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    isFinish = true;
                }
            }
            return false;
        });

    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView, videoTagImage;
        TextView titleText, viewText, likeText;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            videoTagImage = itemView.findViewById(R.id.video_tag);
            titleText = itemView.findViewById(R.id.title);
            viewText = itemView.findViewById(R.id.view_num);
            likeText = itemView.findViewById(R.id.like_num);
        }
    }

}
