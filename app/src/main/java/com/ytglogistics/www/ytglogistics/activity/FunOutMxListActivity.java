package com.ytglogistics.www.ytglogistics.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.github.library.listener.OnRecyclerItemClickListener;
import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.api.Api;
import com.ytglogistics.www.ytglogistics.been.AppInMax;
import com.ytglogistics.www.ytglogistics.been.AppInResult;
import com.ytglogistics.www.ytglogistics.been.DataCbm;
import com.ytglogistics.www.ytglogistics.utils.Consts;
import com.ytglogistics.www.ytglogistics.utils.ParamsUtils;
import com.ytglogistics.www.ytglogistics.xutils.WWXCallBack;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/22.
 */

public class FunOutMxListActivity extends FatherActivity {
    @BindView(R.id.lv_data)
    RecyclerView lvData;
    @BindView(R.id.tv_commit)
    TextView tvCommit;
    @BindView(R.id.tv_shenhe)
    TextView tvShenhe;
    private AppInResult result;
    private ArrayList<AppInMax> appInMaxes;
    private BaseRecyclerAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.act_funoutmaxlist;
    }

    @Override
    protected void initValues() {
        initDefautHead("出仓修改", true);
        result = JSON.parseObject(getIntent().getStringExtra(Consts.KEY_DATA), AppInResult.class);

    }

    @Override
    protected void initView() {
        mAdapter = new BaseRecyclerAdapter<AppInMax>(this, appInMaxes, R.layout.list_funoutmax_item) {
            @Override
            protected void convert(BaseViewHolder helper, AppInMax item) {
                helper.setText(R.id.tv_xuhao, item.OutItem + "");
                helper.setText(R.id.tv_so, item.So);
                helper.setText(R.id.tv_po, item.Po);
                helper.setText(R.id.tv_skn, item.Skn);
                helper.setText(R.id.tv_state, item.QtyStatus);
            }
        };
        mAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AppInMax item = (AppInMax) mAdapter.getItem(position);

            }
        });
        mAdapter.setSelectedColor(R.color.text_selected_white_gray);
        lvData.setHasFixedSize(true);
        lvData.setLayoutManager(new LinearLayoutManager(this));
        lvData.setItemAnimator(new DefaultItemAnimator());
        lvData.setAdapter(mAdapter);
    }

    @Override
    protected void doOperate() {
        getOutMaxData();
    }

    private void getOutMaxData() {
        showWaitDialog();
        RequestParams params = ParamsUtils.getSessionParams(Api.GetAppOutMx());
        params.addBodyParameter("serial", result.Serial);
        params.addBodyParameter("clp", result.Clp);
        x.http().get(params, new WWXCallBack("GetAppOutMx") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                JSONArray jsonArray = data.getJSONArray("Data");
                appInMaxes = (ArrayList<AppInMax>) JSON.parseArray(
                        jsonArray.toJSONString(), AppInMax.class);
                mAdapter.setData(appInMaxes);
            }

            @Override
            public void onAfterFinished() {
                dismissWaitDialog();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_commit, R.id.tv_shenhe})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_commit:
                break;
            case R.id.tv_shenhe:
                break;
        }
    }
}
