package com.zhongyou.meettvapplicaion.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class MyTvListview extends ListView {
    public MyTvListview(Context context) {
        super(context);
    }

    public MyTvListview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTvListview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        int lastSelectItem = getSelectedItemPosition();
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            View v = getChildAt(lastSelectItem - getFirstVisiblePosition());
            int top = (v == null) ? 0 : v.getTop();
            setSelectionFromTop(lastSelectItem, top);
        }
    }
}
