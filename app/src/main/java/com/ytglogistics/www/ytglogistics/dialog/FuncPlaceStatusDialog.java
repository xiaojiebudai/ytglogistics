package com.ytglogistics.www.ytglogistics.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ytglogistics.www.ytglogistics.MyApplication;
import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.api.Api;
import com.ytglogistics.www.ytglogistics.been.Place;
import com.ytglogistics.www.ytglogistics.utils.Consts;
import com.ytglogistics.www.ytglogistics.utils.ParamsUtils;
import com.ytglogistics.www.ytglogistics.utils.WWToast;
import com.ytglogistics.www.ytglogistics.xutils.WWXCallBack;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/22.
 */

public class FuncPlaceStatusDialog extends Dialog {
    @BindView(R.id.tv_bowei)
    TextView tvBowei;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_chepai)
    TextView tvChepai;
    @BindView(R.id.tv_kongxiang)
    TextView tvKongxiang;
    @BindView(R.id.tv_zuoyezhong)
    TextView tvZuoyezhong;
    @BindView(R.id.tv_tongzhilichang)
    TextView tvTongzhilichang;
    @BindView(R.id.tv_tingyong)
    TextView tvTingyong;
    @BindView(R.id.tv_back)
    TextView tvBack;
    private Place place;

    public FuncPlaceStatusDialog(Context context,Place place) {
        super(context, R.style.DialogStyle);
       this.place = place;
        setContentView(R.layout.dialog_funcplacestatus);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8);
        getWindow().setAttributes(attributes);
        getChePia();
        tvBowei.setText(place.PlaceId);
        tvStatus.setText(place.StatusText);
    }

    private void getChePia() {
        RequestParams params = ParamsUtils.getSessionParams(Api.PlaceLastInfo());
        x.http().get(params, new WWXCallBack("PlaceLastInfo") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                tvChepai.setText(data.getString("Data"));
            }
            @Override
            public void onAfterFinished() {

            }
        });
    }

    @OnClick({R.id.tv_kongxiang, R.id.tv_zuoyezhong, R.id.tv_tongzhilichang, R.id.tv_tingyong, R.id.tv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_kongxiang:
                doOper("0");
                break;
            case R.id.tv_zuoyezhong:
                doOper("1");
                break;
            case R.id.tv_tongzhilichang:
                doOper("3");
                break;
            case R.id.tv_tingyong:
                doOper("2");
                break;
            case R.id.tv_back:
                dismiss();
                break;
        }
    }

    private void doOper(String s) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Consts.KEY_SESSIONID, MyApplication
                .getInstance().getSessionId());
        jsonObject.put("placeId",place.PlaceId);
        jsonObject.put("status",s);
        x.http().post(ParamsUtils.getPostJsonParams(jsonObject, Api.PlaceOper()), new WWXCallBack("PlaceOper") {

            @Override
            public void onAfterSuccessOk(JSONObject data) {
                WWToast.showShort("操作成功");
                dismiss();
            }
            @Override
            public void onAfterFinished() {

            }
        });
    }
}
