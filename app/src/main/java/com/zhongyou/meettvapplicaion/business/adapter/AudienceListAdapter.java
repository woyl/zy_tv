package com.zhongyou.meettvapplicaion.business.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.view.GeneralAdapter;
import com.zhongyou.meettvapplicaion.view.OpenPresenter;

import java.util.ArrayList;

/**
 * Created by whatisjava on 17-10-18.
 */

public class AudienceListAdapter extends OpenPresenter {

    private Context mContext;
    private ArrayList<SurfaceView> surfaceViews;
    private GeneralAdapter mAdapter;

    public AudienceListAdapter(Context context, ArrayList<SurfaceView> surfaceViews) {
        this.mContext = context;
        this.surfaceViews = surfaceViews;
    }

    public void setData(ArrayList<SurfaceView> surfaceViews){
        this.surfaceViews = surfaceViews;
        notifyDataSetChanged();
    }

    @Override
    public void setAdapter(GeneralAdapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public int getItemCount() {
        return surfaceViews.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_audience_list, parent, false);
        return new AudienceListHodler(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        AudienceListHodler holder = (AudienceListHodler) viewHolder;

        holder.nameText.setText("name-" + position);
        if (surfaceViews.get(position).getParent() != null) {
            ((FrameLayout)surfaceViews.get(position).getParent()).removeAllViews();
            Log.v("onBindView", "has parent");
        } else {
            Log.v("onBindView", "dont have parent");
        }
        holder.surfaceViewLayout.addView(surfaceViews.get(position));

    }

    private class AudienceListHodler extends ViewHolder {

        FrameLayout surfaceViewLayout;
        ImageView micStatusImage;
        TextView nameText;

        public AudienceListHodler(View itemView) {
            super(itemView);

            surfaceViewLayout = itemView.findViewById(R.id.surface_view);
            micStatusImage = itemView.findViewById(R.id.status);
            nameText = itemView.findViewById(R.id.name);
        }
    }

}
