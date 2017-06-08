package com.fpl.myapp.activity.manage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.fpl.myapp2.R;
import com.fpl.myapp.activity.SplashScreenActivity;
import com.fpl.myapp.adapter.ProjectAdapter;
import com.fpl.myapp.db.DbService;
import com.fpl.myapp.util.Constant;
import com.fpl.myapp.util.HttpCallbackListener;
import com.fpl.myapp.util.HttpUtil;
import com.fpl.myapp.util.NetUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import ww.greendao.dao.Item;

public class FragmentRight extends Fragment {
	private static Context context;
	private SharedPreferences sharedPreferences;
	private String[] projectName = { "50米跑", "800/1000米跑", "身高体重", "肺活量", "立定跳远", "仰卧起坐", "坐位体前屈", "引体向上" };
	private static ArrayList<Map<String, Object>> dataList = new ArrayList<>();
	private static ArrayList<String> strings = new ArrayList<>();
	private ArrayList<String> nameData = new ArrayList<>();
	private static ArrayList<String> names = new ArrayList<>();
	private ArrayList<String> names1 = new ArrayList<>();
	private List<Item> itemList = new ArrayList<>();
	private ArrayList<String> projects = new ArrayList<>();
	private List<Item> newList;
	private SharedPreferences mSharedPreferences;
	private String ip;
	private String number;
	private static ListView lvProject;
	private static ProjectAdapter adapter;

	public static Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				pbFrag.setVisibility(View.GONE);
				NetUtil.showToast(context, "学生信息已存在");
				break;
			case 2:
				NetUtil.showToast(context, "服务器连接异常，数据下载失败");
				break;
			case 3:
				NetUtil.showToast(context, "服务器连接异常");
				break;
			case 4:
				NetUtil.showToast(context, "已为最新项目");
				break;
			case 5:
				Log.i("更新", "------------");
				dataList = getNewDate(names);
				showList();
				break;
			case 6:
				showList();
				break;
			case 7:
				pbFrag.setVisibility(View.GONE);
				NetUtil.showToast(context, "下载完成");
				break;
			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_fragment_right, container, false);
		context = getActivity();

		// 获取保存在本地的选中项目
		sharedPreferences = context.getSharedPreferences("projects", Activity.MODE_PRIVATE);
		// 获取保存在本地的IP地址
		mSharedPreferences = context.getSharedPreferences("ipAddress", Activity.MODE_PRIVATE);
		ip = mSharedPreferences.getString("ip", "");
		number = mSharedPreferences.getString("number", "");

		int selected = sharedPreferences.getInt("size", 0);
		for (int i = 0; i < selected; i++) {
			strings.add(sharedPreferences.getString(i + "", ""));
			Log.i("strings=", strings.toString());
		}

		newList = DbService.getInstance(context).loadAllItem();
		for (int i = 0; i < newList.size(); i++) {
			names1.add(newList.get(i).getItemName());
		}

		initView(view);
		setListener();

		// getItems();
		return view;
	}

	private CheckBox cb;
	private ImageView ivMore;
	private ImageButton ibQuit;
	public static ProgressBar pbFrag;

	/**
	 * 更新进度条显示
	 * 
	 * @param progress
	 */
	public static void handleUI(final int progress) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				pbFrag.setProgress(progress);
			}
		});
	}

	private void setListener() {
		ibQuit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});

		lvProject.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				cb = (CheckBox) view.findViewById(R.id.cb_project);
				cb.toggle();
				ProjectAdapter.getIsSelected().put(position, cb.isChecked());
			}

		});

		ivMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ivMore.setBackgroundResource(R.drawable.more_up);
				showPopupMenu(ivMore);
			}
		});
	}

	private void showPopupMenu(View view) {
		// View当前PopupMenu显示的相对View的位置
		PopupMenu popupMenu = new PopupMenu(context, view);
		// menu布局
		popupMenu.getMenuInflater().inflate(R.menu.main, popupMenu.getMenu());
		// menu的item点击事件
		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.more_item:
					isWifiConnected(NetUtil.netState(context), 1);
					break;
				case R.id.more_student:
					isWifiConnected(NetUtil.netState(context), 2);
					break;
				default:
					break;
				}
				return false;
			}
		});
		// PopupMenu关闭事件
		popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
			@Override
			public void onDismiss(PopupMenu menu) {
				ivMore.setBackgroundResource(R.drawable.more_down);
			}
		});

		popupMenu.show();
	}

	protected void isWifiConnected(boolean result, int i) {

		if (true == result) {
			showDiaglog(i);
		} else {
			NetUtil.checkNetwork(getActivity());
		}
	}

	private void showDiaglog(final int i) {
		new AlertDialog.Builder(context).setTitle("更新数据").setMessage("数据库中数据将被重置，是否确定？")
				.setPositiveButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).setNegativeButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (i) {
						case 1:
							sendItemRequest();
							break;
						case 2:
							pbFrag.setVisibility(View.VISIBLE);
							HttpUtil.getItemInfo(context, "http://" + ip + ":" + number);
							break;

						default:
							break;
						}
					}
				}).show();
	}

	private void initView(View view) {
		lvProject = (ListView) view.findViewById(R.id.lv_project);
		ivMore = (ImageView) view.findViewById(R.id.iv_more);
		ibQuit = (ImageButton) view.findViewById(R.id.ib_top_quit);
		pbFrag = (ProgressBar) view.findViewById(R.id.pb_frag);

		ivMore.setVisibility(View.VISIBLE);
		ivMore.setBackgroundResource(R.drawable.more_down);
		removeName(names1);
		dataList = getNewDate(names1);
		showList();

	}

	private static void showList() {

		Log.i("dataList=", dataList.toString());
		adapter = new ProjectAdapter(context, dataList);
		lvProject.setAdapter(adapter);
		for (int i = 0; i < dataList.size(); i++) {
			for (int j = 0; j < strings.size(); j++) {
				if (dataList.get(i).get("name").equals(strings.get(j))) {
					ProjectAdapter.getIsSelected().put(i, true);
				}
			}

		}
	}

	/**
	 * 获取最新项目信息
	 */
	private void sendItemRequest() {

		try {
			Map<String, String> map = new HashMap<>();
			map.put("mac", SplashScreenActivity.IMEI);
			String url = "http://" + ip + ":" + number + Constant.ITEM_URL;
			HttpUtil.sendOkhttp(2, url, map, new HttpCallbackListener() {

				@Override
				public void onFinish(String response) {
					names.clear();
					// 解析获取的Json数据
					itemList = JSON.parseArray(response, Item.class);
					Log.i("item--->", itemList.get(0).getItemName());
					// 获取项目名字集合
					for (int j = 0; j < itemList.size(); j++) {
						names.add(itemList.get(j).getItemName());
					}
					if (DbService.getInstance(context).loadAllItem().isEmpty()
							|| DbService.getInstance(context).loadAllItem().size() != itemList.size()) {
						DbService.getInstance(context).saveItemLists(itemList);
					}
					removeName(names);
					nameData.clear();
					for (int i = 0; i < dataList.size(); i++) {
						nameData.add(dataList.get(i).get("name").toString());
						Log.i("nameData=", nameData + "");
					}
					// 判断获取的项目是否更新
					if (nameData.size() == names.size()) {
						for (int i = 0; i < nameData.size(); i++) {
							if (nameData.get(i).toString().equals(names.get(i).toString())) {
								mHandler.sendEmptyMessage(4);
							} else {
								mHandler.sendEmptyMessage(5);
							}
						}

					} else {
						Log.i("更新", "------------");
						dataList = getNewDate(names);
						mHandler.sendEmptyMessage(6);
					}
				}

				@Override
				public void onError(Exception e) {
					mHandler.sendEmptyMessage(2);
				}
			});
		} catch (Exception e) {
			mHandler.sendEmptyMessage(2);
		}
	}

	private static ArrayList<Map<String, Object>> getNewDate(List<String> strings) {
		dataList.clear();

		for (int i = 0; i < strings.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("xuhao", i + 9);
			map.put("name", strings.get(i));
			dataList.add(map);
		}
		Log.i("dataList", dataList.toString());
		return dataList;
	}

	/**
	 * 移除项目集合中基本项目
	 * 
	 * @param stringList
	 */
	private void removeName(ArrayList<String> stringList) {
		for (int i = 0; i < projectName.length; i++) {
			stringList.remove(projectName[i]);
		}
		for (Iterator<String> it = stringList.iterator(); it.hasNext();) {
			String item = it.next();
			if (item.equals("身高") || item.equals("体重") || item.contains("仰卧起坐") || item.equals("1000米跑")
					|| item.equals("800米跑")) {
				it.remove();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 退出前获取选中项目
		for (int i = 0; i < dataList.size(); i++) {
			if (ProjectAdapter.getIsSelected().get(i)) {
				projects.add(dataList.get(i).get("name").toString());
			}
		}
		// SharedPreferences保存选中的项目信息
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt("size", projects.size());
		for (int i = 0; i < projects.size(); i++) {
			editor.putString(i + "", projects.get(i));
		}
		editor.commit();
		Log.i("数据成功写入SharedPreferences！", editor + "");
	}
}
