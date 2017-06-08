package com.fpl.myapp.util;

/**
 * http请求回调接口
 * 
 * @author ww
 *
 */
public interface HttpCallbackListener {
	// 发送请求成功
	void onFinish(String response);

	// 发送请求失败
	void onError(Exception e);
}
