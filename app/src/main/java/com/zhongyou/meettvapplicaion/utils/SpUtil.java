package com.zhongyou.meettvapplicaion.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.zhongyou.meettvapplicaion.BaseApplication;

/**
 * @author luopan@centerm.com
 * @date 2019-11-28 10:04.
 */
public class SpUtil {
	private static final String file_name = "keyCode";
	private static final int sp_mode = Context.MODE_PRIVATE;
	private static Context mcontext = BaseApplication.getInstance();

	public static boolean put(String key, Object value) {
		SharedPreferences preferences = mcontext.getSharedPreferences(file_name, sp_mode);
		SharedPreferences.Editor edit = preferences.edit();

		if (value instanceof String) {
			if (!TextUtils.isEmpty((CharSequence) value)) {
				edit.putString(key, (String) value);
			}
		} else if (value instanceof Boolean) {
			edit.putBoolean(key, (Boolean) value);

		} else if (value instanceof Float) {
			edit.putFloat(key, (Float) value);

		} else if (value instanceof Integer) {
			edit.putInt(key, (Integer) value);
		} else {
			edit.putLong(key, (Long) value);
		}

		boolean commit = edit.commit();
		return commit;
	}


	public static String getString(String key, String defualt) {
		SharedPreferences sharedPreferences = mcontext.getSharedPreferences(file_name, sp_mode);
		return sharedPreferences.getString(key, defualt);
	}

	public static int getInt(String key, int defualt) {
		SharedPreferences sharedPreferences = mcontext.getSharedPreferences(file_name, sp_mode);
		return sharedPreferences.getInt(key, defualt);
	}

	public static boolean getBoolean(String key, boolean defualt) {
		SharedPreferences sharedPreferences = mcontext.getSharedPreferences(file_name, sp_mode);
		return sharedPreferences.getBoolean(key, defualt);
	}

	//销毁
	public static void remove(String key) {
		SharedPreferences preferences = mcontext.getSharedPreferences(file_name, sp_mode);
		SharedPreferences.Editor edit = preferences.edit();
		edit.remove(key);
		edit.commit();

	}

	public static void clear(){

	}

}
