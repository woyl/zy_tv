package com.zhongyou.meettvapplicaion.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhongyou.meettvapplicaion.R;


public class MyDialogUtil {
	/**
	 * 弹出进度对话框
	 * @param context
	 * @param
	 * @return
	 */
	public static Dialog showDialog(Context context) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.loading_dialog, null);
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.loading_animation);

		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);//
		loadingDialog.setContentView(v, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		return loadingDialog;
	}

	public static Dialog showDialog(Context context,String content) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.loading_dialog, null);
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
		TextView tv_dialog=(TextView) v.findViewById(R.id.tv_dialog);
		tv_dialog.setText(content);
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.loading_animation);

		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);//
		loadingDialog.setContentView(v, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		return loadingDialog;
	}

	public static void removeDialog(Dialog loadingDialog) {
		if (loadingDialog != null) {
			loadingDialog.dismiss();
			loadingDialog = null;
		}
	}

	/**
	 * 弹出系统只有确定按钮的对话框
	 */
	public static void showSureDialog(Context context, String title,
			String message) {
		Builder builder = new Builder(context);
		builder.setMessage(message);
		builder.setTitle(title);

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}
}
