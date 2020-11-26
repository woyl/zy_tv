package com.zhongyou.meettvapplicaion;

import android.app.Activity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.ClassicFlattener;
import com.elvishew.xlog.interceptor.BlacklistTagsFilterInterceptor;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.crashreport.CrashReport;
import com.tendcloud.tenddata.TCAgent;
import com.zhongyou.meettvapplicaion.crash.CrashHandler;
import com.zhongyou.meettvapplicaion.entities.GlobalConfigurationInformation;
import com.zhongyou.meettvapplicaion.im.IMManager;
import com.zhongyou.meettvapplicaion.net.ApiClient;
import com.zhongyou.meettvapplicaion.net.OkHttpBaseCallback;
import com.zhongyou.meettvapplicaion.net.OkHttpUtil;
import com.zhongyou.meettvapplicaion.network.AppManager;
import com.zhongyou.meettvapplicaion.persistence.Preferences;

import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import io.agora.openlive.model.WorkerThread;
import io.socket.client.IO;
import io.socket.client.Socket;
import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;
import okhttp3.OkHttpClient;

import static io.rong.imkit.utils.SystemUtils.getCurProcessName;

public class BaseApplication extends MultiDexApplication {

	public static final String TAG = "BaseApplication";

	private static BaseApplication instance;
	private Socket mSocket;


	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(base);

	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
/**
 *
 *
 * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
 * io.rong.push 为融云 push 进程名称，不可修改。
 */


		if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
				"io.rong.push".equals(getCurProcessName(getApplicationContext()))) {
			/**
			 * IMKit SDK调用第一步 初始化
			 */
			//	//8luwapkv846rl
			IMManager.getInstance().init(this,"8brlm7uf8qod3");

		}
		ActivitStats();

//		getHostUrl();

		/*FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
				.showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
				.methodCount(2)         // (Optional) How many method line to show. Default 2
				.methodOffset(5)        // (Optional) Hides internal method calls up to offset. Default 5
				.tag("MyLog")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
				.build();*/
		Logger.addLogAdapter(new AndroidLogAdapter() {
			@Override
			public boolean isLoggable(int priority, @Nullable String tag) {
				return true;
			}
		});

		LogConfiguration config = new LogConfiguration.Builder()
				.logLevel(BuildConfig.DEBUG ? LogLevel.ALL : LogLevel.NONE)
				.tag("meeting")
				.addInterceptor(new BlacklistTagsFilterInterceptor(    // Add blacklist tags filter
						"blacklist1", "blacklist2", "blacklist3"))
				.build();
		Printer androidPrinter = new AndroidPrinter();             // Printer that print the log using android.util.Log
		Printer filePrinter = new FilePrinter                      // Printer that print the log to the file system
				.Builder(new File(Environment.getExternalStorageDirectory(), "中幼在线日志").getPath())       // Specify the path to save log file
				.fileNameGenerator(new DateFileNameGenerator())        // Default: ChangelessFileNameGenerator("log")
				.flattener(new ClassicFlattener())                     // Default: DefaultFlattener
				.build();
		XLog.init(                                                 // Initialize XLog
				config,                                                // Specify the log configuration, if not specified, will use new LogConfiguration.Builder().build()
				androidPrinter,                                        // Specify printers, if no printer is specified, AndroidPrinter(for Android)/ConsolePrinter(for java) will be used.
				filePrinter);
		//内存泄露检测工具,开发中最好开启
//        if (BuildConfig.DEBUG) {
//            if (LeakCanary.isInAnalyzerProcess(this)) {
//                 This process is dedicated to LeakCanary for heap analysis.
//                 You should not init your app in this process.
//                return;
//            }
//            LeakCanary.install(this);
//        }

		//初始化bugly 4390f8350d
		CrashReport.initCrashReport(getApplicationContext(), BuildConfig.BUGLY_APPID, true);

		//初始化TD
		TCAgent.LOG_ON = false;
		// App ID: 在TalkingData创建应用后，进入数据报表页中，在“系统设置”-“编辑应用”页面里查看App ID。
		// 渠道 ID: 是渠道标识符，可通过不同渠道单独追踪数据。
		TCAgent.init(this);
		// 如果已经在AndroidManifest.xml配置了App ID和渠道ID，调用TCAgent.init(this)即可；或与AndroidManifest.xml中的对应参数保持一致。
		TCAgent.setReportUncaughtExceptions(true);


		AutoSizeConfig.getInstance().getUnitsManager()
				.setSupportDP(false)
				.setSupportSP(false)
				.setSupportSubunits(Subunits.PT);
//		initSocket();
		requestGlobalConfig();

		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
		loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
		loggingInterceptor.setColorLevel(Level.INFO);
		builder.addInterceptor(loggingInterceptor);
		//全局的读取超时时间
		builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
		//全局的写入超时时间
		builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
		//全局的连接超时时间
		builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
		//使用sp保持cookie，如果cookie不过期，则一直有效
		builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));
	}



	//监听当前Activit状态
	private void ActivitStats() {
		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			@Override
			public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

			}

			@Override
			public void onActivityStarted(Activity activity) {

			}

			@Override
			public void onActivityResumed(Activity activity) {
				AppManager.getInstance().setCurrentActivity(activity);
			}

			@Override
			public void onActivityPaused(Activity activity) {

			}

			@Override
			public void onActivityStopped(Activity activity) {

			}

			@Override
			public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

			}

			@Override
			public void onActivityDestroyed(Activity activity) {

			}
		});
	}



	/*
	 * 根据多渠道打包 判断是否是平板(触摸屏)
	 * 通过build variants来配置默认debug包的渠道
	 * */
	public static boolean isPadApplication(Context ctx) {
		if (ctx == null || TextUtils.isEmpty("UMENG_CHANNEL")) {
			return false;
		}
		String resultData = null;
		try {
			PackageManager packageManager = ctx.getPackageManager();
			if (packageManager != null) {
				//注意此处为ApplicationInfo，因为友盟设置的meta-data是在application标签中
				ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
				if (applicationInfo != null) {
					if (applicationInfo.metaData != null) {
						//key要与manifest中的配置文件标识一致
						resultData = applicationInfo.metaData.getString("UMENG_CHANNEL");
					}
				}
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		if (resultData != null) {
			if (resultData.equals("pad")) {
				return true;
			}
		}
		return false;
	}


//	private void initSocket() {
//		try {
//			IO.Options options = new IO.Options();
//			options.forceNew = false;
//			options.reconnection = true;
//			options.reconnectionDelay = 1000;
//			options.reconnectionDelayMax = 5000;
//			options.reconnectionAttempts = 10;
//			mSocket = IO.socket(Constant.getWEBSOCKETURL());
//			TCAgent.onEvent(this, "socket", "初始化socket连接");
//			WSService.actionStart(this);
//		} catch (URISyntaxException e) {
//			throw new RuntimeException(e);
//		}
//	}



	public static BaseApplication getInstance() {
		return instance;
	}

	private Activity mCurrentActivity = null;

	public Activity getCurrentActivity() {
		return mCurrentActivity;
	}

	public void setCurrentActivity(Activity mCurrentActivity) {
		this.mCurrentActivity = mCurrentActivity;
//        if(mCurrentActivity!=null && LoginHelper.mIsLogout==true){
//            LoginHelper.mIsLogout = false;
//            LoginHelper.logout(mCurrentActivity);
//        }
	}

	private WorkerThread mWorkerThread;

	public synchronized void initWorkerThread(String appId) {
		if (mWorkerThread == null) {
			mWorkerThread = new WorkerThread(getApplicationContext(), appId);
			mWorkerThread.start();

			mWorkerThread.waitForReady();
		}
	}

	public synchronized WorkerThread getWorkerThread() {
		return mWorkerThread;
	}

	public synchronized void deInitWorkerThread() {
		mWorkerThread.exit();
		try {
			mWorkerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mWorkerThread = null;
	}

	private void requestGlobalConfig() {
		String url = ApiClient.getGlobalConfigurationInformation();

		OkHttpUtil.getInstance().get(url, this, new OkHttpBaseCallback<GlobalConfigurationInformation>() {

			@Override
			public void onSuccess(final GlobalConfigurationInformation result) {
				Preferences.setImgUrl(result.getData().getStaticRes().getImgUrl());
				Preferences.setVideoUrl(result.getData().getStaticRes().getVideoUrl());
				Preferences.setDownloadUrl(result.getData().getStaticRes().getDownloadUrl());
				Preferences.setCooperationUrl(result.getData().getStaticRes().getDownloadUrl());
			}
		});
	}
	private TextureView mLocalPreview;
	private void initLocalPreview() {
		mLocalPreview = new TextureView(this);
	}

	public TextureView localPreview() {
		return mLocalPreview;
	}
}


