package com.zhongyou.meettvapplicaion.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import com.tendcloud.tenddata.TCAgent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class DeviceUtil {
    public static boolean checkApkInstall(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static int getVersionCodeByPackage(Context context, String packageName) {
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packageName, 0);
            return packageinfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getDeviceId(Context context) {
        return TCAgent.getDeviceId(context);
    }

    public static int getVersionCode(Context context) {
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageinfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void launchApp(Context context, String packageName) {
        Intent clearIntent = new Intent("com.yoyo.icontrol.server.clear");
        clearIntent.putExtra("package", packageName);
        context.sendBroadcast(clearIntent);

        Intent playResourceIntent = new Intent("action_playresource_listener");
        playResourceIntent.putExtra("class", "launch");
        context.sendBroadcast(playResourceIntent);
//		PackageManager packageManager = context.getPackageManager();   
//	    Intent intent=new Intent();   
//	    intent = packageManager.getLaunchIntentForPackage(packageName);  
//	    context.startActivity(intent);  
//		PackageInfo pi;
//		try {
//			pi = context.getPackageManager().getPackageInfo(packageName, 0);
//			Intent resolveIntent = new Intent();
//			resolveIntent.setPackage(pi.packageName);
//			PackageManager pManager = context.getPackageManager();
//			List apps = pManager.queryIntentActivities(resolveIntent, 0);
//
//			ResolveInfo ri = (ResolveInfo) apps.iterator().next();
//			if (ri != null) {
//				packageName = ri.activityInfo.packageName;
//				String className = ri.activityInfo.name;
//				Intent intent = new Intent(Intent.ACTION_MAIN);
//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				ComponentName cn = new ComponentName(packageName, className);
//				intent.setComponent(cn);
//				context.startActivity(intent);
//			}
//		} catch (NameNotFoundException e) {
//			Toast.makeText(context, "启动失败", Toast.LENGTH_SHORT).show();
//			e.printStackTrace();
//		}
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            Toast.makeText(context, "解析程序失败", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
//	    resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);  
        resolveIntent.setPackage(packageinfo.packageName);
        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager().queryIntentActivities(resolveIntent, 0);
        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String pkeName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(pkeName, className);
            intent.setComponent(cn);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static void installApk(Context context, String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static int getAvaliableSdCarsSize() {
        int avaliableSize = 0;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
            long count = sf.getAvailableBlocks();
            avaliableSize = (int) ((count * blockSize) / (1024 * 1024));
        }
        return avaliableSize;
    }

    @SuppressLint("NewApi")
    public static float getLeftSdCarsSize() {
        float avaliableSize = 0;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
            long count = sf.getAvailableBlocks();
            avaliableSize = (count * blockSize) / (1024f * 1024f * 1024f);
            DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String p = decimalFormat.format(avaliableSize);
            avaliableSize = Float.parseFloat(p);
        }
        return avaliableSize;
    }

    public static float getSdCardSize() {
        float size = 0f;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
            long count = sf.getBlockCount();
            size = (count * blockSize) / (1024f * 1024f * 1024f);
            DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String p = decimalFormat.format(size);
            size = Float.parseFloat(p);
        }
        return size;
    }

    public static float getUsedSdCardSize() {
        float size = 0f;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
            long count = sf.getBlockCount();
            size = (count * blockSize) / (1024f * 1024f * 1024f) - getLeftSdCarsSize();
            DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String p = decimalFormat.format(size);
            size = Float.parseFloat(p);
        }
        return size;
    }

    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    public static String getDeviceEdition() {
        return Build.VERSION.RELEASE;
    }

    public static String getDeviceAvailMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return (int)(memoryInfo.availMem / 1024 / 1024) + "mb";
    }

    public static String getDeviceTotalMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return (int)(memoryInfo.totalMem / 1024 / 1024) + "mb";
    }

    public static String getDeviceAvailInternalStorage() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return (int) (availableBlocks * blockSize / 1024 / 1024) + "mb";
    }

    public static String getDeviceTotalInternalStorage() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return (int) (totalBlocks * blockSize / 1024 / 1024) + "mb";
    }

    public static String getDeviceMemory(Context context) {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return Formatter.formatFileSize(context, initial_memory);// Byte转换为KB或者MB，内存大小规格化
    }

    public static String getDeviceResmemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        return Formatter.formatFileSize(context, mi.availMem);
    }

    public static String getDeviceDisk() {
        return getSdCardSize() * 1024 + "MB";
    }

    public static String getDeviceResDisk() {
        return getLeftSdCarsSize() * 1024 + "MB";
    }

    /**
     * 获取MetaData中指定的的属性
     *
     * @param context
     * @param metaKey
     * @return
     */
    public static String getMetaData(Context context, String metaKey) {
        String value = null;
        try {
            ApplicationInfo applicationInfo =
                    context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            value = applicationInfo.metaData.getString(metaKey);
        } catch (NameNotFoundException e) {
//			e.printStackTrace();
        }
        return value;
    }

    public static String getCurrentAppVersionName(Context context) {
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageinfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "version_exception";
    }

    public static String getChannelId(Context context) {
        String value = null;
        try {
            ApplicationInfo applicationInfo =
                    context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            value = applicationInfo.metaData.getString("HRZY_CHANNEL");
        } catch (NameNotFoundException e) {
//			e.printStackTrace();
        }
        return value;
    }

    public static String getUserAgent(Context context) {
        String userAgent = "";
        try {
            userAgent = "yoyolauncher_" + getCurrentAppVersionName(context) + "_"
                    + getChannelId(context) + "_"
                    + TCAgent.getDeviceId(context)
                    + "(android_OS_"
                    + Build.VERSION.RELEASE + ";" + Build.MANUFACTURER + "_"
                    + Build.MODEL + ")";
        } catch (Exception e) {
            e.printStackTrace();
            return "yoyolauncher_" + getCurrentAppVersionName(context) + "_"
                    + getChannelId(context) + "_"
                    + TCAgent.getDeviceId(context) + "(android;unknown)";
        }
        return userAgent;
    }

    /**
     * 判断是不是海尔TV
     *
     * @return
     */
    public static boolean isHaierTv() {
        return Build.MANUFACTURER.toLowerCase().contains("haier");
    }

    public static boolean isTianMall() {
        return Build.MANUFACTURER.toLowerCase().contains("tmall");
    }


}
