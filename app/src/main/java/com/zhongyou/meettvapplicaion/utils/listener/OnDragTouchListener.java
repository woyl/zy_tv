package com.zhongyou.meettvapplicaion.utils.listener;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.orhanobut.logger.Logger;

import me.jessyan.autosize.utils.AutoSizeUtils;
import me.jessyan.autosize.utils.ScreenUtils;

/**
 * @author luopan@centerm.com
 * @date 2019-12-31 10:22.
 */
public class OnDragTouchListener implements View.OnTouchListener {

	private int mScreenWidth, mScreenHeight;//屏幕宽高
	private float mOriginalX, mOriginalY;//手指按下时的初始位置
	private float mDistanceX, mDistanceY;//记录手指与view的左上角的距离
	private int left, top, right, bottom;
	private OnDraggableClickListener mListener;
	private boolean hasAutoPullToBorder;//标记是否开启自动拉到边缘功能

	public OnDragTouchListener() {
	}

	public OnDragTouchListener(boolean isAutoPullToBorder) {
		hasAutoPullToBorder = isAutoPullToBorder;
	}

	@Override
	public boolean onTouch(final View v, MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mScreenWidth = v.getResources().getDisplayMetrics().widthPixels;
				mScreenHeight = v.getResources().getDisplayMetrics().heightPixels;
				mOriginalX = event.getRawX();
				mOriginalY = event.getRawY();
				mDistanceX = event.getRawX() - v.getLeft();
				mDistanceY = event.getRawY() - v.getTop();
				break;
			case MotionEvent.ACTION_MOVE:
				left = (int) (event.getRawX() - mDistanceX);
				top = (int) (event.getRawY() - mDistanceY);
				right = left + v.getWidth();
				bottom = top + v.getHeight();
				if (left < 0) {
					left = 0;
					right = left + v.getWidth();
				}
				if (top < 0) {
					top = 0;
					bottom = top + v.getHeight();
				}
				if (right > mScreenWidth) {
					right = mScreenWidth;
					left = right - v.getWidth();
				}
				if (bottom > mScreenHeight) {
					bottom = mScreenHeight;
					top = bottom - v.getHeight();
				}
				//在拖动过按钮后，如果其他view刷新导致重绘，会让按钮重回原点，所以需要更改布局参数
				ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
				lp.leftMargin=left;
				lp.rightMargin=right;
				lp.topMargin=top;
				lp.bottomMargin=bottom;
				Logger.e("left="+left+"   top="+top+"    right="+right+"   bottom="+bottom);
				v.setLayoutParams(lp);
				v.postInvalidate();
				break;
			case MotionEvent.ACTION_UP:
			/*	//在拖动过按钮后，如果其他view刷新导致重绘，会让按钮重回原点，所以需要更改布局参数
				ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
				lp.leftMargin=left;
				lp.rightMargin=right;
				lp.topMargin=top;
				lp.bottomMargin=bottom;
				v.setLayoutParams(lp);*/

//				startAutoPull(v, lp);
				//如果移动距离过小，则判定为点击
				/*if (Math.abs(event.getRawX() - mOriginalX) <
						AutoSizeUtils.dp2px(v.getContext(), 5) &&
						Math.abs(event.getRawY() - mOriginalY) <
								AutoSizeUtils.dp2px(v.getContext(), 5)) {
					if (mListener != null) {
						mListener.onClick(v);
					}
				}
				//消除警告
				v.performClick();*/
				break;
		}
		return true;
	}

	public OnDraggableClickListener getOnDraggableClickListener() {
		return mListener;
	}

	public void setOnDraggableClickListener(OnDraggableClickListener listener) {
		mListener = listener;
	}

	public boolean isHasAutoPullToBorder() {
		return hasAutoPullToBorder;
	}

	public void setHasAutoPullToBorder(boolean hasAutoPullToBorder) {
		this.hasAutoPullToBorder = hasAutoPullToBorder;
	}

	/**
	 * 开启自动拖拽
	 *
	 * @param v  拉动控件
	 * @param lp 控件布局参数
	 */
	private void startAutoPull(final View v, final ViewGroup.MarginLayoutParams lp) {
		if (!hasAutoPullToBorder) {
			v.layout(left, top, right, bottom);
			lp.setMargins(left, top, 0, 0);
			v.setLayoutParams(lp);
			if (mListener != null) {
				mListener.onDragged(v, left, top);
			}
			return;
		}
		//当用户拖拽完后，让控件根据远近距离回到最近的边缘
		float end = 0;
		if ((left + v.getWidth() / 2) >= mScreenWidth / 2) {
			end = mScreenWidth - v.getWidth();
		}
		ValueAnimator animator = ValueAnimator.ofFloat(left, end);
		animator.setInterpolator(new DecelerateInterpolator());
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int leftMargin = (int) ((float) animation.getAnimatedValue());
				v.layout(leftMargin, top, right, bottom);
				Logger.e("left="+leftMargin+"   top="+top+"    right="+right+"   bottom="+bottom);
				v.setLayoutParams(lp);
			}
		});
		final float finalEnd = end;
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				if (mListener != null) {
					mListener.onDragged(v, (int) finalEnd, top);
				}
			}
		});
		animator.setDuration(400);
		animator.start();
	}

	/**
	 * 控件拖拽监听器
	 */
	public interface OnDraggableClickListener {

		/**
		 * 当控件拖拽完后回调
		 *
		 * @param v    拖拽控件
		 * @param left 控件左边距
		 * @param top  控件右边距
		 */
		void onDragged(View v, int left, int top);

		/**
		 * 当可拖拽控件被点击时回调
		 *
		 * @param v 拖拽控件
		 */
		void onClick(View v);
	}
}
