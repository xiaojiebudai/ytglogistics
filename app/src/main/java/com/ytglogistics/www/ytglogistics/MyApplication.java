package com.ytglogistics.www.ytglogistics;

import android.app.Application;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.TextUtils;


import com.ytglogistics.www.ytglogistics.activity.MainActivity;
import com.ytglogistics.www.ytglogistics.utils.Consts;
import com.ytglogistics.www.ytglogistics.utils.SharedPreferenceUtils;
import com.ytglogistics.www.ytglogistics.utils.WWToast;
import com.ytglogistics.www.ytglogistics.utils.preDefiniation;

import org.xutils.x;


import rego.printlib.export.regoPrinter;


public class MyApplication extends Application {
	/** 用户id */
	private static String userId;
	/** sessionId */
	private String sessionId;

	private static MyApplication instance;

	/**
	 * 如果内存为空,取sp,sp为空就跳登录界面
	 *
	 * @return
	 */
	public String getSessionId() {
		if (sessionId == null) {
			sessionId = SharedPreferenceUtils.getInstance().getSessionId();
		}
		return sessionId;
	}

	/** 只在登陆的时候用 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
		SharedPreferenceUtils.getInstance().saveSessionId(sessionId);// 存本地
		isSessionPast = false;// 设置id的时候(登录)sessionPast重置
	}

	/** 只在登陆的时候用 */
	public void setSessionPast() {
		isSessionPast = false;// sessionPast重置
	}

	/** session失效 */
	private boolean isSessionPast;
	public void dealSessionPast() {
		if (isSessionPast) {
			return;
		} else {
			isSessionPast = true;
			clearLoginInfo();
			// TODO:弹窗?跳界面
				startActivity(new Intent(this, MainActivity.class).putExtra(
						Consts.KEY_SESSION_ERROR, true).setFlags(
						Intent.FLAG_ACTIVITY_NEW_TASK));
		}
	}
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		SharedPreferenceUtils.init(this);
		x.Ext.init(this);
		x.Ext.setDebug(true);
		WWToast.init(this);
		sessionId = SharedPreferenceUtils.getInstance().getSessionId();
		setObject();
	}
	public static Typeface typeFace;
	public static MyApplication getInstance() {
		return instance;
	}

	/**
	 * 判断登录状态
	 * 
	 * @return
	 */
	public boolean isLogin() {
		String userId = SharedPreferenceUtils.getInstance().getSessionId();
		if (!TextUtils.isEmpty(userId)) {
			return true;
		}
		return false;
	}
	public void clearLoginInfo() {
		sessionId = null;
		SharedPreferenceUtils.getInstance().clearLoginData();
	}
	//打印机相关
	private regoPrinter printer;
	private int myState = 0;
	private String printName="RG-MLP80A";

	private preDefiniation.TransferMode printmode = preDefiniation.TransferMode.TM_NONE;
	private boolean labelmark = true;

	public regoPrinter getObject() {
		return printer;
	}

	public void setObject() {
		 printer = new regoPrinter(this);
	}

	public String getName() {
		return printName;
	}

	public void setName(String name) {
		printName = name;
	}
	public void setState(int state) {
		myState = state;
	}

	public int getState() {
		return myState;
	}

	public void setPrintway(int printway) {

		switch (printway) {
			case 0:
				printmode = preDefiniation.TransferMode.TM_NONE;
				break;
			case 1:
				printmode = preDefiniation.TransferMode.TM_DT_V1;
				break;
			default:
				printmode = preDefiniation.TransferMode.TM_DT_V2;
				break;
		}

	}

	public int getPrintway() {
		return printmode.getValue();
	}

	public boolean getlabel() {
		return labelmark;
	}

	public void setlabel(boolean labelprint) {
		labelmark = labelprint;
	}
}
