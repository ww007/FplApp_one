package com.fpl.myapp.adapter;

import java.util.ArrayList;

import com.fpl.myapp2.R;
import com.fpl.myapp.entity.RunGrade;
import com.wnb.android.nfc.dataobject.entity.Student;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChengjiAdapter extends BaseAdapter {
	public static ArrayList<RunGrade> datas;
	private LayoutInflater inflater;

	public ChengjiAdapter(Context context, ArrayList<RunGrade> list) {
		ChengjiAdapter.datas = list;
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

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_chengji, null);
			holder.xuhao = (TextView) convertView.findViewById(R.id.tv_xuhao);
			holder.chengji = (TextView) convertView.findViewById(R.id.tv_chengji);
			holder.name = (TextView) convertView.findViewById(R.id.tv_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (mSelect == position) {
			convertView.setBackgroundColor(Color.rgb(0, 255, 255)); // 选中项背景
		} else {
			convertView.setBackgroundColor(Color.TRANSPARENT); // 其他项背景
		}
		setData(holder, position);
		return convertView;
	}

	/**
	 * 设置viewHolder的数据
	 * 
	 * @param holder
	 * @param position
	 */
	private void setData(ViewHolder holder, int itemIndex) {
		RunGrade runGrade = datas.get(itemIndex);
		Log.i("datas", runGrade.getXuhao() + "");
		Log.i("datas", runGrade.getTime() + "");
		Log.i("datas===========", datas + "");
		holder.xuhao.setText(runGrade.getXuhao() + "");
		holder.chengji.setText(runGrade.getTime() + "");
		holder.name.setText(runGrade.getName() + "");
	}

	public void updateView(View view, int itemIndex, Student student) {
		if (view == null) {
			return;
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.xuhao = (TextView) view.findViewById(R.id.tv_xuhao);
		holder.chengji = (TextView) view.findViewById(R.id.tv_chengji);
		holder.name = (TextView) view.findViewById(R.id.tv_name);
		datas.get(itemIndex).setStuCode(student.getStuCode());
		datas.get(itemIndex).setSex(student.getSex());
		setData(holder, itemIndex);
	}

	class ViewHolder {
		TextView xuhao, chengji, name;
	}

	public void setSelectItem(int position) {
		mSelect = position;

	}

	int mSelect = 0;
}
