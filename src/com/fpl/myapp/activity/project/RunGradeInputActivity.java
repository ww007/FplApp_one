package com.fpl.myapp.activity.project;

import java.util.List;

import org.apache.log4j.Logger;

import com.fpl.myapp2.R;
import com.fpl.myapp.activity.CaptureActivity;
import com.fpl.myapp.base.NFCActivity;
import com.fpl.myapp.db.DbService;
import com.fpl.myapp.db.SaveDBUtil;
import com.fpl.myapp.util.Constant;
import com.fpl.myapp.util.NetUtil;
import com.wnb.android.nfc.dataobject.entity.IC_ItemResult;
import com.wnb.android.nfc.dataobject.entity.IC_Result;
import com.wnb.android.nfc.dataobject.entity.Student;
import com.wnb.android.nfc.dataobject.service.IItemService;
import com.wnb.android.nfc.dataobject.service.impl.NFCItemServiceImpl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import ww.greendao.dao.Item;
import ww.greendao.dao.StudentItem;

public class RunGradeInputActivity extends NFCActivity {
	private TextView tvInfoTitle;
	private TextView tvName;
	private TextView tvGender;
	private TextView tvShow1;
	private TextView tvShow;
	private EditText etMax;
	private EditText etMin;
	private TextView tvInfoChengji;
	private TextView tvInfoUnit;
	private Button btnSave;
	private Button btnCancel;

	private String sex;
	private String number;
	private String name;
	private String title = "";
	private Student student;
	private Context context;

	private Logger log = Logger.getLogger(RunGradeInputActivity.class);
	private RadioGroup rg;
	private RadioButton rb0;
	private RadioButton rb1;
	private RadioButton rb2;
	private RadioButton rb3;
	private String max;
	private String min;
	private Item items;
	private EditText etMs;
	private EditText etS;
	private EditText etSec;
	private TextView tvMs;
	private TextView tvS;
	private TextView tvSec;
	private LinearLayout llRunChengji;
	private LinearLayout llInfoChengji;
	private String stuData;
	private List<ww.greendao.dao.Student> stuByCode;
	private SharedPreferences mSharedPreferences;
	private int readStyle;
	private Button btnScan;
	private String title2 = "";
	private int constant;
	private int sMax;
	private int sMin;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				etMs.setEnabled(false);
				etS.setEnabled(false);
				etSec.setEnabled(false);
				rb0.setEnabled(false);
				rb1.setEnabled(false);
				rb2.setEnabled(false);
				rb3.setEnabled(false);
				btnScan.setVisibility(View.VISIBLE);
				tvShow.setText("");
				break;
			case 2:
				NetUtil.showToast(context, "请先下载相关数据");
				rb0.setEnabled(false);
				rb1.setEnabled(false);
				rb2.setEnabled(false);
				rb3.setEnabled(false);
				if (readStyle == 0) {
					tvShow.setText("请刷卡");
				} else {
					btnScan.setVisibility(View.VISIBLE);
					tvShow.setVisibility(View.GONE);
				}
				etMs.setEnabled(false);
				etS.setEnabled(false);
				etSec.setEnabled(false);
				break;
			case 4:
				NetUtil.showToast(context, "条码识别不出，请手动输入");
				break;
			default:
				break;
			}
		};
	};
	private List<Item> itemLists;
	private int grade;
	private String etmin;
	private String ets;
	private String etms;
	private IC_ItemResult item;
	private Button btnGetStu;
	private EditText tvNumber;
	public static Activity mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_information);
		context = this;
		mActivity = this;

		// 加载配置
		Intent intent = getIntent();
		number = intent.getStringExtra("number");
		name = intent.getStringExtra("name");
		sex = intent.getStringExtra("sex");
		grade = intent.getIntExtra("grade", 0);
		title = intent.getStringExtra("title");
		title2 = intent.getStringExtra("title2");

		Log.i("grade", "=========" + grade);

		if (grade == 0) {
			etmin = "";
			ets = "";
			etms = "";
		} else {
			etmin = grade / 1000 / 60 + "";
			ets = grade / 1000 % 60 + "";
			etms = grade % 1000 / 10 + "";
		}

		if ("".equals(title)) {
			title = title2;
		}
		mSharedPreferences = getSharedPreferences("readStyles", Activity.MODE_PRIVATE);
		readStyle = mSharedPreferences.getInt("readStyle", 0);

		stuData = getIntent().getStringExtra("data");
		if (stuData != null && stuData.length() != 0) {
			stuByCode = DbService.getInstance(context).queryStudentByCode(stuData);
			if (stuByCode.isEmpty()) {
				if (!stuData.equals("扫码时间过长")) {
					Toast.makeText(context, "查无此人", Toast.LENGTH_SHORT).show();
					stuData = "";
					mHandler.sendEmptyMessage(1);
				}
			}
		}

		if (title.equals("800/1000米跑")) {
			constant = Constant.MIDDLE_RACE;
			itemLists = DbService.getInstance(context).queryItemByMachineCodeList(constant + "");
			if (itemLists.isEmpty()) {
				items = null;
			} else {
				items = itemLists.get(0);
			}
		} else if (title.equals("50米跑")) {
			constant = Constant.RUN50;
			items = DbService.getInstance(context).queryItemByMachineCode(constant + "");
		} else if (title.contains("往返跑")) {
			constant = Constant.SHUTTLE_RUN;
			items = DbService.getInstance(context).queryItemByMachineCode(constant + "");
		} else if (title.contains("篮球")) {
			constant = Constant.BASKETBALL_SKILL;
			items = DbService.getInstance(context).queryItemByMachineCode(constant + "");
		} else if (title.contains("足球")) {
			constant = Constant.FOOTBALL_SKILL;
			items = DbService.getInstance(context).queryItemByMachineCode(constant + "");
		} else if (title.contains("游泳")) {
			constant = Constant.SWIM;
			items = DbService.getInstance(context).queryItemByMachineCode(constant + "");
		}

		if (items == null) {
			max = "";
			min = "";
		} else {
			sMax = items.getMaxValue() / 1000;
			sMin = items.getMinValue() / 1000;
			if (sMax < 60) {
				max = sMax + "″";
				min = sMin + "″";
			} else {
				if (sMax % 60 == 0) {
					max = sMax / 60 + "′";
				} else {
					max = sMax / 60 + "′" + sMax % 60 + "″";
				}
				if (sMin < 60) {
					min = sMin + "″";
				} else {
					if (sMin % 60 == 0) {
						min = sMin / 60 + "′";
					} else {
						min = sMin / 60 + "′" + sMin % 60 + "″";
					}
				}
			}

		}

		initView();
		setListener();

	}

	@Override
	public void onNewIntent(Intent intent) {
		if (readStyle == 0) {
			if (View.VISIBLE == tvShow1.getVisibility() && "成绩保存成功".equals(tvShow1.getText().toString())) {
				writeCard(intent);
			} else {
				readCard(intent);
			}
		} else {
			NetUtil.showToast(context, "当前选择非IC卡状态，请设置");
		}
	}

	private int getChengJi() {
		int sec = 0;
		int s = 0;
		int ms = 0;
		if (!title.equals("50米跑")) {
			if ("".equals(etSec.getText().toString())) {
				sec = 0;
			} else {
				sec = Integer.parseInt(etSec.getText().toString());
			}
		}
		if ("".equals(etS.getText().toString())) {
			s = 0;
		} else {
			s = Integer.parseInt(etS.getText().toString());
		}
		if ("".equals(etMs.getText().toString())) {
			ms = 0;
		} else {
			ms = Integer.parseInt(etMs.getText().toString());
		}

		int etChengji = sec * 60 * 1000 + s * 1000 + ms * 10;
		Log.i("getChengJi", etChengji + "");
		return etChengji;

	}

	/**
	 * 写卡
	 * 
	 * @param intent
	 */
	private void writeCard(Intent intent) {
		try {
			IItemService itemService = new NFCItemServiceImpl(intent);
			if (itemService.IC_ReadStuInfo().getStuCode().equals(tvNumber.getText().toString())) {
				IC_Result[] resultRun = new IC_Result[4];
				// if ("".equals(etMs.getText().toString()) &&
				// "".equals(etS.getText().toString())
				// && "".equals(etSec.getText().toString())) {
				// chengji = 0;
				// } else {
				// chengji = getChengJi() + "";
				// }
				int result1 = chengji;
				resultRun[0] = new IC_Result(result1, 1, 0, 0);
				IC_ItemResult ItemResultRun = new IC_ItemResult(constant, 0, 0, resultRun);
				boolean isRunResult = itemService.IC_WriteItemResult(ItemResultRun);
				log.info("写入跑步成绩=>" + isRunResult + "成绩：" + result1 + "，学生：" + tvNumber.getText().toString());
				if (isRunResult) {
					tvShow1.setText("成绩写卡完成");
					tvShow.setText("请刷卡");
				} else {
					Toast.makeText(this, "写卡出错", Toast.LENGTH_SHORT).show();
				}
			} else {
				NetUtil.showToast(context, "写卡失败，此卡非当前记录");
			}
		} catch (Exception e) {
			log.error(title + "写卡失败");
			e.printStackTrace();
		}

	}

	/**
	 * 读卡
	 */
	private void readCard(Intent intent) {
		try {
			IItemService itemService = new NFCItemServiceImpl(intent);
			student = itemService.IC_ReadStuInfo();
			log.info(title + "读卡=>" + student.toString());
			if (title.equals("800/1000米跑") || title.equals("800米跑") || title.equals("1000米跑")) {
				item = itemService.IC_ReadItemResult(Constant.MIDDLE_RACE);
			} else if (title.equals("50米跑")) {
				item = itemService.IC_ReadItemResult(Constant.RUN50);
			} else if (title.contains("往返跑")) {
				item = itemService.IC_ReadItemResult(Constant.SHUTTLE_RUN);
			} else if (title.contains("篮球")) {
				item = itemService.IC_ReadItemResult(Constant.BASKETBALL_SKILL);
			} else if (title.contains("足球")) {
				item = itemService.IC_ReadItemResult(Constant.FOOTBALL_SKILL);
			} else if (title.contains("游泳")) {
				item = itemService.IC_ReadItemResult(Constant.SWIM);
			}

			int itemResult;

			if (item.getResult()[0].getResultVal() == 0) {
				itemResult = 0;
				btnCancel.setVisibility(View.GONE);
				btnSave.setVisibility(View.GONE);
			} else {
				itemResult = item.getResult()[0].getResultVal();
				btnCancel.setVisibility(View.VISIBLE);
				btnSave.setVisibility(View.VISIBLE);
			}

			Log.i("itemResult======", itemResult + "");
			if (itemResult == 0) {
				etmin = "";
				ets = "";
				etms = "";
			} else {
				etmin = itemResult / 1000 / 60 + "";
				ets = itemResult / 1000 % 60 + "";
				etms = itemResult % 1000 + "";
			}

			if (1 == student.getSex()) {
				sex = "男";
			} else {
				sex = "女";
			}
			tvGender.setText(sex);
			etMs.setText(etms);
			etS.setText(ets);
			etSec.setText(etmin);
			tvName.setText(student.getStuName().toString());
			tvNumber.setText(student.getStuCode().toString());
			tvNumber.setFocusableInTouchMode(false);
			tvNumber.setFocusable(false);
			tvShow1.setVisibility(View.GONE);
			rb0.setChecked(true);
			tvShow.setText("请输入成绩");
			tvShow.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			log.error(title + "读卡失败");
			e.printStackTrace();
		}

	}

	private void initOne() {
		// etMs.setText(etms);
		// etS.setText(ets);
		// etSec.setText(etmin);
		tvShow1.setVisibility(View.GONE);
		btnCancel.setVisibility(View.GONE);
		btnSave.setVisibility(View.GONE);
		etMs.setEnabled(true);
		etS.setEnabled(true);
		etSec.setEnabled(true);
		rb0.setChecked(true);

		tvShow.setText("请输入成绩");
		tvShow.setVisibility(View.VISIBLE);
	}

	protected void initView() {
		btnScan = (Button) findViewById(R.id.btn_scanCode);
		tvInfoTitle = (TextView) findViewById(R.id.tv_info_title);
		tvInfoChengji = (TextView) findViewById(R.id.tv_info_chengji);
		tvInfoUnit = (TextView) findViewById(R.id.tv_info_unit);
		tvName = (TextView) findViewById(R.id.tv_name_edit);
		tvGender = (TextView) findViewById(R.id.tv_gender_edit);
		tvNumber = (EditText) findViewById(R.id.et_number_edit);
		btnGetStu = (Button) findViewById(R.id.btn_person_getstus);
		tvShow1 = (TextView) findViewById(R.id.tv_infor_show1);
		tvShow = (TextView) findViewById(R.id.tv_infor_show);
		tvMs = (TextView) findViewById(R.id.tv_unit_ms);
		tvS = (TextView) findViewById(R.id.tv_unit_s);
		tvSec = (TextView) findViewById(R.id.tv_unit_sec);
		etMs = (EditText) findViewById(R.id.et_run_ms);
		etS = (EditText) findViewById(R.id.et_run_s);
		etSec = (EditText) findViewById(R.id.et_run_sec);
		llRunChengji = (LinearLayout) findViewById(R.id.ll_run_chengji);
		llRunChengji.setVisibility(View.VISIBLE);
		llInfoChengji = (LinearLayout) findViewById(R.id.ll_info_chengji);
		llInfoChengji.setVisibility(View.GONE);
		etMax = (EditText) findViewById(R.id.et_info_max);
		etMin = (EditText) findViewById(R.id.et_info_min);
		btnSave = (Button) findViewById(R.id.btn_info_save);
		btnCancel = (Button) findViewById(R.id.btn_info_cancel);
		rg = (RadioGroup) findViewById(R.id.radioGroup);
		rb0 = (RadioButton) findViewById(R.id.radio0);
		rb1 = (RadioButton) findViewById(R.id.radio1);
		rb2 = (RadioButton) findViewById(R.id.radio2);
		rb3 = (RadioButton) findViewById(R.id.radio3);
		rb2.setText("中退");
		rb3.setText("弃权");

		if (title.equals("800/1000米跑") && sex.equals("男")) {
			tvInfoTitle.setText("1000米跑");
		} else if (title.equals("800/1000米跑") && sex.equals("女")) {
			tvInfoTitle.setText("800米跑");
		} else {
			tvInfoTitle.setText(title);
		}
		tvInfoChengji.setText("成绩");
		tvInfoUnit.setVisibility(View.VISIBLE);
		tvInfoUnit.setText("毫秒");
		tvName.setText(name);
		tvGender.setText(sex);
		etMax.setText(max);
		etMin.setText(min);
		etMs.setText(etms);
		etS.setText(ets);
		etSec.setText(etmin);
		btnCancel.setVisibility(View.GONE);
		btnSave.setVisibility(View.GONE);
		tvShow.setText("请输入成绩");
		tvShow1.setVisibility(View.GONE);
		tvNumber.setText(stuData);

		if (title.equals("50米跑")) {
			etSec.setVisibility(View.GONE);
			tvSec.setVisibility(View.GONE);
		}

		// if (readStyle == 1) {
		// tvNumber.setText(stuData);
		// tvShow.setVisibility(View.GONE);
		// if (stuData == "") {
		// sex = "";
		// tvName.setText("");
		// } else {
		// if (stuByCode.get(0).getSex() == 1) {
		// sex = "男";
		// } else {
		// sex = "女";
		// }
		// tvName.setText(stuByCode.get(0).getStudentName());
		// }
		// tvGender.setText(sex);
		// btnScan.setVisibility(View.GONE);
		// tvShow.setText("请输入成绩");
		// tvShow.setVisibility(View.VISIBLE);
		// initOne();
		// } else {
		// tvNumber.setText(number);
		// stuData = "";
		// }
		if (readStyle != 1) {
			tvNumber.setText(number);
			stuData = "";
			btnGetStu.setVisibility(View.GONE);
			tvNumber.setEnabled(false);
		} else {

			if (tvNumber.getText().toString().isEmpty()) {
				stuData = "";
				tvNumber.setEnabled(false);
				btnGetStu.setVisibility(View.GONE);
			} else if (tvNumber.getText().toString().equals("扫码时间过长")) {
				tvNumber.setText("");
				tvNumber.setEnabled(true);
				btnGetStu.setVisibility(View.VISIBLE);
				mHandler.sendEmptyMessage(4);
			} else {
				tvNumber.setEnabled(false);
				tvNumber.setFocusableInTouchMode(false);
				tvNumber.setFocusable(false);
				btnGetStu.setVisibility(View.GONE);
				if (stuByCode.get(0).getSex() == 1) {
					sex = "男";
				} else {
					sex = "女";
				}
				tvName.setText(stuByCode.get(0).getStudentName());
				tvGender.setText(sex);
				etS.setEnabled(true);
				etMs.setEnabled(true);
				etMin.setEnabled(true);
				btnScan.setVisibility(View.GONE);
				tvShow.setText("请输入成绩");
				tvShow.setVisibility(View.VISIBLE);
				initOne();
			}
		}
		if (grade == 0) {
			btnCancel.setVisibility(View.GONE);
			btnSave.setVisibility(View.GONE);
		} else {
			btnCancel.setVisibility(View.VISIBLE);
			btnSave.setVisibility(View.VISIBLE);
		}
	}

	private int flag = 0;
	private String checkedBtn = "正常";
	private int resultState;
	private int chengji;
	private StudentItem studentItems;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 66 || keyCode == 135 || keyCode == 136) {
			Intent intent = new Intent(RunGradeInputActivity.this, CaptureActivity.class);
			intent.putExtra("className", Constant.runGradeInput);
			intent.putExtra("title2", title);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setListener() {
		btnGetStu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tvNumber.getText().toString().trim().isEmpty()) {
					NetUtil.showToast(context, "学号为空");
				} else {
					List<ww.greendao.dao.Student> getstu = DbService.getInstance(context)
							.queryStudentByCode(tvNumber.getText().toString().trim());
					if (getstu.isEmpty()) {
						NetUtil.showToast(context, "查无此人");
						rb0.setEnabled(false);
						rb1.setEnabled(false);
						rb2.setEnabled(false);
						rb3.setEnabled(false);
						etS.setEnabled(false);
						etMs.setEnabled(false);
						etMin.setEnabled(false);
					} else {
						tvNumber.setEnabled(false);
						tvNumber.setFocusableInTouchMode(false);
						tvNumber.setFocusable(false);
						btnGetStu.setVisibility(View.GONE);
						if (getstu.get(0).getSex() == 1) {
							sex = "男";
						} else {
							sex = "女";
						}
						tvName.setText(getstu.get(0).getStudentName());
						tvGender.setText(sex);
						etS.setEnabled(true);
						etMs.setEnabled(true);
						etMin.setEnabled(true);
						rb0.setEnabled(true);
						rb1.setEnabled(true);
						rb2.setEnabled(true);
						rb3.setEnabled(true);
						btnScan.setVisibility(View.GONE);
						tvShow.setText("请输入成绩");
						tvShow.setVisibility(View.VISIBLE);
						initOne();
					}
				}
			}
		});

		btnScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RunGradeInputActivity.this, CaptureActivity.class);
				intent.putExtra("className", Constant.runGradeInput);
				intent.putExtra("title2", title);
				startActivity(intent);
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				etS.setText("");
				etMs.setText("");
				etSec.setText("");
				btnCancel.setVisibility(View.GONE);
				btnSave.setVisibility(View.GONE);
				tvShow.setText("请输入成绩");
				tvShow.setVisibility(View.VISIBLE);
			}
		});

		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				btnCancel.setVisibility(View.VISIBLE);
				btnSave.setVisibility(View.VISIBLE);
				RadioButton radioButton = (RadioButton) RunGradeInputActivity.this
						.findViewById(group.getCheckedRadioButtonId());
				checkedBtn = radioButton.getText().toString();
				if (checkedBtn.equals("正常")) {
					tvMs.setVisibility(View.VISIBLE);
					tvS.setVisibility(View.VISIBLE);
					if (title.equals("50米跑")) {
						tvSec.setVisibility(View.GONE);
					} else {
						tvSec.setVisibility(View.VISIBLE);
						etSec.setVisibility(View.VISIBLE);
					}
					tvShow.setVisibility(View.VISIBLE);
					etMs.setVisibility(View.VISIBLE);
					etS.setVisibility(View.VISIBLE);
					etS.setText("");
					etMs.setText("");
					etSec.setText("");
					etSec.setTextSize(18);
					etS.setTextSize(18);
					etMs.setTextSize(18);
					etS.setTextColor(getResources().getColor(android.R.color.black));
					etMs.setTextColor(getResources().getColor(android.R.color.black));
					etSec.setTextColor(getResources().getColor(android.R.color.black));
					if (tvNumber.getText().toString().isEmpty()) {
						etS.setEnabled(false);
						etMs.setEnabled(false);
						etSec.setEnabled(false);
					} else {
						etSec.setEnabled(true);
						etMs.setEnabled(true);
						etS.setEnabled(true);
					}
					chengji = getChengJi();
				} else if (checkedBtn.equals("犯规")) {
					if (tvName.getText().toString().isEmpty()) {
						btnSave.setVisibility(View.GONE);
						btnCancel.setVisibility(View.GONE);
					}
					tvMs.setVisibility(View.GONE);
					tvS.setVisibility(View.GONE);
					tvSec.setVisibility(View.GONE);
					etMs.setVisibility(View.GONE);
					etSec.setVisibility(View.GONE);
					btnScan.setVisibility(View.GONE);
					etS.setText("DQ");
					etS.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
					etS.setEnabled(false);
					etS.setTextSize(23);
					chengji = 0;
				} else if (checkedBtn.equals("中退")) {
					if (tvName.getText().toString().isEmpty()) {
						btnSave.setVisibility(View.GONE);
						btnCancel.setVisibility(View.GONE);
					}
					tvMs.setVisibility(View.GONE);
					tvS.setVisibility(View.GONE);
					tvSec.setVisibility(View.GONE);
					etMs.setVisibility(View.GONE);
					etSec.setVisibility(View.GONE);
					btnScan.setVisibility(View.GONE);
					etS.setText("DNF");
					etS.setTextColor(getResources().getColor(android.R.color.darker_gray));
					etS.setEnabled(false);
					etS.setTextSize(23);
					chengji = 0;
				} else if (checkedBtn.equals("弃权")) {
					if (tvName.getText().toString().isEmpty()) {
						btnSave.setVisibility(View.GONE);
						btnCancel.setVisibility(View.GONE);
					}
					tvMs.setVisibility(View.GONE);
					tvS.setVisibility(View.GONE);
					tvSec.setVisibility(View.GONE);
					etMs.setVisibility(View.GONE);
					etSec.setVisibility(View.GONE);
					btnScan.setVisibility(View.GONE);
					etS.setText("DNS");
					etS.setTextColor(getResources().getColor(android.R.color.darker_gray));
					etS.setEnabled(false);
					etS.setTextSize(23);
					chengji = 0;
				}
			}
		});

		etMs.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (tvNumber.getText().toString().isEmpty() || etMs.getText().toString().isEmpty()) {
					if (readStyle != 1) {
						tvShow.setVisibility(View.VISIBLE);
					}
					btnCancel.setVisibility(View.GONE);
					btnSave.setVisibility(View.GONE);
				} else {
					tvShow.setVisibility(View.GONE);
					btnCancel.setVisibility(View.VISIBLE);
					btnSave.setVisibility(View.VISIBLE);
				}
			}
		});

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (DbService.getInstance(context).loadAllItem().isEmpty()) {
					Toast.makeText(context, "请先初始化数据", Toast.LENGTH_SHORT).show();
					return;
				}
				// if (getChengJi() == 0 && checkedBtn.equals("正常")) {
				// Toast.makeText(RunGradeInputActivity.this, "成绩为空",
				// Toast.LENGTH_SHORT).show();
				// return;
				// } else
				if (checkedBtn.equals("犯规")) {
					resultState = -1;
				} else if (checkedBtn.equals("中退")) {
					resultState = -2;
				} else if (checkedBtn.equals("弃权")) {
					resultState = -3;
				} else if (checkedBtn.equals("正常")) {
					chengji = getChengJi();
					if (Integer.parseInt(getS()) > 59) {
						NetUtil.showToast(context, "秒数错误");
						return;
					}
					if (Integer.parseInt(getMs()) > 100) {
						NetUtil.showToast(context, "毫秒数错误");
						return;
					}
					Log.i("chengji=", chengji + "");
					if (chengji > items.getMaxValue() || chengji < items.getMinValue()) {
						Toast.makeText(context, "不在输入范围，请重新输入", Toast.LENGTH_SHORT).show();
						etS.setText("");
						etMs.setText("");
						etSec.setText("");
						return;
					}
					// chengji = getChengJi();
				}
				if (title.equals("50米跑")) {
					// 查询数据库中保存的该学生项目成绩的轮次
					// String itemCode =
					// DbService.getInstance(context).queryItemByMachineCode(Constant.RUN50
					// + "")
					// .getItemCode();
					// studentItems =
					// DbService.getInstance(context).queryStudentItemByCode(tvNumber.getText().toString(),
					// itemCode);
					// if (studentItems == null) {
					// Toast.makeText(context, "当前学生项目不存在",
					// Toast.LENGTH_SHORT).show();
					// return;
					// } else {
					// resultState = 0;
					// }
					flag = SaveDBUtil.saveGradesDB(context, tvNumber.getText().toString(), chengji + "", resultState,
							Constant.RUN50 + "", "50米跑");
					log.info("保存50米跑" + tvNumber.getText().toString() + "成绩：" + chengji);
				} else if (title.equals("800/1000米跑")) {
					String itemName;
					// String itemCode;
					if (sex.equals("男")) {
						itemName = "1000米跑";
					} else {
						itemName = "800米跑";
					}
					// itemCode =
					// DbService.getInstance(context).queryItemByName(itemName).getItemCode();
					// 查询数据库中保存的该学生项目成绩的轮次
					// studentItems =
					// DbService.getInstance(context).queryStudentItemByCode(tvNumber.getText().toString(),
					// itemCode);
					// if (studentItems == null) {
					// Toast.makeText(context, "当前学生项目不存在",
					// Toast.LENGTH_SHORT).show();
					// return;
					// } else {
					// resultState = 0;
					// }
					flag = SaveDBUtil.saveGradesDB(context, tvNumber.getText().toString(), chengji + "", resultState,
							Constant.MIDDLE_RACE + "", itemName);
					log.info("保存800/1000米跑" + tvNumber.getText().toString() + "成绩：" + chengji);
				} else if (title.equals("50米x8往返跑")) {
					// 查询数据库中保存的该学生项目成绩的轮次
					// String itemCode =
					// DbService.getInstance(context).queryItemByMachineCode(Constant.SHUTTLE_RUN
					// + "")
					// .getItemCode();
					// long stuID =
					// DbService.getInstance(context).queryStudentByCode(tvNumber.getText().toString()).get(0)
					// .getStudentID();
					// long itemID =
					// DbService.getInstance(context).queryItemByCode(itemCode).getItemID();
					// studentItems =
					// DbService.getInstance(context).queryStudentItemByCode(tvNumber.getText().toString(),
					// itemCode);
					// if (studentItems == null) {
					// Toast.makeText(context, "当前学生项目不存在",
					// Toast.LENGTH_SHORT).show();
					// return;
					// } else {
					// resultState = 0;
					// }
					flag = SaveDBUtil.saveGradesDB(context, tvNumber.getText().toString(), chengji + "", resultState,
							Constant.SHUTTLE_RUN + "", "50米x8往返跑");
					log.info("保存50米x8往返跑" + tvNumber.getText().toString() + "成绩：" + chengji);
				}
				btnCancel.setVisibility(View.GONE);
				btnSave.setVisibility(View.GONE);
				tvShow1.setVisibility(View.VISIBLE);
				if (readStyle == 1) {
					tvShow.setVisibility(View.GONE);
				} else {
					tvShow.setVisibility(View.VISIBLE);
				}

				if (flag == 1) {
					if (stuData.isEmpty()) {
						tvShow1.setText("成绩保存成功");
						tvShow.setVisibility(View.VISIBLE);
						tvShow.setText("请刷卡");
					} else {
						tvShow.setVisibility(View.GONE);
						tvShow1.setVisibility(View.GONE);
						etMs.setText("");
						etSec.setText("");
						etS.setText("");
						Toast.makeText(context, "成绩保存成功", Toast.LENGTH_SHORT).show();
						btnSave.setVisibility(View.GONE);
						btnCancel.setVisibility(View.GONE);
						btnScan.setVisibility(View.VISIBLE);
					}
				} else {
					if (stuData.isEmpty()) {
						tvShow1.setText("成绩保存失败");
						tvShow.setVisibility(View.VISIBLE);
						tvShow.setText("请刷卡");
					} else {
						tvShow1.setVisibility(View.GONE);
						Toast.makeText(context, "成绩保存失败！", Toast.LENGTH_SHORT).show();
						btnSave.setVisibility(View.GONE);
						btnCancel.setVisibility(View.GONE);
						tvShow.setVisibility(View.GONE);
						btnScan.setVisibility(View.VISIBLE);
					}
				}
			}
		});

	}

	protected String getMs() {
		String ms = etMs.getText().toString();
		if ("".equals(ms)) {
			return "0";
		} else {
			return ms;
		}
	}

	protected String getS() {
		String s = etS.getText().toString();
		if ("".equals(s)) {
			return "0";
		} else {
			return s;
		}
	}
}
