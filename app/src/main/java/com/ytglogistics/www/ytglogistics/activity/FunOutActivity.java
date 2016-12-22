package com.ytglogistics.www.ytglogistics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.adapter.FunInAdapter;
import com.ytglogistics.www.ytglogistics.api.Api;
import com.ytglogistics.www.ytglogistics.been.Car;
import com.ytglogistics.www.ytglogistics.utils.Consts;
import com.ytglogistics.www.ytglogistics.utils.ParamsUtils;
import com.ytglogistics.www.ytglogistics.xutils.WWXCallBack;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/22.
 */

public class FunOutActivity extends FatherActivity {

    @BindView(R.id.et_no)
    EditText etNo;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.lv_data)
    ListView lvData;
    @BindView(R.id.tv_0)
    TextView tv0;
    @BindView(R.id.tv_1)
    TextView tv1;
    @BindView(R.id.tv_2)
    TextView tv2;
    private FunInAdapter funInAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.act_funoutlist;
    }

    @Override
    protected void initValues() {
        initDefautHead("出仓修改", true);
        initTextHeadRigth(R.string.fresh, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListData();
            }
        });
    }

    @Override
    protected void initView() {
        funInAdapter = new FunInAdapter(this, FunInAdapter.FUNIN);
        lvData.setAdapter(funInAdapter);
        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FunOutActivity.this, FunDetailActivity.class);
                intent.putExtra(Consts.KEY_MODULE,FunDetailActivity.FUNOUT);
                intent.putExtra(Consts.KEY_DATA, JSONObject.toJSONString(funInAdapter.getData().get(position)));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void doOperate() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        getListData();
    }

    private void getListData() {
        String carno = etNo.getText().toString().trim();
        RequestParams params = ParamsUtils.getSessionParams(Api.GetOutQueue());
        params.addBodyParameter("status", 4 + "");
        params.addBodyParameter("clp", carno);
        x.http().get(params, new WWXCallBack("GetOutQueue") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                JSONArray jsonArray = data.getJSONArray("Data");
                List<Car> list = JSON.parseArray(
                        jsonArray.toJSONString(), Car.class);
                funInAdapter.setData(list);
                tv0.setText(data.getString("InCount"));
                tv1.setText(data.getString("FpCount"));
                tv2.setText(data.getString("WcCount"));
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

    @OnClick(R.id.tv_search)
    public void onClick() {
        getListData();
    }
}
