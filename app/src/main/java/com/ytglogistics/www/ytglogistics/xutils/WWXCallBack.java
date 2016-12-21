package com.ytglogistics.www.ytglogistics.xutils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ytglogistics.www.ytglogistics.MyApplication;
import com.ytglogistics.www.ytglogistics.utils.SharedPreferenceUtils;
import com.ytglogistics.www.ytglogistics.utils.WWToast;
import com.ytglogistics.www.ytglogistics.utils.ZLog;

import org.xutils.common.Callback.CommonCallback;


/**
 *
 * @author zxj
 * @date 2016年12月8日10:35:31
 * @description
 */
public abstract class WWXCallBack implements CommonCallback<String> {

	public static final String RESULT = "Result";
	/** 业务的数据字段 */
	private String modeKey;
	/** 不需要错误提示 */
	private boolean unUsePublicToast;

	public WWXCallBack(String modeKey) {
		super();
		this.modeKey = modeKey;
	}
	public WWXCallBack(String modeKey, boolean unUsePublicToast) {
		super();
		this.modeKey = modeKey;
		this.unUsePublicToast = unUsePublicToast;
	}


	@Override
	public void onCancelled(CancelledException arg0) {
	}

	@Override
	public void onError(Throwable arg0, boolean arg1) {
		ZLog.e("EORROR", ((Exception) arg0).toString());
	}

	@Override
	public void onFinished() {
		onAfterFinished();
	}

	@Override
	public void onSuccess(String result) {
		JSONObject data = JSON.parseObject(result).getJSONObject(
				modeKey + RESULT);
		ZLog.showPost(result);
		if (data != null) {
			boolean success = data.getBooleanValue("Success");
			if (success) {
 				onAfterSuccessOk(data);
			} else {
				onAfterSuccessError(data);
				WWToast.showShort(data.getString("Message"));
				switch (data.getIntValue("Code")) {// 错误码,处理不同业务
					case -100:// session过期
						MyApplication.getInstance().dealSessionPast();
						break;
					default:
						break;
				}
			}
		}
	}

	public abstract void onAfterSuccessOk(JSONObject data);

	public abstract void onAfterFinished();

	public void onAfterSuccessError(JSONObject data) {
	};

}
