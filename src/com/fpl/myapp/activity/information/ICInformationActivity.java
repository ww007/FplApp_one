package com.fpl.myapp.activity.information;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.fpl.myapp2.R;
import com.alibaba.fastjson.JSON;
import com.fpl.myapp.activity.CaptureActivity;
import com.fpl.myapp.activity.SplashScreenActivity;
import com.fpl.myapp.adapter.ICInfoAdapter;
import com.fpl.myapp.base.NFCActivity;
import com.fpl.myapp.db.DbService;
import com.fpl.myapp.entity.ICInfo;
import com.fpl.myapp.entity.QueryHisResultInfo;
import com.fpl.myapp.entity.QueryResultInfo;
import com.fpl.myapp.util.Constant;
import com.fpl.myapp.util.HttpUtil;
import com.fpl.myapp.util.NetUtil;
import com.wnb.android.nfc.dataobject.entity.IC_ItemResult;
import com.wnb.android.nfc.dataobject.entity.ItemProperty;
import com.wnb.android.nfc.dataobject.entity.Student;
import com.wnb.android.nfc.dataobject.service.IItemService;
import com.wnb.android.nfc.dataobject.service.impl.NFCItemServiceImpl;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.System;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ww.greendao.dao.Item;

public class ICInformationActivity extends NFCActivity {
	private ICInfoAdapter mAdapter;
	private ArrayList<ICInfo> icInfos;
	private String sex;
	private TextView tvNumber;
	private TextView tvName;
	private TextView tvGender;
	private ListView lvIcInfo;
	private static TextView tvShow;
	private static Context context;
	public static Activity mActivity;

	public ArrayList<String> projects = new ArrayList<>();
	private ImageButton ibQuit;
	private Logger log = Logger.getLogger(ICInformationActivity.class);
	private TreeSet<Integer> treeSet;
	private int currentYear = 0;

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				tvShow.setText("读取中...");
				break;
			case 2:
				tvShow.setText("读取完毕");
				break;
			case 3:
				tvShow.setText("读取中断，获取部分数据");
				break;
			case 4:
				icInfos.clear();
				tvGender.setText("");
				tvName.setText("");
				tvNumber.setText("");
				llHisInfo.setVisibility(View.GONE);
				updateView2();
				NetUtil.showToast(context, "服务器连接异常,获取数据失败");
				break;
			case 5:
				icInfos.clear();
				tvGender.setText("");
				tvName.setText("");
				tvNumber.setText("");
				llHisInfo.setVisibility(View.GONE);
				updateView2();
				NetUtil.showToast(context, "查无此人");
				break;
			case 6:
				if (currentYear != 0) {
					tvYear1.setText((currentYear + 1) + "");
					tvYear2.setText(currentYear + "");
				}
				if (treeSet.size() == 1) {
					if (treeSet.first() == currentYear - 1) {
						btnYear4.setVisibility(View.GONE);
						btnYear3.setVisibility(View.GONE);
						tvYear5.setVisibility(View.GONE);
						tvYear4.setVisibility(View.GONE);
						tvYear3.setText(treeSet.first() + "");
					} else if (treeSet.first() == currentYear - 2) {
						btnYear4.setVisibility(View.GONE);
						btnYear2.setVisibility(View.GONE);
						tvYear4.setText(treeSet.first() + "");
						tvYear3.setText((treeSet.first() + 1) + "");
						tvYear5.setVisibility(View.GONE);
					} else {
						btnYear3.setVisibility(View.GONE);
						btnYear2.setVisibility(View.GONE);
						tvYear3.setVisibility(View.GONE);
						tvYear5.setText(treeSet.first() + "");
						tvYear4.setText((treeSet.first() + 1) + "");
					}
				} else if (treeSet.size() == 2) {
					if (treeSet.first() == currentYear - 2 && treeSet.last() == currentYear - 1) {
						btnYear4.setVisibility(View.GONE);
						tvYear5.setVisibility(View.GONE);
						tvYear3.setText(treeSet.last() + "");
						tvYear4.setText(treeSet.first() + "");
					} else if (treeSet.first() == currentYear - 3 && treeSet.last() == currentYear - 1) {
						btnYear3.setVisibility(View.GONE);
						tvYear3.setText(treeSet.last() + "");
						tvYear4.setText((treeSet.last() - 1) + "");
						tvYear5.setText(treeSet.first() + "");
					} else {
						btnYear2.setVisibility(View.GONE);
						tvYear3.setText((treeSet.last() + 1) + "");
						tvYear4.setText(treeSet.last() + "");
						tvYear5.setText(treeSet.first() + "");
					}

				} else {
					tvYear3.setText((treeSet.first() + 2) + "");
					tvYear4.setText((treeSet.first() + 1) + "");
					tvYear5.setText(treeSet.first() + "");
				}
				llHisInfo.setVisibility(View.VISIBLE);
				btnYear1.setBackgroundResource(R.drawable.sort_pressed);
				updateView();
				break;
			case 7:
				NetUtil.showToast(context, "请先设置服务器地址");
				break;
			case 8:
				llHisInfo.setVisibility(View.GONE);
				break;
			case 9:
				NetUtil.showToast(context, "查询失败，请先初始化数据");
				break;
			default:
				break;
			}
		};
	};
	private ICInfo icInfo1;
	private List<ItemProperty> properties;
	private SharedPreferences mSharedPreferences;
	private int readStyle;
	private Button btnScan;
	private List<QueryResultInfo> results = new ArrayList<>();
	private List<QueryHisResultInfo> hisResults = new ArrayList<>();
	private String hisResult;
	private String result;
	private ImageView ivQuery;
	private TextView tvTitle;
	private String number;
	private String ip;
	private Button btnYear1;
	private Button btnYear2;
	private Button btnYear4;
	private Button btnYear3;
	private LinearLayout llHisInfo;
	private ArrayList<ICInfo> icInfos2;
	private ArrayList<ICInfo> icInfos3;
	private ArrayList<ICInfo> icInfos4;
	private ICInfo hHisicInfo;
	private ICInfo wHisicInfo;
	private ArrayList<ICInfo> icInfos1;
	private TextView tvYear1;
	private TextView tvYear2;
	private TextView tvYear3;
	private TextView tvYear4;
	private TextView tvYear5;
	private String codeMessage;
	private String IMEI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_icinformation);
		context = this;
		mActivity = this;

		mSharedPreferences = getSharedPreferences("ipAddress", Activity.MODE_PRIVATE);

		// SharedPreferences获取保存的上传地址
		ip = mSharedPreferences.getString("ip", "0");
		number = mSharedPreferences.getString("number", "0");
		// 获取IMEI码
		IMEI = mSharedPreferences.getString("IMEI", "0");

		icInfo1 = new ICInfo();
		icInfos = new ArrayList<ICInfo>();

		Intent intent = getIntent();
		result = intent.getStringExtra("result");
		codeMessage = intent.getStringExtra("codeMessage");
		if (codeMessage != null) {
			sendForResult(context, codeMessage, "http://" + ip + ":" + number + Constant.GET_RESULT_FOR_STUCODE);
		}

		if (result != null) {
			Log.i("result========", result);
			try {
				results = JSON.parseArray(result, QueryResultInfo.class);
			} catch (Exception e) {
				mHandler.sendEmptyMessage(4);
			}

		}

		mSharedPreferences = getSharedPreferences("readStyles", Activity.MODE_PRIVATE);
		readStyle = mSharedPreferences.getInt("readStyle", 0);

		initView();
		setListener();
	}

	private void updateView2() {
		mAdapter = new ICInfoAdapter(this, icInfos);
		Log.i("icInfos1", icInfos1 + "");
		Log.i("icInfos2", icInfos2 + "");
		Log.i("icInfos3", icInfos3 + "");
		Log.i("icInfos4", icInfos4 + "");
		lvIcInfo.setAdapter(mAdapter);
	}

	@Override
	public void onNewIntent(final Intent intent) {
		readCard(intent);

	}

	private void readCard(Intent intent) {
		llHisInfo.setVisibility(View.GONE);
		NFCItemServiceImpl itemService;
		try {
			icInfos.clear();
			itemService = new NFCItemServiceImpl(intent);
			properties = itemService.IC_ReadAllProperties();
			mHandler.sendEmptyMessage(1);
			Log.i("properties==", properties.toString());
			Student student = itemService.IC_ReadStuInfo();
			Log.i("StudentTest===", student.toString());
			if (1 == student.getSex()) {
				sex = "男";
			} else {
				sex = "女";
			}

			tvGender.setText(sex);
			tvName.setText(student.getStuName().toString());
			tvNumber.setText(student.getStuCode().toString());
			for (ItemProperty itemProperty : properties) {
				int itemCode = itemProperty.getItemCode();
				switch (itemCode) {
				case 1:
					readHW(itemService);
					break;
				case 2:
					readCommon(itemService, Constant.VITAL_CAPACITY, " 毫升", "肺活量");
					break;
				case 3:
					readCommon(itemService, Constant.BROAD_JUMP, " 厘米", "立定跳远");
					break;
				case 4:
					readCommon(itemService, Constant.JUMP_HEIGHT, " 厘米", "摸高");
					break;
				case 5:
					readCommon(itemService, Constant.PUSH_UP, " 次", "俯卧撑");
					break;
				case 6:
					readCommon(itemService, Constant.SIT_UP, " 次", "仰卧起坐");
					break;
				case 7:
					readCommon(itemService, Constant.SIT_AND_REACH, " 厘米", "坐位体前屈");
					break;
				case 8:
					readCommon(itemService, Constant.ROPE_SKIPPING, " 次", "跳绳");
					break;
				case 9:
					readVision(itemService);
					break;
				case 10:
					readCommon(itemService, Constant.PULL_UP, " 次", "引体向上");
					break;
				case 11:
					readCommon(itemService, Constant.INFRARED_BALL, "cm", "实心球");
					break;
				case 12:
					readMiddleRun(itemService);
					break;
				case 13:
					readCommon(itemService, Constant.VOLLEYBALL, "ms", "排球");
					break;
				case 14:
					readCommon(itemService, Constant.BASKETBALL_SKILL, "ms", "篮球运球");
					break;
				case 15:
					Item item = DbService.getInstance(context).queryItemByMachineCode(Constant.SHUTTLE_RUN + "");
					if (item != null) {
						readCommon(itemService, Constant.SHUTTLE_RUN, "ms", item.getItemName());
					} else {
						readCommon(itemService, Constant.SHUTTLE_RUN, "ms", "折返跑");
					}
					break;
				case 16:
					readCommon(itemService, Constant.WALKING1500, "ms", "1500米健步走");
					break;
				case 17:
					readCommon(itemService, Constant.WALKING2000, "ms", "2000米健步走");
					break;
				case 18:
					readCommon(itemService, Constant.RUN50, "ms", "50米跑");
					break;
				case 19:
					readCommon(itemService, Constant.FOOTBALL_SKILL, "ms", "足球运球");
					break;
				case 20:
					readCommon(itemService, Constant.KICKING_SHUTTLECOCK, " 个", "踢毽子");
					break;
				case 21:
					readCommon(itemService, Constant.SWIM, " 毫秒", "游泳");
					break;

				default:
					break;
				}
			}
			updateAdapter();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void readCommon(NFCItemServiceImpl itemService, int code, String unit, String name) {
		IC_ItemResult itemResult;
		ICInfo icInfo = new ICInfo();
		try {
			itemResult = itemService.IC_ReadItemResult(code);
			mHandler.sendEmptyMessage(1);
			Log.d(name + "：", itemResult.toString());
			if (itemResult.getResult()[0].getResultFlag() != 1) {
				Log.i(code + "", name + "没有成绩");
				icInfo.setProjectTitle(name);
				icInfo.setProjectValue("");
				icInfos.add(icInfo);
			} else {
				icInfo.setProjectTitle(name);
				String result = "";
				if (name.contains("跑") || name.contains("篮球") || name.contains("足球") || name.contains("游泳")) {
					result = getRunTime(itemResult.getResult()[0].getResultVal());
				} else if (name.contains("坐位体前屈")) {
					if (itemResult.getResult()[0].getResultVal() == 0) {
						result = "0" + unit;
					} else {
						result = itemResult.getResult()[0].getResultVal() / 10.0 + unit;
					}
				} else {
					result = itemResult.getResult()[0].getResultVal() + unit;
				}
				icInfo.setProjectValue(result);
				icInfos.add(icInfo);
			}

		} catch (Exception e) {
			log.debug("此IC卡中没有" + name + "项目");
		}

	}

	private void updateAdapter() {
		// 获取数据
		if (icInfos.size() < properties.size()) {
			mHandler.sendEmptyMessage(3);
		} else {
			mHandler.sendEmptyMessage(2);
		}
		mAdapter = new ICInfoAdapter(this, icInfos);
		Log.i("icInfos", icInfos + "");
		lvIcInfo.setAdapter(mAdapter);
		// tvShow.setText("读取完毕!");
	}

	/**
	 * 读取视力
	 * 
	 * @param itemService
	 * @throws Exception
	 */
	private void readVision(NFCItemServiceImpl itemService) {
		// 读取视力
		IC_ItemResult itemResultVision;
		ICInfo icInfo = new ICInfo();
		try {
			itemResultVision = itemService.IC_ReadItemResult(Constant.VISION);
			mHandler.sendEmptyMessage(1);
			if (itemResultVision.getResult()[0].getResultFlag() != 1) {
				Log.i("", "视力没有数据");
				icInfo.setProjectTitle("左眼视力");
				icInfo.setProjectValue("");
				icInfo1.setProjectTitle("右眼视力");
				icInfo1.setProjectValue("");
				icInfos.add(icInfo);
				icInfos.add(icInfo1);
			} else {
				double left = itemResultVision.getResult()[0].getResultVal();
				double right = itemResultVision.getResult()[2].getResultVal();
				icInfo.setProjectTitle("左眼视力");
				icInfo.setProjectValue(left + "");
				icInfo1.setProjectTitle("右眼视力");
				icInfo1.setProjectValue(right + "");
				icInfos.add(icInfo);
				icInfos.add(icInfo1);
			}
		} catch (Exception e) {
			log.debug("此IC卡中没有视力项目");
			e.printStackTrace();
		}

	}

	/**
	 * 从IC卡读取中长跑成绩
	 * 
	 * @param itemService
	 * @throws Exception
	 */
	private void readMiddleRun(IItemService itemService) {
		IC_ItemResult itemResultMiddleRace;
		ICInfo icInfo = new ICInfo();
		try {
			itemResultMiddleRace = itemService.IC_ReadItemResult(Constant.MIDDLE_RACE);
			mHandler.sendEmptyMessage(1);
			Log.i("读取中长跑测试", itemResultMiddleRace.toString());
			if (sex.equals("女")) {
				if (itemResultMiddleRace.getResult()[0].getResultFlag() != 1) {
					Log.i("", "800米跑没有成绩");
					icInfo.setProjectTitle("800米跑");
					icInfo.setProjectValue("");
					icInfos.add(icInfo);
				} else {
					icInfo.setProjectTitle("800米跑");
					icInfo.setProjectValue(getRunTime(itemResultMiddleRace.getResult()[0].getResultVal()));
					icInfos.add(icInfo);
				}
			} else {
				if (itemResultMiddleRace.getResult()[0].getResultFlag() != 1) {
					Log.i("", "1000米跑没有成绩");
					icInfo.setProjectTitle("1000米跑");
					icInfo.setProjectValue("");
					icInfos.add(icInfo);
				} else {
					icInfo.setProjectTitle("1000米跑");
					icInfo.setProjectValue(getRunTime(itemResultMiddleRace.getResult()[0].getResultVal()));
					icInfos.add(icInfo);
				}
			}
		} catch (Exception e) {
			log.debug("此IC卡中没有中长跑项目");
			e.printStackTrace();
		}

	}

	/**
	 * 从IC卡读取身高体重
	 * 
	 * @param itemService
	 */
	private void readHW(IItemService itemService) {
		// 读取身高体重
		IC_ItemResult itemResultHW;
		ICInfo icInfo = new ICInfo();
		try {
			itemResultHW = itemService.IC_ReadItemResult(Constant.HEIGHT_WEIGHT);
			mHandler.sendEmptyMessage(1);
			Log.i("读取身高体重测试", itemResultHW.toString());
			if (itemResultHW.getResult()[0].getResultFlag() != 1) {
				Log.i("", "身高体重没有数据");
				icInfo.setProjectTitle("身高");
				icInfo.setProjectValue("");
				icInfo1.setProjectTitle("体重");
				icInfo1.setProjectValue("");
				icInfos.add(icInfo);
				icInfos.add(icInfo1);
			} else {
				double height = itemResultHW.getResult()[0].getResultVal();
				double weight = itemResultHW.getResult()[2].getResultVal();
				icInfo.setProjectTitle("身高");
				icInfo.setProjectValue(height / 10 + " 厘米");
				icInfo1.setProjectTitle("体重");
				icInfo1.setProjectValue(weight / 10 + " 千克");
				icInfos.add(icInfo);
				icInfos.add(icInfo1);
			}
		} catch (Exception e) {
			log.debug("此IC卡中没有身高体重项目");
			e.printStackTrace();
		}

	}

	private void initView() {
		tvShow = (TextView) findViewById(R.id.tv_icinfo_show);
		lvIcInfo = (ListView) findViewById(R.id.lv_icinfo);

		tvNumber = (TextView) findViewById(R.id.tv_icinfo_number_show);
		tvName = (TextView) findViewById(R.id.tv_icinfo_name_show);
		tvGender = (TextView) findViewById(R.id.tv_icinfo_gender_show);
		ibQuit = (ImageButton) findViewById(R.id.ib_quit);
		btnScan = (Button) findViewById(R.id.btn_icinfo_scan);
		ivQuery = (ImageView) findViewById(R.id.iv_query);
		tvTitle = (TextView) findViewById(R.id.tv_icinfo_title);

		btnYear1 = (Button) findViewById(R.id.btn_schollYear1);
		btnYear2 = (Button) findViewById(R.id.btn_schollYear2);
		btnYear3 = (Button) findViewById(R.id.btn_schollYear3);
		btnYear4 = (Button) findViewById(R.id.btn_schollYear4);
		llHisInfo = (LinearLayout) findViewById(R.id.ll_HisInfo);

		tvYear1 = (TextView) findViewById(R.id.tv_year1);
		tvYear2 = (TextView) findViewById(R.id.tv_year2);
		tvYear3 = (TextView) findViewById(R.id.tv_year3);
		tvYear4 = (TextView) findViewById(R.id.tv_year4);
		tvYear5 = (TextView) findViewById(R.id.tv_year5);

		llHisInfo.setVisibility(View.GONE);

		if (readStyle == 1) {
			btnScan.setVisibility(View.VISIBLE);
			tvShow.setVisibility(View.GONE);
			tvTitle.setText("联机查询");
		}

		updateView();
	}

	private void updateView() {
		if (result != null && !results.isEmpty()) {
			icInfos1 = new ArrayList<ICInfo>();
			Log.i("result=", result);
			ww.greendao.dao.Student studentByCode = DbService.getInstance(this)
					.queryStudentByCode(results.get(0).getStudentCode()).get(0);
			tvName.setText(studentByCode.getStudentName());
			tvNumber.setText(results.get(0).getStudentCode());
			if (studentByCode.getSex() == 1) {
				tvGender.setText("男");
			} else {
				tvGender.setText("女");
			}
			String hResult = "";
			String wResult = "";
			for (QueryResultInfo resultInfo : results) {
				ICInfo icInfo = new ICInfo();
				String itemName;
				try {
					itemName = DbService.getInstance(this).queryItemByCode(resultInfo.getItemCode()).getItemName();
				} catch (Exception e) {
					continue;
				}
				if (resultInfo.getItemCode().equals("E01")) {
					if (resultInfo.getStudentItemID() == 0) {
						hResult = "0";
					} else if (resultInfo.getStudentItemID() == -1) {
						hResult = "免测";
					} else {
						hResult = resultInfo.getLastResult() + "";
					}
				} else if (resultInfo.getItemCode().equals("E02")) {
					if (resultInfo.getStudentItemID() == 0) {
						wResult = "0";
					} else if (resultInfo.getStudentItemID() == -1) {
						wResult = "免测";
					} else {
						wResult = resultInfo.getLastResult() + "";
					}
				} else {
					icInfo.setProjectTitle(itemName);
					if (resultInfo.getStudentItemID() == 0) {
						icInfo.setProjectValue("");
					} else if (resultInfo.getStudentItemID() == -1) {
						icInfo.setProjectValue("免测");
					} else {
						if (itemName.contains("跑")) {
							icInfo.setProjectValue(getRunTime(resultInfo.getLastResult()));
						} else if (itemName.contains("篮球")) {
							icInfo.setProjectValue(getRunTime(resultInfo.getLastResult()));
						} else if (itemName.contains("足球")) {
							icInfo.setProjectValue(getRunTime(resultInfo.getLastResult()));
						} else if (itemName.contains("健步走")) {
							icInfo.setProjectValue(getRunTime(resultInfo.getLastResult()));
						} else if (itemName.contains("肺活量")) {
							icInfo.setProjectValue(resultInfo.getLastResult() + " 毫升");
						} else if (itemName.contains("跳远")) {
							icInfo.setProjectValue((double) resultInfo.getLastResult() / 10.0 + " 厘米");
						} else if (itemName.contains("摸高")) {
							icInfo.setProjectValue((double) resultInfo.getLastResult() / 10.0 + " 厘米");
						} else if (itemName.contains("实心球")) {
							icInfo.setProjectValue((double) resultInfo.getLastResult() / 10.0 + " 厘米");
						} else if (itemName.contains("跳远")) {
							icInfo.setProjectValue((double) resultInfo.getLastResult() / 10.0 + " 厘米");
						} else if (itemName.contains("坐位体前屈")) {
							icInfo.setProjectValue(resultInfo.getLastResult() / 10.0 + " 厘米");
						} else if (itemName.contains("俯卧撑")) {
							icInfo.setProjectValue(resultInfo.getLastResult() + " 个");
						} else if (itemName.contains("仰卧起坐")) {
							icInfo.setProjectValue(resultInfo.getLastResult() + " 个");
						} else if (itemName.contains("跳绳")) {
							icInfo.setProjectValue(resultInfo.getLastResult() + " 个");
						} else if (itemName.contains("引体向上")) {
							icInfo.setProjectValue(resultInfo.getLastResult() + " 个");
						} else if (itemName.contains("排球")) {
							icInfo.setProjectValue(resultInfo.getLastResult() + " 个");
						} else if (itemName.contains("毽子")) {
							icInfo.setProjectValue(resultInfo.getLastResult() + " 个");
						}
					}
					icInfos1.add(icInfo);
				}

			}
			if (!"".equals(hResult)) {
				ICInfo hicInfo = new ICInfo();
				ICInfo wicInfo = new ICInfo();
				hicInfo.setProjectTitle("身高");
				if (hResult.equals("0")) {
					hicInfo.setProjectValue("");
				} else if (hResult.equals("免测")) {
					hicInfo.setProjectValue("免测");
				} else {
					hicInfo.setProjectValue(Double.parseDouble(hResult) / 10 + " 厘米");
				}
				icInfos1.add(0, hicInfo);
				wicInfo.setProjectTitle("体重");
				if (wResult.equals("0")) {
					wicInfo.setProjectValue("");
				} else if (wResult.equals("免测")) {
					wicInfo.setProjectValue("免测");
				} else {
					wicInfo.setProjectValue(Double.parseDouble(wResult) / 10 + " 千克");
				}
				icInfos1.add(1, wicInfo);
			}
			if (hisResult != null && !hisResults.isEmpty()) {
				icInfos2 = new ArrayList<ICInfo>();
				icInfos3 = new ArrayList<ICInfo>();
				icInfos4 = new ArrayList<ICInfo>();
				String currentYear;
				if (results.isEmpty()) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
					Date date = new Date();
					currentYear = sdf.format(date);
					currentYear = Integer.parseInt(currentYear) - 1 + "";
				} else {
					currentYear = results.get(0).getStuYear().substring(0, 4);
				}
				for (QueryHisResultInfo hisResultInfo : hisResults) {

					if ((1 + Integer.parseInt(hisResultInfo.getStuYear().substring(0, 4))) == (Integer
							.parseInt(currentYear))) {
						if ((!hisResultInfo.getItemCode().equals("E01"))
								&& (!hisResultInfo.getItemCode().equals("E02"))) {
							icInfos2.add(setHisInfo(hisResultInfo));
						}
						if (hisResultInfo.getItemCode().equals(hisResults.get(hisResults.size() - 1).getItemCode())
								&& hHisicInfo != null) {
							icInfos2.add(0, hHisicInfo);
							icInfos2.add(1, wHisicInfo);
						}
					} else if ((2 + Integer.parseInt(hisResultInfo.getStuYear().substring(0, 4))) == (Integer
							.parseInt(currentYear))) {
						if ((!hisResultInfo.getItemCode().equals("E01"))
								&& (!hisResultInfo.getItemCode().equals("E02"))) {
							icInfos3.add(setHisInfo(hisResultInfo));
						}
						if (hisResultInfo.getItemCode().equals(hisResults.get(hisResults.size() - 1).getItemCode())
								&& hHisicInfo != null) {
							icInfos3.add(0, hHisicInfo);
							icInfos3.add(1, wHisicInfo);
						}
					} else if ((3 + Integer.parseInt(hisResultInfo.getStuYear().substring(0, 4))) == (Integer
							.parseInt(currentYear))) {
						if (hisResultInfo.getItemCode().equals("E01")) {
							icInfos4.add(0, setHisInfo(hisResultInfo));
						} else if (hisResultInfo.getItemCode().equals("E02")) {
							icInfos4.add(1, setHisInfo(hisResultInfo));
						} else {
							icInfos4.add(setHisInfo(hisResultInfo));
						}

					}
				}
			}
			icInfos.clear();
			icInfos.addAll(icInfos1);
			updateView2();
		} else if (result != null && result.equals("1")) {
			mHandler.sendEmptyMessage(4);
		} else if (result != null && result.equals("2")) {
			mHandler.sendEmptyMessage(5);
		} else if (result != null && result.equals("3")) {
			mHandler.sendEmptyMessage(7);
		}
	}

	private ICInfo setHisInfo(QueryHisResultInfo hisResultInfo) {
		String itemName = null;
		try {
			itemName = DbService.getInstance(this).queryItemByCode(hisResultInfo.getItemCode()).getItemName();
		} catch (Exception e) {
			mHandler.sendEmptyMessage(9);
		}
		ICInfo icInfo = new ICInfo();
		icInfo.setProjectTitle(itemName);
		if (hisResultInfo.getStudentItemID() == 0) {
			icInfo.setProjectValue("");
		} else if (hisResultInfo.getStudentItemID() == -1) {
			icInfo.setProjectValue("免测");
		} else {
			if (itemName.contains("跑")) {
				icInfo.setProjectValue(getRunTime(hisResultInfo.getLastResult()));
			} else if (itemName.contains("篮球")) {
				icInfo.setProjectValue(getRunTime(hisResultInfo.getLastResult()));
			} else if (itemName.contains("足球")) {
				icInfo.setProjectValue(getRunTime(hisResultInfo.getLastResult()));
			} else if (itemName.contains("健步走")) {
				icInfo.setProjectValue(getRunTime(hisResultInfo.getLastResult()));
			} else if (itemName.contains("肺活量")) {
				icInfo.setProjectValue(hisResultInfo.getLastResult() + " 毫升");
			} else if (itemName.contains("跳远")) {
				icInfo.setProjectValue((double) hisResultInfo.getLastResult() / 10.0 + " 厘米");
			} else if (itemName.contains("摸高")) {
				icInfo.setProjectValue((double) hisResultInfo.getLastResult() / 10.0 + " 厘米");
			} else if (itemName.contains("实心球")) {
				icInfo.setProjectValue((double) hisResultInfo.getLastResult() / 10.0 + " 厘米");
			} else if (itemName.contains("跳远")) {
				icInfo.setProjectValue((double) hisResultInfo.getLastResult() / 10.0 + " 厘米");
			} else if (itemName.contains("坐位体前屈")) {
				icInfo.setProjectValue(hisResultInfo.getLastResult() / 10.0 + " 厘米");
			} else if (itemName.contains("俯卧撑")) {
				icInfo.setProjectValue(hisResultInfo.getLastResult() + " 个");
			} else if (itemName.contains("仰卧起坐")) {
				icInfo.setProjectValue(hisResultInfo.getLastResult() + " 个");
			} else if (itemName.contains("跳绳")) {
				icInfo.setProjectValue(hisResultInfo.getLastResult() + " 个");
			} else if (itemName.contains("引体向上")) {
				icInfo.setProjectValue(hisResultInfo.getLastResult() + " 个");
			} else if (itemName.contains("排球")) {
				icInfo.setProjectValue(hisResultInfo.getLastResult() + " 个");
			} else if (itemName.contains("毽子")) {
				icInfo.setProjectValue(hisResultInfo.getLastResult() + " 个");
			} else if (itemName.contains("身高")) {
				icInfo.setProjectValue(Double.parseDouble(hisResultInfo.getLastResult() + "") / 10 + " 厘米");
			} else if (itemName.contains("体重")) {
				icInfo.setProjectValue(Double.parseDouble(hisResultInfo.getLastResult() + "") / 10 + " 千克");
			}
		}
		return icInfo;
	}

	private String getRunTime(int time) {
		int min = time / 1000 / 60;
		int s = time / 1000 % 60;
		int ms = (time - min * 60000 - s * 1000) / 10;
		String time2 = "";
		if (min == 0) {
			if (ms == 0) {
				time2 = s + "″";
			} else {
				time2 = s + "″" + ms;
			}
		} else {
			if (ms == 0) {
				time2 = min + "′" + s + "″";
			} else {
				time2 = min + "′" + s + "″" + ms;
			}
		}
		return time2;
	}

	/**
	 * 根据返回值判断接下来的操作
	 * 
	 * @param result
	 */
	protected void isWifiConnected(boolean result) {

		if (true == result) {
			Intent intent = new Intent(ICInformationActivity.this, CaptureActivity.class);
			intent.putExtra("className", "icinfo");
			startActivity(intent);
		} else {
			NetUtil.checkNetwork(this);
		}

	}

	private void setListener() {
		btnYear1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnYear1.setBackgroundResource(R.drawable.sort_pressed);
				btnYear2.setBackgroundResource(R.drawable.sort_unpressed);
				btnYear3.setBackgroundResource(R.drawable.sort_unpressed);
				btnYear4.setBackgroundResource(R.drawable.sort_unpressed);
				icInfos.clear();
				icInfos.addAll(icInfos1);
				mAdapter.notifyDataSetChanged();
			}
		});
		btnYear2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnYear1.setBackgroundResource(R.drawable.sort_unpressed);
				btnYear2.setBackgroundResource(R.drawable.sort_pressed);
				btnYear3.setBackgroundResource(R.drawable.sort_unpressed);
				btnYear4.setBackgroundResource(R.drawable.sort_unpressed);
				icInfos.clear();
				icInfos.addAll(icInfos2);
				mAdapter.notifyDataSetChanged();
			}
		});
		btnYear3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnYear1.setBackgroundResource(R.drawable.sort_unpressed);
				btnYear2.setBackgroundResource(R.drawable.sort_unpressed);
				btnYear3.setBackgroundResource(R.drawable.sort_pressed);
				btnYear4.setBackgroundResource(R.drawable.sort_unpressed);
				icInfos.clear();
				icInfos.addAll(icInfos3);
				mAdapter.notifyDataSetChanged();
			}
		});
		btnYear4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnYear1.setBackgroundResource(R.drawable.sort_unpressed);
				btnYear2.setBackgroundResource(R.drawable.sort_unpressed);
				btnYear3.setBackgroundResource(R.drawable.sort_unpressed);
				btnYear4.setBackgroundResource(R.drawable.sort_pressed);
				icInfos.clear();
				icInfos.addAll(icInfos4);
				mAdapter.notifyDataSetChanged();
			}
		});

		ivQuery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showAddDialog();
			}
		});

		btnScan.setOnClickListener(new OnClickListener() {
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
	}

	@SuppressLint("InflateParams")
	private void showAddDialog() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.query_dialog, null);
		final EditText etNumber = (EditText) textEntryView.findViewById(R.id.et_dialog_query);
		AlertDialog.Builder ad1 = new AlertDialog.Builder(ICInformationActivity.this);
		ad1.setTitle("联机查询:");
		ad1.setIcon(R.drawable.query);
		ad1.setView(textEntryView);
		ad1.setPositiveButton("查询", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int i) {
				if (etNumber.getText().toString().isEmpty()) {
					NetUtil.showToast(context, "考号为空");
				} else {
					if (ip.isEmpty() || number.isEmpty() || ip.equals("0") || number.equals("0")) {
						NetUtil.showToast(context, "请先设置服务器地址");
					} else {
						sendForResult(context, etNumber.getText().toString(),
								"http://" + ip + ":" + number + Constant.GET_RESULT_FOR_STUCODE);
					}
				}

			}
		});
		ad1.show();// 显示对话框

	}

	private void sendForResult(final Context context, final String stuCode, final String ipAddress) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> paramMap = new HashMap<>();
				paramMap.put("mac", IMEI);
				paramMap.put("stuCode", stuCode);
				OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder().connectTimeout(2, TimeUnit.SECONDS)
						.readTimeout(5, TimeUnit.SECONDS).build();
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(ipAddress).append(
						"?signature=" + HttpUtil.getSignatureVal(paramMap) + "&stuCode=" + stuCode + "&mac=" + IMEI);

				Log.i("stringBuilder=", stringBuilder.toString());
				Request request = new Request.Builder().url(stringBuilder.toString()).post(RequestBody.create(null, ""))
						.build();
				Call call = mOkHttpClient.newCall(request);
				call.enqueue(new Callback() {

					@Override
					public void onResponse(Call arg0, Response response) {
						try {
							result = response.body().string();
						} catch (IOException e1) {
							mHandler.sendEmptyMessage(4);
							e1.printStackTrace();
						}
						Log.i("result", result);
						if (result.isEmpty() || result.equals("[]")) {
							mHandler.sendEmptyMessage(5);
							return;
						} else {
							try {
								results = JSON.parseArray(result, QueryResultInfo.class);
								currentYear = Integer.parseInt(results.get(0).getStuYear().substring(0, 4));
							} catch (Exception e) {
								mHandler.sendEmptyMessage(5);
							}
						}
						sendForHisResult(context, stuCode,
								"http://" + ip + ":" + number + Constant.GET_HisRESULT_FOR_STUCODE);

					}

					@Override
					public void onFailure(Call arg0, IOException arg1) {
						mHandler.sendEmptyMessage(4);
					}
				});
			}
		}).start();
	}

	private void sendForHisResult(final Context context, final String stuCode, final String ipAddress) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> paramMap = new HashMap<>();
				paramMap.put("mac", IMEI);
				paramMap.put("stuCode", stuCode);
				OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder().connectTimeout(2, TimeUnit.SECONDS)
						.readTimeout(5, TimeUnit.SECONDS).build();
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(ipAddress).append(
						"?signature=" + HttpUtil.getSignatureVal(paramMap) + "&stuCode=" + stuCode + "&mac=" + IMEI);

				Log.i("stringBuilder=", stringBuilder.toString());
				Request request = new Request.Builder().url(stringBuilder.toString()).post(RequestBody.create(null, ""))
						.build();
				Call call = mOkHttpClient.newCall(request);
				call.enqueue(new Callback() {

					@Override
					public void onResponse(Call arg0, Response response) throws IOException {
						hisResult = response.body().string();
						Log.i("hisResult========", hisResult);
						try {
							hisResults = JSON.parseArray(hisResult, QueryHisResultInfo.class);

							// hisResults.clear();
							// QueryHisResultInfo result1 = new
							// QueryHisResultInfo();
							// QueryHisResultInfo result2 = new
							// QueryHisResultInfo();
							// QueryHisResultInfo result3 = new
							// QueryHisResultInfo();
							// QueryHisResultInfo result4 = new
							// QueryHisResultInfo();
							// QueryHisResultInfo result5 = new
							// QueryHisResultInfo();
							// result1.setIsCurYear("0");
							// result1.setItemCode("E03");
							// result1.setLastResult(2222);
							// result1.setResultState(0);
							// result1.setStudentCode("2015244115");
							// result1.setStudentItemID(12345);
							// result1.setStuYear("2013-2014学年");
							//
							// result2.setIsCurYear("0");
							// result2.setItemCode("E04");
							// result2.setLastResult(15123);
							// result2.setResultState(0);
							// result2.setStudentCode("2015244115");
							// result2.setStudentItemID(54321);
							// result2.setStuYear("2013-2014学年");
							//
							// result3.setIsCurYear("0");
							// result3.setItemCode("E05");
							// result3.setLastResult(100);
							// result3.setResultState(0);
							// result3.setStudentCode("2015244115");
							// result3.setStudentItemID(23235);
							// result3.setStuYear("2013-2014学年");
							//
							// result4.setIsCurYear("0");
							// result4.setItemCode("E03");
							// result4.setLastResult(3333);
							// result4.setResultState(0);
							// result4.setStudentCode("2015244115");
							// result4.setStudentItemID(53126);
							// result4.setStuYear("2012-2013学年");
							//
							// result5.setIsCurYear("0");
							// result5.setItemCode("E09");
							// result5.setLastResult(1234);
							// result5.setResultState(0);
							// result5.setStudentCode("2015244115");
							// result5.setStudentItemID(85621);
							// result5.setStuYear("2012-2013学年");
							//
							// hisResults.add(result1);
							// hisResults.add(result2);
							// hisResults.add(result3);
							// hisResults.add(result4);
							// hisResults.add(result5);
							Log.i("hisResults========", hisResults.toString());
							List<Integer> years = new ArrayList<>();
							for (QueryHisResultInfo hisResultInfo : hisResults) {
								years.add(Integer.parseInt(hisResultInfo.getStuYear().substring(0, 4)));
							}
							treeSet = new TreeSet<>(years);
							Log.i("treeSet", treeSet.toString());
						} catch (Exception e) {
						}
						if (hisResults.isEmpty() || hisResults == null || hisResults.equals("[]")) {
						} else {
							mHandler.sendEmptyMessage(6);
						}

					}

					@Override
					public void onFailure(Call arg0, IOException arg1) {
						mHandler.sendEmptyMessage(4);
					}
				});
			}
		}).start();
	}

	// @Override
	// protected void onStop() {
	// super.onStop();
	// if (!tvName.getText().toString().isEmpty()) {
	// finish();
	// }
	// }

}
