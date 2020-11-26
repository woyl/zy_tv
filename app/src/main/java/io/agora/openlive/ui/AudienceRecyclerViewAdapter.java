package io.agora.openlive.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.zhongyou.meettvapplicaion.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import io.agora.openlive.model.VideoStatusData;

public class AudienceRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static Logger log = LoggerFactory.getLogger(AudienceRecyclerViewAdapter.class);

    protected final LayoutInflater mInflater;
    protected final Context mContext;

    protected final VideoViewEventListener mListener;

    private ArrayList<VideoStatusData> mUsers;

    public AudienceRecyclerViewAdapter(Context context, int localUid, HashMap<Integer, TextureView> uids, VideoViewEventListener listener) {
        mContext = context;
        mInflater = ((Activity) context).getLayoutInflater();

        mListener = listener;

        mUsers = new ArrayList<>();

        init(uids, localUid, false);
    }

    protected int mItemWidth;
    protected int mItemHeight;

    private int mLocalUid;

    public void setLocalUid(int uid) {
        mLocalUid = uid;
    }

    public int getLocalUid() {
        return mLocalUid;
    }

    public void init(HashMap<Integer, TextureView> uids, int localUid, boolean force) {
        for (HashMap.Entry<Integer, TextureView> entry : uids.entrySet()) {
            if (entry.getKey() == 0 || entry.getKey() == mLocalUid) {
                boolean found = false;
                for (VideoStatusData status : mUsers) {
                    if ((status.mUid == entry.getKey() && status.mUid == 0) || status.mUid == mLocalUid) { // first time
                        status.mUid = mLocalUid;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    mUsers.add(0, new VideoStatusData(mLocalUid, entry.getValue(), VideoStatusData.DEFAULT_STATUS, VideoStatusData.DEFAULT_VOLUME));
                }
            } else {
                boolean found = false;
                for (VideoStatusData status : mUsers) {
                    if (status.mUid == entry.getKey()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    mUsers.add(new VideoStatusData(entry.getKey(), entry.getValue(), VideoStatusData.DEFAULT_STATUS, VideoStatusData.DEFAULT_VOLUME));
                }
            }
        }

        Iterator<VideoStatusData> it = mUsers.iterator();
        while (it.hasNext()) {
            VideoStatusData status = it.next();

            if (uids.get(status.mUid) == null) {
                log.warn("after_changed remove not exited members " + (status.mUid & 0xFFFFFFFFL) + " " + status.mView);
                it.remove();
            }
        }

        if (force || mItemWidth == 0 || mItemHeight == 0) {
            mItemWidth = 300;
            mItemHeight = 168;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.video_view_container, parent, false);
        v.getLayoutParams().width = mItemWidth;
        v.getLayoutParams().height = mItemHeight;
        return new VideoUserStatusHolder(v);
    }

    protected final void stripSurfaceView(TextureView view) {
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((FrameLayout) parent).removeView(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VideoUserStatusHolder myHolder = ((VideoUserStatusHolder) holder);

        final VideoStatusData user = mUsers.get(position);

        log.debug("onBindViewHolder " + position + " " + user + " " + myHolder + " " + myHolder.itemView);

        FrameLayout holderView = (FrameLayout) myHolder.itemView;

        holderView.setOnTouchListener(new OnDoubleTapListener(mContext) {
            @Override
            public void onDoubleTap(View view, MotionEvent e) {
                if (mListener != null) {
                    mListener.onItemDoubleClick(view, user);
                }
            }

            @Override
            public void onSingleTapUp() {
            }
        });

        if (holderView.getChildCount() == 0) {
            TextureView target = user.mView;
            stripSurfaceView(target);
            holderView.addView(target, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    @Override
    public int getItemCount() {
//        int sizeLimit = mUsers.size();
//        if (sizeLimit >= ConstantApp.MAX_PEER_COUNT + 1) {
//            sizeLimit = ConstantApp.MAX_PEER_COUNT + 1;
//        }
        return mUsers.size();
    }

    public VideoStatusData getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        VideoStatusData user = mUsers.get(position);

        TextureView view = user.mView;
        if (view == null) {
            throw new NullPointerException("SurfaceView destroyed for user " + user.mUid + " " + user.mStatus + " " + user.mVolume);
        }

        return (String.valueOf(user.mUid) + System.identityHashCode(view)).hashCode();
    }
}
