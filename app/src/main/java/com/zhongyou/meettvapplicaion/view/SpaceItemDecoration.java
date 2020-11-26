package com.zhongyou.meettvapplicaion.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.view.View;

/**
 * Created by whatisjava on 16-11-26.
 */

public class SpaceItemDecoration extends ItemDecoration {


	private int left;
	private int top;
	private int right;
	private int bottom;

	public SpaceItemDecoration(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		outRect.left = left;
		outRect.top = top;
		outRect.right = right;
		outRect.bottom = bottom;
	}

}
