package com.zhongyou.meettvapplicaion.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.view.View;

/**
 * Created by whatisjava on 16-11-26.
 */

public class GridSpaceItemDecoration extends ItemDecoration {


    private int top;


    public GridSpaceItemDecoration(int top) {
        this.top = top;

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            outRect.top = top;
        }
    }




}
