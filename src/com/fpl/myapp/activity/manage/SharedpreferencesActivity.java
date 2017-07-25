package com.fpl.myapp.activity.manage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.fpl.myapp.activity.MainActivity;
import com.fpl.myapp.activity.SplashScreenActivity;
import com.fpl.myapp.db.DbService;
import com.fpl.myapp.util.Constant;
import com.fpl.myapp.util.HttpCallbackListener;
import com.fpl.myapp.util.HttpUtil;
import com.fpl.myapp.util.NetUtil;
import com.fpl.myapp2.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ww.greendao.dao.Item;

public class SharedpreferencesActivity extends Activity {

	private RadioGroup rgIcOrCode;
	private RadioGroup rgImeiOrMac;
	private EditText etIp;
	private EditText etNumber;
	private SharedPreferences mSharedPreferences;
	private int getStyle;
	private int readStyle = 0;
	private SharedPreferences m1SharedPreferences;
	private String ip;
	private String number;
	private int OnlyNumber;
	private RadioButton rbIC;
	private RadioButton rbCode;
	private RadioButton rbImei;
	private RadioButton rbMac;
	private ImageButton ibQuit;
	private Button btnPassword;
	private EditText etIMEI;
	private static String IMEI;
	private static String ipAddress;
	private static Context context;
	public static Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				NetUtil.showToast(context, "连接服务器异常，密码获取失败");
				break;
			case 2:
				NetUtil.showToast(context, "数据初始化成功");
				break;
			case 3:
				NetUtil.showToast(context, "连接服务器异常，数据初始化失败");
				break;
			case 4:
				NetUtil.showToast(context, "该设备未开放");
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sharedpreferences);
		context = this;

		mSharedPreferences = this.getSharedPreferences("readStyles", Activity.MODE_PRIVATE);
		getStyle = mSharedPreferences.getInt("readStyle", 0);

		m1SharedPreferences = this.getSharedPreferences("ipAddress", Activity.MODE_PRIVATE);

		// SharedPreferences获取保存的上传地址
		ip = m1SharedPreferences.getString("ip", "");
		number = m1SharedPreferences.getString("number", "");
		OnlyNumber = m1SharedPreferences.getInt("macorimei", 0);
		// 获取IMEI码
		IMEI = m1SharedPreferences.getString("IMEI", "0");

		initView();
		setListeners();
	}

	private void isWifiConnected(boolean result) {
		if (true == result) {
			if (!etIp.getText().toString().isEmpty() && !etNumber.getText().toString().isEmpty()) {
				ipAddress = "http://" + etIp.getText().toString().trim() + ":" + etNumber.getText().toString().trim();
				Log.i("ipAddress", ipAddress);
				// try {
				// HttpUtil.getPassword(context, ipAddress);
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				try {
					HttpUtil.getPassword(context, ipAddress, IMEI);
				} catch (Exception e) {
					mHandler.sendEmptyMessage(3);
					e.printStackTrace();
				}
			} else {
				NetUtil.showToast(context, "地址为空，无法连接服务器");
			}

		} else {
			NetUtil.checkNetwork(this);
		}

	}

	private void setListeners() {
		btnPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isWifiConnected(NetUtil.netState(context));
			}
		});

		ibQuit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		rgIcOrCode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int radioButtonId = group.getCheckedRadioButtonId();
				RadioButton rbIcOrCode = (RadioButton) findViewById(radioButtonId);
				if (rbIcOrCode.getText().toString().equals("IC卡")) {
					readStyle = 0;
					// SharedPreferences保存读取方式
					SharedPreferences.Editor editor = mSharedPreferences.edit();
					editor.putInt("readStyle", readStyle);
					editor.commit();
				} else if (rbIcOrCode.getText().toString().equals("条形码")) {
					readStyle = 1;
					// SharedPreferences保存读取方式
					SharedPreferences.Editor editor1 = mSharedPreferences.edit();
					editor1.putInt("readStyle", readStyle);
					editor1.commit();
				}

			}
		});

		rgImeiOrMac.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int radioButtonId = group.getCheckedRadioButtonId();
				RadioButton rbImeiOrMac = (RadioButton) findViewById(radioButtonId);
				if (rbImeiOrMac.getText().toString().equals("IMEI")) {
					SharedPreferences.Editor editor = m1SharedPreferences.edit();
					editor.putInt("macorimei", 0);
					editor.commit();
				} else if (rbImeiOrMac.getText().toString().equals("MAC")) {
					SharedPreferences.Editor editor1 = m1SharedPreferences.edit();
					editor1.putInt("macorimei", 1);
					editor1.commit();
				}
			}
		});
	}

	private void initView() {
		ibQuit = (ImageButton) findViewById(R.id.ib_shared_quit);
		rgIcOrCode = (RadioGroup) findViewById(R.id.radioGroup1);
		rgImeiOrMac = (RadioGroup) findViewById(R.id.radioGroup2);
		rbIC = (RadioButton) findViewById(R.id.radio_ic);
		rbCode = (RadioButton) findViewById(R.id.radio_code);
		rbImei = (RadioButton) findViewById(R.id.radio_imei);
		rbMac = (RadioButton) findViewById(R.id.radio_mac);
		etIp = (EditText) findViewById(R.id.et_shared_ip);
		etNumber = (EditText) findViewById(R.id.et_shared_number);
		btnPassword = (Button) findViewById(R.id.btn_getPassword);
		etIMEI = (EditText) findViewById(R.id.et_IMEI);
		etIMEI.setText(IMEI);
		etIp.setText(ip);
		etNumber.setText(number);

		if (OnlyNumber == 0) {
			rbImei.setChecked(true);
		} else {
			rbMac.setChecked(true);
		}
		if (getStyle == 0) {
			rbIC.setChecked(true);
		} else {
			rbCode.setChecked(true);
		}

	}

	/**
	 * 获取最新项目信息
	 */
	public static void sendItemRequest(final String path) {

		final Map<String, String> params = new HashMap<>();
		params.put("mac", IMEI);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 创建okHttpClient对象
				OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder().connectTimeout(5, TimeUnit.SECONDS)
						.readTimeout(10, TimeUnit.SECONDS).build();
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(path + Constant.ITEM_URL).append("?");
				try {
					if (params != null && params.size() != 0) {
						for (Map.Entry<String, String> entry : params.entrySet()) {
							// 转换成UTF-8
							stringBuilder.append(entry.getKey()).append("=")
									.append(URLEncoder.encode(entry.getValue(), "utf-8"));

							stringBuilder.append("&");
						}
					}
					// 连接signature
					stringBuilder.append("signature=" + HttpUtil.getSignatureVal(params));
					Log.i("---------", stringBuilder.toString());
					// 创建一个Request
					Request request = new Request.Builder().url(stringBuilder.toString()).build();
					// Response response =
					// mOkHttpClient.newCall(request).execute();
					Call call = mOkHttpClient.newCall(request);
					call.enqueue(new Callback() {

						@Override
						public void onResponse(Call arg0, Response response) throws IOException {
							String result = response.body().string();
							Log.i("下载成功", result);
							if (result != null && result.trim().length() != 0) {
								// HttpUtil.getPassword(context, ipAddress);
								mHandler.sendEmptyMessage(2);
								List<Item> itemList = JSON.parseArray(result, Item.class);
								if (DbService.getInstance(context).loadAllItem().size() != itemList.size()) {
									DbService.getInstance(context).saveItemLists(itemList);
									Log.i("success", "保存项目信息成功");
								} else {
									Log.i("fail", "项目信息已存在");
								}
							} else {
								mHandler.sendEmptyMessage(3);
							}
						}

						@Override
						public void onFailure(Call arg0, IOException arg1) {
							mHandler.sendEmptyMessage(3);
						}
					});
				} catch (UnsupportedEncodingException e) {
					mHandler.sendEmptyMessage(3);
				}
			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		String saveIp = etIp.getText().toString();
		String saveNumber = etNumber.getText().toString();
		// SharedPreferences保存输入的上传地址
		SharedPreferences.Editor mEditor = m1SharedPreferences.edit();
		mEditor.putString("ip", saveIp);
		mEditor.putString("number", saveNumber);
		mEditor.commit();
		super.onDestroy();
	}

}
