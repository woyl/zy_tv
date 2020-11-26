package com.zhongyou.meettvapplicaion.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhongyou.meettvapplicaion.R;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZUtils;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

/**
 * @author luopan@centerm.com
 * @date 2020-02-18 20:13.
 */
public class PreviewPlayer extends JzvdStd {
	private String TAG = "PreviewPlayer";

	public PreviewPlayer(Context context) {
		super(context);
	}

	public PreviewPlayer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void init(Context context) {
		super.init(context);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		int i = v.getId();
		if (i == cn.jzvd.R.id.fullscreen) {
			Log.i(TAG, "onClick: fullscreen button");
		} else if (i == R.id.start) {
			if (state != STATE_PAUSE) {
				Log.e(TAG, "onClick: 当前是播放状态-----");
				if (mOnVideoPlayChangeListener != null) {
					mOnVideoPlayChangeListener.play();
				}
			} else {
				Log.e(TAG, "onClick: 当前是暂停状态------");
				if (mOnVideoPlayChangeListener!=null){
					mOnVideoPlayChangeListener.pause();
				}

			}
		}
	}


	public int getCurrentState(){
		return  state;
	}

	public interface OnVideoPlayChangeListener {
		void play();

		void pause();

		void complete();
	}

	private OnVideoPlayChangeListener mOnVideoPlayChangeListener;

	public void setOnVideoPlayChangeListener(OnVideoPlayChangeListener videoPlayChangeListener) {
		this.mOnVideoPlayChangeListener = videoPlayChangeListener;
	}

	public SeekBar getSeekBar() {
		return findViewById(R.id.bottom_seek_progress);
	}

	public View getCurrentTimeView() {
		return findViewById(R.id.current);
	}

	public View getTotalTimeView() {
		return findViewById(R.id.total);
	}

	public View getFullScreenView() {
		return findViewById(R.id.fullscreen);
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		super.onTouch(v, event);
		int id = v.getId();
		if (id == cn.jzvd.R.id.surface_container) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					if (mChangePosition) {
						Log.i(TAG, "Touch screen seek position");
					}
					if (mChangeVolume) {
						Log.i(TAG, "Touch screen change volume");
					}
					break;
			}
		}

		return false;
	}

	@Override
	public int getLayoutId() {
		return R.layout.jz_layout_std;
	}

	@Override
	public void startVideo() {
		super.startVideo();
		Log.i(TAG, "startVideo");
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		super.onStopTrackingTouch(seekBar);
		Log.i(TAG, "Seek position ");
	}

	@Override
	public void gotoScreenFullscreen() {
		super.gotoScreenFullscreen();
		Log.i(TAG, "goto Fullscreen");
	}

	@Override
	public void gotoScreenNormal() {
		super.gotoScreenNormal();
		Log.i(TAG, "quit Fullscreen");
	}

	@Override
	public void autoFullscreen(float x) {
		super.autoFullscreen(x);
		Log.i(TAG, "auto Fullscreen");
	}

	@Override
	public void onClickUiToggle() {
		super.onClickUiToggle();
		Log.i(TAG, "click blank");
	}

	//onState 代表了播放器引擎的回调，播放视频各个过程的状态的回调
	@Override
	public void onStateNormal() {
		super.onStateNormal();
	}

	@Override
	public void onStatePreparing() {
		super.onStatePreparing();
	}

	@Override
	public void onStatePlaying() {
		super.onStatePlaying();
	}

	@Override
	public void onStatePause() {
		super.onStatePause();
	}

	@Override
	public void onStateError() {
		super.onStateError();
	}

	@Override
	public void onStateAutoComplete() {
		super.onStateAutoComplete();

		if (mOnVideoPlayChangeListener!=null){
			mOnVideoPlayChangeListener.complete();
		}

	}

	@Override
	public void showWifiDialog() {
		WIFI_TIP_DIALOG_SHOWED = true;
		if (state == STATE_PAUSE) {
			startButton.performClick();
		} else {
			startVideo();
		}
	}

	public void pauseVideo() {
		if (mediaInterface != null) {
			if (state == STATE_PLAYING) {
				mediaInterface.pause();
				onStatePause();
			}
		} else {
			Log.e(TAG, "pauseVideo: mediaInterface==nul");
		}
	}

	//changeUiTo 真能能修改ui的方法
	@Override
	public void changeUiToNormal() {
		super.changeUiToNormal();
	}

	@Override
	public void changeUiToPreparing() {
		super.changeUiToPreparing();
	}

	@Override
	public void changeUiToPlayingShow() {
		super.changeUiToPlayingShow();
	}

	@Override
	public void changeUiToPlayingClear() {
		super.changeUiToPlayingClear();
	}

	@Override
	public void changeUiToPauseShow() {
		super.changeUiToPauseShow();
	}

	@Override
	public void changeUiToPauseClear() {
		super.changeUiToPauseClear();
	}

	@Override
	public void changeUiToComplete() {
		super.changeUiToComplete();
	}

	@Override
	public void changeUiToError() {
		super.changeUiToError();
	}

	@Override
	public void onInfo(int what, int extra) {
		super.onInfo(what, extra);
	}

	@Override
	public void onError(int what, int extra) {
		super.onError(what, extra);
	}

}