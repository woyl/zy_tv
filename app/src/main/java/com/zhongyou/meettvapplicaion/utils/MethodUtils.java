package com.zhongyou.meettvapplicaion.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.UUID;

/**
 * @author liwei
 * @desc 公共方法
 */
@SuppressLint("SimpleDateFormat")
public class MethodUtils {
//	/**
//	 * 返回状态对应中文
//	 * @param str 状态代码
//	 * @return 对应中文
//	 */
//	public static String getStatus(String str) {
//		String return_str = Constant.onLineLiveNull;
//		int status = -1;
//		try {
//			status = Integer.parseInt(str);
//		} catch (NumberFormatException e) {
//			e.printStackTrace();
//		}
//
//		switch (status) {
//		case 0:
//			return_str = Constant.onLineLive0;
//			break;
//		case 1:
//			return_str = Constant.onLineLive1;
//			break;
//		case 2:
//			return_str = Constant.onLineLive2;
//			break;
//		case 3:
//			return_str = Constant.onLineLive3;
//			break;
//		case 4:
//			return_str = Constant.onLineLive4;
//			break;
//		case 99:
//			return_str = Constant.onLineLive99;
//			break;
//		default:
//			break;
//		}
//
//		return return_str;
//	}
	
	/**
	 * 获取当前时间
	 * @param datebuf  时间格式
	 * @return
	 */
	public static String getCurrentTime(String datebuf) {
		String str = "1970-01-01 00:00:00";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(datebuf);
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
			str = formatter.format(curDate);
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return str;
		}
	}
	
	/**
	 * 把秒转换为时分秒
	 * @param estimateDuration  :秒数
	 * @return 时分秒
	 */
	public static String transtimetostr(String estimateDuration) {
		int time = 0;
		try {
			time = Integer.parseInt(estimateDuration);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		int i = time / 1000;
		int hour = i / (60 * 60);
		int minute = i / 60 % 60;
		int second = i % 60;

		StringBuilder sb = new StringBuilder();
		sb.append(hour >= 10 ? hour : "0" + hour);
		sb.append(":");
		sb.append(minute >= 10 ? minute : "0" + minute);
		sb.append(":");
		sb.append(second >= 10 ? second : "0" + second);

		return sb.toString();
	}
	
	/**
	 * 把秒转换为时分秒
	 * @param estimateDuration :秒数
	 * @return 时分秒
	 */
	public static String transtimetostr(int estimateDuration) {
		int time = estimateDuration;

		int i = time / 1000;
		int hour = i / (60 * 60);
		int minute = i / 60 % 60;
		int second = i % 60;

		StringBuilder sb = new StringBuilder();
		sb.append(hour >= 10 ? hour : "0" + hour);
		sb.append(":");
		sb.append(minute >= 10 ? minute : "0" + minute);
		sb.append(":");
		sb.append(second >= 10 ? second : "0" + second);

		return sb.toString();
	}
	
	/**
	 * 生成二维码
	 * @param url  :字符串
	 * @param QR_WIDTH  :宽
	 * @param QR_HEIGHT :高
	 * @return:图像
	 */
	public static Bitmap createQRImage(String url, int QR_WIDTH, int QR_HEIGHT) {
		try {
			// 判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1) {
				return null;
			}
			Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			hints.put(EncodeHintType.MARGIN, "2");

			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			return bitmap;
			// //显示到一个ImageView上面
			// sweepIV.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 生成UUID
	 */
	public static String getUUID() {
		try {
			UUID uuid = UUID.randomUUID();
			return uuid.toString().replace("-", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 视频聊天：haierpackagename暂时写死
	 * @param num：要拨打的号码。是一个8位的号 
	 */
	public static void Call(Context context, String num) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("tvvideochat://preview?cmd=call&number=" + num
							+ "&from=haierpackagename"));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}