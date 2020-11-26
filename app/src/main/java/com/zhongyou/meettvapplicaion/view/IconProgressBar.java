package com.zhongyou.meettvapplicaion.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.zhongyou.meettvapplicaion.R;


public class IconProgressBar extends View {
	private Paint mIconPaint;
	private Paint mRectPaint;

	private Bitmap bitmapIcon;
	private int mBitmapHeight, mBitmapWidth;

	private int progressEndX;

	private int mProgress, mMax;
	private int mRectHeight = 20;// 矩形进度条的高度

	public IconProgressBar(Context context) {

		super(context);
		initView(context);
	}

	public IconProgressBar(Context context, AttributeSet attrs) {

		super(context, attrs);
		initView(context);
	}

	public IconProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	private void initView(Context context) {
		bitmapIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_progress);
		mBitmapHeight = bitmapIcon.getHeight();
		mBitmapWidth = bitmapIcon.getWidth();
		mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mIconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int specHeight = MeasureSpec.getSize(heightMeasureSpec);
		int height;
		int paddingY = getPaddingTop() + getPaddingBottom();
		switch (MeasureSpec.getMode(heightMeasureSpec)) {
		case MeasureSpec.EXACTLY:
			height = specHeight;
			break;
		case MeasureSpec.AT_MOST:
			height = Math.min(mBitmapHeight + paddingY, specHeight);
			break;
		case MeasureSpec.UNSPECIFIED:
		default:
			height = specHeight;
			break;
		}
		setMeasuredDimension(width, height);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		progressEndX = (int) ((getWidth() - mBitmapWidth) * (getProgress() * 1.0f / getMax()) + getPaddingLeft());

		int iconTop = (getHeight() - mBitmapHeight) / 2;

		float rectLeft = 1.0f * mBitmapWidth / 2;
		float rectTop = 1.0f * (getHeight() - mRectHeight) / 2;
		float rectRight = getWidth() - 1.0f * mBitmapWidth / 2;
		float rectButtom = 1.0f * (getHeight() + mRectHeight) / 2;
		float rectProgress = progressEndX + 1.0f * mBitmapWidth / 2;
		
		// 绘制下层图
		mRectPaint.setColor(getResources().getColor(R.color.gray_babdee));
		canvas.drawRoundRect(new RectF(rectLeft, rectTop, rectRight, rectButtom), (float)mRectHeight / 2, (float)mRectHeight / 2, mRectPaint);
		// 绘制下层选项
		mRectPaint.setColor(getResources().getColor(R.color.yellow_ffe539));
		canvas.drawRoundRect(new RectF(rectLeft, rectTop, rectProgress, rectButtom),(float)mRectHeight / 2, (float)mRectHeight / 2, mRectPaint);

		canvas.drawBitmap(bitmapIcon, progressEndX, iconTop, mIconPaint);
	}

	public synchronized void setProgress(int progress) {
		if (progress < 0) {
			progress = 0;
		}

		if (progress > mMax) {
			progress = mMax;
		}

		if (progress != mProgress) {
			mProgress = progress;
			postInvalidate();
		}
	}

	public synchronized int getProgress() {
		return mProgress;
	}

	public synchronized void setMax(int max) {
		if (max < 0) {
			max = 0;
		}
		if (max != mMax) {
			mMax = max;
			postInvalidate();
		}
	}

	public synchronized int getMax() {
		return mMax;
	}
}
