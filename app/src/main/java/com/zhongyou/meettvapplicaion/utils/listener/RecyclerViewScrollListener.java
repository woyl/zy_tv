package com.zhongyou.meettvapplicaion.utils.listener;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * 实现了RecyclerView滚动到顶部监听的OnScrollListener
 *
 * @author Dongce
 * create time: 2018/11/20
 */
public class RecyclerViewScrollListener extends RecyclerView.OnScrollListener implements TopListener {

    // 最后几个完全可见项的位置（瀑布式布局会出现这种情况）
    private int[] firstCompletelyVisiblePositions;
    // 最后一个完全可见项的位置
    private int firstCompletelyVisibleItemPosition;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        // 找到最后一个完全可见项的位置
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) layoutManager;
            if (firstCompletelyVisiblePositions == null) {
                firstCompletelyVisiblePositions = new int[manager.getSpanCount()];
            }
            manager.findFirstCompletelyVisibleItemPositions(firstCompletelyVisiblePositions);
            firstCompletelyVisibleItemPosition = getMaxPosition(firstCompletelyVisiblePositions);
        } else if (layoutManager instanceof GridLayoutManager) {
            firstCompletelyVisibleItemPosition = ((GridLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
        } else if (layoutManager instanceof LinearLayoutManager) {
            firstCompletelyVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
        } else {
            throw new RuntimeException("Unsupported LayoutManager.");
        }
    }

    private int getMaxPosition(int[] positions) {
        int max = positions[0];
        for (int i = 1; i < positions.length; i++) {
            if (positions[i] > max) {
                max = positions[i];
            }
        }
        return max;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        // 通过比对 最后完全可见项位置 和 0位置，来判断是否滑动到顶部
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();

        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            if (visibleItemCount > 0 && firstCompletelyVisibleItemPosition == 0) {

                onScrollToTop();
            }
        }
    }

    @Override
    public void onScrollToTop() {

    }
}
