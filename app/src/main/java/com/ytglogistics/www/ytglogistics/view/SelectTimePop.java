package com.ytglogistics.www.ytglogistics.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.format.Time;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.widget.OnWheelChangedListener;
import com.ytglogistics.www.ytglogistics.widget.WheelView;
import com.ytglogistics.www.ytglogistics.widget.adapters.ArrayWheelAdapter;

import java.util.HashMap;
import java.util.Map;



/***
 * Description:选择时间pop Company: wangwanglife Version：1.0
 * 
 * @author zxj
 * @date 2016-7-29
 */
public class SelectTimePop {
	private String[] yearList;
	private String selectYear;
	private String selectMonth;
	private Map<String, String[]> times = new HashMap<String, String[]>();;

	private WheelView wvYear;
	private WheelView wvMonth;

	private PopupWindow mPopupWindow;
	private OnSelectOKLisentner mLisentner;
	private View mParent;
	private Context context;
	private View view;

	public SelectTimePop(final Context context, int parentId,
						 OnSelectOKLisentner lisentner) {
		// TODO Auto-generated constructor stub
		this.context = context;
		mLisentner = lisentner;
		// 先把时间给上
		setDate();
		LayoutInflater inflater = LayoutInflater.from(context);
		mParent = inflater.inflate(parentId, null);
		view = inflater.inflate(R.layout.pop_select_time, null);

		wvYear = (WheelView) view.findViewById(R.id.wv_province);
		wvMonth = (WheelView) view.findViewById(R.id.wv_city);
		wvYear.setViewAdapter(new ArrayWheelAdapter<String>(context, yearList));

		wvYear.addChangingListener(new myWheelChangeListener());
		wvMonth.addChangingListener(new myWheelChangeListener());
		wvYear.setVisibleItems(8);
		wvMonth.setVisibleItems(8);
		mPopupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		// 设置SelectPicPopupWindow弹出窗体可点击
		mPopupWindow.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		mPopupWindow.setAnimationStyle(R.style.AnimBottom);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				LayoutParams params = ((Activity) context).getWindow()
						.getAttributes();
				params.alpha = 1.0F;
				((Activity) context).getWindow().setAttributes(params);
			}
		});

		view.findViewById(R.id.tv_ok).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mLisentner.selectOk(Integer.parseInt(selectYear),
						Integer.parseInt(selectMonth));
				mPopupWindow.dismiss();
			}
		});
		view.findViewById(R.id.tv_cancel).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						mPopupWindow.dismiss();
					}
				});
		updateCitys();

	}

	public void showChooseWindow() {
		if (mPopupWindow != null) {
				mPopupWindow.showAtLocation(mParent, Gravity.BOTTOM, 0, 0);
				LayoutParams params = ((Activity) context).getWindow()
						.getAttributes();
				params.alpha = 0.6F;
				((Activity) context).getWindow().setAttributes(params);
		}
	}

	public Boolean isShowing() {
		return mPopupWindow.isShowing();
	}

	public void dismissWindow() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
	}

	private void setDate() {
		Time time1 = new Time("GMT+8");
		time1.setToNow();
		yearList = new String[time1.year - 2013];
		for (int i = 2014; i < time1.year + 1; i++) {
			yearList[i - 2014] = i + "";
			String[] monthList = new String[12];
			for (int j = 1; j < 13; j++) {
				monthList[j - 1] = j + "";
			}

			times.put(i + "", monthList);
		}

	}

	class myWheelChangeListener implements OnWheelChangedListener {

		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			if (wheel == wvYear) {
				updateCitys();
			}
			if (wheel == wvMonth) {
				selectMonth = times.get(selectYear)[newValue];
			}
		}

	}

	private void updateCitys() {
		int yearCurrent = wvYear.getCurrentItem();
		selectYear = yearList[yearCurrent];
		String[] months = times.get(selectYear);
		wvMonth.setViewAdapter(new ArrayWheelAdapter<String>(context, months));
		wvMonth.setCurrentItem(0);
		selectMonth = months[0];
	}

	public interface OnSelectOKLisentner {
		void selectOk(int years, int months);
	}
}
