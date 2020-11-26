package com.zhongyou.meettvapplicaion.business;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zhongyou.meettvapplicaion.BuildConfig;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.entities.Version;
import com.zhongyou.meettvapplicaion.utils.CheckNetWorkStatus;
import com.zhongyou.meettvapplicaion.utils.DeviceUtil;
import com.zhongyou.meettvapplicaion.utils.Logger;
import com.zhongyou.meettvapplicaion.utils.MyDialog;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;
import com.zhongyou.meettvapplicaion.view.IconProgressBar;

import java.io.File;
import java.text.NumberFormat;

public class UpdateActivity extends BasisActivity {

	private IconProgressBar progressBar;
	private TextView infoText;
	private Version version;
//    private TextView imgStep;

	private static final String MIME_TYPE = "application/vnd.android.package-archive";
	private static final int MIN_SIZE = 100;
	private boolean downComplete = false;
	private boolean mUpDateType;

	public class CompleteReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
				long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
				final Uri uriForDownloadedFile = downloadManager.getUriForDownloadedFile(completeDownloadId);
				Log.d("uri", "" + uriForDownloadedFile);

				if (uriForDownloadedFile != null) {
					downComplete = true;
					installApk();
				} else {
					finish();
				}
			}
		}

		//安装apk

	}

	private CompleteReceiver completeReceiver;
	private DownloadManager downloadManager;
	private DownloadChangeObserver downloadObserver;
	private long downloadId = 0;
	public static final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");

	@Override
	public String getStatisticsTag() {
		return "下载更新";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);

		version = getIntent().getParcelableExtra("version");
		mUpDateType = getIntent().getBooleanExtra("isForceUpDate", true);

		infoText = (TextView) findViewById(R.id.download_percent);
		progressBar = (IconProgressBar) findViewById(R.id.download_progress_bar);

		downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(version.getUrl()));
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "GuideTV.apk");
		request.setMimeType(MIME_TYPE);
		request.allowScanningByMediaScanner();

		completeReceiver = new CompleteReceiver();
		registerReceiver(completeReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

		downloadObserver = new DownloadChangeObserver(null);
		getContentResolver().registerContentObserver(CONTENT_URI, true, downloadObserver);

		if (isFileExist("/storage/emulated/0/Download/GuideTV.apk")) {

			deleteFileApk("/storage/emulated/0/Download/GuideTV.apk");
		}
		if (checkStorageIsAvailable()) {

			downloadId = downloadManager.enqueue(request);

		} else {
			Toast.makeText(this, "存储信息获取失败", Toast.LENGTH_SHORT).show();
		}


	}

	private boolean isFileExist(String path) {
		if (path != null) {
			File file = new File(path);
			if (file.isFile() && file.exists()) {
				return true;
			}
		}
		return false;
	}

	private void deleteFileApk(String path) {
		if (path != null) {
			File file = new File(path);
			if (file.isFile() && file.exists()) {
				file.delete();
			}
		}
	}

	protected void installApk() {
//
		String filePath = "/storage/emulated/0/Download/GuideTV.apk";
		installApk(this, filePath);
	}

	/**
	 * 安装apk
	 */
	public void installApk(Context context, String filename) {
		String filePath = filename;
		File apkFile = new File(filePath);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		//判断是否是7.0以上版本
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			// 此处修改 必须得检查版本版本更新是否能正常运行 如非必要 请不要更改
			Uri contentUri = FileProvider.getUriForFile(context, "com.zhongyou.meettvapplicaion.fileProvider", apkFile);
			intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
		} else {
			//7.0以下安装apk
			intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
		}
		context.startActivity(intent);
		finish();

	}

	@Override
	protected void onStart() {
		super.onStart();
		if (dialog == null || (dialog != null && !dialog.isShowing())) {
			if (downComplete) {
				installApk();
			}
		}

	}

	private void showExitDialog(final Version version) {

		if (mUpDateType) {
			ToastUtils.showToast("当前为强制升级 不能退出");
			return;
		}
		dialog = new Dialog(UpdateActivity.this, R.style.MyDialog);
		View view = View.inflate(this, R.layout.dialog_selector, null);
		final TextView titleText = (TextView) view.findViewById(R.id.title);

		if (version.getImportance() == 4) {
			titleText.setText("确定中断升级并退出应用？");
		} else {
			titleText.setText("确定中断升级？");
		}

		Button okBtn = (Button) view.findViewById(R.id.left);
		okBtn.setText("取消");
		okBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//                if (version.getImportance() == 4) {
//                    finish();
//                } else {
//                    if (Preferences.isLogin()) {
//                        startActivity(new Intent(UpdateActivity.this, HomeActivity.class));
//                    } else {
//                        startActivity(new Intent(UpdateActivity.this, WXEntryActivity.class));
//                    }
//                }
//                downloadManager.remove(downloadId);

				if (downComplete) {
					installApk();
				}
				dialog.dismiss();
			}
		});

		Button cancelBtn = (Button) view.findViewById(R.id.right);
		cancelBtn.setText("确定");
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//                dialog.dismiss();
				if (!mUpDateType) {
					setResult(0x002);//退出
				}
				finish();
				dialog.dismiss();
			}
		});
		dialog.setCancelable(false);
		dialog.setContentView(view);
		dialog.show();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.height = (int) getResources().getDimension(R.dimen.my_px_480);
		params.width = (int) getResources().getDimension(R.dimen.my_px_740);
		dialog.getWindow().setAttributes(params);
	}

	private boolean checkStorageIsAvailable() {
		String storage = DeviceUtil.getDeviceAvailInternalStorage();
		if (!TextUtils.isEmpty(storage)) {
			String size = storage.replace("mb", "");
			try {
				int available = Integer.parseInt(size);
				if (available < MIN_SIZE) {
					StringBuilder sb = new StringBuilder("内部存储可用:" + storage);
					sb.append("，下载和运行需要至少" + MIN_SIZE + "M存储空间，请先清理再启动本应用");
					Log.i(TAG, sb.toString());
					MyDialog.Builder builder = new MyDialog.Builder(this);
					builder.setMessage(getString(R.string.app_storage_show));
					builder.setOnClickListener(new MyDialog.ClickListener() {
						@Override
						public void onClick(int tags) {
							switch (tags) {
								case MyDialog.BUTTON_POSITIVE:
									finish();
									break;
							}
						}
					});
					builder.create().show();
					return false;
				} else {
					return true;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	private MyDialog mNetworkDialog;

	private void showNetworkDialog() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				MyDialog.Builder builder = new MyDialog.Builder(UpdateActivity.this);
				builder.setMessage("未检测到网络连接，请先设置网络");
				builder.setOnClickListener(new MyDialog.ClickListener() {
					@Override
					public void onClick(int tags) {
						switch (tags) {
							case MyDialog.BUTTON_POSITIVE:
								dismissNetworkDialog();
								Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
								startActivity(wifiSettingsIntent);
								finish();
								break;
						}
					}
				});
				mNetworkDialog = builder.create();
				mNetworkDialog.show();
			}
		});

	}

	private void dismissNetworkDialog() {
		if (mNetworkDialog != null) {
			mNetworkDialog.dismiss();
			mNetworkDialog = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(completeReceiver);
		getContentResolver().unregisterContentObserver(downloadObserver);
	}

	class DownloadChangeObserver extends ContentObserver {

		/**
		 * Creates a content observer.
		 *
		 * @param handler The handler to run {@link #onChange} on, or null if none.
		 */
		public DownloadChangeObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			queryDownloadStatus();
		}

	}

	private void queryDownloadStatus() {
		DownloadManager.Query query = new DownloadManager.Query();
		query.setFilterById(downloadId);
		Cursor cursor = null;
		try {
			cursor = downloadManager.query(query);
			if (cursor != null && cursor.moveToFirst()) {
				int statusIdx = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
				int reasonIdx = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
				int fileSizeIdx = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
				int bytesDLIdx = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);

				int status = cursor.getInt(statusIdx);
				final int fileSize = cursor.getInt(fileSizeIdx);
				final int bytesDL = cursor.getInt(bytesDLIdx);
				int reason = cursor.getInt(reasonIdx);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progressBar.setMax(fileSize);
						progressBar.setProgress(bytesDL);
						infoText.setText(percent(bytesDL, fileSize));
					}
				});

				switch (status) {
					case DownloadManager.STATUS_PENDING:
						Logger.i(TAG, "准备下载");
					case DownloadManager.STATUS_RUNNING:
						Logger.i(TAG, "正在下载");
						break;
					case DownloadManager.STATUS_PAUSED:
						Logger.i(TAG, "暂停下载 " + reason);
					case DownloadManager.STATUS_SUCCESSFUL:
						Logger.i(TAG, "下载成功");
						break;
					case DownloadManager.STATUS_FAILED:
						Logger.i(TAG, "下载失败 " + reason);
						downloadManager.remove(downloadId);
						showNetworkDialog();
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

	}

	public String percent(int p1, int p2) {
		String str;
		double p3 = (double) p1 / (double) p2;
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMinimumFractionDigits(2);
		str = nf.format(p3);
		return str;
	}

	@Override
	public void onBackPressed() {
		showExitDialog(version);
	}

	private Dialog dialog;

}
