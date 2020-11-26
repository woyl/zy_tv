package com.zhongyou.meettvapplicaion.crash;

import android.graphics.Bitmap;
import android.os.Environment;

import com.zhongyou.meettvapplicaion.BaseApplication;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * @author luopan@centerm.com
 * @date 2019-12-03 11:05.
 */
public class FileManage {

	/**
	 * 文件夹路径
	 *
	 * @ClassName: directoryPath
	 * @Description: TODO
	 * @author wuchenghong
	 * @date 2015-2-2 下午1:09:29
	 */
	public class DirectoryPath {

		/** 文件根目录 */
		public static final String FileRoot = "数娱/meetingTV";

		/** 缓存目录 */
		public static final String cache = "cache";

		/** 图片目录 */
		public static final String image = "image";

		/** 语音目录 */
		public static final String voice = "voice";

		/** LOG日志目录 */
		public static final String LOG = "log";


	}

	public static String getAssetsData(String name) {
		StringBuffer sb = new StringBuffer("");
		try {
			InputStream is = BaseApplication.getInstance().getResources().getAssets().open(name);
			InputStreamReader inputStreamReader = null;

			//inputStreamReader = new InputStreamReader(is, HTTP.UTF_8);
			inputStreamReader = new InputStreamReader(is);
			BufferedReader reader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			reader.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 读取SD卡数据
	 *
	 * @Title: getString
	 * @Description: TODO
	 * @param @param inputStream
	 * @param @return
	 * @return String
	 */
	public static String getString(String path) {
		StringBuffer sb = new StringBuffer("");
		try {
			FileInputStream is = new FileInputStream(path);
			InputStreamReader inputStreamReader = null;

			inputStreamReader = new InputStreamReader(is);
			BufferedReader reader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			reader.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static InputStream Bitmap2IS(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
		return sbs;
	}

	/**
	 * 文件拷贝
	 *
	 * @Title: copyFile
	 * @Description: TODO
	 * @param @param oldPath
	 * @param @param newPath
	 * @return void
	 */
	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				fs.flush();
				fs.close();
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 修改文件名
	 *
	 * @param path
	 * @param newName
	 */
	public static void renameFileName(String path, String newName) {

		File f = new File(path);
		String c = f.getParent();
		File mm = new File(c + File.separator + newName);
		if (f.renameTo(mm)) {
			System.out.println("修改成功!");
		} else {
			System.out.println("修改失败");
		}

	}

	/**
	 * 将String数据存为文件
	 */
	public static void fileFromBytes(String content, String path) {
		try {
			PrintWriter pfp = new PrintWriter(path);
			pfp.print(content);
			pfp.close();
		} catch (Exception e) {
		}
	}

	/**
	 * 外置SD卡创建目录与获取SD卡路径
	 *
	 * @Title: getSDPath
	 * @Description: TODO
	 * @param @param sdPath 文件夹
	 * @param @param isDel 是否删除路径
	 * @param @return
	 * @return String
	 */
	public static String getSDPath(String folder) {
		String externalSD = Environment.getExternalStorageDirectory() + "/" + DirectoryPath.FileRoot + "/" + folder + "/";
		File file = new File(externalSD);
		if (!file.exists()) {
			file.mkdirs();
		}
		return externalSD;
	}
}
