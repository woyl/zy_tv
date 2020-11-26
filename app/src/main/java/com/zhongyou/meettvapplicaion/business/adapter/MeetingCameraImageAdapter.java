package com.zhongyou.meettvapplicaion.business.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.entities.GetMeetingScreenshot;
import com.zhongyou.meettvapplicaion.utils.BitmapUtil;
import com.zhongyou.meettvapplicaion.utils.TimeUtil;
import com.zhongyou.meettvapplicaion.utils.statistics.ZYAgent;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author Dongce
 * create time: 2018/12/7
 */
public class MeetingCameraImageAdapter extends RecyclerView.Adapter<MeetingCameraImageAdapter.MeetingCameraImageViewHolder> {

    private Context mContext;
    private ArrayList<GetMeetingScreenshot> meetingCameraImagesData = new ArrayList<>();

    public MeetingCameraImageAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void addData(ArrayList<GetMeetingScreenshot> datas) {
        int currentLastItemIndex = this.meetingCameraImagesData.size();
        this.meetingCameraImagesData.addAll(datas);
        ZYAgent.onEvent(mContext, "最新会议图像总数是" + this.meetingCameraImagesData.size() + "个图像");
        notifyItemRangeInserted(currentLastItemIndex, datas.size());
    }

    public void clearData() {
        this.meetingCameraImagesData.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MeetingCameraImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_broadcast_meeting_camera_layout, parent, false);
        return new MeetingCameraImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingCameraImageViewHolder viewHolder, int position) {
        final GetMeetingScreenshot getMeetingScreenshot = meetingCameraImagesData.get(position);

        MeetingCameraImageViewHolder holder = viewHolder;

        Picasso.with(mContext)
                .load(getMeetingScreenshot.getImgUrl())
                .transform(BitmapUtil.getTransformation(holder.item_broadcast_meetingcamera_image))
                .placeholder(R.drawable.item_forum_img_loading)
                .error(R.drawable.item_forum_img_error)
                .config(Bitmap.Config.RGB_565)
                .into(holder.item_broadcast_meetingcamera_image);

        String userInfo = getMeetingScreenshot.getUserName() + " " + getMeetingScreenshot.getAreaName();
        holder.item_broadcast_meetingcamera_userInfo.setText(userInfo);

        holder.item_broadcast_meetingcamera_ts.setText(TimeUtil.getHourAndMinute(getMeetingScreenshot.getCreateDate()));
    }

    @Override
    public int getItemCount() {
        return meetingCameraImagesData.size();
    }

    class MeetingCameraImageViewHolder extends RecyclerView.ViewHolder {
        private TextView item_broadcast_meetingcamera_userInfo, item_broadcast_meetingcamera_ts;
        private ImageView item_broadcast_meetingcamera_image;

        private MeetingCameraImageViewHolder(View itemView) {
            super(itemView);
            item_broadcast_meetingcamera_userInfo = itemView.findViewById(R.id.item_broadcast_meetingcamera_userInfo);
            item_broadcast_meetingcamera_ts = itemView.findViewById(R.id.item_broadcast_meetingcamera_ts);
            item_broadcast_meetingcamera_image = itemView.findViewById(R.id.item_broadcast_meetingcamera_image);
        }
    }
}
