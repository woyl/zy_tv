package com.zhongyou.meettvapplicaion.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class ApkUtil {
	
	/**
	 * 显示安装APK
	 * @param context
	 * @param path
	 */
	public static void installApk(Context context, String path){
		if(TextUtils.isEmpty(path)){
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://" + path),"application/vnd.android.package-archive");
		context.startActivity(intent);
	}
	
	/**
	 * 检测文件是否存在
	 * @param uri
	 * @return
	 */
	public static boolean isFileExist(String uri){
		if(!TextUtils.isEmpty(uri)){
			File file = new File(uri);
			if(file.isFile() && file.exists()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 检查更新包是否存在<br>
	 * 如果更新包不完整则删除更新包
	 * 
	 * @param info
	 * @return
	 */
	public static boolean isApkExist(Context context, String apkUrl) {
			if (isFileExist(apkUrl)) {
				boolean isApk = checkPackageAvailble(context, apkUrl);
				if (isApk) {
					return true;
				} else {
					File file = new File(apkUrl);
					if (file.isFile()) {
						file.delete();
					}
				}
		}
		return false;
	}
	
	/**
	 * 检测安装包是否可解析
	 * @param context
	 * @param path
	 * @return
	 */
	public static boolean checkPackageAvailble(Context context, String path){
		if(context == null || TextUtils.isEmpty(path)){
			return false;
		}
		PackageManager manager = context.getPackageManager();
		PackageInfo info = manager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
		if(info != null){
			return true;
		}
		return false;
	}
	
	/**
	 * 获取已安装的应用版本
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static int getVersionCodeByPackage(Context context, String packageName){
		if(!checkApkInstall(context, packageName)){
			return -1;
		}
		PackageInfo packageinfo = null;
	    try {  
	        packageinfo =context.getPackageManager().getPackageInfo(packageName, 0);
	        return packageinfo.versionCode;
	    } catch (NameNotFoundException e) {
	        e.printStackTrace();  
	    }  
	    return -1;
	}

	/**
	 * 获取已安装的应用版本
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static String getVersionNameByPackage(Context context, String packageName){
		if(!checkApkInstall(context, packageName)){
			return "";
		}
		PackageInfo packageinfo = null;
		try {
			packageinfo =context.getPackageManager().getPackageInfo(packageName, 0);
			return packageinfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 通过包名启动程序
	 * @param context
	 * @param packageName
	 */
	public static void launcherAppByPackageName(Context context, String packageName){
//		try {
//			Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
//			context.startActivity(intent);
//		} catch (Exception e) {
//			Toast.makeText(context, "程序启动失败", Toast.LENGTH_SHORT).show();
//		}

		// 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
		PackageInfo packageinfo = null;
		try {
			packageinfo =context.getPackageManager().getPackageInfo(packageName, 0);
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
	
	/**
	 * 根据下载地址确定下载文件名
	 * 
	 * @param uri
	 * @return
	 */
	public static String getAddressFileName(String uri) {
		if(!TextUtils.isEmpty(uri)){
			int subIndex = uri.lastIndexOf("/");
			if (subIndex != -1) {
				return uri.substring(subIndex + 1);
			}
		}
		return "unkownFile";
	}
	
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
	
	public static String getSingMD5(Context context, String packageName){
	     String signMd5 = "";
	     try {  
	         Signature[] signs = getSignatures(context, packageName);
	         for (Signature sig : signs){
	             signMd5 = getSignatureString(sig,"MD5");
	         }  
	     }  
	     catch (Exception e){
	         e.printStackTrace();  
	     }  
	     return signMd5;  
	 }
	
	/** 
     * 返回对应包的签名信息 
     * 
     * @param context 
     * @param packageName 
     * 
     * @return 
     */  
    public static Signature[] getSignatures(Context context, String packageName){
        PackageInfo packageInfo = null;
        try  
        {  
            packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            return packageInfo.signatures;  
        }  
        catch (NameNotFoundException e)
        {  
            e.printStackTrace();  
        }  
        return null;  
    }
    
    /** 
     * 获取相应的类型的字符串（把签名的byte[]信息转换成16进制） 
     * 
     * @param sig 
     * @param type 
     * 
     * @return 
     */  
    public static String getSignatureString(Signature sig, String type){
        byte[] hexBytes = sig.toByteArray();  
        String fingerprint = "error!";
        try{  
            MessageDigest digest = MessageDigest.getInstance(type);
            if (digest != null){  
                byte[] digestBytes = digest.digest(hexBytes);  
                StringBuilder sb = new StringBuilder();
                for (byte digestByte : digestBytes){  
                    sb.append((Integer.toHexString((digestByte & 0xFF) | 0x100)).substring(1, 3));
                }  
                fingerprint = sb.toString();  
            }  
        }  
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();  
        }  
  
        return fingerprint;  
    }
	/**
	 * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0
	 * @param version1
	 * @param version2
	 * @return
	 */
	public static int compareVersion(String version1, String version2) throws Exception {
		if (version1 == null || version2 == null) {
			throw new Exception("compareVersion error:illegal params.");
		}
		String[] versionArray1 = version1.split("\\.");//注意此处为正则匹配，不能用"."；
		String[] versionArray2 = version2.split("\\.");
		int idx = 0;
		int minLength = Math.min(versionArray1.length, versionArray2.length);//取最小长度值
		int diff = 0;
		while (idx < minLength
				&& (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0//先比较长度
				&& (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {//再比较字符
			++idx;
		}
		//如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
		diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
		return diff;
	}
}
