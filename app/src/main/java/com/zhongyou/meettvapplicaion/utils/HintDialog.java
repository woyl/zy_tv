package com.zhongyou.meettvapplicaion.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhongyou.meettvapplicaion.R;


public class HintDialog extends AlertDialog {
	/** 确定 */
	public static final int BUTTON_POSITIVE = 0;
	/** 取消 */
	public static final int BUTTON_NEGATIVE = -1;

	private HintDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	private HintDialog(Context context, int theme) {
		super(context, theme);
	}

	private HintDialog(Context context) {
		super(context);
	}

	@SuppressLint("InflateParams")
	public static class Builder extends AlertDialog.Builder {
		private Context context;
		private HintDialog dialog;
		private String title;
		private String msg;
		private ClickListener clickListener;
		private TextView tvTitle;
		private TextView tvMsg;
		private Button btLeft;
		private Button btRight;
		private LinearLayout llContent;
		private boolean isSimpleSelection = false;// 是否单选
		private int backgroundResId;
		private int leftImgId;
		private int rightImgId;
		private int msgGravity;
		private int msgPaddingLeft;
		private int msgPaddingTop;
		private int msgPaddingRight;
		private int msgPaddingBottom;
		private int resMsgColor;
		private boolean cancelable = true;
		private String positiveMsg;
		private String negativeMsg;


		public Builder(Context context, int theme) {
			super(context, theme);
			this.context = context;
		}

		public Builder(Context context) {
			super(context);
			this.context = context;
		}

		public HintDialog create() {
			dialog = new HintDialog(context, R.style.MyDialog);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			View view = createView();
			dialog.setCancelable(cancelable);
			dialog.setView(view, 0, 0, 0, 0);
			btLeft.requestFocus();
			return dialog;
		}

		public HintDialog createSingleSelectionDialog() {
			isSimpleSelection = true;
			return create();
		}

		@SuppressWarnings("deprecation")
		private View createView() {
			LayoutInflater inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.hint_double, null);
			llContent = (LinearLayout) view.findViewById(R.id.ll_content);
			tvTitle = (TextView) view.findViewById(R.id.tv_title);
			tvMsg = (TextView) view.findViewById(R.id.tv_msg);
			if (msgGravity != 0) {
				tvMsg.setGravity(msgGravity);
			}
			// 20dp为初始值
			tvMsg.setPadding(msgPaddingLeft + 80, msgPaddingTop, msgPaddingRight + 80, msgPaddingBottom);
			btLeft = (Button) view.findViewById(R.id.bt_left);
			btLeft.requestFocus();
			btRight = (Button) view.findViewById(R.id.bt_right);
			tvTitle.setText(title);
			tvMsg.setText(msg);

			btLeft.setText(positiveMsg);

			btRight.setText(negativeMsg);
			if (TextUtils.isEmpty(negativeMsg)){
				btRight.setVisibility(View.GONE);
				view.findViewById(R.id.img_line).setVisibility(View.GONE);
			}else {
				btRight.setVisibility(View.VISIBLE);
				view.findViewById(R.id.img_line).setVisibility(View.VISIBLE);
			}
			if (resMsgColor != 0) {
				tvMsg.setTextColor(resMsgColor);
			}
			if (leftImgId != 0) {
				btLeft.setBackgroundDrawable(context.getResources().getDrawable(leftImgId));
			}
			if (rightImgId != 0) {
				btRight.setBackgroundDrawable(context.getResources().getDrawable(rightImgId));
			}
			if (backgroundResId != 0) {
				llContent.setBackgroundDrawable(context.getResources().getDrawable(backgroundResId));
			}
			setListeners();
			if (isSimpleSelection) {
				btRight.setVisibility(View.GONE);
			}
			return view;
		}

		private void setListeners() {
			btLeft.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (clickListener != null) {
						clickListener.onClick(BUTTON_POSITIVE);
					}
					dismissDialog();
				}
			});
			btRight.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dismissDialog();
					if (clickListener != null) {
						clickListener.onClick(BUTTON_NEGATIVE);
					}
				}
			});
		}

		public Builder setCancelable(boolean cancelable) {
			this.cancelable = cancelable;
			return this;
		}

		private void dismissDialog() {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		}

		public Builder setTitle(String title) {
			this.title = title == null ? "" : title;
			return this;
		}
		public Builder setPositiveButtonMsg(String text) {
			this.positiveMsg = text;

			return this;
		}

		public Builder setNegativeButtonMsg(String text) {
			this.negativeMsg=text;
			return this;
		}



		public Builder setMessage(String msg) {
			this.msg = msg == null ? "" : msg;
			return this;
		}

		public Builder setOnClickListener(ClickListener listener) {
			this.clickListener = listener;
			return this;
		}

		/**
		 * 设置图片资源
		 * 
		 * @param resId
		 * @return
		 */
		public Builder setPositiveImgRes(int resId) {
			this.leftImgId = resId;
			return this;
		}

		public Builder setNegativeImgRes(int resId) {
			this.rightImgId = resId;
			return this;
		}

		/**
		 * 设置提示语的对齐方式<br>
		 * 默认：居中
		 * 
		 * @param gravity
		 * @param
		 * @return
		 */
		public Builder setMsgGravity(int gravity) {
			this.msgGravity = gravity;
			return this;
		}

		/**
		 * 设置提示语padding
		 * 
		 * @param paddingLeft
		 * @param paddingTop
		 * @param paddingRight
		 * @param paddingBottom
		 * @return
		 */
		public Builder setMsgPadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
			this.msgPaddingLeft = paddingLeft;
			this.msgPaddingTop = paddingTop;
			this.msgPaddingRight = paddingRight;
			this.msgPaddingBottom = paddingBottom;
			return this;
		}

		/**
		 * 设置提示语的颜色
		 * 
		 * @param resId
		 * @return
		 */
		public Builder setMsgTextColor(int resId) {
			this.resMsgColor = resId;
			return this;
		}

		/**
		 * 设置背景资源
		 * 
		 * @param resId
		 * @return
		 */
		public Builder setBackgroudRes(int resId) {
			this.backgroundResId = resId;
			return this;
		}
	}

	public interface ClickListener {
		/**
		 * 根据tags判断事件类别：[确定] or [取消]
		 * 
		 * @param tags
		 *            {@link HintDialog#BUTTON_POSITIVE} or
		 *            {@link HintDialog#BUTTON_NEGATIVE}
		 */
		void onClick(int tags);
	}
}
