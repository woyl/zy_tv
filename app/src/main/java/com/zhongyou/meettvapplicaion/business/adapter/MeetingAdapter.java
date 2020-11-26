package com.zhongyou.meettvapplicaion.business.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.entities.Meeting;
import com.zhongyou.meettvapplicaion.view.GeneralAdapter;
import com.zhongyou.meettvapplicaion.view.OpenPresenter;

import java.util.ArrayList;


/**
 * Created by whatisjava on 17-10-18.
 */

public class MeetingAdapter extends OpenPresenter {

    private Context mContext;
    private ArrayList<Meeting> meetings;

    private int[] colors = new int[]{ R.color.c_e646006a, R.color.c_e600556a, R.color.c_e66a5200 };

    private GeneralAdapter mAdapter;
    private boolean isFirst = true;

    public MeetingAdapter(Context context, ArrayList<Meeting> meetings) {
        this.mContext = context;
        this.meetings = meetings;
    }

    @Override
    public void setAdapter(GeneralAdapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public int getItemCount() {
        return meetings != null ? meetings.size() : 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_meeting, parent, false);
        return new MeetingHodler(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        Meeting meeting = meetings.get(position);
        MeetingHodler holder = (MeetingHodler) viewHolder;
        holder.titleText.setText(meeting.getTitle());
        holder.beginTimeText.setText(meeting.getStartTime().substring(0, 16));
        if (meeting.getMeetingProcess() == 1) {
            holder.stateText.setBackgroundColor(mContext.getResources().getColor(R.color.calling_name_color));
            holder.stateText.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.stateText.setText("进行中");
        } else {
            holder.stateText.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
            holder.stateText.setTextColor(mContext.getResources().getColor(R.color.c_909090));
            holder.stateText.setText("未开始");
        }
        holder.bgLayout.setBackgroundColor(mContext.getResources().getColor(colors[position % 3]));

        if (isFirst && position == 0) {
            holder.itemView.requestFocus();
            isFirst = false;
        }
    }

    private class MeetingHodler extends ViewHolder {

        View itemView;
        LinearLayout bgLayout;
        TextView titleText, beginTimeText, stateText;

        MeetingHodler(View itemView) {
            super(itemView);
            this.itemView = itemView;
            bgLayout = (LinearLayout) itemView.findViewById(R.id.background);
            titleText = (TextView) itemView.findViewById(R.id.title);
            beginTimeText = (TextView) itemView.findViewById(R.id.begin_time);
            stateText = (TextView) itemView.findViewById(R.id.meeting_state);
        }
    }

}
