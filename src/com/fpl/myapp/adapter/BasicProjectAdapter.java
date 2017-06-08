package com.fpl.myapp.adapter;

import java.util.ArrayList;
import java.util.Map;

import com.fpl.myapp2.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class BasicProjectAdapter extends BaseAdapter {
	private ArrayList<Map<String, Object>> datas;
	private LayoutInflater inflater;

	public BasicProjectAdapter(Context context, ArrayList<Map<String, Object>> dataList) {
		this.datas = dataList;
		inflater = LayoutInflater.from(context);
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();

			convertView = inflater.inflate(R.layout.item_project, null);
			holder.xuhao = (TextView) convertView.findViewById(R.id.tv_project_xuhao);
			holder.name = (TextView) convertView.findViewById(R.id.tv_project_name);
			holder.blank = (TextView) convertView.findViewById(R.id.tv_project_blank);
			holder.cbProject = (CheckBox) convertView.findViewById(R.id.cb_project);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.xuhao.setText(datas.get(position).get("xuhao").toString());
		holder.name.setText(datas.get(position).get("name").toString());
		holder.cbProject.setVisibility(View.GONE);
		holder.blank.setVisibility(View.VISIBLE);

		return convertView;
	}

	class ViewHolder {
		TextView xuhao, name, blank;
		CheckBox cbProject;
	}

}
