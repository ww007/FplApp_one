package com.fpl.myapp.activity.project;

import java.util.List;

import org.apache.log4j.Logger;

import com.fpl.myapp2.R;
import com.fpl.myapp.activity.CaptureActivity;
import com.fpl.myapp.base.NFCActivity;
import com.fpl.myapp.db.DbService;
import com.fpl.myapp.db.SaveDBUtil;
import com.fpl.myapp.util.Constant;
import com.fpl.myapp.util.HttpUtil;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import ww.greendao.dao.Item;
import ww.greendao.dao.StudentItem;

public class BroadJumpActivity extends NFCActivity {
	private TextView tvInfoTitle;
	private TextView tvName;
	private TextView tvGender;
	private TextView tvShow1;
	private TextView tvShow;
	private EditText etChengji;
	private EditText etMax;
	private EditText etMin;
	private TextView tvInfoChengji;
	private TextView tvInfoUnit;
	private Button btnSave;
	private Button btnCancel;
	private ImageButton ibQuit;
	private Student student;
	private String sex;

	private Context context;
	private String max;
	private String min;
	private StudentItem studentItems;
	private Logger log = Logger.getLogger(BroadJumpActivity.class);
	private RadioGroup rg;
	private RadioButton rb0;
	private RadioButton rb1;
	private RadioButton rb2;
	private RadioButton rb3;
	private Button btnScan;
	private String stuData;
	private List<ww.greendao.dao.Student> stuByCode;
	private SharedPreferences mSharedPreferences;
	private int readStyle;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				NetUtil.showToast(context, "当前学生项目不存在");
				tvShow.setVisibility(View.GONE);
				btnScan.setVisibility(View.VISIBLE);
				rb0.setEnabled(false);
				rb1.setEnabled(false);
				rb2.setEnabled(false);
				rb3.setEnabled(false);
				etChengji.setEnabled(false);
				break;
			case 2:
				NetUtil.showToast(context, "当前学生项目不存在");
				tvShow.setVisibility(View.VISIBLE);
				tvShow.setText("请刷卡");
				rb0.setEnabled(false);
				rb1.setEnabled(false);
				rb2.setEnabled(false);
				rb3.setEnabled(false);
				etChengji.setEnabled(false);
				break;
			case 3:
				NetUtil.showToast(context, "当前卡片无此项目");
				rb0.setEnabled(false);
				rb1.setEnabled(false);
				rb2.setEnabled(false);
				rb3.setEnabled(false);
				tvShow.setText("请刷卡");
				etChengji.setEnabled(false);
				break;
			case 4:
				NetUtil.showToast(context, "条码识别不出，请手动输入");
				break;
			default:
				break;
			}
		};
	};
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

		mSharedPreferences = getSharedPreferences("readStyles", Activity.MODE_PRIVATE);
		readStyle = mSharedPreferences.getInt("readStyle", 0);

		stuData = getIntent().getStringExtra("data");
		if (stuData != null && stuData.length() != 0) {
			stuByCode = DbService.getInstance(context).queryStudentByCode(stuData);
			if (stuByCode.isEmpty()) {
				if (!stuData.equals("扫码时间过长")) {
					Toast.makeText(context, "查无此人", Toast.LENGTH_SHORT).show();
					stuData = "";
				}
			} else {
				String itemCode = DbService.getInstance(context).queryItemByMachineCode(Constant.BROAD_JUMP + "")
						.getItemCode();
				studentItems = DbService.getInstance(context).queryStudentItemByCode(stuData, itemCode);
				if (studentItems == null) {
					mHandler.sendEmptyMessage(1);
				}
			}
		}

		Item items = DbService.getInstance(context).queryItemByName("立定跳远");
		if (items == null) {
			max = "";
			min = "";
		} else {
			max = items.getMaxValue() / 10 + "";
			min = items.getMinValue() / 10 + "";
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

	/**
	 * 写卡
	 * 
	 * @param intent
	 */
	private void writeCard(Intent intent) {

		try {
			IItemService itemService = new NFCItemServiceImpl(intent);
			if (itemService.IC_ReadStuInfo().getStuCode().equals(tvNumber.getText().toString())) {
				String chengji = "";
				IC_Result[] resultBroadJump = new IC_Result[4];
				if (checkedBtn.equals("犯规") || checkedBtn.equals("免跳") || checkedBtn.equals("退出")) {
					chengji = "0";
				} else {
					chengji = etChengji.getText().toString();
				}
				int result1 = (int) (Double.parseDouble(chengji) * 10);
				resultBroadJump[0] = new IC_Result(result1, 1, 0, 0);
				IC_ItemResult ItemResultBroadJump = new IC_ItemResult(Constant.BROAD_JUMP, 0, 0, resultBroadJump);
				boolean isBroadJumpResult = itemService.IC_WriteItemResult(ItemResultBroadJump);
				log.info("写入立定跳远成绩=>" + isBroadJumpResult + "成绩：" + result1 + "，学生：" + student.toString());
				if (isBroadJumpResult) {
					tvShow1.setText("成绩写卡完成");
					tvShow.setText("请刷卡");
				} else {
					Toast.makeText(this, "写卡出错", Toast.LENGTH_SHORT).show();
				}
			} else {
				NetUtil.showToast(context, "写卡失败，此卡非当前记录");
			}
		} catch (Exception e) {
			log.error("立定跳远写卡失败");
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
			log.info("立定跳远读卡->" + student.toString());
			
			if (1 == student.getSex()) {
				sex = "男";
			} else {
				sex = "女";
			}
			tvGender.setText(sex);
			tvName.setText(student.getStuName().toString());
			tvNumber.setText(student.getStuCode().toString());
			
			item = itemService.IC_ReadItemResult(Constant.BROAD_JUMP);
			String itemResult = "";
			Log.i("item", item.getResult()[0].toString());
			

			if (item.getResult()[0].getResultVal() == 0) {
				itemResult = "";
				btnCancel.setVisibility(View.GONE);
				btnSave.setVisibility(View.GONE);
			} else {
				itemResult = item.getResult()[0].getResultVal() / 10.0 + "";
				btnCancel.setVisibility(View.VISIBLE);
				btnSave.setVisibility(View.VISIBLE);
			}

			etChengji.setText(itemResult);
			etChengji.setSelection(etChengji.getText().length());
			tvShow1.setVisibility(View.GONE);
			etChengji.setEnabled(true);
			rb0.setChecked(true);
			tvShow.setText("请输入成绩");
			tvShow.setVisibility(View.VISIBLE);
			// if (DbService.getInstance(context).loadAllItem().isEmpty()
			// || DbService.getInstance(context).getStudentsCount() == 0) {
			// mHandler.sendEmptyMessage(3);
			// return;
			// }
			// String itemCode =
			// DbService.getInstance(context).queryItemByMachineCode(Constant.BROAD_JUMP
			// + "")
			// .getItemCode();
			// studentItems =
			// DbService.getInstance(context).queryStudentItemByCode(student.getStuCode(),
			// itemCode);
			// if (studentItems == null) {
			// mHandler.sendEmptyMessage(2);
			// } else {
			// initOne();
			// }
		} catch (Exception e) {
			log.error("立定跳远读卡失败");
			mHandler.sendEmptyMessage(3);
			e.printStackTrace();
		}

	}

	private void initOne() {
		tvShow1.setVisibility(View.GONE);
		btnCancel.setVisibility(View.GONE);
		btnSave.setVisibility(View.GONE);
		etChengji.setEnabled(true);
		rb0.setChecked(true);

		tvShow.setText("请输入成绩");
		tvShow.setVisibility(View.VISIBLE);
	}

	private void initView() {
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
		etChengji = (EditText) findViewById(R.id.et_info_chengji);
		etMax = (EditText) findViewById(R.id.et_info_max);
		etMin = (EditText) findViewById(R.id.et_info_min);
		btnSave = (Button) findViewById(R.id.btn_info_save);
		btnCancel = (Button) findViewById(R.id.btn_info_cancel);
		ibQuit = (ImageButton) findViewById(R.id.ib_quit);
		rg = (RadioGroup) findViewById(R.id.radioGroup);
		rb0 = (RadioButton) findViewById(R.id.radio0);
		rb1 = (RadioButton) findViewById(R.id.radio1);
		rb2 = (RadioButton) findViewById(R.id.radio2);
		rb3 = (RadioButton) findViewById(R.id.radio3);

		tvInfoTitle.setText("立定跳远");
		tvInfoChengji.setText("立定跳远");
		tvInfoUnit.setText("厘米");

		tvName.setText("");
		tvGender.setText("");
		etChengji.setText("");
		etChengji.setEnabled(false);
		etMax.setText(max);
		etMin.setText(min);
		btnCancel.setVisibility(View.GONE);
		btnSave.setVisibility(View.GONE);
		tvShow.setText("请刷卡");
		tvShow1.setVisibility(View.GONE);
		tvNumber.setText(stuData);
		if (readStyle == 1) {
			btnScan.setVisibility(View.VISIBLE);
			tvShow.setVisibility(View.GONE);
		}
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
			etChengji.setEnabled(true);
			etChengji.setFocusable(true);
			btnScan.setVisibility(View.GONE);
			tvShow.setText("请输入成绩");
			tvShow.setVisibility(View.VISIBLE);
			initOne();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 66 || keyCode == 135 || keyCode == 136) {
			Intent intent = new Intent(BroadJumpActivity.this, CaptureActivity.class);
			intent.putExtra("className", Constant.BROAD_JUMP + "");
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

	private String checkedBtn = "正常";
	private String grade;
	private int resultState;

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
						etChengji.setEnabled(false);
					} else {
						rb0.setEnabled(true);
						rb1.setEnabled(true);
						rb2.setEnabled(true);
						rb3.setEnabled(true);
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
						etChengji.setEnabled(true);
						etChengji.setFocusable(true);
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
				Intent intent = new Intent(BroadJumpActivity.this, CaptureActivity.class);
				intent.putExtra("className", Constant.BROAD_JUMP + "");
				startActivity(intent);
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				etChengji.setText("");
				btnCancel.setVisibility(View.GONE);
				btnSave.setVisibility(View.GONE);
				tvShow.setText("请输入成绩");
				tvShow.setVisibility(View.VISIBLE);
				rb0.setChecked(true);
			}
		});

		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				btnCancel.setVisibility(View.VISIBLE);
				btnSave.setVisibility(View.VISIBLE);
				RadioButton radioButton = (RadioButton) BroadJumpActivity.this
						.findViewById(group.getCheckedRadioButtonId());
				checkedBtn = radioButton.getText().toString();
				if (checkedBtn.equals("正常")) {
					etChengji.setText("");
					etChengji.setTextColor(getResources().getColor(android.R.color.black));
					if (tvNumber.getText().toString().isEmpty()) {
						etChengji.setEnabled(false);
					} else {
						etChengji.setEnabled(true);
					}
				} else if (checkedBtn.equals("犯规")) {
					etChengji.setText("X");
					etChengji.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
					etChengji.setEnabled(false);
					etChengji.setTextSize(23);
					grade = "0";
				} else if (checkedBtn.equals("免跳")) {
					etChengji.setText("-");
					etChengji.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
					etChengji.setEnabled(false);
					etChengji.setTextSize(23);
					grade = "0";
				} else if (checkedBtn.equals("退出")) {
					etChengji.setText("r");
					etChengji.setTextColor(getResources().getColor(android.R.color.darker_gray));
					etChengji.setEnabled(false);
					etChengji.setTextSize(23);
					grade = "0";
				}
			}
		});

		ibQuit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (DbService.getInstance(context).loadAllItem().isEmpty()) {
					Toast.makeText(context, "请先初始化数据", Toast.LENGTH_SHORT).show();
					return;
				}
				if ("".equals(etChengji.getText().toString()) && checkedBtn.equals("正常")) {
					Toast.makeText(BroadJumpActivity.this, "成绩为空", Toast.LENGTH_SHORT).show();
					return;
				} else if (checkedBtn.equals("犯规")) {
					resultState = -1;
				} else if (checkedBtn.equals("免跳")) {
					resultState = -2;
				} else if (checkedBtn.equals("退出")) {
					resultState = -3;
				} else if (checkedBtn.equals("正常")) {
					if (Double.parseDouble(etChengji.getText().toString()) > Double.parseDouble(max)
							|| Double.parseDouble(etChengji.getText().toString()) < Double.parseDouble(min)) {
						Toast.makeText(context, "不在输入范围，请重新输入", Toast.LENGTH_SHORT).show();
						etChengji.setText("");
						return;
					}
					grade = etChengji.getText().toString().trim();

					List<ww.greendao.dao.Student> students = DbService.getInstance(context)
							.queryStudentByCode(tvNumber.getText().toString());
					if (students.isEmpty()) {
						int currentSex;
						if (tvGender.getText().toString().equals("男")) {
							currentSex = 1;
						} else {
							currentSex = 2;
						}
						ww.greendao.dao.Student currentStu = new ww.greendao.dao.Student(null,
								tvNumber.getText().toString(), tvName.getText().toString(), currentSex, null, null,
								null, null, HttpUtil.getCurrentTime(), null, null, null);
						DbService.getInstance(context).saveStudent(currentStu);
						// String itemCode = DbService.getInstance(context)
						// .queryItemByMachineCode(Constant.BROAD_JUMP +
						// "").getItemCode();
						// StudentItem studentItem = new StudentItem(null,
						// tvNumber.getText().toString(), itemCode, null, 0,
						// null, 0, null, null, null);
						// DbService.getInstance(context).saveStudentItem(studentItem);
						resultState = 0;
					} else {
						List<StudentItem> stuItems = DbService.getInstance(context)
								.queryStudentItemBystuCode(tvNumber.getText().toString());
						String itemCode = DbService.getInstance(context)
								.queryItemByMachineCode(Constant.BROAD_JUMP + "").getItemCode();
						studentItems = DbService.getInstance(context)
								.queryStudentItemByCode(tvNumber.getText().toString(), itemCode);
						if ((!stuItems.isEmpty()) && studentItems == null) {
							Toast.makeText(context, "当前学生项目不存在", Toast.LENGTH_SHORT).show();
							return;
						} else {
							resultState = 0;
						}
					}

				}

				Log.i("grade", Double.parseDouble(grade) * 10 + "");
				int flag = SaveDBUtil.saveGradesDB(context, tvNumber.getText().toString(),
						(int) (Double.parseDouble(grade) * 10) + "", resultState, Constant.BROAD_JUMP + "", "立定跳远");

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
						etChengji.setText("");
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

		etChengji.addTextChangedListener(new TextWatcher() {
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
				if (tvNumber.getText().toString().isEmpty() || etChengji.getText().toString().isEmpty()) {
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
	}
}
