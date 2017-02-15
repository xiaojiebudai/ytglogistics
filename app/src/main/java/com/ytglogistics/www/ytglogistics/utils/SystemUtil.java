package com.ytglogistics.www.ytglogistics.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.ytglogistics.www.ytglogistics.MyApplication;


public class SystemUtil {
	/**
	 * @return获取版本信息
	 * @throws Exception
	 */
	public static int getVersionCode() throws Exception {
		// 获取packagemanager的实例
		PackageManager packageManager = MyApplication.getInstance().getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(MyApplication.getInstance().getPackageName(),
				0);
		String version = packInfo.versionName;
		int versioncode = packInfo.versionCode;
		return versioncode;
	}
}
