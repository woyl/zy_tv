package com.zhongyou.meettvapplicaion.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by wufan on 2017/6/28.
 */

public class FocusFixedGridLayoutManager extends android.support.v7.widget.GridLayoutManager {
    private int spancount = 0;
    private int lastFirst = 0;
    private int lastLast = 0;
    private boolean downFlag = false;

    public FocusFixedGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public FocusFixedGridLayoutManager(Context context, int spanCount, boolean downFlag) {
        super(context, spanCount);
        spancount = spanCount;
        this.downFlag = downFlag;
    }

    public FocusFixedGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout, boolean downFlag) {
        super(context, spanCount, orientation, reverseLayout);
        spancount = spanCount;
        this.downFlag = downFlag;
    }

    @Override
    public View onInterceptFocusSearch(View focused, int direction) {

        int pos = getPosition(focused);
        int count = getItemCount();
        if (count % spancount == 0) {
            lastFirst = ((count / spancount) - 1) * spancount;
        } else {
            lastFirst = ((count / spancount)) * spancount;
        }

        Log.v("lastFirst==", "lastFirst==" + lastFirst);

        if (direction == View.FOCUS_RIGHT) {
            if (pos == count - 1) {
                return focused;
            }
        } else if (direction == View.FOCUS_LEFT) {
            if (pos == 0) {
                return focused;
            }
        } else if (direction == View.FOCUS_DOWN) {
            if (downFlag) {
                if (pos >= lastFirst) {
                    return focused;
                }
            }

        }
        return super.onInterceptFocusSearch(focused, direction);
    }
}