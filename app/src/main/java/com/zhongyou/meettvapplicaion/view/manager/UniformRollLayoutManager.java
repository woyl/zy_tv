package com.zhongyou.meettvapplicaion.view.manager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

/**
 * 匀速滚动效果
 *
 * @author Dongce
 * create time: 2018/12/14
 */
public class UniformRollLayoutManager extends LinearLayoutManager {
    private float time;
    public static final int NORMAL_TIME = 3000;

    public UniformRollLayoutManager(Context context) {
        super(context);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                System.out.println("time = " + time);
                return time / displayMetrics.densityDpi;
            }
        };
        smoothScroller.setTargetPosition(position);
        if (position != -1) {
            startSmoothScroll(smoothScroller);
        }
    }

    public void setSmoothTime(float time) {
        this.time = time == 0.0 ? NORMAL_TIME : time;
    }
}
