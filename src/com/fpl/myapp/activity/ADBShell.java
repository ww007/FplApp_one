package com.fpl.myapp.activity;

import java.io.OutputStream;

/**
 * adb命令需root（未用）
 * @author ww
 *
 */
public class ADBShell {
	private OutputStream os;

	/**
	 * <b>方法描述： </b>
	 * <dd>方法作用： 执行adb命令
	 * 注意，Runtime.getRuntime().exec("su").getOutputStream();
	 * 
	 * @param cmd
	 *            具体命令语句
	 * @since Met 1.0
	 * @see
	 */
	public final void execute(String cmd) {
		try {
			if (os == null) {
				os = Runtime.getRuntime().exec("su").getOutputStream();
			}
			os.write(cmd.getBytes());
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <b>方法描述：模拟按键长按操作 </b>
	 * @param keyCode
	 *            对应的按键代码
	 * @since Met 1.0
	 * @see
	 */
	public final void simulateKey(int keyCode) {
		execute("input keyevent --longpress " + keyCode + "\n");
	}
}
