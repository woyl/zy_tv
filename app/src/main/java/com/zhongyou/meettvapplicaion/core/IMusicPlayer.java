package com.zhongyou.meettvapplicaion.core;

/**
 * @author golangdorid@gmail.com
 * @date 2020/4/8 1:57 PM.
 * @
 */
public interface IMusicPlayer {
	/**
	 * 播放歌曲
	 */
	void play();

	/**
	 * 暂停播放
	 */
	void pause();

	/**
	 * 判断当前是否正在播放
	 *
	 * @return 返回true则表示正在播放，返回false则表示没有在播放
	 */
	boolean isPlaying();

	/**
	 * 播放指定的歌曲
	 *
	 * @param path
	 *            歌曲的路径
	 */
	void onLinePlay(String path);

	/**
	 * 获取当前播放到的位置
	 *
	 * @return 当前播放到的位置，值为播放到的时间的毫秒数
	 */
	int getCurrentPosition();

	/**
	 * 获取当前播放的歌曲的总时长
	 *
	 * @return 当前播放的歌曲的总时长，单位为毫秒
	 */
	int getDuration();

	/***
	 *快进到指定位置播放
	 *
	 *
	 */
	void seekTo(int position);
}
