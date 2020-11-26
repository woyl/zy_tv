package com.zhongyou.meettvapplicaion.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.tendcloud.tenddata.TCAgent;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;

/**
 * Created by ztw on 2016/4/7.
 */
public class AppUtil
{	
	//data list
	private static Queue<String> clearList = new LinkedList<String>();
	private static Queue<String> installList = new LinkedList<String>();
	private static HashMap<String,String> mapPkg = new HashMap<String,String>();
	
	private static String oldPkg = null;
	private static Timer timer = new Timer();
	
	private static String INSTALL_CMD = "pm install -r ";
	private static String UNINSTALL_CMD = "pm uninstall ";
	private static String CHMOD_CMD = "chmod 777 ";
	
	//install error
	private static String INSTALL_ERR_INVALID_URI = "[INSTALL_FAILED_INVALID_URI]";


	public static String getDeviceId(Context context){
		return TCAgent.getDeviceId(context);
	}

	public static String getCurrentAppVersionName(Context context){
		PackageInfo packageinfo = null;
		try {
			packageinfo =context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageinfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return "version_exception";
	}

	public static String getVersion(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			int version = info.versionCode;
			return String.valueOf(version);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String formatAppSize(int size) {
		String result = "";
		int count = 0;
		while (size > 1024) {
			count++;
			size = size / 1024;
		}
		switch (count) {
			case 0:
				result = size + "B";
				break;
			case 1:
				result = size + "KB";
				break;
			case 2:
				result = size + "MB";
				break;
			case 3:
				result = size + "GB";
				break;
			default:
				break;
		}
		return result;
	}

	
	/** 
	 * 判断应用是否正在运行 
	 *  
	 * @param context 
	 * @param packageName 
	 * @return 
	 */  
	public static boolean isRunning(Context context, String packageName) {
	    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
	    for (RunningAppProcessInfo appProcess : list) {
	        String processName = appProcess.processName;
	                
	        if (processName != null && processName.equals(packageName)) {  
	            return true;  
	        }  
	    }  
	    return false;  
	}  
	
    
    /**
     * 删除单个文件
     * @param   sPath    被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        
        return flag;
    }

	public static int getSDKVersion() {
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
		}
		return version;
	}
    
    public static void install(Context context, String path, String pkg) {
    	execInstallCommand(context,INSTALL_CMD + path, path, pkg); 
    }
    
    public static void uninstall(Context context, String pkg) {
    	execUninstallCommand(context, UNINSTALL_CMD + pkg, pkg); 
    }

	public static void installBroadcast(final Context context, final String pkg, final String result, final String info){
		
        new Thread(new Runnable() {
            @Override
            public void run() {
        		
        		Intent intent = new Intent("com.yoyo.icontrol.server.installing");
        		intent.putExtra("package", pkg);
        		intent.putExtra("result", result);
        		intent.putExtra("info", info);
        		
        		context.sendBroadcast(intent);
            }
        }).start();
	}
	
	public static void execInstallCommand(final Context context, final String command, final String path, final String pkg) {
				
		installBroadcast(context,pkg,"-1","");
		
        new Thread(new Runnable() {
            @Override
            public void run() {
            	
        		Process process = null;
        		InputStream instream = null;
        		BufferedReader bufferReader = null;
        		String results = "";

        		int retry = 0;
        		while(true){
            		try {
            			process = Runtime.getRuntime().exec(command);

            			String readline;

            			//获取子进程的输入流
            			instream = process.getInputStream();
            			bufferReader = new BufferedReader(new InputStreamReader(instream, "GBK"));

            			while ((readline = bufferReader.readLine()) != null) {
            				results += readline + "\n"; 
            			}
            			
            			//获取子进程的错误流
            			instream = process.getErrorStream();
            			bufferReader = new BufferedReader(new InputStreamReader(instream, "GBK"));

            			while ((readline = bufferReader.readLine()) != null) {
            				results += readline + "\n"; 
            			}

            			int status = process.waitFor();
            			
            			
            			if(status == 0){
            				;//ToastUtil3.showToastInThead(context, "文件安装成功！" +pkg);
            			}
            			else{
            				;//ToastUtil3.showToastInThead(context, "文件安装失败！" + pkg + "  results:" + results);
            				
            				//安装失败
            				if(++retry <2){
            					execCommand(context,CHMOD_CMD + path); 
            					
            					continue; //重试安装流程
            				}
            				else{//安装失败的时候走一次卸载，确保清理干净
            					execUninstallCommand(context, UNINSTALL_CMD + pkg, pkg); 
            				}
            			}
            			            			
            			if(deleteFile(path)){
        					;//ToastUtil3.showToastInThead(context, "安装包文件删除成功！");
        				}
        				else
        					;//ToastUtil3.showToastInThead(context, "安装包文件删除失败！");
            			
            			installBroadcast(context,pkg, String.valueOf(status),results);
            			
            			Log.i("install","execute command :" + command + ", status : " + status + ", results : " + results);
                		break;
            		} 
            		catch (Exception e) {
            			Log.i("install",e.getMessage());
            			break;
            		}             		           		
        		}            	
            }
        }).start(); 
	}
	
	public static void execUninstallCommand(final Context context, final String command, final String pkg) {
				
        new Thread(new Runnable() {
            @Override
            public void run() {
            	
        		Process process = null;
        		InputStream instream = null;
        		BufferedReader bufferReader = null;
        		String results = "";

        		while(true){
            		try {
            			process = Runtime.getRuntime().exec(command);

            			String readline;

            			//获取子进程的输入流
            			instream = process.getInputStream();
            			bufferReader = new BufferedReader(new InputStreamReader(instream, "GBK"));

            			while ((readline = bufferReader.readLine()) != null) {
            				results += readline + "\n"; 
            			}
            			
            			//获取子进程的错误流
            			instream = process.getErrorStream();
            			bufferReader = new BufferedReader(new InputStreamReader(instream, "GBK"));

            			while ((readline = bufferReader.readLine()) != null) {
            				results += readline + "\n"; 
            			}

            			int status = process.waitFor();            			
            			
            			Log.i("install","execute command :" + command + ", status : " + status + ", results : " + results);
            			
            			if(status == 0)
            				;//ToastUtil3.showToastInThead(context, "卸载成功！" +pkg);
            			else{
            				;//ToastUtil3.showToastInThead(context, "卸载失败！" + pkg + "  results:" + results);            				           			
            			}
            		} 
            		catch (IOException e) {
            			Log.i("install",e.getMessage());
            		} 
            		catch (InterruptedException e) {
            			Log.i("install",e.getMessage());
            		}
            		
            		break;
        		}            	
            }
        }).start(); 
	}
	
	public static void execCommand(final Context context, final String command) {
		
		Process process = null;
		InputStream instream = null;
		BufferedReader bufferReader = null;
		String results = "";

		while(true){
    		try {
    			process = Runtime.getRuntime().exec(command);

    			String readline;

    			//获取子进程的输入流
    			instream = process.getInputStream();
    			bufferReader = new BufferedReader(new InputStreamReader(instream, "GBK"));

    			while ((readline = bufferReader.readLine()) != null) {
    				results += readline + "\n"; 
    			}
    			
    			//获取子进程的错误流
    			instream = process.getErrorStream();
    			bufferReader = new BufferedReader(new InputStreamReader(instream, "GBK"));

    			while ((readline = bufferReader.readLine()) != null) {
    				results += readline + "\n"; 
    			}

    			int status = process.waitFor();    			
    			
    			Log.i("install","execute command :" + command + ", status : " + status + ", results : " + results);
    			
    			if(status == 0)
    				;//ToastUtil3.showToastInThead(context, "操作成功！" +command);
    			else{
    				;//ToastUtil3.showToastInThead(context, "操作失败！" + command + "  results:" + results);
    			}
    		} 
    		catch (IOException e) {
    			Log.i("install",e.getMessage());
    		} 
    		catch (InterruptedException e) {
    			Log.i("install",e.getMessage());
    		}
    		
    		break;
		} 
	}
}
