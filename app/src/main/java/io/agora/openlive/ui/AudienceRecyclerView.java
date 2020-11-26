package io.agora.openlive.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.TextureView;

import java.util.HashMap;

import io.agora.openlive.model.VideoStatusData;

public class AudienceRecyclerView extends RecyclerView {
    public AudienceRecyclerView(Context context) {
        super(context);
    }

    public AudienceRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AudienceRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private AudienceRecyclerViewAdapter audienceRecyclerViewAdapter;

    private VideoViewEventListener mEventListener;

    public void setItemEventHandler(VideoViewEventListener listener) {
        this.mEventListener = listener;
    }

    private boolean initAdapter(int localUid, HashMap<Integer, TextureView> uids) {
        if (audienceRecyclerViewAdapter == null) {
            audienceRecyclerViewAdapter = new AudienceRecyclerViewAdapter(getContext(), localUid, uids, mEventListener);
            audienceRecyclerViewAdapter.setHasStableIds(true);
            return true;
        }
        return false;
    }

    public void initViewContainer(Context context, int localUid, HashMap<Integer, TextureView> uids) {
        boolean newCreated = initAdapter(localUid, uids);

        if (!newCreated) {
            audienceRecyclerViewAdapter.setLocalUid(localUid);
            audienceRecyclerViewAdapter.init(uids, localUid, true);
        }

        this.setAdapter(audienceRecyclerViewAdapter);

        this.setLayoutManager(new GridLayoutManager(context, 2, RecyclerView.VERTICAL, false));

//        int count = uids.size();
//        if (count <= 2) { // only local full view or or with one peer
//            this.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
//        } else if (count > 2 && count <= 4) {
//            this.setLayoutManager(new GridLayoutManager(context, 2, RecyclerView.VERTICAL, false));
//        }

        audienceRecyclerViewAdapter.notifyDataSetChanged();
    }

    public TextureView getSurfaceView(int index) {
        return audienceRecyclerViewAdapter.getItem(index).mView;
    }

    public VideoStatusData getItem(int position) {
        return audienceRecyclerViewAdapter.getItem(position);
    }
}
