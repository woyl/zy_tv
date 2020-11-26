package io.agora.openlive.ui;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.WindowManager;

import com.zhongyou.meettvapplicaion.utils.DensityUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import io.agora.openlive.model.VideoStatusData;

public class SmallVideoViewAdapter extends VideoViewAdapter {
    private final static Logger log = LoggerFactory.getLogger(SmallVideoViewAdapter.class);

    public SmallVideoViewAdapter(Context context, int exceptedUid, HashMap<Integer, TextureView> uids, VideoViewEventListener listener) {
        super(context, exceptedUid, uids, listener);
    }

    @Override
    protected void customizedInit(HashMap<Integer, TextureView> uids, boolean force) {
        for (HashMap.Entry<Integer, TextureView> entry : uids.entrySet()) {
            if (entry.getKey() != exceptedUid) {
//                entry.getValue().setZOrderOnTop(true);
//                entry.getValue().setZOrderMediaOverlay(true);
                mUsers.add(new VideoStatusData(entry.getKey(), entry.getValue(), VideoStatusData.DEFAULT_STATUS, VideoStatusData.DEFAULT_VOLUME));
            }
        }

        if (force || mItemWidth == 0 || mItemHeight == 0) {
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(outMetrics);
//            mItemWidth = outMetrics.widthPixels / 4;
//            mItemHeight = outMetrics.heightPixels / 4;
            mItemWidth = DensityUtil.dip2px(mContext, 366);
            mItemHeight = DensityUtil.dip2px(mContext, 206);
        }
    }

    @Override
    public void notifyUiChanged(HashMap<Integer, TextureView> uids, int uidExcluded, HashMap<Integer, Integer> status, HashMap<Integer, Integer> volume) {
        mUsers.clear();

        for (HashMap.Entry<Integer, TextureView> entry : uids.entrySet()) {
            log.debug("notifyUiChanged " + entry.getKey() + " " + uidExcluded);

            if (entry.getKey() != uidExcluded) {
//                entry.getValue().setZOrderOnTop(true);
//                entry.getValue().setZOrderMediaOverlay(true);
                mUsers.add(new VideoStatusData(entry.getKey(), entry.getValue(), VideoStatusData.DEFAULT_STATUS, VideoStatusData.DEFAULT_VOLUME));
            }
        }

        notifyDataSetChanged();
    }

    public int getExceptedUid() {
        return exceptedUid;
    }
}