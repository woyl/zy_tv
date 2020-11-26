package com.zhongyou.meettvapplicaion.business.adapter;

import android.content.Context;
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
import com.zhongyou.meettvapplicaion.view.GeneralAdapter;
import com.zhongyou.meettvapplicaion.view.OpenPresenter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by whatisjava on 17-10-18.
 */

public class StatusAdapter extends OpenPresenter {

    private Context mContext;
    private ArrayList<Status> statuses = new ArrayList<Status>();

    private GeneralAdapter mAdapter;

    private boolean isFinish = false;

    public StatusAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public void setAdapter(GeneralAdapter adapter) {
        this.mAdapter = adapter;
    }

    public void insertLastItems(ArrayList<Status> lists) {
        mAdapter.notifyItemRangeInserted(getItemCount(), lists.size());
        this.statuses.addAll(lists);
    }

    @Override
    public int getItemCount() {
        return statuses != null ? statuses.size() : 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_status, parent, false);
        return new StatuesHodler(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        Status status = statuses.get(position);

        StatuesHodler statuesHodler = (StatuesHodler) viewHolder;
        String url = status.getSpaceStatusPublishList().get(0).getUrl();
        if (status.getType() == 1) {
            String imageUrl = ImageHelper.videoFrameUrl(url, DensityUtil.dip2px(mContext, 235), DensityUtil.dip2px(mContext, 139));
            Picasso.with(mContext)
                    .load(imageUrl)
                    .into(statuesHodler.imageView);
        } else if (status.getType() == 0) {
            Picasso.with(mContext)
                    .load(ImageHelper.getThumAndCrop(url, DensityUtil.px2dip(mContext, 235), DensityUtil.px2dip(mContext, 139)))
                    .into(statuesHodler.imageView);
        }

        if (status.getType() == 0) {
            statuesHodler.videoTagImage.setVisibility(View.GONE);
        } else if (status.getType() == 1) {
            statuesHodler.videoTagImage.setVisibility(View.VISIBLE);
        }

        statuesHodler.titleText.setText(status.getStatus());
        statuesHodler.viewText.setText(String.valueOf(status.getViewingNum()));
        statuesHodler.likeText.setText(String.valueOf(status.getLikeNum()));

        viewHolder.view.setOnKeyListener((v, keyCode, event) -> {
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

    private class StatuesHodler extends ViewHolder {

        ImageView imageView, videoTagImage;
        TextView titleText, viewText, likeText;

        public StatuesHodler(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            videoTagImage = itemView.findViewById(R.id.video_tag);
            titleText = itemView.findViewById(R.id.title);
            viewText = itemView.findViewById(R.id.view_num);
            likeText = itemView.findViewById(R.id.like_num);
        }
    }

}
