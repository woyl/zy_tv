package com.zhongyou.meettvapplicaion.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCheckUtil {
	// 验证手机
	public static boolean isPhoneNum(String value) {
//		String regex = "^((13[0-9])|(15[0-9])|(17[0-9])|(14[0-9])|(18[0-9])|(19[0-9]))\\d{8}$";
//		Pattern p = Pattern.compile(regex);
//		Matcher m = p.matcher(value);
//		return m.find();

		return true; //TODO 不验证手机
	}
	// 验证email
	public static boolean isEmail(String value) {
		String regex = "^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(value);
		return m.find();
	}

	// 验证身份证
	public static boolean isID(String value) {
		String regex15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
		String regex18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{4}$";
		Pattern p = Pattern.compile(regex15);
		Matcher m = p.matcher(value);
		if (m.find()) {
			return true;
		} else {
			p = Pattern.compile(regex18);
			m = p.matcher(value);
		}
		return m.find();
	}
	
	/**
	 * 检查字符串中是否包含空格
	 * 
	 * @param str
	 * @return
	 */
	public static boolean haveBlankSpace(String str) {

		boolean haveSpace = false;

		for (int i = 0; i < str.length(); i++) {
			if (str.contains(" ")) {
				haveSpace = true;
				break;
			}
		}
		return haveSpace;
	}
	
	/**
	 * 校验输入是否合法 true -合法，false 不合法
	 * 密码可以由数字、字母或符号组成；字母区分大小写；6-20个字符
	 * @return
	 */
	public static boolean isPassword(String str) {

		if (str.length() < 6 || str.length() > 20) {// 请输入6~20位字符密码
//			 MyToast.makeTextAndIcon(getApplicationContext(),
//			 getResources().getString(R.string.lib_password_regular),
//			 R.drawable.login_icon_cry, Toast.LENGTH_SHORT).show();
			return false;
		} else if (haveBlankSpace(str.toString())) {// 密码中有空格
			// MyToast.makeTextAndIcon(getApplicationContext(),
			// getResources().getString(R.string.lib_havespace),
			// R.drawable.login_icon_cry, Toast.LENGTH_SHORT).show();
//			password.setText("");
			return false;
		}
		
		return true;
	}
	
	
	public static String getHidePhoneNum4(String str){
		return str.substring(0, 3)+"****"+str.substring(7, str.length());
	}

	/**
	 * 计算分享内容的字数，一个汉字=两个英文字母，一个中文标点=两个英文标点 注意：该函数的不适用于对单个字符进行计算，因为单个字符四舍五入后都是1
	 *
	 * @param c
	 * @return
	 */
	public static long calculateLength(CharSequence c) {
		double len = 0;
		for (int i = 0; i < c.length(); i++) {
			int tmp = (int) c.charAt(i);
			if (tmp > 0 && tmp < 127) {
				len += 0.5;
			} else {
				len++;
			}
		}
		return Math.round(len);
	}
}
