package com.fpl.myapp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.fpl.myapp2.R;
import com.fpl.myapp.activity.help.HelpActivity;
import com.fpl.myapp.activity.information.ICInformationActivity;
import com.fpl.myapp.activity.manage.SystemManagementActivity;
import com.fpl.myapp.activity.online.OnlineActivity;
import com.fpl.myapp.db.DbService;
import com.fpl.myapp.db.SaveDBUtil;
import com.fpl.myapp.entity.PH_RoundGround;
import com.fpl.myapp.util.UpdateManager;
import com.wnb.android.nfc.dataobject.entity.ItemProperty;
import com.wnb.android.nfc.dataobject.service.IItemService;
import com.wnb.android.nfc.dataobject.service.impl.NFCItemServiceImpl;

import android.Manifest;
import android.R.integer;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import ww.greendao.dao.RoundResult;
import ww.greendao.dao.Student;
import ww.greendao.dao.StudentItem;
import ww.greendao.dao.WhRoundResult;
import android.widget.AdapterView.OnItemClickListener;
import de.greenrobot.dao.async.AsyncSession;

public class MainActivity extends Activity {
	private int[] icon = { R.drawable.main_projects_selector, R.drawable.main_online_selector,
			R.drawable.main_iccard_selector, R.drawable.main_manager_selector, R.drawable.main_help_selector,
			R.drawable.main_quit_selector };
	private String[] iconname = { "项目选择", "计算机联机", "成绩查询", "系统管理", "帮助", "退出" };
	private GridView gvMain;
	private ArrayList<Map<String, Object>> dataList;
	private SimpleAdapter simAdapter;
	private int[] h = { 1750, 1600, 1650, 1660, 1645, 1755, 1880, 1715, 1695, 1900 };
	private int[] w = { 65000, 55000, 50000, 66000, 56500, 60500, 70000, 61500, 50500, 51000 };
	private String[] fhl = { "1000", "2000", "3000", "4000", "4500", "3500", "5000", "2510", "3545", "4400" };
	private String[] r50 = { "10000", "11100", "11200", "9000", "9500", "10500", "11500", "12510", "13545", "14400" };
	private String[] zwtqq = { "10", "111", "-100", "150", "-50", "-200", "300", "80", "50", "-90" };
	private String[] ywqz = { "10", "111", "100", "150", "50", "200", "300", "80", "30", "90" };
	private String[] ldty = { "1500", "1666", "1000", "2000", "3000", "1800", "3500", "3800", "3100", "3900" };
	private String[] ytxx = { "15", "16", "10", "20", "30", "18", "35", "38", "31", "39" };
	private String[] r1000 = { "60000", "70000", "80000", "110000", "130000", "180000", "350000", "380000", "310000",
			"390000" };
	private String[] r800 = { "60000", "70000", "80000", "110000", "130000", "180000", "350000", "380000", "310000",
			"390000" };
	private String testResult;
	// private String MACID;
	// private static AsyncSession mAsyncSession =
	// DbService.mDaoSession.startAsyncSession();
	private ArrayList<RoundResult> ph_RoundGrounds = new ArrayList<RoundResult>();
	private ArrayList<WhRoundResult> whRoundResults = new ArrayList<>();
	// public static String IMEI;
	private int REQUEST_PHONE_STATE = 0;
	public static Context context;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		setListeners();
		context = this;
		
		Intent intent = getIntent();
		int getNo = intent.getIntExtra("1", 0);
		if (getNo == 1) {
			Log.i("getNo=", getNo + "");
			SplashScreenActivity.mActivity.finish();
		}


		// if (ActivityCompat.checkSelfPermission(this,
		// Manifest.permission.READ_PHONE_STATE) !=
		// PackageManager.PERMISSION_GRANTED) {
		// ActivityCompat.requestPermissions(this, new String[] {
		// Manifest.permission.READ_PHONE_STATE },
		// REQUEST_PHONE_STATE);
		// } else {
		// TelephonyManager TelephonyMgr = (TelephonyManager)
		// getSystemService(TELEPHONY_SERVICE);
		// IMEI = TelephonyMgr.getDeviceId();
		// }
		//
		// Log.i("IMEI=", IMEI + "");
		// TelephonyManager tm = (TelephonyManager)
		// this.getSystemService(TELEPHONY_SERVICE);
		// MACID = tm.getDeviceId();

		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// for (int j = 0; j < 11; j++) {
		// List<Student> students =
		// DbService.getInstance(MainActivity.this).getStudentForPage(j);
		//
		// for (Student student : students) {
		// WhRoundResult whRoundResult = new WhRoundResult();
		// Random rand = new Random();
		// int num = rand.nextInt(10);
		// whRoundResult.setGradeCode(null);
		// whRoundResult.setHItemCode("E01");
		// whRoundResult.setHResult(h[num]);
		// whRoundResult.setMac(MACID);
		// whRoundResult.setResultState(0);
		// whRoundResult.setRoundNo(1);
		// whRoundResult.setSex(student.getSex());
		// whRoundResult.setStudentCode(student.getStudentCode());
		// whRoundResult.setTestTime("2017-05-23 10:00:00");
		// whRoundResult.setWItemCode("E02");
		// whRoundResult.setWResult(w[num]);
		// whRoundResults.add(whRoundResult);
		// }
		// }
		// mAsyncSession.runInTx(new Runnable() {
		// @Override
		// public void run() {
		// DbService.whRoundResultDao.insertOrReplaceInTx(whRoundResults);
		// Log.i("whRoundResults", whRoundResults.size() + "");
		// Log.i("----------", "保存身高体重完成");
		// }
		// });
		// for (int i = 0; i < 84; i++) {
		// List<StudentItem> studentItems =
		// DbService.getInstance(MainActivity.this).getStudentItemsForPage(i);
		// for (StudentItem studentItem : studentItems) {
		// Random rand = new Random();
		// int num = rand.nextInt(10);
		// RoundResult ph_RoundGround = new RoundResult();
		// String stuCode = studentItem.getStudentCode();
		// String itemCode = studentItem.getItemCode();
		// if (itemCode.equals("E02") || itemCode.equals("E01")) {
		// } else {
		// switch (itemCode) {
		// case "E03":
		// testResult = fhl[num];
		// break;
		// case "E04":
		// testResult = r50[num];
		// break;
		// case "E05":
		// testResult = zwtqq[num];
		// break;
		// case "E06":
		// testResult = ytxx[num];
		// break;
		// case "E07":
		// testResult = ywqz[num];
		// break;
		// case "E08":
		// testResult = r1000[num];
		// break;
		// case "E09":
		// testResult = ldty[num];
		// break;
		// case "E11":
		// testResult = ytxx[num];
		// break;
		// case "E12":
		// testResult = r1000[num];
		// break;
		// case "E13":
		// testResult = r800[num];
		// break;
		//
		// default:
		// break;
		// }
		// ph_RoundGround.setIsLastResult(0);
		// ph_RoundGround.setItemCode(itemCode);
		// ph_RoundGround.setMac(MACID);
		// ph_RoundGround.setResult(Integer.parseInt(testResult));
		// ph_RoundGround.setResultState(0);
		// ph_RoundGround.setRoundNo(1);
		// ph_RoundGround.setStudentCode(stuCode);
		// ph_RoundGround.setTestTime("2017-05-23 10:00:00");
		// ph_RoundGrounds.add(ph_RoundGround);
		//
		// }
		// Log.i("ph_RoundGrounds=", i + "---" + ph_RoundGrounds.size() +
		// "--------");
		//
		// }
		//
		// }
		// mAsyncSession.runInTx(new Runnable() {
		// @Override
		// public void run() {
		// DbService.roundResultDao.insertOrReplaceInTx(ph_RoundGrounds);
		// Log.i("ph_RoundGrounds", ph_RoundGrounds.size() + "");
		// Log.i("----------", "保存成绩完成");
		// }
		// });
		////
		// }
		// }).start();

	}

	// public void onRequestPermissionsResult(int requestCode, String[]
	// permissions, int[] grantResults) {
	// if (requestCode == REQUEST_PHONE_STATE && grantResults.length == 1
	// && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
	// TelephonyManager TelephonyMgr = (TelephonyManager)
	// getSystemService(TELEPHONY_SERVICE);
	// IMEI = TelephonyMgr.getDeviceId();
	// }
	// }

	private void initView() {
		gvMain = (GridView) findViewById(R.id.gv_main);
		// 新建list
		dataList = new ArrayList<Map<String, Object>>();
		// 获取数据
		getData();
		// 新建适配器
		String[] from = { "image", "text" };
		int[] to = { R.id.image, R.id.text };
		simAdapter = new SimpleAdapter(this, dataList, R.layout.item, from, to);
		// 配置适配器
		gvMain.setAdapter(simAdapter);

	}

	private void setListeners() {
		gvMain.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					startActivity(new Intent(MainActivity.this, ProjectSelectionActivity.class));
					break;
				case 1:
					startActivity(new Intent(MainActivity.this, OnlineActivity.class));
					break;
				case 2:
					startActivity(new Intent(MainActivity.this, ICInformationActivity.class));
					break;
				case 3:
					startActivity(new Intent(MainActivity.this, SystemManagementActivity.class));
					break;
				case 4:
					startActivity(new Intent(MainActivity.this, HelpActivity.class));
					break;
				case 5:
					finish();
					// close();
					break;
				default:
					break;
				}
			}

		});
	}

	/**
	 * 关机，需root权限
	 */
	private void close() {
		try {
			Log.v("", "root Runtime->shutdown");
			// Process proc =Runtime.getRuntime().exec(new
			// String[]{"su","-c","shutdown"}); //关机
			Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", "reboot -p" }); // 关机
			proc.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ADBShell adbShell = new ADBShell();
		adbShell.simulateKey(KeyEvent.KEYCODE_POWER);
		Instrumentation mInst = new Instrumentation();
		mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_UP);
	}

	public List<Map<String, Object>> getData() {

		for (int i = 0; i < icon.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", icon[i]);
			map.put("text", iconname[i]);
			dataList.add(map);
		}
		Log.i("dataList", dataList.toString());
		return dataList;
	}

}
