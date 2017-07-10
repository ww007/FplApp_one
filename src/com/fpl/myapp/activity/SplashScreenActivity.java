package com.fpl.myapp.activity;

import com.fpl.myapp2.R;

import com.fpl.myapp.db.DbService;
import com.fpl.myapp.util.HttpUtil;
import com.fpl.myapp.util.NetUtil;
import com.fpl.myapp.util.UpdateManager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

/**
 * 开机引导界面
 *
 */
public class SplashScreenActivity extends Activity {

	public static Context context;
	public static String versionName, serverVersionName, downloadResult;
	public static int version, serverVersion;
	private UpdateManager manager = UpdateManager.getInstance();
	private int SPLASH_TIME_OUT = 2000;
	public static ProgressBar pbSplash;
	public static Activity mActivity = null;
	private int REQUEST_PHONE_STATE = 0;
	private SharedPreferences mSharedPreferences;
	public String IMEI;
	public static Handler mHandle = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				NetUtil.showToast(context, "网络异常，数据下载失败");
				break;
			case 2:
				NetUtil.showToast(context, "无更新文件，下载失败");
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		context = this;
		mActivity = this;
		version = manager.getVersion(context);
		versionName = manager.getVersionName(context);
		if (ActivityCompat.checkSelfPermission(this,
				Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_PHONE_STATE },
					REQUEST_PHONE_STATE);
		} else {
			TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			IMEI = TelephonyMgr.getDeviceId();
		}

		Log.i("IMEI=", IMEI + "");
		mSharedPreferences = this.getSharedPreferences("ipAddress", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putString("IMEI", IMEI);
		editor.commit();

		pbSplash = (ProgressBar) findViewById(R.id.pb_splash);
		// 调用NetUtil中的网络判断方法
		boolean result = NetUtil.netState(context);
		isWifiConnected(result);

	}

	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == REQUEST_PHONE_STATE && grantResults.length == 1
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			IMEI = TelephonyMgr.getDeviceId();
		}
	}

	private void isWifiConnected(boolean result) {
		if (true == result) {
			// manager.compareVersion(context);
			handlePost(2000);
		} else {
			handlePost(2000);
		}

	}

	public static void handleUI(final int progress) {
		mHandle.post(new Runnable() {
			@Override
			public void run() {
				pbSplash.setVisibility(View.VISIBLE);
				pbSplash.setProgress(progress);
			}
		});
	}

	private void handlePost(int time) {
		mHandle.postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
				finish();
			}
		}, time);
	}

}
