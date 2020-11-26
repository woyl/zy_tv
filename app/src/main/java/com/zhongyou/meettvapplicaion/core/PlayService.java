package com.zhongyou.meettvapplicaion.core;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.widget.LinearLayout;

import java.io.IOException;


/**
 * 播放音乐相关Service服务
 */
public class PlayService extends Service {
	private MediaPlayer mediaPlayer;
	private boolean isLoop = true;
	private Intent mIntent;
	private UpdateProgressThread mUpdateProgressThread;

	/**
	 * 创建Service时执行1次
	 */
	public void onCreate() {
		mediaPlayer = new MediaPlayer();
		//注册mediaPlayer的监听
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			public void onPrepared(MediaPlayer mp) {
				//准备完成  开始播放
				mediaPlayer.start();
				//发送广播 -> 音乐已经开始播放
				mIntent = new Intent(GlobalConsts.ACTION_MUSIC_STARTED);
				//启动更新音乐进度的线程
				sendBroadcast(mIntent);
			}
		});
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				//next() 通知Activity播完了
				mIntent = new Intent(GlobalConsts.ACTION_COMPLETE_MUSIC);
				sendBroadcast(mIntent);


			}
		});
		mUpdateProgressThread = new UpdateProgressThread();
		mUpdateProgressThread.start();

		mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				return true;
			}
		});

	}

	@Override
	public IBinder onBind(Intent intent) {
		return new MusicBinder();
	}


	@Override
	public void onDestroy() {
		mediaPlayer.release();  //释放资源
		isLoop = false;
		super.onDestroy();
	}

	public class MusicBinder extends Binder {

		/**
		 * 播放或暂停
		 */
		/**
		 * 播放或暂停
		 */
	/*	public void startOrPause() {
			if (mediaPlayer.isPlaying()) {
				Intent intent = new Intent(GlobalConsts.ACTION_PAUSE_MUSIC);
				sendBroadcast(intent);
				mediaPlayer.pause();
			} else {
				Intent intent = new Intent(GlobalConsts.ACTION_STATR_MUSIC);
				sendBroadcast(intent);
				mediaPlayer.start();
			}
		}*/
		public void resetMediaPlayer() {
			if (mediaPlayer != null) {
				mediaPlayer.reset();
			}
		}


		/**
		 * 暂停播放
		 */
		public void pause() {
			if (mediaPlayer != null) {
				Intent intent = new Intent(GlobalConsts.ACTION_PAUSE_MUSIC);
				sendBroadcast(intent);
				mediaPlayer.pause();
			}
		}

		/**
		 * 开始播放
		 */
		public void start() {
			if (mediaPlayer != null) {
				Intent intent = new Intent(GlobalConsts.ACTION_STATR_MUSIC);
				sendBroadcast(intent);
				mediaPlayer.start();
			}
		}


		/**
		 * 定位到某个位置 继续播放、暂停
		 *
		 * @param position
		 */
		public void seekTo(int position) {
			mediaPlayer.seekTo(position);
		}

		/**
		 * 供客户端调用的接口方法
		 * 当连接与正在播放的连接相同的时候  就直接播放
		 *
		 * @param url
		 */
		private String url;

		public void playMusic(String url) {

			this.url = url;
			try {
				mediaPlayer.reset();
				mediaPlayer.setDataSource(url);
				mediaPlayer.prepareAsync();
			} catch (IOException e) {
				e.printStackTrace();
				Intent intent = new Intent(GlobalConsts.ACTION_COMPLETE_MUSIC);
				intent.putExtra("isPlaying", mediaPlayer.isPlaying());
				sendBroadcast(intent);
			}
		}

		public void stop() {
			url = "";
			try {
				mediaPlayer.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public String getCurrentUrl() {
			return url;
		}

		public boolean isPlaying() {
			if (mediaPlayer != null) {
				return mediaPlayer.isPlaying();
			}
			return false;
		}

		public long getCurrentDutation() {
			if (mediaPlayer != null) {
				return mediaPlayer.getDuration();
			}
			return -1;
		}

		public long getCurrentPosition() {
			return mediaPlayer.getCurrentPosition();
		}
	}


	/**
	 * 更新进度
	 * 每1S发送一次广播
	 */
	class UpdateProgressThread extends Thread {
		public void run() {
			while (isLoop) {
				try {
					Thread.sleep(1000);
					if (mediaPlayer.isPlaying()) {
						//发送广播
						Intent intent = new Intent(GlobalConsts.ACTION_UPDATE_PROGRESS);
						int current = mediaPlayer.getCurrentPosition();
						int total = mediaPlayer.getDuration();
						intent.putExtra("current", current);
						intent.putExtra("total", total);
						intent.putExtra("isPlaying", mediaPlayer.isPlaying());
						sendBroadcast(intent);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}


}



