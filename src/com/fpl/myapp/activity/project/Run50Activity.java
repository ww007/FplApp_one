package com.fpl.myapp.activity.project;

import org.apache.log4j.Logger;

import com.fpl.myapp2.R;
import com.fpl.myapp.activity.CaptureActivity;
import com.fpl.myapp.base.NFCActivity;
import com.fpl.myapp.util.Constant;
import com.fpl.myapp.util.NetUtil;
import com.wnb.android.nfc.dataobject.entity.IC_ItemResult;
import com.wnb.android.nfc.dataobject.entity.Student;
import com.wnb.android.nfc.dataobject.service.IItemService;
import com.wnb.android.nfc.dataobject.service.impl.NFCItemServiceImpl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Run50Activity extends NFCActivity {

	private TextView tvTitle;
	private Button btnStart;
	private Button btnQuit;
	private Student student;
	private String sex;

	private Logger log = Logger.getLogger(Run50Activity.class);
	private Button btnScan;
	private SharedPreferences mSharedPreferences;
	private int readStyle;
	private Context context;
	private TextView tv;
	private IC_ItemResult item;
	public static Activity mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run);
		context = this;
		mActivity = this;

		mSharedPreferences = getSharedPreferences("readStyles", Activity.MODE_PRIVATE);
		readStyle = mSharedPreferences.getInt("readStyle", 0);

		initView();
		setListeners();
	}

	@Override
	public void onNewIntent(Intent intent) {
		if (readStyle == 0) {
			readCard(intent);
		} else {
			NetUtil.showToast(context, "当前选择非IC卡状态，请设置");
		}
	}

	/**
	 * 读卡
	 */
	private void readCard(Intent intent) {
		try {
			IItemService itemService = new NFCItemServiceImpl(intent);
			student = itemService.IC_ReadStuInfo();
			log.info("50米跑读卡=>" + student.toString());
			item = itemService.IC_ReadItemResult(Constant.RUN50);
			int itemResult;

			if (item.getResult()[0].getResultVal() == 0) {
				itemResult = 0;
			} else {
				itemResult = item.getResult()[0].getResultVal();
			}

			if (1 == student.getSex()) {
				sex = "男";
			} else {
				sex = "女";
			}
			Log.i("50米跑读卡", "" + itemResult);
			if (student.getStuCode() != null) {
				Intent intent2 = new Intent(Run50Activity.this, RunGradeInputActivity.class);
				intent2.putExtra("number", student.getStuCode());
				intent2.putExtra("name", student.getStuName());
				intent2.putExtra("sex", sex);
				intent2.putExtra("grade", itemResult);
				intent2.putExtra("title", "50米跑");
				startActivity(intent2);
			} else {
				Toast.makeText(this, "此卡无效", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			log.error("50米跑读卡失败");
			e.printStackTrace();
		}

	}

	private void initView() {
		btnScan = (Button) findViewById(R.id.btn_run_scanCode);
		tvTitle = (TextView) findViewById(R.id.tv_title_run);
		tv = (TextView) findViewById(R.id.tv_run);
		tvTitle.setText("50米跑");
		btnStart = (Button) findViewById(R.id.btn_start);
		btnQuit = (Button) findViewById(R.id.btn_quit);

		if (readStyle == 0) {
			btnScan.setVisibility(View.GONE);
		} else {
			tv.setVisibility(View.VISIBLE);
			tv.setText("请扫码");
			btnStart.setText("扫码");
			// btnScan.setVisibility(View.VISIBLE);
		}
	}

	private void setListeners() {
		btnScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent1 = new Intent(Run50Activity.this, CaptureActivity.class);
				intent1.putExtra("className", Constant.RUN50 + "");
				startActivity(intent1);
			}
		});
		btnStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (readStyle == 0) {
					Intent intent = new Intent(Run50Activity.this, RunMeteringActivity.class);
					intent.putExtra("title", tvTitle.getText().toString());
					startActivity(intent);
				} else {
					// NetUtil.showToast(context, "当前为扫码模式");
					Intent intent1 = new Intent(Run50Activity.this, CaptureActivity.class);
					intent1.putExtra("className", Constant.RUN50 + "");
					startActivity(intent1);
				}
			}
		});

		btnQuit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			Intent intent = new Intent(Run50Activity.this, RunMeteringActivity.class);
			intent.putExtra("title", tvTitle.getText().toString());
			startActivity(intent);
			return true;
		case 135:
			Intent intent1 = new Intent(Run50Activity.this, CaptureActivity.class);
			intent1.putExtra("className", Constant.RUN50 + "");
			startActivity(intent1);
			finish();
			return true;
		default:
			break;
		}

		return super.onKeyDown(keyCode, event);

	}

}
