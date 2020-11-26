package com.zhongyou.meettvapplicaion.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yuna on 2017/7/17.
 */

public class FocusFixedLinearLayoutManager extends android.support.v7.widget.LinearLayoutManager {

    private boolean interceptLeft = true;
    private boolean interceptRight = true;
    private boolean interceptTop;
    private boolean interceptBottom;

    public FocusFixedLinearLayoutManager(Context context) {
        super(context);
    }

    public FocusFixedLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public FocusFixedLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr,
                                         int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onInterceptFocusSearch(View focused, int direction) {
//        ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if((focused.getLayoutParams() instanceof RecyclerView.LayoutParams)){
            int pos = getPosition(focused);
            int count = getItemCount();
            int orientation = getOrientation();
            if (direction == View.FOCUS_RIGHT) {
                if (interceptRight) {
                    if (pos == count - 1) {
                        return focused;
                    }
                }

            } else if (direction == View.FOCUS_LEFT) {
                if (interceptLeft) {
                    if (pos == 0) {
                        return focused;
                    }
                }

            }
        }
        return super.onInterceptFocusSearch(focused, direction);
    }

    public boolean isInterceptLeft() {
        return interceptLeft;
    }

    public void setInterceptLeft(boolean interceptLeft) {
        this.interceptLeft = interceptLeft;
    }

    public boolean isInterceptRight() {
        return interceptRight;
    }

    public void setInterceptRight(boolean interceptRight) {
        this.interceptRight = interceptRight;
    }

    public boolean isInterceptTop() {
        return interceptTop;
    }

    public void setInterceptTop(boolean interceptTop) {
        this.interceptTop = interceptTop;
    }

    public boolean isInterceptBottom() {
        return interceptBottom;
    }

    public void setInterceptBottom(boolean interceptBottom) {
        this.interceptBottom = interceptBottom;
    }
}