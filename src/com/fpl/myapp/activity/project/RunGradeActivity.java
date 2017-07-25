package com.fpl.myapp.activity.project;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.fpl.myapp2.R;
import com.fpl.myapp.adapter.ChengjiAdapter;
import com.fpl.myapp.base.NFCActivity;
import com.fpl.myapp.db.DbService;
import com.fpl.myapp.db.SaveDBUtil;
import com.fpl.myapp.entity.RunGrade;
import com.fpl.myapp.util.Constant;
import com.fpl.myapp.util.NetUtil;
import com.wnb.android.nfc.dataobject.entity.IC_ItemResult;
import com.wnb.android.nfc.dataobject.entity.IC_Result;
import com.wnb.android.nfc.dataobject.entity.Student;
import com.wnb.android.nfc.dataobject.service.IItemService;
import com.wnb.android.nfc.dataobject.service.impl.NFCItemServiceImpl;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class RunGradeActivity extends NFCActivity {

	private Bundle bd;
	private ArrayList<RunGrade> runGrades;
	private ChengjiAdapter adapter;
	private TextView tvTitle;
	private String title;
	private ListView lvGrade;
	private Button btnQuit;
	private Button btnSure;
	private String currentName;
	private int currentPosition;
	private Context context;

	private Logger log = Logger.getLogger(RunGradeActivity.class);
	private Student student = new Student();
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				NetUtil.showToast(context, "成绩还未写入IC卡中，请重新刷卡");
				break;

			default:
				break;
			}
		};
	};

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_run_grade);
		context = this;

		Intent intent = getIntent();
		bd = intent.getExtras();
		title = intent.getStringExtra("title");
		runGrades = (ArrayList<RunGrade>) bd.getSerializable("grades");
		log.info(title + "成绩：" + runGrades.toString());

		initView();
		setListener();

	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		writeCard(intent);
		// readCard(intent);
	}

	/**
	 * 读卡操作
	 * 
	 * @param intent
	 */
	private void readCard(Intent intent) {

		try {
			IItemService itemService = new NFCItemServiceImpl(intent);
			student = itemService.IC_ReadStuInfo();
			showView();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 写卡操作
	 * 
	 * @param intent
	 */
	private List<String> stuCodes = new ArrayList<>();
	private AlertDialog dialog;

	private void writeCard(Intent intent) {

		try {
			final NFCItemServiceImpl itemService = new NFCItemServiceImpl(intent);
			student = itemService.IC_ReadStuInfo();

			if (student == null) {
				NetUtil.showToast(context, "此卡无效");
				return;
			}
			stuCodes.clear();
			for (RunGrade runGrade : ChengjiAdapter.datas) {
				stuCodes.add(runGrade.getStuCode());
			}
			Log.i("stuCodes", stuCodes.toString());

			if (stuCodes.contains(student.getStuCode())) {
				for (int i = 0; i < ChengjiAdapter.datas.size(); i++) {
					final int j = i;
					if (student.getStuCode().equals(ChengjiAdapter.datas.get(i).getStuCode())) {
						dialog = new AlertDialog.Builder(RunGradeActivity.this).setTitle("当前IC卡已有成绩，是否覆盖？")
								.setMessage("温馨提示：此操作时请不要移开IC卡以防写卡失败！").setPositiveButton("否", null)
								.setNegativeButton("是", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										try {
											ChengjiAdapter.datas.get(j).setName("");
											ChengjiAdapter.datas.get(j).setSex(0);
											ChengjiAdapter.datas.get(j).setStuCode("");
											readAndWrite(itemService);
										} catch (Exception e) {
											log.error(title + "成绩还未写入IC卡");
											mHandler.sendEmptyMessage(1);
										}
									}
								}).show();
					}
				}
			} else {
				readAndWrite(itemService);
			}
		} catch (Exception e) {
			log.error(title + "写卡操作失败");
		}
	}

	private void readAndWrite(IItemService itemService) throws Exception {
		int code = 0;
		switch (title) {
		case "50米跑":
			code = Constant.RUN50;
			break;
		case "800/1000米跑":
			code = Constant.MIDDLE_RACE;
			break;
		case "50米x8往返跑":
			code = Constant.SHUTTLE_RUN;
			break;
		case "游泳":
			code = Constant.SWIM;
			break;
		case "篮球运球":
			code = Constant.BASKETBALL_SKILL;
			break;
		case "足球运球":
			code = Constant.FOOTBALL_SKILL;
			break;

		default:
			break;
		}

		String time = runGrades.get(currentPosition).getTime();
		int result1;
		if (title.contains("50米跑")) {
			result1 = Integer.parseInt(time.subSequence(0, 2).toString()) * 60 * 1000
					+ Integer.parseInt(time.subSequence(3, 5).toString()) * 1000
					+ Integer.parseInt(time.substring(6, 8).toString()) * 10;
		} else {
			result1 = Integer.parseInt(time.subSequence(0, 2).toString()) * 60 * 1000
					+ Integer.parseInt(time.subSequence(3, 5).toString()) * 1000;
		}
		IC_Result[] result = new IC_Result[4];
		result[0] = new IC_Result(result1, 1, 0, 0);// 成绩1
		IC_ItemResult ItemResult = new IC_ItemResult(code, 0, 0, result);
		boolean isResult = itemService.IC_WriteItemResult(ItemResult);
		log.info(code + "写卡=>" + isResult + "成绩：" + result1 + "，学生：" + student.toString());
		showView();
	}

	private void showView() {
		currentName = student.getStuName();
		log.info("当前读卡位置=>" + lvGrade.getFirstVisiblePosition() + "=>" + student.toString());
		updateView(currentPosition, student);
		if (lvGrade.getLastVisiblePosition() == currentPosition+1) {
			lvGrade.setSelection(lvGrade.getFirstVisiblePosition() + 6);
		}
		currentPosition++;
		lvGrade.getLastVisiblePosition();
		adapter.setSelectItem(currentPosition);
		adapter.notifyDataSetInvalidated();
	}

	/**
	 * 更新当前listView显示部分信息
	 * 
	 * @param itemIndex
	 * @param student
	 */
	private void updateView(int itemIndex, Student student) {
		runGrades.get(itemIndex).setName(currentName);
		currentName = "";
		// 得到第一个可显示控件的位置
		int visiblePosition = lvGrade.getFirstVisiblePosition();
		// 只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
		if (itemIndex - visiblePosition >= 0) {
			// 得到要更新的item的view
			View view = lvGrade.getChildAt(itemIndex - visiblePosition);
			// 调用adapter更新界面
			adapter.updateView(view, itemIndex, student);
		}

	}

	private ArrayList<RunGrade> datas;
	private TextView tv;

	private void setListener() {
		lvGrade.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				currentPosition = position;
				adapter.setSelectItem(position);
				adapter.notifyDataSetInvalidated();
			}
		});

		btnSure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new AlertDialog.Builder(RunGradeActivity.this).setTitle("保存成绩").setMessage("成绩将被保存后退出，是否确认？")
						.setPositiveButton("取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub

							}
						}).setNegativeButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								datas = ChengjiAdapter.datas;
								// if
								// (DbService.getInstance(context).getStudentItemsCount()
								// == 0) {
								// NetUtil.showToast(context, "相关数据未下载，不能保存");
								// return;
								// }
								if (title.equals("50米跑")) {
									for (RunGrade runGrade : datas) {
										if (!runGrade.getName().isEmpty()) {
											int result = Integer.parseInt(
													runGrade.getTime().subSequence(0, 2).toString()) * 60 * 1000
													+ Integer.parseInt(runGrade.getTime().subSequence(3, 5).toString())
															* 1000
													+ Integer.parseInt(runGrade.getTime().substring(6, 8).toString())
															* 10;
											SaveDBUtil.saveGradesDB(context, runGrade.getStuCode(), result + "", 0,
													Constant.RUN50 + "", "50米跑");
										}
									}

								} else if (title.equals("800/1000米跑")) {
									for (RunGrade runGrade : datas) {
										if (!runGrade.getName().isEmpty()) {
											int result = Integer.parseInt(
													runGrade.getTime().subSequence(0, 2).toString()) * 60 * 1000
													+ Integer.parseInt(runGrade.getTime().subSequence(3, 5).toString())
															* 1000;
											String proName = null;
											if (runGrade.getSex() == 1) {
												proName = "1000米跑";
											} else {
												proName = "800米跑";
											}
											SaveDBUtil.saveGradesDB(context, runGrade.getStuCode(), result + "", 0,
													Constant.MIDDLE_RACE + "", proName);
										}
									}
								} else if (title.equals("50米x8往返跑")) {
									for (RunGrade runGrade : datas) {
										if (!runGrade.getName().isEmpty()) {
											int result = Integer.parseInt(
													runGrade.getTime().subSequence(0, 2).toString()) * 60 * 1000
													+ Integer.parseInt(runGrade.getTime().subSequence(3, 5).toString())
															* 1000;
											SaveDBUtil.saveGradesDB(context, runGrade.getStuCode(), result + "", 0,
													Constant.SHUTTLE_RUN + "", "50米x8往返跑");
										}
									}
								}
								finish();
							}
						}).show();
				// lvGrade.setEnabled(false);
				// btnSure.setEnabled(false);
			}
		});
		btnQuit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(RunGradeActivity.this).setTitle("确认").setMessage("成绩未保存，是否退出？")
						.setPositiveButton("取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub

							}
						}).setNegativeButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						}).show();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			new AlertDialog.Builder(RunGradeActivity.this).setTitle("确认").setMessage("成绩未保存，是否退出？")
					.setPositiveButton("取消", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					}).setNegativeButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					}).show();
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initView() {
		lvGrade = (ListView) findViewById(R.id.lv_run_grade);
		lvGrade.setChoiceMode(ListView.CHOICE_MODE_SINGLE);// 设置单选模式

		tvTitle = (TextView) findViewById(R.id.tv_title_runGrade);
		btnQuit = (Button) findViewById(R.id.btn_grade_quit);
		btnSure = (Button) findViewById(R.id.btn_grade_sure);
		tv = (TextView) findViewById(R.id.tv_runGrade);

		tvTitle.setText(title);
		adapter = new ChengjiAdapter(this, runGrades);
		lvGrade.setAdapter(adapter);
	}

}
