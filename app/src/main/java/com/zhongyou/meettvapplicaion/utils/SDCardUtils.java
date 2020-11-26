package com.zhongyou.meettvapplicaion.utils;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;

//SD卡相关的辅助类
public class SDCardUtils {
	public static final String TAG = "SDCardUtils";
	private SDCardUtils() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * 判断SDCard是否可用
	 *
	 * @return
	 */
	public static boolean isSDCardEnable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取SD卡路径
	 *
	 * @return
	 */
	public static String getSDCardPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath()	+ File.separator;
	}


	/**
	 * 获取SD卡容量 单位byte
	 */
	public static long getSDCardSize() {
		if (isSDCardEnable()) {
			StatFs sf = new StatFs(getSDCardPath());
			// 获取空闲的数据块的数量
			// 获取单个数据块的大小（byte）
			long blockSize = sf.getBlockSize();
			long blockCount = sf.getBlockCount();
			Log.d(TAG, "getSDCardSize block大小:" + blockSize + ",block数目:" + blockCount + ",总大小:" + blockSize * blockCount / 1024 / 1024 + "MB");
			return blockSize * blockCount;
		}
		return 0;
	}

	/**
	 * 获取SD卡的剩余容量单位byte
	 */
	public static long getSDCardAvailableSize() {
		if (isSDCardEnable()) {
			StatFs sf = new StatFs(getSDCardPath());
			// 获取空闲的数据块的数量
			// 获取单个数据块的大小（byte）
			long blockSize = sf.getBlockSize();
			long availCount = sf.getAvailableBlocks();
			Log.d(TAG, "getSDCardAvailableSize block数目：:" + availCount + ",剩余空间:" + availCount * blockSize / 1024 / 1024 + "MB");
			return blockSize * availCount;
		}
		return 0;
	}


	/**
	 * 获取系统存储路径
	 */
	public static String getRootDirectoryPath() {
		return Environment.getRootDirectory().getAbsolutePath();
	}

	/**
	 * 获取System容量 单位bytd
	 */
	public static long getSystemSize() {
		StatFs sf = new StatFs(getRootDirectoryPath());
		// 获取空闲的数据块的数量
		// 获取单个数据块的大小（byte）
		long blockSize = sf.getBlockSize();
		long blockCount = sf.getBlockCount();
		Log.d(TAG, "getSystemSize block大小:" + blockSize + ",block数目:" + blockCount + ",总大小:" + blockSize * blockCount / 1024 / 1024 + "MB");
		return blockSize * blockCount;
	}

	/**
	 * 获取System
	 * 的剩余容量 单位byte
	 *
	 * @return
	 */
	public static long getSystemAvailableSize() {
		StatFs sf = new StatFs(getRootDirectoryPath());
		// 获取空闲的数据块的数量
		// 获取单个数据块的大小（byte）
		long blockSize = sf.getBlockSize();
		long availCount = sf.getAvailableBlocks();
		Log.d(TAG, "getSystemAvailableSize block数目：:" + availCount + ",剩余空间:" + availCount * blockSize / 1024 / 1024 + "MB");
		return blockSize * availCount;
	}

	public static long getBToMB(long B){
		return B/1024/1024;
	}

	public static long getSystemSizeMB() {
		return getBToMB(getSystemSize());
	}

	public static long getSystemAvailableSizeMB() {
		return getBToMB(getSystemAvailableSize());
	}

	public static long getSDCardSizeMB() {
		return getBToMB(getSDCardSize());
	}

	public static long getSDCardAvailableSizeMB() {
		return getBToMB(getSDCardAvailableSize());
	}




	/**
	 * 获取指定路径所在空间的剩余可用容量字节数，单位byte
	 *
	 * @param filePath
	 * @return 容量字节 SDCard可用空间，内部存储可用空间
	 */
	public static long getFreeBytes(String filePath) {
		// 如果是sd卡的下的路径，则获取sd卡可用容量
		if (filePath.startsWith(getSDCardPath())) {
			filePath = getSDCardPath();
		} else {// 如果是内部存储的路径，则获取内存存储的可用容量
			filePath = Environment.getDataDirectory().getAbsolutePath();
		}
		StatFs stat = new StatFs(filePath);
		long availableBlocks = (long) stat.getAvailableBlocks() - 4;
		return stat.getBlockSize() * availableBlocks;
	}



}
