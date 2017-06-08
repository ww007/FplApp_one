package com.fpl.myapp.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fpl.myapp2.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ProjectAdapter2 extends BaseAdapter {
	private ArrayList<Map<String, Object>> datas;
	private LayoutInflater inflater;
	// 用来控制CheckBox的选中状况
	private static HashMap<Integer, Boolean> isSelected;

	@SuppressLint("UseSparseArrays")
	public ProjectAdapter2(Context context, ArrayList<Map<String, Object>> dataList) {
		this.datas = dataList;
		inflater = LayoutInflater.from(context);
		isSelected = new HashMap<Integer, Boolean>();
		// 初始化数据
		initDate();
	}

	private void initDate() {
		for (int i = 0; i < datas.size(); i++) {
			getIsSelected().put(i, false);
		}
	}

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas == null ? null : datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();

			convertView = inflater.inflate(R.layout.item_project, null);
			holder.xuhao = (TextView) convertView.findViewById(R.id.tv_project_xuhao);
			holder.name = (TextView) convertView.findViewById(R.id.tv_project_name);
			holder.cbProject = (CheckBox) convertView.findViewById(R.id.cb_project);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.xuhao.setText(datas.get(position).get("xuhao").toString());
		holder.name.setText(datas.get(position).get("name").toString());
		holder.cbProject.setChecked(getIsSelected().get(position));

		return convertView;
	}

	class ViewHolder {
		TextView xuhao, name;
		CheckBox cbProject;
	}

	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		ProjectAdapter2.isSelected = isSelected;
	}
}
