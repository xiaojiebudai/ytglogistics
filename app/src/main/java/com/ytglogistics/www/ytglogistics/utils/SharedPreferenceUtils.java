package com.ytglogistics.www.ytglogistics.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * 获取SharedPreference数据的工具类
 * 
 * @author lc
 * 
 */
public class SharedPreferenceUtils {
	private static final String USER_INFO = "shared_save_info";

	private static SharedPreferenceUtils utils;
	private static SharedPreferences mSharedPreferences;
	private static SharedPreferences.Editor editor;

	/** 登录的sessionId */
	private static String SHARED_KEY_LOGIN_ID = "login_id";
	/** 登录的用户名*/
	private static String SHARED_KEY_USERNAME = "user_name";
	/** 登录的用户信息*/
	private static String SHARED_KEY_USERINFO = "user_info";
	/** 登录的打印机IP*/
	private static String SHARED_KEY_IP = "ip_address";
	private SharedPreferenceUtils(Context cxt) {
		mSharedPreferences = cxt.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		editor = mSharedPreferences.edit();
	}

	public static synchronized void init(Context cxt) {
		if (utils == null) {
			utils = new SharedPreferenceUtils(cxt);
		}
	}

	public synchronized static SharedPreferenceUtils getInstance() {
		if (utils == null) {
			throw new RuntimeException("please init first!");
		}
		return utils;
	}

	/**
	 * 保存会话id
	 * 
	 * @param sessionId
	 */
	public void saveSessionId(String sessionId) {
		editor.putString(SHARED_KEY_LOGIN_ID, sessionId);
		editor.commit();
	}

	/**
	 * 获取会话id
	 * 
	 * @return
	 */
	public String getSessionId() {
		return mSharedPreferences.getString(SHARED_KEY_LOGIN_ID, "");
	}
	/**
	 * 保存用户名
	 *
	 * @param name
	 */
	public void saveUserName(String name) {
		editor.putString(SHARED_KEY_USERNAME, name);
		editor.commit();
	}

	/**
	 * 获取用户名
	 *
	 * @return
	 */
	public String getUserName() {
		return mSharedPreferences.getString(SHARED_KEY_USERNAME, "");
	}
	/**
	 * 保存用户信息
	 *
	 * @param info
	 */
	public void saveUserInfo(String info) {
		editor.putString(SHARED_KEY_USERINFO, info);
		editor.commit();
	}

	/**
	 * 获取用户信息
	 *
	 * @return
	 */
	public String getUserInfo() {
		return mSharedPreferences.getString(SHARED_KEY_USERINFO, "");
	}
	/**
	 * IP信息
	 *
	 * @param info
	 */
	public void saveIP(String info) {
		editor.putString(SHARED_KEY_IP, info);
		editor.commit();
	}

	/**
	 * 获取IP信息
	 *
	 * @return
	 */
	public String getIP() {
		return mSharedPreferences.getString(SHARED_KEY_IP, "");
	}
	/** 个人的清除登录数据 */
	public void clearLoginData() {
		editor.putString(SHARED_KEY_LOGIN_ID, null);
		editor.putString(SHARED_KEY_USERNAME, null);
		editor.commit();
	}
}
