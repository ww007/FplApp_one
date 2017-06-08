package com.fpl.myapp.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apaches.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.JSON;
import com.fpl.myapp.activity.MainActivity;
import com.fpl.myapp.activity.SplashScreenActivity;
import com.fpl.myapp.activity.information.ICInformationActivity;
import com.fpl.myapp.activity.manage.FragmentRight;
import com.fpl.myapp.activity.manage.SharedpreferencesActivity;
import com.fpl.myapp.db.DbService;
import com.fpl.myapp.db.SaveDBUtil;
import com.fpl.myapp.entity.First_Student;
import com.fpl.myapp.entity.First_StudentItem;
import com.fpl.myapp.entity.PH_Student;
import com.fpl.myapp.entity.PH_StudentItem;
import com.fpl.myapp.entity.Password;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ww.greendao.dao.Item;

public class HttpUtil {
	private static List<PH_StudentItem> studentItems = new ArrayList<>();
	private static List<Item> itemList;
	public static int okFlag;
	public static long startTime = 0;

	/**
	 * OKhttp发送请求
	 * 
	 * @param itemFlag
	 * 
	 * @param path
	 * @param params
	 * @param listener
	 */

	public static int sendOkhttp(final int itemFlag, final String path, final Map<String, String> params,
			final HttpCallbackListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 创建okHttpClient对象
				OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder().connectTimeout(5, TimeUnit.SECONDS)
						.readTimeout(100, TimeUnit.SECONDS).build();
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(path).append("?");
				try {
					if (params != null && params.size() != 0) {
						for (Map.Entry<String, String> entry : params.entrySet()) {
							// 转换成UTF-8
							stringBuilder.append(entry.getKey()).append("=")
									.append(URLEncoder.encode(entry.getValue(), "utf-8"));

							stringBuilder.append("&");
						}
					}
					// 连接signature
					stringBuilder.append("signature=" + getSignatureVal(params));
					Log.i("---------", stringBuilder.toString());
					// 创建一个Request
					Request request = new Request.Builder().url(stringBuilder.toString()).build();
					// Response response =
					// mOkHttpClient.newCall(request).execute();
					Call call = mOkHttpClient.newCall(request);
					call.enqueue(new Callback() {

						@Override
						public void onResponse(Call arg0, Response response) throws IOException {
							String result = response.body().string();
							Log.i("下载成功", result);
							okFlag = 1;
							listener.onFinish(result);
						}

						@Override
						public void onFailure(Call arg0, IOException arg1) {
							if (itemFlag == 1) {
								SplashScreenActivity.mHandle.sendEmptyMessage(1);
								Intent intent = new Intent(SplashScreenActivity.context, MainActivity.class);
								intent.putExtra("1", 1);
								SplashScreenActivity.context.startActivity(intent);
							} else {
								FragmentRight.mHandler.sendEmptyMessage(2);
							}

						}
					});
				} catch (UnsupportedEncodingException e) {
					if (itemFlag == 1) {
						SplashScreenActivity.mHandle.sendEmptyMessage(1);
						Intent intent = new Intent(SplashScreenActivity.context, MainActivity.class);
						intent.putExtra("1", 1);
						SplashScreenActivity.context.startActivity(intent);
					} else {
						FragmentRight.mHandler.sendEmptyMessage(2);
					}
					e.printStackTrace();
				}
			}
		}).start();
		return okFlag;

	}

	private static String result = "";

	public static void sendForResult(final String url1, final Context context, final String stuCode, final String mac) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> paramMap = new HashMap<>();
				paramMap.put("mac", mac);
				paramMap.put("stuCode", stuCode);
				OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder().connectTimeout(2, TimeUnit.SECONDS)
						.readTimeout(5, TimeUnit.SECONDS).build();
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(url1+Constant.GET_RESULT_FOR_STUCODE)
						.append("?signature=" + getSignatureVal(paramMap) + "&stuCode=" + stuCode + "&mac=" + mac);

				Log.i("stringBuilder=", stringBuilder.toString());
				Request request = new Request.Builder().url(stringBuilder.toString()).post(RequestBody.create(null, ""))
						.build();
				Call call = mOkHttpClient.newCall(request);
				call.enqueue(new Callback() {

					@Override
					public void onResponse(Call arg0, Response response) throws IOException {
						result = response.body().string();
						Log.i("result----------", result);
						Intent mIntent = new Intent(context, ICInformationActivity.class);
						if (result.isEmpty() || result == null || result.equals("[]")) {
							mIntent.putExtra("result", "2");
						} else {
							mIntent.putExtra("result", result);
						}
						context.startActivity(mIntent);

					}

					@Override
					public void onFailure(Call arg0, IOException arg1) {
						Intent mIntent = new Intent(context, ICInformationActivity.class);
						mIntent.putExtra("result", "1");
						context.startActivity(mIntent);
					}
				});
			}
		}).start();
	}

	/**
	 * MD5加密
	 * 
	 * @param paramMap
	 *            加密参数
	 * @param i
	 * @return
	 */
	public static String getSignatureVal(Map<String, String> paramMap) {
		try {
			StringBuilder stringBuilder = new StringBuilder();
			List<String> list = new ArrayList<String>();
			if (paramMap != null && paramMap.size() != 0) {
				for (Map.Entry<String, String> entry : paramMap.entrySet()) {
					list.add(entry.getKey());
					list.add(entry.getValue());
				}
			}
			// 字典排序
			Collections.sort(list);
			for (int i = 0; i < list.size(); i++) {
				stringBuilder.append(list.get(i));
			}
			stringBuilder.append(Constant.TOKEN);
			return HttpUtil.getMD5(stringBuilder.toString());
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	public static String getSignatureVal1(Map<String, String> paramMap, int pageNo) {
		try {
			StringBuilder stringBuilder = new StringBuilder();
			List<String> list = new ArrayList<String>();
			if (paramMap != null && paramMap.size() != 0) {
				for (Map.Entry<String, String> entry : paramMap.entrySet()) {
					list.add(entry.getKey());
					list.add(entry.getValue());
				}
			}
			list.add("pageNo");
			list.add(pageNo + "");
			// 字典排序
			Collections.sort(list);
			for (int i = 0; i < list.size(); i++) {
				stringBuilder.append(list.get(i));
			}
			stringBuilder.append(Constant.TOKEN);
			return HttpUtil.getMD5(stringBuilder.toString());
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	/**
	 * MD5加密算法
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String getMD5(String str) throws Exception {
		try {
			return DigestUtils.md5Hex(str.getBytes("UTF-8"));

		} catch (Exception e) {
			throw new Exception("MD5加密失败", e);
		}
	}

	private static int studentFlag;
	private static boolean StuItemFlag;

	/**
	 * 获取学校、班级、年级信息
	 * 
	 * @param context
	 * @param url1
	 * @return
	 */
	public static int getStudentInfo(final Context context, final String url1) {
		if (context.getClass().equals(SplashScreenActivity.class)) {
			itemFlag = 1;
		} else {
			itemFlag = 2;
		}
		Map<String, String> map = new HashMap<>();
		map.put("mac", SplashScreenActivity.IMEI);
		sendOkhttp(itemFlag, url1 + Constant.STUDENT_URL, map, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				SaveDBUtil.saveStudentDB(response, context);
				sendOkHttpForStudentPage(url1 + Constant.STUDENT_Page_URL, 1, context, url1);
			}

			@Override
			public void onError(Exception e) {
				Log.i("error", "下载学生信息失败");

			}
		});
		if (studentFlag == 1) {
			return 1;
		} else {
			return 0;
		}
	}

	private static int currentStuPage = 0;
	private static List<PH_Student> totalStudents = new ArrayList<>();

	/**
	 * 发送学生分页请求
	 * 
	 * @param studentPageUrl
	 * @param i
	 *            页数
	 * @param context
	 */
	private static int flag = 0;

	private static void sendOkHttpForStudentPage(final String studentPageUrl, final int i, final Context context,
			final String url1) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				final OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(50, TimeUnit.SECONDS).build();
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(studentPageUrl)
						.append("?pageNo=" + i + "&" + "pageSize=1000" + "&mac=" + SplashScreenActivity.IMEI);
				stringBuilder.append("&signature=" + getSignatureVal2(i));
				Log.i("stringBuilder.toString()", stringBuilder.toString());
				Request request = new Request.Builder().url(stringBuilder.toString()).get().build();
				final Call call = client.newCall(request);
				call.enqueue(new Callback() {

					@Override
					public void onResponse(Call arg0, Response response) throws IOException {
						String result = response.body().string();
						First_Student currentStudent = JSON.parseObject(result, First_Student.class);
						currentStuPage = currentStudent.getPageNo();
						if (currentStudent.getTotalCount() == DbService.getInstance(context).getStudentsCount()) {
							Log.i("------------", DbService.getInstance(context).getStudentsCount() + "学生信息已存在");
							HttpUtil.getStudentItemInfo(context, url1);
							return;
						} else {
							List<PH_Student> currentResult = currentStudent.getResult();
							Log.i("student当前页", currentStudent.getPageNo() + "");
							if (i == 1) {
								totalStudents = currentResult;
							} else {
								totalStudents.addAll(currentResult);
							}
							currentStuPage++;
							if (context.getClass().equals(SplashScreenActivity.class)) {
								SplashScreenActivity.handleUI((currentStuPage * 100) / currentStudent.getTotalPage());
							} else {
								FragmentRight.handleUI((currentStuPage * 100) / currentStudent.getTotalPage());
							}
							if (currentStudent.getPageNo() == currentStudent.getTotalPage()) {
								SaveDBUtil.saveStudentPage(context, totalStudents);
								HttpUtil.getStudentItemInfo(context,url1);
								return;
							} else {
								sendOkHttpForStudentPage(url1 + Constant.STUDENT_Page_URL, currentStuPage, context,
										url1);
							}
						}
					}

					@Override
					public void onFailure(Call arg0, IOException arg1) {
						flag++;
						Log.e(currentStuPage + "页下载失败", arg1 + "失败次数：" + flag);
						if (flag < 100) {
							sendOkHttpForStudentPage(url1+Constant.STUDENT_Page_URL, currentStuPage, context, url1);
						} else {
							if (context.getClass().equals(SplashScreenActivity.class)) {
								SplashScreenActivity.mHandle.sendEmptyMessage(1);
								Intent intent = new Intent(context, MainActivity.class);
								intent.putExtra("1", 1);
								context.startActivity(intent);
							} else {
								FragmentRight.mHandler.sendEmptyMessage(2);
							}
						}
					}
				});
			}
		}).start();

	}

	/**
	 * 获取学生分页信息加密
	 * 
	 * @param pageNo
	 * @return
	 */
	protected static String getSignatureVal2(int pageNo) {
		try {
			StringBuilder stringBuilder = new StringBuilder();
			List<String> list = new ArrayList<String>();
			list.add("pageNo");
			list.add(pageNo + "");
			list.add("pageSize");
			list.add("1000");
			list.add("mac");
			list.add(SplashScreenActivity.IMEI);
			// 字典排序
			Collections.sort(list);
			for (int i = 0; i < list.size(); i++) {
				stringBuilder.append(list.get(i));
			}
			stringBuilder.append(Constant.TOKEN);
			return HttpUtil.getMD5(stringBuilder.toString());
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	private static int currentPage = 0;

	/**
	 * 发送学生项目分页请求
	 * 
	 * @param path
	 * @param i
	 * @param params
	 * @param context
	 */
	private static int flag1 = 0;

	public static void sendOkhttpForStudentItem(final String path, final int i, final Map<String, String> params,
			final Context context) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(50, TimeUnit.SECONDS).build();
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(path).append("?pageNo=" + i + "&");
				if (params != null && params.size() != 0) {
					for (Map.Entry<String, String> entry : params.entrySet()) {
						// 转换成UTF-8
						try {
							stringBuilder.append(entry.getKey()).append("=")
									.append(URLEncoder.encode(entry.getValue(), "utf-8"));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						stringBuilder.append("&");
					}
				}
				// 连接signature
				stringBuilder.append("signature=" + getSignatureVal1(params, i));
				Request request = new Request.Builder().url(stringBuilder.toString()).get().build();
				final Call call = client.newCall(request);
				call.enqueue(new Callback() {

					@Override
					public void onResponse(Call arg0, Response response) throws IOException {
						String result = response.body().string();
						First_StudentItem currentStuItem = JSON.parseObject(result, First_StudentItem.class);
						currentPage = currentStuItem.getPageNo();
						if (currentStuItem.getTotalCount() == DbService.getInstance(context).getStudentItemsCount()) {
							Log.i("-----------------", currentStuItem.getTotalCount() + "学生项目信息已存在");
							FragmentRight.mHandler.sendEmptyMessage(1);
							return;
						} else {
							List<PH_StudentItem> currentResult = currentStuItem.getResult();
							if (i == 1) {
								studentItems = currentStuItem.getResult();
							} else {
								studentItems.addAll(currentResult);
							}
							Log.i("studentItem当前页", currentStuItem.getPageNo() + "");
							currentPage++;
							if (context.getClass().equals(SplashScreenActivity.class)) {
								SplashScreenActivity.handleUI((currentPage * 100) / currentStuItem.getTotalPage());
							} else {
								if (currentStuItem.getPageNo() == currentStuItem.getTotalPage()) {
									FragmentRight.mHandler.sendEmptyMessage(7);
								} else {
									FragmentRight.handleUI((currentPage * 100) / currentStuItem.getTotalPage());
								}
							}
							if (currentStuItem.getPageNo() == currentStuItem.getTotalPage()) {
								Log.i("studentItems", studentItems.size() + "");
								SaveDBUtil.saveStudentItemDB(studentItems, context, currentStuItem.getTotalPage(),
										currentStuItem.getPageNo());
								return;
							} else {
								sendOkhttpForStudentItem(path, currentPage, params, context);
							}
						}
					}

					@Override
					public void onFailure(Call arg0, IOException arg1) {
						flag1++;
						Log.e(currentPage + "页下载失败", arg1 + "失败次数：" + flag1);
						if (flag1 < 100) {
							sendOkhttpForStudentItem(path, currentPage, params, context);
						} else {
							if (context.getClass().equals(SplashScreenActivity.class)) {
								SplashScreenActivity.mHandle.sendEmptyMessage(1);
								Intent intent = new Intent(context, MainActivity.class);
								intent.putExtra("1", 1);
								context.startActivity(intent);
							} else {
								FragmentRight.mHandler.sendEmptyMessage(2);
							}
						}
					}
				});
			}
		}).start();
	}

	/**
	 * 获取学生项目信息
	 * 
	 * @param context
	 * @param url1 
	 * @param context2
	 */
	public static boolean getStudentItemInfo(final Context context, String url1) {
		try {
			Map<String, String> map = new HashMap<>();
			map.put("mac", SplashScreenActivity.IMEI);
			sendOkhttpForStudentItem(url1+Constant.STUDENT_ITEM_URL, 1, map, context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return StuItemFlag;
	}

	public static void getPassword(final Context context, final String ip) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> paramMap = new HashMap<>();
				paramMap.put("mac", SplashScreenActivity.IMEI);
				OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder().connectTimeout(5, TimeUnit.SECONDS)
						.readTimeout(5, TimeUnit.SECONDS).build();
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(ip + Constant.GET_PASSWORD).append(
						"?signature=" + HttpUtil.getSignatureVal(paramMap) + "&mac=" + SplashScreenActivity.IMEI);

				Log.i("stringBuilder=", stringBuilder.toString());
				Request request = new Request.Builder().url(stringBuilder.toString()).post(RequestBody.create(null, ""))
						.build();
				Call call = mOkHttpClient.newCall(request);
				call.enqueue(new Callback() {

					@Override
					public void onResponse(Call arg0, Response response) throws IOException {
						result = response.body().string();
						List<Password> password = JSON.parseArray("[" + result + "]", Password.class);
						Log.i("result============", result);
						SharedPreferences mSharedPreferences = context.getSharedPreferences("password",
								Activity.MODE_PRIVATE);
						SharedPreferences.Editor editor = mSharedPreferences.edit();
						editor.putString("password", password.get(0).getPassword());
						editor.commit();
						if (password.isEmpty()) {
							SharedpreferencesActivity.mHandler.sendEmptyMessage(1);
						} else {
							SharedpreferencesActivity.mHandler.sendEmptyMessage(2);
						}
					}

					@Override
					public void onFailure(Call arg0, IOException arg1) {
						SharedpreferencesActivity.mHandler.sendEmptyMessage(1);
					}
				});
			}
		}).start();
	}

	/**
	 * 获取项目信息
	 * 
	 * @param context
	 */
	private static int itemFlag = 0;

	public static int getItemInfo(final Context context, final String url1) {
		try {
			if (context.getClass().equals(SplashScreenActivity.class)) {
				itemFlag = 1;
			} else {
				itemFlag = 2;
			}
			startTime = System.currentTimeMillis();
			Map<String, String> map = new HashMap<>();
			map.put("mac", SplashScreenActivity.IMEI);
			itemFlag = sendOkhttp(itemFlag, url1+Constant.ITEM_URL, map, new HttpCallbackListener() {
				public void onFinish(String response) {
					// 解析获取的Json数据
					itemList = JSON.parseArray(response, Item.class);
					if (DbService.getInstance(context).loadAllItem().size() != itemList.size()) {
						DbService.getInstance(context).saveItemLists(itemList);
						Log.i("success", "保存项目信息成功");
					} else {
						Log.i("fail", "项目信息已存在");
					}
					HttpUtil.getStudentInfo(context, url1);
				}

				@Override
				public void onError(Exception e) {
					Log.i("error", "项目数据下载失败");
					if (context.getClass().equals(SplashScreenActivity.class)) {
						SplashScreenActivity.mHandle.sendEmptyMessage(1);
						Intent intent = new Intent(context, MainActivity.class);
						intent.putExtra("1", 1);
						context.startActivity(intent);
					} else {
						FragmentRight.mHandler.sendEmptyMessage(2);
					}
				}
			});
		} catch (Exception e) {
			Log.i("-1", "项目数据下载失败");
			if (context.getClass().equals(SplashScreenActivity.class)) {
				SplashScreenActivity.mHandle.sendEmptyMessage(1);
				Intent intent = new Intent(context, MainActivity.class);
				intent.putExtra("1", 1);
				context.startActivity(intent);
			} else {
				FragmentRight.mHandler.sendEmptyMessage(2);
			}
			e.printStackTrace();
		}
		Log.i("itemFlag=", okFlag + "");
		return okFlag;
	}

}
