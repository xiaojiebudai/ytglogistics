package com.ytglogistics.www.ytglogistics.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.utils.DialogUtils;

import butterknife.ButterKnife;

/**
 * activity基类
 *
 * @author xl
 * @date:2016-7-24下午3:06:09
 * @description
 */
public abstract class FatherActivity extends AppCompatActivity {
	protected Bundle mSavedInstanceState;
	protected InputMethodManager inputMethodManager;
	protected Dialog mDialog_wait;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 透明状态栏
			// Window window = getWindow();
			// window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
			// WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
		mSavedInstanceState = savedInstanceState;
		setContentView(getLayoutId());
		ButterKnife.bind(this);
		initValues();
		initView();
		doOperate();
	}

	@TargetApi(19)
	private void setTranslucentStatus(boolean on) {
		Window win = this.getWindow();

		WindowManager.LayoutParams winParams = win.getAttributes();

		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

		if (on) {

			winParams.flags |= bits;

		} else {

			winParams.flags &= ~bits;

		}

		win.setAttributes(winParams);

	}

	/** 设置布局ID */
	protected abstract int getLayoutId();

	/** 初始值 */
	protected abstract void initValues();

	/** 初始化控件 */
	protected abstract void initView();

	/** 进行操作 */
	protected abstract void doOperate();

	/**
	 * 初始默认头部
	 *
	 * @author xl
	 * @date:2016-7-24下午3:11:37
	 * @description 左侧按钮返回,中间title文本
	 * @param titleId
	 * @param withLeft
	 *            看UI图好像有些界面左侧没返回按钮
	 */
	protected void initDefautHead(int titleId, boolean withLeft) {
		TextView center = (TextView) findViewById(R.id.tv_head_center);
		if (center != null) {
			center.setText(titleId);
			center.setTextColor(getResources().getColor(R.color.white));
			center.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		}
		if (withLeft) {
			initHeadBack();
		}

	}

	/**
	 * 初始默认头部
	 *
	 * @author xl
	 * @date:2016-7-24下午3:11:37
	 * @description 左侧按钮返回,中间title文本
	 * @param title
	 * @param withLeft
	 *            看UI图好像有些界面左侧没返回按钮
	 */
	protected void initDefautHead(String title, boolean withLeft) {
		TextView center = (TextView) findViewById(R.id.tv_head_center);
		if (center != null) {
			center.setText(title);
			center.setTextColor(getResources().getColor(R.color.white));
			center.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		}
		if (withLeft) {
			initHeadBack();
		}

	}

	/**
	 * 初始化头部右侧的文本按钮
	 *
	 * @param resId
	 * @param listener
	 */
	protected void initTextHeadRigth(int resId, OnClickListener listener) {
		View right = findViewById(R.id.rl_head_right);
		TextView text = (TextView) findViewById(R.id.tv_head_right);
		if (right != null && text != null) {
			right.setOnClickListener(listener);
			text.setText(resId);
		}
	}

	/**
	 * 初始化头部左侧返回
	 *
	 */
	protected void initHeadBack() {
		View left = findViewById(R.id.rl_head_left);
		if (left != null) {
			left.findViewById(R.id.tv_head_left).setBackgroundResource(
					R.mipmap.arrow_back);
			left.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					back();
				}
			});
		}

	}

	protected void back() {
		finish();
	}

	/** 隐藏软键盘 */
	public void hideSoftKeyboard() {
		if (inputMethodManager == null) {
			inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/** 改变背景色 */
	public void changeBackgroundAlpha(float alpha) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = alpha;
		if (alpha == 1) {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);// 不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
		} else {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);// 此行代码主要是解决在华为手机上半透明效果无效的bug
		}
		getWindow().setAttributes(lp);
	}

	/**
	 * 初始化WaitDialog
	 *
	 * @author xl
	 * @description 开发设置是否取消
	 * @param cancelable
	 */
	protected void initWaitDialog(boolean cancelable) {
		if (mDialog_wait == null) {
			mDialog_wait = DialogUtils.getWaitDialog(this, cancelable);
		}
	}

	/** 显示WaitDialog */
	public void showWaitDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (mDialog_wait == null) {
					initWaitDialog(true);
				}
				if (mDialog_wait.isShowing()) {
					return;
				}
				mDialog_wait.show();
			}
		});

	}

	/** 隐藏WaitDialog */
	protected void dismissWaitDialog() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mDialog_wait != null && mDialog_wait.isShowing()) {
					mDialog_wait.dismiss();
				}
			}
		});
	}

	/** WaitDialog是否处于显示状态,用于处理显示被用于手动取消显示的逻辑处理 */
	protected boolean isWaitDialogShowing() {
		if (mDialog_wait != null) {
			return mDialog_wait.isShowing();
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				dismissWaitDialog();
				mDialog_wait = null;
			}
		});

	}


}
