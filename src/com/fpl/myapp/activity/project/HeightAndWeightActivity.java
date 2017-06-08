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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import ww.greendao.dao.Item;
import ww.greendao.dao.StudentItem;

public class HeightAndWeightActivity extends NFCActivity {

	private TextView tvNumber;
	private TextView tvGender;
	private TextView tvName;
	private TextView tvShow1;
	private TextView tvShow;
	private EditText etHeight;
	private EditText etWeight;
	private Button btnSave;
	private Button btnCancel;
	private ImageButton ibQuit;
	private Student student;
	private String sex;
	private TextView tvHUnit;
	private TextView tvWUnit;
	private TextView tvLeft;
	private TextView tvRight;
	private TextView tvTitle;
	private Context context;
	private StudentItem studentItems;

	private Logger log = Logger.getLogger(HeightAndWeightActivity.class);
	private EditText etHMax;
	private EditText etHMin;
	private EditText etWMax;
	private EditText etWMin;
	private String hMax;
	private String hMin;
	private String wMax;
	private String wMin;
	private SharedPreferences mSharedPreferences;
	private int readStyle;
	private String stuData;
	private List<ww.greendao.dao.Student> stuByCode;
	private Button btnScan;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				NetUtil.showToast(context, "当前学生项目不存在");
				tvShow.setVisibility(View.GONE);
				btnScan.setVisibility(View.VISIBLE);
				etHeight.setEnabled(false);
				etWeight.setEnabled(false);
				break;
			case 2:
				NetUtil.showToast(context, "当前学生项目不存在");
				tvShow.setVisibility(View.VISIBLE);
				tvShow.setText("请刷卡");
				etHeight.setEnabled(false);
				etWeight.setEnabled(false);
				break;
			case 3:
				NetUtil.showToast(context, "请先下载相关数据");
				tvShow.setText("请刷卡");
				etHeight.setEnabled(false);
				etWeight.setEnabled(false);
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_height_and_weight);
		context = this;

		mSharedPreferences = getSharedPreferences("readStyles", Activity.MODE_PRIVATE);
		readStyle = mSharedPreferences.getInt("readStyle", 0);

		stuData = getIntent().getStringExtra("data");
		if (stuData != null && stuData.length() != 0) {
			stuByCode = DbService.getInstance(context).queryStudentByCode(stuData);
			Log.i("stuByCode", stuByCode.size() + "-" + stuByCode.get(0).toString());
			if (stuByCode.isEmpty()) {
				Toast.makeText(context, "查无此人", Toast.LENGTH_SHORT).show();
				stuData = "";
			} else {
				String itemCode = DbService.getInstance(context).queryItemByMachineCode2(Constant.HEIGHT_WEIGHT + "")
						.get(0).getItemCode();
				studentItems = DbService.getInstance(context).queryStudentItemByCode(stuData, itemCode);
				Log.i("studentItems", studentItems.toString());
				if (studentItems == null) {
					mHandler.sendEmptyMessage(1);
				}
			}
		}

		Item items = DbService.getInstance(context).queryItemByCode("E01");
		Item items2 = DbService.getInstance(context).queryItemByCode("E02");

		if (items == null) {
			hMax = "";
			hMin = "";
			wMax = "";
			wMin = "";
		} else {
			hMax = items.getMaxValue() / 10 + "";
			hMin = items.getMinValue() / 10 + "";
			wMax = items2.getMaxValue() / 1000 + "";
			wMin = items2.getMinValue() / 1000 + "";
		}
		initView();
		setListener();
	}

	@Override
	public void onNewIntent(Intent intent) {
		if (readStyle == 0) {
			if (View.VISIBLE == tvShow1.getVisibility() && "保存成功".equals(tvShow1.getText().toString())) {
				writeCard(intent);
			} else {
				readCard(intent);
			}
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
			log.info("身高体重读卡->" + student.toString());

			if (1 == student.getSex()) {
				sex = "男";
			} else {
				sex = "女";
			}
			tvGender.setText(sex);
			tvName.setText(student.getStuName().toString());
			tvNumber.setText(student.getStuCode().toString());

			if (DbService.getInstance(context).loadAllItem().isEmpty()
					|| DbService.getInstance(context).getStudentsCount() == 0) {
				mHandler.sendEmptyMessage(3);
				return;
			}
			String itemCode = DbService.getInstance(context).queryItemByMachineCode2(Constant.HEIGHT_WEIGHT + "").get(0)
					.getItemCode();
			studentItems = DbService.getInstance(context).queryStudentItemByCode(student.getStuCode(), itemCode);
			if (studentItems == null) {
				mHandler.sendEmptyMessage(2);
			} else {
				initOne();
			}

		} catch (Exception e) {
			log.error("身高体重读卡失败");
			e.printStackTrace();
		}

	}

	/**
	 * 写卡
	 * 
	 * @param intent
	 */
	private void writeCard(Intent intent) {

		try {
			int hResult1 = (int) (Double.parseDouble(etHeight.getText().toString()) * 10);
			int wResult1 = (int) (Double.parseDouble(etWeight.getText().toString()) * 1000);
			IItemService itemService = new NFCItemServiceImpl(intent);
			if (itemService.IC_ReadStuInfo().getStuCode().equals(tvNumber.getText().toString())) {
				IC_Result[] HWresult = new IC_Result[4];
				HWresult[0] = new IC_Result(hResult1, 1, 0, 0);// 身高1
				// HWresult[1] = new IC_Result(1710, 1, 0, 0);// 身高2
				HWresult[2] = new IC_Result(wResult1, 1, 0, 0);// 体重1
				IC_ItemResult HWItemResult = new IC_ItemResult(Constant.HEIGHT_WEIGHT, 0, 0, HWresult);
				boolean isHWResult = itemService.IC_WriteItemResult(HWItemResult);
				log.info("写入身高体重=>" + isHWResult + "身高：" + hResult1 + "体重：" + wResult1 + "，学生：" + student.toString());
				if (isHWResult) {
					tvShow1.setText("写卡完成");
					tvShow.setText("请刷卡");
				} else {
					Toast.makeText(this, "写卡出错", Toast.LENGTH_SHORT).show();
				}
			} else {
				NetUtil.showToast(context, "写卡失败，此卡非当前记录");
			}
		} catch (Exception e) {
			log.error("身高体重写卡失败");
		}

	}

	private void initOne() {
		etHeight.setText("");
		etWeight.setText("");
		tvShow1.setVisibility(View.GONE);
		btnCancel.setVisibility(View.GONE);
		btnSave.setVisibility(View.GONE);
		etHeight.setEnabled(true);
		etWeight.setEnabled(true);

		tvShow.setText("请输入");
		tvShow.setVisibility(View.VISIBLE);
	}

	/**
	 * 初始化View
	 */
	private void initView() {
		btnScan = (Button) findViewById(R.id.btn_hw_scanCode);
		tvHUnit = (TextView) findViewById(R.id.tv_h_unit);
		tvWUnit = (TextView) findViewById(R.id.tv_w_unit);
		tvLeft = (TextView) findViewById(R.id.tv_hw_height);
		tvRight = (TextView) findViewById(R.id.tv_hw_weight);
		tvTitle = (TextView) findViewById(R.id.tv_hw_title);
		tvNumber = (TextView) findViewById(R.id.tv_number_edit_HW);
		tvGender = (TextView) findViewById(R.id.tv_gender_edit_HW);
		tvName = (TextView) findViewById(R.id.tv_name_edit_HW);
		tvShow1 = (TextView) findViewById(R.id.tv_hw_show1);
		tvShow = (TextView) findViewById(R.id.tv_hw_show);
		etHeight = (EditText) findViewById(R.id.et_height_edit);
		etWeight = (EditText) findViewById(R.id.et_weight_edit);
		etHMax = (EditText) findViewById(R.id.et_h_max);
		etHMin = (EditText) findViewById(R.id.et_h_min);
		etWMax = (EditText) findViewById(R.id.et_w_max);
		etWMin = (EditText) findViewById(R.id.et_w_min);
		btnSave = (Button) findViewById(R.id.btn_hw_save);
		btnCancel = (Button) findViewById(R.id.btn_hw_cancel);
		ibQuit = (ImageButton) findViewById(R.id.ib_hw_quit);
		etHeight.setEnabled(false);
		etWeight.setEnabled(false);

		tvTitle.setText("身高体重");
		tvLeft.setText("身高");
		tvRight.setText("体重");
		tvHUnit.setText("厘米");
		tvWUnit.setText("千克");
		tvGender.setText("");
		tvName.setText("");
		etHeight.setText("");
		etWeight.setText("");
		etHMax.setText(hMax);
		etHMin.setText(hMin);
		etWMax.setText(wMax);
		etWMin.setText(wMin);
		btnCancel.setVisibility(View.GONE);
		btnSave.setVisibility(View.GONE);
		tvShow.setText("请刷卡");
		tvShow1.setVisibility(View.GONE);
		tvNumber.setText(stuData);
		if (readStyle == 1) {
			btnScan.setVisibility(View.VISIBLE);
			tvShow.setVisibility(View.GONE);
		}
		if ("".equals(tvNumber.getText().toString())) {
			stuData = "";
		} else {
			if (stuByCode.get(0).getSex() == 1) {
				sex = "男";
			} else {
				sex = "女";
			}
			tvName.setText(stuByCode.get(0).getStudentName());
			tvGender.setText(sex);
			etHeight.setEnabled(true);
			etWeight.setEnabled(true);
			btnScan.setVisibility(View.GONE);
			tvShow.setText("请输入");
			tvShow.setVisibility(View.VISIBLE);
			initOne();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 66 || keyCode == 135 || keyCode == 136) {
			Intent intent = new Intent(HeightAndWeightActivity.this, CaptureActivity.class);
			intent.putExtra("className", Constant.HEIGHT_WEIGHT + "");
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setListener() {
		btnScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HeightAndWeightActivity.this, CaptureActivity.class);
				intent.putExtra("className", Constant.HEIGHT_WEIGHT + "");
				startActivity(intent);
				finish();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				etHeight.setText("");
				etWeight.setText("");
				btnCancel.setVisibility(View.GONE);
				btnSave.setVisibility(View.GONE);
				tvShow.setText("请输入");
				tvShow.setVisibility(View.VISIBLE);
			}
		});

		ibQuit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		etWeight.addTextChangedListener(new TextWatcher() {

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
				if (tvNumber.getText().toString().isEmpty()) {
					tvShow.setVisibility(View.VISIBLE);
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
					Toast.makeText(context, "请先获取项目相关数据", Toast.LENGTH_SHORT).show();
					return;
				}
				if ("".equals(etHeight.getText().toString()) || "".equals(etWeight.getText().toString())) {
					Toast.makeText(HeightAndWeightActivity.this, "身高或体重为空", Toast.LENGTH_SHORT).show();
				} else {
					if (Double.parseDouble(etHeight.getText().toString()) > Double.parseDouble(hMax)
							|| Double.parseDouble(etHeight.getText().toString()) < Integer.parseInt(hMin)
							|| Double.parseDouble(etWeight.getText().toString()) > Double.parseDouble(wMax)
							|| Double.parseDouble(etWeight.getText().toString()) < Integer.parseInt(wMin)) {
						Toast.makeText(context, "不在输入范围，请重新输入", Toast.LENGTH_SHORT).show();
						etHeight.setText("");
						etWeight.setText("");
						return;
					}
					// String itemCode =
					// DbService.getInstance(context).queryItemByName("身高").getItemCode();
					// studentItems =
					// DbService.getInstance(context).queryStudentItemByCode(tvNumber.getText().toString(),
					// itemCode);
					if (studentItems == null) {
						Toast.makeText(context, "当前学生项目不存在", Toast.LENGTH_SHORT).show();
					} else {
						int hResult = (int) (Double.parseDouble(etHeight.getText().toString()) * 10);
						int wResult = (int) (Double.parseDouble(etWeight.getText().toString()) * 1000);
						// 保存身高体重
						// int flag1 = SaveDBUtil.saveGradesDB(context,
						// tvNumber.getText().toString(), hResult + "", 0,
						// Constant.HEIGHT_WEIGHT + "", "身高");
						// int flag2 = SaveDBUtil.saveGradesDB(context,
						// tvNumber.getText().toString(), wResult + "", 0,
						// Constant.HEIGHT_WEIGHT + "", "体重");
						int currentSex;
						if (stuData != null && stuData.length() != 0) {
							currentSex = stuByCode.get(0).getSex();
						} else {
							currentSex = student.getSex();
						}
						int flag = SaveDBUtil.saveWhGrades(context, tvNumber.getText().toString(), hResult, wResult, 0,
								"E01", "E02", currentSex);

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
								tvShow1.setText("保存成功");
								tvShow.setVisibility(View.VISIBLE);
								tvShow.setText("请刷卡");
							} else {
								tvShow.setVisibility(View.GONE);
								tvShow1.setVisibility(View.GONE);
								etHeight.setText("");
								etWeight.setText("");
								Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
								btnCancel.setVisibility(View.GONE);
								btnSave.setVisibility(View.GONE);
								btnScan.setVisibility(View.VISIBLE);
							}
						} else {
							if (stuData.isEmpty()) {
								tvShow1.setText("保存失败");
								tvShow.setVisibility(View.VISIBLE);
								tvShow.setText("请刷卡");
							} else {
								tvShow1.setVisibility(View.GONE);
								Toast.makeText(context, "保存失败！", Toast.LENGTH_SHORT).show();
								btnCancel.setVisibility(View.GONE);
								btnSave.setVisibility(View.GONE);
								tvShow.setVisibility(View.GONE);
								btnScan.setVisibility(View.VISIBLE);
							}
						}
					}
				}
			}
		});

	}

}
