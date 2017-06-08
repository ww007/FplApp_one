package com.fpl.myapp.activity.manage;

import com.fpl.myapp.util.NetUtil;
import com.fpl.myapp2.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class SystemManagementActivity extends Activity {

	private ListView lv;
	private String[] data = { "初始化设置", "WLAN", "项目设置", "日期和时间", "存储", "关于本机" };
	private ImageButton ibQuit;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_management);
		SharedPreferences mSharedPreferences = this.getSharedPreferences("password", Activity.MODE_PRIVATE);
		password = mSharedPreferences.getString("password", "fpl");
		initView();
		setListener();
	}

	@Override
	protected void onResume() {
		SharedPreferences mSharedPreferences = this.getSharedPreferences("password", Activity.MODE_PRIVATE);
		password = mSharedPreferences.getString("password", "fpl");
		super.onResume();
	}

	private void initView() {
		ibQuit = (ImageButton) findViewById(R.id.ib_system_quit);
		lv = (ListView) findViewById(R.id.lv_system_manager);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,
				data);
		lv.setAdapter(adapter);
	}

	private void setListener() {
		ibQuit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					startActivity(new Intent(SystemManagementActivity.this, SharedpreferencesActivity.class));
					break;
				case 1:
					if (android.os.Build.VERSION.SDK_INT > 10) {
						startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
					} else {
						startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
					}
					break;
				case 2:
					showAddDialog();
					break;
				case 3:
					startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
					break;
				case 4:
					startActivity(new Intent(android.provider.Settings.ACTION_INTERNAL_STORAGE_SETTINGS));
					break;
				case 5:
					startActivity(new Intent(android.provider.Settings.ACTION_DEVICE_INFO_SETTINGS));
					break;
				default:
					break;
				}

			}
		});

	}

	@SuppressLint("InflateParams")
	private void showAddDialog() {
		Log.i("password=", password);
		LayoutInflater factory = LayoutInflater.from(this);
		View textEntryView = factory.inflate(R.layout.password, null);
		final EditText etNumber = (EditText) textEntryView.findViewById(R.id.et_password);
		AlertDialog.Builder ad1 = new AlertDialog.Builder(SystemManagementActivity.this);
		ad1.setTitle("输入密码:");
		ad1.setView(textEntryView);
		ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int i) {
				if (etNumber.getText().toString().equals(password)) {
					startActivity(new Intent(SystemManagementActivity.this, ProjectManagerActivity.class));
				} else {
					NetUtil.showToast(SystemManagementActivity.this, "密码错误");
				}

			}
		});
		ad1.setNegativeButton("取消", null);
		ad1.show();// 显示对话框

	}

}
