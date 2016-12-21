package com.ytglogistics.www.ytglogistics.api;

public class Api {
	 private static final boolean isDebug =true;

	 /** 主机地址 */
	public static final String ONLINE = isDebug ? "http://120.24.184.18:808/Wcf/bllService.svc/"
			: "http://www.yplog.com.cn/Wcf/bllService.svc/";

	/**
	 * 登陆
	 * @return
	 */
	public static final String UserLogin() {
		return ONLINE + "UserLogin";
	}

	/**
	 * 获取用户信息
	 * @return
	 */
	public static final String GetMyInfo() {
		return ONLINE + "GetMyInfo";
	}
	public static final String GetAppMxCbmById() {
		return ONLINE + "GetAppMxCbmById";
	}
	/**
	 * 收货
	 * @return
	 */
	public static final String FinishQueue() {
		return ONLINE + "FinishQueue";
	}
	/**
	 * 入仓数据列表
	 * @return
	 */
	public static final String GetQueues() {
		return ONLINE + "GetQueues";
	}
	/**
	 * 装卸队列表
	 * @return
	 */
	public static final String GetSteved() {
		return ONLINE + "GetSteved";
	}

	public static final String GetUsers() {
		return ONLINE + "GetUsers";
	}
	/**
	 * in保存  post  AppInResult
	 * @return
	 */
	public static final String AppInCommit() {
		return ONLINE + "AppInCommit";
	}
	public static final String GetAppIn() {
		return ONLINE + "GetAppIn";
	}
	public static final String GetAppInMx() {
		return ONLINE + "GetAppInMx";
	}

	/**
	 * in收获  get
	 * serial : serial,
	 queueNo : yyNo
	 * @return
	 */
	public static final String AppInMxCommit() {
		return ONLINE + "AppInMxCommit";
	}
	/**
	 * 获取打印数据
	 * @return
	 */
	public static final String PdaPallet() {
		return ONLINE + "PdaPallet";
	}

}
