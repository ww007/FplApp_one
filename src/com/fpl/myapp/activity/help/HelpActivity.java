package com.fpl.myapp.activity.help;

import java.util.ArrayList;
import java.util.List;

import com.fpl.myapp.util.Constant;
import com.fpl.myapp2.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class HelpActivity extends Activity {

	private TextView tvTitle;
	private ImageButton ibQuit;
	private ListView lvHelp;
	private List<String> data = new ArrayList<>();
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		data.add(Constant.HELP_1);
		data.add(Constant.HELP_2);
		data.add(Constant.HELP_3);
		data.add(Constant.HELP_4);
		data.add(Constant.HELP_5);
		data.add(Constant.HELP_6);

		tvTitle = (TextView) findViewById(R.id.tv_top_title);
		tvTitle.setText("帮助");
		ibQuit = (ImageButton) findViewById(R.id.ib_top_quit);
		lvHelp = (ListView) findViewById(R.id.lv_help);
		adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
		lvHelp.setAdapter(adapter);

		ibQuit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
