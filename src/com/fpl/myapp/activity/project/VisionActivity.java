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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ww.greendao.dao.Item;
import ww.greendao.dao.StudentItem;

/**
 * 视力
 * 
 * @author ww
 *
 */
public class VisionActivity extends NFCActivity {

	private TextView tvNumber;
	private TextView tvGender;
	private TextView tvName;
	private TextView tvShow1;
	private TextView tvShow;
	private EditText etMax;
	private EditText etMin;
	private Button btnSave;
	private Button btnCancel;
	private ImageButton ibQuit;
	private Student student;
	private String sex;
	private TextView tvHUnit;
	private TextView tvWUnit;
	private TextView tvTitle;
	private TextView tvLeft;
	private TextView tvRight;
	private EditText etLeft;
	private EditText etRight;
	private Context context;
	private String max;
	private String min;

	private Logger log = Logger.getLogger(VisionActivity.class);
	private LinearLayout llHW;
	private LinearLayout llHWVison;
	private SharedPreferences mSharedPreferences;
	private int readStyle;
	private String stuData;
	private List<ww.greendao.dao.Student> stuByCode;
	private Button btnScan;
	private StudentItem studentItems;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				NetUtil.showToast(context, "当前学生项目不存在");
				tvShow.setVisibility(View.GONE);
				btnScan.setVisibility(View.VISIBLE);
				etLeft.setEnabled(false);
				etRight.setEnabled(false);
				break;
			case 2:
				NetUtil.showToast(context, "当前学生项目不存在");
				tvShow.setVisibility(View.VISIBLE);
				tvShow.setText("请刷卡");
				etLeft.setEnabled(false);
				etRight.setEnabled(false);
				break;
			case 3:
				NetUtil.showToast(context, "请先下载相关数据");
				tvShow.setText("请刷卡");
				etLeft.setEnabled(false);
				etRight.setEnabled(false);
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
			if (stuByCode.isEmpty()) {
				Toast.makeText(context, "查无此人", Toast.LENGTH_SHORT).show();
				stuData = "";
			} else {
				String itemCode = DbService.getInstance(context).queryItemByMachineCode2(Constant.VISION + "").get(0)
						.getItemCode();
				studentItems = DbService.getInstance(context).queryStudentItemByCode(stuData, itemCode);
				if (studentItems == null) {
					mHandler.sendEmptyMessage(1);
				}
			}
		}

		Item items = DbService.getInstance(context).queryItemByName("视力");

		if (items == null) {
			max = "";
			min = "";
		} else {
			max = items.getMaxValue().toString();
			min = items.getMinValue().toString();
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
			int hResult1 = Integer.parseInt(etLeft.getText().toString());
			int wResult1 = Integer.parseInt(etRight.getText().toString());
			IItemService itemService = new NFCItemServiceImpl(intent);
			if (itemService.IC_ReadStuInfo().getStuCode().equals(tvNumber.getText().toString())) {
				IC_Result[] HWresult = new IC_Result[4];
				HWresult[0] = new IC_Result(hResult1, 1, 0, 0);// 左眼视力
				HWresult[2] = new IC_Result(wResult1, 1, 0, 0);// 右眼视力
				IC_ItemResult HWItemResult = new IC_ItemResult(Constant.VISION, 0, 0, HWresult);
				boolean isHWResult = itemService.IC_WriteItemResult(HWItemResult);
				log.info("写入视力成绩=>" + isHWResult + "左眼：" + hResult1 + "，右眼：" + wResult1 + "，学生：" + student.toString());
				if (isHWResult) {
					tvShow1.setText("成绩写卡完成");
					tvShow.setText("请刷卡");
				} else {
					Toast.makeText(this, "写卡出错", Toast.LENGTH_SHORT).show();
				}
			} else {
				NetUtil.showToast(context, "写卡失败，此卡非当前记录");
			}
		} catch (Exception e) {
			log.error("视力写卡失败");
		}
	}

	/**
	 * 读卡
	 */
	private void readCard(Intent intent) {
		try {
			IItemService itemService = new NFCItemServiceImpl(intent);
			student = itemService.IC_ReadStuInfo();
			log.info("视力读卡=>" + student.toString());
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
			String itemCode = DbService.getInstance(context).queryItemByMachineCode(Constant.VISION + "").getItemCode();
			studentItems = DbService.getInstance(context).queryStudentItemByCode(student.getStuCode(), itemCode);
			if (studentItems == null) {
				mHandler.sendEmptyMessage(2);
			} else {
				initOne();
			}

		} catch (Exception e) {
			log.error("视力读卡失败");
			e.printStackTrace();
		}

	}

	private void initOne() {
		etLeft.setText("");
		etRight.setText("");
		tvShow1.setVisibility(View.GONE);
		btnCancel.setVisibility(View.GONE);
		btnSave.setVisibility(View.GONE);
		etLeft.setEnabled(true);
		etRight.setEnabled(true);

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
		tvTitle = (TextView) findViewById(R.id.tv_hw_title);
		tvNumber = (TextView) findViewById(R.id.tv_number_edit_HW);
		tvGender = (TextView) findViewById(R.id.tv_gender_edit_HW);
		tvLeft = (TextView) findViewById(R.id.tv_hw_height);
		tvRight = (TextView) findViewById(R.id.tv_hw_weight);
		tvName = (TextView) findViewById(R.id.tv_name_edit_HW);
		tvShow1 = (TextView) findViewById(R.id.tv_hw_show1);
		tvShow = (TextView) findViewById(R.id.tv_hw_show);
		etLeft = (EditText) findViewById(R.id.et_height_edit);
		etRight = (EditText) findViewById(R.id.et_weight_edit);
		llHW = (LinearLayout) findViewById(R.id.ll_hw);
		llHWVison = (LinearLayout) findViewById(R.id.ll_hw_Vison);
		etMax = (EditText) findViewById(R.id.et_hw_max);
		etMin = (EditText) findViewById(R.id.et_hw_min);
		btnSave = (Button) findViewById(R.id.btn_hw_save);
		btnCancel = (Button) findViewById(R.id.btn_hw_cancel);
		ibQuit = (ImageButton) findViewById(R.id.ib_hw_quit);
		llHW.setVisibility(View.GONE);
		llHWVison.setVisibility(View.VISIBLE);
		etLeft.setEnabled(false);
		etRight.setEnabled(false);

		tvTitle.setText("视力");
		tvLeft.setText("左眼视力");
		tvRight.setText("右眼视力");
		tvHUnit.setText("");
		tvWUnit.setText("");
		tvGender.setText("");
		tvName.setText("");
		etLeft.setText("");
		etRight.setText("");
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
			etLeft.setEnabled(true);
			etRight.setEnabled(true);
			btnScan.setVisibility(View.GONE);
			tvShow.setText("请输入");
			tvShow.setVisibility(View.VISIBLE);
			initOne();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 66 || keyCode == 135 || keyCode == 136) {
			Intent intent = new Intent(VisionActivity.this, CaptureActivity.class);
			intent.putExtra("className", Constant.VISION + "");
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setListener() {
		btnScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(VisionActivity.this, CaptureActivity.class);
				intent.putExtra("className", Constant.VISION + "");
				startActivity(intent);
				finish();
			}
		});

		ibQuit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		etRight.addTextChangedListener(new TextWatcher() {

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
				tvShow.setVisibility(View.GONE);
				btnCancel.setVisibility(View.VISIBLE);
				btnSave.setVisibility(View.VISIBLE);
			}
		});

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (DbService.getInstance(context).loadAllItem().isEmpty()) {
					Toast.makeText(context, "请先获取项目相关数据", Toast.LENGTH_SHORT).show();
					return;
				}
				if ("".equals(etLeft.getText().toString()) || "".equals(etRight.getText().toString())) {
					Toast.makeText(VisionActivity.this, "视力为空", Toast.LENGTH_SHORT).show();
				} else {
					if (Double.parseDouble(etLeft.getText().toString()) > Double.parseDouble(max)
							|| Double.parseDouble(etLeft.getText().toString()) < Integer.parseInt(min)
							|| Double.parseDouble(etRight.getText().toString()) > Double.parseDouble(max)
							|| Double.parseDouble(etRight.getText().toString()) < Integer.parseInt(min)) {
						Toast.makeText(context, "不在输入范围，请重新输入", Toast.LENGTH_SHORT).show();
						etLeft.setText("");
						etRight.setText("");
						return;
					}

					// String itemCode =
					// DbService.getInstance(context).queryItemByMachineCode(Constant.VISION
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

					if (studentItems == null) {
						Toast.makeText(context, "当前学生项目不存在", Toast.LENGTH_SHORT).show();
					} else {
						int flag1 = SaveDBUtil.saveGradesDB(context, tvNumber.getText().toString(),
								etLeft.getText().toString(), 0, "" + Constant.VISION, "左眼视力");
						int flag2 = SaveDBUtil.saveGradesDB(context, tvNumber.getText().toString(),
								etRight.getText().toString(), 0, "" + Constant.VISION, "右眼视力");

						btnCancel.setVisibility(View.GONE);
						btnSave.setVisibility(View.GONE);
						tvShow1.setVisibility(View.VISIBLE);
						if (readStyle == 1) {
							tvShow.setVisibility(View.GONE);
						} else {
							tvShow.setVisibility(View.VISIBLE);
						}
						if (flag1 == 1 && flag2 == 1) {
							if (stuData.isEmpty()) {
								tvShow1.setText("保存成功");
								tvShow.setVisibility(View.VISIBLE);
								tvShow.setText("请刷卡");
							} else {
								tvShow.setVisibility(View.GONE);
								tvShow1.setVisibility(View.GONE);
								etLeft.setText("");
								etRight.setText("");
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
