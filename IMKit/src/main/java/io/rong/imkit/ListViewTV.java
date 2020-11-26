package io.rong.imkit;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListAdapter;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import io.rong.imkit.widget.AutoRefreshListView;

/**
 * @author luopan@centerm.com
 * @date 2020-03-11 11:12.
 */
public class ListViewTV extends AutoRefreshListView {

	private String TAG = ListViewTV.class.getSimpleName();
	private int itemsCount;

	private int itemHeight;

	private ListAdapter adapter;

	private int scrollDuration = 100;

	private boolean isScrollTop;

	private Timer timer;

	private OnScrollBottomListener onScrollBottomListener;

	private OnScrollTopListener onScrollTopListener;

	public ListViewTV(Context context) {
		this(context, null);
	}

	public ListViewTV(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setSmoothScrollbarEnabled(true);

	}


	public void setItemsCount(int itemsCount) {
		this.itemsCount = itemsCount;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (adapter != null) {
			// 获取每个item 的高度，因为要调用滑动的方法，每次滑动的距离就是item 的高度
			itemHeight = this.getChildAt(0).getHeight();
		}

	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		this.adapter = adapter;
		// 获取listview
		// item的count，一定要是由adapter获得，不能通过listView，因为listView是动态添加删除孩子的，可以打印一下比较看看

	}

	/**
	 * 设置滚动动画的滚动时间
	 *
	 * @param scrollDutation
	 */
	public void setScrollDuration(int scrollDutation) {
		this.scrollDuration = scrollDutation;

	}

	public int currentItemPosition;

	public void setFocedPosition(int position) {
		currentItemPosition = position;
	}


	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		int lastVisiblePosition = getLastVisiblePosition();
//		this.smoothScrollBy(itemHeight, scrollDuration);
		// 获取当前被选中的位置
		Log.e(TAG, "currentItemPosition: " + currentItemPosition + "  itemsCount:" + itemsCount + "  lastVisiblePosition:" + lastVisiblePosition);
//		this.setSelectionFromTop(currentItemPosition, this.getHeight()/2);
		if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
			this.smoothScrollToPosition(currentItemPosition+1);
			return false;
		}

		if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {

		}
		return super.dispatchKeyEvent(event);
	}


	private void smoothScrollToBottom() {
		Log.e(TAG, "平滑移动到最后");
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				ListViewTV.this.post(new Runnable() {

					@Override
					public void run() {
						ListViewTV.this
								.setSelection(ListViewTV.this
										.getLastVisiblePosition());

					}
				});

			}
		}, scrollDuration / 3);
	}

	/**
	 * 当滚动到底部的时候的监听
	 */
	public interface OnScrollBottomListener {
		void onScrollBottom();
	}

	public void setOnScrollBottomListener(OnScrollBottomListener onScrollBottomListener) {

		this.onScrollBottomListener = onScrollBottomListener;
	}

	/**
	 * 当滚动到顶部的时候的监听
	 */
	public interface OnScrollTopListener {
		void onScrollTop();
	}

	public void setOnScrollTopListener(OnScrollTopListener onScrollTopListener) {
		isScrollTop = true;
		this.onScrollTopListener = onScrollTopListener;
	}

}
