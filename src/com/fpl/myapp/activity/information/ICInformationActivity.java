package com.fpl.myapp.activity.information;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

	public ArrayList<String> projects = new ArrayList<>();
	private ImageButton ibQuit;
	private Logger log = Logger.getLogger(ICInformationActivity.class);

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
				NetUtil.showToast(context, "服务器连接异常,获取数据失败");
				break;
			case 5:
				icInfos.clear();
				tvGender.setText("");
				tvName.setText("");
				tvNumber.setText("");
				updateView2();
				NetUtil.showToast(context, "查无此人");
				break;
			case 6:
				updateView();
				break;
			case 7:
				NetUtil.showToast(context, "请先设置服务器地址");
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
	private String result;
	private ImageView ivQuery;
	private TextView tvTitle;
	private String number;
	private String ip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_icinformation);
		context = this;

		mSharedPreferences = getSharedPreferences("ipAddress", Activity.MODE_PRIVATE);
		// SharedPreferences获取保存的上传地址
		ip = mSharedPreferences.getString("ip", "");
		number = mSharedPreferences.getString("number", "");

		icInfo1 = new ICInfo();
		icInfos = new ArrayList<ICInfo>();

		Intent intent = getIntent();
		result = intent.getStringExtra("result");
		if (result != null) {
			Log.i("result========", result);
		}
		if (result != null) {
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
		Log.i("icInfos", icInfos + "");
		lvIcInfo.setAdapter(mAdapter);
	}

	@Override
	public void onNewIntent(final Intent intent) {
		readCard(intent);

	}

	private void readCard(Intent intent) {
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
					readCommon(itemService, Constant.VITAL_CAPACITY, "ml", "肺活量");
					break;
				case 3:
					readCommon(itemService, Constant.BROAD_JUMP, "cm", "立定跳远");
					break;
				case 4:
					readCommon(itemService, Constant.JUMP_HEIGHT, "cm", "摸高");
					break;
				case 5:
					readCommon(itemService, Constant.PUSH_UP, "个", "俯卧撑");
					break;
				case 6:
					readCommon(itemService, Constant.SIT_UP, "个", "仰卧起坐");
					break;
				case 7:
					readCommon(itemService, Constant.SIT_AND_REACH, "mm", "坐位体前屈");
					break;
				case 8:
					readCommon(itemService, Constant.ROPE_SKIPPING, "个", "跳绳");
					break;
				case 9:
					readVision(itemService);
					break;
				case 10:
					readCommon(itemService, Constant.PULL_UP, "个", "引体向上");
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
					readCommon(itemService, Constant.SHUTTLE_RUN, "ms", "折返跑");
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
					readCommon(itemService, Constant.KICKING_SHUTTLECOCK, "个", "踢毽子");
					break;
				case 21:
					readCommon(itemService, Constant.SWIM, "ms", "游泳");
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
				if (name.contains("立定跳远")) {
					result = (Double) (itemResult.getResult()[0].getResultVal() / 10.0) + unit;
				} else if (name.contains("跑") || name.contains("篮球") || name.contains("足球") || name.contains("游泳")) {
					result = getRunTime(itemResult.getResult()[0].getResultVal());
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
					icInfo.setProjectValue(itemResultMiddleRace.getResult()[0].getResultVal() + " ms");
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
					icInfo.setProjectValue(itemResultMiddleRace.getResult()[0].getResultVal() + " ms");
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
				icInfo.setProjectValue(height / 10 + " cm");
				icInfo1.setProjectTitle("体重");
				icInfo1.setProjectValue(weight / 1000 + " kg");
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

		if (readStyle == 1) {
			btnScan.setVisibility(View.VISIBLE);
			tvShow.setVisibility(View.GONE);
			tvTitle.setText("联机查询");
		}

		updateView();
	}

	private void updateView() {
		if (result != null && !results.isEmpty()) {
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
						hResult = "未测";
					} else {
						hResult = resultInfo.getLastResult() + "";
					}
				} else if (resultInfo.getItemCode().equals("E02")) {
					if (resultInfo.getStudentItemID() == 0) {
						wResult = "未测";
					} else {
						wResult = resultInfo.getLastResult() + "";
					}
				} else {
					icInfo.setProjectTitle(itemName);
					if (resultInfo.getStudentItemID() == 0) {
						icInfo.setProjectValue("未测");
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
							icInfo.setProjectValue(resultInfo.getLastResult() + " ml");
						} else if (itemName.contains("跳远")) {
							icInfo.setProjectValue((double) resultInfo.getLastResult() / 10.0 + " cm");
						} else if (itemName.contains("摸高")) {
							icInfo.setProjectValue((double) resultInfo.getLastResult() / 10.0 + " cm");
						} else if (itemName.contains("实心球")) {
							icInfo.setProjectValue((double) resultInfo.getLastResult() / 10.0 + " cm");
						} else if (itemName.contains("跳远")) {
							icInfo.setProjectValue((double) resultInfo.getLastResult() / 10.0 + " cm");
						} else if (itemName.contains("坐位体前屈")) {
							icInfo.setProjectValue(resultInfo.getLastResult() + " mm");
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
					icInfos.add(icInfo);
				}

			}
			if (!"".equals(hResult)) {
				ICInfo hicInfo = new ICInfo();
				ICInfo wicInfo = new ICInfo();
				hicInfo.setProjectTitle("身高");
				hicInfo.setProjectValue(Double.parseDouble(hResult) / 10 + " cm");
				icInfos.add(hicInfo);
				wicInfo.setProjectTitle("体重");
				wicInfo.setProjectValue(Double.parseDouble(wResult) / 1000 + " kg");
				icInfos.add(wicInfo);
			}

			updateView2();
		} else if (result != null && result.equals("1")) {
			mHandler.sendEmptyMessage(4);
		} else if (result != null && result.equals("2")) {
			mHandler.sendEmptyMessage(5);
		} else if (result != null && result.equals("3")) {
			mHandler.sendEmptyMessage(7);
		}
	}

	private String getRunTime(int time) {
		int min = time / 1000 / 60;
		int s = time / 1000 % 60;
		int ms = time - min * 60000 - s * 1000;
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
			finish();
		} else {
			NetUtil.checkNetwork(this);
		}

	}

	private void setListener() {
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
					sendForResult(context, etNumber.getText().toString(), SplashScreenActivity.IMEI);
				}

			}
		});
		ad1.show();// 显示对话框

	}

	private void sendForResult(final Context context, final String stuCode, final String mac) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> paramMap = new HashMap<>();
				paramMap.put("mac", mac);
				paramMap.put("stuCode", stuCode);
				OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder().connectTimeout(2, TimeUnit.SECONDS)
						.readTimeout(5, TimeUnit.SECONDS).build();
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("http://" + ip + ":" + number + Constant.GET_RESULT_FOR_STUCODE).append(
						"?signature=" + HttpUtil.getSignatureVal(paramMap) + "&stuCode=" + stuCode + "&mac=" + mac);

				Log.i("stringBuilder=", stringBuilder.toString());
				Request request = new Request.Builder().url(stringBuilder.toString()).post(RequestBody.create(null, ""))
						.build();
				Call call = mOkHttpClient.newCall(request);
				call.enqueue(new Callback() {

					@Override
					public void onResponse(Call arg0, Response response) throws IOException {
						result = response.body().string();
						try {
							results = JSON.parseArray(result, QueryResultInfo.class);
						} catch (Exception e) {
							mHandler.sendEmptyMessage(4);
						}
						if (result.isEmpty() || result == null || result.equals("[]")) {
							mHandler.sendEmptyMessage(5);
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

	@Override
	protected void onStop() {
		super.onStop();
		if (!tvName.getText().toString().isEmpty()) {
			finish();
		}
	}

}
