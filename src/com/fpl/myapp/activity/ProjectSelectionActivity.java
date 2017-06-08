package com.fpl.myapp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fpl.myapp.activity.project.BroadJumpActivity;
import com.fpl.myapp.activity.project.HeightAndWeightActivity;
import com.fpl.myapp.activity.project.InfraredBallActivity;
import com.fpl.myapp.activity.project.JumpHeightActivity;
import com.fpl.myapp.activity.project.PullUpActivity;
import com.fpl.myapp.activity.project.PushUpActivity;
import com.fpl.myapp.activity.project.RopeSkippingActivity;
import com.fpl.myapp.activity.project.Run50Activity;
import com.fpl.myapp.activity.project.Run800Activity;
import com.fpl.myapp.activity.project.ShuttleRunActivity;
import com.fpl.myapp.activity.project.SitAndReachActivity;
import com.fpl.myapp.activity.project.SitUpActivity;
import com.fpl.myapp.activity.project.VisionActivity;
import com.fpl.myapp.activity.project.VitalCapacityActivity;
import com.fpl.myapp2.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ProjectSelectionActivity extends Activity {
	private int[] icon = { R.drawable.btn_run50_selector, R.drawable.btn_middle_run_selector,
			R.drawable.btn_hw_selector, R.drawable.btn_fhl_selector, R.drawable.btn_ldty_selector,
			R.drawable.btn_ywqz_selector, R.drawable.btn_zwtqq_selector, R.drawable.btn_ytxx_selector,
			R.drawable.project_return_selector };
	private String[] iconname = { "50米跑", "800/1000米跑", "身高体重", "肺活量", "立定跳远", "仰卧起坐", "坐位体前屈", "引体向上", "返回" };
	private GridView gvProjectSelection;
	private ArrayList<Map<String, Object>> dataList;
	private SimpleAdapter simAdapter;
	private ArrayList<String> projects = new ArrayList<>();
	private String[] newiconname;
	private int[] newicon;
	private SharedPreferences sharedPreferences;
	// 条形码或者NFC
	private int readStyle = 0;
	private boolean shortPress = false;
	private SharedPreferences mSharedPreferences;
	private int getStyle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_selection);

		sharedPreferences = getSharedPreferences("projects", Activity.MODE_PRIVATE);
		int selected = sharedPreferences.getInt("size", 0);
		Log.i("selected=", selected + "");

		mSharedPreferences = this.getSharedPreferences("readStyles", Activity.MODE_PRIVATE);
		getStyle=mSharedPreferences.getInt("readStyle", 0);

		for (int i = 0; i < selected; i++) {
			projects.add(sharedPreferences.getString(i + "", ""));
		}
		Log.i("projects=", projects.toString());
		initView();
		setListeners();
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_F1) {
			new AlertDialog.Builder(this).setTitle("选择读取方法").setIcon(android.R.drawable.ic_dialog_info)
					.setSingleChoiceItems(new String[] { "IC卡", "条形码" }, getStyle, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								readStyle = 0;
								Log.i("-------", readStyle + "");
								// SharedPreferences保存读取方式
								SharedPreferences.Editor editor = mSharedPreferences.edit();
								editor.putInt("readStyle", readStyle);
								editor.commit();
								break;
							case 1:
								readStyle = 1;
								Log.i("-------", readStyle + "");
								// SharedPreferences保存读取方式
								SharedPreferences.Editor editor1 = mSharedPreferences.edit();
								editor1.putInt("readStyle", readStyle);
								editor1.commit();
								break;

							default:
								break;
							}

						}
					}).setNegativeButton("确定", null).show();
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_F1) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				event.startTracking();
				if (event.getRepeatCount() == 0) {
					shortPress = true;
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_F1) {
			if (shortPress) {
				// Toast.makeText(this, "shortPress", Toast.LENGTH_LONG).show();
			} else {
			}
			shortPress = false;
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	private void initView() {
		newiconname = new String[iconname.length + projects.size()];
		newicon = new int[icon.length + projects.size()];

		for (int i = 0; i < iconname.length; i++) {
			newiconname[i] = iconname[i];
			newicon[i] = icon[i];
		}

		for (int i = 0; i < projects.size(); i++) {
			newiconname[8 + i] = projects.get(i).toString();
			if (projects.get(i).toString().contains("跳绳")) {
				newicon[8 + i] = R.drawable.btn_ts_selector;
			} else if (projects.get(i).toString().contains("篮球")) {
				newicon[8 + i] = R.drawable.btn_basketball_selector;
			} else if (projects.get(i).toString().contains("足球")) {
				newicon[8 + i] = R.drawable.btn_football_selector;
			} else if (projects.get(i).toString().contains("排球")) {
				newicon[8 + i] = R.drawable.btn_pq_selector;
			} else if (projects.get(i).toString().contains("实心球")) {
				newicon[8 + i] = R.drawable.btn_sxq_selector;
			} else if (projects.get(i).toString().contains("摸高")) {
				newicon[8 + i] = R.drawable.btn_mg_selector;
			} else if (projects.get(i).toString().contains("视力")) {
				newicon[8 + i] = R.drawable.btn_sl_selector;
			} else if (projects.get(i).toString().contains("游泳")) {
				newicon[8 + i] = R.drawable.btn_swim_selector;
			} else if (projects.get(i).toString().contains("毽子")) {
				newicon[8 + i] = R.drawable.btn_tjz_selector;
			} else if (projects.get(i).toString().contains("返跑")) {
				newicon[8 + i] = R.drawable.btn_zfp_selector;
			} else if (projects.get(i).toString().contains("俯卧撑")) {
				newicon[8 + i] = R.drawable.btn_fwc_selector;
			}

			newiconname[iconname.length + projects.size() - 1] = "返回";
			newicon[iconname.length + projects.size() - 1] = R.drawable.project_return_selector;
		}

		gvProjectSelection = (GridView) findViewById(R.id.gv_project_selection);
		dataList = new ArrayList<Map<String, Object>>();

		getData();
		String[] from = { "image", "text" };
		int[] to = { R.id.image, R.id.text };
		simAdapter = new SimpleAdapter(this, dataList, R.layout.item, from, to);
		gvProjectSelection.setAdapter(simAdapter);
	}

	private void setListeners() {
		gvProjectSelection.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView tv = (TextView) view.findViewById(R.id.text);
				String tvItem = tv.getText().toString();
				Log.i("tvItem", tvItem);
				switch (tvItem) {
				case "50米跑":
					startActivity(new Intent(ProjectSelectionActivity.this, Run50Activity.class));
					break;
				case "身高体重":
					startActivity(new Intent(ProjectSelectionActivity.this, HeightAndWeightActivity.class));
					break;
				case "肺活量":
					startActivity(new Intent(ProjectSelectionActivity.this, VitalCapacityActivity.class));
					break;
				case "立定跳远":
					startActivity(new Intent(ProjectSelectionActivity.this, BroadJumpActivity.class));
					break;
				case "仰卧起坐":
					startActivity(new Intent(ProjectSelectionActivity.this, SitUpActivity.class));
					break;
				case "800/1000米跑":
					startActivity(new Intent(ProjectSelectionActivity.this, Run800Activity.class));
					break;
				case "俯卧撑":
					startActivity(new Intent(ProjectSelectionActivity.this, PushUpActivity.class));
					break;
				case "坐位体前屈":
					startActivity(new Intent(ProjectSelectionActivity.this, SitAndReachActivity.class));
					break;
				case "一分钟跳绳":
					startActivity(new Intent(ProjectSelectionActivity.this, RopeSkippingActivity.class));
					break;
				case "视力":
					startActivity(new Intent(ProjectSelectionActivity.this, VisionActivity.class));
					break;
				case "引体向上":
					startActivity(new Intent(ProjectSelectionActivity.this, PullUpActivity.class));
					break;
				case "摸高":
					startActivity(new Intent(ProjectSelectionActivity.this, JumpHeightActivity.class));
					break;
				case "排球":

					break;
				case "红外实心球":
					startActivity(new Intent(ProjectSelectionActivity.this, InfraredBallActivity.class));
					break;
				case "篮球运球":

					break;
				case "50米x8往返跑":
					startActivity(new Intent(ProjectSelectionActivity.this, ShuttleRunActivity.class));
					break;
				case "返回":
					finish();
					break;

				default:
					break;
				}
			}
		});
	}

	private void getData() {
		for (int i = 0; i < newicon.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", newicon[i]);
			map.put("text", newiconname[i]);
			dataList.add(map);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
