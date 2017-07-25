package com.fpl.myapp.activity.project;

import java.util.ArrayList;

import com.fpl.myapp2.R;
import com.fpl.myapp.adapter.TimeAdapter;
import com.fpl.myapp.entity.RunGrade;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RunMeteringActivity extends ListActivity implements OnClickListener {
	private Button btnMetering;
	private Button btnOver;
	private Button btnReset;
	private ArrayList<String> list = new ArrayList<String>();
	private ArrayList<RunGrade> grades = new ArrayList<RunGrade>();
	private TimeAdapter adapter;
	private long timeUsedInsec;

	private static final int TICK_WHAT = 2;

	@SuppressLint("HandlerLeak")
	private Handler uiHandle = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case TICK_WHAT:
				updateClockUI();
				uiHandle.sendEmptyMessage(TICK_WHAT);
				break;
			default:
				break;
			}
		}
	};
	private LinearLayout llTitle;
	private String title;
	private TextView tvTitle;
	private TextView tvTime;
	private long startTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_run_metering);
		// 记录开始的时间ms数
		startTime = System.currentTimeMillis();
		Intent intent = getIntent();
		title = intent.getStringExtra("title");

		initView();
		setListener();
		startTime();
	}

	private void initView() {
		tvTime = (TextView) findViewById(R.id.tv_Metering_time);
		tvTitle = (TextView) findViewById(R.id.tv_title_runMetering);
		btnMetering = (Button) findViewById(R.id.btn_metering);
		btnOver = (Button) findViewById(R.id.btn_over);
		btnReset = (Button) findViewById(R.id.btn_reset);
		llTitle = (LinearLayout) findViewById(R.id.ll_title_metering);

		tvTitle.setText(title);

		adapter = new TimeAdapter(this, list);
		this.setListAdapter(adapter);
	}

	private void setListener() {
		btnMetering.setOnClickListener(this);
		btnOver.setOnClickListener(this);
		btnReset.setOnClickListener(this);
	}

	int i = 1;
	private RunGrade grade;
	private Bundle bd;
	private int min;
	private int sec;
	private int longmill;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_metering:
			metering();
			break;
		case R.id.btn_over:
			over();
			break;
		case R.id.btn_reset:
			reset();
			break;

		default:
			break;
		}

	}

	/**
	 * 重置
	 */
	private void reset() {
		new AlertDialog.Builder(this).setTitle("确认").setMessage("将退出当前计时界面，是否重置？")
				.setPositiveButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).setNegativeButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						uiHandle.removeMessages(TICK_WHAT);
						finish();
					}
				}).show();
	}

	/**
	 * 退出计时
	 */
	private void over() {
		new AlertDialog.Builder(this).setTitle("确认").setMessage("是否结束？")
				.setPositiveButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).setNegativeButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						uiHandle.removeMessages(TICK_WHAT);
						Intent intent = new Intent(RunMeteringActivity.this, RunGradeActivity.class);
						intent.putExtra("grades", grades);
						intent.putExtra("title", tvTitle.getText().toString());
						startActivity(intent);
						finish();
					}
				}).show();
	}

	/**
	 * 开始计时
	 */
	private void metering() {
		llTitle.setVisibility(View.VISIBLE);
		if (title.contains("50米跑")) {
			list.add(0, getDeltaT());
			grade = new RunGrade(i, getDeltaT(), "", "", 0);
		} else {
			list.add(0, getDeltaTForMiddleRace());
			grade = new RunGrade(i, getDeltaTForMiddleRace(), "", "", 0);
		}
		// list.add(0, getDeltaT());
		bd = new Bundle();
		// grade = new RunGrade(i, getDeltaT(), "", "", 0);
		grades.add(grade);
		bd.putSerializable("grades", grades);
		adapter.notifyDataSetChanged();
		i++;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		uiHandle.removeMessages(TICK_WHAT);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_F1:
			metering();
			return true;
		case KeyEvent.KEYCODE_F2:
			over();
			return true;
		case KeyEvent.KEYCODE_F3:
			reset();
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		uiHandle.removeMessages(TICK_WHAT);
		finish();
	}

	private void startTime() {
		new Thread() {
			public void run() {
				uiHandle.sendEmptyMessage(TICK_WHAT);
			};
		}.start();

	}

	/**
	 * 获取时间差
	 * 
	 * @return
	 */
	public String getDeltaT() {
		timeUsedInsec = System.currentTimeMillis() - startTime;
		min = (int) ((timeUsedInsec) / 60000);
		sec = (int) ((timeUsedInsec - min * 60000) / 1000);
		longmill = (int) (timeUsedInsec - min * 60000 - sec * 1000);
		String string = "";
		if (longmill < 991 && longmill > 90) {
			if (longmill % 10 == 0) {
				string = getMin(0) + ":" + getSec(0) + "." + longmill / 10;
			} else {
				string = getMin(0) + ":" + getSec(0) + "." + (longmill / 10 + 1);
			}
		} else if (longmill < 91) {
			if (longmill % 10 == 0) {
				string = getMin(0) + ":" + getSec(0) + "." + "0" + longmill / 10;
			} else {
				string = getMin(0) + ":" + getSec(0) + "." + "0" + (longmill / 10 + 1);
			}
		} else {
			if (sec < 59) {
				string = getMin(0) + ":" + getSec(1) + "." + "00";
			} else {
				string = getMin(1) + ":" + "00" + "." + "00";
			}
		}
		// string = getMin() + ":" + getSec() + "." + getLongMill(1);
		return string;
	}

	public String getDeltaTForMiddleRace() {
		timeUsedInsec = System.currentTimeMillis() - startTime;
		min = (int) ((timeUsedInsec) / 60000);
		sec = (int) ((timeUsedInsec - min * 60000) / 1000);
		longmill = (int) (timeUsedInsec - min * 60000 - sec * 1000);
		String string = "";
		if (longmill < 991 && longmill > 90) {
			if (longmill % 10 == 0) {
				string = getMin(0) + ":" + getSec(0);
			} else {
				string = getMin(0) + ":" + getSec(0);
			}
		} else if (longmill < 91) {
			if (longmill % 10 == 0) {
				string = getMin(0) + ":" + getSec(0);
			} else {
				string = getMin(0) + ":" + getSec(0);
			}
		} else {
			if (sec < 59) {
				string = getMin(0) + ":" + getSec(1);
			} else {
				string = getMin(1) + ":" + "00";
			}
		}
		// string = getMin() + ":" + getSec();
		return string;
	}

	/**
	 * 更新时间的显示
	 */
	private void updateClockUI() {
		Log.i("time", timeUsedInsec + "");
		tvTime.setText(getDeltaT());
		tvTime.setText(getDeltaT());

	}

	public String getMin(int n) {
		// min = (int) ((timeUsedInsec) / 60000) + n;
		return (min + n) < 10 ? "0" + (min + n) : (min + n) + "";
	}

	public String getSec(int n) {
		// sec = (int) ((timeUsedInsec - min * 60000) / 1000) + n;
		return (sec + n) < 10 ? "0" + (sec + n) : (sec + n) + "";
	}

	public String getLongMill(int flag) {
		longmill = (int) (timeUsedInsec - min * 60000 - sec * 1000);
		String result = "";
		switch (flag) {
		case 1:
			if (longmill % 10 == 0) {
				if (longmill / 10 < 10) {
					result = "0" + longmill / 10;
				} else {
					result = "" + longmill / 10;
				}
			} else {
				result = "" + longmill / 10 + 1;
			}
			break;
		case 2:
			if (longmill % 100 < 10) {
				result = longmill / 100 + "";
			} else {
				if (longmill > 909) {
					result = "0";
				} else {
					result = longmill / 100 + 1 + "";
				}

			}
			break;
		default:
			break;
		}
		// if (longmill < 10) {
		// return "00" + longmill;
		// } else if (longmill < 100) {
		// return "0" + longmill;
		// } else {
		// return longmill + "";
		// }
		return result;
	}

}
