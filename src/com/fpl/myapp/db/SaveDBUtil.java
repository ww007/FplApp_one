package com.fpl.myapp.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fpl.myapp2.R;
import com.fpl.myapp.activity.MainActivity;
import com.fpl.myapp.activity.SplashScreenActivity;
import com.fpl.myapp.entity.PH_Class;
import com.fpl.myapp.entity.PH_Grade;
import com.fpl.myapp.entity.PH_School;
import com.fpl.myapp.entity.PH_Student;
import com.fpl.myapp.entity.PH_StudentItem;
import com.fpl.myapp.util.HttpUtil;
import com.wnb.android.nfc.dataobject.entity.IC_Result;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.util.Log;
import de.greenrobot.dao.async.AsyncSession;
import ww.greendao.dao.Classes;
import ww.greendao.dao.Grade;
import ww.greendao.dao.RoundResult;
import ww.greendao.dao.School;
import ww.greendao.dao.Student;
import ww.greendao.dao.StudentItem;
import ww.greendao.dao.WhRoundResult;

public class SaveDBUtil {
	private static List<PH_School> schools = new ArrayList<>();
	private static List<PH_Grade> grades = new ArrayList<>();
	private static List<PH_Class> classes = new ArrayList<>();
	public static int over = 0;
	private static NotificationManager notificationManager;
	private static ArrayList<Student> mStudents = new ArrayList<>();
	private static AsyncSession mAsyncSession;
	private static ArrayList<StudentItem> mStudentItems;
	private static ArrayList<Classes> mClasses;
	private static ArrayList<Grade> mGrade;

	/**
	 * 数据库中查询所有成绩并存入IC卡
	 * 
	 * @param context
	 * @param roundResults
	 * @param result
	 */
	public static void queryRoundResults(List<RoundResult> roundResults, IC_Result[] result) {
		List<Integer> list = new ArrayList<>();
		for (RoundResult roundResult : roundResults) {
			list.add(roundResult.getResult());
		}
		// 将最大成绩保存到result[0]
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) == Collections.max(list)) {
				result[0] = new IC_Result(list.get(i), 1, 0, 0);
				list.remove(i);
			}
		}
		// 将除去最大成绩的其它成绩保存
		for (int i = 0; i < list.size(); i++) {
			result[i + 1] = new IC_Result(list.get(i), 1, 0, 0);
		}
	}

	/**
	 * 存储学校、班级、年级到数据库
	 * 
	 * @param response
	 * @param context
	 */
	public static void saveStudentDB(String response, Context context) {
		mAsyncSession = DbService.mDaoSession.startAsyncSession();
		mClasses = new ArrayList<Classes>();
		mGrade = new ArrayList<Grade>();
		// 解析获取的Json数据
		JSONObject jsonObject = JSON.parseObject(response);
		JSONArray jsonSchool = jsonObject.getJSONArray("school");
		JSONArray jsonClass = jsonObject.getJSONArray("class");
		JSONArray jsonGrade = jsonObject.getJSONArray("grade");
		schools = JSON.parseArray(jsonSchool.toJSONString(), PH_School.class);
		classes = JSON.parseArray(jsonClass.toJSONString(), PH_Class.class);
		grades = JSON.parseArray(jsonGrade.toJSONString(), PH_Grade.class);

		// 保存学校信息
		for (int i = 0; i < schools.size(); i++) {
			School school = new School(null, schools.get(i).getSchoolName(), schools.get(i).getSchoolYear(), null);
			DbService.getInstance(context).saveSchool(school);
		}
		Log.i("----------", "保存学校信息完成");
		// 保存年级信息
		for (int i = 0; i < grades.size(); i++) {
			String schoolName = grades.get(i).getSchoolName();
			String gradeCode = grades.get(i).getGradeCode();
			String gradeName = grades.get(i).getGradeName();
			Long SchoolID = DbService.getInstance(context).querySchoolByName(schoolName).getSchoolID();
			Grade grade = new Grade(null, SchoolID, gradeCode, gradeName, null);
			mGrade.add(grade);
		}
		DbService.gradeDao.insertOrReplaceInTx(mGrade);
		Log.i("mGrade", mGrade.size() + "");
		Log.i("----------", "保存年级信息完成");
		// mAsyncSession.runInTx(new Runnable() {
		// @Override
		// public void run() {
		// DbService.gradeDao.insertOrReplaceInTx(mGrade);
		// Log.i("mGrade", mGrade.size() + "");
		// Log.i("----------", "保存年级信息完成");
		// }
		// });
		// 保存班级信息
		for (int i = 0; i < classes.size(); i++) {
			String gradeCode = classes.get(i).getGradeCode();
			String classCode = classes.get(i).getClassCode();
			String className = classes.get(i).getClassName();
			long gradeID = DbService.getInstance(context).queryGradeByCode(gradeCode).getGradeID();
			Classes newClass = new Classes(null, gradeID, classCode, className, null);
			mClasses.add(newClass);
		}
		mAsyncSession.runInTx(new Runnable() {
			@Override
			public void run() {
				DbService.classesDao.insertOrReplaceInTx(mClasses);
				Log.i("mClasses", mClasses.size() + "");
				Log.i("----------", "保存班级信息完成");
			}
		});
	}

	/**
	 * 存储学生信息到数据库
	 * 
	 * @param context
	 * @param students
	 * @param context2
	 */
	public static void saveStudentPage(final Context context, final List<PH_Student> students) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mAsyncSession = DbService.mDaoSession.startAsyncSession();
				// 保存学生信息
				Log.i("students.size()---", students.size() + "");
				for (int i = 0; i < students.size(); i++) {
					String calssCode = students.get(i).getClassCode();
					String gradeCode = students.get(i).getGradeCode();
					Long classID = DbService.getInstance(context).queryClassesByCode(calssCode).getClassID();
					Long gradeID = DbService.getInstance(context).queryGradeByCode(gradeCode).getGradeID();
					Student student = new Student(null, students.get(i).getStudentCode(),
							students.get(i).getStudentName(), students.get(i).getSex(), classID, gradeID,
							students.get(i).getIDCardNo(), students.get(i).getICCardNo(),
							students.get(i).getDownloadTime(), null, null, null);
					mStudents.add(student);
				}
				mAsyncSession.runInTx(new Runnable() {
					@Override
					public void run() {
						DbService.studentDao.insertOrReplaceInTx(mStudents);
						Log.i("students", mStudents.size() + "");
						Log.i("----------", "保存学生信息完成");
					}
				});
			}
		}).start();

	}

	/**
	 * 存储学生项目信息到数据库
	 * 
	 * @param studentItems
	 *            获取到的学生项目信息
	 * @param context
	 * @param i
	 * @param j
	 * @param totalPage
	 */
	public static void saveStudentItemDB(final List<PH_StudentItem> studentItems, final Context context,
			final int totalPage, final int currentPage) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				mAsyncSession = DbService.mDaoSession.startAsyncSession();
				Log.i("studentItems", studentItems.size() + "");
				mStudentItems = new ArrayList<>();
				for (PH_StudentItem stuItem : studentItems) {
					String itemCode = stuItem.getItemCode();
					String studentCode = stuItem.getStudentCode();
					StudentItem studentItem = new StudentItem(null, studentCode, itemCode, null, 0, null, 0, null, null,
							null);
					mStudentItems.add(studentItem);
				}

				mAsyncSession.runInTx(new Runnable() {
					@Override
					public void run() {
						DbService.studentItemDao.insertOrReplaceInTx(mStudentItems);
						Log.i("currentPage----------", "保存学生项目信息完成");
						Log.i("mStudentItems", mStudentItems.size() + "");
						showNotification(context);
						if (context.getClass().equals(SplashScreenActivity.class)) {
							Intent intent = new Intent(context, MainActivity.class);
							intent.putExtra("1", 1);
							context.startActivity(intent);
						}
					}
				});

			}
		}).start();
	}

	/**
	 * 数据下载保存完后提示信息
	 * 
	 * @param context
	 */
	private static void showNotification(final Context context) {
		long hasTime = System.currentTimeMillis() - HttpUtil.startTime;
		Log.i("初始化用时：", hasTime + "");
		Notification.Builder builder = new Notification.Builder(context);
		builder.setSmallIcon(R.drawable.app);
		builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app));
		builder.setAutoCancel(true);
		builder.setContentTitle("MyApp通知");
		builder.setContentText("数据初始化完成");
		builder.setTicker("数据初始化完成,可以开始测试");
		builder.setDefaults(Notification.DEFAULT_SOUND);
		// 设置点击跳转
		Intent hangIntent = new Intent();
		// hangIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// hangIntent.setClass(context, MainActivity.class);
		// 如果描述的PendingIntent已经存在，则在产生新的Intent之前会先取消掉当前的
		PendingIntent hangPendingIntent = PendingIntent.getActivity(context, 0, hangIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		builder.setFullScreenIntent(hangPendingIntent, true);
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(2, builder.build());
	}

	/**
	 * 存储成绩到数据库
	 * 
	 * @param context
	 * @param stuCode
	 *            学生考号
	 * @param etChengji
	 *            成绩
	 * @param resultState
	 * @param code
	 *            项目机器代码
	 * @param tvTitle
	 *            项目名称
	 * @return
	 */
	public static int saveGradesDB(Context context, String stuCode, String etChengji, int resultState, String code,
			String tvTitle) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("ipAddress", Activity.MODE_PRIVATE);
		// 获取IMEI码
		String IMEI = mSharedPreferences.getString("IMEI", "0");
		int RoundNo = 1;
		String itemCode = "";
		if (tvTitle.equals("身高") || tvTitle.equals("体重") || tvTitle.equals("1000米跑") || tvTitle.equals("800米跑")
				|| tvTitle.equals("左眼视力") || tvTitle.equals("右眼视力")) {
			itemCode = DbService.getInstance(context).queryItemByName(tvTitle).getItemCode();
			// itemID =
			// DbService.getInstance(context).queryItemByName(tvTitle).getItemID();
		} else {
			itemCode = DbService.getInstance(context).queryItemByMachineCode(code).getItemCode();
			// itemID =
			// DbService.getInstance(context).queryItemByMachineCode(code).getItemID();
		}
		Long studentItemID;
		StudentItem studentItems = DbService.getInstance(context).queryStudentItemByCode(stuCode, itemCode);
		if (studentItems == null) {
			studentItemID = null;
		} else {
			studentItemID = studentItems.getStudentItemID();
		}
		// List<RoundResult> round =
		// DbService.getInstance(context).queryRoundResultByID(studentItemID);
		List<RoundResult> round = DbService.getInstance(context).queryRoundResultByCode(stuCode, itemCode);

		List<Integer> rounds = new ArrayList<>();

		if (round.size() != 0) {
			for (int i = 0; i < round.size(); i++) {
				rounds.add(round.get(i).getRoundNo());
			}
			Log.i("rounds", rounds.toString());
			RoundNo = Collections.max(rounds) + 1;
		}

		Integer currentResult = Integer.parseInt(etChengji);
		// 获取当前系统时间
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		Date curDate = new Date(java.lang.System.currentTimeMillis());// 获取当前时间
		String currentTime = formatter.format(curDate);
		Log.i("currentTime--->", currentTime);
		// 存储当前测试轮次
		// String macAddress = NetUtil.getLocalMacAddressFromWifiInfo(context);
		RoundResult roundResult = new RoundResult(null, studentItemID, stuCode, itemCode, currentResult, RoundNo,
				currentTime, resultState, 0, IMEI, null, null);
		DbService.getInstance(context).saveRoundResult(roundResult);

		return 1;

	}

	public static int saveWhGrades(Context context, String stuCode, int hResult, int wResult, int resultState,
			String hItemCode, String wItemCode, int sex) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("ipAddress", Activity.MODE_PRIVATE);
		// 获取IMEI码
		String IMEI = mSharedPreferences.getString("IMEI", "0");
		int RoundNo = 1;
		// StudentItem studentItems =
		// DbService.getInstance(context).queryStudentItemByCode(stuCode,
		// "E01");
		//
		// if (studentItems == null) {
		// return 0;
		// }
		List<WhRoundResult> round = DbService.getInstance(context).queryWhRoundResultByCode(stuCode);
		List<Integer> rounds = new ArrayList<>();

		if (round.size() != 0) {
			for (int i = 0; i < round.size(); i++) {
				rounds.add(round.get(i).getRoundNo());
			}
			Log.i("rounds", rounds.toString());
			RoundNo = Collections.max(rounds) + 1;
		}

		// 获取当前系统时间
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		Date curDate = new Date(java.lang.System.currentTimeMillis());// 获取当前时间
		String currentTime = formatter.format(curDate);
		Log.i("currentTime--->", currentTime);
		WhRoundResult whRoundResult = new WhRoundResult(null, stuCode, null, sex, hItemCode, wItemCode, hResult,
				wResult, RoundNo, currentTime, resultState, IMEI);
		// 存储当前测试轮次
		DbService.getInstance(context).saveWhRoundResult(whRoundResult);
		return 1;
	}
}
