package com.fpl.myapp.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fpl.myapp.activity.MainActivity;
import com.fpl.myapp.activity.SplashScreenActivity;
import com.fpl.myapp2.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * 自动更新帮助类
 * 
 * @author ww
 *
 */
public class UpdateManager {
	private static UpdateManager manager = null;

	private UpdateManager() {
	}

	public static UpdateManager getInstance() {
		manager = new UpdateManager();
		return manager;
	}

	// 获取版本号
	public int getVersion(Context context) {
		int version = 0;
		try {
			version = context.getPackageManager().getPackageInfo("com.fpl.myapp2", 0).versionCode;
		} catch (Exception e) {
			System.out.println("获取版本号异常！");
		}
		return version;
	}

	// 获取版本名
	public String getVersionName(Context context) {
		String versionName = null;
		try {
			versionName = context.getPackageManager().getPackageInfo("com.fpl.myapp2", 0).versionName;
		} catch (Exception e) {
			System.out.println("获取版本名异常！");
		}
		return versionName;
	}

	// 获取服务器版本号
	public String getServerVersion() {
		String serverJson = null;
		byte[] buffer = new byte[128];

		try {
			URL serverURL = new URL("http://192.168.0.150/ver.aspx");
			HttpURLConnection connect = (HttpURLConnection) serverURL.openConnection();
			BufferedInputStream bis = new BufferedInputStream(connect.getInputStream());
			int n = 0;
			while ((n = bis.read(buffer)) != -1) {
				serverJson = new String(buffer);
			}
		} catch (Exception e) {
			System.out.println("获取服务器版本号异常！" + e);
		}

		return serverJson;
	}

	// 比较服务器版本与本地版本弹出对话框
	public boolean compareVersion(Context context) {

		final Context contextTemp = context;

		new Thread() {
			public void run() {
				Looper.prepare();
				String serverJson = manager.getServerVersion();
				if (serverJson == null) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Intent intent = new Intent(contextTemp, MainActivity.class);
					intent.putExtra("1", 1);
					contextTemp.startActivity(intent);
					return;
				}
				// 解析Json数据
				try {
					JSONArray array = new JSONArray(serverJson);
					JSONObject object = array.getJSONObject(0);
					String getServerVersion = object.getString("version");
					String getServerVersionName = object.getString("versionName");

					SplashScreenActivity.serverVersion = Integer.parseInt(getServerVersion);
					SplashScreenActivity.serverVersionName = getServerVersionName;

					if (SplashScreenActivity.version < SplashScreenActivity.serverVersion) {
						// 弹出一个对话框
						AlertDialog.Builder builder = new Builder(contextTemp);
						builder.setTitle("版本更新");
						builder.setMessage("当前版本：" + SplashScreenActivity.versionName + "\n" + "服务器版本："
								+ SplashScreenActivity.serverVersionName);
						builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								// 开启线程下载apk
								new Thread() {
									public void run() {
										Looper.prepare();
										downloadApkFile(contextTemp);
										Looper.loop();
									};
								}.start();
							}
						});
						builder.setNegativeButton("下次再说", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent(contextTemp, MainActivity.class);
								intent.putExtra("1", 1);
								contextTemp.startActivity(intent);
							}
						});
						builder.show();
					} else {
						Log.i("", "当前已经是最新版本");
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Intent intent = new Intent(contextTemp, MainActivity.class);
						intent.putExtra("1", 1);
						contextTemp.startActivity(intent);
						// AlertDialog.Builder builder = new
						// Builder(contextTemp);
						// builder.setTitle("版本信息");
						// builder.setMessage("当前已经是最新版本");
						// builder.setPositiveButton("确定", null);
						// builder.show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					System.out.println("获取服务器版本线程异常！" + e);
					Intent intent = new Intent(contextTemp, MainActivity.class);
					intent.putExtra("1", 1);
					contextTemp.startActivity(intent);
				}

				Looper.loop();
			};

		}.start();
		return false;
	}

	// 下载apk文件
	public void downloadApkFile(Context context) {
		String savePath = Environment.getExternalStorageDirectory() + "/FplApp.apk";
		String serverFilePath = "http://192.168.0.150/FplApp.apk";
		try {
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				URL serverURL = new URL(serverFilePath);
				HttpURLConnection connect = (HttpURLConnection) serverURL.openConnection();
				BufferedInputStream bis = new BufferedInputStream(connect.getInputStream());
				File apkfile = new File(savePath);
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(apkfile));

				int fileLength = connect.getContentLength();
				int downLength = 0;
				int progress = 0;
				int n;
				byte[] buffer = new byte[1024];
				while ((n = bis.read(buffer, 0, buffer.length)) != -1) {
					bos.write(buffer, 0, n);
					downLength += n;
					progress = (int) (((float) downLength / fileLength) * 100);
					Message msg = new Message();
					msg.arg1 = progress;
					SplashScreenActivity.handleUI(progress);
					if (progress == 100) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						String path = Environment.getExternalStorageDirectory() + "/FplApp.apk";
						intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
						context.startActivity(intent);
					}
					// show(context, progress);
					// SplashScreenActivity.handler.sendMessage(msg);
					// System.out.println("发送"+progress);
				}
				bis.close();
				bos.close();
				connect.disconnect();
			}

		} catch (Exception e) {
			System.out.println("下载出错！" + e);
			SplashScreenActivity.mHandle.sendEmptyMessage(2);
			Intent intent = new Intent(context, MainActivity.class);
			intent.putExtra("1", 1);
			context.startActivity(intent);
		}
	}

	public void show(Context context, int progress) {
		final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setSmallIcon(R.drawable.app);
		builder.setContentTitle("下载");
		builder.setContentText(progress + "%");
		final NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(3, builder.build());
		builder.setProgress(100, progress, false);
		if (progress == 100) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			String path = Environment.getExternalStorageDirectory() + "/FplApp.apk";
			intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
			context.startActivity(intent);
		}
	}

	/*
	 * AlertDialog.Builder builder = new Builder(context);
	 * builder.setTitle("下载apk" ) ; builder.setMessage("正在下载" ) ;
	 * builder.setPositiveButton("确定",null); builder.show();
	 */

}
