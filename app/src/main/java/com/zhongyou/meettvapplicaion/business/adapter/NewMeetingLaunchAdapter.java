package com.zhongyou.meettvapplicaion.business.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhongyou.meettvapplicaion.Constant;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.business.MeetingsActivityLaunch;
import com.zhongyou.meettvapplicaion.business.SearchActivity;
import com.zhongyou.meettvapplicaion.entities.Meeting;
import com.zhongyou.meettvapplicaion.view.GeneralAdapter;
import com.zhongyou.meettvapplicaion.view.OpenPresenter;

import java.util.ArrayList;

public class NewMeetingLaunchAdapter extends OpenPresenter {

	private Context mContext;
	private ArrayList<Meeting> meetings = new ArrayList<>();

//    private int[] colors = new int[]{ R.color.c_e646006a, R.color.c_e600556a, R.color.c_e66a5200 };

	private GeneralAdapter mAdapter;
	private boolean isFirst = true;

	public NewMeetingLaunchAdapter(Context context, ArrayList<Meeting> meetings) {
		this.mContext = context;
		this.meetings = meetings;
		Log.v("meetingsactivityl", "结构方法：" + meetings.size());
	}

	public void changeData( ArrayList<Meeting> meetings){
		this.meetings=meetings;
		notifyDataSetChanged();
	}


	@Override
	public void setAdapter(GeneralAdapter adapter) {
		this.mAdapter = adapter;
	}

	@Override
	public int getItemCount() {

		Log.v("meetingsactivityl", "getItemCount*****************" + meetings.size());
		return meetings != null ? meetings.size() : 0;
	}

	public void CreateMeeting(Meeting meeting) {

		if (meetings == null) {
			meetings = new ArrayList<>();
		}
		mAdapter.notifyItemRangeInserted(meetings.size(), 1);
//        Meeting meeting1 = new Meeting();
////
//        meeting1.setId(meeting.get(0).getId()+"12");
//        meeting1.setTitle(meeting.get(0).getTitle()+"12");
		meetings.add(meeting);
		Log.v("meetingsactivityl", "CreateMeeting*****************" + meetings.size());
	}

	public void UpdateMeeting(Meeting meeting) {
		Log.v("meetingsactivityl2", "UpdateMeeting*****************");
		for (int i = 0; i < meetings.size(); i++) {
			if (meeting.getId().equals(meetings.get(i).getId())) {
				meetings.get(i).setId(meeting.getId());
				meetings.get(i).setStartTime(meeting.getStartTime());
				meetings.get(i).setTitle(meeting.getTitle());
				meetings.get(i).setMeetingProcess(meeting.getMeetingProcess());
				mAdapter.notifyItemChanged(i);
			}
		}

//        mAdapter.notifyItemRangeChanged();
//        mAdapter.notifyDataSetChanged();
	}

	public void DeleteMeeting(Meeting meeting) {
		for (int i = 0; i < meetings.size(); i++) {
			if (meeting.getId().equals(meetings.get(i).getId())) {
				meetings.get(i).setId(meeting.getId());
				meetings.get(i).setStartTime(meeting.getStartTime());
				meetings.get(i).setTitle(meeting.getTitle());
				meetings.get(i).setMeetingProcess(meeting.getMeetingProcess());
				mAdapter.notifyItemRemoved(i);
			}
		}
		meetings.remove(meeting);

//        mAdapter.notifyDataSetChanged();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view;
		if (Constant.isPadApplicaion){
			view = LayoutInflater.from(mContext).inflate(R.layout.item_meeting_launch_pad, parent, false);
		}else {
			view = LayoutInflater.from(mContext).inflate(R.layout.item_meeting_launch, parent, false);
		}
		return new MeetingHodler(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
		Meeting meeting = meetings.get(position);
		MeetingHodler holder = (MeetingHodler) viewHolder;
		holder.titleText.setText(meeting.getTitle());
		holder.beginTimeText.setText(meeting.getStartTime().substring(0, 16));
		holder.beginTimeText.setBackground(mContext.getResources().getDrawable(R.drawable.blackleft));
		if ((position+1) % 2!= 0) {
			holder.imgMeetingBg.setBackground(mContext.getResources().getDrawable(R.drawable.background_01));
		} else if ((position+1) % 2== 0) {
			holder.imgMeetingBg.setBackground(mContext.getResources().getDrawable(R.drawable.background_02));
		}
		if (meeting.getMeetingProcess() == 1) {
			holder.stateText.setText("进行中");
			holder.stateText.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.item_status_color));
		} else {

			holder.stateText.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.item_status_color_not_begin));
			holder.stateText.setText("未开始");
		}


		holder.bgLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((SearchActivity)mContext).goToMeeting(meeting);
			}
		});




	}


	private class MeetingHodler extends ViewHolder {

		View itemView;
		RelativeLayout bgLayout;
		TextView titleText, beginTimeText, stateText;
		ImageView imgMeetingBg;

		MeetingHodler(View itemView) {
			super(itemView);
			this.itemView = itemView;
			bgLayout=itemView.findViewById(R.id.layout_bg);
			titleText = (TextView) itemView.findViewById(R.id.tv_title);
			beginTimeText = (TextView) itemView.findViewById(R.id.tv_time);
			stateText = (TextView) itemView.findViewById(R.id.tv_start);
			imgMeetingBg = (ImageView) itemView.findViewById(R.id.meeting_bg);
		}
	}

}

