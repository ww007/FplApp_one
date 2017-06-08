package com.fpl.myapp.activity.manage;

import com.fpl.myapp2.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ProjectManagerActivity extends FragmentActivity implements OnClickListener {

	private Button btnLeft;
	private Button btnRight;
	private Fragment tabLeft;
	private Fragment tabRight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_manager);
		initView();// 初始化所有的view
		initEvents();
		setSelect(0);// 默认显示界面
	}

	private void initView() {
		btnLeft = (Button) findViewById(R.id.btn_left);
		btnRight = (Button) findViewById(R.id.btn_right);
		resetBackgraound();
	}

	private void initEvents() {
		btnLeft.setOnClickListener(this);
		btnRight.setOnClickListener(this);
	}

	private void setSelect(int i) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();// 创建一个事务
		hideFragment(transaction);// 先把所有的Fragment隐藏了，然后下面再开始处理具体要显示的Fragment
		switch (i) {
		case 0:
			if (tabLeft == null) {
				tabLeft = new FragmentLeft();
				transaction.add(R.id.id_content, tabLeft);// 将Fragment添加到Activity中
			} else {
				transaction.show(tabLeft);
			}
			btnLeft.setBackgroundResource(R.color.fragment);
			break;
		case 1:
			if (tabRight == null) {
				tabRight = new FragmentRight();
				transaction.add(R.id.id_content, tabRight);
			} else {
				transaction.show(tabRight);
			}
			btnRight.setBackgroundResource(R.color.fragment);
			break;
		default:
			break;
		}
		transaction.commit();// 提交事务
	}

	@Override
	public void onClick(View v) {
		resetBackgraound();
		switch (v.getId()) {
		case R.id.btn_left:
			setSelect(0);
			break;
		case R.id.btn_right:
			setSelect(1);
			break;

		default:
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		resetBackgraound();
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			setSelect(1);
			return true;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			setSelect(0);
			return true;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void resetBackgraound() {
		btnLeft.setBackgroundResource(0);
		btnRight.setBackgroundResource(0);
	}

	/*
	 * 隐藏所有的Fragment
	 */
	private void hideFragment(FragmentTransaction transaction) {
		if (tabLeft != null) {
			transaction.hide(tabLeft);
		}
		if (tabRight != null) {
			transaction.hide(tabRight);
		}
	}

}
