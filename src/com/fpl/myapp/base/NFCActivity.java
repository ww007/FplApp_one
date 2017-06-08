package com.fpl.myapp.base;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class NFCActivity extends Activity {

	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	private int readStyle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences mSharedPreferences = getSharedPreferences("readStyles", Activity.MODE_PRIVATE);
		readStyle = mSharedPreferences.getInt("readStyle", 0);
		Log.i("readStyle", readStyle + "");
		if (readStyle == 1) {
			Log.i("readStyle", readStyle + "");
			return;
		} else {
			init();
		}
	}

	public void init() {
		mAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mAdapter == null) {
			Toast.makeText(this, "设备不支持NFC，请选用扫码模式", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		if (mAdapter != null && !mAdapter.isEnabled()) {
			Toast.makeText(this, "请在系统设置中先启用NFC功能！", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		mPendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		// 设置所有MIME基础派遣一个意图过滤器
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		try {
			ndef.addDataType("*/*");
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		mFilters = new IntentFilter[] { ndef };
		// 设置为所有MifareClassic标签技术列表
		mTechLists = new String[][] { new String[] { MifareClassic.class.getName() } };
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (readStyle == 1) {
			return;
		} else {
			// 使用前台发布系统
			mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (readStyle == 1) {
			return;
		} else {
			mAdapter.disableForegroundDispatch(this);
		}
	}

}
