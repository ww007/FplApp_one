package com.fpl.myapp.adapter;

import java.util.ArrayList;
import com.fpl.myapp2.R;
import com.fpl.myapp.entity.ICInfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ICInfoAdapter extends BaseAdapter {
	private ArrayList<ICInfo> datas;
	private LayoutInflater inflater;

	public ICInfoAdapter(Context context, ArrayList<ICInfo> dataList) {
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

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_icinfo, null);
			holder.project = (TextView) convertView.findViewById(R.id.tv_icinfo_projectName);
			holder.projectValue = (TextView) convertView.findViewById(R.id.tv_icinfo_projectValue);
			holder.line = (TextView) convertView.findViewById(R.id.tv_icinfo_line);
			holder.line1 = (TextView) convertView.findViewById(R.id.tv_icinfo_line1);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		setData(holder, position);
		return convertView;
	}

	private void setData(ViewHolder holder, int position) {
		holder.projectValue.setText(datas.get(position).getProjectValue());
		holder.project.setText(datas.get(position).getProjectTitle());
		if (datas.get(position).getProjectTitle().equals("身高")
				|| datas.get(position).getProjectTitle().equals("左眼视力")) {
			holder.line.setVisibility(View.INVISIBLE);
			holder.line1.setVisibility(View.VISIBLE);
		} else {
			holder.line.setVisibility(View.VISIBLE);
			holder.line1.setVisibility(View.INVISIBLE);
		}
	}

	class ViewHolder {
		TextView project, projectValue, line, line1;
	}

}
