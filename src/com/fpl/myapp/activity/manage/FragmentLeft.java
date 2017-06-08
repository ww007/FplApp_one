package com.fpl.myapp.activity.manage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fpl.myapp2.R;
import com.fpl.myapp.adapter.BasicProjectAdapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

public class FragmentLeft extends Fragment {
	private int[] projectXuhao = { 1, 2, 3, 4, 5, 6, 7, 8 };
	private String[] projectName = { "50米跑", "800/1000米跑", "身高体重", "肺活量", "立定跳远", "仰卧起坐", "坐位体前屈", "引体向上" };
	private ArrayList<Map<String, Object>> basicList = new ArrayList<>();
	private BasicProjectAdapter basicAdapter;
	private ListView lvProjectBasic;
	private Context context;
	private ImageButton ibQuit;
	private ImageView ivMore;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_fragment_left, container, false);
		context = getActivity();

		getBasicDate();
		lvProjectBasic = (ListView) view.findViewById(R.id.lv_project_basic);
		ibQuit = (ImageButton) view.findViewById(R.id.ib_top_quit);
		ivMore = (ImageView) view.findViewById(R.id.iv_more);
		ivMore.setVisibility(View.GONE);
		basicAdapter = new BasicProjectAdapter(context, basicList);
		lvProjectBasic.setAdapter(basicAdapter);

		ibQuit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		return view;
	}

	private List<Map<String, Object>> getBasicDate() {

		for (int i = 0; i < projectXuhao.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("xuhao", projectXuhao[i]);
			map.put("name", projectName[i]);
			basicList.add(map);
		}
		return basicList;
	}
}
