package com.fpl.myapp.activity.online;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.fpl.myapp2.R;
import com.alibaba.fastjson.JSON;
import com.fpl.myapp.activity.SplashScreenActivity;
import com.fpl.myapp.db.DbService;
import com.fpl.myapp.entity.PH_RoundGround;
import com.fpl.myapp.entity.PH_WHRoundResul;
import com.fpl.myapp.ui.ArcProgressBar;
import com.fpl.myapp.util.Constant;
import com.fpl.myapp.util.HttpUtil;
import com.fpl.myapp.util.NetUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import de.greenrobot.dao.async.AsyncSession;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ww.greendao.dao.RoundResult;
import ww.greendao.dao.TotalRoundResult;
import ww.greendao.dao.TotalWhRoundResult;
import ww.greendao.dao.WhRoundResult;

public class OnlineActivity extends Activity {

	private static ArcProgressBar mArcProgressBar;
	private boolean result;
	private static Context context;
	private List<RoundResult> roundResults;
	private Logger log = Logger.getLogger(OnlineActivity.class);
	private String MACID;
	private String ip;
	private String number;
	private SharedPreferences mSharedPreferences;
	private String MACORIMEI;
	@SuppressLint("HandlerLeak")
	private static Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Log.i("进度更新", currentPage + "");
				mArcProgressBar.setProgress((int) ((100 * currentPage) / ((totalCount + totalWhCount) / 2000)));
				mArcProgressBar.setmArcText("正在发送中");
				mArcProgressBar.setProgressDesc("");
				break;
			case 5:
				Log.i("进度更新", currentPage + "");
				mArcProgressBar.setProgress(
						(int) ((100 * (currentPage + totalWhCount / 2000)) / ((totalCount + totalWhCount) / 2000)));
				mArcProgressBar.setmArcText("正在发送中");
				mArcProgressBar.setProgressDesc("");
				break;
			case 2:
				mArcProgressBar.setProgress(100);
				mArcProgressBar.setProgressDesc("发送完毕");
				mArcProgressBar.setmArcText("");
				break;
			case 3:
				NetUtil.showToast(context, "数据为空");
				break;
			case 4:
				NetUtil.showToast(context, "连接服务器异常");
				break;
			case 6:
				NetUtil.showToast(context, "上传失败");
				break;
			}
		}
	};
	// private int ONLYNUMBER;
	private ArrayList<PH_RoundGround> ph_RoundGrounds;
	private PH_RoundGround ph_RoundGround;
	private AsyncSession mAsyncSession;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online);
		context = this;
		mSharedPreferences = getSharedPreferences("ipAddress", Activity.MODE_PRIVATE);

		// SharedPreferences获取保存的上传地址
		ip = mSharedPreferences.getString("ip", "");
		number = mSharedPreferences.getString("number", "");
		Log.i("ip", ip);
		// 获取Android机IMEI号
		MACID = SplashScreenActivity.IMEI;

		MACORIMEI = MACID;

		// ONLYNUMBER = mSharedPreferences.getInt("macorimei", 0);

		initView();
		setListener();
	}

	private boolean shortPress = false;
	private ImageView ivReturn;
	private ImageView ivSend;
	private static long totalWhCount;
	private static long totalCount;

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_F1) {
			shortPress = false;
			showAddDialog();
			return true;
		}
		// else if (keyCode == KeyEvent.KEYCODE_F2) {
		// shortPress = false;
		// new
		// AlertDialog.Builder(this).setTitle("选择唯一标识").setIcon(android.R.drawable.ic_dialog_info)
		// .setSingleChoiceItems(new String[] { "IMEI", "MAC地址" }, ONLYNUMBER,
		// new DialogInterface.OnClickListener() {
		//
		// public void onClick(DialogInterface dialog, int which) {
		// switch (which) {
		// case 0:
		// MACORIMEI = MACID;
		// Log.d("IMEI=", MACORIMEI);
		// SharedPreferences.Editor editor1 = mSharedPreferences.edit();
		// editor1.putInt("macorimei", 0);
		// editor1.commit();
		// break;
		// case 1:
		// MACORIMEI = NetUtil.getLocalMacAddressFromWifiInfo(context);
		// Log.d("MAC=", MACORIMEI);
		// SharedPreferences.Editor editor2 = mSharedPreferences.edit();
		// editor2.putInt("macorimei", 1);
		// editor2.commit();
		// break;
		// default:
		// break;
		// }
		// }
		// })
		// .setNegativeButton("确定", null).show();
		// }
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
		// else if (keyCode == KeyEvent.KEYCODE_F2) {
		// if (event.getAction() == KeyEvent.ACTION_DOWN) {
		// event.startTracking();
		// if (event.getRepeatCount() == 0) {
		// shortPress = true;
		// }
		// return true;
		// }
		// }
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
		// else if (keyCode == KeyEvent.KEYCODE_F2) {
		// if (shortPress) {
		// } else {
		// }
		// shortPress = false;
		// return true;
		// }
		return super.onKeyUp(keyCode, event);
	}

	private void initView() {
		totalCount = DbService.getInstance(context).getRoundResultsCount();
		totalWhCount = DbService.getInstance(context).getWhCount();
		Log.i("成绩表总条数：", totalCount + "");
		mArcProgressBar = (ArcProgressBar) findViewById(R.id.progressBar);
		ivReturn = (ImageView) findViewById(R.id.iv_online_return);
		ivSend = (ImageView) findViewById(R.id.iv_online_send);

	}

	private void setListener() {
		ivReturn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ivSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new Thread() {
					public void run() {
						// 调用NetUtil中的网络判断方法
						result = NetUtil.netState(context);
						isWifiConnected(result);
					};
				}.run();

			}

		});

	}

	/**
	 * 自定义输入上传地址弹窗
	 */
	@SuppressLint("InflateParams")
	private void showAddDialog() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.dialog, null);
		final EditText editIP = (EditText) textEntryView.findViewById(R.id.et_IP);
		final EditText editNumber = (EditText) textEntryView.findViewById(R.id.et_number);
		editIP.setText(ip);
		editNumber.setText(number);
		AlertDialog.Builder ad1 = new AlertDialog.Builder(OnlineActivity.this);
		ad1.setTitle("输入上传地址:");
		ad1.setIcon(android.R.drawable.ic_dialog_info);
		ad1.setView(textEntryView);
		ad1.setPositiveButton("是", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int i) {
				ip = editIP.getText().toString();
				number = editNumber.getText().toString();

				// SharedPreferences保存输入的上传地址
				SharedPreferences.Editor editor = mSharedPreferences.edit();
				editor.putString("ip", ip);
				editor.putString("number", number);
				editor.commit();

			}
		});
		ad1.setNegativeButton("否", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int i) {

			}
		});
		ad1.show();// 显示对话框

	}

	/**
	 * 根据返回值判断接下来的操作
	 * 
	 * @param result
	 */
	protected void isWifiConnected(boolean result) {

		if (true == result) {
			if (ip.isEmpty() || number.isEmpty()) {
				Toast.makeText(context, "请先设置上传地址", Toast.LENGTH_SHORT).show();
				return;
			}
			time1 = System.currentTimeMillis();
			postWhRoundResultForPager(context, 0);
		} else {
			NetUtil.checkNetwork(this);
		}

	}

	private long time1;
	private long time2;
	private long useTime;
	private static int currentPage = 0;

	private int flag = 0;
	private List<TotalRoundResult> totalRoundResults = new ArrayList<>();
	private TotalRoundResult totalRoundResult;

	private void postRoundResultsForPage(final Context context, final int page) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				mAsyncSession = DbService.mDaoSession.startAsyncSession();
				Map<String, String> paramMap = new HashMap<>();
				paramMap.put("mac", MACORIMEI);
				currentPage = page;
				ph_RoundGrounds = new ArrayList<PH_RoundGround>();
				OkHttpClient okHttpClient = new OkHttpClient();
				MediaType JSONTYPE = MediaType.parse("application/json; charset=utf-8");
				roundResults = DbService.getInstance(context).getRoundResultForPage(page);
				if (roundResults.isEmpty() && page == 0) {
					handler.sendEmptyMessage(3);
					return;
				} else if (roundResults.size() != 2000) {
					currentPage = -1;
				}
				for (RoundResult roundResult : roundResults) {
					ph_RoundGround = new PH_RoundGround();
					totalRoundResult = new TotalRoundResult();
					String stuCode = roundResult.getStudentCode();
					String itemCode = roundResult.getItemCode();
					Integer result = roundResult.getResult();
					int round = roundResult.getRoundNo();
					String time = roundResult.getTestTime();
					int state = roundResult.getResultState();

					ph_RoundGround.setIsLastResult(0);
					ph_RoundGround.setItemCode(itemCode);
					ph_RoundGround.setMac(MACORIMEI);
					ph_RoundGround.setResult(result + "");
					ph_RoundGround.setResultState(state);
					ph_RoundGround.setRoundNo(round);
					ph_RoundGround.setStudentCode(stuCode);
					ph_RoundGround.setTestTime(time);
					ph_RoundGrounds.add(ph_RoundGround);

					totalRoundResult.setIsLastResult(0);
					totalRoundResult.setItemCode(itemCode);
					totalRoundResult.setMac(MACORIMEI);
					totalRoundResult.setRemark1(null);
					totalRoundResult.setRemark2(null);
					totalRoundResult.setResult(result);
					totalRoundResult.setResultState(state);
					totalRoundResult.setRoundNo(round);
					totalRoundResult.setRoundResultID(null);
					totalRoundResult.setStudentCode(stuCode);
					totalRoundResult.setStudentItemID(roundResult.getStudentItemID());
					totalRoundResult.setTestTime(time);
					totalRoundResults.add(totalRoundResult);

				}
				Log.i("ph_RoundGrounds.size()=", ph_RoundGrounds.size() + "");
				try {
					// json为String类型的json数据
					String jsonResult = JSON.toJSONString(ph_RoundGrounds);
					RequestBody requestBody = RequestBody.create(JSONTYPE, jsonResult);
					Log.i("requestBody", requestBody.toString());
					String url = "http://" + ip + ":" + number + Constant.ROUND_RESULT_SAVE_URL;
					final Request request = new Request.Builder()
							.url(url + "?signature=" + HttpUtil.getSignatureVal(paramMap) + "&mac=" + MACORIMEI)
							.post(requestBody).build();
					Log.i("url=", url + "?signature=" + HttpUtil.getSignatureVal(paramMap) + "&mac=" + MACORIMEI);

					Call call = okHttpClient.newCall(request);
					call.enqueue(new Callback() {

						@Override
						public void onResponse(Call arg0, Response response) throws IOException {
							String responseNo = response.body().string();
							Log.i("返回值", responseNo + "---" + currentPage);
							if (responseNo.equals("1")) {
								if (currentPage == -1) {
									log.info("上传完毕");
									time2 = System.currentTimeMillis();
									useTime = time2 - time1;
									log.info("上传用时：" + useTime + "ms");
									handler.sendEmptyMessage(2);
									showNotification(context);
									DbService.getInstance(context).deleteAllRoundResult();
									mAsyncSession.runInTx(new Runnable() {
										@Override
										public void run() {
											DbService.totalRoundResultDao.insertOrReplaceInTx(totalRoundResults);
											Log.i("totalRoundResults", totalRoundResults.size() + "");
										}
									});
									return;
								} else {
									currentPage++;
									handler.sendEmptyMessage(5);
									postRoundResultsForPage(context, currentPage);
								}
							} else {
								handler.sendEmptyMessage(6);
							}
						}

						@Override
						public void onFailure(Call arg0, IOException arg1) {
							log.error("上传失败");
							if (flag > 50) {
								handler.sendEmptyMessage(4);
								return;
							} else {
								postRoundResultsForPage(context, currentPage);
							}
							flag++;
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
					if (flag > 50) {
						handler.sendEmptyMessage(4);
						return;
					} else {
						postRoundResultsForPage(context, currentPage);
					}
					log.error("服务器连接中断");
					flag++;
				}
			}
		}).start();
	}

	private PH_WHRoundResul whRoundResul;
	private List<PH_WHRoundResul> whRoundResuls;
	private List<TotalWhRoundResult> totalWhRoundResults = new ArrayList<>();
	private TotalWhRoundResult totalWhRoundResul;

	private void postWhRoundResultForPager(final Context context, final int page) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				mAsyncSession = DbService.mDaoSession.startAsyncSession();
				Map<String, String> paramMap = new HashMap<>();
				paramMap.put("mac", MACORIMEI);
				ivSend.setClickable(false);
				currentPage = page;
				whRoundResuls = new ArrayList<>();
				OkHttpClient okHttpClient = new OkHttpClient();
				MediaType JSONTYPE = MediaType.parse("application/json; charset=utf-8");

				List<WhRoundResult> whRoundResults = DbService.getInstance(context).getWhRoundResultForPage(page);
				if (totalCount == 0 && totalWhCount == 0) {
					handler.sendEmptyMessage(3);
					return;
				} else if (whRoundResults.size() != 2000) {
					currentPage = -1;
				}
				for (WhRoundResult whRoundResult : whRoundResults) {
					whRoundResul = new PH_WHRoundResul();
					totalWhRoundResul = new TotalWhRoundResult();

					whRoundResul.setGradeCode("");
					whRoundResul.setHitemCode("E01");
					whRoundResul.setHresult(whRoundResult.getHResult());
					whRoundResul.setMac(MACORIMEI);
					whRoundResul.setResultState(0);
					whRoundResul.setRoundNo(whRoundResult.getRoundNo());
					whRoundResul.setSex(whRoundResult.getSex());
					whRoundResul.setStuCode(whRoundResult.getStudentCode());
					whRoundResul.setTestTime(whRoundResult.getTestTime());
					whRoundResul.setWitemCode("E02");
					whRoundResul.setWresult(whRoundResult.getWResult());
					whRoundResuls.add(whRoundResul);

					totalWhRoundResul.setGradeCode("");
					totalWhRoundResul.setHItemCode("E01");
					totalWhRoundResul.setHResult(whRoundResult.getHResult());
					totalWhRoundResul.setMac(MACORIMEI);
					totalWhRoundResul.setResultState(0);
					totalWhRoundResul.setRoundNo(whRoundResult.getRoundNo());
					totalWhRoundResul.setSex(whRoundResult.getSex());
					totalWhRoundResul.setGradeCode(whRoundResult.getStudentCode());
					totalWhRoundResul.setTestTime(whRoundResult.getTestTime());
					totalWhRoundResul.setWItemCode("E02");
					totalWhRoundResul.setWResult(whRoundResult.getWResult());
					totalWhRoundResults.add(totalWhRoundResul);

				}
				Log.i("whRoundResuls.size()=", whRoundResuls.size() + "");
				Log.i("totalWhRoundResults.size()=", totalWhRoundResults.size() + "");
				try {
					String jsonResult2 = JSON.toJSONString(whRoundResuls);
					RequestBody requestBody2 = RequestBody.create(JSONTYPE, jsonResult2);
					String url2 = "http://" + ip + ":" + number + Constant.ROUND_RESULT_SAVEWH_URL;
					Request request2 = new Request.Builder()
							.url(url2 + "?signature=" + HttpUtil.getSignatureVal(paramMap) + "&mac=" + MACORIMEI)
							.post(requestBody2).build();
					Log.i("---", url2 + "?signature=" + HttpUtil.getSignatureVal(paramMap) + "&mac=" + MACORIMEI);
					Call call = okHttpClient.newCall(request2);
					call.enqueue(new Callback() {

						@Override
						public void onResponse(Call arg0, Response response) throws IOException {
							String responseNo = response.body().string();
							Log.i("返回值", responseNo + "---" + currentPage);
							if (responseNo.equals("1")) {
								if (currentPage == -1) {
									if (totalCount != 0) {
										postRoundResultsForPage(context, 0);
									} else {
										handler.sendEmptyMessage(2);
									}
									Log.i("上传身高体重完成", "-------");
									DbService.getInstance(context).deleteAllWhRoundGround();
									mAsyncSession.runInTx(new Runnable() {
										@Override
										public void run() {
											DbService.totalWhRoundResultDao.insertOrReplaceInTx(totalWhRoundResults);
											Log.i("totalRoundResults", totalRoundResults.size() + "");
										}
									});
								} else {
									currentPage++;
									handler.sendEmptyMessage(1);
									postWhRoundResultForPager(context, currentPage);
								}
							} else {
								handler.sendEmptyMessage(6);
							}

						}

						@Override
						public void onFailure(Call arg0, IOException arg1) {
							if (flag > 10) {
								handler.sendEmptyMessage(4);
								return;
							} else {
								postWhRoundResultForPager(context, currentPage);
							}
							log.error("服务器连接中断");
							flag++;

						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					if (flag > 10) {
						handler.sendEmptyMessage(4);
						return;
					} else {
						postWhRoundResultForPager(context, currentPage);
					}
					log.error("服务器连接中断");
					flag++;
				}
			}
		}).start();

	}

	private void showNotification(Context context) {
		Notification.Builder builder = new Notification.Builder(context);
		builder.setSmallIcon(R.drawable.app);
		builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app));
		builder.setAutoCancel(true);
		builder.setContentTitle("MyApp通知");
		builder.setContentText("上传数据完成");
		builder.setDefaults(Notification.DEFAULT_SOUND);
		// 设置点击跳转
		Intent hangIntent = new Intent();
		// hangIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// hangIntent.setClass(context, MainActivity.class);
		// 如果描述的PendingIntent已经存在，则在产生新的Intent之前会先取消掉当前的
		PendingIntent hangPendingIntent = PendingIntent.getActivity(context, 0, hangIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		builder.setFullScreenIntent(hangPendingIntent, true);
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(2, builder.build());
	}

}
