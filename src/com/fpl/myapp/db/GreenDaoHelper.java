package com.fpl.myapp.db;

import org.apache.log4j.Logger;

import com.fpl.myapp.log4j.LogUtil;
import android.app.Application;
import android.content.Context;
import ww.greendao.dao.DaoMaster;
import ww.greendao.dao.DaoMaster.OpenHelper;
import ww.greendao.dao.DaoSession;

public class GreenDaoHelper extends Application {
	public static final String DB_NAME = "myapp_db";
	private static GreenDaoHelper mInstance;
	private static DaoMaster daoMaster;
	private static DaoSession daoSession;

	@Override
	public void onCreate() {
		super.onCreate();

		// 配置log4j基本参数
		LogUtil.configLog();

		// 获取Application Log
		Logger log = Logger.getLogger(GreenDaoHelper.class);
		// LogcatHelper.getInstance(this).start();

		// 输出MyApplication的信息
		log.info("Log4j Is Ready and My Application Was Created Successfully!");
		if (mInstance == null)
			mInstance = this;
	}

	/**
	 * return DaoMaster
	 * 
	 * @param context
	 */
	public static DaoMaster getDaoMaster(Context context) {
		if (daoMaster == null) {
			OpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
			daoMaster = new DaoMaster(helper.getWritableDatabase());
		}
		return daoMaster;
	}

	/**
	 * return DaoSession
	 * 
	 * @param context
	 */
	public static DaoSession getDaoSession(Context context) {
		if (daoSession == null) {
			if (daoMaster == null) {
				daoMaster = getDaoMaster(context);
			}
			daoSession = daoMaster.newSession();
		}
		return daoSession;
	}
}
