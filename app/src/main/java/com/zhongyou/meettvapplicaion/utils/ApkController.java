package com.zhongyou.meettvapplicaion.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述: app安装操作
 * 
 */
public class ApkController {

	public static boolean isInstall = false;
	private static Dialog myDialog;

	/**
	 * 描述: 安装
	 */
	@SuppressLint("NewApi")
	public static boolean install(final String apkPath, final Context context) {
		// final View waitDialog = LayoutInflater.from(context).inflate(
		// R.layout.weixin_dialog_view, null);
		AsyncTask<Void, Void, Boolean> installTask = new AsyncTask<Void, Void, Boolean>() {

			protected void onPreExecute() {
				myDialog = MyDialogUtil.showDialog(context);
				myDialog.show();
			};

			@Override
			protected Boolean doInBackground(Void... params) {
				// boolean hasRootPerssion = hasRootPerssion();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}

			protected void onPostExecute(Boolean result) {
				MyDialogUtil.removeDialog(myDialog);
				// 先判断手机是否有root权限
				if (result) {
					// 有root权限，利用静默安装实现
					isInstall = clientInstall(apkPath);
				} else {
					// 没有root权限，利用意图进行安装
					File file = new File(apkPath);
					if (!file.exists())
						isInstall = false;
					installApk(context, apkPath);
					// Intent intent = new Intent();
					// intent.setAction("android.intent.action.VIEW");
					// intent.addCategory("android.intent.category.DEFAULT");
					// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// intent.setDataAndType(Uri.fromFile(file),
					// "application/vnd.android.package-archive");
					// context.startActivity(intent);
					isInstall = true;
				}
			}
		};
		installTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		return isInstall;

	}

	public static void installApk(Context context, String filename) {
		// String url1 = AbFileUtil.getFileDownloadDir(context) + "/" + "files"
		// + filename;
		// String url = url1.replace("files/", "");
		String url = filename;
		Log.e("tag", "安装地址---》" + url);
		try {
			String command = "chmod " + "777" + " " + filename;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
			Log.d("tag", "chmod 777 ok");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + url), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 描述: 卸载
	 */
	public static boolean uninstall(String packageName, Context context,
			Handler handle) {
		// if (hasRootPerssion()) {
		// // 有root权限，利用静默卸载实现
		// return clientUninstall(packageName);
		// } else {
		Uri packageURI = Uri.parse("package:" + packageName);
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(uninstallIntent);
		handle.sendEmptyMessage(0);
		return true;
		// }
	}

	/**
	 * 判断手机是否有root权限
	 */
	private static boolean hasRootPerssion() {
		PrintWriter PrintWriter = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("su");
			PrintWriter = new PrintWriter(process.getOutputStream());
			PrintWriter.flush();
			PrintWriter.close();
			int value = process.waitFor();
			return returnResult(value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
		return false;
	}

	/**
	 * 静默安装
	 */
	private static boolean clientInstall(String apkPath) {
		PrintWriter PrintWriter = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("su");
			PrintWriter = new PrintWriter(process.getOutputStream());
			PrintWriter.println("chmod 777 " + apkPath);
			PrintWriter
					.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
			PrintWriter.println("pm install -r " + apkPath);
			// PrintWriter.println("exit");
			PrintWriter.flush();
			PrintWriter.close();
			int value = process.waitFor();
			return returnResult(value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
		return false;
	}

	/**
	 * 静默卸载
	 */
	private static boolean clientUninstall(String packageName) {
		PrintWriter PrintWriter = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("su");
			PrintWriter = new PrintWriter(process.getOutputStream());
			PrintWriter.println("LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
			PrintWriter.println("pm uninstall " + packageName);
			PrintWriter.flush();
			PrintWriter.close();
			int value = process.waitFor();
			return returnResult(value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
		return false;
	}

	/**
	 * 启动app com.exmaple.client/.MainActivity
	 * com.exmaple.client/com.exmaple.client.MainActivity
	 */
	public static boolean startApp(String packageName, String activityName) {
		boolean isSuccess = false;
		String cmd = "am start -n " + packageName + "/" + activityName + " \n";
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			int value = process.waitFor();
			return returnResult(value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
		return isSuccess;
	}

	public static void startAppByIntent(Context context, String packageName,
			String activityName) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		ComponentName cn = new ComponentName(packageName, activityName);
		intent.setComponent(cn);
		context.startActivity(intent);
	}

	public static void startAppByPackageName(Context context, String packageName) {
		Intent intent = new Intent();
		PackageManager packageManager = context.getPackageManager();
		intent = packageManager.getLaunchIntentForPackage(packageName);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);

	}

	private static boolean returnResult(int value) {
		// 代表成功
		if (value == 0) {
			return true;
		} else if (value == 1) { // 失败
			return false;
		} else { // 未知情况
			return false;
		}
	}

	public static String getUninstallApkPackageName(Context context,
			String apkPath) {
		String packageName = null;
		if (apkPath == null) {
			return packageName;
		}

		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath,
				PackageManager.GET_ACTIVITIES);
		if (info == null) {
			return packageName;
		}

		packageName = info.packageName;
		return packageName;
	}

	public static List<ResolveInfo> findActivitiesForPackage(Context context,
			String packageName) {
		final PackageManager pm = context.getPackageManager();

		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mainIntent.setPackage(packageName);

		try {
			ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
			if (info != null) {
				int i = 0;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		final List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);
		return apps != null ? apps : new ArrayList<ResolveInfo>();
	}

//	public static ArrayList<AppInfomation> getInstalledApps(Context context) {
//		ArrayList<AppInfomation> appList = new ArrayList<AppInfomation>(); // 用来存储获取的应用信息数据
//		PackageManager packageManager = context.getPackageManager();
//		List<PackageInfo> packages = packageManager.getInstalledPackages(0);
//		for (int i = 0; i < packages.size(); i++) {
//			PackageInfo packageInfo = packages.get(i);
//			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
//				// 如果非系统应用，则添加至appList
//				AppInfomation appInfo = new AppInfomation();
//				appInfo.setAppName(packageInfo.applicationInfo.loadLabel(
//						packageManager).toString());
//				appInfo.setPakageName(packageInfo.packageName);
//				appInfo.setVersionName(packageInfo.versionName);
//				appInfo.setVersionCode(packageInfo.versionCode);
//				appInfo.setAppIcon(packageInfo.applicationInfo
//						.loadIcon(packageManager));
//				// Only display the non-system app info
//
//				appList.add(appInfo);
//			}
//		}
//		return appList;
//	}

	/**
	 * 通过应用包名获取本机对应应用的版本名称
	 * 
	 * @param context
	 * @param pagageName
	 * @return
	 */
	public static int getVersionCodeByPagageName(Context context,
			String pagageName) {
		int versionCode = -1;
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> packages = packageManager.getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			if (pagageName.equals(packageInfo.packageName)) {
				versionCode = packageInfo.versionCode;
				break;
			}
			// appInfo.setVersionName(packageInfo.versionName);
			// appInfo.setVersionCode(packageInfo.versionCode);
			// appInfo.setAppIcon(packageInfo.applicationInfo
			// .loadIcon(packageManager));
		}
		return versionCode;

	}
}