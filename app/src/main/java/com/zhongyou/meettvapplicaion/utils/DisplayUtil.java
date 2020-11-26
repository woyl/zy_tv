package com.zhongyou.meettvapplicaion.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author luopan@centerm.com
 * @date 2019-11-18 10:14.
 */
public class DisplayUtil {

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * @return 屏幕宽度 in pixel
	 */
	public static int getWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		int width = wm.getDefaultDisplay().getWidth();
		return width;
	}

	/**
	 *
	 * @return 屏幕高度 in pixel
	 */
	public static int getHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int height = wm.getDefaultDisplay().getHeight();
		return height;
	}

	/**
	 *
	 * @param activity
	 * @return > 0 success; <= 0 fail
	 */
	public static int getStatusHeight(Activity activity) {
		int statusHeight = 0;
		Rect localRect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
		statusHeight = localRect.top;
		if (0 == statusHeight) {
			Class<?> localClass;
			try {
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = localClass.newInstance();
				int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
				statusHeight = activity.getResources().getDimensionPixelSize(i5);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return statusHeight;
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static Point getScreenMetrics(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int w_screen = dm.widthPixels;
		int h_screen = dm.heightPixels;
		return new Point(w_screen, h_screen);

	}


	/**
	 *华为start
	 */
	//判断是否是华为刘海屏
	public static boolean hasNotchInScreen(Context context)
	{
		boolean ret = false;
		try {
			ClassLoader cl = context.getClassLoader();
			Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
			Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
			ret = (boolean) get.invoke(HwNotchSizeUtil);

		} catch (ClassNotFoundException e)
		{ Log.e("test", "hasNotchInScreen ClassNotFoundException"); }
		catch (NoSuchMethodException e)
		{ Log.e("test", "hasNotchInScreen NoSuchMethodException"); }
		catch (Exception e)
		{ Log.e("test", "hasNotchInScreen Exception"); }
		finally
		{ return ret; }
	}
	//获取华为刘海的高宽
	public static int[] getNotchSize(Context context) {
		int[] ret = new int[]{0, 0};
		try {
			ClassLoader cl = context.getClassLoader();
			Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
			Method get = HwNotchSizeUtil.getMethod("getNotchSize"); ret = (int[]) get.invoke(HwNotchSizeUtil);
		} catch (ClassNotFoundException e) {
			Log.e("test", "getNotchSize ClassNotFoundException"); }
		catch (NoSuchMethodException e)
		{ Log.e("test", "getNotchSize NoSuchMethodException"); }
		catch (Exception e) { Log.e("test", "getNotchSize Exception"); }
		finally { return ret; }
	}
	//全屏显示 暂时不能用
	public static final int FLAG_NOTCH_SUPPORT=0x00010000;
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static void setFullScreenWindowLayoutInDisplayCutout(Window window) {
		if (window == null) { return; }
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		try
		{
			Class layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
			Constructor con=layoutParamsExCls.getConstructor(WindowManager.LayoutParams.class);
			Object layoutParamsExObj=con.newInstance(layoutParams);
			Method method=layoutParamsExCls.getMethod("addHwFlags", int.class);
			method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT);
		}
		catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |InstantiationException | InvocationTargetException e)
		{ Log.e("test", "hw notch screen flag api error"); }
		catch (Exception e) { Log.e("test", "other Exception"); }
	}
	/**
	 *华为end
	 */

	/**
	 *Oppo start
	 */
	public static boolean hasNotchInScreenAtOppo(Context context){
		return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
	}
	/**
	 *Oppo end
	 */

	/**
	 *voio start
	 */
	public static final int NOTCH_IN_SCREEN_VOIO=0x00000020;//是否有凹槽
	public static final int ROUNDED_IN_SCREEN_VOIO=0x00000008;//是否有圆角
	public static boolean hasNotchInScreenAtVoio(Context context){
		boolean ret = false;
		try {
			ClassLoader cl = context.getClassLoader();
			Class FtFeature = cl.loadClass("com.util.FtFeature");
			Method get = FtFeature.getMethod("isFeatureSupport",int.class);
			ret = (boolean) get.invoke(FtFeature,NOTCH_IN_SCREEN_VOIO);

		} catch (ClassNotFoundException e)
		{ Log.e("test", "hasNotchInScreen ClassNotFoundException"); }
		catch (NoSuchMethodException e)
		{ Log.e("test", "hasNotchInScreen NoSuchMethodException"); }
		catch (Exception e)
		{ Log.e("test", "hasNotchInScreen Exception"); }
		finally
		{ return ret; }
	}
	/**
	 *voio end
	 */



	//获取是否存在NavigationBar
	public static boolean checkDeviceHasNavigationBar(Context context) {
		boolean hasNavigationBar = false;
		Resources rs = context.getResources();
		int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
		if (id > 0) {
			hasNavigationBar = rs.getBoolean(id);
		}
		try {
			Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
			Method m = systemPropertiesClass.getMethod("get", String.class);
			String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
			if ("1".equals(navBarOverride)) {
				hasNavigationBar = false;
			} else if ("0".equals(navBarOverride)) {
				hasNavigationBar = true;
			}
		} catch (Exception e) {

		}
		return hasNavigationBar;

	}

	public static int getNavigationBarHeight(Activity mActivity) {
		Resources resources = mActivity.getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height","dimen", "android");
		int height = resources.getDimensionPixelSize(resourceId);
		Log.v("dbw", "Navi height:" + height);
		return height;
	}

	public static int[] getLocation(View v) {
		int[] loc = new int[4];
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		loc[0] = location[0];
		loc[1] = location[1];
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		v.measure(w, h);

		loc[2] = v.getMeasuredWidth();
		loc[3] = v.getMeasuredHeight();

		//base = computeWH();
		return loc;
	}
}
