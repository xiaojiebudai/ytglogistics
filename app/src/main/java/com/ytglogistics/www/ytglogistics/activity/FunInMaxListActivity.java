package com.ytglogistics.www.ytglogistics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.adapter.FunInAdapter;
import com.ytglogistics.www.ytglogistics.adapter.FunInMaxListAdapter;
import com.ytglogistics.www.ytglogistics.api.Api;
import com.ytglogistics.www.ytglogistics.been.AppInMax;
import com.ytglogistics.www.ytglogistics.been.AppInResult;
import com.ytglogistics.www.ytglogistics.utils.Consts;
import com.ytglogistics.www.ytglogistics.utils.ParamsUtils;
import com.ytglogistics.www.ytglogistics.utils.WWToast;
import com.ytglogistics.www.ytglogistics.xutils.WWXCallBack;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/12/9.
 */

public class FunInMaxListActivity extends FatherActivity {
    @BindView(R.id.lv_data)
    ListView lvData;
    private FunInMaxListAdapter adapter;
    private AppInResult result;
    @Override
    protected int getLayoutId() {
        return R.layout.act_funinmaxlist;
    }

    @Override
    protected void initValues() {
        initDefautHead("入仓修改",true);
        result=JSON.parseObject(getIntent().getStringExtra(Consts.KEY_DATA),AppInResult.class);
    }

    @Override
    protected void initView() {
        adapter=new FunInMaxListAdapter(this);
        lvData.setAdapter(adapter);
        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(FunInMaxListActivity.this,FunInMaxDetailActivity.class);
                intent.putExtra(Consts.KEY_DATA,JSON.toJSONString(adapter.getData().get(position)));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void doOperate() {
        getInMaxData();
    }

    private void getInMaxData() {
        RequestParams params = ParamsUtils.getSessionParams(Api.GetAppInMx());
        params.addBodyParameter("serial", result.Serial);
        params.addBodyParameter("queueNo",result.QueueNo);
        x.http().get(params, new WWXCallBack("GetAppInMx") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                //获取打印数据打印
                JSONArray jsonArray = data.getJSONArray("Data");
                ArrayList<AppInMax> list = (ArrayList<AppInMax>) JSON.parseArray(
                        jsonArray.toJSONString(), AppInMax.class);
                adapter.setData(list);
            }
            @Override
            public void onAfterFinished() {

            }
        });

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
