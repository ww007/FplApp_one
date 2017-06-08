package com.fpl.myapp.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import com.fpl.myapp2.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 判断网络连接帮助类
 * 
 * @author ww
 *
 */
public class NetUtil {
	/**
	 * 根据Wifi信息获取本地Mac
	 * 
	 * @param context
	 * @return
	 */
	public static String getLocalMacAddressFromWifiInfo(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	public static String getIMEI(Context context) {
		// 获取Android机IMEI号
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String MACID = tm.getDeviceId();
		return MACID;
	}

	public static String getLocalMacAddress() {
		String macSerial = null;
		String str = "";
		try {
			Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			for (; null != str;) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空格
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		return macSerial;
	}

	/**
	 * 判断网络情况
	 * 
	 * @param context
	 *            上下文
	 * @return false 表示没有网络 true 表示有网络
	 */
	public static boolean isNetworkAvalible(Context context) {
		// 获得网络状态管理器
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
		if (activeNetwork != null) { // connected to the internet
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
				// connected to wifi
			} else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
				// connected to the mobile provider's data plan
			}
			return true;
		} else {
			// not connected to the internet
			return false;
		}
	}

	/**
	 * 如果没有网络，则弹出网络设置对话框
	 * 
	 * @param activity
	 */
	public static void checkNetwork(final Activity activity) {
		if (!NetUtil.isNetworkAvalible(activity)) {
			TextView msg = new TextView(activity);
			msg.setText("当前没有可以使用的网络，请设置网络！");
			msg.setTextSize(18);
			new AlertDialog.Builder(activity).setIcon(R.drawable.super_tip).setTitle("网络状态提示").setView(msg)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int whichButton) {
							// 跳转到设置界面
							if (android.os.Build.VERSION.SDK_INT > 10) {
								activity.startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
							} else {
								activity.startActivityForResult(new Intent(Settings.ACTION_WIRELESS_SETTINGS), 0);
							}

						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					}).create().show();
		}
		return;
	}

	/**
	 * 判断网络是否连接
	 **/
	public static boolean netState(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 获取代表联网状态的NetWorkInfo对象
		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		// 获取当前的网络连接是否可用
		boolean available = false;
		try {
			available = networkInfo.isAvailable();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (available) {
			Log.i("通知", "当前的网络连接可用");
			return true;
		} else {
			Log.i("通知", "当前的网络连接可用");
			return false;
		}
	}

	private static Toast toast;

	public static void showToast(Context context, String content) {
		if (toast == null) {
			toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
		} else {
			toast.setText(content);
		}
		toast.show();
	}
}
