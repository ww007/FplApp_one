package com.fpl.myapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 自启动（未用）
 * @author ww
 *
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent bootStartIntent = new Intent(context, SplashScreenActivity.class);

		bootStartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		context.startActivity(bootStartIntent);
	}

}
